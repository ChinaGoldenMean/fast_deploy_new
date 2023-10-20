package com.xc.fast_deploy.dto.module;

import com.xc.fast_deploy.model.master_model.ModuleDeployNeed;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ModuleDeployNeedDTO implements Serializable {
  
  private ModuleDeployNeed deployNeed;
  
  private List<String> moduleNameList;
}
