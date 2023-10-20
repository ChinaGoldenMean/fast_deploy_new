package com.xc.fast_deploy.websocketConfig.myThread;

import com.alibaba.fastjson.JSONObject;
import com.xc.fast_deploy.dto.StatusDTO;
import com.xc.fast_deploy.service.common.ModulePackageService;
import lombok.extern.slf4j.Slf4j;
import org.yeauty.pojo.Session;

@Slf4j
public class UpdateCodeThread implements Runnable {
  
  private Integer moduleId;
  private ModulePackageService packageService;
  private Session session;
  
  public UpdateCodeThread(Integer moduleId, ModulePackageService packageService, Session session) {
    this.moduleId = moduleId;
    this.packageService = packageService;
    this.session = session;
    
  }
  
  @Override
  public void run() {
    boolean b = false;
    try {
      b = packageService.updateModuleAllCode(this.moduleId, this.session);
    } catch (Exception e) {
      session.sendText(e.getMessage());
      log.error("run job error: {}", e.getMessage());
      e.printStackTrace();
    }
    log.info("当前线程 :{} 执行状态: {}", Thread.currentThread().getName(), b);
    StatusDTO statusDTO = new StatusDTO();
    statusDTO.setResult(b);
    statusDTO.setStatus(1);
    statusDTO.setModuleId(moduleId);
    try {
      this.session.sendText(JSONObject.toJSONString(statusDTO)).sync();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}
