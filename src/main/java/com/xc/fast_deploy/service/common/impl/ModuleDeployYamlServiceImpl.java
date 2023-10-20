package com.xc.fast_deploy.service.common.impl;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.gson.Gson;
import com.xc.fast_deploy.dao.master_dao.ModuleDeployMapper;
import com.xc.fast_deploy.dao.master_dao.ModuleDeployYamlMapper;
import com.xc.fast_deploy.dao.master_dao.ModuleEnvMapper;
import com.xc.fast_deploy.dao.master_dao.ModuleManageMapper;
import com.xc.fast_deploy.dto.MyPageInfo;
import com.xc.fast_deploy.dto.ResponseDTO;
import com.xc.fast_deploy.dto.module.ModuleDeployDTO;
import com.xc.fast_deploy.dto.module.ModuleDeployYamlDTO;
import com.xc.fast_deploy.dto.module.ModuleManageDTO;
import com.xc.fast_deploy.model.master_model.ModuleDeploy;
import com.xc.fast_deploy.model.master_model.ModuleDeployYaml;
import com.xc.fast_deploy.model.master_model.ModuleEnv;
import com.xc.fast_deploy.model.master_model.example.ModuleDeployExample;
import com.xc.fast_deploy.model.master_model.example.ModuleDeployYamlExample;
import com.xc.fast_deploy.myException.FileStoreException;
import com.xc.fast_deploy.myException.TransYaml2K8sVoException;
import com.xc.fast_deploy.myenum.YamlFileTypeEnum;
import com.xc.fast_deploy.myenum.k8sEnum.K8sApiversionTypeEnum;
import com.xc.fast_deploy.myenum.k8sEnum.K8sKindTypeEnum;
import com.xc.fast_deploy.quartz_job.job.MyYamlCompareThread;
import com.xc.fast_deploy.rediscache.JedisManage;
import com.xc.fast_deploy.service.common.ModuleDeployService;
import com.xc.fast_deploy.service.common.ModuleDeployYamlService;
import com.xc.fast_deploy.service.common.ModuleJobService;
import com.xc.fast_deploy.utils.DateUtils;
import com.xc.fast_deploy.utils.FoldUtils;
import com.xc.fast_deploy.utils.constant.K8sNameSpace;
import com.xc.fast_deploy.utils.constant.RedisKeyPrefix;
import com.xc.fast_deploy.utils.k8s.K8sUtils;
import com.xc.fast_deploy.vo.K8sYamlVo;
import com.xc.fast_deploy.vo.k8s_vo.K8sResourceVo;
import com.xc.fast_deploy.vo.module_vo.DeployModuleVo;
import com.xc.fast_deploy.vo.module_vo.ModuleDeployVo;
import com.xc.fast_deploy.vo.module_vo.ModuleEnvCenterManageVo;
import com.xc.fast_deploy.vo.module_vo.param.ModuleDeployYamlSelectParamVo;
import com.xc.fast_deploy.websocketConfig.myThread.ShutDownThread;
import io.kubernetes.client.openapi.models.ExtensionsV1beta1Deployment;
import io.kubernetes.client.openapi.models.V1Deployment;
import io.kubernetes.client.openapi.models.V1LabelSelector;
import io.kubernetes.client.openapi.models.V1Service;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.yaml.snakeyaml.reader.ReaderException;
import org.yaml.snakeyaml.scanner.ScannerException;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static com.xc.fast_deploy.utils.FoldUtils.SEP;

@Slf4j
@Service
public class ModuleDeployYamlServiceImpl extends BaseServiceImpl<ModuleDeployYaml, Integer> implements ModuleDeployYamlService {
  
  @Autowired
  private ModuleDeployYamlMapper deployYamlMapper;
  @Autowired
  private ModuleJobService jobService;
  @Autowired
  private ModuleDeployMapper deployMapper;
  @Autowired
  private ModuleManageMapper manageMapper;
  @Autowired
  private ModuleEnvMapper envMapper;
  @Autowired
  private ModuleDeployService deployService;
  @Resource
  private JedisManage jedisManage;
  
  @Value("${file.storge.path.prefix}")
  private String storgePrefix;
  @Value("${myself.pspass.prodId}")
  private String prodIds;
  
  @PostConstruct
  public void init() {
    super.init(deployYamlMapper);
  }
  
