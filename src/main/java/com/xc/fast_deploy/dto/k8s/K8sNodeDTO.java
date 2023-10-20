package com.xc.fast_deploy.dto.k8s;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.Date;
import java.util.Map;

@Data
public class K8sNodeDTO {
  
  private String nodeName;
  
  //    private String labels;
  private Map<String, String> labelMap;
  
  private String nodeIP;
  
  private String status;
  
  private String roleName;
  
  @JSONField(format = "yyyy-MM-dd HH:mm:ss")
  private Date createTime;
  
  private String podCIDR;
  
}
