package com.xc.fast_deploy.dao.master_dao;

import com.xc.fast_deploy.model.master_model.ModuleNeed;
import com.xc.fast_deploy.model.master_model.example.ModuleDeployNeedExample;
import com.xc.fast_deploy.model.master_model.ModuleDeployNeed;
import com.xc.fast_deploy.vo.module_vo.ModuleUpgradeVo;
import com.xc.fast_deploy.vo.module_vo.param.ModuleNeedSelectParamVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

public interface ModuleDeployNeedMapper {
  long countByExample(ModuleDeployNeedExample example);
  
  int deleteByExample(ModuleDeployNeedExample example);
  
  int deleteByPrimaryKey(Integer id);
  
  int insert(ModuleDeployNeed record);
  
  int insertSelective(ModuleDeployNeed record);
  
  List<ModuleDeployNeed> selectByExample(ModuleDeployNeedExample example);
  
  ModuleDeployNeed selectByPrimaryKey(Integer id);
  
  int updateByExampleSelective(@Param("record") ModuleDeployNeed record, @Param("example") ModuleDeployNeedExample example);
  
  int updateByExample(@Param("record") ModuleDeployNeed record, @Param("example") ModuleDeployNeedExample example);
  
  int updateByPrimaryKeySelective(ModuleDeployNeed record);
  
  int updateByPrimaryKey(ModuleDeployNeed record);
  
  List<ModuleNeed> selectModuleNeedById(Integer needId);
  
  List<ModuleUpgradeVo> findModuleEnvByNeeds(@Param("needIds") List<Integer> needIds);
  
  int insertModuleNeed(List<ModuleNeed> moduleNeedList);
  
  int selectByNeedDescribe(@Param("needDescribe") String needDescribe,
                           @Param("envId") Integer envId);
  
  //根据需求id删除其模块的关联
  int deleteModuleNeedByNeedId(Integer needId);
  
  List<ModuleDeployNeed> selectAllNeedByDeveloper(ModuleNeedSelectParamVo paramVo);
  
  String selectDeveploperById(@Param("needId") String needId);
  
  List<ModuleDeployNeed> selectAllNeed(ModuleNeedSelectParamVo paramVo);
  
  List<ModuleDeployNeed> selectModuleNeedByNeedIds(Integer[] needIds);
  
  Set<Integer> selectAllModuleByneedId(@Param("needId") Integer needId, @Param("envId") Integer envId);
}