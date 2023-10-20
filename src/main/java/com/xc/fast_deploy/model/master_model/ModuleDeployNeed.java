package com.xc.fast_deploy.model.master_model;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;

import java.io.Serializable;
import java.util.Date;

@Data
public class ModuleDeployNeed implements Serializable {
  
  private Integer id;
  
  private Integer envId;
  
  private String envName;
  
  private Integer approveStatus;
  
  private Integer dr;
  
  private Integer pstTest;
  
  private Integer drTest;
  
  private String needNumber;
  
  private String needContent;
  
  private String needDescribe;
  
  private String developer;
  
  private String testReportPath;
  
  private String approver;
  
  @JSONField(format = "yyyy-MM-dd HH:mm:ss")
  private Date approveTime;
  
  @JSONField(format = "yyyy-MM-dd HH:mm:ss")
  private Date deployTime;
  
  @JSONField(format = "yyyy-MM-dd HH:mm:ss")
  private Date createTime;
  
  @JSONField(format = "yyyy-MM-dd HH:mm:ss")
  private Date updateTime;
  
  private static final long serialVersionUID = 1L;
  
}