package com.xc.fast_deploy.model.master_model;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.Date;

@Data
public class ModuleYamlDiff {
  
  private Integer id;
  private String envName;
  private String yamlName;
  private String diffArgs;
  @JSONField(format = "yyyy-MM-dd HH:mm:ss")
  private Date createTime;
}
