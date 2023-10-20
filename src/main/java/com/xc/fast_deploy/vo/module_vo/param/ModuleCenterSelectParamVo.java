package com.xc.fast_deploy.vo.module_vo.param;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

@Data
public class ModuleCenterSelectParamVo implements Serializable {
  
  private static final long serialVersionUID = -5827767071723686149L;
  
  private Integer centerId;
  
  private Integer envId;
  
  private String nameCode;
  
  private String beginTime;
  
  private String endTime;
  
  private Set<Integer> envIds;
}