  /**
   * 新建一个上线的发布应用
   *
   * @param deployModuleVo
   * @param yamlFile
   * @param yamlPath
   * @return
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  public boolean createDeployModuleYaml(DeployModuleVo deployModuleVo, MultipartFile yamlFile,
                                        String yamlPath, MultipartFile svcYamlFile,
                                        String svcYamlPath) {
    YamlFileTypeEnum yamlFileTypeEnum = YamlFileTypeEnum.getTypeByCode(deployModuleVo.getYamlFileType());
    YamlFileTypeEnum svcYamlTypeEnum = YamlFileTypeEnum.getTypeByCode(deployModuleVo.getSvcYamlFileType());
    //这一步确认该模块在该环境中是否有创建好job内容
    ModuleManageDTO moduleManageDTO =
        jobService.selectJobModuleByModuleAndEnvId(deployModuleVo.getEnvId(),
            deployModuleVo.getModuleId());
    
    // 需要确定好的是 在一个环境中一个模块只能建立一次发布(这样方便我们找到对应关系去做发布)
    ModuleDeployExample deployExample = new ModuleDeployExample();
    ModuleDeployExample.Criteria criteria = deployExample.createCriteria();
    criteria.andModuleIdEqualTo(deployModuleVo.getModuleId()).
        andEnvIdEqualTo(deployModuleVo.getEnvId()).andIsDeleteEqualTo(0);
    boolean isNeedValidateName = true;
    
    if (yamlFileTypeEnum != null && moduleManageDTO != null
        && deployMapper.countByExample(deployExample) <= 0) {
      
      //特别注意 如果该模块已经配置过了yaml即验证该模块id是否已经在
      // module_deploy这张表里找到记录的则在这里暂时先不要过这个部分的验证
      ModuleDeployExample example = new ModuleDeployExample();
      example.createCriteria().andModuleIdEqualTo(deployModuleVo.getModuleId());
      List<ModuleDeploy> moduleDeploys = deployMapper.selectByExample(example);
      if (moduleDeploys.size() > 0) {
        isNeedValidateName = false;
      }
      
      ModuleDeploy moduleDeploy = new ModuleDeploy();
      moduleDeploy.setEnvId(deployModuleVo.getEnvId());
      moduleDeploy.setModuleId(deployModuleVo.getModuleId());
      moduleDeploy.setCreateTime(new Date());
      moduleDeploy.setUpdateTime(new Date());
      deployMapper.insertSelective(moduleDeploy);
      ModuleDeployYaml moduleDeployYaml = new ModuleDeployYaml();
      moduleDeployYaml.setDeployId(moduleDeploy.getId());
      moduleDeployYaml.setCreateTime(new Date());
      moduleDeployYaml.setUpdateTime(new Date());
      moduleDeployYaml.setIsOnlineYaml(1);
      if (moduleDeploy.getId() != null) {
        ModuleManageDTO manageDTO = manageMapper.selectInfoById(deployModuleVo.getModuleId());
        String regionName = manageDTO.getRegionName();
        String centerCode = manageDTO.getCenterCode();
        
        switch (yamlFileTypeEnum) {
          //针对yaml文件上传的文件格式验证
          case YAML_FILE_UPLOAD:
            //首先把文件存储下来  将文件存储的模块一级目录下
            StringBuilder filePrefix = new StringBuilder();
            filePrefix.append(storgePrefix)
                .append(moduleManageDTO.getCenterPath())
                .append(SEP).append(moduleManageDTO.getModuleContentName())
                .append(SEP);
            StringBuilder fileName = new StringBuilder();
            fileName.append(filePrefix).append(yamlFile.getOriginalFilename())
                .append(DateUtils.generateDateString());
            File file;
            K8sYamlVo k8sYamlVo = null;
            try {
              file = new File(fileName.toString());
              yamlFile.transferTo(file);
              k8sYamlVo = K8sUtils.transYaml2Vo(file);
            } catch (IOException e) {
              log.error("deploy文件存储出现错误: {}", yamlFile.getOriginalFilename());
              throw new FileStoreException("deploy文件存储出现错误");
            } catch (ReaderException | ScannerException e) {
              throw new TransYaml2K8sVoException("yaml文件转换为k8svo 出现错误: " + e.getMessage());
            }
            
            if (k8sYamlVo == null) {
//                            FoldUtils.deleteFolders(fileName.toString());
              throw new TransYaml2K8sVoException("yaml文件未能解析到正确数据");
            }
            if (isNeedValidateName) {
              validateDeployMetaName(k8sYamlVo, regionName, centerCode,
                  false, null);
            }
            //存储svc的相关信息
            addSvcInfo(moduleManageDTO, svcYamlTypeEnum, svcYamlFile, svcYamlPath,
                moduleDeploy.getId(), k8sYamlVo.getMetadataName(), isNeedValidateName);
            
            moduleDeployYaml.setYamlPath(fileName.toString().replace(storgePrefix, ""));
            moduleDeployYaml.setYamlType(k8sYamlVo.getKind());
            moduleDeployYaml.setYamlName(k8sYamlVo.getMetadataName());
            moduleDeployYaml.setYamlNamespace(k8sYamlVo.getNamespace());
            Map<String, String> labelMap = k8sYamlVo.getLabelMap();
            if (labelMap != null && labelMap.size() > 0) {
              moduleDeployYaml.setMirrorName(JSONObject.toJSONString(labelMap));
            }
            deployYamlMapper.insertSelective(moduleDeployYaml);
            return true;
          case YAML_FILE_APPOINT:
            File yamlPathFile = new File(storgePrefix + yamlPath);
            K8sYamlVo k8sYamlVoPath = null;
            try {
              k8sYamlVoPath = K8sUtils.transYaml2Vo(yamlPathFile);
            } catch (IOException | ReaderException | ScannerException e) {
              throw new TransYaml2K8sVoException("yaml文件转换为k8svo 出现错误: " + e.getMessage());
            }
            if (k8sYamlVoPath == null) {
              throw new TransYaml2K8sVoException("yaml文件未能解析到正确数据");
            }
            
            if (isNeedValidateName) {
              
              validateDeployMetaName(k8sYamlVoPath, regionName, centerCode,
                  false, null);
              //存储svc的相关信息
              
            }
            addSvcInfo(moduleManageDTO, svcYamlTypeEnum, svcYamlFile,
                svcYamlPath, moduleDeploy.getId(), k8sYamlVoPath.getMetadataName(), isNeedValidateName);
            moduleDeployYaml.setYamlName(k8sYamlVoPath.getMetadataName());
            moduleDeployYaml.setYamlPath(yamlPath);
            moduleDeployYaml.setYamlType(k8sYamlVoPath.getKind());
            moduleDeployYaml.setYamlNamespace(k8sYamlVoPath.getNamespace());
            Map<String, String> labelMapPath = k8sYamlVoPath.getLabelMap();
            if (labelMapPath != null && labelMapPath.size() > 0) {
              moduleDeployYaml.setMirrorName(JSONObject.toJSONString(labelMapPath));
              Set<String> stringSet = labelMapPath.keySet();
              for (String key : stringSet) {
                String vale = labelMapPath.get(key);
                if (StringUtils.isNotBlank(vale)
                    && (vale.equals(moduleDeployYaml.getYamlName())
                    || vale.contains(moduleDeployYaml.getYamlName())
                    || moduleDeployYaml.getYamlName().contains(vale))) {
                  
                  moduleDeployYaml.setContainerName(key + "=" + vale);
                }
              }
            }
            deployYamlMapper.insertSelective(moduleDeployYaml);
            return true;
          default:
            break;
        }
        
      }
    }
    return false;
  }
  
  /**
   * 主要是为了校验metaDataName是否符合规定的格式
   *
   * @param k8sYamlVo
   * @param regionName
   * @param centerCode
   */
  private void validateDeployMetaName(K8sYamlVo k8sYamlVo, String regionName,
                                      String centerCode, boolean isService, String deployMetadataNameValue) {
    //1\验证metadataName是否符合要求  域-中心-自定义..-服务
    //2\验证label是否格式也有name: metadataName的一组label
    //3\如果有service 验证service里selector是否也有一组这样的格式name: metadataName
    //4\是否需要验证deployment的相关属性设置.... todo 暂不验证
//        Map<String, String> labelMap = k8sYamlVo.getLabelMap();
//        String nameValue = labelMap.get("name");
    //    String metadataName = k8sYamlVo.getMetadataName();
//
//        if (!metadataName.equals(nameValue)) {
//            throw new ValidateExcetion("metadataName: " +
//                    metadataName + " 不匹配 labelName: " + nameValue);
//        }
//
//        if (!isService) {
//            String prefixName = regionName + "-" + centerCode + "-";
//            if (!metadataName.startsWith(prefixName)) {
//                throw new ValidateExcetion("yaml metadataName 需要以 " + prefixName + " 开始");
//            }
//        } else {
//            if (!deployMetadataNameValue.equals(k8sYamlVo.getMetadataName())) {
//                throw new ValidateExcetion("deploymentName: " + deployMetadataNameValue
//                        + " 不匹配service的metadataName: " + k8sYamlVo.getMetadataName());
//            }
//            V1Service v1Service = K8sUtils.getObject(k8sYamlVo.getO(), V1Service.class);
//            Map<String, String> selectorMap = v1Service.getSpec().getSelector();
//            String svcSelectNameValue = selectorMap.get("name");
//
//            if (!deployMetadataNameValue.equals(svcSelectNameValue)) {
//                throw new ValidateExcetion("deployment metadataName: " +
//                        deployMetadataNameValue + " 不匹配 labelName: " + nameValue);
//            }
//        }
  }
  
