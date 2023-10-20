package com.xc.fast_deploy.service.common.impl;

import com.xc.fast_deploy.dao.master_dao.ModuleUserMapper;

import com.xc.fast_deploy.model.master_model.ModuleUser;
import com.xc.fast_deploy.model.master_model.example.ModuleUserExample;
import com.xc.fast_deploy.service.common.TestServcice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TestServiceImpl implements TestServcice {
  
  @Autowired
  private ModuleUserMapper userMapper;
  
  @Override
  @Transactional(rollbackFor = Exception.class)
  public int testInsert(ModuleUser user) {
    return userMapper.insert(user);
  }
  
  public List<ModuleUser> testSelectAll() {
    ModuleUserExample userExample = new ModuleUserExample();
    List<ModuleUser> moduleUsers = userMapper.selectByExample(userExample);
    return moduleUsers;
  }
  
  @Override
  @Async
  public void doSomething() {
    System.out.println("开始调用异步任务");
    try {
      Thread.sleep(1000 * 3);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    System.out.println("异步任务调用结束");
  }
  
}

