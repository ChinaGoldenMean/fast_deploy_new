package com.xc.fast_deploy.vo.module_vo;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class ModuleEnvManageMirrorVo implements Serializable {
  
  private static final long serialVersionUID = -4890245871037039642L;
  
  private Integer envId;
  
  private String k8sConfig;
  
  private String envCode;
  
  private Integer moduleId;
  
  private String moduleName;
  
  private Integer mirrorId;
  
  private String mirrorName;
  
  @JSONField(format = "yyyy-MM-dd HH:mm:ss")
  private Date updateTime;
  
}
