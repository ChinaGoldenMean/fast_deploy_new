package com.xc.fast_deploy.dao.master_dao;

import com.xc.fast_deploy.dto.module.ModulePackageCertDTO;
import com.xc.fast_deploy.dto.module.ModulePackageDTO;
import com.xc.fast_deploy.model.master_model.ModulePackage;

import java.util.List;

import com.xc.fast_deploy.model.master_model.example.ModulePackageExample;
import com.xc.fast_deploy.vo.module_vo.ModulePackageVo;
import org.apache.ibatis.annotations.Param;

public interface ModulePackageMapperModule extends ModuleBaseMapper<ModulePackage, Integer> {
  int countByExample(ModulePackageExample example);
  
  int deleteByExample(ModulePackageExample example);
  
  int deleteByPrimaryKey(Integer id);
  
  int insert(ModulePackage record);
  
  int insertSelective(ModulePackage record);
  
  List<ModulePackage> selectByExample(ModulePackageExample example);
  
  int findBranch(Integer id);
  
  ModulePackage selectByPrimaryKey(Integer id);
  
  int updateByExampleSelective(@Param("record") ModulePackage record, @Param("example") ModulePackageExample example);
  
  int updateByExample(@Param("record") ModulePackage record, @Param("example") ModulePackageExample example);
  
  int updateByPrimaryKeySelective(ModulePackage record);
  
  int updateByPrimaryKey(ModulePackage record);
  
  //self add
  
  int insertBatch(List<ModulePackage> list);
  
  Integer selectIdFirst();
  
  Integer updateVal(Integer val);
  
  ModulePackageVo selectCertInfoById(Integer packageId);
  
  List<ModulePackageDTO> selectPackageInfoByModuleId(Integer moduleId);
  
  Integer selectEnvIdByPackageId(Integer packageId);
  
  ModulePackageCertDTO selectAllInfoWithCert();
}