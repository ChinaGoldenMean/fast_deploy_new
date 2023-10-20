package com.xc.fast_deploy.k8stest;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.xc.fast_deploy.dao.master_dao.ModuleDeployYamlMapper;
import com.xc.fast_deploy.dao.master_dao.ModuleEnvMapper;
import com.xc.fast_deploy.dto.ResponseDTO;
import com.xc.fast_deploy.dto.k8s.K8sContainerDTO;
import com.xc.fast_deploy.dto.k8s.K8sPodDTO;
import com.xc.fast_deploy.dto.module.ModuleDeployNeedDTO;
import com.xc.fast_deploy.model.master_model.ModuleDeployNeed;
import com.xc.fast_deploy.model.master_model.ModuleDeployYaml;
import com.xc.fast_deploy.model.master_model.ModuleEnv;
import com.xc.fast_deploy.model.master_model.ModuleUser;
import com.xc.fast_deploy.model.master_model.example.ModuleDeployYamlExample;
import com.xc.fast_deploy.myenum.k8sEnum.K8sApiversionTypeEnum;
import com.xc.fast_deploy.myenum.k8sEnum.K8sContainerStatusEnum;
import com.xc.fast_deploy.service.common.ModuleDeployService;
import com.xc.fast_deploy.service.k8s.K8sNodeService;
import com.xc.fast_deploy.service.k8s.K8sPodService;
import com.xc.fast_deploy.utils.HttpUtils;
import com.xc.fast_deploy.utils.constant.K8sNameSpace;
import com.xc.fast_deploy.utils.constant.K8sPatchMirror;
import com.xc.fast_deploy.utils.k8s.K8sExceptionUtils;
import com.xc.fast_deploy.utils.k8s.K8sManagement;
import com.xc.fast_deploy.utils.k8s.K8sUtils;
import com.xc.fast_deploy.vo.K8sPatchVo;
import com.xc.fast_deploy.vo.K8sYamlVo;
import io.kubernetes.client.Exec;
import io.kubernetes.client.custom.Quantity;
import io.kubernetes.client.custom.V1Patch;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.ApiResponse;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.apis.ExtensionsV1beta1Api;
import io.kubernetes.client.openapi.models.*;
import io.kubernetes.client.util.Yaml;
import javafx.scene.transform.Scale;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static com.xc.fast_deploy.utils.k8s.K8sUtils.okhttpGetBack;
import static com.xc.fast_deploy.utils.k8s.K8sUtils.okhttpPatchBack;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class K8sTest {
  
  @Autowired
  private ModuleEnvMapper envMapper;
  @Autowired
  private K8sPodService k8sPodService;
  @Autowired
  private ModuleDeployYamlMapper yamlMapper;
  @Autowired
  private ModuleDeployService deployService;
  
  @Value("${file.storge.path.prefix}")
  private String storgePrefix;
  @Value("${myself.pspass.prodId}")
  private String prodId;
  
  @Test
  public void testK8s() {
    
    ModuleEnv moduleEnv = envMapper.selectOne(10);
    CoreV1Api coreV1Api = K8sManagement.getCoreV1Api(moduleEnv.getK8sConfig());
    ExtensionsV1beta1Api extensionApi = K8sManagement.getExtensionApi(moduleEnv.getK8sConfig());
    try {
//            V1Pod v1Pod = coreV1Api.readNamespacedPod("myboot-854d66c7c8-gkvmw", "default", null, null, null);
//            System.out.println(JSONObject.toJSONString(v1Pod));
//            V1Deployment deployment = extensionApi.readNamespacedDeployment("myboot", "default", null, null, null);
//            System.out.println(JSONObject.toJSONString(deployment));

//            File file = new File("F:\\storge\\code1\\gogo.yaml");
//            K8sYamlVo k8sYamlVo = K8sUtils.transYaml2Vo(file);
//            V1Deployment beta1Deployment = K8sUtils.getObject(k8sYamlVo.getO(), V1Deployment.class);
//            V1Deployment deployment = extensionApi.createNamespacedDeployment("default", beta1Deployment, null, null, null);
//            System.out.println(JSONObject.toJSONString(deployment));
//            File file = new File("F:\\storge\\code1\\gogo.yaml");
//            K8sYamlVo k8sYamlVo = K8sUtils.transYaml2Vo(file);
//            ExtensionsV1beta1Deployment beta1Deployment = K8sUtils.getObject(k8sYamlVo.getO(), ExtensionsV1beta1Deployment.class);
//            List<V1Container> containers = beta1Deployment.getSpec().getTemplate().getSpec().getContainers();
//            for (V1Container container : containers) {
//                container.setImage("124.35.23.24:4000/test/test");
//            }
//
//            ExtensionsV1beta1Deployment deployment = extensionApi.replaceNamespacedDeployment("myboot", "default", beta1Deployment, null, null);
//            System.out.println(JSONObject.toJSONString(deployment));
//            V1DeleteOptions v1DeleteOptions = new V1DeleteOptions();
//            v1DeleteOptions.setOrphanDependents(false);
//            V1Status v1Status = extensionApi.deleteNamespacedDeployment("myboot", "default", v1DeleteOptions, null, null, null,
//                    false, null);

//            ExtensionsV1beta1Scale extensionsV1beta1Scale = extensionApi.readNamespacedDeploymentScale("myboot", "default", null);
//            extensionsV1beta1Scale.getSpec().setReplicas(1);
//            ExtensionsV1beta1Scale scale = extensionApi.replaceNamespacedDeploymentScale("myboot", "default", extensionsV1beta1Scale, null, null);
//            System.out.println(JSONObject.toJSONString(scale));

//            AppsV1beta1Deployment deployment = appsV1beta1Api.readNamespacedDeployment("myboot", "default", null, null, null);
//            String generateName = deployment.getMetadata().getGenerateName();
      
      K8sPatchVo k8sPatchVo = new K8sPatchVo();
      k8sPatchVo.setPath("/spec/strategy");
      Map<String, Object> map = new HashMap<>();
      Map<String, Integer> map1 = new HashMap<>();
      map1.put("maxSurge", 1);
      map1.put("maxUnavailable", 0);
      map.put("type", "RollingUpdate");
      map.put("rollingUpdate", map1);
      k8sPatchVo.setValue(map);
      
      JsonElement jsonElement = (JsonElement) deserialize(JSONObject.toJSONString(k8sPatchVo), JsonElement.class);
      JsonObject jsonObject = jsonElement.getAsJsonObject();
      ArrayList<JsonObject> arr = new ArrayList<>();
      arr.add(jsonObject);
      
      ExtensionsV1beta1Deployment deployment = extensionApi.patchNamespacedDeployment("boot-demo", "default", new V1Patch(arr.toString()),
          null, null, null, null);
    } catch (ApiException e) {
      log.error("k8s 操作出现错误");
      e.printStackTrace();
    } catch (JsonSyntaxException e) {
      log.error("出现某不知名的错误!!!");
      K8sExceptionUtils.anylse(e);
    }
  }
  
  public Object deserialize(String jsonStr, Class<?> targetClass) {
    Object obj = (new Gson()).fromJson(jsonStr, targetClass);
    return obj;
  }
  
  @Test
  public void patchresources() {
    ModuleEnv moduleEnv = envMapper.selectOne(10);
    ExtensionsV1beta1Api extensionApi = K8sManagement.getExtensionApi(moduleEnv.getK8sConfig());
    try {
      ExtensionsV1beta1Deployment deployment =
          extensionApi.readNamespacedDeployment("crm-platform-pressure-center-machine-pressure-sample",
              "default", null, null, false);
      V1ResourceRequirements v1ResourceRequirements = new V1ResourceRequirements();
      Map<String, Quantity> requests = new HashMap<>();
      requests.put("memory", Quantity.fromString("2Gi"));
      requests.put("cpu", Quantity.fromString("2"));
      v1ResourceRequirements.setRequests(requests);
      Map<String, Quantity> limits = new HashMap<>();
      limits.put("memory", Quantity.fromString("1Gi"));
      limits.put("cpu", Quantity.fromString("1"));
      v1ResourceRequirements.setLimits(limits);
      v1ResourceRequirements.setRequests(requests);
      deployment.getSpec().getTemplate().getSpec().getContainers().get(0).setResources(v1ResourceRequirements);
      ExtensionsV1beta1Deployment deployment1 =
          extensionApi.replaceNamespacedDeployment("crm-platform-pressure-center-machine-pressure-sample", "default", deployment, null, null, null);
//            Map<String,String> limitMap = new LinkedHashMap<>();
//            Map<String,String> requestMap = Maps.newHashMap();
//            limitMap.put("cpu","0");
//            limitMap.put("memory","0");
//            requestMap.put("memory","2Gi");
//            requestMap.put("cpu","2");
//
////            K8sPatchVo k8sPatchVo = new K8sPatchVo();
////            k8sPatchVo.setPath("/spec/template/spec/containers/0/resources/limits");
////            k8sPatchVo.setValue(limitMap);
////            JsonElement jsonElement = (JsonElement) deserialize(JSONObject.toJSONString(k8sPatchVo), JsonElement.class);
////            JsonObject jsonObject = jsonElement.getAsJsonObject();
//            K8sPatchVo k8sPatchVo1 = new K8sPatchVo();
////            k8sPatchVo1.setValue(requestMap);
////            k8sPatchVo1.setPath("/spec/template/spec/containers/0/resources/requests");
////            JsonElement element = new Gson().fromJson(JSONObject.toJSONString(k8sPatchVo1), JsonElement.class);
////            JsonObject jsonObject1 = element.getAsJsonObject();
////            ArrayList<JsonObject> arr = new ArrayList<>();
//////            arr.add(jsonObject);
////            arr.add(jsonObject1);
//            List<JsonObject> limitList =
//                    K8sUtils.generatePatchPath("/spec/template/spec/containers/0/resources/limits", limitMap, null);
//            List<JsonObject> requestList =
//                    K8sUtils.generatePatchPath("/spec/template/spec/containers/0/resources/requests", requestMap, null);
//            List<JsonObject> pathList = new ArrayList<>();
//            pathList.addAll(limitList);
//            pathList.addAll(requestList);
    } catch (ApiException e) {
      log.error("k8s 操作出现错误");
      e.printStackTrace();
    } catch (JsonSyntaxException e) {
      log.error("出现某不知名的错误!!!");
      K8sExceptionUtils.anylse(e);
    }
  }
  
  @Test
  public void getPodInfo() {
    ModuleEnv env = envMapper.selectOne(22);
    List<K8sPodDTO> podDTOS = new ArrayList<>();
    CoreV1Api coreV1Api = K8sManagement.getCoreV1Api(env.getK8sConfig());
    
    List<V1Pod> v1Pods = k8sPodService.getPodInfoByName(coreV1Api, "bss-cpct-web", K8sNameSpace.DEFAULT, null);
    if (v1Pods != null && v1Pods.size() > 0) {
      for (V1Pod pod : v1Pods) {
        K8sPodDTO k8sPodDTO = new K8sPodDTO();
        k8sPodDTO.setPodName(pod.getMetadata().getName());
        k8sPodDTO.setContainerSize(pod.getSpec().getContainers().size());
        k8sPodDTO.setNodeIP(pod.getStatus().getHostIP());
        k8sPodDTO.setPodIP(pod.getStatus().getPodIP());
        k8sPodDTO.setStatus(pod.getStatus().getPhase());
        
        if (pod.getStatus().getStartTime() != null) {
          k8sPodDTO.setStartTime(pod.getStatus().getStartTime().toDate());
        }
        
        int containerRunningSize = 0;
        
        List<V1ContainerStatus> containerStatuses = pod.getStatus().getContainerStatuses();
        List<K8sContainerDTO> containerDTOList = new ArrayList<>();
        if (containerStatuses != null && containerStatuses.size() > 0) {
          for (V1ContainerStatus v1ContainerStatus : containerStatuses) {
            K8sContainerDTO containerDTO = new K8sContainerDTO();
            if (v1ContainerStatus.getName().equals("bss-cpct-web")) {
              k8sPodDTO.setMainContainerName(v1ContainerStatus.getName());
              k8sPodDTO.setMainContainerMirrorName(v1ContainerStatus.getImage());
              k8sPodDTO.setMainContainerRestartCount(v1ContainerStatus.getRestartCount());
            }
            
            containerDTO.setContainerName(v1ContainerStatus.getName());
            containerDTO.setImageName(v1ContainerStatus.getImage());
            containerDTO.setRestartCount(v1ContainerStatus.getRestartCount());
            
            V1ContainerStateRunning running = v1ContainerStatus.getState().getRunning();
            V1ContainerStateTerminated terminated = v1ContainerStatus.getState().getTerminated();
            V1ContainerStateWaiting waiting = v1ContainerStatus.getState().getWaiting();
            if (running != null) {
              containerRunningSize++;
              Date date = running.getStartedAt().toDate();
              SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
              String s = dateFormat.format(date);
              containerDTO.setDescribeMsg("start time: " + s);
              containerDTO.setStatus(K8sContainerStatusEnum.RUNNINT.getStatus());
            }
            if (terminated != null) {
              containerDTO.setDescribeMsg(terminated.getReason());
              containerDTO.setStatus(K8sContainerStatusEnum.TERMINATED.getStatus());
            }
            if (waiting != null) {
              containerDTO.setDescribeMsg(waiting.getReason());
              containerDTO.setStatus(K8sContainerStatusEnum.WAITING.getStatus());
            }
            containerDTOList.add(containerDTO);
          }
          k8sPodDTO.setContainerDTOList(containerDTOList);
          k8sPodDTO.setContainerRunningSize(containerRunningSize);
        }
        podDTOS.add(k8sPodDTO);
      }
    }
    System.out.println(JSONObject.toJSONString(podDTOS));
  }
  
  @Test
  public void showK8sExec() {
    ModuleEnv env = envMapper.selectOne(10);
    CoreV1Api coreV1Api = K8sManagement.getCoreV1Api(env.getK8sConfig());
    try {
      StringBuffer sb = new StringBuffer();
//            V1PodList v1PodList = coreV1Api.listNamespacedPod(K8sNameSpace.DEFAULT, null,
//                    null, null, null, null
//                    , null, null, 200, null);
//            List<V1Pod> items = v1PodList.getItems();
//            if (items != null && items.size() > 0) {
//                for (V1Pod v1Pod : items) {
//                    System.out.println(v1Pod.getMetadata().getName());
//                }
//            }
//            String s = coreV1Api.connectGetNamespacedPodExec("intelligence-devops-5897b679f9-9w877", K8sNameSpace.DEFAULT,
//                    "date", "intelligence-devops",
//                    null, null, true, true);
      Exec exec = new Exec();
      // final Process proc = exec.exec("default", "nginx-4217019353-k5sn9", new String[]
      //   {"sh", "-c", "echo foo"}, true, tty);
      final Process proc =
          exec.exec(
              K8sNameSpace.DEFAULT,
              "intelligence-devops-5897b679f9-9w877",
              new String[]{"sh", "/app/jmx-tool/jmx-monitor-12345.sh"},
//                            new String[]{"env"},
              false,
              false);
//            final String[] result = {null};
      ThreadPoolExecutor poolExecutor = new ThreadPoolExecutor(1, 1,
          0L, TimeUnit.SECONDS, new ArrayBlockingQueue<>(10));
      poolExecutor.submit(new Thread(() -> {
        InputStream inputStream = null;
        try {
          inputStream = proc.getInputStream();
          byte[] bytes = new byte[2048];
          int len;
          while ((len = inputStream.read(bytes)) != -1) {
            sb.append(new String(bytes, 0, len));
          }
        } catch (IOException ex) {
          ex.printStackTrace();
        } finally {
          if (inputStream != null) {
            try {
              inputStream.close();
            } catch (IOException e) {
              e.printStackTrace();
            }
          }
        }
      }));
      
      proc.waitFor(5, TimeUnit.SECONDS);

//            System.out.println(sb.toString());
      Thread.sleep(2000);

//            poolExecutor.shutdown();
      log.info(sb.toString());
//            poolExecutor.wait(10);
//            System.out.println("test2 : " + Arrays.toString(result));
      // wait for any last output; no need to wait for input thread
//            PoolExcutorUtils.shutDownExcutor(poolExecutor, 1);
      proc.destroy();
//            System.exit(proc.exitValue());
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  
  @Test
  public void testGetHarobrInfo() {
    ResponseDTO responseDTO = new ResponseDTO();
    for (int i = 0; i < 100; i++) {
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      new Thread(() -> {
        String result = HttpUtils.doGetHarborInfo("http://134.108.3.248:8100/api/projects",
            "billing", "Zjyx_123");
        if (StringUtils.isNotBlank(result)) {
          JSONArray jsonArray = JSONObject.parseArray(result);
          responseDTO.success(jsonArray);
        }
        System.out.println(JSONObject.toJSONString(responseDTO));
      }).start();
    }
    
  }
  
  @Autowired
  private ModuleDeployService moduleDeployService;
  
  @Test
  public void testShowExec() {
    moduleDeployService.clearMemoryPods(10, 31,
        new String[]{"boot-demo2-d5bf46d8f-b7zt6"}, new ModuleUser());
  }
  
  @Test
  public void testNode() throws ApiException {
    CoreV1Api coreV1Api = K8sManagement.getCoreV1Api(K8sUtils.MY_OWN);
    V1NodeList v1NodeList = coreV1Api.listNode(null, null, null, null,
        null, null, null, null, null);
//        System.out.println(v1NodeList.getItems().get(0).getMetadata().getName());
//        List<V1Node> items = v1NodeList.getItems();
//        for (V1Node v1Node : items) {
//            System.out.println(JSONObject.toJSONString(v1Node));
//        }
    //增加 和 修改 node的标签
    String node1Name = v1NodeList.getItems().get(1).getMetadata().getName();
    //        Map<String, String> labels = v1NodeList.getItems().get(1).getMetadata().getLabels();
    //        labels.put("beta.kubernetes.io/arch","amd64");
    //        labels.put("beta.kubernetes.io/os","linux");
    //        labels.put("kubernetes.io/hostname","zdhyw-pst-02");
    //        ArrayList<JsonObject> path = K8sUtils.generatePatchMirrorPath(K8sPatchMirror.LABEL, labels);
    //        V1Node v1Node = coreV1Api.patchNode(node1Name, path, null, null);
//        System.out.println(JSONObject.toJSONString(v1Node));
    
    //隔离node节点
//        ArrayList<JsonObject> path = K8sUtils.generatePatchPath(K8sPatchMirror.NODE_UNSCHEDULABLE, true);
    K8sPatchVo k8sPatchVo = new K8sPatchVo();
    k8sPatchVo.setOp("add");
    k8sPatchVo.setPath(K8sPatchMirror.NODE_UNSCHEDULABLE);
    k8sPatchVo.setValue(true);
    JsonElement element = new Gson().fromJson(JSONObject.toJSONString(k8sPatchVo), JsonElement.class);
    JsonObject jsonObject = element.getAsJsonObject();
    List<JsonObject> pathList = new ArrayList<>();
    pathList.add(jsonObject);
    ApiResponse<V1Node> apiResponse = coreV1Api.patchNodeWithHttpInfo(node1Name, new V1Patch(jsonObject.toString()),
        null, null, null, null);
    System.out.println(apiResponse);
  }
  
  @Autowired
  private K8sNodeService k8sNodeService;
  
  @Test
  public void getTestNodeInfo() throws ApiException {
    CoreV1Api coreV1Api = K8sManagement.getCoreV1Api(K8sUtils.MY_OWN);
    V1NodeList nodeList = coreV1Api.listNode(null, null, null, null,
        "center=deploy", null, null, null, null);
    for (V1Node v1Node : nodeList.getItems()) {
      System.out.println(v1Node);
    }
//        V1PodList podList = coreV1Api.listNamespacedPod("default",
//                null, null, null,
//                null, null, null, null, null, null);
//        System.out.println(podList);

//        List<K8sNodeDTO> nodeDTOList = new ArrayList<>();
//        try {
//            CoreV1Api coreV1Api = K8sManagement.getCoreV1Api(K8sUtils.MY_OWN);
//            V1NodeList v1NodeList = coreV1Api.listNode(true, null,
//                    null, null, null,
//                    null, null, null, null);
//            List<V1Node> items = v1NodeList.getItems();
//            if (items != null && items.size() > 0) {
//                for (V1Node v1Node : items) {
//                    K8sNodeDTO k8sNodeDTO = new K8sNodeDTO();
//                    k8sNodeDTO.setNodeName(v1Node.getMetadata().getName());
//                    List<V1NodeAddress> addresses = v1Node.getStatus().getAddresses();
//                    String nodeIP = null;
//                    for (V1NodeAddress nodeAddress : addresses) {
//                        if ("InternalIP".equals(nodeAddress.getType())) {
//                            nodeIP = nodeAddress.getAddress();
//                        }
//                    }
//                    k8sNodeDTO.setNodeIP(nodeIP);
//                    k8sNodeDTO.setCreateTime(v1Node.getMetadata().getCreationTimestamp().toDate());
//                    k8sNodeDTO.setPodCIDR(v1Node.getSpec().getPodCIDR());
//                    List<V1NodeCondition> conditions = v1Node.getStatus().getConditions();
//                    if (conditions != null && conditions.size() > 0) {
//                        for (V1NodeCondition condition : conditions) {
//                            String type = condition.getType();
//                            if ("Ready".equals(type)) {
//                                if ("True".equals(condition.getStatus())) {
//                                    k8sNodeDTO.setStatus("Ready");
//                                } else {
//                                    k8sNodeDTO.setStatus("NotReady");
//                                }
//                            }
//                        }
//                    }
//                    if (v1Node.getSpec().isUnschedulable() != null && v1Node.getSpec().isUnschedulable()) {
//                        k8sNodeDTO.setStatus(k8sNodeDTO.getStatus() + ",SchedulingDisabled");
//                    }
//                    StringBuilder labelSb = new StringBuilder();
//                    Map<String, String> labels = v1Node.getMetadata().getLabels();
//                    if (labels != null && labels.size() > 0) {
//                        Set<String> labelKeySet = labels.keySet();
//                        for (String key : labelKeySet) {
//                            String value = labels.get(key);
//                            if (StringUtils.isNotBlank(key) && !key.contains("kubernetes.io")) {
//                                labelSb.append(key).append(":").append(value).append(",");
//                            }
//                        }
//                    }
//                    if (labelSb.length() > 0) {
//                        labelSb.deleteCharAt(labelSb.length() - 1);
//                        k8sNodeDTO.setLabels(labelSb.toString());
//                    } else {
//                        k8sNodeDTO.setLabels("暂无");
//                    }
//                    List<V1Taint> taints = v1Node.getSpec().getTaints();
//                    k8sNodeDTO.setRoleName("Node");
//                    if (taints != null && taints.size() > 0) {
//                        for (V1Taint v1Taint : taints) {
//                            if ("node-role.kubernetes.io/master".equals(v1Taint.getKey())) {
//                                k8sNodeDTO.setRoleName("Master");
//                            }
//                        }
//                    }
//                    nodeDTOList.add(k8sNodeDTO);
//                }
//            }
//        } catch (ApiException e) {
//            e.printStackTrace();
//        }
  }
  
  @Test
  public void getstratgyargs() {
    ModuleEnv moduleEnv = envMapper.selectOne(10);
    ExtensionsV1beta1Api extensionApi = K8sManagement.getExtensionApi(moduleEnv.getK8sConfig());
    try {
      ExtensionsV1beta1Deployment deployment =
          extensionApi.readNamespacedDeployment("boot-demo", "default", null, null, null);
      ExtensionsV1beta1DeploymentStrategy strategy = deployment.getSpec().getStrategy();
      Integer replicas = deployment.getSpec().getReplicas();
      if (strategy.getType().equals("RollingUpdate")) {
        String maxSurge = strategy.getRollingUpdate().getMaxSurge().toString();
        String maxUnavailable = strategy.getRollingUpdate().getMaxUnavailable().toString();
        if (maxSurge.contains("%")) {
          Integer maxS = Integer.valueOf(maxSurge.replace("%", ""));
          Integer ceil = (int) Math.ceil(maxS / 100.0 * replicas);
          System.out.println(ceil);
        }
        if (maxUnavailable.contains("%")) {
          Integer maxU = Integer.valueOf(maxUnavailable.replace("%", ""));
          Integer floor = (int) Math.floor(maxU / 100.0 * replicas);
          System.out.println(floor);
        }
      }
    } catch (ApiException e) {
      e.printStackTrace();
    }
  }
  
  @Test
  public void compareYaml() {
    ModuleEnv moduleEnv = envMapper.selectOne(10);
    ExtensionsV1beta1Api extensionApi = K8sManagement.getExtensionApi(moduleEnv.getK8sConfig());
    try {
      ExtensionsV1beta1DeploymentList deployList =
          extensionApi.listNamespacedDeployment("default", null, null, null, null
              , null, null, null, null, null);
      for (ExtensionsV1beta1Deployment deployment : deployList.getItems()) {
        ModuleDeployYamlExample yamlExample = new ModuleDeployYamlExample();
        yamlExample.createCriteria().andYamlNameEqualTo(deployment.getMetadata().getName())
            .andIsOnlineYamlEqualTo(1).andIsDeployedEqualTo(1);
        List<ModuleDeployYaml> moduleDeployYamls = yamlMapper.selectByExampleWithBLOBs(yamlExample);
        K8sYamlVo k8sYamlVo = null;
        if (moduleDeployYamls.size() > 0) {
          if (StringUtils.isNotBlank(moduleDeployYamls.get(0).getYamlJson())) {
            k8sYamlVo = K8sUtils.transObject2Vo(Yaml.load(moduleDeployYamls.get(0).getYamlJson()));
          } else {
            k8sYamlVo = K8sUtils.transYaml2Vo(new File(storgePrefix + moduleDeployYamls.get(0).getYamlPath()));
          }
        }
        ExtensionsV1beta1Deployment yamlDeployment = K8sUtils.getObject(k8sYamlVo.getO(), ExtensionsV1beta1Deployment.class);
        if (deployment.getSpec().getStrategy().equals(yamlDeployment.getSpec().getStrategy()))
          System.out.println("strategy----------ok");
        if (deployment.getSpec().getReplicas().equals(yamlDeployment.getSpec().getReplicas()))
          System.out.println("replicas ------------ok");
        if (deployment.getSpec().getTemplate().getSpec().getContainers().get(0).getResources()
            .equals(yamlDeployment.getSpec().getTemplate().getSpec().getContainers().get(0).getResources()))
          System.out.println("containers ---------------ok");
      }
    } catch (ApiException | IOException e) {
      e.printStackTrace();
    }
  }
  
  @Test
  public void otherTest() {
    File file = new File("C:\\Users\\luochangbin\\Desktop\\批量导入需求模板.xlsm");
    FileInputStream inputStream = null;
    try {
      inputStream = new FileInputStream(file);
      XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
      XSSFSheet sheetAt = workbook.getSheetAt(0);
      List<String> moduleNameList = new ArrayList<>();
      List<ModuleDeployNeedDTO> deployNeedDTOS = new LinkedList<>();
      ModuleDeployNeed deployNeed = new ModuleDeployNeed();
      for (int i = 1; i <= sheetAt.getLastRowNum(); i++) {
        ModuleDeployNeedDTO moduleDeployNeedDTO = new ModuleDeployNeedDTO();
        XSSFRow row = sheetAt.getRow(i);
        deployNeed.setNeedNumber(row.getCell(0).getStringCellValue());
        deployNeed.setNeedDescribe(row.getCell(1).getStringCellValue());
        deployNeed.setNeedContent(row.getCell(2).getStringCellValue());
        deployNeed.setEnvName(row.getCell(3).getStringCellValue());
        deployNeed.setDeveloper(row.getCell(6).getStringCellValue());
        deployNeed.setPstTest("通过".equals(row.getCell(7).getStringCellValue()) ? 1 : 0);
        deployNeed.setDrTest("通过".equals(row.getCell(8).getStringCellValue()) ? 1 : 0);
        if (row.getCell(5) != null)
          moduleNameList = Arrays.asList(row.getCell(5).getStringCellValue().split(" "));
        moduleDeployNeedDTO.setDeployNeed(deployNeed);
        moduleDeployNeedDTO.setModuleNameList(moduleNameList);
        deployNeedDTOS.add(moduleDeployNeedDTO);
      }
      System.out.println(deployNeedDTOS);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    
  }
  
  @Test
  public void deleteResource() {
    ModuleEnv moduleEnv = envMapper.selectOne(10);
    ExtensionsV1beta1Api extensionApi = K8sManagement.getExtensionApi(moduleEnv.getK8sConfig());
    V1DeleteOptions v1DeleteOptions = new V1DeleteOptions();
    v1DeleteOptions.setPropagationPolicy("Foreground");
    try {
      extensionApi.deleteNamespacedDeployment("bss-sysmgr-privilege-dubbo", "default", null,
          null, null, null, null, v1DeleteOptions);
    } catch (ApiException e) {
      System.out.println("删除错误：" + e.getResponseBody());
      ;
    }
  }
  
  @Test
  public void testOkHttp() throws IOException, ApiException {
    String url = "/apis/extensions/v1beta1/namespaces/default/deployments/crm-man-platform-kafka-manage";
    ModuleEnv moduleEnv = envMapper.selectOne(25);
    ArrayList<JsonObject> jsonObjects = K8sUtils.generatePatchPath("/spec/replicas", 1, null);
    String back = okhttpGetBack(moduleEnv.getK8sConfig(), url);
    Object load = Yaml.load(back);
    ExtensionsV1beta1Deployment object = K8sUtils.getObject(load, ExtensionsV1beta1Deployment.class);
    ExtensionsV1beta1Deployment cast = object.getClass().cast(object);
    System.out.println(cast);
  }
  
  @Test
  public void testTrans() throws IOException {
    List<ModuleDeployYaml> yamls =
        deployService.getModuleDeployByModuleAndEnvId(3136, 47, true);
    ModuleDeployYaml yaml = yamls.get(0);
    K8sYamlVo k8sYamlVo = K8sUtils.transObject2Vo(Yaml.load(yaml.getYamlJson()));
    V1Deployment deployment;
    if (K8sApiversionTypeEnum.EXTENSIONAPI.getApiVersionType().equals(k8sYamlVo.getApiVersion())) {
      ExtensionsV1beta1Deployment beta1Deployment =
          K8sUtils.getObject(k8sYamlVo.getO(), ExtensionsV1beta1Deployment.class);
      deployment = K8sUtils.toV1Deploy(beta1Deployment);
    } else {
      deployment = K8sUtils.getObject(k8sYamlVo.getO(), V1Deployment.class);
    }
    System.out.println(deployment);
  }
  
}
