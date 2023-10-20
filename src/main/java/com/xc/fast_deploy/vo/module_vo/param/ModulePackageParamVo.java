package com.xc.fast_deploy.vo.module_vo.param;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Data
public class ModulePackageParamVo implements Serializable {
  
  private static final long serialVersionUID = -3631285466372368624L;
  @NotBlank
  private String contentName;
  
  //如果是svn 表示svnUrl  如果是程序包 表示包名
  @NotBlank
  private String codeUrl;
  
  private Integer isPomFolder;
  
  private String gitBranch;
  
}
