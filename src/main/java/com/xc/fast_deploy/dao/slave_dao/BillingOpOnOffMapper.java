package com.xc.fast_deploy.dao.slave_dao;

import com.xc.fast_deploy.model.slave_model.BillingOpOnOff;
import com.xc.fast_deploy.model.slave_model.example.BillingOpOnOffExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

public interface BillingOpOnOffMapper {
  int countByExample(BillingOpOnOffExample example);
  
  int deleteByExample(BillingOpOnOffExample example);
  
  int deleteByPrimaryKey(Integer id);
  
  int insert(BillingOpOnOff record);
  
  int insertSelective(BillingOpOnOff record);
  
  List<BillingOpOnOff> selectByExample(BillingOpOnOffExample example);
  
  BillingOpOnOff selectByPrimaryKey(Integer id);
  
  int updateByExampleSelective(@Param("record") BillingOpOnOff record, @Param("example") BillingOpOnOffExample example);
  
  int updateByExample(@Param("record") BillingOpOnOff record, @Param("example") BillingOpOnOffExample example);
  
  int updateByPrimaryKeySelective(BillingOpOnOff record);
  
  int updateByPrimaryKey(BillingOpOnOff record);
  
  List<BillingOpOnOff> selectAll();
  
  BillingOpOnOff selectByEnvId(Integer envId);
  
  int updateHostNamesByEnvId(BillingOpOnOff billingOpOnOff);
}