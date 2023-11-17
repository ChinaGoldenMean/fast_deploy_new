package com.xc.fast_deploy.service.common.impl;

import cn.hutool.core.io.FileUtil;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.offbytwo.jenkins.JenkinsServer;
import com.offbytwo.jenkins.model.JobWithDetails;
import com.xc.fast_deploy.dao.master_dao.*;
import com.xc.fast_deploy.dto.MyPageInfo;
import com.xc.fast_deploy.dto.StatusDTO;
import com.xc.fast_deploy.dto.UploadModuleStatusDTO;
import com.xc.fast_deploy.dto.module.ModuleCenterEnvDTO;
import com.xc.fast_deploy.dto.module.ModuleManageDTO;
import com.xc.fast_deploy.dto.module.ModulePackageDTO;
import com.xc.fast_deploy.model.master_model.*;
import com.xc.fast_deploy.model.master_model.example.*;
import com.xc.fast_deploy.myException.*;
import com.xc.fast_deploy.myenum.CertificateTypeEnum;
import com.xc.fast_deploy.myenum.CodeUpTypeEnum;
import com.xc.fast_deploy.myenum.ComplieTypeEnum;
import com.xc.fast_deploy.myenum.ModuleTypeEnum;
import com.xc.fast_deploy.service.common.ModuleCertificateService;
import com.xc.fast_deploy.service.common.ModuleManageService;
import com.xc.fast_deploy.service.common.ModulePackageService;
import com.xc.fast_deploy.service.common.SyncService;
import com.xc.fast_deploy.utils.DateUtils;
import com.xc.fast_deploy.utils.FoldUtils;
import com.xc.fast_deploy.utils.code_utils.ExcelPhraseUtils;
import com.xc.fast_deploy.utils.code_utils.GitUtils;
import com.xc.fast_deploy.utils.code_utils.SvnUtils;
import com.xc.fast_deploy.utils.constant.K8sNameSpace;
import com.xc.fast_deploy.utils.encyption_utils.EncryptUtil;
import com.xc.fast_deploy.utils.jenkins.JenkinsManage;
import com.xc.fast_deploy.utils.k8s.K8sManagement;
import com.xc.fast_deploy.utils.k8s.K8sUtils;
import com.xc.fast_deploy.vo.FoldDataVo;
import com.xc.fast_deploy.vo.K8sYamlVo;
import com.xc.fast_deploy.vo.module_vo.ModuleManageDeployVO;
import com.xc.fast_deploy.vo.module_vo.ModuleManageUpdateVo;
import com.xc.fast_deploy.vo.module_vo.ModuleMangeVo;
import com.xc.fast_deploy.vo.module_vo.param.ModuleCenterSelectParamVo;
import com.xc.fast_deploy.vo.module_vo.param.ModuleManageParamVo;
import com.xc.fast_deploy.vo.module_vo.param.ModuleManageSelectParamVo;
import com.xc.fast_deploy.vo.module_vo.param.ModulePackageParamVo;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.AppsV1Api;
import io.kubernetes.client.openapi.models.V1Deployment;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.POIXMLException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;
import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.yaml.snakeyaml.reader.ReaderException;
import org.yaml.snakeyaml.scanner.ScannerException;
import org.yeauty.pojo.Session;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.*;
import java.util.*;

import static com.xc.fast_deploy.utils.FoldUtils.SEP;
import static com.xc.fast_deploy.utils.FoldUtils.SSH;

@Service
@Slf4j
public class ModuleManageServiceImpl extends BaseServiceImpl<ModuleManage, Integer> implements ModuleManageService {
  
  @Autowired
  private ModuleManageMapper manageMapper;
  
  @Autowired
  private ModuleDeployMapper deployMapper;
  
  @Autowired
  private ModulePackageService packageService;
  
  @Autowired
  private ModuleCertificateService certificateService;
  
  @Autowired
  private SyncService syncService;
  
  @Autowired
  private ModuleJobMapper jobMapper;
  
  @Autowired
  private ModuleCenterMapper centerMapper;
  
  @Resource
  private JenkinsManage jenkinsManage;
  
  @Autowired
  private ModuleEnvMapper envMapper;
  
  @Autowired
  private ModuleDeployYamlMapper deployYamlMapper;
  
  @Value("${file.storge.path.prefix}")
  private String storgePrefix;
  
  @Value("${file.storge.path.moduleUploadPath}")
  private String moduleUploadPath;
  
  @Value("${file.storge.path.moduleExportPath}")
  private String moduleExportPath;
  
  @PostConstruct
  public void init() {
    super.init(manageMapper);
  }
  
  @Override
  public MyPageInfo<ModuleMangeVo> getAllModuleDTOBySelect(Integer pageNum, Integer pageSize, ModuleManageSelectParamVo manageSelectParamVo) {
    MyPageInfo<ModuleMangeVo> myPageInfo = new MyPageInfo<>();
    if (pageNum != null && pageNum > 0 && pageSize != null && pageSize > 0) {
      PageHelper.startPage(pageNum, pageSize);
      List<ModuleMangeVo> list = manageMapper.selectModuleVoPageByVo(manageSelectParamVo);
      PageInfo<ModuleMangeVo> pageInfo = new PageInfo<>(list);
      BeanUtils.copyProperties(pageInfo, myPageInfo);
    }
    return myPageInfo;
  }
  
