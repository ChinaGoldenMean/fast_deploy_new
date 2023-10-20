package com.xc.fast_deploy.utils.k8s.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class ClusterDTO implements Serializable {
  
  private String name;
  private ClusterVO cluster;
}
