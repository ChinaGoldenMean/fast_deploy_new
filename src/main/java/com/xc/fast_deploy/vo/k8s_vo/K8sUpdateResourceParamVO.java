package com.xc.fast_deploy.vo.k8s_vo;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class K8sUpdateResourceParamVO {
  private Integer envId;
  private Integer moduleId;
  private Integer argsType;
  private String limitMemory;
  private String limitCPU;
  private String labelKey;
  private String labelValue;
  private String metadataLabelsValue;
  private List<Map<String, String>> volumeMapList;
}
