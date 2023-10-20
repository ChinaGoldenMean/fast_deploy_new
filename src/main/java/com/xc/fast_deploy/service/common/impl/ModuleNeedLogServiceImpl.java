package com.xc.fast_deploy.service.common.impl;

import com.xc.fast_deploy.dao.master_dao.ModuleNeedLogMapper;
import com.xc.fast_deploy.model.master_model.ModuleNeedLog;
import com.xc.fast_deploy.service.common.ModuleNeedLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
@Slf4j
public class ModuleNeedLogServiceImpl extends BaseServiceImpl<ModuleNeedLog, Integer> implements ModuleNeedLogService {
  
  @Autowired
  private ModuleNeedLogMapper needLogMapper;
  
  @PostConstruct
  public void init() {
    super.init(needLogMapper);
  }
}
