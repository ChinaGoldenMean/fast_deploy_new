package com.xc.fast_deploy.model.slave_model;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.Date;

@Data
public class BillingPodDeploy {
  private Long id;
  
  private String podname;
  
  private String appname;
  
  private String imagename;
  
  private Integer envId;
  
  private String envCode;
  
  private Integer isDelete;
  
  private Integer createResult;
  
  @JSONField(format = "yyyy-MM-dd HH:mm:ss")
  private Date createTime;
  
  @JSONField(format = "yyyy-MM-dd HH:mm:ss")
  private Date updateTime;
  
  private String podArgs;
  
  private String createResultInfo;
  
}