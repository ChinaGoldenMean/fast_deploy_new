package com.xc.fast_deploy.dto.module;

import com.xc.fast_deploy.model.master_model.ModulePackage;
import lombok.Data;

import java.io.Serializable;

@Data
public class ModulePackageCertDTO extends ModulePackage implements Serializable {
  
  private static final long serialVersionUID = 8917927871193681645L;
  private String username;
  private String password;
}
