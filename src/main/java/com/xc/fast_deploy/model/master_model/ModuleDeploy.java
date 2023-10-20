package com.xc.fast_deploy.model.master_model;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.Date;

@Data
public class ModuleDeploy {
  private Integer id;
  
  private String deployName;
  
  private Integer envId;
  
  private Integer moduleId;
  
  private String genernateName;
  
  private Integer isDeployed;
  
  private String deployStatus;
  
  private Date lastDeployTime;
  
  private Integer isDelete;
  
  @JSONField(format = "yyyy-MM-dd HH:mm:ss")
  private Date createTime;
  @JSONField(format = "yyyy-MM-dd HH:mm:ss")
  private Date updateTime;
  
}