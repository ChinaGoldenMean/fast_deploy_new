package com.xc.fast_deploy.vo.module_vo.param;

import lombok.Data;
import org.apache.commons.lang.StringUtils;

import java.io.Serializable;
import java.util.Set;

@Data
public class ModuleNeedSelectParamVo implements Serializable {
  
  private String beginTime;
  
  private String endTime;
  
  private String searchKey;
  
  private String developer;
  
  private Integer status;
  
  private Integer envId;
  
  private Set<Integer> envIds;
  
}
