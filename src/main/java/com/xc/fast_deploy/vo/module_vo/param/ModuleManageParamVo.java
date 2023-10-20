package com.xc.fast_deploy.vo.module_vo.param;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.util.List;

@Data
public class ModuleManageParamVo implements Serializable {
  
  private static final long serialVersionUID = -3453941567847293286L;
  
  private String envId;
  
  private String moduleName;
  
  private String chargePerson;
  
  @NotBlank
  private String id;
  
  @NotBlank
  private String moduleContentName;
  
  @NotBlank
  private String moduleProjectCode;
  
  private String chargeTelephone;
  
  private String officalChargePerson;
  
  private String officalChargeTelephone;
  
  @NotEmpty
  @Pattern(regexp = "^[0-2]+$", message = "必须是数字 0,1,2")
  private String moduleType;
  
  @NotEmpty
  @Pattern(regexp = "^\\d+$", message = "必须是数字")
  private String centerId;
  
  @Pattern(regexp = "^\\d+$", message = "必须是数字")
  private String certificateId;
  
  @Pattern(regexp = "^[0-1]+$", message = "必须是数字 0,1")
  private String codeUpType;
  
  private String centerName;
  
  private List<ModulePackageParamVo> packageList;
  
  private List<String> programFileNameList;
  
  private String svnAutoUpUrl;
  
  private String gitBranch;
  
  private Integer moduleTypeCode;
  
}
