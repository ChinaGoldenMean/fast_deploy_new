package com.xc.fast_deploy.dto.jenkins;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;

@Data
public class BuildDetailsDTO implements Serializable {
  
  private static final long serialVersionUID = 8466532450004812415L;
  
  private boolean building;
  
  private String description;
  
  private String displayName;
  
  private long duration;
  
  private long estimatedDuration;
  
  private String fullDisplayName;
  
  private String id;
  
  private long timestamp;
  
  private String result;
  
  private Map<String, String> parameters;
  
}
