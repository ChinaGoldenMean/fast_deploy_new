package com.xc.fast_deploy.dao.master_dao;

import com.xc.fast_deploy.model.master_model.PModuleBasePermission;
import com.xc.fast_deploy.model.master_model.PModulePermission;
import com.xc.fast_deploy.vo.module_vo.permission.ModuleBaseBindPermissionVo;
import org.apache.ibatis.annotations.Param;

import javax.xml.crypto.dsig.keyinfo.KeyName;
import java.util.List;
import java.util.Set;

public interface PModuleBasePermissionMapper {
  
  int deleteByPrimaryKey(Integer id);
  
  Integer insert(PModuleBasePermission record);
  
  int insertSelective(PModuleBasePermission record);
  
  PModuleBasePermission selectByPrimaryKey(Integer id);
  
  int updateByPrimaryKeySelective(PModuleBasePermission record);
  
  int updateByPrimaryKey(PModuleBasePermission record);
  
  List<PModuleBasePermission> selectAll(@Param("keyName") String keyName);
  
  List<PModulePermission> selectPerListById(Integer basePermissionId);
  
  void deleteBindPermById(Integer basePermissionId);
  
  void insertBatchBindBasePerm(List<ModuleBaseBindPermissionVo> list);
  
  void deleteBindPermByPermId(Integer permissionId);
  
  void deleteBindPermByPermIds(Set<Integer> permissionIds);
  
  Set<Integer> selectIds(Set<Integer> ids);
}