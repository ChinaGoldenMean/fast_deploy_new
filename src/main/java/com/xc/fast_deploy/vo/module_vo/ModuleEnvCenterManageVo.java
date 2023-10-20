package com.xc.fast_deploy.vo.module_vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = true)
public class ModuleEnvCenterManageVo extends ModuleCenterManageVo implements Serializable {
  
  private static final long serialVersionUID = -2992133243536120520L;
  
  private Integer envId;
  
  private String envName;
  
  private String envCode;
  
}
