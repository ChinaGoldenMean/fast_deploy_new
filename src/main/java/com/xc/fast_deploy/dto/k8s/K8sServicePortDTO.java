package com.xc.fast_deploy.dto.k8s;

import lombok.Data;

import java.io.Serializable;

@Data
public class K8sServicePortDTO implements Serializable {
  
  private static final long serialVersionUID = -6399926730384677570L;
  private String name;
  private Integer nodePort;
  private String protocol;
  private String targetPort;
  private Integer port;
  
}
