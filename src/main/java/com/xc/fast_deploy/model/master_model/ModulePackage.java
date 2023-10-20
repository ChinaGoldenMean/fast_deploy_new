package com.xc.fast_deploy.model.master_model;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
public class ModulePackage {
  private Integer id;
  
  private String contentName;
  
  private String codeUrl;
  
  private String codeReversion;
  
  private Integer codeType;
  
  private String packagePathName;
  
  private Integer moduleId;
  
  private Integer isPomFolder;
  
  private String programFileName;
  
  private String gitBranch;
  
  @JSONField(format = "yyyy-MM-dd HH:mm:ss")
  private Date createTime;
  
}