  /**
   * 添加svc信息
   *
   * @param moduleManageDTO
   * @param svcYamlTypeEnum
   * @param svcYamlFile
   * @param svcYamlPath
   * @param deployId
   */
  private void addSvcInfo(ModuleManageDTO moduleManageDTO,
                          YamlFileTypeEnum svcYamlTypeEnum, MultipartFile svcYamlFile,
                          String svcYamlPath, Integer deployId, String metaDataName, boolean isNeedValidateName) {
    if (svcYamlTypeEnum != null && deployId != null && moduleManageDTO != null) {
      ModuleDeployYaml moduleDeployYaml = new ModuleDeployYaml();
      moduleDeployYaml.setDeployId(deployId);
      moduleDeployYaml.setUpdateTime(new Date());
      moduleDeployYaml.setCreateTime(new Date());
      moduleDeployYaml.setIsOnlineYaml(0);
      
      switch (svcYamlTypeEnum) {
        case YAML_FILE_UPLOAD:
          if (svcYamlFile != null && !svcYamlFile.isEmpty()) {
            StringBuilder filePrefix = new StringBuilder();
            filePrefix.append(storgePrefix)
                .append(moduleManageDTO.getCenterPath())
                .append(SEP)
                .append(moduleManageDTO.getModuleContentName())
                .append(SEP);
            StringBuilder fileName = new StringBuilder();
            fileName.append(filePrefix)
                .append(svcYamlFile.getOriginalFilename())
                .append(DateUtils.generateDateString());
            File file;
            K8sYamlVo k8sYamlVo = null;
            try {
              file = new File(fileName.toString());
              svcYamlFile.transferTo(file);
              k8sYamlVo = K8sUtils.transYaml2Vo(file);
              
            } catch (IOException e) {
              log.error("svc文件存储出现错误: {}", svcYamlFile.getOriginalFilename());
              throw new FileStoreException("svc文件存储出现错误");
            } catch (ReaderException | ScannerException e) {
              throw new TransYaml2K8sVoException("svc_yaml文件转换为k8svo 出现错误");
            }
            if (k8sYamlVo == null) {
              FoldUtils.deleteFolders(fileName.toString());
              throw new TransYaml2K8sVoException("svc的yaml文件转换为k8svo 出现错误");
            }
            //校验svc的文件和deployment文件的相关name是否匹配
            if (isNeedValidateName) {
              validateDeployMetaName(k8sYamlVo, null, null,
                  true, metaDataName);
            }
            
            moduleDeployYaml.setYamlPath(fileName.toString().replace(storgePrefix, ""));
            moduleDeployYaml.setYamlType(k8sYamlVo.getKind());
            moduleDeployYaml.setYamlName(k8sYamlVo.getMetadataName());
            moduleDeployYaml.setYamlNamespace(k8sYamlVo.getNamespace());
            deployYamlMapper.insertSelective(moduleDeployYaml);
          }
          break;
        case YAML_FILE_APPOINT:
          if (StringUtils.isNotBlank(svcYamlPath)) {
            File yamlPathFile = new File(storgePrefix + svcYamlPath);
            K8sYamlVo k8sYamlVoPath = null;
            try {
              k8sYamlVoPath = K8sUtils.transYaml2Vo(yamlPathFile);
            } catch (IOException | ReaderException | ScannerException e) {
              throw new TransYaml2K8sVoException("yaml文件转换为k8svo 出现错误: " + e.getMessage());
            }
            if (k8sYamlVoPath == null) {
              throw new TransYaml2K8sVoException("yaml文件未能解析到正确数据");
            }
            if (isNeedValidateName) {
              validateDeployMetaName(k8sYamlVoPath, null, null,
                  true, metaDataName);
            }
            moduleDeployYaml.setYamlName(k8sYamlVoPath.getMetadataName());
            moduleDeployYaml.setYamlPath(svcYamlPath);
            moduleDeployYaml.setYamlType(k8sYamlVoPath.getKind());
            moduleDeployYaml.setYamlNamespace(k8sYamlVoPath.getNamespace());
            deployYamlMapper.insertSelective(moduleDeployYaml);
          }
          break;
      }
    }
  }
  
