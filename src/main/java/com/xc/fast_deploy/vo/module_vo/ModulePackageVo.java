package com.xc.fast_deploy.vo.module_vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class ModulePackageVo implements Serializable {
  private static final long serialVersionUID = 3870873133480616309L;

//    private String svnUrl;
  
  private String codeUrl;
  
  private String username;
  
  private String password;
  
  private Integer codeType;
  
  private String packagePathName;
  
  private Integer moduleId;
  
}
