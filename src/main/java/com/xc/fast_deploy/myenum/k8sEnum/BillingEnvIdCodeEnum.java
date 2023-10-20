package com.xc.fast_deploy.myenum.k8sEnum;

import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.Map;

public enum BillingEnvIdCodeEnum {
  
  PST_BILLING_ENV(22, "rate", "PST_ENV_CODE", "center:dockerfile1,cer:sdg"),
  MSIT_BILLING_ENV(1001, "rate", "MSIT_ENV_CODE", "center: dsdow"),
  PROD_BILLING_ENV(33, "rate", "PROD_ENV_CODE", "cent=34");
  
  private Integer envId;
  private String namespace;
  private String envCode;
  private String envNodeSelectorLabel;
  
  BillingEnvIdCodeEnum(Integer envId, String namespace, String envCode, String envNodeSelectorLabel) {
    this.envId = envId;
    this.namespace = namespace;
    this.envCode = envCode;
    this.envNodeSelectorLabel = envNodeSelectorLabel;
  }
  
  public String getNamespace() {
    return namespace;
  }
  
  public void setNamespace(String namespace) {
    this.namespace = namespace;
  }
  
  public Integer getEnvId() {
    return envId;
  }
  
  public void setEnvId(Integer envId) {
    this.envId = envId;
  }
  
  public String getEnvCode() {
    return envCode;
  }
  
  public void setEnvCode(String envCode) {
    this.envCode = envCode;
  }
  
  public Map<String, String> getEnvNodeSelectorLabel() {
    Map<String, String> labelMap = new HashMap<>();
    String[] splits = this.envNodeSelectorLabel.split(",");
    for (String spilt : splits) {
      String[] splitMap = spilt.split(":");
      labelMap.put(splitMap[0], splitMap[1]);
    }
    return labelMap;
  }
  
  public void setEnvNodeSelectorLabel(String envNodeSelectorLabel) {
    this.envNodeSelectorLabel = envNodeSelectorLabel;
  }
  
  public static BillingEnvIdCodeEnum getBillingEnvIdCodeEnumByCode(String envCode) {
    if (StringUtils.isNotBlank(envCode)) {
      for (BillingEnvIdCodeEnum billingEnum : values()) {
        if (envCode.equals(billingEnum.getEnvCode())) {
          return billingEnum;
        }
      }
    }
    return null;
  }
}