  /**
   * 存储新添加的模块管理的信息
   *
   * @param mangeVo
   * @param codeExcelFile programPackages
   * @return
   */
  @SneakyThrows
  @Override
  @Transactional(rollbackFor = Exception.class)
  public boolean saveAll(String userId, ModuleManageParamVo mangeVo,
                         MultipartFile codeExcelFile, MultipartFile yamlFile) {
    ModuleManage moduleManage = new ModuleManage();
    moduleManage.setUserId(userId);
    if (mangeVo != null) {
      BeanUtils.copyProperties(mangeVo, moduleManage);
      Integer centerId = Integer.valueOf(mangeVo.getCenterId());
      Integer envId = Integer.valueOf(mangeVo.getEnvId());
      //查询该环境下是否存在该中心 //先查询出该环境下的所有中心 然后匹配中心id
      ModuleCenterSelectParamVo paramVo = new ModuleCenterSelectParamVo();
      paramVo.setEnvId(envId);
      paramVo.setCenterId(centerId);
      List<ModuleCenterEnvDTO> centerEnvDTOS = centerMapper.selectByParamVo(paramVo);
      
      ModuleCenterEnvDTO centerEnvDTO = null;
      
      if (centerEnvDTOS != null && centerEnvDTOS.size() > 0) {
        centerEnvDTO = centerEnvDTOS.get(0);
      } else {
        log.error("中心不存在: {}", centerId);
        return false;
      }
      //查询中心下的所有模块,确认模块目录添加不能重复
      List<ModuleManage> manageList = selectAllByCenterId(centerId);
      if (manageList != null && manageList.size() > 0) {
        for (ModuleManage manage : manageList) {
          if (manage.getModuleContentName().equals(mangeVo.getModuleContentName())) {
            log.error("模块目录添加重复: {}", manage.getModuleContentName());
            throw new ValidateExcetion("该中心下的模块目录添加重复: "
                + manage.getModuleContentName());
          }
        }
      }
      moduleManage.setCenterId(centerId);
      moduleManage.setModuleType(Integer.valueOf(mangeVo.getModuleType()));
      moduleManage.setEnvId(envId);
      moduleManage.setEnvCode(centerEnvDTO.getEnvCode());
      moduleManage.setMark(centerEnvDTO.getEnvName());
      moduleManage.setChargePerson(mangeVo.getChargePerson());
      moduleManage.setCreateTime(new Date());
      moduleManage.setUpdateTime(new Date());
      ModuleTypeEnum typeEnum = ModuleTypeEnum.getTypeByCode(moduleManage.getModuleType());
      if (typeEnum != null) {
        ModuleCertificate certificate = null;
        //模块文件目录的存放路径
        StringBuilder filePrefix = new StringBuilder();
        filePrefix.append(storgePrefix).append(centerEnvDTO.getCenterPath()).append(SEP)
            .append(mangeVo.getModuleContentName()).append(SEP);
        
        File filePre = new File(filePrefix.toString());
        if (!filePre.exists() && !filePre.mkdirs()) {
          log.info("创建文件夹失败");
          return false;
        }
        
        if (typeEnum.equals(ModuleTypeEnum.GIT_SOURCE_CODE) ||
            typeEnum.equals(ModuleTypeEnum.SVN_SOURCE_CODE) ||
            typeEnum.equals(ModuleTypeEnum.GIT_AUTO_UP_SOURCE_CODE)) {
          Integer certificatiId = Integer.valueOf(mangeVo.getCertificateId());
          certificate = certificateService.selectById(certificatiId);
          if (certificate != null) {
            moduleManage.setCertificateId(certificatiId);
          }
          if (mangeVo.getCodeUpType().equals(CodeUpTypeEnum.UPLOAD_FILE.getCode().toString())) {
            try {
              //将excel数据解析出来 或者是.sh中的数据提取出来
              StringBuilder fileName = new StringBuilder();
              List<ModulePackageParamVo> packageList = null;
              if (codeExcelFile.getOriginalFilename().endsWith(".xlsx")) {
                packageList = ExcelPhraseUtils.
                    getAllExcelData(codeExcelFile.getInputStream());
              } else {
                packageList = ExcelPhraseUtils.
                    getAllShData(codeExcelFile.getInputStream());
              }
              
              if (packageList != null && packageList.size() > 0) {
                mangeVo.setPackageList(packageList);
                //上传excel文件保存起来
                fileName.append(filePrefix).append(codeExcelFile.getOriginalFilename())
                    .append(DateUtils.generateDateString());
                codeExcelFile.transferTo(new File(fileName.toString()));
              }
            } catch (IOException e) {
              FoldUtils.deleteFolders(filePrefix.toString());
              log.error("存储excel文件失败");
              throw new FileStoreException("存储excel文件失败");
            } catch (POIXMLException e) {
              FoldUtils.deleteFolders(filePrefix.toString());
              log.error("excel文件解析出错");
              throw new FileStoreException("svn_excel文件解析出错");
            }
          }
        }
        
        switch (typeEnum) {
          case SVN_SOURCE_CODE:
            return saveModulePackage(certificate, moduleManage, mangeVo, filePrefix);
          case SVN_AUTO_UP_CODE:
            //解析填入的脚本地址验证是否为svnUrl路径
            try {
              Integer certificatiId = Integer.valueOf(mangeVo.getCertificateId());
              certificate = certificateService.selectById(certificatiId);
              if (certificate != null) {
                moduleManage.setCertificateId(certificatiId);
                boolean urlExist = SvnUtils.isURLExist(mangeVo.getSvnAutoUpUrl(),
                    certificate.getUsername(),
                    EncryptUtil.decrypt(certificate.getPassword()));
                if (!urlExist) {
                  throw new SvnUrlNotExistException(mangeVo.getSvnAutoUpUrl()
                      + " 不是一个正确的svn地址");
                }
                //然后将脚本下来下来
                StringBuilder shFilePath = new StringBuilder(filePrefix);
                String[] split = mangeVo.getSvnAutoUpUrl().split(SEP);
                shFilePath.append(SSH).append(SEP).append(split[split.length - 1]);
                
                File file = new File(shFilePath.toString());
                SVNClientManager clientManager = SvnUtils.getManager(certificate.getUsername(),
                    EncryptUtil.decrypt(certificate.getPassword()));
                SvnUtils.doExport(clientManager, SVNURL.parseURIEncoded(mangeVo.getSvnAutoUpUrl()),
                    SVNRevision.create(SvnUtils.LATEST_REVERSION), file, SVNDepth.INFINITY);
                moduleManage.setShPath(shFilePath.toString().replace(storgePrefix, ""));
                moduleManage.setSvnAutoUrl(mangeVo.getSvnAutoUpUrl());
                List<ModulePackageParamVo> paramVos =
                    ExcelPhraseUtils.getAllShData(new FileInputStream(file));
                mangeVo.setPackageList(paramVos);
                return saveModulePackage(certificate, moduleManage, mangeVo, filePrefix);
              }
            } catch (SVNException | FileNotFoundException e) {
              e.printStackTrace();
              throw new SvnUrlNotExistException(e.getMessage());
            }
          case GIT_SOURCE_CODE:
            gitSourceSet(certificate, moduleManage, mangeVo, filePrefix);
            
            break;
          case GIT_AUTO_UP_SOURCE_CODE:
            
            moduleManage.setSvnAutoUrl(mangeVo.getSvnAutoUpUrl());
            packageService.gitAutoType(certificate, moduleManage, null, null, filePrefix, null);
            break;
          case PROJECT_PACKAGE:
            //程序包类型的插入  //首先将模块相关数据存储到数据库
            moduleManage.setCertificateId(null);
            manageMapper.insertSelective(moduleManage);
            List<ModulePackage> packageList = new ArrayList<>();
            List<String> fileList = mangeVo.getProgramFileNameList();
            if (fileList != null && fileList.size() > 0) {
              for (String fileName : fileList) {
                File file = new File(moduleUploadPath + fileName);
                if (file.exists()) {
                  //转移文件到指定位置 采用异步的形式 首先把记录添加成功
                  syncService.copyFile2File(file, new File(filePrefix.append(fileName).toString()));
                  ModulePackage modulePackage = new ModulePackage();
                  modulePackage.setCreateTime(new Date());
                  modulePackage.setModuleId(moduleManage.getId());
                  //存入上传文件的全路径
                  modulePackage.setPackagePathName(filePrefix.toString()
                      .replace(storgePrefix, ""));
                  packageList.add(modulePackage);
                }
              }
            }
            packageService.insertAll(packageList);
            packageList.clear();
            return true;
          case YAML_DEPLOY_TYPE:
            //YAML文件类型插入 首先将模块的相关数据存储下来
            moduleManage.setCertificateId(null);
            manageMapper.insertSelective(moduleManage);
            String yamlFilePath =
                filePrefix.append(yamlFile.getOriginalFilename()).toString();
            //保存modulePackage信息
            ModulePackage modulePackage = new ModulePackage();
            modulePackage.setCreateTime(new Date());
            modulePackage.setModuleId(moduleManage.getId());
            modulePackage.setPackagePathName(filePrefix.toString()
                .replace(storgePrefix, ""));
            packageService.insertSelective(modulePackage);
            File file = new File(yamlFilePath);
            //保存deploy信息
            ModuleDeploy moduleDeploy = new ModuleDeploy();
            moduleDeploy.setModuleId(moduleManage.getId());
            moduleDeploy.setCreateTime(new Date());
            moduleDeploy.setUpdateTime(new Date());
            moduleDeploy.setEnvId(moduleManage.getEnvId());
            deployMapper.insertSelective(moduleDeploy);
            ModuleJob moduleJob = new ModuleJob();
            StringBuilder jobName = new StringBuilder();
            moduleJob.setJobName(jobName.append(moduleManage.getEnvCode())
                .append("-").append(moduleManage.getModuleContentName())
                .append("-").append(UUID.randomUUID().toString().split("-")[1]).toString());
            moduleJob.setJobName(jobName.toString());
            moduleJob.setUpdateTime(new Date());
            moduleJob.setModuleId(moduleManage.getId());
            moduleJob.setCreateTime(new Date());
            moduleJob.setModuleEnvId(moduleManage.getEnvId());
            moduleJob.setDockerfilePath("none");
            jobMapper.insertSelective(moduleJob);
            K8sYamlVo k8sYamlVo;
            try {
              yamlFile.transferTo(file);
              k8sYamlVo = K8sUtils.transYaml2Vo(file);
            } catch (IOException e) {
              log.error("YAML文件存储出现错误: {}", yamlFile.getOriginalFilename());
              throw new FileStoreException("deploy文件存储出现错误");
            } catch (ReaderException | ScannerException e) {
              throw new TransYaml2K8sVoException("yaml文件转换为k8svo 出现错误: " + e.getMessage());
            }
            //开始存储YAML文件对应的解析出来的信息
            if (k8sYamlVo == null) {
              FoldUtils.deleteFolders(yamlFilePath);
              throw new TransYaml2K8sVoException("yaml文件未能解析到正确数据");
            }
            //需要解析file内容确定YAML文件的正确性去存储相关信息,主要的发布模块
            ModuleDeployYaml deployYaml = new ModuleDeployYaml();
            deployYaml.setDeployId(moduleDeploy.getId());
            deployYaml.setYamlPath(modulePackage.getPackagePathName());
            deployYaml.setYamlType(k8sYamlVo.getKind());
            deployYaml.setYamlName(k8sYamlVo.getMetadataName());
            
            deployYaml.setYamlNamespace(k8sYamlVo.getNamespace());
            deployYaml.setIsOnlineYaml(1);
            deployYaml.setCreateTime(new Date());
            deployYaml.setUpdateTime(new Date());
            deployYamlMapper.insertSelective(deployYaml);
            return true;
          default:
            break;
        }
      }
    }
    return false;
  }
  
  private void gitSourceSet(ModuleCertificate certificate, ModuleManage moduleManage, ModuleManageParamVo mangeVo, StringBuilder filePrefix) {
    if (certificate != null && CertificateTypeEnum.
        GIT_CERTIFICATE_TYPE.getCode().equals(certificate.getType())) {
      manageMapper.insertSelective(moduleManage);
      
      List<ModulePackage> packageList = new ArrayList<>();
      //然后对package里面的svnUrl做Url验证
      String reversion = null;
      for (ModulePackageParamVo packageVo : mangeVo.getPackageList()) {
        ModulePackage modulePackage = new ModulePackage();
        String gitBranch = packageVo.getGitBranch();
        try {
          //git clone 下拉代码
          //如果git不传值的话默认拉取master分支下的数据
          if (StringUtils.isBlank(gitBranch)) {
            gitBranch = "master";
          }
          GitUtils.gitClone(packageVo.getCodeUrl(),
              filePrefix + packageVo.getContentName(),
              certificate.getUsername(),
              EncryptUtil.decrypt(certificate.getPassword()),
              gitBranch);
        } catch (GitAPIException e) {
          throw new SvnUrlNotExistException(e.getMessage());
        }
        modulePackage.setGitBranch(gitBranch);
        modulePackage.setCodeReversion("-1");
        modulePackage.setCreateTime(new Date());
        modulePackage.setCodeType(Integer.valueOf(mangeVo.getModuleType()));
        modulePackage.setContentName(packageVo.getContentName());
        modulePackage.setCodeUrl(packageVo.getCodeUrl());
        modulePackage.setModuleId(moduleManage.getId());
        modulePackage.setPackagePathName((filePrefix + packageVo.getContentName()).
            replace(storgePrefix, ""));
        packageList.add(modulePackage);
      }
      packageService.insertAll(packageList);
    }
  }
  
