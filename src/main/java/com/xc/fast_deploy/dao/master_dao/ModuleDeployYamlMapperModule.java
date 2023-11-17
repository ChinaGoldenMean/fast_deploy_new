package com.xc.fast_deploy.dao.master_dao;

import com.xc.fast_deploy.dto.module.ModuleDeployYamlDTO;
import com.xc.fast_deploy.model.master_model.ModuleDeployYaml;
import com.xc.fast_deploy.model.master_model.ModuleYamlDiff;
import com.xc.fast_deploy.model.master_model.example.ModuleDeployYamlExample;

import java.util.List;

import com.xc.fast_deploy.vo.module_vo.param.ModuleDeployYamlSelectParamVo;
import org.apache.ibatis.annotations.Param;

public interface ModuleDeployYamlMapperModule extends ModuleBaseMapper<ModuleDeployYaml, Integer> {
  int countByExample(ModuleDeployYamlExample example);
  
  int deleteByExample(ModuleDeployYamlExample example);
  
  int deleteByPrimaryKey(Integer id);
  
  int insert(ModuleDeployYaml record);
  
  int insertSelective(ModuleDeployYaml record);
  
  List<ModuleDeployYaml> selectByExampleWithBLOBs(ModuleDeployYamlExample example);
  
  List<ModuleDeployYaml> selectByExample(ModuleDeployYamlExample example);
  
  ModuleDeployYaml selectByPrimaryKey(Integer id);
  
  int updateByExampleSelective(@Param("record") ModuleDeployYaml record, @Param("example") ModuleDeployYamlExample example);
  
  int updateByExampleWithBLOBs(@Param("record") ModuleDeployYaml record, @Param("example") ModuleDeployYamlExample example);
  
  int updateByExample(@Param("record") ModuleDeployYaml record, @Param("example") ModuleDeployYamlExample example);
  
  int updateByPrimaryKeySelective(ModuleDeployYaml record);
  
  int updateByPrimaryKeyWithBLOBs(ModuleDeployYaml record);
  
  int updateByPrimaryKey(ModuleDeployYaml record);
  
  //select all
  List<ModuleDeployYaml> selectAll();
  
  default List<ModuleDeployYamlDTO> selectDeployYamlVoPageByVo() {
    return selectDeployYamlVoPageByVo();
  }
  
  List<ModuleDeployYamlDTO> selectDeployYamlVoPageByVo(ModuleDeployYamlSelectParamVo yamlSelectParamVo);
  
  Integer selectEnvIdByYamlId(Integer deployYamlId);
  
  List<ModuleDeployYaml> selectYamlJsonByModuleId(Integer moduleId);
  
  int insertYamlDiff(List<ModuleYamlDiff> yamlDiffList);
  
  List<ModuleYamlDiff> getYamlDiff();
}