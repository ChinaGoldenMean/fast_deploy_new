package com.xc.fast_deploy.dto.module;

import com.xc.fast_deploy.model.master_model.ModuleCenter;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ModuleCenterEnvDTO extends ModuleCenter implements Serializable {
  private static final long serialVersionUID = -7418349966486481688L;
  
  private Integer centerId;
  
  private String envCode;
  
  private String envName;
  
  private List<ModuleCenterDTO> centerDTOList;

//    private List<ModuleManage> manageList;

}
