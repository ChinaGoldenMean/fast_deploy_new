package com.xc.fast_deploy.service.common;

import com.xc.fast_deploy.dto.ResponseDTO;
import com.xc.fast_deploy.dto.module.ModuleDeploySelfConfDTO;
import com.xc.fast_deploy.model.master_model.ModuleDeploySelfConf;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ModuleResourceService {
  
  int insertAll(List<ModuleDeploySelfConf> list);
  
  List<ModuleDeploySelfConfDTO> listAllResource(Integer envId, String namespace,
                                                Integer k8sKindType, Integer k8sStatus, String userId, String keywords);
  
  ResponseDTO insertResourceInfo(Integer envId, MultipartFile yamlFile, String userIdFromToken);
  
  boolean updateOneResource(Integer envId, Integer resourceId, MultipartFile yamlFile, String userIdFromToken);
  
  boolean offlineResource(Integer envId, Integer resourceId);
  
  boolean onlineResource(Integer envId, Integer resourceId);
  
  ModuleDeploySelfConfDTO getResourceDetails(Integer envId, Integer resourceId);
  
  boolean deleteResourceData(Integer envId, Integer resourceId);
}
