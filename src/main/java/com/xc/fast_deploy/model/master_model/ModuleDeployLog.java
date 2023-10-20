package com.xc.fast_deploy.model.master_model;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.Date;

@Data
public class ModuleDeployLog {
  
  private Integer id;
  
  private Integer deployId;
  
  private Integer moduleId;
  
  private Integer envId;
  
  private String opUserId;
  
  private String opActive;
  
  private String args;
  
  private Integer opResult;
  
  private String opUsername;
  
  @JSONField(format = "yyyy-MM-dd HH:mm:ss")
  private Date createTime;
  
}