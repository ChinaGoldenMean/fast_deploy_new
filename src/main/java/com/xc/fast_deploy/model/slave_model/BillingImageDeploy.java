package com.xc.fast_deploy.model.slave_model;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.Date;

@Data
public class BillingImageDeploy {
  private Long id;
  
  private String tarName;
  
  private String imageName;
  
  private Integer envId;
  
  private String envCode;
  
  private String centerName;
  
  private Integer isAvailable;
  
  private Integer isReviewed;
  
  @JSONField(format = "yyyy-MM-dd HH:mm:ss")
  private Date reviewTime;
  
  @JSONField(format = "yyyy-MM-dd HH:mm:ss")
  private Date createTime;
  
  @JSONField(format = "yyyy-MM-dd HH:mm:ss")
  private Date updateTime;
  
}