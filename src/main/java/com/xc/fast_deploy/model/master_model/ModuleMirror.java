package com.xc.fast_deploy.model.master_model;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class ModuleMirror implements Serializable {
  
  private static final long serialVersionUID = -7243595542602583263L;
  
  private Integer id;
  
  private Integer moduleEnvId;
  
  private String mirrorName;
  
  private Integer jobId;
  
  private Integer jobReversion;
  
  private Integer moduleId;
  
  private String devUserId;
  private String opUserId;
  // 是否立即升级
  private Integer isPromptly;
  //0 失败不可用 1 成功可用 2 正在进行中
  private Integer isAvailable;
  
  //0 未被使用过 1 被上线或者升级过
  private Integer isUsed;
  
  @JSONField(format = "yyyy-MM-dd HH:mm:ss")
  private Date createTime;
  
  @JSONField(format = "yyyy-MM-dd HH:mm:ss")
  private Date updateTime;
}