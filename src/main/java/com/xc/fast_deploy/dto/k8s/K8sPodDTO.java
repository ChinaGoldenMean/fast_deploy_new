package com.xc.fast_deploy.dto.k8s;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
public class K8sPodDTO implements Serializable {
  
  private static final long serialVersionUID = 6306219295589256231L;
  private String podName;
  private String podIP;
  private String nodeIP;
  private String status;
  private Integer containerSize;
  private String yamlNamespace;
  private Integer containerRunningSize;
  private String mainContainerMirrorName;
  private String mainContainerName;
  private Integer mainContainerRestartCount;
  private List<K8sContainerDTO> containerDTOList;
  
  @JSONField(format = "yyyy-MM-dd HH:mm:ss")
  private Date startTime;

//    private String moduleEnvName;
//
//    private ModuleManage moduleManage;

}
