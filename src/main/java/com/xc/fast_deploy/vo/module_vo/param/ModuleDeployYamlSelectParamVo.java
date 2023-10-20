package com.xc.fast_deploy.vo.module_vo.param;

import lombok.Data;

import java.io.Serializable;
import java.util.Set;

@Data
public class ModuleDeployYamlSelectParamVo implements Serializable {
  
  private static final long serialVersionUID = -6887611920430724747L;
  
  private Integer envId;
  
  private Integer centerId;
  
  private String yamlType;
  
  private Integer moduleType;
  
  //关键词查询 匹配 moduleName yamlName 模糊查询
  private String keyName;

//    private Integer isOnlineYaml;
  
  private String beginTime;
  
  private String endTime;
  
  private Set<Integer> envIds;
  
}
