package com.xc.fast_deploy.dao.master_dao;

import java.util.List;
import java.util.Set;

import com.xc.fast_deploy.dto.module.ModuleCenterEnvDTO;
import com.xc.fast_deploy.model.master_model.ModuleCenter;
import com.xc.fast_deploy.model.master_model.example.ModuleCenterExample;
import com.xc.fast_deploy.vo.module_vo.ModuleEnvCenterManageVo;
import com.xc.fast_deploy.vo.module_vo.param.ModuleCenterSelectParamVo;
import org.apache.ibatis.annotations.Param;

public interface ModuleCenterMapperModule extends ModuleBaseMapper<ModuleCenter, Integer> {
  
  int countByExample(ModuleCenterExample example);
  
  int deleteByExample(ModuleCenterExample example);
  
  int deleteByPrimaryKey(Integer id);
  
  int insert(ModuleCenter record);
  
  Integer[] findCenterIdByCenterName(@Param("centerNames") String[] centerNames, @Param("envId") Integer envId);
  
  int insertSelective(ModuleCenter record);
  
  List<ModuleCenter> selectByExample(ModuleCenterExample example);
  
  ModuleCenter selectByPrimaryKey(Integer id);
  
  int updateByExampleSelective(@Param("record") ModuleCenter record, @Param("example") ModuleCenterExample example);
  
  int updateByExample(@Param("record") ModuleCenter record, @Param("example") ModuleCenterExample example);
  
  int updateByPrimaryKeySelective(ModuleCenter record);
  
  int updateByPrimaryKey(ModuleCenter record);
  
  //self add
  List<ModuleCenter> selectByCenter(ModuleCenter record);
  
  //select all
  List<ModuleCenter> selectAll();
  
  boolean updateById(Integer id);
  
  //分页条件查询
  List<ModuleCenterEnvDTO> selectByParamVo(ModuleCenterSelectParamVo moduleCenterSelectParamVo);
  
  default List<ModuleEnvCenterManageVo> selectCenterModule() {
    return selectCenterModule();
  }
  
  List<ModuleEnvCenterManageVo> selectCenterModule(Set<Integer> envIds);
  
}