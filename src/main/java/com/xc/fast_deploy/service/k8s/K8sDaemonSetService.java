package com.xc.fast_deploy.service.k8s;

import com.xc.fast_deploy.dto.k8s.K8sDaemonSetDTO;
import com.xc.fast_deploy.model.master_model.ModuleEnv;
import io.kubernetes.client.openapi.models.V1DaemonSet;

import java.util.List;

public interface K8sDaemonSetService {
  List<K8sDaemonSetDTO> getEnvAllDaemonSet(ModuleEnv env);
  
  boolean deleteDaemonSet(String daemonSetName, ModuleEnv env);
  
  V1DaemonSet getDaemonSetByName(ModuleEnv env, String daemonSetName);
}
