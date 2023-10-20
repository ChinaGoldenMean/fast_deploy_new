package com.xc.fast_deploy.utils.k8s.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class ClusterVO implements Serializable {
  
  private static final long serialVersionUID = 3886609240081575338L;
  
  private String certificateAuthorityData;
  private String server;
}
