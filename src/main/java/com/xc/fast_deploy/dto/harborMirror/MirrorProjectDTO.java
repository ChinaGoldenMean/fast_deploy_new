package com.xc.fast_deploy.dto.harborMirror;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class MirrorProjectDTO implements Serializable {
  
  private static final long serialVersionUID = -1655183307108662437L;
  private Integer projectId;
  
  private Integer ownerId;
  
  private String name;

//    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
//    private Date creationTime;

//    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
//    private Date updateTime;
  
  private Integer repoCount;
  
  private List<MirrorDTO> mirrorDTOList;
  
}
