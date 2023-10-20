package com.xc.fast_deploy.dto.module;

import com.xc.fast_deploy.model.master_model.ModulePackage;
import lombok.Data;

@Data
public class ModulePackageDTO extends ModulePackage {
  
  private Integer moduleType;
  
  private String moduleName;
  
  private String moduleContentName;
  
  private String moduleProjectCode;
  
}
