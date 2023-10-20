package com.xc.fast_deploy.dto.module;

import com.xc.fast_deploy.model.master_model.ModuleDeploySelfConf;
import lombok.Data;

@Data
public class ModuleDeploySelfConfDTO extends ModuleDeploySelfConf {
  
  private String EnvName;
  
}
