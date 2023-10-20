package com.xc.fast_deploy.vo.module_vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class ModuleEnvVo implements Serializable {
  
  private static final long serialVersionUID = -7720116964148126113L;
  
  private String harborUrl;
  
  private String envName;
  
  private String envCode;
  
  private String username;
  
  private String password;
  
}
