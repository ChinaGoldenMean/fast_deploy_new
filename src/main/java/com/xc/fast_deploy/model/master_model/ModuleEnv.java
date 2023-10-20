package com.xc.fast_deploy.model.master_model;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.Date;

@Data
public class ModuleEnv {
  
  private Integer id;
  
  private String envName;
  
  private String envCode;
  
  private Integer certificateId;
  
  private String username;
  
  private String paasname;
  
  private String name;
  
  private String harborUrl;
  
  private Integer paasId;
  
  private String k8sConfig;
  
  private String mvnProfile;
  
  private Integer isNeedNodeSelector;
  
  private Integer isResourceLimit;
  
  private Integer isProd;
  
  private String regionName;
  
  @JSONField(format = "yyyy-MM-dd HH:mm:ss")
  private Date createTime;
  
  @JSONField(format = "yyyy-MM-dd HH:mm:ss")
  private Date updateTime;
  
}