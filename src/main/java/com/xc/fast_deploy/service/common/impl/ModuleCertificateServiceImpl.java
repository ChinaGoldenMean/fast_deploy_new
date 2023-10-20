package com.xc.fast_deploy.service.common.impl;

import com.xc.fast_deploy.dao.master_dao.ModuleCertificateMapper;
import com.xc.fast_deploy.model.master_model.ModuleCertificate;
import com.xc.fast_deploy.model.master_model.example.ModuleCertificateExample;
import com.xc.fast_deploy.myException.ValidateExcetion;
import com.xc.fast_deploy.service.common.ModuleCertificateService;
import com.xc.fast_deploy.utils.encyption_utils.EncryptUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class ModuleCertificateServiceImpl extends BaseServiceImpl<ModuleCertificate, Integer> implements ModuleCertificateService {
  
  @Autowired
  private ModuleCertificateMapper certificateMapper;
  
  @PostConstruct
  public void init() {
    super.init(certificateMapper);
    
  }
  
  @Override
  public Integer CountById(Integer id) {
    ModuleCertificateExample example = new ModuleCertificateExample();
    ModuleCertificateExample.Criteria criteria = example.createCriteria();
    criteria.andIdEqualTo(id);
    return certificateMapper.countByExample(example);
  }
  
  @Override
  public ModuleCertificate selectSvnCertByModuleId(Integer moduleId) {
    return certificateMapper.selectCertByModuleId(moduleId);
  }
  
  /**
   * 查询Certificate表的所有name和username；作为env添加下拉列表
   */
  @Override
  public List<ModuleCertificate> selectCertAll() {
    List<ModuleCertificate> moduleCertificates = certificateMapper.selectCertAll();
    return moduleCertificates;
  }
  
  /**
   * 对数据做删除标记
   *
   * @param id
   * @return
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  public boolean updateById(Integer id) {
    boolean b = certificateMapper.updateById(id);
    if (b) {
      return b;
    } else {
      return false;
    }
  }
  
  /**
   * 查询全部
   *
   * @return
   */
  @Override
  public List<ModuleCertificate> getAllModuleCer() {
    List<ModuleCertificate> certificateList = certificateMapper.getAllModuleCer();
    if (certificateList != null && certificateList.size() > 0) {
      for (ModuleCertificate certificate : certificateList) {
        certificate.setPassword("*******");
      }
    }
    return certificateList;
  }
  
  /**
   * 根据type查询
   *
   * @param type
   * @return
   */
  @Override
  public List<ModuleCertificate> getTypeModuleCer(Integer type) {
    return certificateMapper.getTypeModuleCer(type);
  }
  
  /**
   * 插入一条数据
   *
   * @param moduleCertificate
   * @return
   */
  @Transactional(rollbackFor = Exception.class)
  public int insert(ModuleCertificate moduleCertificate) {
    ModuleCertificate moduleCertificate1 = certificateMapper.selectByName(moduleCertificate.getName());
    if (moduleCertificate1 != null) {
      return 0;
    } else {
      moduleCertificate.setPassword(EncryptUtil.encrpt(moduleCertificate.getPassword()));
      moduleCertificate.setCreateTime(new Date());
      moduleCertificate.setUpdateTime(new Date());
      moduleCertificate.setIsDeleted(0);
      return certificateMapper.insert(moduleCertificate);
    }
  }
  
  /**
   * 更新一条数据
   *
   * @param moduleCertificate
   * @return
   */
  @Transactional(rollbackFor = Exception.class)
  public int update(ModuleCertificate moduleCertificate) {
    int i = 0;
    if (moduleCertificate.getId() != null) {
      ModuleCertificate certificate = certificateMapper.selectByPrimaryKey(moduleCertificate.getId());
      if (certificate != null) {
        ModuleCertificateExample certificateExample = new ModuleCertificateExample();
        certificateExample.createCriteria().andIdNotEqualTo(moduleCertificate.getId()).andNameEqualTo(moduleCertificate.getName());
        List<ModuleCertificate> certificates = certificateMapper.selectByExample(certificateExample);
        if (certificates != null && certificates.size() > 0) {
          throw new ValidateExcetion("凭证名已存在");
        }
        moduleCertificate.setUpdateTime(new Date());
        if (StringUtils.isNotBlank(moduleCertificate.getPassword()) && !"******".equals(moduleCertificate.getPassword())) {
          moduleCertificate.setPassword(EncryptUtil.encrpt(moduleCertificate.getPassword()));
        }
        i = certificateMapper.updateByPrimaryKeySelective(moduleCertificate);
      }
    }
    return i;
  }
  
}
