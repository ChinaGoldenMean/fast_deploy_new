package com.xc.fast_deploy.model.master_model;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.Date;

@Data
public class PModuleRole {
  
  private Integer id;
  
  private String roleName;
  
  private String roleCode;
  
  @JSONField(format = "yyyy-MM-dd HH:mm:ss")
  private Date createTime;
  @JSONField(format = "yyyy-MM-dd HH:mm:ss")
  private Date updateTime;
  
}