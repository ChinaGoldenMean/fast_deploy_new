package com.xc.fast_deploy.model.master_model;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.Date;

@Data
public class ModuleManage {
  
  private Integer id;
  
  private String moduleName;
  
  private String moduleContentName;
  
  private Integer centerId;
  
  private Integer certificateId;
  
  private Integer moduleType;
  
  private String moduleProjectCode;
  
  private String userId;
  
  private Integer envId;
  
  private String envCode;
  
  private String mark;
  
  private Integer isDelete;
  
  private String chargePerson;
  
  private String chargeTelephone;
  
  private String officalChargePerson;
  
  private String officalChargeTelephone;
  
  private String shPath;
  
  private String svnAutoUrl;
  
  private String remark;
  
  private String remarkYw;
  
  private String remarkBak;
  
  @JSONField(format = "yyyy-MM-dd HH:mm:ss")
  private Date createTime;
  
  @JSONField(format = "yyyy-MM-dd HH:mm:ss")
  private Date updateTime;
  
}