package com.xc.fast_deploy.dto.jenkins;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class JobBuildDetailsDTO implements Serializable {
  private static final long serialVersionUID = -7288568827232256608L;
  
  @JSONField(name = "IsBuilding")
  private boolean isBuilding;
  
  private String result;
  
  private Integer buildNumber;
  
  private long duration;
  
  @JSONField(format = "yyyy-MM-dd HH:mm:ss")
  private Date buildDate;
  
}