  private ResponseDTO updateYamlInfo(YamlFileTypeEnum yamlFileTypeEnum, MultipartFile yamlFile, ModuleDeployYaml deployYaml, String yamlPath) {
    ResponseDTO responseDTO = new ResponseDTO();
    responseDTO.fail("更新yaml文件失败");
    switch (yamlFileTypeEnum) {
      case YAML_FILE_UPLOAD:
        if (yamlFile != null && !yamlFile.isEmpty()) {
          StringBuilder originFileName = new StringBuilder();
          originFileName.append(yamlFile.getOriginalFilename()).append(DateUtils.generateDateString());
          String yamlPath1 = storgePrefix + deployYaml.getYamlPath();
          if (StringUtils.isNotBlank(yamlPath1)) {
            String[] split = yamlPath1.split("/");
            String fileName = yamlPath1.replace(split[split.length - 1], originFileName.toString());
            File file;
            try {
              file = new File(fileName);
              yamlFile.transferTo(file);
            } catch (IOException e) {
              throw new FileStoreException("文件存储出现错误");
            }
            K8sYamlVo k8sYamlVoPath = null;
            try {
              k8sYamlVoPath = K8sUtils.transYaml2Vo(file);
            } catch (IOException | ReaderException | ScannerException e) {
              throw new TransYaml2K8sVoException("yaml文件转换为k8svo 出现错误: " + e.getMessage());
            }
            if (k8sYamlVoPath == null) {
              FoldUtils.deleteFolders(fileName);
              throw new TransYaml2K8sVoException("yaml文件格式转换出现错误");
            } else {
              deployYaml.setYamlPath(fileName.replace(storgePrefix, ""));
              deployYaml.setYamlType(k8sYamlVoPath.getKind());
              deployYaml.setYamlName(k8sYamlVoPath.getMetadataName());
              if (K8sKindTypeEnum.DEPLOYMENT.getKindType().equals(k8sYamlVoPath.getKind())) {
                V1Deployment deployment;
                if (K8sApiversionTypeEnum.EXTENSIONAPI.getApiVersionType().equals(k8sYamlVoPath.getApiVersion())) {
                  ExtensionsV1beta1Deployment beta1Deployment =
                      K8sUtils.getObject(k8sYamlVoPath.getO(), ExtensionsV1beta1Deployment.class);
                  deployment = K8sUtils.toV1Deploy(beta1Deployment);
                } else {
                  deployment = K8sUtils.getObject(k8sYamlVoPath.getO(), V1Deployment.class);
                }
                deployYaml.setYamlJson(new Gson().toJson(deployment));
              } else {
                V1Service v1Service = K8sUtils.getObject(k8sYamlVoPath.getO(), V1Service.class);
                deployYaml.setYamlJson(new Gson().toJson(v1Service));
              }
              if (deployYamlMapper.updateByPrimaryKeySelective(deployYaml) > 0) {
                responseDTO.success("更新yaml文件成功");
              }
            }
          }
        }
        break;
      case YAML_FILE_APPOINT:
        if (StringUtils.isNotBlank(yamlPath)) {
          File yamlPathFile = new File(storgePrefix + yamlPath);
          if (yamlPathFile.exists()) {
            K8sYamlVo k8sYamlVoPath = null;
            try {
              k8sYamlVoPath = K8sUtils.transYaml2Vo(yamlPathFile);
            } catch (IOException | ReaderException | ScannerException e) {
              throw new TransYaml2K8sVoException("yaml文件转换为k8svo 出现错误: " + e.getMessage());
            }
//                        K8sYamlVo k8sYamlVo = K8sUtils.transYaml2Vo(yamlPathFile);
            if (k8sYamlVoPath == null) {
              throw new TransYaml2K8sVoException("yaml文件格式转换出现错误");
            } else {
              deployYaml.setYamlPath(yamlPath);
              deployYaml.setYamlType(k8sYamlVoPath.getKind());
              deployYaml.setYamlName(k8sYamlVoPath.getMetadataName());
              if (K8sKindTypeEnum.DEPLOYMENT.getKindType().equals(k8sYamlVoPath.getKind())) {
                V1Deployment deployment;
                if (K8sApiversionTypeEnum.EXTENSIONAPI.getApiVersionType().equals(k8sYamlVoPath.getApiVersion())) {
                  ExtensionsV1beta1Deployment beta1Deployment =
                      K8sUtils.getObject(k8sYamlVoPath.getO(), ExtensionsV1beta1Deployment.class);
                  deployment = K8sUtils.toV1Deploy(beta1Deployment);
                } else {
                  deployment = K8sUtils.getObject(k8sYamlVoPath.getO(), V1Deployment.class);
                }
                deployYaml.setYamlJson(new Gson().toJson(deployment));
              } else {
                V1Service v1Service = K8sUtils.getObject(k8sYamlVoPath.getO(), V1Service.class);
                deployYaml.setYamlJson(new Gson().toJson(v1Service));
              }
              if (deployYamlMapper.updateByPrimaryKeySelective(deployYaml) > 0) {
                responseDTO.success("更新yaml文件成功");
              }
            }
          }
        }
        break;
      default:
        break;
    }
    return responseDTO;
  }
  
  /**
   * 更新yaml文件对应的信息 即重新指定deploy对应的yaml文件信息
   * 暂时不做名字方面的校验 考虑到已经发布的模块需要重新指定yaml文件 暂时不做这方面的校验 2020-03-31
   *
   * @param deployModuleVo
   * @param yamlFile
   * @param yamlPath
   * @param svcYamlFile
   * @param svcYamlPath
   * @return
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  public ResponseDTO updateDeployModuleYaml(DeployModuleVo deployModuleVo,
                                            MultipartFile yamlFile, String yamlPath,
                                            MultipartFile svcYamlFile, String svcYamlPath) {
    ResponseDTO responseDTO = new ResponseDTO();
    responseDTO.success("更新yaml文件成功");
    if (deployModuleVo != null && deployModuleVo.getDeployYamlId() != null) {
      ModuleDeployYaml deployYaml = deployYamlMapper.selectByPrimaryKey(deployModuleVo.getDeployYamlId());
      ModuleDeploy moduleDeploy = deployMapper.selectByPrimaryKey(deployYaml.getDeployId());
//            if (StringUtils.isNotBlank(deployYaml.getYamlJson())) {
//                responseDTO.fail("已自定义yaml文件,修改yaml文件指定失败");
//                return responseDTO;
//            }
      YamlFileTypeEnum yamlFileTypeEnum = YamlFileTypeEnum.getTypeByCode(deployModuleVo.getYamlFileType());
      //判断是否更改发布yaml文件
      if (yamlFileTypeEnum != null) {
        //该yaml文件已经发布,需要先下线才能修改发布yaml文件

//                重新定义yaml文件的时候可以不需要下线 todo 2020 3-31 有待商榷
        if (deployYaml.getIsDeployed() == 1) {
          responseDTO.fail("yaml文件正在发布中,请先下线");
          return responseDTO;
        }
        //更新发布的yaml文件信息
        responseDTO = updateYamlInfo(yamlFileTypeEnum, yamlFile, deployYaml, yamlPath);
        if (responseDTO.getCode() != 200) {
          return responseDTO;
        }
      }
      
      //然后判断svc的yaml文件是否上传
      YamlFileTypeEnum svcYamlFileTypeEnum = YamlFileTypeEnum.getTypeByCode(deployModuleVo.getSvcYamlFileType());
      //处理svc的信息
      if (svcYamlFileTypeEnum != null) {
        //表示重新上传的svc的yaml文件
        //首先验证原有的yaml文件是否包含svc信息
        Integer deployId = deployYaml.getDeployId();
        ModuleDeployYamlExample yamlExample = new ModuleDeployYamlExample();
        ModuleDeployYamlExample.Criteria criteria = yamlExample.createCriteria();
        criteria.andIsOnlineYamlEqualTo(0).andDeployIdEqualTo(deployId)
            .andYamlTypeEqualTo(K8sKindTypeEnum.SERVICE.getKindType());
        List<ModuleDeployYaml> yamlList = deployYamlMapper.selectByExample(yamlExample);
        if (yamlList == null || yamlList.size() == 0) {
          //没有svc yaml文件的信息 只需要做添加操作即可
          ModuleManageDTO moduleManageDTO =
              jobService.selectJobModuleByModuleAndEnvId(moduleDeploy.getEnvId(),
                  moduleDeploy.getModuleId());
          addSvcInfo(moduleManageDTO, svcYamlFileTypeEnum,
              svcYamlFile, svcYamlPath, deployId, "", false);
        } else {
          //已有svc信息
          ModuleDeployYaml moduleDeployYaml = yamlList.get(0);
          if (moduleDeployYaml.getIsDeployed() == 1) {
            responseDTO.fail("svc已经发布,请先下线");
            return responseDTO;
          }
          //否则的话做替换svc信息的操作
          responseDTO = updateYamlInfo(svcYamlFileTypeEnum, svcYamlFile,
              moduleDeployYaml, svcYamlPath);
        }
      }
    }
    return responseDTO;
  }
  
  /**
   * 查询所有的发布yaml文件信息 并分页展示
   *
   * @param pageNum
   * @param pageSize
   * @param yamlSelectParamVo
   * @return
   */
  @Override
  public MyPageInfo<ModuleDeployYamlDTO> selectDeployYamlAll(Integer pageNum, Integer pageSize, ModuleDeployYamlSelectParamVo yamlSelectParamVo) {
    MyPageInfo<ModuleDeployYamlDTO> myPageInfo = new MyPageInfo<>();
    if (pageNum != null && pageNum > 0 && pageSize != null && pageSize > 0) {
      PageHelper.startPage(pageNum, pageSize);
      List<ModuleDeployYamlDTO> list = deployYamlMapper.selectDeployYamlVoPageByVo(yamlSelectParamVo);
      PageInfo<ModuleDeployYamlDTO> pageInfo = new PageInfo<>(list);
      BeanUtils.copyProperties(pageInfo, myPageInfo);
    }
    return myPageInfo;
  }
  
