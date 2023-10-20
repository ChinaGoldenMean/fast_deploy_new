package com.xc.fast_deploy.vo.k8s_vo;

import lombok.Data;

@Data
public class K8sStrategyParamVO {
  
  private Integer maxSurge;
  
  private Integer maxUnavailable;
}
