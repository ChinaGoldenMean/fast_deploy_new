package com.xc.fast_deploy.dao.master_dao;

import com.xc.fast_deploy.model.master_model.ModuleBuildInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ModuleBuildInfoMapper extends ModuleBaseMapper<ModuleBuildInfo, Integer> {
  
  int insertBuildInfo(@Param("creator") String creator, @Param("lastBuildStatusPrev") String lastBuildStatusPrev, @Param("currentBuildStatus") String currentBuildStatus, @Param("moduleId") Integer moduleId, @Param("envId") Integer envId);
  
  List<ModuleBuildInfo> listBuildInfo(@Param("moduleId") Integer moduleId, @Param("envId") Integer envId);
}