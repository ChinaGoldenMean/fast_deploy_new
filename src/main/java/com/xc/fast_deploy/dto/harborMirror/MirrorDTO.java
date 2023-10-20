package com.xc.fast_deploy.dto.harborMirror;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
public class MirrorDTO implements Serializable {
  private static final long serialVersionUID = -9172101132263529232L;
  
  private Integer projectId;
  
  private Integer pullCount;
  
  private Integer starCount;
  
  private Integer tagsCount;
  
  private String name;
  
  private Integer id;
  
  @JSONField(format = "yyyy-MM-dd HH:mm:ss")
  private Date creationTime;
  
  @JSONField(format = "yyyy-MM-dd HH:mm:ss")
  private Date updateTime;
  
  private List<MirrorTagDTO> mirrorTagDTOs;
  
}

