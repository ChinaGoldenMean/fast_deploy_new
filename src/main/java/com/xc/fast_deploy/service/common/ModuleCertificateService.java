package com.xc.fast_deploy.service.common;

import com.xc.fast_deploy.model.master_model.ModuleCertificate;

import java.util.List;

public interface ModuleCertificateService extends BaseService<ModuleCertificate, Integer> {
  
  Integer CountById(Integer id);
  
  ModuleCertificate selectSvnCertByModuleId(Integer moduleId);
  
  //查询Certificate表的所有name和username；作为env添加下拉列表
  List<ModuleCertificate> selectCertAll();
  
  //对数据做删除标记
  boolean updateById(Integer id);
  
  //查询全部数据
  List<ModuleCertificate> getAllModuleCer();
  
  //分类型查看凭证
  List<ModuleCertificate> getTypeModuleCer(Integer type);
  
}
