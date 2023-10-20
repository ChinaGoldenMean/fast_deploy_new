package com.xc.fast_deploy.dto.k8s;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Data
public class K8sServiceDTO implements Serializable {
  
  private static final long serialVersionUID = -2636186113959980462L;
  
  private String name;
  
  private String clusterIP;
  
  private List<String> externalIPs;
  
  private String type;
  
  private Map<String, String> selector;
  
  private List<K8sServicePortDTO> servicePortDTOList;
  
}
