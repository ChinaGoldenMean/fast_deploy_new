package com.xc.fast_deploy.service.k8s.impl;

import com.xc.fast_deploy.service.k8s.K8sResourceService;
import com.xc.fast_deploy.utils.k8s.K8sUtils;
import com.xc.fast_deploy.vo.K8sYamlVo;
import io.kubernetes.client.util.Yaml;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class K8sResourceServiceImpl implements K8sResourceService {
  
  public static void test1() {
    try {
      List<Object> objects =
          Yaml.loadAll(new File("f:/home/service-apollo-admin-server-gray.yaml"));
      if (objects != null && objects.size() > 0) {
        for (Object o : objects) {
          K8sYamlVo k8sYamlVo = K8sUtils.transObject2Vo(o);
          System.out.println(k8sYamlVo.getMetadataName());
          System.out.println(k8sYamlVo.getKind());
          System.out.println(k8sYamlVo.getApiVersion());
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  public static void main(String[] args) {
    test1();
  }
}
