package com.xc.fast_deploy.dao.master_dao;

import com.xc.fast_deploy.model.master_model.ModuleCertificate;
import com.xc.fast_deploy.model.master_model.example.ModuleCertificateExample;

import java.util.List;

import org.apache.ibatis.annotations.Param;

public interface ModuleCertificateMapper extends BaseMapper<ModuleCertificate, Integer> {
  int countByExample(ModuleCertificateExample example);
  
  int deleteByExample(ModuleCertificateExample example);
  
  int deleteByPrimaryKey(Integer id);
  
  int insert(ModuleCertificate record);
  
  int insertSelective(ModuleCertificate record);
  
  List<ModuleCertificate> selectByExample(ModuleCertificateExample example);
  
  ModuleCertificate selectByPrimaryKey(Integer id);
  
  int updateByExampleSelective(@Param("record") ModuleCertificate record, @Param("example") ModuleCertificateExample example);
  
  int updateByExample(@Param("record") ModuleCertificate record, @Param("example") ModuleCertificateExample example);
  
  int updateByPrimaryKeySelective(ModuleCertificate record);
  
  int updateByPrimaryKey(ModuleCertificate record);
  
  //select all
  List<ModuleCertificate> selectAll();
  
  ModuleCertificate selectCertByModuleId(Integer moduleId);
  
  //查询Certificate表的所有name和username；作为env添加下拉列表
  List<ModuleCertificate> selectCertAll();
  
  //查询是否已有相同name的数据
  ModuleCertificate selectByName(String name);
  
  //对数据做删除标记
  boolean updateById(Integer id);
  
  //查询全部
  List<ModuleCertificate> getAllModuleCer();
  
  //根据类型查询
  List<ModuleCertificate> getTypeModuleCer(Integer type);
  
}