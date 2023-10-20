package com.xc.fast_deploy.myenum.k8sEnum;

public enum K8sApiversionTypeEnum {
  
  EXTENSIONAPI("extensions/v1beta1"),
  COREAPIV1("v1"),
  APPV1("apps/v1"),
  APPS_V1VETA1("apps/v1beta1"),
  APPS_V1VETA2("apps/v1beta2"),
  NETWORKINGV1API("networking.k8s.io/v1");
  
  private String apiVersionType;
  
  K8sApiversionTypeEnum(String apiVersionType) {
    this.apiVersionType = apiVersionType;
  }
  
  public String getApiVersionType() {
    return apiVersionType;
  }
  
  public void setApiVersionType(String apiVersionType) {
    this.apiVersionType = apiVersionType;
  }
  
  public static K8sApiversionTypeEnum getEnumByType(String apiVersion) {
    
    for (K8sApiversionTypeEnum k8sApiversionTypeEnum : values()) {
      if (k8sApiversionTypeEnum.getApiVersionType().equals(apiVersion)) {
        return k8sApiversionTypeEnum;
      }
    }
    return null;
  }
}
