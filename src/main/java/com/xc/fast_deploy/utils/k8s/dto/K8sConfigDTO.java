package com.xc.fast_deploy.utils.k8s.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class K8sConfigDTO implements Serializable {
  private static final long serialVersionUID = 9163808214645146335L;
  
  private List<ClusterDTO> clusters;
  
  private List<UserDTO> users;
}
