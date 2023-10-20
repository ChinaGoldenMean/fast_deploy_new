package com.xc.fast_deploy.vo.module_vo.param;

import lombok.Data;

import java.io.Serializable;
import java.util.Set;

@Data
public class ModuleManageSelectParamVo implements Serializable {
  
  private static final long serialVersionUID = 3718600599489872748L;
  
  private String moduleName;
  
  private Integer centerId;
  
  private Integer moduleType;
  
  private Integer userId;
  
  private String beginTime;
  private String endTime;
  
  private Integer envId;
  
  private Set<Integer> envIds;
  
  private int topCount;
}

