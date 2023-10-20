package com.xc.fast_deploy.dao.master_dao;

import com.xc.fast_deploy.model.master_model.ModuleDeploySelfConf;
import com.xc.fast_deploy.model.master_model.example.ModuleDeploySelfConfExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ModuleDeploySelfConfMapper {
  int countByExample(ModuleDeploySelfConfExample example);
  
  int deleteByExample(ModuleDeploySelfConfExample example);
  
  int deleteByPrimaryKey(Integer id);
  
  int insert(ModuleDeploySelfConf record);
  
  int insertSelective(ModuleDeploySelfConf record);
  
  List<ModuleDeploySelfConf> selectByExampleWithBLOBs(ModuleDeploySelfConfExample example);
  
  List<ModuleDeploySelfConf> selectByExample(ModuleDeploySelfConfExample example);
  
  ModuleDeploySelfConf selectByPrimaryKey(Integer id);
  
  int updateByExampleSelective(@Param("record") ModuleDeploySelfConf record, @Param("example") ModuleDeploySelfConfExample example);
  
  int updateByExampleWithBLOBs(@Param("record") ModuleDeploySelfConf record, @Param("example") ModuleDeploySelfConfExample example);
  
  int updateByExample(@Param("record") ModuleDeploySelfConf record, @Param("example") ModuleDeploySelfConfExample example);
  
  int updateByPrimaryKeySelective(ModuleDeploySelfConf record);
  
  int updateByPrimaryKeyWithBLOBs(ModuleDeploySelfConf record);
  
  int updateByPrimaryKey(ModuleDeploySelfConf record);
  
  Integer selectIdFirst();
  
  Integer updateVal(Integer val);
  
  int insertBatch(List<ModuleDeploySelfConf> list);
}