package com.xc.fast_deploy.service.slave;

import com.xc.fast_deploy.model.slave_model.BillingOpOnOff;

import java.util.List;
import java.util.Set;

public interface IBillingOpOnOffService {
  
  List<BillingOpOnOff> selectAll(Set<Integer> envIdSet);
  
  boolean updateAbleNext(Integer envId, Integer isAbleNext);
  
  Set<Integer> selectEnvIds(boolean isProdEnv);
  
  List<String> selectHostNamesByEnvId(Integer envId);
  
  boolean updateHostNames(Integer envId, List<String> hostnames);
  
  void CronUpdateAble();
}
