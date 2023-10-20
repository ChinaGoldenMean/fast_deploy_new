package com.xc.fast_deploy.dto.module;

import com.xc.fast_deploy.model.slave_model.BillingPodDeploy;
import lombok.Data;

@Data
public class BillingPodDeployDTO extends BillingPodDeploy {
  
  private String imageName;
  
  private String centerName;
  
}
