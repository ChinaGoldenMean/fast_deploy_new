package com.xc.fast_deploy.vo.module_vo;

import com.xc.fast_deploy.model.master_model.ModuleDeploy;
import lombok.Data;

import java.util.Set;

@Data
public class ModuleDeployVo extends ModuleDeploy {
  
  private Set<Integer> envIds;
  
  private String keyName;
  
  private Set<Integer> moduleIds;
  
  private Set<Integer> centerIds;

//    private boolean isNeedJobId;

}
