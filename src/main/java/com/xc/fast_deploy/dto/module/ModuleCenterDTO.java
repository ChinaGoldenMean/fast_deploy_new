package com.xc.fast_deploy.dto.module;

import com.xc.fast_deploy.model.master_model.ModuleManage;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ModuleCenterDTO extends ModuleCenterEnvDTO implements Serializable {
  
  private List<ModuleManage> manageList;
}
