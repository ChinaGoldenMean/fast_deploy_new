package com.xc.fast_deploy.model.master_model;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.Date;

@Data
public class ModuleCenter {
  
  private Integer id;
  
  private Integer envId;
  
  private String centerName;
  
  private String centerCode;
  
  private String remark;
  
  private String centerPath;
  
  private Integer isDeleted;
  
  private String childCenterName;
  
  private String childCenterContentName;
  
  @JSONField(format = "yyyy-MM-dd HH:mm:ss")
  private Date createTime;
  
  @JSONField(format = "yyyy-MM-dd HH:mm:ss")
  private Date updateTime;
  
}