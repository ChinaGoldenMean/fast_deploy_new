package com.xc.fast_deploy.dao.master_dao;

import com.xc.fast_deploy.model.master_model.ModuleDeploy;

import java.util.List;
import java.util.Set;

import com.xc.fast_deploy.model.master_model.example.ModuleDeployExample;
import com.xc.fast_deploy.vo.module_vo.ModuleDeployVo;
import com.xc.fast_deploy.vo.module_vo.ModuleEnvCenterManageVo;
import com.xc.fast_deploy.vo.module_vo.ModuleManageDeployVO;
import org.apache.ibatis.annotations.Param;

public interface ModuleDeployMapperModule extends ModuleBaseMapper<ModuleDeploy, Integer> {
  int countByExample(ModuleDeployExample example);
  
  int deleteByExample(ModuleDeployExample example);
  
  int deleteByPrimaryKey(Integer id);
  
  int insert(ModuleDeploy record);
  
  int insertSelective(ModuleDeploy record);
  
  List<ModuleDeploy> selectByExample(ModuleDeployExample example);
  
  ModuleDeploy selectByPrimaryKey(Integer id);
  
  int updateByExampleSelective(@Param("record") ModuleDeploy record, @Param("example") ModuleDeployExample example);
  
  int updateByExample(@Param("record") ModuleDeploy record, @Param("example") ModuleDeployExample example);
  
  int updateByPrimaryKeySelective(ModuleDeploy record);
  
  int updateByPrimaryKey(ModuleDeploy record);
  
  //select all
  List<ModuleDeploy> selectAll();
  
  List<ModuleEnvCenterManageVo> selectModuleEnvCenterAll(ModuleDeployVo deployVo);
  
  List<ModuleManageDeployVO> selectDeployListModule(Set<Integer> moduleIds);
  
  List<ModuleManageDeployVO> selectModuleCenter(Set<Integer> moduleIds);
}