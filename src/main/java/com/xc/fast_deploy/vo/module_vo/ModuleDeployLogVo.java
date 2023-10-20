package com.xc.fast_deploy.vo.module_vo;

import com.xc.fast_deploy.model.master_model.ModuleDeployLog;
import lombok.Data;

@Data
public class ModuleDeployLogVo extends ModuleDeployLog {
  
  private Integer centerId;
  
  private String centerName;
}
