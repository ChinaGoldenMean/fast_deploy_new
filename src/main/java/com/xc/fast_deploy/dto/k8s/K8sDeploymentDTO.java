package com.xc.fast_deploy.dto.k8s;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class K8sDeploymentDTO implements Serializable {
  
  private static final long serialVersionUID = -27755069069224502L;
  private String deploymentName;
  private Integer replicas;
  private Integer availableReplicas;
  private Integer updatedReplicas;
  private List<K8sContainerDTO> containerDTOList;
  
}
