package com.xc.fast_deploy.dto.k8s;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class K8sDaemonSetDTO implements Serializable {
  
  private static final long serialVersionUID = 5745962587573589993L;
  private String name;
  private Integer desiredNumber;
  private Integer currentNumber;
  private Integer readyNumber;
  private Integer availableNumber;
  private Integer updateNumber;
  private List<K8sContainerDTO> containerDTOList;
}
