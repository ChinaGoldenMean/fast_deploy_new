package com.xc.fast_deploy.vo.module_vo.param;

import lombok.Data;

import java.util.Set;

@Data
public class ModuleMirrorSelectParamVo {
  
  private Integer moduleId;
  
  private String nameCode;
  
  private Integer envId;
  
  private String beginTime;
  
  private String endTime;
  
  private Set<Integer> envIds;
  
  private Integer size;
  
  private String opUserId;
  
  private Integer isUsed;
  
}
