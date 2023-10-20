package com.xc.fast_deploy.model.master_model;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class PModuleBasePermission implements Serializable {
  
  private static final long serialVersionUID = 6037420787202940444L;
  
  private Integer id;
  
  private String basePerName;
  
  private String basePerCode;
  
  private String remark;
  
  @JSONField(format = "yyyy-MM-dd HH:mm:ss")
  private Date createTime;
  
  @JSONField(format = "yyyy-MM-dd HH:mm:ss")
  private Date updateTime;
  
}