package com.xc.fast_deploy.vo;

import io.kubernetes.client.openapi.models.V1Pod;
import lombok.Data;

import java.io.Serializable;

@Data
public class V1PodVo implements Serializable {
  
  private static final long serialVersionUID = -7388593692752838473L;
  
  private V1Pod v1Pod;
  
  private String namespace;
  
  private String appName;
  
  private String envCode;
  
}
