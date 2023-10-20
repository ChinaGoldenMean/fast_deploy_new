package com.xc.fast_deploy.dto.harborMirror;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class MirrorTagDTO implements Serializable {
  
  private static final long serialVersionUID = 8926784373082228501L;
  
  private long size;
  
  private String os;
  
  private String author;
  
  private String digest;
  
  private String name;
  
  private String dockerVersion;

//    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
//    private Date created;

}
