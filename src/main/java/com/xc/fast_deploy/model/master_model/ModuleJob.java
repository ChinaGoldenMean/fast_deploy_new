package com.xc.fast_deploy.model.master_model;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class ModuleJob implements Serializable {
  
  private static final long serialVersionUID = 130596522082958047L;
  
  private Integer id;
  
  private String jobName;
  
  private Integer moduleId;
  
  private String crontabExpression;
  
  private String mirrorPrefix;
  
  private String compileFilePath;
  
  private String compileCommand;
  
  private String dockerfilePath;
  
  private Integer moduleEnvId;
  
  private Integer isDelete;
  
  private Integer status;
  
  @JSONField(format = "yyyy-MM-dd HH:mm:ss")
  private Date updateTime;
  
  @JSONField(format = "yyyy-MM-dd HH:mm:ss")
  private Date createTime;
  
}