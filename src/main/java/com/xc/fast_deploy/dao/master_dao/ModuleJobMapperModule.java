package com.xc.fast_deploy.dao.master_dao;

import com.xc.fast_deploy.dto.module.ModuleJobDTO;
import com.xc.fast_deploy.dto.module.ModuleManageDTO;
import com.xc.fast_deploy.model.master_model.ModuleJob;
import com.xc.fast_deploy.model.master_model.example.ModuleJobExample;

import java.util.List;

import com.xc.fast_deploy.vo.module_vo.ModuleJobVo;
import com.xc.fast_deploy.vo.module_vo.param.ModuleJobSelectParamVo;
import org.apache.ibatis.annotations.Param;

public interface ModuleJobMapperModule extends ModuleBaseMapper<ModuleJob, Integer> {
  
  int countByExample(ModuleJobExample example);
  
  int deleteByExample(ModuleJobExample example);
  
  int deleteByPrimaryKey(Integer id);
  
  int insert(ModuleJob record);
  
  int insertSelective(ModuleJob record);
  
  List<ModuleJob> selectByExample(ModuleJobExample example);
  
  ModuleJob selectByPrimaryKey(Integer id);
  
  int updateByExampleSelective(@Param("record") ModuleJob record, @Param("example") ModuleJobExample example);
  
  int updateByExample(@Param("record") ModuleJob record, @Param("example") ModuleJobExample example);
  
  int updateByPrimaryKeySelective(ModuleJob record);
  
  int updateByPrimaryKey(ModuleJob record);
  
  //select all
  List<ModuleJob> selectAll();
  
  List<ModuleJobVo> selectJobVoPageByVo(ModuleJobSelectParamVo selectParamVo);
  
  ModuleJobDTO selectJobDTOById(Integer jobId);
  
  List<ModuleManageDTO> selectJobModuleByEnvId(Integer envId);
  
  ModuleManageDTO selectJobModuleByEnvAndModuleId(ModuleJob moduleJob);
}