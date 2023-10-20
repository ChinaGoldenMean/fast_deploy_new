package com.xc.fast_deploy.model.slave_model;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.Date;

@Data
public class BillingOpOnOff {
  private Integer id;
  
  private Integer isAbleNext;
  
  private Integer envId;
  
  private String envCode;
  
  private String opUserId;
  
  private Integer isProd;
  
  private String hostnames;
  
  @JSONField(format = "yyyy-MM-dd HH:mm:ss")
  private Date opTime;
  
}