package com.xc.fast_deploy.vo.module_vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Objects;

@Data
public class ModuleCenterManageVo implements Serializable {
  
  private Integer centerId;
  
  private String centerName;
  
  private String childCenterName;
  
  private Integer moduleId;
  
  private String moduleName;
  
  private String moduleContentName;
  
  private Integer jobId;
  
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof ModuleCenterManageVo)) return false;
    ModuleCenterManageVo that = (ModuleCenterManageVo) o;
    return moduleId.equals(that.moduleId);
  }
  
  @Override
  public int hashCode() {
    return Objects.hash(moduleId);
  }
}
