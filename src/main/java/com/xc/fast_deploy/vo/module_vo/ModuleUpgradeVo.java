package com.xc.fast_deploy.vo.module_vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author litiewang
 * @Date 2022/5/31 15:23
 * @Version 1.0
 */
@Data
public class ModuleUpgradeVo implements Serializable {
  private static final long serialVersionUID = -144460917374505284L;
  
  private Integer needId;
  private Integer moduleId;
  private Integer envId;
}
