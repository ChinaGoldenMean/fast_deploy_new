package com.xc.fast_deploy.service.common;

import com.xc.fast_deploy.dto.MyPageInfo;
import com.xc.fast_deploy.dto.module.ModuleMirrorDTO;
import com.xc.fast_deploy.model.master_model.ModuleMirror;
import com.xc.fast_deploy.vo.module_vo.ModuleEnvCenterManageVo;
import com.xc.fast_deploy.vo.module_vo.param.ModuleMirrorSelectParamVo;

import java.util.List;
import java.util.Set;

public interface ModuleMirrorService extends BaseService<ModuleMirror, Integer> {
  
  //更新生成的镜像信息:
//    void updateMirrorStatus(Integer mirrorId, Integer status);
  
  List<ModuleMirror> selectAvailableInfoByJobId(Integer jobId);
  
  boolean updateMirror(Integer mirrorId);
  
  List<ModuleMirror> selectAvailableInfoById(Integer moduleId, Integer envId);
  
  List<ModuleEnvCenterManageVo> selectCenManage(Integer envId, String moduleName);
  
  MyPageInfo<ModuleMirrorDTO> selectAvailInfoPageByParam(Integer pageNum, Integer pageSize,
                                                         ModuleMirrorSelectParamVo moduleMirrorSelectParamVo);
  
  boolean deleteMirrorInfo(Integer mirrorId);
  
  void clearNotInHaroborMirrorInfo();
  
  List<String> getHarborProjectCode(Integer envId);
  
  //根据jobid内容查询对应的生成的镜像信息
  List<ModuleMirror> selectByJobIds(Set<Integer> jobIds);
  
  int updateSelective(ModuleMirror moduleMirror);
  
  List<ModuleMirrorDTO> getPollingMirrorJobInfoByEnvId(Set<Integer> envIdSet, Integer size,
                                                       Integer isUsedSelect, String userIdFromToken);
  
  /**
   * 获取某个环境的某个模块的harbor仓库对应的mirrorlist
   *
   * @param envId
   * @param moduleId
   * @return
   */
  List<String> getHarborMirrorList(Integer envId, Integer moduleId);
  
  //清理对应环境的harbor仓库
  void clearHarborNotUsedMirror(Set<Integer> envIds);
}
