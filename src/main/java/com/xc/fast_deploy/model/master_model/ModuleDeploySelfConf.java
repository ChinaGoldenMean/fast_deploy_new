package com.xc.fast_deploy.model.master_model;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.Date;

@Data
public class ModuleDeploySelfConf {
  private Integer id;
  
  private String resourceName;
  
  private String resourceKind;
  
  private String resourceFilePath;
  
  private Integer envId;
  
  private Integer resourceStatus;
  
  private String userId;
  
  private String resourceNamespace;
  
  @JSONField(format = "yyyy-MM-dd HH:mm:ss")
  private Date createTime;
  
  private String resourceJson;
}