package com.xc.fast_deploy.vo.module_vo;

import com.alibaba.fastjson.annotation.JSONField;
import com.xc.fast_deploy.model.master_model.ModuleManage;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class ModuleMangeVo extends ModuleManage implements Serializable {
  
  private static final long serialVersionUID = 6678344887721988516L;
  
  private Integer moduleId;
  
  private String centerName;
  
  private String childCenterName;
  
  private String username;
  
  @JSONField(format = "yyyy-MM-dd HH:mm:ss")
  private Date createTime;
  
}
