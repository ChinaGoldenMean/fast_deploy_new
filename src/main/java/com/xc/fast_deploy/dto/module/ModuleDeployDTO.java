package com.xc.fast_deploy.dto.module;

import com.xc.fast_deploy.vo.module_vo.ModuleEnvCenterManageVo;
import lombok.Data;

import java.util.List;

@Data
public class ModuleDeployDTO extends ModuleEnvCenterManageVo {
  
  private Integer deployId;
  
  private List<ModuleDeployYamlDTO> moduleDeployYamls;
  
}
