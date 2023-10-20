package com.xc.fast_deploy.vo.module_vo;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class ModuleMirrorJobEnvVo implements Serializable {
  private static final long serialVersionUID = 4533779851394699911L;
  
  private Integer id;
  
  private String mirrorName;
  
  private String jobName;
  
  private Integer jobReversion;
  
  private String envName;
  
  @JSONField(format = "yyyy-MM-dd HH:mm:ss")
  private Date updateTime;
}
