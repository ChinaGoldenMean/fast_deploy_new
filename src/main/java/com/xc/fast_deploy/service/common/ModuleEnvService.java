package com.xc.fast_deploy.service.common;

import com.xc.fast_deploy.dto.module.ModuleEnvDTO;
import com.xc.fast_deploy.model.master_model.ModuleEnv;
import com.xc.fast_deploy.vo.module_vo.ModuleEnvVo;
import com.xc.fast_deploy.vo.module_vo.PsPaasEnvVo;

import java.util.List;
import java.util.Set;

public interface ModuleEnvService extends BaseService<ModuleEnv, Integer> {
  
  ModuleEnvVo selectWithCertById(Integer id);
  
  //插入一条model_env的数据
  boolean insertOneEnv(ModuleEnv moduleEnv);
  
  //更新一条model_env的数据
  boolean updateAll(ModuleEnv moduleEnv);
  
  //删除一条model_env的数据
  boolean delectOne(Integer id);
  
  //查询一条model_env的数据
  ModuleEnv selectOne(Integer id);
  
  //查询paas的所有数据作为下拉列表
//    List<ModuleEnv> selectPsAll(Set<Integer> envIds);
  
  ModuleEnvDTO selectCerEnvInfoById(Integer id);
  
  //查询env的所有数据作为下拉列表
  List<ModuleEnv> selectEnvAll(Set<Integer> envIds);
  
  //查询所有数据
  List<ModuleEnv> getAllModuleEnv(Set<Integer> envIds);
  
  ModuleEnv selectBlobsById(Integer billEnvId);
  
  List<PsPaasEnvVo> selectPsAll();
}
