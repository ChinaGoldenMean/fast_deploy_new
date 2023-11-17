package com.xc.fast_deploy.dao.master_dao;

import java.util.List;

import com.xc.fast_deploy.model.master_model.ModuleNeedLog;
import com.xc.fast_deploy.model.master_model.example.ModuleNeedLogExample;
import org.apache.ibatis.annotations.Param;

public interface ModuleNeedLogMapperModule extends ModuleBaseMapper<ModuleNeedLog, Integer> {
  long countByExample(ModuleNeedLogExample example);
  
  int deleteByExample(ModuleNeedLogExample example);
  
  int deleteByPrimaryKey(Integer id);
  
  int insert(ModuleNeedLog record);
  
  int insertSelective(ModuleNeedLog record);
  
  List<ModuleNeedLog> selectByExample(ModuleNeedLogExample example);
  
  ModuleNeedLog selectByPrimaryKey(Integer id);
  
  int updateByExampleSelective(@Param("record") ModuleNeedLog record, @Param("example") ModuleNeedLogExample example);
  
  int updateByExample(@Param("record") ModuleNeedLog record, @Param("example") ModuleNeedLogExample example);
  
  int updateByPrimaryKeySelective(ModuleNeedLog record);
  
  int updateByPrimaryKey(ModuleNeedLog record);
}