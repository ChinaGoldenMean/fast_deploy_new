package com.xc.fast_deploy.service.common;

import com.xc.fast_deploy.model.master_model.ModuleUser;

import java.util.List;

public interface TestServcice {
  
  int testInsert(ModuleUser user);
  
  List<ModuleUser> testSelectAll();
  
  void doSomething();
  
}
