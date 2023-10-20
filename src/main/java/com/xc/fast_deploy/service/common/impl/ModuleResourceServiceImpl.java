package com.xc.fast_deploy.service.common.impl;

import com.google.gson.Gson;
import com.xc.fast_deploy.dao.master_dao.ModuleDeploySelfConfMapper;
import com.xc.fast_deploy.dao.master_dao.ModuleEnvMapper;
import com.xc.fast_deploy.dto.ResponseDTO;
import com.xc.fast_deploy.dto.module.ModuleDeploySelfConfDTO;
import com.xc.fast_deploy.model.master_model.ModuleDeploySelfConf;
import com.xc.fast_deploy.model.master_model.ModuleEnv;
import com.xc.fast_deploy.model.master_model.example.ModuleDeploySelfConfExample;
import com.xc.fast_deploy.myException.K8SDeployException;
import com.xc.fast_deploy.myException.TransYaml2K8sVoException;
import com.xc.fast_deploy.myenum.k8sEnum.K8sKindTypeEnum;
import com.xc.fast_deploy.myenum.k8sEnum.K8sResourceStatusEnum;
import com.xc.fast_deploy.service.common.ModuleResourceService;
import com.xc.fast_deploy.service.k8s.K8sService;
import com.xc.fast_deploy.utils.DateUtils;
import com.xc.fast_deploy.utils.constant.K8sNameSpace;
import com.xc.fast_deploy.utils.k8s.K8sUtils;
import com.xc.fast_deploy.vo.K8sYamlVo;
import io.kubernetes.client.openapi.models.V1ObjectMeta;
import io.kubernetes.client.util.Yaml;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class ModuleResourceServiceImpl implements ModuleResourceService {
  
  @Autowired
  private ModuleEnvMapper envMapper;
  @Autowired
  private K8sService k8sService;
  @Autowired
  private ModuleDeploySelfConfMapper deploySelfConfMapper;
  
  @Value("${file.storge.path.deploySelfYamlPath}")
  private String yamlFilePathPrefix;
  
  /**
   * 批量添加k8s资源信息
   *
   * @param list
   * @return
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  public int insertAll(List<ModuleDeploySelfConf> list) {
    int insertNum = 0;
    if (list != null && list.size() > 0) {
      log.info("批量添加资源信息");
      Integer first = deploySelfConfMapper.selectIdFirst();
      for (int i = 0; i < list.size(); i++) {
        list.get(i).setId(first++);
      }
      deploySelfConfMapper.updateVal(first - 1);
      insertNum = deploySelfConfMapper.insertBatch(list);
    }
    return insertNum;
  }
  
  /**
   * 获取k8s资源列表
   *
   * @param envId
   * @param namespace
   * @param k8sKindType
   * @param k8sStatus   状态筛选 0 未上线 1 已上线 2 全部
   * @param keywords
   * @return
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  public List<ModuleDeploySelfConfDTO> listAllResource(Integer envId, String namespace,
                                                       Integer k8sKindType, Integer k8sStatus,
                                                       String userId, String keywords) {
    List<ModuleDeploySelfConfDTO> returnDeployConfDTOS = new ArrayList<>();
    List<ModuleDeploySelfConfDTO> deploySelfConfDTOS = new ArrayList<>();
    ModuleEnv moduleEnv = envMapper.selectOne(envId);
    if (moduleEnv != null) {
      K8sKindTypeEnum enumByCode = K8sKindTypeEnum.getEnumByCode(k8sKindType);
      if (enumByCode != null) {
        List<Object> objectList = k8sService.listAllNameSpacedResource(moduleEnv,
            namespace, enumByCode.getKindType());
        
        //获取数据库中已经存在的数据
        ModuleDeploySelfConfExample selfConfExample =
            new ModuleDeploySelfConfExample();
        
        ModuleDeploySelfConfExample.Criteria criteria = selfConfExample.createCriteria();
        
        criteria.andResourceKindEqualTo(enumByCode.getKindType())
            .andResourceNamespaceEqualTo(namespace)
            .andEnvIdEqualTo(envId);
        
        List<ModuleDeploySelfConf> deploySelfConfs =
            deploySelfConfMapper.selectByExample(selfConfExample);
        //遍历现有的资源存储到MAP中
        Map<String, ModuleDeploySelfConfDTO> selfConfDTOMap = new ConcurrentHashMap<>();
        
        Map<String, ModuleDeploySelfConf> selfConfMap = new ConcurrentHashMap<>();
        if (deploySelfConfs != null && deploySelfConfs.size() > 0) {
          for (ModuleDeploySelfConf deploySelfConf : deploySelfConfs) {
            selfConfMap.put(deploySelfConf.getResourceName(), deploySelfConf);
          }
        }
        
        //解析获取从k8s集群中获取的资源 形成数据保存起来
        if (objectList != null && objectList.size() > 0) {
          for (Object object : objectList) {
            Field[] fields = object.getClass().getDeclaredFields();
            ModuleDeploySelfConfDTO deploySelfConfDTO =
                new ModuleDeploySelfConfDTO();
            V1ObjectMeta objectMeta = null;
            for (Field field : fields) {
              field.setAccessible(true);
              String name = field.getName();
              if ("metadata".equals(name)) {
                try {
                  objectMeta = (V1ObjectMeta) field.get(object);
                } catch (IllegalAccessException e) {
                  e.printStackTrace();
                }
                break;
              }
            }
            if (objectMeta != null) {
              deploySelfConfDTO.setResourceName(objectMeta.getName());
              deploySelfConfDTO.setCreateTime(objectMeta.
                  getCreationTimestamp().toDate());
            }
            deploySelfConfDTO.setEnvId(moduleEnv.getId());
            deploySelfConfDTO.setEnvName(moduleEnv.getEnvName());
            deploySelfConfDTO.setResourceKind(enumByCode.getKindType());
            deploySelfConfDTO.setResourceNamespace(namespace);
            deploySelfConfDTO.setResourceJson(new Gson().toJson(object));
            deploySelfConfDTO.setResourceStatus(1);
            deploySelfConfDTOS.add(deploySelfConfDTO);
          }
          
          List<ModuleDeploySelfConf> insertDeploySelfConfList = new ArrayList<>();
          //保存当前已发布的资源信息到本地数据库 便于下次发布使用 前提是已发布的资源信息在本地找不到
          for (ModuleDeploySelfConfDTO deploySelfConfDTO : deploySelfConfDTOS) {
            //将数据存储到Map中然后留作后面更新现有的状态更新处理
            selfConfDTOMap.put(deploySelfConfDTO.getResourceName(), deploySelfConfDTO);
            //当前数据库里的数据已经包含该资源
            if (selfConfMap.containsKey(deploySelfConfDTO.getResourceName())) {
              //判断数据库的资源状态是否为已上线 如果不是需要修改为已上线
              ModuleDeploySelfConf deploySelfConf = selfConfMap.get(deploySelfConfDTO
                  .getResourceName());
              deploySelfConfDTO.setId(deploySelfConf.getId());
              deploySelfConfDTO.setEnvName(moduleEnv.getEnvName());
              if (deploySelfConf.getResourceStatus() == 0) {
                deploySelfConf.setResourceStatus(1);
                deploySelfConf.setResourceJson(deploySelfConfDTO.getResourceJson());
                deploySelfConfMapper.updateByPrimaryKeyWithBLOBs(deploySelfConf);
              }
            } else {
              //否则不包含 需要添加进入数据库
              insertDeploySelfConfList.add(deploySelfConfDTO);
            }
          }
          //批量添加存入数据库
          insertAll(insertDeploySelfConfList);
          insertDeploySelfConfList.clear();
        }
        //遍历数据库中的数据判断状态更新资源
        if (deploySelfConfs != null && deploySelfConfs.size() > 0) {
          //更改数据库中数据的状态  并将未发布上线的资源也添加到展示数据中
          for (ModuleDeploySelfConf deploySelfConf : deploySelfConfs) {
            
            //如果包含也就是发布在k8s集群上的资源数据库中有记录并在线上的
            if (selfConfDTOMap.containsKey(deploySelfConf.getResourceName())) {
              //然后验证数据中数据资源的状态是否对的上
              if (deploySelfConf.getResourceStatus() == 0) {
                deploySelfConf.setResourceStatus(1);
                deploySelfConfMapper.updateByPrimaryKeySelective(deploySelfConf);
              }
            } else {
              ModuleDeploySelfConfDTO deploySelfConfDTO =
                  new ModuleDeploySelfConfDTO();
              deploySelfConfDTO.setResourceName(deploySelfConf.getResourceName());
              deploySelfConfDTO.setCreateTime(deploySelfConf.getCreateTime());
              deploySelfConfDTO.setEnvId(moduleEnv.getId());
              deploySelfConfDTO.setEnvName(moduleEnv.getEnvName());
              deploySelfConfDTO.setResourceKind(enumByCode.getKindType());
              deploySelfConfDTO.setResourceStatus(0);
              deploySelfConfDTO.setResourceNamespace(namespace);
              deploySelfConfDTO.setId(deploySelfConf.getId());
              deploySelfConfDTO.setEnvName(moduleEnv.getEnvName());
              
              deploySelfConfDTOS.add(deploySelfConfDTO);
              if (deploySelfConf.getResourceStatus() == 1) {
                deploySelfConf.setResourceStatus(0);
                deploySelfConfMapper.updateByPrimaryKeySelective(deploySelfConf);
              }
            }
          }
        }
        
        //最后遍历数据返回给前端
        if (deploySelfConfDTOS.size() > 0) {
          for (ModuleDeploySelfConfDTO deploySelfConfDTO : deploySelfConfDTOS) {
            //根据条件返回给前端
            //JSON数据量大不做返回
            deploySelfConfDTO.setResourceJson("");
            
            //根据需求
            K8sResourceStatusEnum statusEnum = K8sResourceStatusEnum.getEnumByCode(k8sStatus);
            if (statusEnum != null) {
              switch (statusEnum) {
                case IS_ONLINE:
                  if (deploySelfConfDTO.getResourceStatus() == 1) {
                    
                    if (StringUtils.isNotBlank(keywords)) {
                      if (deploySelfConfDTO.getResourceName().contains(keywords)) {
                        returnDeployConfDTOS.add(deploySelfConfDTO);
                      }
                    } else {
                      returnDeployConfDTOS.add(deploySelfConfDTO);
                    }
                  }
                  break;
                case IS_NOT_ONLINE:
                  if (deploySelfConfDTO.getResourceStatus() == 0) {
                    if (StringUtils.isNotBlank(keywords)) {
                      if (deploySelfConfDTO.getResourceName().contains(keywords)) {
                        returnDeployConfDTOS.add(deploySelfConfDTO);
                      }
                    } else {
                      returnDeployConfDTOS.add(deploySelfConfDTO);
                    }
                  }
                  break;
                case ALL_RESOURCE:
                  if (StringUtils.isNotBlank(keywords)) {
                    if (deploySelfConfDTO.getResourceName().contains(keywords)) {
                      returnDeployConfDTOS.add(deploySelfConfDTO);
                    }
                  } else {
                    returnDeployConfDTOS.add(deploySelfConfDTO);
                  }
                  break;
                default:
                  break;
              }
            }
          }
          deploySelfConfDTOS.clear();
          //排序数据重新输出结果
          Collections.sort(returnDeployConfDTOS, (o1, o2) -> {
            int mark = 1;
            Date date0 = o1.getCreateTime();
            Date date1 = o2.getCreateTime();
            if (date0.getTime() > date1.getTime()) {
              mark = -1;
            }
            if (o1.getCreateTime().equals(o2.getCreateTime())) {
              mark = 0;
            }
            return mark;
          });
        }
      }
    }
    return returnDeployConfDTOS;
  }
  
  /**
   * 上传YAML文件然后解析YAML文件信息存入数据库 并发布该上传的YAMl文件
   * 保证同步的操作,限制并发
   *
   * @param envId
   * @param yamlFile
   * @param userIdFromToken
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  public synchronized ResponseDTO insertResourceInfo(Integer envId, MultipartFile yamlFile,
                                                     String userIdFromToken) {
    ResponseDTO responseDTO = new ResponseDTO();
    ModuleEnv moduleEnv = envMapper.selectOne(envId);
    if (moduleEnv != null) {
      //首先保存文件
      StringBuilder fileNameSb = new StringBuilder();
      String dateOnlyString = DateUtils.generateDateOnlyString();
      String name = yamlFile.getName();
      fileNameSb.append(yamlFilePathPrefix).append(name).append(dateOnlyString);
      File file = new File(fileNameSb.toString());
      
      List<K8sYamlVo> k8sYamlVoList;
      try {
        yamlFile.transferTo(file);
        //解析文件内容
        k8sYamlVoList = K8sUtils.transYaml2VoList(file);
      } catch (IOException e) {
        throw new TransYaml2K8sVoException("文件解析出错" + e.getMessage());
      }
      //存入相关数据
      if (k8sYamlVoList != null && k8sYamlVoList.size() > 0) {
        //对多个文件来说 要么全部成功 要么全部失败 控制在一个事物内的操作
        
        for (K8sYamlVo k8sYamlVo : k8sYamlVoList) {
          log.info("k8s yaml vo 数据: {}", new Gson().toJson(k8sYamlVo));
          ModuleDeploySelfConf deploySelfConf = new ModuleDeploySelfConf();
          deploySelfConf.setResourceName(k8sYamlVo.getMetadataName());
          deploySelfConf.setCreateTime(new Date());
          deploySelfConf.setEnvId(envId);
          deploySelfConf.setResourceKind(k8sYamlVo.getKind());
          deploySelfConf.setUserId(userIdFromToken);
          deploySelfConf.setResourceStatus(1);
          deploySelfConf.setResourceFilePath(fileNameSb.toString()
              .replace(yamlFilePathPrefix, ""));
          deploySelfConf.setResourceJson(new Gson().toJson(k8sYamlVo.getO()));
          String namespace = k8sYamlVo.getNamespace();
          if (StringUtils.isBlank(namespace)) {
            namespace = K8sNameSpace.DEFAULT;
          }
          deploySelfConf.setResourceNamespace(namespace);
          //查询当前数据库是否有同名的资源存储于数据库中 并且已经下线的 这种情况直接删除该条记录即可
          ModuleDeploySelfConfExample selfConfExample = new ModuleDeploySelfConfExample();
          ModuleDeploySelfConfExample.Criteria criteria = selfConfExample.createCriteria();
          criteria.andEnvIdEqualTo(envId).andResourceNamespaceEqualTo(namespace)
              .andResourceKindEqualTo(k8sYamlVo.getKind())
              .andResourceNameEqualTo(k8sYamlVo.getMetadataName())
              .andResourceStatusEqualTo(0);
          List<ModuleDeploySelfConf> deploySelfConfs =
              deploySelfConfMapper.selectByExample(selfConfExample);
          if (deploySelfConfs != null && deploySelfConfs.size() > 0) {
            for (ModuleDeploySelfConf deploySelfConf1 : deploySelfConfs) {
              deploySelfConfMapper.deleteByPrimaryKey(deploySelfConf1.getId());
            }
          }
          //然后插入新的数据
          deploySelfConfMapper.insertSelective(deploySelfConf);
        }
        List<K8sYamlVo> successK8sYamlVoList = new ArrayList<>();
        Map<String, Boolean> resultMap = new HashMap<>();
        int count = 0;
        //数据库数据插入OK后进行文件的发布
        for (K8sYamlVo k8sYamlVo : k8sYamlVoList) {
          try {
            if (k8sService.deploy(k8sYamlVo, moduleEnv, null)) {
              successK8sYamlVoList.add(k8sYamlVo);
              resultMap.put(k8sYamlVo.getMetadataName() + " : " + k8sYamlVo.getKind(), true);
            } else {
              count++;
              responseDTO.fail("发布失败");
            }
          } catch (Exception e) {
            count++;
            e.printStackTrace();
            responseDTO.fail(e.getMessage());
            break;
          }
        }
        //一旦有发布失败的情况发生 需要删除原有的
        if (count > 0) {
          if (successK8sYamlVoList.size() > 0) {
            for (K8sYamlVo k8sYamlVo : successK8sYamlVoList) {
              k8sService.deleteNamespacedSource(k8sYamlVo.getMetadataName(),
                  k8sYamlVo.getKind(), k8sYamlVo.getNamespace(), moduleEnv);
            }
          }
          //同时需要抛出异常使得数据库的插入操作回滚
          throw new K8SDeployException(responseDTO.getData().toString());
        } else {
          //发布成功的显示
          responseDTO.success(resultMap);
        }
      }
    }
    return responseDTO;
  }
  
  /**
   * 替换YAML文件存入数据库 并直接上线 也是要作为同步的操作 否则会造成数据库数据的脏读
   * 这里只能做单个资源的资源更换
   *
   * @param envId
   * @param resourceId
   * @param yamlFile
   * @param userIdFromToken
   * @return
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  public synchronized boolean updateOneResource(Integer envId, Integer resourceId,
                                                MultipartFile yamlFile, String userIdFromToken) {
    //查找出现有的数据库中的数据
    ModuleDeploySelfConf deploySelfConf = deploySelfConfMapper.selectByPrimaryKey(resourceId);
    ModuleEnv moduleEnv = envMapper.selectOne(envId);
    if (deploySelfConf != null && moduleEnv != null) {
      //首先保存文件
      StringBuilder fileNameSb = new StringBuilder();
      String dateOnlyString = DateUtils.generateDateOnlyString();
      String name = yamlFile.getName();
      fileNameSb.append(yamlFilePathPrefix).append(name).append(dateOnlyString);
      File file = new File(fileNameSb.toString());
      List<K8sYamlVo> k8sYamlVos;
      try {
        yamlFile.transferTo(file);
        //解析多文件内容
        k8sYamlVos = K8sUtils.transYaml2VoList(file);
      } catch (IOException e) {
        throw new TransYaml2K8sVoException("文件解析出错" + e.getMessage());
      }
      
      if (k8sYamlVos != null && k8sYamlVos.size() > 0) {
        for (K8sYamlVo k8sYamlVo : k8sYamlVos) {
          String namespace = k8sYamlVo.getNamespace();
          if (StringUtils.isBlank(namespace)) {
            namespace = "default";
          }
          //必须是同样的资源名称和类型包括namespace相同才能做替换资源处理 否则更新失败
          if (!deploySelfConf.getResourceName().equals(k8sYamlVo.getMetadataName())
              || !deploySelfConf.getResourceKind().equals(k8sYamlVo.getKind())
              || !deploySelfConf.getResourceNamespace().equals(namespace)) {
            
            return false;
          }
          boolean flag = true;
          //然后 先下线应用
          if (deploySelfConf.getResourceStatus() == 1) {
            flag = k8sService.deleteNamespacedSource(deploySelfConf.getResourceName(),
                deploySelfConf.getResourceKind(),
                deploySelfConf.getResourceNamespace(), moduleEnv);
            if (flag) {
              //3s的停顿时间 //待资源下线
              try {
                Thread.sleep(3000);
              } catch (InterruptedException e) {
                e.printStackTrace();
              }
            }
          }
          //下线成功后上线该应用
          if (flag) {
            if (k8sService.deploy(k8sYamlVo, moduleEnv, null)) {
              deploySelfConf.setUserId(userIdFromToken);
              deploySelfConf.setResourceStatus(1);
              deploySelfConf.setResourceFilePath(fileNameSb.toString()
                  .replace(yamlFilePathPrefix, ""));
              deploySelfConf.setResourceJson(new Gson().toJson(k8sYamlVo.getO()));
              deploySelfConfMapper.updateByPrimaryKeyWithBLOBs(deploySelfConf);
            }
          }
        }
        return true;
      }
    }
    return false;
  }
  
  /**
   * 下线资源文件
   *
   * @param envId
   * @param resourceId
   * @return
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  public boolean offlineResource(Integer envId, Integer resourceId) {
    ModuleDeploySelfConf deploySelfConf = deploySelfConfMapper.selectByPrimaryKey(resourceId);
    ModuleEnv moduleEnv = envMapper.selectOne(envId);
    if (deploySelfConf != null && moduleEnv != null) {
      deploySelfConf.setResourceStatus(0);
      if (k8sService.deleteNamespacedSource(deploySelfConf.getResourceName(),
          deploySelfConf.getResourceKind(),
          deploySelfConf.getResourceNamespace(), moduleEnv)) {
        deploySelfConfMapper.updateByPrimaryKeySelective(deploySelfConf);
        return true;
      }
    }
    return false;
  }
  
  /**
   * 将下线的应用上线
   *
   * @param envId
   * @param resourceId
   * @return
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  public boolean onlineResource(Integer envId, Integer resourceId) {
    ModuleDeploySelfConf deploySelfConf = deploySelfConfMapper.selectByPrimaryKey(resourceId);
    ModuleEnv moduleEnv = envMapper.selectOne(envId);
    if (deploySelfConf != null && moduleEnv != null) {
      deploySelfConf.setResourceStatus(1);
      String resourceJson = deploySelfConf.getResourceJson();
      K8sYamlVo k8sYamlVo = null;
      if (StringUtils.isNotBlank(resourceJson)) {
        try {
          k8sYamlVo = K8sUtils.transObject2Vo(Yaml.load(resourceJson));
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
      
      if (k8sYamlVo != null && k8sService.deploy(k8sYamlVo, moduleEnv, null)) {
        deploySelfConfMapper.updateByPrimaryKeySelective(deploySelfConf);
        return true;
      }
      
    }
    return false;
  }
  
  /**
   * 获取资源的详情信息
   *
   * @param envId
   * @param resourceId
   */
  @Override
  public ModuleDeploySelfConfDTO getResourceDetails(Integer envId, Integer resourceId) {
    ModuleDeploySelfConfDTO deploySelfConfDTO = new ModuleDeploySelfConfDTO();
    ModuleDeploySelfConf deploySelfConf = deploySelfConfMapper.selectByPrimaryKey(resourceId);
    ModuleEnv moduleEnv = envMapper.selectOne(envId);
    if (deploySelfConf != null && moduleEnv != null) {
      
      BeanUtils.copyProperties(deploySelfConf, deploySelfConfDTO);
      String resourceName = deploySelfConf.getResourceName();
      String namespace = deploySelfConf.getResourceNamespace();
      String kind = deploySelfConf.getResourceKind();
      deploySelfConfDTO.setEnvName(moduleEnv.getEnvName());
      
      String resourceByName = k8sService.getResourceByNameUseOKHttp(resourceName,
          moduleEnv, namespace, kind);
      //如果没有获取到线上的内容  则显示的是数据库存储的json数据
      if (StringUtils.isNotBlank(resourceByName)) {
        deploySelfConfDTO.setResourceJson(resourceByName);
        deploySelfConfDTO.setResourceStatus(1);
      }
    }
    return deploySelfConfDTO;
  }
  
  /**
   * 删除已下线资源记录 物理删除 直接清除数据
   *
   * @param envId
   * @param resourceId
   * @return
   */
  @Override
  public boolean deleteResourceData(Integer envId, Integer resourceId) {
    ModuleDeploySelfConf deploySelfConf = deploySelfConfMapper.selectByPrimaryKey(resourceId);
    if (deploySelfConf != null && deploySelfConf.getResourceStatus() == 0
        && deploySelfConf.getEnvId().equals(envId)) {
      deploySelfConfMapper.deleteByPrimaryKey(resourceId);
      return true;
    }
    return false;
  }
}

