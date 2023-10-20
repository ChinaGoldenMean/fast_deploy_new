package com.xc.fast_deploy.service.common;

import com.xc.fast_deploy.dto.MyPageInfo;
import com.xc.fast_deploy.dto.ResponseDTO;
import com.xc.fast_deploy.dto.module.ModuleDeployDTO;
import com.xc.fast_deploy.dto.module.ModuleDeployYamlDTO;
import com.xc.fast_deploy.model.master_model.ModuleDeployYaml;
import com.xc.fast_deploy.vo.k8s_vo.K8sResourceVo;
import com.xc.fast_deploy.vo.module_vo.DeployModuleVo;
import com.xc.fast_deploy.vo.module_vo.param.ModuleDeployYamlSelectParamVo;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ModuleDeployYamlService extends BaseService<ModuleDeployYaml, Integer> {
  
  boolean createDeployModuleYaml(DeployModuleVo deployModuleVo, MultipartFile yamlFile, String yamlPath, MultipartFile svcYamlFile, String svcYamlPath);
  
  ResponseDTO updateDeployModuleYaml(DeployModuleVo deployModuleVo, MultipartFile yamlFile, String yamlPath, MultipartFile svcYamlFile, String svcYamlPath);
  
  MyPageInfo<ModuleDeployYamlDTO> selectDeployYamlAll(Integer pageNum, Integer pageSize, ModuleDeployYamlSelectParamVo yamlSelectParamVo);
  
  ResponseDTO deleteByYamlId(Integer deployYamlId);
  
  Object getYamlInfoById(Integer deployYamlId);
  
  ModuleDeployDTO getOneYamlInfoById(Integer deployId);
  
  Integer selectEnvIdByYamlId(Integer deployYamlId);
  
  Object getYamlJsonInfo(Integer envId, Integer moduleId, Integer type) throws IOException;
  
  boolean saveYamlJson(K8sResourceVo resourceVo);
  
  boolean updateDeployModuleJSON(K8sResourceVo resourceVo);
  
  void compareProdYaml();
  
}
