package com.xc.fast_deploy.model.master_model;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.Date;

@Data
public class ModuleNeedLog {
  
  private Integer id;
  
  private Integer needId;
  
  private String opActive;
  
  private String opResult;
  
  private String opUser;
  
  private String opArgs;
  
  @JSONField(format = "yyyy-MM-dd HH:mm:ss")
  private Date opTime;
}
