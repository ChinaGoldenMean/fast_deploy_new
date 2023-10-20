package com.xc.fast_deploy.httpGet;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xc.fast_deploy.dao.master_dao.ModuleEnvMapper;
import com.xc.fast_deploy.model.master_model.ModuleDeployYaml;
import com.xc.fast_deploy.model.master_model.ModuleEnv;
import com.xc.fast_deploy.myenum.k8sEnum.K8sKindTypeEnum;
import com.xc.fast_deploy.service.common.ModuleDeployService;
import com.xc.fast_deploy.service.k8s.K8sService;
import com.xc.fast_deploy.utils.HttpUtils;
import com.xc.fast_deploy.vo.k8s_vo.K8sNodeResourcesVO;
import io.kubernetes.client.openapi.models.V1Deployment;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class httptest {
  @Autowired
  private ModuleEnvMapper envMapper;
  @Autowired
  private ModuleDeployService deployService;
  @Autowired
  private K8sService k8sService;
  
  @Value("${myself.httpUrl.getNodeResources}")
  private String getNodeResources;
  
  @Test
  public void testNoderesources() {
    ModuleEnv moduleEnv = envMapper.selectOne(30);
    List<ModuleDeployYaml> yamlList =
        deployService.getModuleDeployByModuleAndEnvId(581, 30, true);
    if (yamlList != null && yamlList.size() > 0) {
      V1Deployment deployment =
          k8sService.readNameSpacedResource(yamlList.get(0).getYamlName(),
              moduleEnv, yamlList.get(0).getYamlNamespace(),
              K8sKindTypeEnum.DEPLOYMENT.getKindType(), V1Deployment.class);
      Integer replicas = deployment.getSpec().getReplicas();
      Map<String, String> nodeSelector = deployment.getSpec().getTemplate().getSpec().getNodeSelector();
      StringBuilder sb = new StringBuilder();
      String url = sb.append(getNodeResources).append("search=")
          .append("center").append(":").append(nodeSelector.get("center")).append("&")
          .append("paasid=").append(moduleEnv.getPaasId()).toString();
      String result = HttpUtils.httpGetUrl(url, null, null);
      Map map = JSON.parseObject(result);
      Integer total = (Integer) map.get("count");
      if (total > 10) {
        url = sb.append("&").append("page_size=").append(total).toString();
        result = HttpUtils.httpGetUrl(url, null, null);
      }
      map = JSON.parseObject(result);
      Object results = map.get("results");
      float cpu_remian_total = 0;
      float mem_remian_total = 0;
      List<K8sNodeResourcesVO> list = JSONObject.parseArray(results.toString(), K8sNodeResourcesVO.class);
      for (K8sNodeResourcesVO resourcesVO : list) {
        String cpu_request = resourcesVO.getCpu_request();
        String mem_request = resourcesVO.getMemory_request();
        cpu_remian_total += Float.valueOf(cpu_request.substring(cpu_request.indexOf("(") + 1, cpu_request.indexOf("%")));
        mem_remian_total += Float.valueOf(mem_request.substring(cpu_request.indexOf("(") + 1, mem_request.indexOf("%")));
      }
//            Map<String, String> containerRequest = k8sService.getContainerRequest(deployment);
//            Matcher cpu_m = Pattern.compile("\\d+").matcher(containerRequest.get("cpu"));
//            Matcher mem_m = Pattern.compile("\\d+").matcher(containerRequest.get("memory"));
//            if (cpu_m.find()) System.out.println(cpu_m.group());
//            if (mem_m.find()) System.out.println(mem_m.group());
      //Integer pod_cpu_request = Integer.valueOf(Pattern.compile("\\d+").matcher(containerRequest.get("cpu")).group(0));
      //  Integer pod_mem_request = Integer.valueOf(Pattern.compile("\\d+").matcher(containerRequest.get("memory")).group(0));
      System.out.println(cpu_remian_total / total + ":" + mem_remian_total / total);
    }
  }
}
