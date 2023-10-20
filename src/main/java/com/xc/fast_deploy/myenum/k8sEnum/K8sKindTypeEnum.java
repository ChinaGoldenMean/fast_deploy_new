package com.xc.fast_deploy.myenum.k8sEnum;

public enum K8sKindTypeEnum {
  
  DEPLOYMENT(1, "Deployment"),
  POD(2, "pod"),
  SERVICE(3, "Service"),
  DAEMONSET(4, "DaemonSet"),
  REPLICASET(6, "ReplicaSet"),
  REPLICATIONCONTROLLER(7, "ReplicationController"),
  INGRESS(8, "Ingress"),
  CONFIG_MAP(9, "ConfigMap"),
  NODE(10, "Node"),
  STATEFULSET(11, "StatefulSet"),
  ENDPOINTS(12, "Endpoints");
  
  private Integer code;
  private String kindType;
  
  K8sKindTypeEnum(Integer code, String kindType) {
    this.code = code;
    this.kindType = kindType;
  }
  
  public Integer getCode() {
    return code;
  }
  
  public void setCode(Integer code) {
    this.code = code;
  }
  
  public String getKindType() {
    return kindType;
  }
  
  public void setKindType(String kindType) {
    this.kindType = kindType;
  }
  
  public static K8sKindTypeEnum getEnumByType(String kindType) {
    for (K8sKindTypeEnum k8sKindTypeEnum : values()) {
      if (k8sKindTypeEnum.getKindType().equals(kindType)) {
        return k8sKindTypeEnum;
      }
    }
    return null;
  }
  
  public static K8sKindTypeEnum getEnumByCode(Integer code) {
    for (K8sKindTypeEnum k8sKindTypeEnum : values()) {
      if (k8sKindTypeEnum.getCode().equals(code)) {
        return k8sKindTypeEnum;
      }
    }
    return null;
  }
}