  @Override
  public boolean saveGitModulePackage(ModuleCertificate certificate, ModuleManage moduleManage, List<ModulePackageParamVo> packageList, StringBuilder filePrefix, StatusDTO statusDTO, Session session) {
    if (certificate != null) {
      manageMapper.insertSelective(moduleManage);
      List<ModulePackageDTO> packageGitDTOS = packageService.selectByModuleId(moduleManage.getId());
      if (statusDTO != null) {
        for (int i = 0; i < packageList.size(); i++) {
          statusDTO.setCurrent(i + 1);
          statusDTO.setName(packageList.get(i).getContentName());
          if (session != null) {
            session.sendText(JSONObject.toJSONString(statusDTO));
          }
          
        }
      }
      
      ////删除原有的数据
      if (!CollectionUtils.isEmpty(packageGitDTOS)) {
        for (ModulePackageDTO packageDTO : packageGitDTOS) {
          //首先删除记录
          deleteById(packageDTO.getId());
          //然后删除对应的所有文件
          FoldUtils.deleteFolders(storgePrefix + packageDTO.getPackagePathName());
        }
      }
      
      List<ModulePackage> modulePackages = new ArrayList<>();
      //然后对package里面的svnUrl做Url验证
      for (ModulePackageParamVo packageVo : packageList) {
        if (statusDTO != null) {
          statusDTO.setTotal(packageList.size() * 2);
        }
        ModulePackage modulePackage = new ModulePackage();
        modulePackage.setCodeReversion("-1");
        modulePackage.setCreateTime(new Date());
        modulePackage.setCodeType(ModuleTypeEnum.GIT_AUTO_UP_SOURCE_CODE.getModuleTypeCode());
        modulePackage.setContentName(packageVo.getContentName());
        modulePackage.setCodeUrl(packageVo.getCodeUrl());
        modulePackage.setModuleId(moduleManage.getId());
        modulePackage.setPackagePathName((filePrefix + packageVo.getContentName()).
            replace(storgePrefix, ""));
        modulePackages.add(modulePackage);
        packageService.insertAll(modulePackages);
        return true;
      }
      
    }
    return false;
  }
  
  /**
   * svn源码类型的数据存储
   *
   * @param certificate
   * @param moduleManage
   * @param mangeVo
   * @param filePrefix
   * @return
   */
  private boolean saveModulePackage(ModuleCertificate certificate, ModuleManage moduleManage,
                                    ModuleManageParamVo mangeVo, StringBuilder filePrefix) {
    if (certificate != null && CertificateTypeEnum.SVN_CERTIFICATE_TYPE.getCode().equals(certificate.getType())) {
      manageMapper.insertSelective(moduleManage);
      List<ModulePackage> packageList = new ArrayList<>();
      //然后对package里面的svnUrl做Url验证
      for (ModulePackageParamVo packageVo : mangeVo.getPackageList()) {
        ModulePackage modulePackage = new ModulePackage();
        try {
          boolean exist = SvnUtils.isURLExist(packageVo.getCodeUrl(),
              certificate.getUsername(), EncryptUtil.decrypt(certificate.getPassword()));
          if (!exist) {
            packageList.clear();
            mangeVo.getPackageList().clear();
            throw new SvnUrlNotExistException("svnUrl不存在异常: " + packageVo.getCodeUrl());
          }
        } catch (SVNException e) {
          FoldUtils.deleteFolders(filePrefix.toString());
          throw new SvnUrlNotExistException(e.getMessage());
        }
        modulePackage.setCodeReversion(String.valueOf(SvnUtils.LATEST_REVERSION));
        modulePackage.setCreateTime(new Date());
        modulePackage.setCodeType(ModuleTypeEnum.SVN_SOURCE_CODE.getModuleTypeCode());
        modulePackage.setContentName(packageVo.getContentName());
        modulePackage.setCodeUrl(packageVo.getCodeUrl());
        modulePackage.setModuleId(moduleManage.getId());
        modulePackage.setPackagePathName((filePrefix + packageVo.getContentName()).
            replace(storgePrefix, ""));
        packageList.add(modulePackage);
      }
      packageService.insertAll(packageList);
      syncService.checkoutAllAsync(packageList, certificate);
      return true;
    }
    return false;
  }
  
  /**
   * 根据模块id查询模块对应的相关联的包括中心的信息
   *
   * @return
   */
  @Override
  public ModuleManageDTO selectInfoById(Integer moduleId) {
    ModuleManageDTO moduleManageDTO = manageMapper.selectInfoById(moduleId);
    if (moduleManageDTO != null && moduleManageDTO.getCertificateId() != null) {
      ModuleCertificate certificate = certificateService
          .selectById(moduleManageDTO.getCertificateId());
      if (certificate != null) {
        moduleManageDTO.setCertificateName(certificate.getName());
      }
    }
    return moduleManageDTO;
  }
  
  /**
   * 更新模块信息
   *
   * @param updateVo
   * @param excelFile
   * @param packageFiles
   * @return
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  public boolean updateInfo(ModuleManageUpdateVo updateVo, MultipartFile excelFile, MultipartFile[] packageFiles) {
    ModuleManage moduleManage = new ModuleManage();
    if (updateVo != null && StringUtils.isNotBlank(updateVo.getModuleId())) {
      ModuleManage manage = manageMapper.selectByPrimaryKey(Integer.valueOf(updateVo.getModuleId()));
      moduleManage.setId(manage.getId());
      moduleManage.setCenterId(Integer.valueOf(updateVo.getCenterId()));
      moduleManage.setUpdateTime(new Date());
      moduleManage.setModuleName(updateVo.getModuleName());
      
      //模块目录定义为不可更改,如果模块目录更改的话会将数据结构变动太大,要么就只能重新建模块目录
      moduleManage.setModuleContentName(null);
      
      //判断模块的更新类型
      if (StringUtils.isNotBlank(updateVo.getModuleType()) &&
          updateVo.getModuleType().equals(ModuleTypeEnum.SVN_SOURCE_CODE
              .getModuleTypeCode().toString()) && StringUtils.isNotBlank(updateVo.getCertificateId())) {
        //查询凭证的正确性
        ModuleCertificate certificate = certificateService.selectById(Integer.valueOf(updateVo.getCertificateId()));
        
        if (certificate != null && CertificateTypeEnum.SVN_CERTIFICATE_TYPE.getCode().equals(certificate.getType())) {
          //验证成功之后
          moduleManage.setModuleType(Integer.valueOf(updateVo.getModuleType()));
          moduleManage.setCertificateId(certificate.getId());
          //先将模块信息更新
          int i = manageMapper.updateByPrimaryKeySelective(moduleManage);
          //如果有excel文件的上传
          if (i > 0 && excelFile != null && !excelFile.isEmpty()) {
            try {
              List<ModulePackageParamVo> paramPackageList = ExcelPhraseUtils.
                  getAllExcelData(excelFile.getInputStream());
              if (paramPackageList != null && paramPackageList.size() > 0) {
                //删除原有的package信息
                if (packageService.deleteByModuleId(moduleManage.getId())) {
                  //删除原有的模块目录下的所有文件
                  if (!FoldUtils.deleteFolders(storgePrefix +
                      manage.getModuleContentName())) {
                    //如果数据删除失败则抛出错误
                    throw new NotDeleteOkException("模块包删除失败");
                  }
                  List<ModulePackage> packageList = new ArrayList<>();
                  //然后对package里面的svnUrl做Url验证
                  for (ModulePackageParamVo packageVo : paramPackageList) {
                    ModulePackage modulePackage = new ModulePackage();
                    Long urlReversion = null;
                    try {
                      urlReversion = SvnUtils.getUrlReversion(packageVo.getCodeUrl(),
                          certificate.getUsername(),
                          EncryptUtil.decrypt(certificate.getPassword()));
                      if (urlReversion == null) {
                        packageList.clear();
                        paramPackageList.clear();
                        throw new SvnUrlNotExistException(packageVo.getCodeUrl());
                      }
                    } catch (SVNException e) {
                      throw new SvnUrlNotExistException(e.getMessage());
                    }
                    modulePackage.setCreateTime(new Date());
                    modulePackage.setCodeReversion(String.valueOf(urlReversion.intValue()));
                    modulePackage.setContentName(packageVo.getContentName());
                    modulePackage.setCodeUrl(packageVo.getCodeUrl());
                    modulePackage.setModuleId(moduleManage.getId());
                    packageList.add(modulePackage);
                  }
                  //验证完成后将数据保存下来
                  packageService.insertAll(packageList);
                  
                  StringBuffer filePrefix = new StringBuffer(storgePrefix +
                      manage.getModuleContentName() + "/");
                  File filePre = new File(filePrefix.toString());
                  if (!filePre.exists()) {
                    filePre.mkdirs();
                  }
                  filePrefix.append(excelFile.getOriginalFilename())
                      .append(DateUtils.generateDateString());
                  excelFile.transferTo(new File(filePrefix.toString()));
                  
                } else {
                  //如果数据删除失败则抛出错误
                  throw new NotDeleteOkException("模块包删除失败");
                }
              } else {
                return false;
              }
            } catch (IOException e) {
              log.error("解析excel文件出错");
              throw new FileStoreException("解析svn_excel文件出错");
            }
          }
          return true;
        }
      } else if (StringUtils.isNotBlank(updateVo.getModuleType()) &&
          updateVo.getModuleType().equals(ModuleTypeEnum.PROJECT_PACKAGE
              .getModuleTypeCode().toString())) {
        //如果是程序包类型
        moduleManage.setModuleType(Integer.valueOf(updateVo.getModuleType()));
        moduleManage.setCertificateId(null);
        //首先更新 模块信息
        int i = manageMapper.updateByPrimaryKeySelective(moduleManage);
        
        //然后再就是判断是否有新的程序包上传
        if (packageFiles != null && packageFiles.length > 0 && verifyFileEmpty(packageFiles)) {
          //先删除数据库中的数据
          if (packageService.deleteByModuleId(moduleManage.getId())) {
            //数据删除成功 //删除原有的文件
            if (!FoldUtils.deleteFolders(storgePrefix + manage.getModuleContentName())) {
              //如果数据删除失败则抛出错误
              throw new NotDeleteOkException("模块包删除失败");
            }
            //新建文件夹 存入新的数据
            String filePrefix = storgePrefix + manage.getModuleContentName() + "/";
            File filePre = new File(filePrefix);
            if (!filePre.exists()) {
              filePre.mkdirs();
            }
            List<ModulePackage> packageList = new ArrayList<>();
            //上传文件,并存入数据库
            for (MultipartFile file : packageFiles) {
              ModulePackage modulePackage = new ModulePackage();
              String fileName = filePrefix + file.getOriginalFilename();
              try {
                file.transferTo(new File(fileName));
                modulePackage.setCreateTime(new Date());
                modulePackage.setModuleId(manage.getId());
                modulePackage.setPackagePathName(fileName);
                packageList.add(modulePackage);
              } catch (IOException e) {
                log.error("上传文件失败");
                throw new FileStoreException("文件上传失败: " + fileName);
              }
            }
            packageService.insertAll(packageList);
            packageList.clear();
          } else {
            //如果数据删除失败则抛出错误
            throw new NotDeleteOkException("模块包删除失败");
          }
        }
        return true;
      }
    }
    return false;
  }
  
  /**
   * 根据模块id删除模块内容
   *
   * @param moduleId
   * @param moduleManage
   * @return
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  public boolean deleteInfoById(Integer moduleId, ModuleManage moduleManage) {
    //物理文件存储直接删除 //表字段数据逻辑删除
    if (moduleManage != null) {
      ModuleDeployExample deployExample = new ModuleDeployExample();
      ModuleDeployExample.Criteria deployExampleCriteria = deployExample.createCriteria();
      deployExampleCriteria.andIsDeleteEqualTo(0).andModuleIdEqualTo(moduleId);
      List<ModuleDeploy> moduleDeploys = deployMapper.selectByExample(deployExample);
      if (moduleDeploys != null && moduleDeploys.size() > 0) {
        throw new DeployIsOnlineExcetion("该模块在发布中,请先下线!");
      }
      ModuleCenterSelectParamVo paramVo = new ModuleCenterSelectParamVo();
      paramVo.setCenterId(moduleManage.getCenterId());
      paramVo.setEnvId(moduleManage.getEnvId());
      List<ModuleCenterEnvDTO> centerEnvDTOS = centerMapper.selectByParamVo(paramVo);
      
      if (centerEnvDTOS != null && centerEnvDTOS.size() > 0) {
        ModuleCenterEnvDTO centerEnvDTO = centerEnvDTOS.get(0);
        moduleManage.setIsDelete(1);
        manageMapper.updateByPrimaryKeySelective(moduleManage);
        //package 直接物理删除
        packageService.deleteByModuleId(moduleId);
        //然后再删除有关的数据存储
        StringBuilder fileName = new StringBuilder();
        
        if (!FoldUtils.deleteFolders(fileName.append(storgePrefix)
            .append(centerEnvDTO.getCenterPath()).append(SEP)
            .append(moduleManage.getModuleContentName()).toString())) {
          log.error("删除模块对应文件失败 file: {}", fileName.toString());
          throw new NotDeleteOkException("删除文件失败");
        }
        //删除jenkins里面的job信息 并且更新数据库信息
        ModuleJobExample moduleJobExample = new ModuleJobExample();
        ModuleJobExample.Criteria criteria = moduleJobExample.createCriteria();
        criteria.andIsDeleteEqualTo(0).andModuleIdEqualTo(moduleId);
        List<ModuleJob> moduleJobs = jobMapper.selectByExample(moduleJobExample);
        if (moduleJobs != null && moduleJobs.size() > 0) {
          JenkinsServer jenkinsServer = jenkinsManage.getJenkinsServer();
          for (ModuleJob moduleJob : moduleJobs) {
            try {
              JobWithDetails job = jenkinsServer.getJob(moduleJob.getJobName());
              if (job != null) {
                jenkinsServer.deleteJob(moduleJob.getJobName());
              }
            } catch (IOException e) {
              throw new NotDeleteOkException("删除job失败");
            }
            moduleJob.setIsDelete(1);
            moduleJob.setUpdateTime(new Date());
            jobMapper.updateByPrimaryKey(moduleJob);
          }
        }
        return true;
      }
    }
    return false;
  }
  
  /**
   * 根据moduleId 获取该模块下所有的文件树形图 这里排除了.svn文件夹的内容
   *
   * @param moduleId
   * @return
   */
  @Override
  public FoldDataVo getAllManageFolders(Integer moduleId) {
    FoldDataVo foldDataVo = new FoldDataVo();
    ModuleManageDTO moduleManageDTO = manageMapper.selectInfoById(moduleId);
    if (moduleManageDTO != null) {
      StringBuilder sb = new StringBuilder();
      sb.append(storgePrefix).append(moduleManageDTO.getCenterPath())
          .append(SEP).append(moduleManageDTO.getModuleContentName());
      File file = new File(sb.toString());
      if (!file.exists()) {
        return null;
      }
      int count = 0;
      FoldUtils.getAllFoldJson(file, foldDataVo, storgePrefix, count);
      return foldDataVo;
    }
    return foldDataVo;
  }
  
