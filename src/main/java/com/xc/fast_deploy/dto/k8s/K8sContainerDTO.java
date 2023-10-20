package com.xc.fast_deploy.dto.k8s;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class K8sContainerDTO implements Serializable {
  
  private String containerName;
  private String imageName;
  private Integer restartCount;
  private String status;
  private String describeMsg;
  
}
