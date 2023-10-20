package com.xc.fast_deploy.vo.module_vo.param;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class ModuleDeployLogSelectParam {
  
  private List<String> opActives;
  
  private String beginTime;
  
  private String endTime;
  
  private Integer envId;
  
  private String moduleName;
  
}
