package com.xc.fast_deploy.dto.module;

import com.alibaba.fastjson.annotation.JSONField;
import com.xc.fast_deploy.model.master_model.ModuleDeployLog;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class ModuleDeployStatisticsDTO extends ModuleDeployLog implements Serializable {
  private String mark;
  
  private String centerName;
  
  private String moduleName;
  
  private Integer total;
  
  private Integer envId;
  
  @JSONField(format = "yyyy-MM-dd HH:mm:ss")
  private Date createTime;
  
  @JSONField(format = "yyyy-MM-dd HH:mm:ss")
  private Date changeTime;
  
}
