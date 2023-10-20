package com.xc.fast_deploy.dao.master_dao;

import com.xc.fast_deploy.dto.module.ModuleDeployStatisticsDTO;
import com.xc.fast_deploy.dto.module.ModuleMirrorDTO;
import com.xc.fast_deploy.model.master_model.ModuleDeployLog;

import java.util.Date;
import java.util.List;

import com.xc.fast_deploy.model.master_model.example.ModuleDeployLogExample;
import com.xc.fast_deploy.vo.module_vo.ModuleDeployLogVo;
import com.xc.fast_deploy.vo.module_vo.param.ModuleDeployLogSelectParam;
import com.xc.fast_deploy.vo.module_vo.param.ModuleManageSelectParamVo;
import org.apache.ibatis.annotations.Param;

public interface ModuleDeployLogMapper extends BaseMapper<ModuleDeployLog, Integer> {
  int countByExample(ModuleDeployLogExample example);
  
  int deleteByExample(ModuleDeployLogExample example);
  
  int deleteByPrimaryKey(Integer id);
  
  int insert(ModuleDeployLog record);
  
  int insertSelective(ModuleDeployLog record);
  
  List<ModuleDeployLog> selectByExample(ModuleDeployLogExample example);
  
  ModuleDeployLog selectByPrimaryKey(Integer id);
  
  int updateByExampleSelective(@Param("record") ModuleDeployLog record, @Param("example") ModuleDeployLogExample example);
  
  int updateByExample(@Param("record") ModuleDeployLog record, @Param("example") ModuleDeployLogExample example);
  
  int updateByPrimaryKeySelective(ModuleDeployLog record);
  
  int updateByPrimaryKey(ModuleDeployLog record);
  
  List<ModuleDeployLogVo> selectAllDeployedModule(ModuleDeployLogSelectParam logSelectParam);
  
  List<ModuleDeployStatisticsDTO> selectModuleDeployOrderByTime(ModuleManageSelectParamVo moduleManageSelectParamVo);
  
  List<ModuleDeployStatisticsDTO> selectModuleDeployChangeLogByTime(ModuleDeployLogSelectParam moduleDeployLogSelectParam);
  
  ModuleDeployLog selectLastOffline(@Param("moduleId") Integer moduleId, @Param("envId") Integer envId);
  //   Integer selectIsUsedMirrorByParam(ModuleMirrorDTO moduleMirrorDTO);
}