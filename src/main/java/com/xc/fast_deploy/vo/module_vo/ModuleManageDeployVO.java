package com.xc.fast_deploy.vo.module_vo;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.Date;

@Data
public class ModuleManageDeployVO {
  
  private String moduleName;
  private Integer moduleId;
  private String childCenterName;
  private String centerName;
  private String envName;
  private String yamlName;
  private String chargePerson;
  private String chargeTelephone;
  private String officalChargePerson;
  private String officalChargeTelephone;
  private String remark;
  private String ywRemark;
  
  @JSONField(format = "yyyy-MM-dd HH:mm:ss")
  private Date createTime;
  
}
