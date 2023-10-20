package com.xc.fast_deploy.dto.module;

import com.xc.fast_deploy.model.master_model.ModuleDeployYaml;
import lombok.Data;

import java.io.Serializable;

@Data
public class ModuleDeployYamlDTO extends ModuleDeployYaml implements Serializable {
  
  private static final long serialVersionUID = -5824679791724166832L;
  
  private Integer deployYamlId;
  
  private Integer moduleId;
  
  private String childCenterName;
  
  private String moduleName;
  
  private Integer moduleType;
  
  private String envName;
  
  private String envCode;
  
  private String deployName;
  
  private Object fileObject;
  
  private boolean canEditYaml;
  
}