  /**
   * 删除发布yaml文件信息
   * 判断 该yaml文件对应的信息是否是在已经发布在k8s中 todo 去掉这一步的判断可以自由删除
   *
   * @param deployYamlId
   * @return
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  public ResponseDTO deleteByYamlId(Integer deployYamlId) {
    ResponseDTO responseDTO = new ResponseDTO();
    responseDTO.fail("删除失败");
    ModuleDeployYaml moduleDeployYaml = deployYamlMapper.selectByPrimaryKey(deployYamlId);
    if (moduleDeployYaml != null) {
      Integer deployId = moduleDeployYaml.getDeployId();
      ModuleDeployYamlExample deployYamlExample = new ModuleDeployYamlExample();
      deployYamlExample.createCriteria().andDeployIdEqualTo(deployId);
      List<ModuleDeployYaml> moduleDeployYamls = deployYamlMapper.selectByExample(deployYamlExample);
      // 2020 3-30直接删除数据库配置好的yaml数据 并且将已发布的配置数据同时删除
      if (moduleDeployYamls != null && moduleDeployYamls.size() > 0) {
        for (ModuleDeployYaml deployYaml : moduleDeployYamls) {
          deployYamlMapper.deleteByPrimaryKey(deployYaml.getId());
        }
        ModuleDeploy moduleDeploy = new ModuleDeploy();
        moduleDeploy.setId(moduleDeployYaml.getDeployId());
        moduleDeploy.setIsDelete(1);
        deployMapper.updateByPrimaryKeySelective(moduleDeploy);
        responseDTO.success("删除成功");
      }
    }
    return responseDTO;
  }
  
  /**
   * 预览yaml文件信息
   *
   * @param deployYamlId
   * @return
   */
  @Override
  public Object getYamlInfoById(Integer deployYamlId) {
    if (deployYamlId != null) {
      ModuleDeployYamlExample yamlExample = new ModuleDeployYamlExample();
      ModuleDeployYamlExample.Criteria criteria = yamlExample.createCriteria();
      criteria.andIdEqualTo(deployYamlId);
      List<ModuleDeployYaml> deployYamlList = deployYamlMapper.selectByExampleWithBLOBs(yamlExample);
      if (deployYamlList != null && deployYamlList.size() > 0) {
        if (StringUtils.isNotBlank(deployYamlList.get(0).getYamlJson())) {
          return JSONObject.parse(deployYamlList.get(0).getYamlJson());
        } else {
          String yamlPath = storgePrefix + deployYamlList.get(0).getYamlPath();
          File file = new File(yamlPath);
          org.yaml.snakeyaml.Yaml yaml = new org.yaml.snakeyaml.Yaml();
          if (file.exists()) {
            try {
              return yaml.load(new FileInputStream(file));
            } catch (IOException e) {
              log.error("转yaml文件失败");
            }
          } else {
            log.info("yamlFile 文件不存在");
          }
        }
      }
    }
    return null;
  }
  
  /**
   * 查看某条发布的详情内容
   *
   * @param deployId
   */
  @Override
  public ModuleDeployDTO getOneYamlInfoById(Integer deployId) {
    ModuleDeployDTO deployDTO = new ModuleDeployDTO();
    if (deployId != null) {
      ModuleDeployVo deployVo = new ModuleDeployVo();
      deployVo.setId(deployId);
      List<ModuleEnvCenterManageVo> moduleDeployInfoAll = deployMapper.selectModuleEnvCenterAll(deployVo);
      if (moduleDeployInfoAll != null && moduleDeployInfoAll.size() > 0) {
        ModuleEnvCenterManageVo moduleEnvCenterManageVo = moduleDeployInfoAll.get(0);
        BeanUtils.copyProperties(moduleEnvCenterManageVo, deployDTO);
        ModuleDeployYamlExample deployYamlExample = new ModuleDeployYamlExample();
        ModuleDeployYamlExample.Criteria criteria = deployYamlExample.createCriteria();
        criteria.andDeployIdEqualTo(deployId);
        
        List<ModuleDeployYaml> moduleDeployYamls = deployYamlMapper.selectByExampleWithBLOBs(deployYamlExample);
        if (moduleDeployYamls != null && moduleDeployYamls.size() > 0) {
          List<ModuleDeployYamlDTO> yamlDTOList = new ArrayList<>();
          for (ModuleDeployYaml deployYaml : moduleDeployYamls) {
            
            ModuleDeployYamlDTO deployYamlDTO = new ModuleDeployYamlDTO();
            BeanUtils.copyProperties(deployYaml, deployYamlDTO);
            if (StringUtils.isNotBlank(deployYaml.getYamlJson())) {
              deployYamlDTO.setFileObject(JSONObject.parse(deployYaml.getYamlJson()));
            } else {
              String yamlPath = storgePrefix + deployYaml.getYamlPath();
              File file = new File(yamlPath);
              org.yaml.snakeyaml.Yaml yaml = new org.yaml.snakeyaml.Yaml();
              if (file.exists()) {
                try {
                  deployYamlDTO.setFileObject(yaml.load(new FileInputStream(file)));
                } catch (FileNotFoundException e) {
                  log.error("yaml文件找不到异常出现");
                  e.printStackTrace();
                }
              } else {
                log.info("yamlFile 文件不存在");
              }
            }
            yamlDTOList.add(deployYamlDTO);
          }
          moduleDeployYamls.clear();
          deployDTO.setModuleDeployYamls(yamlDTOList);
        }
      }
    }
    return deployDTO;
  }
  
