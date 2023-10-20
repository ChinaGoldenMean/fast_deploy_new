package com.xc.fast_deploy.dto.module;

import com.xc.fast_deploy.model.master_model.ModuleMirror;
import lombok.Data;

import java.io.Serializable;

@Data
public class ModuleMirrorDTO extends ModuleMirror implements Serializable {
  
  private static final long serialVersionUID = 4218806159167394188L;
  
  private String moduleName;
  
  private String envName;
  
  private String jobName;
  
  private Integer centerId;

//    private boolean isUsed;
}
