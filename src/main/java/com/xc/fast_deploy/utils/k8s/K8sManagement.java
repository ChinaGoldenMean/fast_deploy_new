package com.xc.fast_deploy.utils.k8s;

import cn.hutool.core.io.FileUtil;
import com.alibaba.fastjson.JSONObject;
import com.xc.fast_deploy.utils.k8s.dto.K8sConfigDTO;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.Configuration;
import io.kubernetes.client.openapi.apis.*;
import io.kubernetes.client.util.Config;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import io.kubernetes.client.util.ClientBuilder;
import io.kubernetes.client.util.Config;
import io.kubernetes.client.util.KubeConfig;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.SafeConstructor;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Map;

@Slf4j
public class K8sManagement {
  
  private static ApiClient apiClient;
  
  public static ApiClient getApiClient() {
    return apiClient;
  }
  
  private static K8sConfigDTO getK8sConfigDTO(String k8sConfig) {
    org.yaml.snakeyaml.Yaml yaml = new org.yaml.snakeyaml.Yaml();
    Object load = yaml.load(k8sConfig);
    String s = JSONObject.toJSONString(load);
    K8sConfigDTO configDTO = JSONObject.parseObject(s, K8sConfigDTO.class);
    return configDTO;
  }
  
  public static ApiClient getApiClient(String k8sConfig) {
    
    init(k8sConfig);
    return apiClient;
  }
  
  public static CoreV1Api getCoreV1Api(String k8sConfig) {
    init(k8sConfig);
    return new CoreV1Api();
  }
  
  public static AppsV1Api getExtensionApi(String k8sConfig) {
    init(k8sConfig);
    return new AppsV1Api();
  }
  
  public static AutoscalingV1Api getAutoscalingV1Api(String k8sConfig) {
    init(k8sConfig);
    //设置10分钟的超时时间
    return new AutoscalingV1Api();
  }

//
//    public static AppsV1beta1Api getAppsV1beta1Api(String k8sConfig) {
//        init(k8sConfig);
//        //设置10分钟的超时时间
//        return new AppsV1beta1Api();
//    }
  
  public static AppsV1Api getAppsV1Api(String k8sConfig) {
    init(k8sConfig);
    //设置10分钟的超时时间
    return new AppsV1Api();
  }
  
  public static NetworkingV1Api getNetworkingV1Api(String k8sConfig) {
    init(k8sConfig);
    return new NetworkingV1Api();
  }
//
//    public static AppsV1beta2Api getAppsV1beta2Api(String k8sConfig) {
//        init(k8sConfig);
//        //设置10分钟的超时时间
//        return new AppsV1beta2Api();
//    }
  
  private static void init(K8sConfigDTO dto, String k8sConfig) {
    InputStream resourceAsStream = null;
    try {
      resourceAsStream = new ByteArrayInputStream(k8sConfig.getBytes());
      StringReader stringReader = new StringReader(k8sConfig);
  
      apiClient = ClientBuilder.kubeconfig(KubeConfig.loadKubeConfig(stringReader)).build();
  
      OkHttpClient client = K8sUtils.getHttpClient(dto);
      apiClient.setHttpClient(client);
      Configuration.setDefaultApiClient(apiClient);
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      if (resourceAsStream != null) {
        try {
          resourceAsStream.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
    
  }
  
  private static void init(String k8sConfig) {
    K8sConfigDTO dto = getK8sConfigDTO(k8sConfig);
    InputStream resourceAsStream = null;
    try {
      resourceAsStream = new ByteArrayInputStream(k8sConfig.getBytes());
      StringReader stringReader = new StringReader(k8sConfig);
  
      apiClient = ClientBuilder.kubeconfig(KubeConfig.loadKubeConfig(stringReader)).build();
      FileUtil.createTempFile();
      OkHttpClient client = K8sUtils.getHttpClient(dto);
      //设置10分钟的读取超时时间
      //apiClient.getHttpClient().setReadTimeout(10 * 60, TimeUnit.SECONDS);
      apiClient.setHttpClient(client);
      Configuration.setDefaultApiClient(apiClient);
//            apiClient.setDebugging(true);
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      if (resourceAsStream != null) {
        try {
          resourceAsStream.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
  }
  
  public static KubeConfig loadKubeConfig(String input) {
    Yaml yaml = new Yaml();
    Object config = yaml.load(input);
    Map<String, Object> configMap = (Map) config;
    String currentContext = (String) configMap.get("current-context");
    ArrayList<Object> contexts = (ArrayList) configMap.get("contexts");
    ArrayList<Object> clusters = (ArrayList) configMap.get("clusters");
    ArrayList<Object> users = (ArrayList) configMap.get("users");
    Object preferences = configMap.get("preferences");
    KubeConfig kubeConfig = new KubeConfig(contexts, clusters, users);
    kubeConfig.setContext(currentContext);
    kubeConfig.setPreferences(preferences);
    return kubeConfig;
  }
  
}
