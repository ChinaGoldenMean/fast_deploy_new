package com.xc.fast_deploy.dao.master_dao;

import com.xc.fast_deploy.dto.module.ModuleMirrorCertificateEnvDTO;
import com.xc.fast_deploy.dto.module.ModuleMirrorDTO;
import com.xc.fast_deploy.model.master_model.ModuleMirror;
import com.xc.fast_deploy.model.master_model.example.ModuleMirrorExample;

import java.util.List;
import java.util.Set;

import com.xc.fast_deploy.vo.module_vo.ModuleEnvCenterManageVo;
import com.xc.fast_deploy.vo.module_vo.ModuleEnvManageMirrorVo;
import com.xc.fast_deploy.vo.module_vo.param.ModuleMirrorSelectParamVo;
import org.apache.ibatis.annotations.Param;

public interface ModuleMirrorMapper extends BaseMapper<ModuleMirror, Integer> {
  int countByExample(ModuleMirrorExample example);
  
  int deleteByExample(ModuleMirrorExample example);
  
  int deleteByPrimaryKey(Integer id);
  
  int insert(ModuleMirror record);
  
  int insertSelective(ModuleMirror record);
  
  List<ModuleMirror> selectByExample(ModuleMirrorExample example);
  
  ModuleMirror selectByPrimaryKey(Integer id);
  
  int updateByExampleSelective(@Param("record") ModuleMirror record, @Param("example") ModuleMirrorExample example);
  
  int updateByExample(@Param("record") ModuleMirror record, @Param("example") ModuleMirrorExample example);
  
  int updateByPrimaryKeySelective(ModuleMirror record);
  
  int updateByPrimaryKey(ModuleMirror record);
  
  //最近的10条记录
  List<ModuleMirror> selectAvailableMirrorById(ModuleMirror moduleMirror);
  
  //查询manageName
  List<ModuleEnvCenterManageVo> selectCenManage(ModuleEnvCenterManageVo envCenterManageVo);
  
  //根据module_manage的id查询镜像
//    List<ModuleMirrorJobEnvVo> selectByModuleId(Integer moduleId);
  
  String selectLatestMirrorNameByModuleId(Integer moduleId);
  
  List<ModuleMirrorDTO> selectAvailInfoByParam(ModuleMirrorSelectParamVo moduleMirrorSelectParamVo);
  
  List<ModuleMirrorCertificateEnvDTO> selectAllAvailableMirrorInfo();
  
  ModuleMirrorCertificateEnvDTO selectMirrorInfoById(Integer mirrorId);
  
  List<ModuleMirror> selectByJobIds(Set<Integer> jobIds);
  
  List<ModuleMirrorDTO> selectLastestMirrorInfo(ModuleMirrorSelectParamVo selectParamVo);
  
  List<ModuleEnvManageMirrorVo> selectModuleMirrorLatestBatch(Set<Integer> moduleIds);
  
  //除了正在使用的镜像，将其他镜像的is_used字段设置成0,方便后期清理镜像
  int updateMirrorIsUsedByModuleId(Integer moduleId);
  
  List<ModuleMirrorCertificateEnvDTO> selectIsNotUsedMirror(Set<Integer> envIds);
  
}
