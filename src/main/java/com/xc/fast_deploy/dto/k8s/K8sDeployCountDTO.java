package com.xc.fast_deploy.dto.k8s;

import lombok.Data;

import java.util.Map;

@Data
public class K8sDeployCountDTO {
  
  private int deployAllModuleCount;
  private int deployNowModuelCount;
  private String incrPercent;
  
  private int nodeCount;
  private int runNodeCount;
  private int busyNodeCount;
  private int podCount;
  
  private Map<String, Integer> centerCountMap;
  
}
