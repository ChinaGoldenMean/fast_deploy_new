package com.xc.fast_deploy.model.master_model;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.Date;

@Data
public class ModuleDeployYaml {
  private Integer id;
  
  private String yamlName;
  
  private String yamlType;
  
  private String yamlPath;
  
  private String mirrorName;
  
  private Integer isOnlineYaml;
  
  private String containerName;
  
  private Integer isDeployed;
  
  private Integer deployId;
  
  private String yamlJson;
  
  private String yamlNamespace;
  
  @JSONField(format = "yyyy-MM-dd HH:mm:ss")
  private Date createTime;
  
  @JSONField(format = "yyyy-MM-dd HH:mm:ss")
  private Date updateTime;
  
}