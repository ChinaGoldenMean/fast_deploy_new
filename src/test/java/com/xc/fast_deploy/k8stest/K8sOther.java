package com.xc.fast_deploy.k8stest;

import com.google.common.collect.Maps;
import com.google.gson.JsonObject;
import com.xc.fast_deploy.dao.master_dao.ModuleEnvMapper;
import com.xc.fast_deploy.model.master_model.ModuleEnv;
import com.xc.fast_deploy.service.k8s.K8sPodService;
import com.xc.fast_deploy.service.k8s.K8sService;
import com.xc.fast_deploy.utils.constant.K8sPatchMirror;
import com.xc.fast_deploy.utils.k8s.K8sManagement;
import com.xc.fast_deploy.utils.k8s.K8sUtils;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.AppsV1Api;
import io.kubernetes.client.openapi.apis.CoreV1Api;

import io.kubernetes.client.openapi.models.V1Pod;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class K8sOther {
  
  @Autowired
  private ModuleEnvMapper envMapper;
  @Autowired
  private K8sPodService k8sPodService;
  @Autowired
  private K8sService k8sService;
  
  @Test
  public void getIngress() {
    ModuleEnv moduleEnv = envMapper.selectOne(10);
    CoreV1Api coreV1Api = K8sManagement.getCoreV1Api(moduleEnv.getK8sConfig());
    V1Pod v1Pod = null;
    try {
      v1Pod = coreV1Api.readNamespacedPod("xfer-oldde-pstn-ismp-paas-billing-docker-093-billing-1035223-1-1th",
          "b4center", null);
    } catch (ApiException e) {
      System.out.println(e.getResponseBody());
    }

//        AppsV1Api extensionApi = K8sManagement.getExtensionApi(moduleEnv.getK8sConfig());
//        V1beta1IngressList ingressList = extensionApi.listIngressForAllNamespaces(null, null, null, null,
//                null, null, null, null, null);
//        if (ingressList != null && ingressList.getItems().size() > 0) {
//            System.out.println(new Gson().toJson(ingressList));
//        }
  }
  
  @Test
  public void getIngressOk() throws InterruptedException {
    ModuleEnv moduleEnv = new ModuleEnv();
    moduleEnv.setK8sConfig(K8sUtils.MY_OWN);
    
    k8sService.scaleModuleSize("boot-demo2",
        "default", null, moduleEnv, 0);
    Thread.sleep(2000);
    k8sService.deleteNamespacedSource("boot-demo2",
        "Deployment", "default", moduleEnv);
  }
  
  @Test
  public void testPatch() {
    AppsV1Api v1beta1Api = K8sManagement.getExtensionApi(K8sUtils.MY_OWN);
    Map<String, String> result = Maps.newHashMap();
    result.put("memory", "1200Mi");
    result.put("cpu", "3000m");
    List<JsonObject> pathList =
        K8sUtils.generatePatchPath(K8sPatchMirror.TEMPLATE_CONTAINERS_RESOURCE_LIMIT, result, null);
//        try {
//            V1Deployment deployment = v1beta1Api.patchNamespacedDeployment("boot-demo2",
//                    K8sNameSpace.DEFAULT, pathList, null, null);
//
//        } catch (ApiException e) {
//            e.printStackTrace();
//        }
//        AppsV1Api v1beta1Api = K8sManagement.getExtensionApi(K8sUtils.MY_OWN);
//        Map<String, String> result = Maps.newHashMap();
//        result.put("memory", "1200Mi");
//        result.put("cpu", "3000m");
//        List<JsonObject> pathList =
//                K8sUtils.generatePatchPath(K8sPatchMirror.TEMPLATE_CONTAINERS_RESOURCE_LIMIT, result);
//        try {
//            V1Deployment deployment = v1beta1Api.patchNamespacedDeployment("boot-demo2",
//                    K8sNameSpace.DEFAULT, pathList, null, null);
//
//        } catch (ApiException e) {
//            e.printStackTrace();
//        }

//        AppsV1Api v1beta1Api = K8sManagement.getExtensionApi(K8sUtils.MY_OWN);
//        Map<String, String> result = Maps.newHashMap();
//        result.put("memory", "1200Mi");
//        result.put("cpu", "3000m");
//        List<JsonObject> pathList =
//                K8sUtils.generatePatchPath(K8sPatchMirror.TEMPLATE_CONTAINERS_RESOURCE_LIMIT, result);
//        try {
//            V1Deployment deployment = v1beta1Api.patchNamespacedDeployment("boot-demo2",
//                    K8sNameSpace.DEFAULT, pathList, null, null);
//
//        } catch (ApiException e) {
//            e.printStackTrace();
//        }
  }
}
