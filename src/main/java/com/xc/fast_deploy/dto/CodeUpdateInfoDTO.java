package com.xc.fast_deploy.dto;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
public class CodeUpdateInfoDTO implements Serializable {
  
  private static final long serialVersionUID = -1111028585802442305L;
  private String reversion;
  private String author;
  private String commitLog;
  
  @JSONField(format = "yyyy-MM-dd HH:mm:ss")
  private Date opDate;
  
  private List<String> changeContent;
  
}