  /**
   * 根据中心id 获取中心下面的所有可用模块 并根据update_time 排好序
   * 并且未配置job任务的
   *
   * @param centerId
   * @return
   */
  @Override
  public List<ModuleManage> selectAllByCenterId(Integer centerId) {
    ModuleManageExample manageExample = new ModuleManageExample();
    manageExample.setOrderByClause("update_time DESC");
    ModuleManageExample.Criteria criteria = manageExample.createCriteria();
    criteria.andCenterIdEqualTo(centerId).andIsDeleteEqualTo(0);
    return manageMapper.selectByExample(manageExample);
  }
  
  /**
   * 更新模块的配置操作
   *
   * @param moduleManage
   * @param moduleType
   * @return
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  public int updateBySelective(ModuleManage moduleManage, Integer moduleType, String svnAutoUpUrl) throws SVNException {
    ModuleTypeEnum typeByCode = ModuleTypeEnum.getTypeByCode(moduleType);
    if (typeByCode != null) {
      ModuleCertificate certificate = certificateService.selectById(moduleManage.getCertificateId());
      switch (typeByCode) {
        case SVN_SOURCE_CODE:
          moduleManage.setModuleType(ModuleTypeEnum.SVN_SOURCE_CODE.getModuleTypeCode());
          break;
        case GIT_AUTO_UP_SOURCE_CODE:
          packageService.gitAutoType(certificate, moduleManage, null, null, null, null);
          
          break;
        case SVN_AUTO_UP_CODE:
          if (StringUtils.isNotBlank(svnAutoUpUrl)) {
            //首先查询得到相关的凭证信息
            
            if (certificate != null) {
              //svnUrl是全新的 不存在的
              //首先验证svnUrlPath 是否为正确的svn地址
              if (SvnUtils.isURLExist(svnAutoUpUrl, certificate.getUsername(),
                  EncryptUtil.decrypt(certificate.getPassword()))) {
                //下拉路径里面的代码到新路径下 模块目录下的ssh目录之下
                ModuleCenter center = centerMapper.selectByPrimaryKey(moduleManage.getCenterId());
                if (center != null) {
                  String[] split = svnAutoUpUrl.trim().split("/");
                  StringBuilder sb = new StringBuilder();
                  sb.append(storgePrefix).append(center.getCenterPath()).append(SEP)
                      .append(moduleManage.getModuleContentName()).append(SEP)
                      .append("ssh/");
                  //这个目录原有的先删除掉
                  File file1 = new File(sb.toString());
                  if (file1.exists()) {
                    FoldUtils.deleteFolders(file1.getAbsolutePath());
                  }
                  //然后再下来新的脚本
                  sb.append(split[split.length - 1]);
                  File file = new File(sb.toString());
                  
                  SVNClientManager clientManager =
                      SvnUtils.getManager(certificate.getUsername(),
                          EncryptUtil.decrypt(certificate.getPassword()));
                  
                  //下拉代码到指定路径下面去
                  SvnUtils.doExport(clientManager, SVNURL.parseURIEncoded(svnAutoUpUrl),
                      SVNRevision.create(SvnUtils.LATEST_REVERSION),
                      file, SVNDepth.INFINITY);
                  moduleManage.setShPath(sb.toString().replace(storgePrefix, ""));
                  moduleManage.setSvnAutoUrl(svnAutoUpUrl);
                  moduleManage.setModuleType(
                      ModuleTypeEnum.SVN_AUTO_UP_CODE.getModuleTypeCode());
                }
              }
            }
          } else {
            return 0;
          }
          break;
        default:
          break;
      }
    }
    return manageMapper.updateByPrimaryKeySelective(moduleManage);
  }
  
  /**
   * 批量导入模块和相关子模块信息
   *
   * @param session
   * @param userId
   * @param paramVo
   * @param moduleEnv
   * @param certificate
   * @param projectCode
   * @param fileName
   * @return
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  public boolean saveAll(Session session, String userId, ModuleManageParamVo paramVo, ModuleEnv moduleEnv
      , ModuleCertificate certificate, String projectCode, String fileName) {
    if (moduleEnv != null && certificate != null && paramVo != null
        && session != null && StringUtils.isNotBlank(userId)
        && StringUtils.isNotBlank(projectCode)
        && StringUtils.isNotBlank(fileName)) {
      
      ModuleCenterExample centerExample = new ModuleCenterExample();
      centerExample.createCriteria().andIsDeletedEqualTo(0).andEnvIdEqualTo(moduleEnv.getId())
          .andChildCenterNameEqualTo(paramVo.getCenterName());
      List<ModuleCenter> centerList = centerMapper.selectByExample(centerExample);
      ModuleCenter center = null;
      if (centerList != null && centerList.size() > 0) {
        center = centerList.get(0);
      } else {
        session.sendText(JSONObject.toJSONString(UploadModuleStatusDTO.fail(fileName,
            ("无法查询到该中心信息: " + paramVo.getCenterName()))));
        return false;
      }
      ModuleManage moduleManage = new ModuleManage();
      moduleManage.setChargePerson(paramVo.getChargePerson());
      moduleManage.setModuleName(paramVo.getModuleName());
      moduleManage.setEnvId(moduleEnv.getId());
      moduleManage.setMark(moduleEnv.getEnvName());
      moduleManage.setEnvCode(moduleEnv.getEnvCode());
      moduleManage.setCenterId(center.getId());
      moduleManage.setCertificateId(certificate.getId());
      
      moduleManage.setModuleType(paramVo.getModuleTypeCode());
      
      moduleManage.setUserId(userId);
      moduleManage.setChargeTelephone(paramVo.getChargeTelephone());
      moduleManage.setOfficalChargePerson(paramVo.getOfficalChargePerson());
      moduleManage.setOfficalChargeTelephone(paramVo.getOfficalChargeTelephone());
      
      String contentName = paramVo.getModuleContentName();
      StringBuilder filePrefix = new StringBuilder();
      filePrefix.append(storgePrefix).append(center.getCenterPath()).append(SEP)
          .append(contentName).append(SEP);
      ModuleManageExample manageExample = new ModuleManageExample();
      manageExample.createCriteria()
          .andIsDeleteEqualTo(0)
          .andEnvIdEqualTo(moduleEnv.getId())
          .andCenterIdEqualTo(center.getId())
          .andModuleContentNameEqualTo(paramVo.getModuleContentName());
      
      List<ModuleManage> manageList = manageMapper.selectByExample(manageExample);
      if (manageList != null && manageList.size() > 0) {
        log.error("模块目录添加重复: {}", paramVo.getModuleContentName());
        session.sendText(JSONObject.toJSONString(UploadModuleStatusDTO.fail(fileName,
            ("模块目录添加重复: " + paramVo.getModuleContentName()))));
        return false;
      }
      File filePre = new File(filePrefix.toString());
      if (!FoldUtils.judgeContent(contentName) && !filePre.exists() && !filePre.mkdirs()) {
        log.error("创建文件夹失败");
        session.sendText(JSONObject.toJSONString(UploadModuleStatusDTO.fail(fileName,
            ("创建文件夹失败" + contentName))));
        return false;
      }
      moduleManage.setModuleContentName(contentName);
      moduleManage.setModuleProjectCode(projectCode);
      moduleManage.setCreateTime(new Date());
      moduleManage.setUpdateTime(new Date());
      manageMapper.insertSelective(moduleManage);
      
      List<ModulePackage> packageList = new ArrayList<>();
      List<ModulePackageParamVo> paramVoPackageList = paramVo.getPackageList();
      for (int i = 0; i < paramVoPackageList.size(); i++) {
        session.sendText(JSONObject.toJSONString(UploadModuleStatusDTO.progress(fileName,
            i + 1, paramVoPackageList.size())));
        ModulePackageParamVo packageVo = paramVoPackageList.get(i);
        ModulePackage modulePackage = new ModulePackage();
        Integer moduleTypeCode = paramVo.getModuleTypeCode();
        ModuleTypeEnum typeEnum = ModuleTypeEnum.getTypeByCode(moduleTypeCode);
        
        if (typeEnum != null) {
          switch (typeEnum) {
            case SVN_SOURCE_CODE:
              try {
                if (!SvnUtils.isURLExist(packageVo.getCodeUrl(),
                    certificate.getUsername(),
                    EncryptUtil.decrypt(certificate.getPassword()))) {
                  session.sendText(JSONObject.toJSONString(
                      UploadModuleStatusDTO.fail(fileName,
                          "svnUrl不存在异常: " + packageVo.getCodeUrl())));
                  FoldUtils.deleteFolders(filePrefix.toString());
                  throw new SvnUrlNotExistException("svnUrl不存在异常: "
                      + packageVo.getCodeUrl());
                }
              } catch (SVNException e) {
                session.sendText(JSONObject.toJSONString(UploadModuleStatusDTO.fail(fileName,
                    "svnUrl不存在异常: " + packageVo.getCodeUrl())));
                FoldUtils.deleteFolders(filePrefix.toString());
                throw new SvnUrlNotExistException("svnUrl不存在异常: " + packageVo.getCodeUrl());
              }
              break;
            case GIT_SOURCE_CODE:
              try {
                String urlReversion =
                    GitUtils.getUrlReversion(packageVo.getCodeUrl(),
                        certificate.getUsername(),
                        EncryptUtil.decrypt(certificate.getPassword()));
                log.info("git reversion 为: {}", urlReversion);
              } catch (GitAPIException e) {
                session.sendText(JSONObject.toJSONString(UploadModuleStatusDTO.fail(fileName,
                    "gitUrl不存在异常: " + packageVo.getCodeUrl())));
                FoldUtils.deleteFolders(filePrefix.toString());
                throw new SvnUrlNotExistException("gitUrl不存在异常: " + packageVo.getCodeUrl());
              }
              modulePackage.setGitBranch(paramVo.getGitBranch());
              break;
            default:
              break;
          }
        }
        modulePackage.setCodeReversion(String.valueOf(SvnUtils.LATEST_REVERSION));
        modulePackage.setCreateTime(new Date());
        modulePackage.setCodeType(moduleTypeCode);
        modulePackage.setContentName(packageVo.getContentName());
        modulePackage.setCodeUrl(packageVo.getCodeUrl());
        modulePackage.setModuleId(moduleManage.getId());
        modulePackage.setPackagePathName((filePrefix + packageVo.getContentName()).
            replace(storgePrefix, ""));
        packageList.add(modulePackage);
      }
      
      paramVoPackageList.clear();
      packageService.insertAll(packageList);
      syncService.checkoutAllAsync(packageList, certificate);
      return true;
    }
    return false;
  }
  
  @Override
  public List<ModuleManage> selectByIds(Integer[] moduleIds) {
    return manageMapper.selectByIds(moduleIds);
    
  }
  
  @Override
  public String getFilePath(Integer moduleId, Integer type) {
    ModuleManageDTO moduleManageDTO = manageMapper.selectInfoById(moduleId);
    FoldDataVo dataVo = new FoldDataVo();
    if (moduleManageDTO != null) {
      StringBuilder sb = new StringBuilder();
      sb.append(storgePrefix).append(moduleManageDTO.getCenterPath())
          .append(SEP).append(moduleManageDTO.getModuleContentName());
      File file = new File(sb.toString());
      if (!file.exists()) {
        return null;
      }
      
      if (type == 1) {
        FoldUtils.getFilePath(file, storgePrefix,
            moduleManageDTO.getModuleContentName(), "Dockerfile", dataVo);
      } else if (type == 2) {
        FoldUtils.getFilePath(file, storgePrefix,
            moduleManageDTO.getModuleContentName(), "k8s", dataVo);
      }
    }
    return dataVo.getAbsolutePath();
  }
  
  /**
   * 查询中心下的模块 并且未在任务配置页面添加过的
   *
   * @param centerId
   * @return
   */
  @Override
  public List<ModuleManage> selectAllNotInJobById(Integer centerId) {
    List<ModuleManage> manageList = selectAllByCenterId(centerId);
    List<ModuleManage> returnManageList = new ArrayList<>();
    if (manageList != null && manageList.size() > 0) {
      List<ModuleManage> manageJobList = manageMapper.selectInfoInJobByCenterId(centerId);
      Set<Integer> manageIds = new HashSet<>();
      if (manageJobList != null && manageJobList.size() > 0) {
        for (ModuleManage moduleManage : manageJobList) {
          manageIds.add(moduleManage.getId());
        }
      }
      if (manageIds.size() > 0) {
        for (ModuleManage moduleManage : manageList) {
          if (!manageIds.contains(moduleManage.getId())) {
            returnManageList.add(moduleManage);
          }
        }
        return returnManageList;
      }
    }
    
    return manageList;
  }
  
