package com.xc.fast_deploy.model.master_model;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class ModulePermissionUser implements Serializable {
  private static final long serialVersionUID = 6724658253036412010L;
  
  private Integer id;
  
  private String userId;
  
  private String userPermissionInfo;
  
  @JSONField(format = "yyyy-MM-dd HH:mm:ss")
  private Date createTime;
  
}
