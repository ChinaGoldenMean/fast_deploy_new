package com.xc.fast_deploy.vo.k8s_vo;

import lombok.Data;

@Data
public class K8sNodeResourcesVO {
  private Integer up_clock;
  
  private Integer pods_cap;
  
  private String name;
  
  private String memory_cap;
  
  private String memory_limit;
  
  private String cpu_limit;
  
  private String cpu_request;
  
  private Integer pod_count;
  
  private String start_date;
  
  private String paasid;
  
  private String memory_request;
  
  private String address;
  
  private Integer cpu_cap;
  
  private String label;
  
  private Integer id;
  
  private Integer is_isolatior;
}