  /**
   * 批量添加自动更新的模块信息
   *
   * @param session
   * @param moduleEnv
   * @param manageParamVos
   * @param userId
   * @param certificate
   * @param projectCode
   * @param fileName
   * @return
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  public boolean saveAllAutoManageData(Session session, ModuleEnv moduleEnv, List<ModuleManageParamVo> manageParamVos,
                                       String userId, ModuleCertificate certificate,
                                       String projectCode, String fileName) {
    if (moduleEnv != null && certificate != null && manageParamVos != null
        && manageParamVos.size() > 0 && session != null
        && StringUtils.isNotBlank(userId) && StringUtils.isNotBlank(projectCode)
        && StringUtils.isNotBlank(fileName)) {
      Set<String> centerNameSet = new HashSet<>();
      for (ModuleManageParamVo paramVo : manageParamVos) {
        centerNameSet.add(paramVo.getCenterName());
      }
      Map<String, ModuleCenter> centerMap = new HashMap<>();
      for (String centerName : centerNameSet) {
        ModuleCenterExample centerExample = new ModuleCenterExample();
        ModuleCenterExample.Criteria criteria = centerExample.createCriteria();
        criteria.andIsDeletedEqualTo(0).andChildCenterNameEqualTo(centerName)
            .andEnvIdEqualTo(moduleEnv.getId());
        List<ModuleCenter> centerList = centerMapper.selectByExample(centerExample);
        if (centerList == null || centerList.size() <= 0) {
          session.sendText(JSONObject.toJSONString(UploadModuleStatusDTO.fail(fileName,
              "中心名称无法完全匹配: " + JSONObject.toJSONString(centerNameSet))));
          return false;
        }
        centerMap.put(centerName, centerList.get(0));
      }
      SVNClientManager clientManager = SvnUtils.getManager(certificate.getUsername(),
          EncryptUtil.decrypt(certificate.getPassword()));
      //遍历所有模块信息 根据信息添加模块操作
      for (int i = 0; i < manageParamVos.size(); i++) {
//                session.sendText(JSONObject.toJSONString(UploadModuleStatusDTO.progress(fileName,
//                        i + 1, manageParamVos.size())));
        ModuleManageParamVo manageParamVo = manageParamVos.get(i);
        ModuleCenter moduleCenter = centerMap.get(manageParamVo.getCenterName());
        String svnAutoUpUrl = manageParamVo.getSvnAutoUpUrl();
        String[] split = svnAutoUpUrl.split(SEP);
        StringBuffer modulePrefix = new StringBuffer();
        modulePrefix.append(storgePrefix).append(moduleCenter.getCenterPath()).
            append(SEP).append(manageParamVo.getModuleContentName()).
            append(SEP);
        StringBuffer sshFile = new StringBuffer(modulePrefix);
        sshFile.append("ssh").append(SEP).append(split[split.length - 1]);
        File file = new File(sshFile.toString());
        try {
          SvnUtils.doExport(clientManager, SVNURL.parseURIEncoded(manageParamVo.getSvnAutoUpUrl()),
              SVNRevision.create(SvnUtils.LATEST_REVERSION), file, SVNDepth.INFINITY);
        } catch (SVNException e) {
          session.sendText(JSONObject.toJSONString(UploadModuleStatusDTO.failPackage(fileName,
              "svn拉取脚本失败: " + e.getMessage(), manageParamVo.getModuleName(),
              "svn拉取脚本失败: " + e.getMessage())));
//                    session.sendText(JSONObject.toJSONString(UploadModuleStatusDTO.fail(fileName,
//                            "svn拉取脚本失败: " + e.getMessage())));
          FoldUtils.deleteFolders(modulePrefix.toString());
          //失败后 该模块相关解析跳过
          continue;
        }
        
        //判断模块名是否重复添加 同一环境的同一中心不允许相同的模块名
        ModuleManageExample manageExample = new ModuleManageExample();
        ModuleManageExample.Criteria criteria = manageExample.createCriteria();
        criteria.andEnvIdEqualTo(moduleEnv.getId()).andIsDeleteEqualTo(0)
            .andCenterIdEqualTo(moduleCenter.getId()).andModuleNameEqualTo(manageParamVo.getModuleName());
        
        //该需要判断模块目录名也不能重复
        ModuleManageExample manageExample2 = new ModuleManageExample();
        ModuleManageExample.Criteria criteria2 = manageExample2.createCriteria();
        criteria2.andEnvIdEqualTo(moduleEnv.getId()).andIsDeleteEqualTo(0)
            .andCenterIdEqualTo(moduleCenter.getId())
            .andModuleContentNameEqualTo(manageParamVo.getModuleContentName());
        
        List<ModuleManage> manageList = manageMapper.selectByExample(manageExample);
        List<ModuleManage> manageList2 = manageMapper.selectByExample(manageExample2);
        if (manageList.size() > 0) {
          session.sendText(JSONObject.toJSONString(UploadModuleStatusDTO.failPackage(fileName,
              "模块名称重复: " + manageParamVo.getModuleName(), manageParamVo.getModuleName(),
              "模块名称重复: " + manageParamVo.getModuleName())));
//                    session.sendText(JSONObject.toJSONString(UploadModuleStatusDTO.fail(fileName,
//                            "模块名称重复: " + manageParamVo.getModuleName())));
          FoldUtils.deleteFolders(modulePrefix.toString());
          //跳过该模块的解析
          continue;
        }
        
        if (manageList2.size() > 0) {
          session.sendText(JSONObject.toJSONString(UploadModuleStatusDTO.failPackage(fileName,
              "模块目录名称重复: " + manageParamVo.getModuleName(), manageParamVo.getModuleName(),
              "模块目录名称重复: " + manageParamVo.getModuleName())));
//                    session.sendText(JSONObject.toJSONString(UploadModuleStatusDTO.fail(fileName,
//                            "模块名称重复: " + manageParamVo.getModuleName())));
          FoldUtils.deleteFolders(modulePrefix.toString());
          //跳过该模块的解析
          continue;
        }
        
        //将模块信息补全 插入到数据库
        ModuleManage moduleManage = new ModuleManage();
        moduleManage.setChargePerson(manageParamVo.getChargePerson());
        moduleManage.setModuleName(manageParamVo.getModuleName());
        moduleManage.setEnvId(moduleEnv.getId());
        moduleManage.setMark(moduleEnv.getEnvName());
        moduleManage.setEnvCode(moduleEnv.getEnvCode());
        moduleManage.setCenterId(moduleCenter.getId());
        moduleManage.setCertificateId(certificate.getId());
        moduleManage.setModuleType(ModuleTypeEnum.SVN_AUTO_UP_CODE.getModuleTypeCode());
        moduleManage.setUserId(userId);
        moduleManage.setChargeTelephone(manageParamVo.getChargeTelephone());
        moduleManage.setOfficalChargePerson(manageParamVo.getOfficalChargePerson());
        moduleManage.setOfficalChargeTelephone(manageParamVo.getOfficalChargeTelephone());
        moduleManage.setShPath(sshFile.toString().replace(storgePrefix, ""));
        moduleManage.setSvnAutoUrl(manageParamVo.getSvnAutoUpUrl());
        moduleManage.setModuleContentName(manageParamVo.getModuleContentName());
        moduleManage.setModuleProjectCode(projectCode);
        moduleManage.setCreateTime(new Date());
        moduleManage.setUpdateTime(new Date());
        manageMapper.insertSelective(moduleManage);
        //然后插入对应的子模块信息
        try {
          List<ModulePackageParamVo> paramVos = ExcelPhraseUtils.getAllShData(new FileInputStream(file));
          if (paramVos == null || paramVos.size() <= 0) {
            session.sendText(JSONObject.toJSONString(UploadModuleStatusDTO.failPackage(fileName,
                "解析脚本文件失败: " + file.getName(), manageParamVo.getModuleName(),
                "解析脚本文件失败: " + file.getName())));
//                        session.sendText(JSONObject.toJSONString(UploadModuleStatusDTO.fail(fileName,
//                                "解析脚本文件失败: " + file.getName())));
            FoldUtils.deleteFolders(modulePrefix.toString());
            break;
          }
          List<ModulePackage> packageList = new ArrayList<>();
          boolean flag = false;
          for (int j = 0; j < paramVos.size(); j++) {
            ModulePackageParamVo packageParamVo = paramVos.get(j);
            ModulePackage modulePackage = new ModulePackage();
            session.sendText(JSONObject.toJSONString(UploadModuleStatusDTO.progressPackage(fileName,
                i + 1, manageParamVos.size(), moduleManage.getModuleName(),
                j + 1, paramVos.size(), packageParamVo.getCodeUrl())));
            try {
              if (!SvnUtils.isURLExist(packageParamVo.getCodeUrl(), certificate.getUsername(),
                  EncryptUtil.decrypt(certificate.getPassword()))) {
                session.sendText(JSONObject.toJSONString(UploadModuleStatusDTO.failPackage(fileName,
                    "svnUrl 验证不通过: " + packageParamVo.getCodeUrl(), manageParamVo.getModuleName(),
                    "svnUrl 验证不通过: " + packageParamVo.getCodeUrl())));
                log.info("svnUrl 验证不通过:{}", packageParamVo.getCodeUrl());
                FoldUtils.deleteFolders(modulePrefix.toString());
                flag = true;
                break;
              }
            } catch (SVNException e) {
              session.sendText(JSONObject.toJSONString(UploadModuleStatusDTO.failPackage(fileName,
                  "svnUrl 验证不通过: " + packageParamVo.getCodeUrl(), manageParamVo.getModuleName(),
                  "svnUrl 验证不通过: " + packageParamVo.getCodeUrl())));
              FoldUtils.deleteFolders(modulePrefix.toString());
              flag = true;
              break;
            }
            modulePackage.setCodeReversion(String.valueOf(SvnUtils.LATEST_REVERSION));
            modulePackage.setCreateTime(new Date());
            modulePackage.setCodeType(ModuleTypeEnum.SVN_AUTO_UP_CODE.getModuleTypeCode());
            modulePackage.setContentName(packageParamVo.getContentName());
            modulePackage.setCodeUrl(packageParamVo.getCodeUrl());
            modulePackage.setModuleId(moduleManage.getId());
            modulePackage.setPackagePathName((modulePrefix + packageParamVo.getContentName()).
                replace(storgePrefix, ""));
            packageList.add(modulePackage);
          }
          //解析出问题后 跳过本次模块的循环
          if (flag) {
            packageList.clear();
            continue;
          }
          //没出问题的继续
          paramVos.clear();
          packageService.insertAll(packageList);
          //异步拉取代码的步骤
          syncService.checkoutAllAsync(packageList, certificate);
          session.sendText(JSONObject.toJSONString(UploadModuleStatusDTO.successPackage(fileName,
              "该模块存入成功", manageParamVo.getModuleName(), "模块存入成功")));
        } catch (FileNotFoundException e) {
          e.printStackTrace();
        }
      }
      //执行完对应的之后
      manageParamVos.clear();
      return true;
    }
    return false;
  }
  
  /**
   * 平移模块从一个环境到另外一个环境
   *
   * @param moduleIds
   * @param srcEnvId
   * @param desEnvId
   * @param hProjectCode
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  public synchronized Map<String, String> copyModule2OtherEnv(List<Integer> moduleIds, Integer srcEnvId,
                                                              Integer desEnvId, String userId, String hProjectCode) {
    ModuleEnv srcEnv = envMapper.selectByPrimaryKey(srcEnvId);
    ModuleEnv desEnv = envMapper.selectByPrimaryKey(desEnvId);
    Map<String, String> resultMap = new HashMap<>();
    Map<String, Integer> centerNameMap = new HashMap<>();
    if (srcEnv != null && desEnv != null && moduleIds != null) {
      //遍历模块 验证所有的模块对应的数据是否存在
      for (Integer moduleId : moduleIds) {
        ModuleManageDTO moduleManageDTO = manageMapper.selectInfoById(moduleId);
        boolean flag = false;
        //首先保证该模块是存在的
        if (moduleManageDTO != null) {
          //没查过的
          ModuleCenterExample centerExample = new ModuleCenterExample();
          ModuleCenterExample.Criteria criteria = centerExample.createCriteria();
          criteria.andEnvIdEqualTo(desEnvId).andIsDeletedEqualTo(0)
              .andChildCenterNameEqualTo(moduleManageDTO.getChildCenterName());
          List<ModuleCenter> centerList = centerMapper.selectByExample(centerExample);
          ModuleCenter moduleCenter = centerMapper.selectByPrimaryKey(moduleManageDTO.getCenterId());
          String srcCenterPath = moduleCenter.getCenterPath();
          
          if (centerList == null || centerList.size() <= 0) {
            //这样肯定是新的中心需要添加  那对应的也是新的模块
            //添加新的中心
            //先查询原有的中心数据
            moduleCenter.setEnvId(desEnvId);
            moduleCenter.setUpdateTime(new Date());
            moduleCenter.setCreateTime(new Date());
            moduleCenter.setId(null);
            StringBuilder descCenterPrefix = new StringBuilder();
            descCenterPrefix.append(storgePrefix).append(desEnv.getEnvCode())
                .append(desEnv.getId()).append(FoldUtils.SEP)
                .append(moduleCenter.getChildCenterContentName());
            String descCenterPath = descCenterPrefix.toString();
            File file = new File(descCenterPath);
            if (!file.exists()) {
              boolean mkdirs = file.mkdirs();
            }
            moduleCenter.setCenterPath(descCenterPrefix.toString()
                .replace(storgePrefix, ""));
            centerMapper.insertSelective(moduleCenter);
            centerNameMap.put(moduleManageDTO.getChildCenterName(),
                moduleCenter.getId());
            //复制模块的操作
            //数据库的数据copy
            ModuleManage newModuleManage = copyModuleData(srcEnv, desEnv,
                moduleId, userId, moduleCenter.getId(),
                srcCenterPath, descCenterPath, hProjectCode);
            //然后迁移对应的配置的job 和 deploy 关系
//                        copyModuleJobData(newModuleManage, moduleId, srcEnv, desEnv);
//                        //再然后迁移对应的配置的deploy的相关数据
//                        copyModuleDeployData(newModuleManage, moduleId, srcEnv, desEnv);
            flag = true;
            resultMap.put(moduleManageDTO.getModuleName(), "迁移成功");
            //ok
          } else {
            if (!moduleManageDTO.getChildCenterContentName()
                .equals(moduleCenter.getChildCenterContentName())) {
              //如果中心目录不一致则跳过
              resultMap.put(moduleManageDTO.getModuleName(), "源centerCode: " +
                  moduleManageDTO.getChildCenterContentName() + " 目标centerCode: " +
                  moduleCenter.getChildCenterContentName() + " 不一致 迁移失败");
              continue;
            }
            //否则的话需要在已有的中心里面匹配模块名是否重复
            if (!centerNameMap.containsKey(moduleManageDTO.getChildCenterName())) {
              centerNameMap.put(moduleManageDTO.getChildCenterName(), centerList.get(0).getId());
            }
            
            ModuleManage moduleManage = new ModuleManage();
            moduleManage.setEnvId(desEnv.getId());
            moduleManage.setModuleName(moduleManageDTO.getModuleName());
            moduleManage.setModuleContentName(moduleManageDTO.getModuleContentName());
            moduleManage.setCenterId(centerNameMap.get(moduleManageDTO.getChildCenterName()));
            
            Integer count = manageMapper.selectModuleNameOrContentExist(moduleManage);
            
            if (count <= 0) {
              //继续操作复制模块的操作
              ModuleManage newModuleManage = copyModuleData(srcEnv, desEnv, moduleId, userId,
                  centerNameMap.get(moduleManageDTO.getChildCenterName()),
                  srcCenterPath, storgePrefix +
                      centerList.get(0).getCenterPath(), hProjectCode);
              //取消掉相关操作
//                            //然后迁移对应的配置的job 和 deploy 关系
//                            copyModuleJobData(newModuleManage, moduleId, srcEnv, desEnv);
//                            //再然后迁移对应的配置的deploy的相关数据
//                            copyModuleDeployData(newModuleManage, moduleId, srcEnv, desEnv);
              flag = true;
              resultMap.put(moduleManageDTO.getModuleName(), "迁移成功");
              //true
            } else {
              flag = true;
              resultMap.put(moduleManageDTO.getModuleName(), "该模块或该模块的目录在中心下已存在");
            }
          }
        }
        if (!flag) {
          resultMap.put("模块迁移", "迁移失败");
        }
      }
    }
    return resultMap;
  }
  
  /**
   * 统计某个环境配置的模块信息形成文件保存
   *
   * @param envId
   * @return
   */
  @Override
  public File genManageFile(Integer envId) {
    ModuleEnv moduleEnv = envMapper.selectOne(envId);
    
    if (moduleEnv != null) {
//            CoreV1Api coreV1Api = K8sManagement.getCoreV1Api(moduleEnv.getK8sConfig());
      AppsV1Api appsV1Api = K8sManagement.getAppsV1Api(moduleEnv.getK8sConfig());
      Workbook wb0 = new XSSFWorkbook();
      CellStyle cellStyle = wb0.createCellStyle();
      cellStyle.setAlignment(HorizontalAlignment.CENTER);
      cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
      cellStyle.setBorderBottom(BorderStyle.THIN);
      cellStyle.setBorderLeft(BorderStyle.THIN);
      cellStyle.setBorderRight(BorderStyle.THIN);
      
      List<ModuleManageDeployVO> deployVOS = manageMapper.selectAllModuleByEnvId(envId);
      Sheet sheet = wb0.createSheet(deployVOS.get(0).getEnvName());
      
      Row row = sheet.createRow(0);
      Cell cell0 = row.createCell(0);
      cell0.setCellValue("序号");
      cell0.setCellStyle(cellStyle);
      
      Cell cell1 = row.createCell(1);
      cell1.setCellValue("中心名称");
      cell1.setCellStyle(cellStyle);
      
      Cell cell2 = row.createCell(2);
      cell2.setCellValue("子中心名称");
      cell2.setCellStyle(cellStyle);
      
      Cell cell3 = row.createCell(3);
      cell3.setCellValue("模块名称");
      cell3.setCellStyle(cellStyle);
      
      Cell cell4 = row.createCell(4);
      cell4.setCellValue("pod数目");
      cell4.setCellStyle(cellStyle);
      
      Cell cell5 = row.createCell(5);
      cell5.setCellValue("pod前缀");
      cell5.setCellStyle(cellStyle);
      
      Cell cell6 = row.createCell(6);
      cell6.setCellValue("负责人");
      cell6.setCellStyle(cellStyle);
      
      Cell cell7 = row.createCell(7);
      cell7.setCellValue("局方负责人");
      cell7.setCellStyle(cellStyle);
      
      Cell cell8 = row.createCell(8);
      cell8.setCellValue("业务备注");
      cell8.setCellStyle(cellStyle);
      
      Cell cell9 = row.createCell(9);
      cell9.setCellValue("备注");
      cell9.setCellStyle(cellStyle);
      
      int count = 1;
      
      for (int i = 0; i < deployVOS.size(); i++) {
        Row rowi = sheet.createRow(i + 1);
        Cell celli0 = rowi.createCell(0);
        celli0.setCellValue(count++);
        celli0.setCellStyle(cellStyle);
        
        Cell celli1 = rowi.createCell(1);
        celli1.setCellValue(deployVOS.get(i).getCenterName());
        celli1.setCellStyle(cellStyle);
        
        Cell celli2 = rowi.createCell(2);
        celli2.setCellValue(deployVOS.get(i).getChildCenterName());
        celli2.setCellStyle(cellStyle);
        
        Cell celli3 = rowi.createCell(3);
        celli3.setCellValue(deployVOS.get(i).getModuleName());
        celli3.setCellStyle(cellStyle);
        
        Cell celli4 = rowi.createCell(4);
        
        int podNum = 0;
        try {
          V1Deployment deployment = appsV1Api
              .readNamespacedDeployment(deployVOS.get(i)
                      .getYamlName(), K8sNameSpace.DEFAULT,
                  null);
          if (deployment != null && deployment.getStatus() != null) {
            podNum = deployment.getStatus().getReplicas();
          }
        } catch (ApiException e) {
          e.printStackTrace();
        }
        celli4.setCellValue(podNum);
        celli4.setCellStyle(cellStyle);
        
        Cell celli5 = rowi.createCell(5);
        celli5.setCellValue(deployVOS.get(i).getYamlName());
        celli5.setCellStyle(cellStyle);
        
        Cell celli6 = rowi.createCell(6);
        String chargePerson = deployVOS.get(i).getChargePerson();
        String chargeTelephone = deployVOS.get(i).getChargeTelephone();
        if (StringUtils.isBlank(chargePerson)) {
          chargePerson = "";
        }
        if (StringUtils.isBlank(chargeTelephone)) {
          chargeTelephone = "";
        }
        
        celli6.setCellValue(chargePerson + chargeTelephone);
        celli6.setCellStyle(cellStyle);
        
        Cell celli7 = rowi.createCell(7);
        String officalChargePerson = deployVOS.get(i).getOfficalChargePerson();
        String officalChargeTelephone = deployVOS.get(i).getOfficalChargeTelephone();
        if (StringUtils.isBlank(officalChargePerson)) {
          officalChargePerson = "";
        }
        if (StringUtils.isBlank(officalChargeTelephone)) {
          officalChargeTelephone = "";
        }
        celli7.setCellValue(officalChargePerson + officalChargeTelephone);
        celli7.setCellStyle(cellStyle);
        
        Cell celli8 = rowi.createCell(8);
        celli8.setCellValue(deployVOS.get(i).getYwRemark());
        celli8.setCellStyle(cellStyle);
        
        Cell celli9 = rowi.createCell(9);
        celli9.setCellValue(deployVOS.get(i).getRemark());
        celli9.setCellStyle(cellStyle);
      }
      
      //设置自适应样式
      setSizeColumn(sheet, 9);
      
      StringBuilder fileNameSb = new StringBuilder();
      fileNameSb.append(moduleExportPath).append("module_file")
          .append(DateUtils.generateDateOnlyString()).append(".xlsx");
      File file = new File(fileNameSb.toString());
      if (!file.exists()) {
        try {
          boolean b = file.createNewFile();
          FileOutputStream fos = new FileOutputStream(file);
          wb0.write(fos);
          fos.close();
          log.info("生成excel文件成功: {}", file.getName());
          return file;
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
    return null;
  }
  
  /**
   * 设置sheet的列宽为自适应
   *
   * @param sheet
   * @param size
   */
  public void setSizeColumn(Sheet sheet, int size) {
    for (int columnNum = 0; columnNum < size; columnNum++) {
      int columnWidth = sheet.getColumnWidth(columnNum) / 256;
      for (int rowNum = 0; rowNum < sheet.getLastRowNum(); rowNum++) {
        Row currentRow;
        //当前行未被使用过
        if (sheet.getRow(rowNum) == null) {
          currentRow = sheet.createRow(rowNum);
        } else {
          currentRow = sheet.getRow(rowNum);
        }
        
        if (currentRow.getCell(columnNum) != null) {
          Cell currentCell = currentRow.getCell(columnNum);
          if (currentCell.getCellType() == XSSFCell.CELL_TYPE_STRING) {
            int length = currentCell.getStringCellValue().getBytes().length;
            if (columnWidth < length) {
              columnWidth = length;
            }
          }
        }
      }
      sheet.setColumnWidth(columnNum, columnWidth * 256);
    }
  }
  
  /**
   * 迁移发布关系指定到新的环境中
   *
   * @param newModuleManage
   * @param moduleId
   * @param srcEnv
   * @param desEnv
   */
  private void copyModuleDeployData(ModuleManage newModuleManage, Integer moduleId, ModuleEnv srcEnv, ModuleEnv desEnv) {
    ModuleDeployExample deployExample = new ModuleDeployExample();
    deployExample.createCriteria().andEnvIdEqualTo(srcEnv.getId()).andIsDeleteEqualTo(0).andModuleIdEqualTo(moduleId);
    List<ModuleDeploy> deployList = deployMapper.selectByExample(deployExample);
    if (deployList != null && deployList.size() > 0) {
      ModuleDeploy moduleDeploy = deployList.get(0);
      Integer deployId = moduleDeploy.getId();
      moduleDeploy.setId(null);
      moduleDeploy.setModuleId(newModuleManage.getId());
      moduleDeploy.setIsDeployed(0);
      moduleDeploy.setIsDelete(0);
      moduleDeploy.setCreateTime(new Date());
      moduleDeploy.setUpdateTime(new Date());
      moduleDeploy.setEnvId(desEnv.getId());
      moduleDeploy.setGenernateName(null);
      moduleDeploy.setLastDeployTime(null);
      moduleDeploy.setDeployStatus(null);
      //存入deploy数据
      deployMapper.insertSelective(moduleDeploy);
      ModuleDeployYamlExample yamlExample = new ModuleDeployYamlExample();
      yamlExample.createCriteria().andDeployIdEqualTo(deployId);
      List<ModuleDeployYaml> yamlList = deployYamlMapper.selectByExample(yamlExample);
      if (yamlList != null && yamlList.size() > 0) {
        for (ModuleDeployYaml deployYaml : yamlList) {
          deployYaml.setId(null);
          deployYaml.setIsDeployed(0);
          deployYaml.setCreateTime(new Date());
          deployYaml.setUpdateTime(new Date());
          deployYaml.setDeployId(moduleDeploy.getId());
          deployYaml.setYamlPath(deployYaml.getYamlPath().replace(srcEnv.getEnvCode() + srcEnv.getId(),
              desEnv.getEnvCode() + desEnv.getId()));
          //存入deployYaml文件数据
          deployYamlMapper.insertSelective(deployYaml);
        }
      }
    }
  }
  
  /**
   * 迁移job到新的环境中
   *
   * @param newModuleManage
   * @param srcModuleId
   * @param srcEnv
   * @param desEnv
   */
  private void copyModuleJobData(ModuleManage newModuleManage, Integer srcModuleId, ModuleEnv srcEnv, ModuleEnv desEnv) {
    ModuleJobExample jobExample = new ModuleJobExample();
    jobExample.createCriteria().andModuleIdEqualTo(srcModuleId).andModuleEnvIdEqualTo(srcEnv.getId()).andIsDeleteEqualTo(0);
    List<ModuleJob> jobList = jobMapper.selectByExample(jobExample);
    if (jobList != null && jobList.size() > 0) {
      ModuleJob moduleJob = jobList.get(0);
      moduleJob.setId(null);
      moduleJob.setModuleId(newModuleManage.getId());
      moduleJob.setModuleEnvId(desEnv.getId());
      if (StringUtils.isNotBlank(moduleJob.getJobName())) {
        moduleJob.setJobName(moduleJob.getJobName().replace(srcEnv.getEnvCode(), desEnv.getEnvCode()));
      }
      if (StringUtils.isNotBlank(moduleJob.getMirrorPrefix())) {
        StringBuilder sb = new StringBuilder();
        sb.append(desEnv.getHarborUrl()).append(SEP).append(newModuleManage.getModuleProjectCode())
            .append(SEP).append(newModuleManage.getModuleContentName());
//                String replace = moduleJob.getMirrorPrefix().replace(srcEnv.getHarborUrl(), desEnv.getHarborUrl());
        moduleJob.setMirrorPrefix(sb.toString());
      }
      if (StringUtils.isNotBlank(moduleJob.getDockerfilePath())) {
        moduleJob.setDockerfilePath(moduleJob.getDockerfilePath().replace(srcEnv.getEnvCode() + srcEnv.getId(),
            desEnv.getEnvCode() + desEnv.getId()));
      }
      if (StringUtils.isNotBlank(moduleJob.getCompileFilePath())) {
        moduleJob.setCompileFilePath(moduleJob.getCompileFilePath().replace(srcEnv.getEnvCode() + srcEnv.getId(),
            desEnv.getEnvCode() + desEnv.getId()));
        
      }
      
      //环境的切换使得profile也会有变化的
      String compileCommand = moduleJob.getCompileCommand();
      if (StringUtils.isNotBlank(compileCommand)) {
        String[] split = compileCommand.trim().split("\\s+");
        if (split.length > 0) {
          for (String key : split) {
            if (key.startsWith("-P")) {
              moduleJob.setCompileCommand(compileCommand.replace(key, "-P" + desEnv.getMvnProfile()));
              break;
            }
          }
        }
      }
      moduleJob.setCreateTime(new Date());
      moduleJob.setUpdateTime(new Date());
      //存入数据库
      jobMapper.insertSelective(moduleJob);
      //连接jenkins 创建job
      jenkinsManage.createRealJob(moduleJob, ModuleTypeEnum.getTypeByCode(newModuleManage.getModuleType()),
          ComplieTypeEnum.COMMAND_COMPILIE, desEnv.getEnvCode());
      
    }
    
  }
  
  /**
   * 迁移模块数据 即模块对应的文件
   *
   * @param srcEnv
   * @param desEnv
   * @param moduleId
   * @param userId
   * @param centerId
   * @param srcCenterPath
   * @param descCenterPrefix
   */
  private ModuleManage copyModuleData(ModuleEnv srcEnv, ModuleEnv desEnv,
                                      Integer moduleId, String userId, Integer centerId,
                                      String srcCenterPath, String descCenterPrefix, String hProjcetCode) {
    ModuleManage moduleManage = manageMapper.selectByPrimaryKey(moduleId);
    moduleManage.setEnvId(desEnv.getId());
    moduleManage.setUserId(userId);
    moduleManage.setEnvCode(desEnv.getEnvCode());
    moduleManage.setMark(desEnv.getEnvName());
    moduleManage.setId(null);
    if (StringUtils.isNotBlank(moduleManage.getShPath())) {
      moduleManage.setShPath(moduleManage.getShPath().replace(srcEnv.getEnvCode() + srcEnv.getId()
          , desEnv.getEnvCode() + desEnv.getId()));
    }
    moduleManage.setCenterId(centerId);
    moduleManage.setUpdateTime(new Date());
    moduleManage.setCreateTime(new Date());
    moduleManage.setModuleProjectCode(hProjcetCode);
    manageMapper.insertSelective(moduleManage);
    //然后copy对应的package数据
    List<ModulePackageDTO> packageDTOS = packageService.selectByModuleId(moduleId);
    List<ModulePackage> modulePackages = new ArrayList<>();
    if (packageDTOS != null && packageDTOS.size() > 0) {
      for (ModulePackageDTO packageDTO : packageDTOS) {
        ModulePackage modulePackage = new ModulePackage();
        BeanUtils.copyProperties(packageDTO, modulePackage);
        modulePackage.setCreateTime(new Date());
        modulePackage.setId(null);
        modulePackage.setModuleId(moduleManage.getId());
        if (StringUtils.isNotBlank(packageDTO.getPackagePathName())) {
          modulePackage.setPackagePathName(packageDTO.getPackagePathName().replace(
              srcEnv.getEnvCode() + srcEnv.getId(),
              desEnv.getEnvCode() + desEnv.getId()));
        }
        modulePackages.add(modulePackage);
      }
    }
    packageService.insertAll(modulePackages);
    //物理上模块文件的copy
    StringBuilder srcFilePrefix = new StringBuilder();
    srcFilePrefix.append(storgePrefix).append(srcCenterPath).append(SEP).append(moduleManage.getModuleContentName());
    StringBuilder descFilePrefix = new StringBuilder();
    descFilePrefix.append(descCenterPrefix).append(SEP).append(moduleManage.getModuleContentName());
    //从测试往生产上迁移 或 从生产往测试 不做文件复制
    try {
      log.info("迁移数据路径: srcFilePrefix: {} descFilePrefix: {}", srcFilePrefix.toString(), descFilePrefix);
      FoldUtils.copyFileDirToPath(new File(srcFilePrefix.toString()), new File(descFilePrefix.toString()));
    } catch (IOException e) {
      throw new RuntimeException("文件copy失败");
    }
    return moduleManage;
  }
  
  /**
   * 判断文件数组是否为空
   *
   * @param packageFiles
   * @return
   */
  private boolean verifyFileEmpty(MultipartFile[] packageFiles) {
    if (packageFiles != null && packageFiles.length > 0) {
      boolean flag = true;
      for (MultipartFile file : packageFiles) {
        if (file.isEmpty()) {
          flag = false;
          break;
        }
      }
      return flag;
    }
    return false;
  }
  
}
