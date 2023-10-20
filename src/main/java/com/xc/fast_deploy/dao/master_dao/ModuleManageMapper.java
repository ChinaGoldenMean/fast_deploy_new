package com.xc.fast_deploy.dao.master_dao;

import com.xc.fast_deploy.dto.module.ModuleManageDTO;
import com.xc.fast_deploy.model.master_model.ModuleDeployNeed;
import com.xc.fast_deploy.vo.module_vo.ModuleManageDeployVO;
import com.xc.fast_deploy.vo.module_vo.ModuleMangeVo;
import com.xc.fast_deploy.model.master_model.ModuleManage;
import com.xc.fast_deploy.model.master_model.example.ModuleManageExample;

import java.util.List;

import com.xc.fast_deploy.vo.module_vo.param.ModuleManageSelectParamVo;
import org.apache.ibatis.annotations.Param;

public interface ModuleManageMapper extends BaseMapper<ModuleManage, Integer> {
  int countByExample(ModuleManageExample example);
  
  int deleteByExample(ModuleManageExample example);
  
  int deleteByPrimaryKey(Integer id);
  
  int insert(ModuleManage record);
  
  int insertSelective(ModuleManage record);
  
  List<ModuleManage> selectByExample(ModuleManageExample example);
  
  ModuleManage selectByPrimaryKey(Integer id);
  
  int updateByExampleSelective(@Param("record") ModuleManage record, @Param("example") ModuleManageExample example);
  
  int updateByExample(@Param("record") ModuleManage record, @Param("example") ModuleManageExample example);
  
  int updateByPrimaryKeySelective(ModuleManage record);
  
  int updateByPrimaryKey(ModuleManage record);
  
  //select all
  List<ModuleManage> selectAll();
  
  List<ModuleMangeVo> selectModuleVoPageByVo(ModuleManageSelectParamVo manageSelectParamVo);
  
  ModuleManageDTO selectInfoById(Integer moduleId);
  
  List<ModuleManage> selectByIds(Integer[] moduleIds);
  
  List<ModuleManage> selectInfoInJobByCenterId(Integer centerId);
  
  Integer selectModuleNameOrContentExist(ModuleManage moduleManage);
  
  List<ModuleManageDeployVO> selectAllModuleByEnvId(Integer envId);
  
  List<ModuleManage> selectBYMdouleName(String moduleName);
  
  List<ModuleManage> selectIdByMdouleName(List<String> moduleNames);
  
}