package com.xc.fast_deploy.service.common;

import com.xc.fast_deploy.dto.MyPageInfo;
import com.xc.fast_deploy.dto.ResponseDTO;
import com.xc.fast_deploy.dto.module.ModuleCenterEnvDTO;
import com.xc.fast_deploy.dto.module.ModuleDeployEnvDTO;
import com.xc.fast_deploy.model.master_model.ModuleCenter;
import com.xc.fast_deploy.vo.module_vo.param.ModuleCenterSelectParamVo;

import java.util.List;
import java.util.Set;

public interface ModuleCenterService extends BaseService<ModuleCenter, Integer> {
  
  //查询模块分类(各种条件)
//    List<ModuleCenter> selectByModuleType(ModuleCenter center);
//
//    ModuleCenter selectAvailableById(Integer centerId);
//
//    boolean updateById(Integer id);
  
  MyPageInfo<ModuleCenterEnvDTO> selectPageCen(Integer pageNum, Integer pageSize, ModuleCenterSelectParamVo moduleCenterSelectParamVo);
  
  List<ModuleDeployEnvDTO> selectAllCenterModule(Set<Integer> envIds);
  
  ResponseDTO delInfoById(Integer id);
  
  ResponseDTO updateCenterInfo(ModuleCenter moduleCenter);
  
  ResponseDTO insertOneInfo(ModuleCenter moduleCenter);
  
  List<ModuleCenter> selectAllCenterByEnvId(Integer envId, String userId);
  
}
