package com.xc.fast_deploy.dao.master_dao;

import com.xc.fast_deploy.dto.module.ModuleEnvDTO;
import com.xc.fast_deploy.model.master_model.ModuleEnv;

import java.util.List;
import java.util.Set;

import com.xc.fast_deploy.vo.module_vo.ModuleEnvVo;
import com.xc.fast_deploy.vo.module_vo.PsPaasEnvVo;
import org.apache.ibatis.annotations.Param;

public interface ModuleEnvMapperModule extends ModuleBaseMapper<ModuleEnv, Integer> {
  
  ModuleEnv selectByPrimaryKey(Integer id);
  
  ModuleEnv selectWithBlobsByPrimaryKey(Integer id);
  
  ModuleEnvVo selectWithCertById(Integer id);
  
  //查询一条model_env的数据
  ModuleEnv selectOne(Integer id);
  
  //查询paas的所有数据
  List<ModuleEnv> selectPsAll(Set<Integer> envIds);
  
  //查询env的所有数据作为下拉列表
  List<ModuleEnv> selectEnvAll(Set<Integer> envIds);
  
  ////查询所有数据
  List<ModuleEnv> getAllModuleEnv(@Param("collection") Set<Integer> envIds, @Param("prod") Integer prod);
  
  ModuleEnvDTO selectCerEnvInfoById(Integer id);
  
  List<ModuleEnv> getEnvIdByPaasId();
  
  //通过envName查询envId
  Integer getEnvIdByEnvName(String envName);
  
  List<PsPaasEnvVo> selectPsAllData();
  
  int insert(ModuleEnv moduleEnv);
}