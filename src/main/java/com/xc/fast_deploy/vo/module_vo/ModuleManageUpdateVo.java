package com.xc.fast_deploy.vo.module_vo;

import com.xc.fast_deploy.vo.module_vo.param.ModuleManageParamVo;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

@Data
public class ModuleManageUpdateVo extends ModuleManageParamVo {
  
  @NotEmpty
  @Pattern(regexp = "^\\d+$", message = "必须是数字")
  private String moduleId;
  
}
