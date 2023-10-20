package com.xc.fast_deploy.service.common;

import com.xc.fast_deploy.dto.ResponseDTO;
import com.xc.fast_deploy.model.master_model.ModuleUser;
import com.xc.fast_deploy.vo.module_vo.ModuleEnvCenterManageVo;
import com.xc.fast_deploy.vo.module_vo.param.ModuleDeployPodCenterManageVo;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface ModuleDeployListService {
  
  //获取发布列表清单数据
  Set<ModuleEnvCenterManageVo> getAllDeployListSelf(String userId, Integer envId,
                                                    Integer centerId, String keyName);
  
  //插入到清单的模块数据
  boolean insert2RedisModuleDeployList(Integer envId, String userId, Integer[] moduleIds);
  
  //从excel导入发布清单,region表示CRM或者计费
  List<Map<String, Object>> fromExcelInsertModuleDeployList(String region, String userId, MultipartFile excelFile);
  
  //移除清单中的数据
  boolean removeModuleDeployList(Integer[] moduleIds);
  
  //获取发布清单带pods的相关数据
  List<ModuleDeployPodCenterManageVo> getAllDeployListSelfPods(Integer envId, String userId,
                                                               Integer centerId, String keyName);
  
  Map<String, Object> replaceBatchMirrors(ModuleUser moduleUser, Integer[] moduleIds);
  
  //判断模块是否在发布清单中
  boolean judgeModuleInDeployList(Integer envId, Integer moduleId, String userId);
  
  File exportModuleDeployList(Integer[] moduleIds);
}
