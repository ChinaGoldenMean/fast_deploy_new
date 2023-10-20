package com.xc.fast_deploy.moduleTest;

import com.alibaba.fastjson.JSON;
import com.xc.fast_deploy.dao.master_dao.ModuleDeployYamlMapper;
import com.xc.fast_deploy.model.master_model.ModuleDeployYaml;
import com.xc.fast_deploy.model.master_model.example.ModuleDeployYamlExample;
import com.xc.fast_deploy.utils.encyption_utils.Pbkdf2Sha256;
import com.xc.fast_deploy.utils.k8s.K8sUtils;
import com.xc.fast_deploy.vo.K8sYamlVo;
import io.kubernetes.client.custom.Quantity;
import io.kubernetes.client.openapi.models.V1Deployment;
import io.kubernetes.client.openapi.models.V1ResourceRequirements;
import io.kubernetes.client.util.Yaml;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class ModuleYamlTest {
  @Autowired
  private ModuleDeployYamlMapper deployYamlMapper;
  
  @Value("${file.storge.path.prefix}")
  private String storgePrefix;
  
  /**
   * 更改yaml_json的内容
   */
  @Test
  public void changeYamlJson() {
    ModuleDeployYamlExample deployYamlExample = new ModuleDeployYamlExample();
    deployYamlExample.createCriteria().andYamlPathLike("%prod30%")
        .andYamlTypeEqualTo("Deployment").andIsDeployedEqualTo(1)
        .andYamlNameEqualTo("bss-cpct-dubbo-cpc");
    List<ModuleDeployYaml> moduleDeployYamls = deployYamlMapper.selectByExampleWithBLOBs(deployYamlExample);
    for (ModuleDeployYaml moduleDeployYaml : moduleDeployYamls) {
      K8sYamlVo k8sYamlVo = null;
      if (StringUtils.isNotBlank(moduleDeployYaml.getYamlJson())) {
        try {
          k8sYamlVo =
              K8sUtils.transObject2Vo(Yaml.load(moduleDeployYaml.getYamlJson()));
        } catch (IOException e) {
          e.printStackTrace();
        }
//                String yamlJson =
//                        moduleDeployYaml.getYamlJson().replace("\"bss\": \"share\"","\"center\": \"crmhc\"");

//                List<String> stringList = Arrays.asList(moduleDeployYaml.getYamlName().split("-"));
//                String startStr = stringList.get(0) + '-' + stringList.get(1) + "-" + "service" + "-";
//                String yamlJson = moduleDeployYaml.getYamlJson().replace(startStr+"DUBBO_PORT_TO_REGISTRY","DUBBO_PORT_TO_REGISTRY");
//                yamlJson = yamlJson.replace(startStr+"ESB_UNREGISTER_URI","ESB_UNREGISTER_URI");
//                yamlJson = yamlJson.replace(startStr+"esb-url","esb-url");
//                yamlJson = yamlJson.replace(startStr+"APP_ENV","APP_ENV");
//                yamlJson = yamlJson.replace(startStr+"app-env","app-env");
//                yamlJson = yamlJson.replace(startStr+"DUBBO_IP_TO_REGISTRY","DUBBO_IP_TO_REGISTRY");
//                yamlJson = yamlJson.replace(startStr+"MY_POD_NAME","MY_POD_NAME");
//                yamlJson = yamlJson.replace(startStr+"POD_ID","POD_ID");
//                yamlJson = yamlJson.replace(startStr+"HOST_IP","HOST_IP");
        
        //        String yamlName = moduleDeployYaml.getYamlName().replace("ord-order","ord");
//                if (StringUtils.isNotBlank(moduleDeployYaml.getContainerName())) {
//                    String containerName = moduleDeployYaml.getContainerName().replace("ord-order", "ord");
//                    deployYaml.setContainerName(containerName);
//                }
//                String yamlJson =
//                        moduleDeployYaml.getYamlJson().replace("\"replicas\":1","\"replicas\":1,\"selector\":{\"matchLabels\":{\"name\": " + "\"" + moduleDeployYaml.getYamlName() + "\"" + "}}");

//                deployYaml.setId(moduleDeployYaml.getId());
//                deployYaml.setYamlJson(yamlJson);
        //  deployYaml.setYamlName(yamlName);
        //deployYamlMapper.updateByPrimaryKeySelective(deployYaml);
      } else {
        try {
          k8sYamlVo =
              K8sUtils.transYaml2Vo(new File(storgePrefix + moduleDeployYaml.getYamlPath()));
        } catch (IOException e) {
          e.printStackTrace();
        }
        
      }
      if (k8sYamlVo == null) continue;
      V1Deployment deployment =
          K8sUtils.getObject(k8sYamlVo.getO(), V1Deployment.class);
      V1ResourceRequirements requirements =
          deployment.getSpec().getTemplate().getSpec().getContainers().get(0).getResources();
      Map<String, Quantity> limit = requirements.getLimits();
      Map<String, String> map = new HashMap<>();
      for (String key : limit.keySet()) {
        map.put(key, limit.get(key).toSuffixedString());
      }
      System.out.println(map);
      String str = "{\"image\":\"20201124181430_5af9\",\"limit\":{\"memory\":\"8Gi\",\"cpu\":\"10\"}}";
      Map map1 = JSON.parseObject(str);
      for (Object obj : map1.keySet()) {
        if ("limit".equals(obj) && map.equals(map1.get(obj))) {
          System.out.println("ok");
        }
      }
//            if (requirements != null){
//                Map<String, Quantity> requests = requirements.getRequests();
//                Map<String,String> map = new HashMap<>();
//                if (requests != null) {
//                    Set<String> keyset = requests.keySet();
//                    for (String key : keyset) {
//                        map.put(key,requests.get(key).toSuffixedString());
//                    }
//                }
//                System.out.println(moduleDeployYaml.getYamlName()+"          "+map);
//            }
    }
  }
  
  @Test
  public void testPass() {
    System.out.println(Pbkdf2Sha256.encode("zhaojg_123"));
  }
  
  @Test
  public void changRequests() {
    //String yamlnames = "bss-srv-icss-ca-service,bss-srv-icss-ca-controller,bss-srv-icss-bt-service,bss-srv-icss-bt-controller,bss-srv-icss-front,bss-yz2-web,bss-order-openapi-dubbo,bss-shopcart-write-rule-engine-gray,bss-order-jt-query-controller,bss-order-process-dubbo,bss-order-process-receipt-inbound-dubbo,bss-order-process-receipt-dubbo,bss-order-shopcart-controller,bss-order-jt-report-mq-consumer-gray,crm-ord-orderitem-verify-trade-dubbo-inbound,crm-ord-packageitem-verify-trade-dubbo-inbound,bss-orderverify-rule-dispatcher,bss-order-shopcart-dubbo,bss-packageitem-verify-rule-engine,bss-orderitem-verify-rule-engine,bss-packageitem-verify-rule-engine,crm-ord-packageitem-verify-batch-dubbo-inbound,crm-ord-packageitem-verify-jt-dubbo-inbound,crm-ord-orderitem-verify-batch-dubbo-inbound,crm-ord-orderitem-verify-jt-dubbo-inbound,crm-order-quickshop-batch-dubbo-inbound-provide,bss-order-quickshop-dubbo,bss-order-quickshop-zq-dubbo";
    // String yamlnames = "bss-srv-cac-service,bss-srv-cac-front,bss-srv-icss-ca-service,bss-srv-icss-ca-controller,bss-srv-icss-bt-service,bss-srv-icss-bt-controller,bss-srv-icss-front,crm-srv-iqi-controller";
    //  String[] split = yamlnames.split(",");
    //  List<String> yamlnamelist = new ArrayList<>(Arrays.asList(split));
    ModuleDeployYamlExample yamlExample = new ModuleDeployYamlExample();
    yamlExample.createCriteria().andIsDeployedEqualTo(1).andYamlPathLike("%gray34%")
        .andIsOnlineYamlEqualTo(1);
    List<ModuleDeployYaml> moduleDeployYamls = deployYamlMapper.selectByExampleWithBLOBs(yamlExample);
    for (ModuleDeployYaml moduleDeployYaml : moduleDeployYamls) {
      if (StringUtils.isNotBlank(moduleDeployYaml.getYamlJson())
          && moduleDeployYaml.getYamlJson().contains("requests")) {
        String yamlJson = moduleDeployYaml.getYamlJson()
            .replaceFirst("requests.*?}", "requests\":{\"memory\":\"1Gi\",\"cpu\":\"100m\"}");
        ModuleDeployYaml deployYaml = new ModuleDeployYaml();
        deployYaml.setId(moduleDeployYaml.getId());
        deployYaml.setYamlJson(yamlJson);
        deployYamlMapper.updateByPrimaryKeySelective(deployYaml);
        //  System.out.println(yamlJson);
      }
      ;
    }
  }
  
  @Test
  public void testExample() {
    ModuleDeployYamlExample yamlExample = new ModuleDeployYamlExample();
    yamlExample.createCriteria().andIsOnlineYamlEqualTo(5);
    List<ModuleDeployYaml> moduleDeployYamls = deployYamlMapper.selectByExampleWithBLOBs(yamlExample);
    System.out.println(moduleDeployYamls.isEmpty());
    moduleDeployYamls.remove(null);
    System.out.println(moduleDeployYamls.isEmpty());
  }
}
