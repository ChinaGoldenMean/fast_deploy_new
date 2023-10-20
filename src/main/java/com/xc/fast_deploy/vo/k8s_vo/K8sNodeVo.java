package com.xc.fast_deploy.vo.k8s_vo;

import lombok.Data;

import java.util.Map;

@Data
public class K8sNodeVo {
  
  private Integer envId;
  private String nodeName;
  private Map<String, String> labelMap;
}
