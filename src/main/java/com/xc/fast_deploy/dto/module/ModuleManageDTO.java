package com.xc.fast_deploy.dto.module;

import com.xc.fast_deploy.model.master_model.ModuleManage;
import com.xc.fast_deploy.model.master_model.ModulePackage;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ModuleManageDTO extends ModuleManage implements Serializable {
  
  private static final long serialVersionUID = 385203494117243779L;
  private Integer moduleId;
  
  private String centerName;
  
  private String childCenterName;
  
  private String centerCode;
  
  private String centerPath;
  
  private String certificateName;
  
  private String childCenterContentName;
  
  //    private Integer svnUpType;
  private Integer codeUpType;
  
  private String regionName;
  
  List<ModulePackage> packageList;
}
