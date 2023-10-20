package com.xc.fast_deploy.mycallback;

import com.xc.fast_deploy.dao.master_dao.ModuleDeployMapper;
import com.xc.fast_deploy.model.master_model.ModuleDeploy;
import io.kubernetes.client.openapi.ApiCallback;
import io.kubernetes.client.openapi.ApiException;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.Map;

@Slf4j
public class MyOwnApiCallback<T> implements ApiCallback<T> {
  
  private Integer deployId;
  
  private ModuleDeployMapper deployMapper;
  
  public MyOwnApiCallback(Integer deployId, ModuleDeployMapper deployMapper) {
    this.deployId = deployId;
    this.deployMapper = deployMapper;
  }
  
  @Override
  public void onFailure(ApiException e, int i, Map map) {
    log.info("fail up");
    ModuleDeploy moduleDeploy = deployMapper.selectByPrimaryKey(deployId);
    if (moduleDeploy != null) {
      moduleDeploy.setDeployStatus("fail");
      moduleDeploy.setIsDeployed(0);
      deployMapper.updateByPrimaryKeySelective(moduleDeploy);
    }
  }
  
  @Override
  public void onSuccess(Object o, int i, Map map) {
    log.info("success deploy module");
    ModuleDeploy moduleDeploy = deployMapper.selectByPrimaryKey(deployId);
    if (moduleDeploy != null) {
      moduleDeploy.setDeployStatus("successs");
      moduleDeploy.setIsDeployed(1);
      moduleDeploy.setLastDeployTime(new Date());
      deployMapper.updateByPrimaryKeySelective(moduleDeploy);
    }
  }
  
  @Override
  public void onUploadProgress(long l, long l1, boolean b) {
    log.info("onUploadProgress l: {} l1: {} b:{}", l, l1, b);
  }
  
  @Override
  public void onDownloadProgress(long l, long l1, boolean b) {
    log.info("downloadprogress");
  }
}
