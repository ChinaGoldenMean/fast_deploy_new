package com.xc.fast_deploy.dto.k8s;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.Date;

@Data
public class K8sDeployTrendData {
  
  @JSONField(format = "yyyy-MM-dd HH:mm:ss")
  private Date time;
  
  private Integer count;
  
}
