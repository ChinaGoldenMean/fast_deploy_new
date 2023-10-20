package com.xc.fast_deploy.quartz_job.job;

import com.xc.fast_deploy.dao.master_dao.ModuleDeployYamlMapper;
import com.xc.fast_deploy.model.master_model.ModuleDeployYaml;
import com.xc.fast_deploy.model.master_model.ModuleEnv;
import com.xc.fast_deploy.model.master_model.ModuleYamlDiff;
import com.xc.fast_deploy.model.master_model.example.ModuleDeployYamlExample;
import com.xc.fast_deploy.utils.k8s.K8sManagement;
import com.xc.fast_deploy.utils.k8s.K8sUtils;
import com.xc.fast_deploy.vo.K8sYamlVo;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.AppsV1Api;
import io.kubernetes.client.openapi.models.V1Deployment;
import io.kubernetes.client.openapi.models.V1DeploymentList;
import io.kubernetes.client.util.Yaml;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class MyYamlCompareThread implements Runnable {
  
  private ModuleEnv moduleEnv;
  private ModuleDeployYamlMapper yamlMapper;
  private String storgePrefix;
  
  public MyYamlCompareThread(ModuleEnv moduleEnv, ModuleDeployYamlMapper yamlMapper,
                             String storgePrefix) {
    this.moduleEnv = moduleEnv;
    this.yamlMapper = yamlMapper;
    this.storgePrefix = storgePrefix;
  }
  
  @Override
  public void run() {
    AppsV1Api appsV1Api =
        K8sManagement.getAppsV1Api(moduleEnv.getK8sConfig());
    try {
      V1DeploymentList deploymentList =
          appsV1Api.listNamespacedDeployment("default", null, null
              , null, null, null, null, null
              , null, null, null);
      List<ModuleYamlDiff> yamlDiffList = new ArrayList<>();
      for (V1Deployment deployment : deploymentList.getItems()) {
        ModuleDeployYamlExample yamlExample = new ModuleDeployYamlExample();
        yamlExample.createCriteria().andYamlNameEqualTo(deployment.getMetadata().getName())
            .andIsOnlineYamlEqualTo(1).andIsDeployedEqualTo(1)
            .andYamlPathLike("%" + moduleEnv.getId() + "%");
        List<ModuleDeployYaml> moduleDeployYamls = yamlMapper.selectByExampleWithBLOBs(yamlExample);
        K8sYamlVo k8sYamlVo = null;
        if (moduleDeployYamls.size() > 0) {
          if (StringUtils.isNotBlank(moduleDeployYamls.get(0).getYamlJson())) {
            k8sYamlVo = K8sUtils.transObject2Vo(Yaml.load(moduleDeployYamls.get(0).getYamlJson()));
          } else {
            k8sYamlVo = K8sUtils.transYaml2Vo(new File(storgePrefix + moduleDeployYamls.get(0).getYamlPath()));
          }
        } else {
          ModuleYamlDiff moduleYamlDiff = new ModuleYamlDiff();
          moduleYamlDiff.setEnvName(moduleEnv.getEnvName());
          moduleYamlDiff.setYamlName(deployment.getMetadata().getName());
          moduleYamlDiff.setDiffArgs("模块名不匹配");
          yamlDiffList.add(moduleYamlDiff);
          log.info(moduleEnv.getEnvName() + ":" + deployment.getMetadata().getName() + "未找到对应的模块");
          continue;
        }
        StringBuilder sb = new StringBuilder();
        V1Deployment yamlDeployment =
            K8sUtils.getObject(k8sYamlVo.getO(), V1Deployment.class);
        if (!deployment.getSpec().getStrategy().equals(yamlDeployment.getSpec().getStrategy()))
          sb.append("strategy,");
        if (!deployment.getSpec().getReplicas().equals(yamlDeployment.getSpec().getReplicas()))
          sb.append("replicas,");
        if (!deployment.getSpec().getTemplate().getSpec().getContainers().get(0).getResources()
            .equals(yamlDeployment.getSpec().getTemplate().getSpec().getContainers().get(0).getResources()))
          sb.append("resources,");
        if (!sb.toString().isEmpty()) {
          ModuleYamlDiff moduleYamlDiff = new ModuleYamlDiff();
          moduleYamlDiff.setEnvName(moduleEnv.getEnvName());
          moduleYamlDiff.setYamlName(deployment.getMetadata().getName());
          moduleYamlDiff.setDiffArgs(sb.substring(0, sb.length() - 1));
          yamlDiffList.add(moduleYamlDiff);
        }
      }
      if (!yamlDiffList.isEmpty()) {
        synchronized (this) {
          yamlMapper.insertYamlDiff(yamlDiffList);
        }
      }
    } catch (IOException | ApiException e) {
      e.printStackTrace();
    }
  }
}
