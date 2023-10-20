package com.xc.fast_deploy.vo;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.io.Serializable;

@Data
public class K8sPatchVo implements Serializable {
  
  private static final long serialVersionUID = 5270724851663939953L;
  
  @JSONField(name = "op")
  private String op = "replace";
  
  private String path;
  
  private Object value;
}
