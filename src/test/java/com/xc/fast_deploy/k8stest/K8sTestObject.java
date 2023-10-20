package com.xc.fast_deploy.k8stest;

import com.google.gson.Gson;
import com.xc.fast_deploy.utils.k8s.K8sUtils;
import com.xc.fast_deploy.vo.K8sYamlVo;
import io.kubernetes.client.util.Yaml;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class K8sTestObject {
  
  @Test
  public void testGetObject() {
//        CoreV1Api coreV1Api = K8sManagement.getCoreV1Api(K8sUtils.MY_OWN);
    String s = K8sUtils.okhttpGetBack(K8sUtils.MY_OWN,
        "/api/v1/namespaces/default/pods/app-test-one-75d955d49b-w2n2l");
//        System.out.println(s);
    final org.yaml.snakeyaml.Yaml yaml = new org.yaml.snakeyaml.Yaml();
    final Object load1 = yaml.load(s);
    final String toJson = new Gson().toJson(load1);
    
    final Object load;
    try {
      load = Yaml.load(toJson);
      final K8sYamlVo k8sYamlVo = K8sUtils.transObject2Vo(load);
      System.out.println(k8sYamlVo);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