  /**
   * 根据发布yamlid获取环境id
   *
   * @param deployYamlId
   * @return
   */
  @Override
  public Integer selectEnvIdByYamlId(Integer deployYamlId) {
    return deployYamlMapper.selectEnvIdByYamlId(deployYamlId);
  }
  
  /**
   * 获取模块配置的对应的json数据内容
   *
   * @param envId
   * @param moduleId
   * @param type
   */
  @Override
  public Object getYamlJsonInfo(Integer envId, Integer moduleId, Integer type) {
    //如果该模块已经生成相关的json数据直接返回对应json数据即可.
    K8sKindTypeEnum enumByCode = K8sKindTypeEnum.getEnumByCode(type);
    if (enumByCode != null) {
      List<ModuleDeployYaml> deployYamlList = deployYamlMapper.selectYamlJsonByModuleId(moduleId);
      if (deployYamlList != null && deployYamlList.size() > 0) {
        for (ModuleDeployYaml deployYaml : deployYamlList) {
          if (enumByCode.getKindType().equals(deployYaml.getYamlType()) &&
              StringUtils.isNotBlank(deployYaml.getYamlJson())) {
            return JSONObject.parse(deployYaml.getYamlJson());
          }
        }
      }
    }
    //首先确认该模块在该环境在是否存在 并查出模块相关的信息
    ModuleManageDTO moduleManageDTO = manageMapper.selectInfoById(moduleId);
    if (moduleManageDTO != null && envId.equals(moduleManageDTO.getEnvId()) && enumByCode != null) {
      //当前的模块如果也是在pst环境的话直接到下一步
      //校验pst环境的是否已有同名的模块已经配置好json数据的
//            if (!PSTConstants.CRM_PST_CODE.equals(moduleManageDTO.getEnvCode()) &&
//                    !PSTConstants.BILLINT_PST_CODE.equals(moduleManageDTO.getEnvCode())) {
//                //然后用模块相关信息查出模块名相同的数据信息
//                ModuleManageExample manageExample = new ModuleManageExample();
//                ModuleManageExample.Criteria criteria = manageExample.createCriteria();
//                List<String> envCodeList = new ArrayList<>();
//                envCodeList.add(PSTConstants.CRM_PST_CODE);
//                envCodeList.add(PSTConstants.BILLINT_PST_CODE);
//                criteria.andModuleNameEqualTo(moduleManageDTO.getModuleName())
//                        .andIsDeleteEqualTo(0).andEnvCodeIn(envCodeList);
//                List<ModuleManage> moduleManages = manageMapper.selectByExample(manageExample);
//                //如果查询到相关数据
//                if (moduleManages != null && moduleManages.size() > 0) {
//                    //同名的在pst环境中已经存在的模块数据
//                    Integer simModuleId = moduleManages.get(0).getId();
//                    ModuleDeployExample deployExample = new ModuleDeployExample();
//                    ModuleDeployExample.Criteria deployExampleCriteria = deployExample.createCriteria();
//                    deployExampleCriteria.andModuleIdEqualTo(simModuleId).andIsDeleteEqualTo(0);
//                    List<ModuleDeploy> moduleDeploys = deployMapper.selectByExample(deployExample);
//                    if (moduleDeploys != null && moduleDeploys.size() > 0) {
//                        //根据模块id查询是否已经配置过json相关数据,根据type判断获取数据
//                        Integer deployId = moduleDeploys.get(0).getId();
//                        ModuleDeployYamlExample deployYamlExample = new ModuleDeployYamlExample();
//                        ModuleDeployYamlExample.Criteria yamlExampleCriteria =
//                                deployYamlExample.createCriteria();
//                        yamlExampleCriteria.andDeployIdEqualTo(deployId)
//                                .andYamlTypeEqualTo(enumByCode.getKindType());
//                        List<ModuleDeployYaml> deployYamls =
//                                deployYamlMapper.selectByExampleWithBLOBs(deployYamlExample);
//                        if (deployYamls != null && deployYamls.size() > 0) {
//                            String yamlJson = deployYamls.get(0).getYamlJson();
//                            if (StringUtils.isNotBlank(yamlJson)) {
//                                return JSONObject.parse(yamlJson);
//                            }
//                        }
//                    }
//                }
//            }
      
      //校验是否已指定好yaml文件
      ModuleDeployExample deployExample = new ModuleDeployExample();
      ModuleDeployExample.Criteria deployExampleCriteria = deployExample.createCriteria();
      deployExampleCriteria.andModuleIdEqualTo(moduleId).andEnvIdEqualTo(envId).andIsDeleteEqualTo(0);
      List<ModuleDeploy> moduleDeploys = deployMapper.selectByExample(deployExample);
      if (moduleDeploys != null && moduleDeploys.size() > 0) {
        Integer deployId = moduleDeploys.get(0).getId();
        ModuleDeployYamlExample deployYamlExample = new ModuleDeployYamlExample();
        ModuleDeployYamlExample.Criteria yamlExampleCriteria = deployYamlExample.createCriteria();
        yamlExampleCriteria.andDeployIdEqualTo(deployId).andYamlTypeEqualTo(enumByCode.getKindType());
        List<ModuleDeployYaml> deployYamls = deployYamlMapper.selectByExample(deployYamlExample);
        if (deployYamls != null && deployYamls.size() > 0) {
          String yamlPath = storgePrefix + deployYamls.get(0).getYamlPath();
          File file = new File(yamlPath);
          try {
            return io.kubernetes.client.util.Yaml.load(file);
          } catch (IOException e) {
            throw new TransYaml2K8sVoException("yaml文件解析异常: " + e.getMessage());
          }
        }
      }
      //如果都没有匹配直接去获取默认数据匹配
      return getDefaultYaml(moduleManageDTO.getModuleContentName(), enumByCode);
    }
    return null;
  }
  
