package com.xc.fast_deploy.service.k8s;

import com.xc.fast_deploy.dto.k8s.K8sNodeDTO;
import com.xc.fast_deploy.vo.k8s_vo.K8sNodeVo;

import java.util.List;

public interface K8sNodeService {
  
  List<K8sNodeDTO> selectInfo(Integer envId, String nodeIPKey, String labelKeyName);
  
  boolean scheduleNode(Integer envId, String nodeName, boolean unScheduleNode);
  
  boolean updateNodeLabel(K8sNodeVo k8sNodeVo);
}
