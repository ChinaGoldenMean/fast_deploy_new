package com.xc.fast_deploy.vo.module_vo.param;

import com.xc.fast_deploy.dto.k8s.K8sPodDTO;
import com.xc.fast_deploy.dto.k8s.K8sServiceDTO;
import com.xc.fast_deploy.vo.module_vo.ModuleEnvCenterManageVo;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ModuleDeployPodCenterManageVo extends ModuleEnvCenterManageVo implements Serializable {
  
  private List<K8sPodDTO> k8sPodDTOS;
  
  private K8sServiceDTO k8sServiceDTO;
  
}