  /**
   * 添加发布文件形成json数据 存入新的数据库
   *
   * @param resourceVo
   * @return
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  public boolean saveYamlJson(K8sResourceVo resourceVo) {
    
    //首先确认模块在该环境中没有做过发布操作
    // 需要确定好的是 在一个环境中一个模块只能建立一次发布(这样方便我们找到对应关系去做发布)
    boolean flag = false;
    K8sKindTypeEnum typeEnum = K8sKindTypeEnum.getEnumByCode(resourceVo.getType());
    ModuleDeployExample deployExample = new ModuleDeployExample();
    ModuleDeployExample.Criteria criteria = deployExample.createCriteria();
    criteria.andModuleIdEqualTo(resourceVo.getModuleId()).
        andEnvIdEqualTo(resourceVo.getEnvId()).andIsDeleteEqualTo(0);
    //确认是否在job里面已经创建过的
    ModuleManageDTO moduleManageDTO = jobService.selectJobModuleByModuleAndEnvId(resourceVo.getEnvId(),
        resourceVo.getModuleId());
    
    if (typeEnum != null && moduleManageDTO != null && deployMapper.countByExample(deployExample) <= 0) {
      
      ModuleManageDTO manageDTO = manageMapper.selectInfoById(resourceVo.getModuleId());
      String regionName = manageDTO.getRegionName();
      String centerCode = manageDTO.getCenterCode();
      
      ModuleDeploy moduleDeploy = new ModuleDeploy();
      moduleDeploy.setEnvId(resourceVo.getEnvId());
      moduleDeploy.setModuleId(resourceVo.getModuleId());
      moduleDeploy.setCreateTime(new Date());
      moduleDeploy.setUpdateTime(new Date());
      deployMapper.insertSelective(moduleDeploy);
      ModuleDeployYaml moduleDeployYaml = new ModuleDeployYaml();
      moduleDeployYaml.setDeployId(moduleDeploy.getId());
      moduleDeployYaml.setCreateTime(new Date());
      moduleDeployYaml.setUpdateTime(new Date());
      moduleDeployYaml.setIsOnlineYaml(1);
      
      String s = new Gson().toJson(resourceVo.getDeployResource());
      
      try {
        Object load = io.kubernetes.client.util.Yaml.load(s);
        K8sYamlVo k8sYamlVo = K8sUtils.transObject2Vo(load);
        moduleDeployYaml.setYamlName(k8sYamlVo.getMetadataName());
        moduleDeployYaml.setYamlType(k8sYamlVo.getKind());
        moduleDeployYaml.setYamlNamespace(k8sYamlVo.getNamespace());
        if (!typeEnum.getKindType().equals(k8sYamlVo.getKind())) {
          throw new TransYaml2K8sVoException("yaml json 格式错误: " +
              k8sYamlVo.getKind() + "--" + typeEnum.getKindType());
        }
        //新增添加关于
        validateDeployMetaName(k8sYamlVo, regionName, centerCode, false, null);
      } catch (IOException e) {
        e.printStackTrace();
        throw new TransYaml2K8sVoException("yaml json 格式错误: " + e.getMessage());
      }
      moduleDeployYaml.setYamlJson(s);
      
      deployYamlMapper.insertSelective(moduleDeployYaml);
      //判断是否需要重新创建svc 0否 1是  然后存储svc文件
      if (resourceVo.getIsNeedSvc() == 1) {
        V1Service v1Service = K8sUtils.genV1Svc(moduleDeployYaml.getYamlName(),
            resourceVo.getNamespace(), resourceVo.getTargetPort(), resourceVo.getPort(),
            resourceVo.getNodePort());
        ModuleDeployYaml moduleSvcYaml = new ModuleDeployYaml();
        moduleSvcYaml.setDeployId(moduleDeploy.getId());
        moduleSvcYaml.setYamlName(moduleDeployYaml.getYamlName());
        moduleSvcYaml.setYamlType(K8sKindTypeEnum.SERVICE.getKindType());
        moduleSvcYaml.setYamlNamespace(moduleDeployYaml.getYamlNamespace());
        moduleSvcYaml.setYamlJson(new Gson().toJson(v1Service));
        moduleSvcYaml.setUpdateTime(new Date());
        moduleSvcYaml.setCreateTime(new Date());
        deployYamlMapper.insertSelective(moduleSvcYaml);
      }
      flag = true;
    }
    //保存新建的yamlJson信息
    return flag;
  }
  
  /**
   * 更新yaml文件配置的json信息
   *
   * @param resourceVo
   * @return
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  public boolean updateDeployModuleJSON(K8sResourceVo resourceVo) {
    List<ModuleDeployYaml> deployYamls =
        deployService.getModuleDeployByModuleAndEnvId(resourceVo.getModuleId(), resourceVo.getEnvId(), false);
    String namespace = StringUtils.isNotBlank(resourceVo.getNamespace()) ? resourceVo.getNamespace() : K8sNameSpace.DEFAULT;
    
    //获取资源的类型
    K8sKindTypeEnum typeEnum = K8sKindTypeEnum.getEnumByCode(resourceVo.getType());
    if (deployYamls != null && deployYamls.size() > 0) {
      int count = 0;
      ModuleDeployYaml tmpDeployYaml = new ModuleDeployYaml();
      String s = JSONObject.toJSONString(resourceVo.getDeployResource());
      Object load = null;
      K8sYamlVo k8sYamlVo = null;
      V1Deployment deployment = null;
      if (StringUtils.isNotBlank(s)) {
        try {
          load = io.kubernetes.client.util.Yaml.load(s);
          k8sYamlVo = K8sUtils.transObject2Vo(load);
          if (K8sApiversionTypeEnum.EXTENSIONAPI.getApiVersionType().equals(k8sYamlVo.getApiVersion())) {
            ExtensionsV1beta1Deployment beta1Deployment =
                K8sUtils.getObject(k8sYamlVo.getO(), ExtensionsV1beta1Deployment.class);
            deployment = K8sUtils.toV1Deploy(beta1Deployment);
          } else {
            deployment = K8sUtils.getObject(k8sYamlVo.getO(), V1Deployment.class);
          }
        } catch (IOException e) {
          e.printStackTrace();
          throw new TransYaml2K8sVoException("yaml json 格式错误: " + e.getMessage());
        }
      }
      
      for (ModuleDeployYaml deployYaml : deployYamls) {
        //主发布文件的更新修改
        if (deployYaml.getIsOnlineYaml() == 1 && typeEnum != null &&
            !K8sKindTypeEnum.SERVICE.getKindType().equals(deployYaml.getYamlType())) {
          if (deployYaml.getIsDeployed() == 1 &&
              !deployYaml.getYamlNamespace().equals(namespace)) {
            throw new TransYaml2K8sVoException("不能更改已上线模块的namespace，请先下线！");
          }
          if (deployment != null) {
            Map<String, String> nodeSelector =
                deployment.getSpec().getTemplate().getSpec().getNodeSelector();
            if (nodeSelector != null && nodeSelector.size() > 0) {
              for (String key : nodeSelector.keySet()) {
                if (StringUtils.isBlank(key)
                    || StringUtils.isBlank(nodeSelector.get(key))) {
                  throw new TransYaml2K8sVoException("nodeSelector格式错误 key: "
                      + key + " value: " + nodeSelector.get(key));
                }
              }
            }
            V1LabelSelector v1LabelSelector = new V1LabelSelector();
            v1LabelSelector.setMatchLabels(deployment.getSpec().getTemplate().getMetadata().getLabels());
            deployment.getSpec().setSelector(v1LabelSelector);
            deployment.getMetadata().setNamespace(namespace);
            tmpDeployYaml.setId(deployYaml.getId());
            tmpDeployYaml.setYamlName(k8sYamlVo.getMetadataName());
            tmpDeployYaml.setUpdateTime(new Date());
            tmpDeployYaml.setYamlNamespace(namespace);
            tmpDeployYaml.setYamlType(k8sYamlVo.getKind());
            
            tmpDeployYaml.setYamlJson(new Gson().toJson(deployment));
            //更新deployYaml的主
            deployYamlMapper.updateByPrimaryKeySelective(tmpDeployYaml);
          }
        }
        //表示有svc信息
        if (K8sKindTypeEnum.SERVICE.getKindType().equals(deployYaml.getYamlType())) {
          count++;
          if (resourceVo.getIsNeedSvc() == 1) {
            V1Service v1Service = K8sUtils.genV1Svc(deployYaml.getYamlName(), namespace,
                resourceVo.getTargetPort(), resourceVo.getPort(), resourceVo.getNodePort());
            ModuleDeployYaml svcYaml = new ModuleDeployYaml();
            svcYaml.setId(deployYaml.getId());
            svcYaml.setYamlName(deployYaml.getYamlName());
//                        svcYaml.setYamlJson(JSONObject.toJSONString(v1Service));
            svcYaml.setYamlJson(new Gson().toJson(v1Service));
            svcYaml.setUpdateTime(new Date());
            svcYaml.setYamlNamespace(namespace);
            deployYamlMapper.updateByPrimaryKeySelective(svcYaml);
          } else {
            //否则的话就是删除原有的svc信息
            deployYamlMapper.deleteByPrimaryKey(deployYaml.getId());
          }
        }
      }
      //原有的数据里面没有找到svc信息 编辑操作添加svc信息 需要添加新的svc信息
      if (count <= 0 && resourceVo.getIsNeedSvc() == 1 && tmpDeployYaml.getId() != null) {
        V1Service v1Service = K8sUtils.genV1Svc(
            tmpDeployYaml.getYamlName(), namespace,
            resourceVo.getTargetPort(), resourceVo.getPort(), resourceVo.getNodePort());
        ModuleDeployYaml moduleSvcYaml = new ModuleDeployYaml();
        moduleSvcYaml.setDeployId(resourceVo.getDeployId());
        moduleSvcYaml.setYamlName(tmpDeployYaml.getYamlName());
        moduleSvcYaml.setYamlType(K8sKindTypeEnum.SERVICE.getKindType());
        moduleSvcYaml.setYamlJson(new Gson().toJson(v1Service));
        moduleSvcYaml.setYamlNamespace(namespace);
        moduleSvcYaml.setUpdateTime(new Date());
        moduleSvcYaml.setCreateTime(new Date());
        deployYamlMapper.insertSelective(moduleSvcYaml);
      }
      return true;
    }
    return false;
  }
  
  /**
   * 获取默认的yamljson信息
   *
   * @param moduleContentName
   * @param k8sKindTypeEnum
   * @return
   */
  private Object getDefaultYaml(String moduleContentName, K8sKindTypeEnum k8sKindTypeEnum) {
    if (k8sKindTypeEnum != null) {
      switch (k8sKindTypeEnum) {
        case REPLICASET:
          break;
        case DEPLOYMENT:
          String s = jedisManage.get(RedisKeyPrefix.YAML_CONFIG_KEY + k8sKindTypeEnum.getKindType());
          if (StringUtils.isBlank(s)) {
            
            V1Deployment beta1Deployment = K8sUtils.generateDeployMents(moduleContentName);
            jedisManage.set(RedisKeyPrefix.YAML_CONFIG_KEY +
                    k8sKindTypeEnum.getKindType(),
                new Gson().toJson(beta1Deployment));
            
            return beta1Deployment;
          } else {
            V1Deployment deployment = new Gson().fromJson(s, V1Deployment.class);
            deployment.getMetadata().getLabels().put("name", moduleContentName);
            deployment.getMetadata().setName(moduleContentName);
            deployment.getSpec().getTemplate().getMetadata().getLabels().put("name", moduleContentName);
            deployment.getSpec().getTemplate().getSpec().getContainers().get(0).setName(moduleContentName);
            return deployment;
          }
        case SERVICE:
          break;
        default:
          break;
      }
    }
    return null;
  }
  
  @Override
  public void compareProdYaml() {
    if (!prodIds.isEmpty()) {
      List<String> envIdList = Arrays.asList(prodIds.split(","));
      List<ModuleEnv> envList = new ArrayList<>();
      for (int i = 0; i < envIdList.size(); i++) {
        ModuleEnv moduleEnv = envMapper.selectOne(Integer.valueOf(envIdList.get(i)));
        envList.add(moduleEnv);
      }
      ThreadPoolExecutor poolExecutor = new ThreadPoolExecutor(envList.size(), envList.size(), 0L,
          TimeUnit.SECONDS, new ArrayBlockingQueue<>(100));
      for (ModuleEnv env : envList) {
        poolExecutor.submit(new MyYamlCompareThread(env, deployYamlMapper, storgePrefix));
      }
      //关闭线程池的操作
      Executors.newSingleThreadExecutor().submit(new ShutDownThread(poolExecutor));
    }
  }
}
