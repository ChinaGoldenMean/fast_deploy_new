package com.xc.fast_deploy.dto.module;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ModuleDeployEnvDTO implements Serializable {
  
  private static final long serialVersionUID = 8487556565384877816L;
  
  private Integer envId;
  
  private String envCode;
  
  private String envName;
  
  private List<ModuleCenterEnvDTO> centerList;
  
}
