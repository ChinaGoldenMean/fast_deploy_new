package com.xc.fast_deploy.dto.module;

import com.xc.fast_deploy.model.master_model.ModuleEnv;
import lombok.Data;

import java.io.Serializable;

@Data
public class ModuleEnvDTO extends ModuleEnv implements Serializable {
  
  private static final long serialVersionUID = -6145358153354457583L;
  
  private String certificateName;
  
}
