package com.xc.fast_deploy.dao.master_dao;

import com.xc.fast_deploy.model.master_model.ModulePermissionUser;
import com.xc.fast_deploy.model.master_model.ModuleUser;
import com.xc.fast_deploy.model.master_model.example.ModuleUserExample;

import java.util.List;

import org.apache.ibatis.annotations.Param;

public interface ModuleUserMapperModule extends ModuleBaseMapper<ModuleUser, Integer> {
  int countByExample(ModuleUserExample example);
  
  int deleteByExample(ModuleUserExample example);
  
  int deleteByPrimaryKey(Integer id);
  
  int insert(ModuleUser record);
  
  int insertSelective(ModuleUser record);
  
  List<ModuleUser> selectByExample(ModuleUserExample example);
  
  ModuleUser selectByPrimaryKey(Integer id);
  
  int updateByExampleSelective(@Param("record") ModuleUser record, @Param("example") ModuleUserExample example);
  
  int updateByExample(@Param("record") ModuleUser record, @Param("example") ModuleUserExample example);
  
  int updateByPrimaryKeySelective(ModuleUser record);
  
  int updateByPrimaryKey(ModuleUser record);
  
  //select all
  List<ModuleUser> selectAll();
  
  String selectUserPermissionById(String userId);
  
  int insertPermission(ModulePermissionUser permissionUser);
  
  void deletePermissionByUserId(String userId);
}