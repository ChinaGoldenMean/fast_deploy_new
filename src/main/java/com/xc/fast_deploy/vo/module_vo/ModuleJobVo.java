package com.xc.fast_deploy.vo.module_vo;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class ModuleJobVo implements Serializable {
  private static final long serialVersionUID = -144460917374505284L;
  
  private Integer jobId;
  
  private String jobName;
  
  private String moduleName;
  
  private Integer moduleId;
  
  private Integer moduleType;
  
  private Integer envId;
  
  private String envName;
  
  private Integer centerId;
  
  private String centerName;
  
  private String childCenterName;
  
  private String status;
  
  private Integer nextBuildNumber;
  
  private String svnAutoUrl;
  
  @JSONField(format = "yyyy-MM-dd HH:mm:ss")
  private Date createTime;
  
  @JSONField(format = "yyyy-MM-dd HH:mm:ss")
  private Date updateTime;
}
