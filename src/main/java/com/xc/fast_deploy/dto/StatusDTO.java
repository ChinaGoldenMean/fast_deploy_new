package com.xc.fast_deploy.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class StatusDTO implements Serializable {
  private static final long serialVersionUID = -1656625930977233697L;
  private Integer jobId;
  
  private Integer moduleId;
  
  private Integer total;
  
  private Integer current;
  
  private Integer status;
  
  private boolean result;
  
  private String name;
  
  private String moduleName;
  
}
