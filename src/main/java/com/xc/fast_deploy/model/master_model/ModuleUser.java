package com.xc.fast_deploy.model.master_model;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

import java.util.Date;

@NoArgsConstructor
@Data
public class ModuleUser {
  
  private String id;
  
  private String username;
  
  private String cnname;
  
  private String mobile;
  
  @JSONField(name = "QQ")
  private String QQ;
  
  private String wxcode;
  
  private String remark;
  
  private String userPermissionInfo;
  
  private String userId;
  
  private String password;
  
  @JSONField(format = "yyyy-MM-dd HH:mm:ss")
  private Date createTime;
  
  @JSONField(format = "yyyy-MM-dd HH:mm:ss")
  private Date updateTime;
  
  public ModuleUser(PModuleUser moduleUser) {
    id = moduleUser.getUserId();
    username = moduleUser.getUsername();
  }
}