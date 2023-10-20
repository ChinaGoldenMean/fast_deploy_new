package com.xc.fast_deploy.vo.module_vo.param;

import lombok.Data;

import java.io.Serializable;
import java.util.Set;

@Data
public class ModuleJobSelectParamVo implements Serializable {
  
  private static final long serialVersionUID = -709892959324411029L;
  
  //模糊查询选择
  private String jobName;
  
  //模糊查询选择
  private String moduleName;
  
  private Integer envId;
  
  private Integer centerId;
  
  private Integer moduleType;
  
  private String userId;
  
  private String beginTime;
  
  private String endTime;
  
  private Set<Integer> envIds;
  
}
