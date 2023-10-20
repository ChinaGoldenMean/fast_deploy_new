package com.xc.fast_deploy.utils.k8s;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.xc.fast_deploy.myenum.k8sEnum.K8sApiversionTypeEnum;
import com.xc.fast_deploy.myenum.k8sEnum.K8sKindTypeEnum;
import com.xc.fast_deploy.utils.constant.K8sNameSpace;
import com.xc.fast_deploy.utils.k8s.dto.K8sConfigDTO;
import com.xc.fast_deploy.vo.K8sPatchVo;
import com.xc.fast_deploy.vo.K8sYamlVo;
import io.kubernetes.client.custom.IntOrString;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.models.*;
import io.kubernetes.client.util.SSLUtils;
import io.kubernetes.client.util.Yaml;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;

import javax.net.ssl.*;
import java.io.*;
import java.lang.reflect.Field;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Slf4j
public class K8sUtils {
  
  public static final String THREAD_DUMP_JAVAPATH = "thread-dump-java-";
  
  public static final String MY_OWN = "apiVersion: v1\n" +
      "clusters:\n" +
      "- cluster:\n" +
      "    certificate-authority-data: LS0tLS1CRUdJTiBDRVJUSUZJQ0FURS0tLS0tCk1JSUN5RENDQWJDZ0F3SUJBZ0lCQURBTkJna3Foa2lHOXcwQkFRc0ZBREFWTVJNd0VRWURWUVFERXdwcmRXSmwKY201bGRHVnpNQjRYRFRFNE1USXdOakE0TkRreU1sb1hEVEk0TVRJd016QTRORGt5TWxvd0ZURVRNQkVHQTFVRQpBeE1LYTNWaVpYSnVaWFJsY3pDQ0FTSXdEUVlKS29aSWh2Y05BUUVCQlFBRGdnRVBBRENDQVFvQ2dnRUJBTlRUCjlDc3hjd2s0N3NwTUhBeVNWZy9pbm1DbWEyeUVsdUpob1VVd05NaFFVR2txVzFhZVZWTEhSSkVZU0lubmMySmoKNU13MHFvcy9hZ2U3QWVzR1lXUmZuR1hQNmdmd0RrS0U4NmZ0UkJPUklNYmhWNTVacVZxQ2FjbDl3bmVVNi85VQpYcUVkV1RSTGRYSzJvS3VJWGtLaTdmbEFuYlpaN3oyZmY2RU1JaER6TGlJL1lMSHlTNUhuUTdobFRWWXJXM1ZmCk1xbkwzb2Voei9QeTZuYW5GT0JDK2RJVzExV1Jwc2xOaitPUEhJVTBvbytSNHF1cmlrMC9yVDJMNjdBbFZLN24KdXdWaTU3SWFBWTM4R1lkL2dwdUovbjhmVjdMTHI1Ry8rQUdhRnRSTitJZU9tTi9WZnVUK09TSFdDRmFZbXhjeApncU5yWHNXYnQ2YTJlL0lSTTg4Q0F3RUFBYU1qTUNFd0RnWURWUjBQQVFIL0JBUURBZ0trTUE4R0ExVWRFd0VCCi93UUZNQU1CQWY4d0RRWUpLb1pJaHZjTkFRRUxCUUFEZ2dFQkFERzhyd0JYUWNucXloN1lCek1zSm5SUWtSV1IKRmJscU5GbnZpWTVUdTUzU0dOaUF3dmVzdlp1NTVLclBjUW9HMmExdjBpY0FFblkzaXliYTN3bmEzcFMvaGFtbQpZZ21RaWFNcVNYcHVSSEsvVXJEcVdsekJqVUM0aTFLNFZad3dIeVMrMDJ5QkwxNE1KaG5BSHAvUzdVSm9GT3ZaCkxJZndsc1NLSTdjZjFMTEd2cTJTc2xXeXY5cjZDcWJWYk1yLzROMk9uNUg0OWFud2xjMzZXOVRRTXhxdnY3cTgKZVZ1K0lFV1NiUXhRRTZvY2xnUFR2QVIrVXdML1cyUWdDdEhuRmtoQWV6WTRBR2VGWFhoZENRcDE5cUZIMlNnbwp3K1ZTVnc2bVdhUm9lWDZaZ0E2ZVRNaXV2MDc5OHFHYlNIa2g3L2RBazBxS0haempjWXRkKzd5VDg2az0KLS0tLS1FTkQgQ0VSVElGSUNBVEUtLS0tLQo=\n" +
      "    server: https://134.96.253.219:6443\n" +
      "  name: kubernetes\n" +
      "contexts:\n" +
      "- context:\n" +
      "    cluster: kubernetes\n" +
      "    user: kubernetes-admin\n" +
      "  name: kubernetes-admin@kubernetes\n" +
      "current-context: kubernetes-admin@kubernetes\n" +
      "kind: Config\n" +
      "preferences: {}\n" +
      "users:\n" +
      "- name: kubernetes-admin\n" +
      "  user:\n" +
      "    client-certificate-data: LS0tLS1CRUdJTiBDRVJUSUZJQ0FURS0tLS0tCk1JSUM4akNDQWRxZ0F3SUJBZ0lJTkpYcHVTUGczR0l3RFFZSktvWklodmNOQVFFTEJRQXdGVEVUTUJFR0ExVUUKQXhNS2EzVmlaWEp1WlhSbGN6QWVGdzB4T0RFeU1EWXdPRFE1TWpKYUZ3MHlNVEV5TWpJd016SXdNRFphTURReApGekFWQmdOVkJBb1REbk41YzNSbGJUcHRZWE4wWlhKek1Sa3dGd1lEVlFRREV4QnJkV0psY201bGRHVnpMV0ZrCmJXbHVNSUlCSWpBTkJna3Foa2lHOXcwQkFRRUZBQU9DQVE4QU1JSUJDZ0tDQVFFQXVla21oWnVXRjhpSE9UKzcKbkNFWEtzVkNLOG1VTGVEeVdwK1Byd1VYa1pQZmVOQVU5SW1Pc3lhU1pzemZLY0h0ckwzdzNRK1dzRnQ4UjRYdApwRVdrVnVlQTYzbUlYbVZHazRiWXNmbGNzdERFS0ZieVRIZGlvYmI5ME9DcjJ5ckJpT2ZtR0t4OWM0RFJFTS93Cm5lbmpxdWV2TGl0Z3dyYU8zV0pEQ2dLWWE0OC9sY01sN1IxYnJvTUk0eFF5ODRGSHlMTTVLd2lsQ01OK2RzVXoKR1JPc2ZITjBqNDVlcWJ2UnJsNEkyZ29tWFFCWkkyZzRqS2Vmc2thRU5jbFZvbGQ0TDUrZld3NTY3eFNhL05MUApQS24rVkpDUXI1czBWNEVHZTdJT0FXYS9CTkNpUU1SQUFBNytsQWZhcXIyaWg5RkxtM3VkSlFJQTJuZjE4Q0xBClRoTG83UUlEQVFBQm95Y3dKVEFPQmdOVkhROEJBZjhFQkFNQ0JhQXdFd1lEVlIwbEJBd3dDZ1lJS3dZQkJRVUgKQXdJd0RRWUpLb1pJaHZjTkFRRUxCUUFEZ2dFQkFBM0xjWW9JYnNsM0M5bDgyQjFFSUJlVVdRVTNRV3dLY3pzTgpMTVQ0TU44Ni82bU9iY25xWkJ0dk96MDU0d0NaZ1dwaG5adnYrUzgzUlRlcTJJdHhmYkk2Sit1L28vRTU3UFR1Ck1jMUVxOFdHQVdLcTVSOGNLK25IY1VaOENrUFQzZis4R0hPaUxNYkx1NFNqRFEvT0diTjRLTERBcU9xd0JxMGgKTnQ3V3Jsd2RENHRsdTdjckJSNjEwRkdKUENPTVM3V21HbjJWUEQvV085L0Y1TStmS3JvR1hZaHgxejNsb2c5RwpUbEk1ZnJ6RGFOTTFwRzdyZEpTdnptbEkwaWZZU2tOYjIvTUdjRW9Rb2dvSE14N3VBZCtUOGpyZllGSUNqTEhRCmRidFg3ZnpMbVl6WTdvSEE5cW80TXU0ZHpIeisxcHpoTEVsYitJMXZVRFF5YmtzTlBvTT0KLS0tLS1FTkQgQ0VSVElGSUNBVEUtLS0tLQo=\n" +
      "    client-key-data: LS0tLS1CRUdJTiBSU0EgUFJJVkFURSBLRVktLS0tLQpNSUlFb3dJQkFBS0NBUUVBdWVrbWhadVdGOGlIT1QrN25DRVhLc1ZDSzhtVUxlRHlXcCtQcndVWGtaUGZlTkFVCjlJbU9zeWFTWnN6ZktjSHRyTDN3M1ErV3NGdDhSNFh0cEVXa1Z1ZUE2M21JWG1WR2s0YllzZmxjc3RERUtGYnkKVEhkaW9iYjkwT0NyMnlyQmlPZm1HS3g5YzREUkVNL3duZW5qcXVldkxpdGd3cmFPM1dKRENnS1lhNDgvbGNNbAo3UjFicm9NSTR4UXk4NEZIeUxNNUt3aWxDTU4rZHNVekdST3NmSE4wajQ1ZXFidlJybDRJMmdvbVhRQlpJMmc0CmpLZWZza2FFTmNsVm9sZDRMNStmV3c1Njd4U2EvTkxQUEtuK1ZKQ1FyNXMwVjRFR2U3SU9BV2EvQk5DaVFNUkEKQUE3K2xBZmFxcjJpaDlGTG0zdWRKUUlBMm5mMThDTEFUaExvN1FJREFRQUJBb0lCQUdkMVYwbXRIdWJYWW1FNgpsWFk2ZUFFR1MxL0F5eVNJOVhYM0srZXpScjF0OUVQd1VHa1JrQWNrcFA1dWJwWjNaSVBvSS8xeGFtTjBWZ0liCmh2Y1BCbmlTVm8wMFVlNXVIOHVEbEYzZXJuYVlkVUVadWJkU0RPSi95bG9PWGJVVzR5TUVJbW1DdkVHbDR6S2UKOVlGN29vWCtidCticGpwVmk4V3BwRE1TNUsyZDA0WmZTOVNtSFN0N0hzbTNGcWc3bVdkZGNSYXdrZ0NaRTNSegpJaUY1OUcrL1J0MTNIb0lEUmdlbUNNN2VXdkRVOUoyQ2Z6OGcrdHVOVHRYdmVrZXYzL1Y1aTNhRFgzN0J6b2l5CkRLNVE0azc2TXVSNi9OT3pCV3hDaVpGaDlvbmdzVVlFZjZRb1R3ZUNDUHNGejlFVWIxNERNMjJsQjE2M1lyR3AKOVdSZDl3VUNnWUVBeUI5SndLYjBZenE5Sys4SVBvNUNBZ1Rsa2ZxYkxyTlVXSnVKcmxzaUlYVjVMOW1sSGs5OApjUnYzOXQxUFVnSTNybmlOaTJQeWNjakhJU3cxMjNSc3dLbTFSSDRtQ3dVWVZRaGtRUTVvVW5URnM5WExXalFTClFEcWpRcDJZUG5FZktERnRhR1U4SW1QajZROG5tN0xqMjYwL1NCcWY1WjA3a0g4b1pTR0xmbThDZ1lFQTdkSUgKSW1pcEFOMDZmSW1tUGlVUHBHdUhyb2ZJN1BHN1dVaHRYMU12ejhwSXNNS1dpeDcxRUZsZnFKQytmckowa01xRQpja0dCVjVNbE0xTmY0bEs5S0VIYVgxclhpT0Z5cUlldG1aSUdTSytQTnJ3aW9OWFNWNDBaRHRGTnVpZzd5bUl1CnJGeUhKK25NdnpWOS9KNlZIaGpFdkJQYzhaRkxJSGcxMHJQeFBHTUNnWUE4aEFtZmNsMHdySkNMK29wNXNEY1IKdVQxYXVYZTMxWTdLQks3THhNODdZdlMwblpJcTZrcHRRZEhvR3VCam9qd0lUSGd4RUpZK0JrTUJLd2RXUjR5agpiaTVjZWZFekgrVk53VzNmcG1XeG1aSTNZWTFPeDdSRksvWTJyTVZmRElJcGUxamtXc2ltZGFKejFadGFuK3Y3CmdCWkV6WmhRclExWUVydTZhVHh2alFLQmdRQ0Q3NDd4c2VxQWhqc2FPQ3M0TXN0WXhpY0IxbWJMdk5mWkVtdEwKQlJWelM1L0VJRS93WW41R1VhN1dEMmIvVnZXQUZqb2dRT01HdUtWY3NoWnhzc0VxQWs2cHMvWDdCRWZkNDFlVgo2UUwvZEJZWm9ZbjhmR016R3g0WnNGaFV2RmxmVDlUZzNueEUwbG82Z2V5aEowRkc3eXJGeEtkQWhRQ094T0huClFOakdUUUtCZ0dPRHo2ZHJVSkZFaGE1WElNUlJ6MWxWeUI2aHF6SVBJZkUwWjRrOFN1ckE5aGFwV3gxVHpmOHUKU1Y5VGQ2cldPd3dSK21JTFZTY0lLSmV5bXlQYWVyRVNWRytiNXBuOVVybXdiSHZWR2srK0pPc09laFRhd2o4MApxV0o0YW1WdjFWZ2E3anA5cXd0QVhWTWxwWnNnWEF2MmlSRjd4ZUN3Q3dXYnprOVVWYWlJCi0tLS0tRU5EIFJTQSBQUklWQVRFIEtFWS0tLS0tCg==";
  
  public static final MediaType JSON = MediaType
      .parse("application/json; charset=utf-8");
  
  public static K8sYamlVo transObject2Vo(Object o) {
    K8sYamlVo k8sYamlVo = null;
    if (o != null) {
      k8sYamlVo = new K8sYamlVo();
      Field[] fields = o.getClass().getDeclaredFields();
      for (Field field : fields) {
        field.setAccessible(true);
        String name = field.getName();
        if ("apiVersion".equals(name)) {
          try {
            String apiType = (String) field.get(o);
            k8sYamlVo.setApiVersion(apiType);
          } catch (IllegalAccessException e) {
            e.printStackTrace();
          }
        }
        if ("kind".equals(name)) {
          try {
            String kind = (String) field.get(o);
            k8sYamlVo.setKind(kind);
          } catch (IllegalAccessException e) {
            e.printStackTrace();
          }
        }
        if ("metadata".equals(name)) {
          try {
            V1ObjectMeta objectMeta = (V1ObjectMeta) field.get(o);
            k8sYamlVo.setMetadataName(objectMeta.getName());
            k8sYamlVo.setLabelMap(objectMeta.getLabels());
            k8sYamlVo.setNamespace(StringUtils.isNotBlank(objectMeta.getNamespace()) ? objectMeta.getNamespace() : K8sNameSpace.DEFAULT);
          } catch (IllegalAccessException e) {
            e.printStackTrace();
          }
        }
        //判断好条件后 跳出循环
        if (StringUtils.isNotBlank(k8sYamlVo.getApiVersion()) &&
            StringUtils.isNotBlank(k8sYamlVo.getKind())
            && StringUtils.isNotBlank(k8sYamlVo.getMetadataName())) {
          break;
        }
      }
      k8sYamlVo.setO(o);
    }
    return k8sYamlVo;
  }
  
  /**
   * 将yaml_file文件转换为java类
   *
   * @param yamlFile
   * @return
   */
  public static K8sYamlVo transYaml2Vo(File yamlFile) throws IOException {
    K8sYamlVo k8sYamlVo = null;
    if (yamlFile != null && yamlFile.exists()) {
      Object o = Yaml.load(yamlFile);
      k8sYamlVo = transObject2Vo(o);
    }
    return k8sYamlVo;
  }
  
  @SneakyThrows
  public static void main(String[] args) {
    File yamlPathFile = new File("E:\\Users\\litiewang\\Downloads\\bss-order-front-deploy.yaml");
    K8sYamlVo k8sYamlVoPath = null;
    k8sYamlVoPath = K8sUtils.transYaml2Vo(yamlPathFile);
    System.out.println(k8sYamlVoPath);
  }
  
  public static List<K8sYamlVo> transYaml2VoList(File yamlFile) throws IOException {
    List<K8sYamlVo> k8sYamlVoList = new ArrayList<>();
    if (yamlFile != null && yamlFile.exists()) {
      List<Object> objectList = Yaml.loadAll(yamlFile);
      if (objectList != null && objectList.size() > 0) {
        for (Object o : objectList) {
          k8sYamlVoList.add(transObject2Vo(o));
        }
      }
      
    }
    return k8sYamlVoList;
  }
  
  /**
   * 转换object类型为其对应的类
   *
   * @param o
   * @param clazz
   * @param <T>
   * @return
   */
  public static <T> T getObject(Object o, Class<T> clazz) {
    if (o != null && o.getClass().equals(clazz)) {
      return clazz.cast(o);
    }
    return null;
  }
  
  /**
   * 生成为patch所用的template里的mirror信息
   *
   * @param value
   */
  public static ArrayList<JsonObject> generatePatchPath(String path, Object value, String op) {
    K8sPatchVo k8sPatchVo = new K8sPatchVo();
    if (StringUtils.isNotBlank(op)) {
      k8sPatchVo.setOp("add");
    }
    k8sPatchVo.setPath(path);
    k8sPatchVo.setValue(value);
    JsonElement element = new Gson().fromJson(JSONObject.toJSONString(k8sPatchVo), JsonElement.class);
    JsonObject jsonObject = element.getAsJsonObject();
    ArrayList<JsonObject> arr = new ArrayList<>();
    arr.add(jsonObject);
    return arr;
  }
  
  public static OkHttpClient getHttpClient(K8sConfigDTO configDTO) {
    OkHttpClient client;
    X509TrustManager trustManager;
    SSLSocketFactory sslSocketFactory;
    try {
      trustManager = trustManagerForCertificates(configDTO.getClusters().get(0).getCluster().getCertificateAuthorityData());
      SSLContext sslContext = SSLContext.getInstance("TLS");
      KeyManager[] managers = SSLUtils.keyManagers(
          Base64.decodeBase64(configDTO.getUsers().get(0).getUser().getClientCertificateData()),
          Base64.decodeBase64(configDTO.getUsers().get(0).getUser().getClientKeyData()),
          "RSA", "", null, null);
      
      sslContext.init(managers, new TrustManager[]{trustManager}, null);
      sslSocketFactory = sslContext.getSocketFactory();
    } catch (Exception e) {
      e.printStackTrace();
      throw new RuntimeException();
    }
    
    client = new OkHttpClient.Builder().connectTimeout(3, TimeUnit.MINUTES).readTimeout(5, TimeUnit.MINUTES)
        .sslSocketFactory(sslSocketFactory, trustManager)
        .build();
    return client;
  }
  
  /**
   * okHttp 请求利用k8sConfig配置文件解析成ssl形式请求获取相关数据
   *
   * @param k8sConfig
   * @param url
   * @return
   */
  public static String okhttpGetBack(String k8sConfig, String url) {
    org.yaml.snakeyaml.Yaml yaml = new org.yaml.snakeyaml.Yaml();
    Object load = yaml.load(k8sConfig);
    String s = JSONObject.toJSONString(load);
    K8sConfigDTO configDTO = JSONObject.parseObject(s, K8sConfigDTO.class);
    if (configDTO == null) {
      return null;
    }
    String back = null;
    OkHttpClient client = getHttpClient(configDTO);
    Request request = new Request.Builder()
        .url(configDTO.getClusters().get(0).getCluster().getServer() + url)
        .build();
    try {
      Response response = client.newCall(request).execute();
      back = response.body().string();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return back;
  }
  
  public static InputStream okhttpGetBackByInputStream(String k8sConfig, String url) {
    org.yaml.snakeyaml.Yaml yaml = new org.yaml.snakeyaml.Yaml();
    Object load = yaml.load(k8sConfig);
    String s = JSONObject.toJSONString(load);
    K8sConfigDTO configDTO = JSONObject.parseObject(s, K8sConfigDTO.class);
    if (configDTO == null) {
      return null;
    }
    InputStream back = null;
    OkHttpClient client = getHttpClient(configDTO);
    Request request = new Request.Builder()
        .url(configDTO.getClusters().get(0).getCluster().getServer() + url)
        .build();
    try {
      Response response = client.newCall(request).execute();
      back = response.body().byteStream();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return back;
  }
  
  public static String okhttpDeleteBack(String k8sConfig, String url) throws ApiException {
    org.yaml.snakeyaml.Yaml yaml = new org.yaml.snakeyaml.Yaml();
    Object load = yaml.load(k8sConfig);
    String s = JSONObject.toJSONString(load);
    K8sConfigDTO configDTO = JSONObject.parseObject(s, K8sConfigDTO.class);
    if (configDTO == null) {
      return null;
    }
    String back = null;
    OkHttpClient client = getHttpClient(configDTO);
    V1DeleteOptions v1DeleteOptions = new V1DeleteOptions();
    v1DeleteOptions.setPropagationPolicy("Foreground");
    ApiClient apiClient = K8sManagement.getApiClient(k8sConfig);
    RequestBody body = apiClient.serialize(v1DeleteOptions, "application/json");
    Request request = new Request.Builder()
        .url(configDTO.getClusters().get(0).getCluster().getServer() + url)
        .delete(body).build();
    try {
      Response response = client.newCall(request).execute();
      back = response.body().string();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return back;
  }
  
  public static String okhttpPatchBack(String k8sConfig, String url, ArrayList<JsonObject> patchList)
      throws ApiException {
    org.yaml.snakeyaml.Yaml yaml = new org.yaml.snakeyaml.Yaml();
    Object load = yaml.load(k8sConfig);
    String s = JSONObject.toJSONString(load);
    K8sConfigDTO configDTO = JSONObject.parseObject(s, K8sConfigDTO.class);
    if (configDTO == null) {
      return null;
    }
    String back = null;
    OkHttpClient client = getHttpClient(configDTO);
    ApiClient apiClient = K8sManagement.getApiClient(k8sConfig);
    RequestBody body = apiClient.serialize(patchList, "application/json-patch+json");
    Request request = new Request.Builder()
        .url(configDTO.getClusters().get(0).getCluster().getServer() + url)
        .patch(body).build();
    try {
      Response response = client.newCall(request).execute();
      back = response.body().string();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return back;
  }
  
  public static String okhttpPostBack(String k8sConfig, String url, Object obj)
      throws ApiException {
    org.yaml.snakeyaml.Yaml yaml = new org.yaml.snakeyaml.Yaml();
    Object load = yaml.load(k8sConfig);
    String s = JSONObject.toJSONString(load);
    K8sConfigDTO configDTO = JSONObject.parseObject(s, K8sConfigDTO.class);
    if (configDTO == null) {
      return null;
    }
    String back = null;
    OkHttpClient client = getHttpClient(configDTO);
    ApiClient apiClient = K8sManagement.getApiClient(k8sConfig);
    RequestBody body = apiClient.serialize(obj, "application/json");
    Request request = new Request.Builder()
        .url(configDTO.getClusters().get(0).getCluster().getServer() + url)
        .post(body).build();
    try {
      Response response = client.newCall(request).execute();
      back = response.body().string();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return back;
  }
  
  private static X509TrustManager trustManagerForCertificates(String cad) {
    try {
      CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
      Collection<? extends Certificate> certificates = certificateFactory.generateCertificates(
          new ByteArrayInputStream(Base64.decodeBase64(cad)));
      
      char[] password = "".toCharArray(); // Any password will work.
      KeyStore keyStore = newEmptyKeyStore(password);
      int index = 0;
      for (Certificate certificate : certificates) {
        String certificateAlias = Integer.toString(index++);
        keyStore.setCertificateEntry(certificateAlias, certificate);
      }
      
      KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(
          KeyManagerFactory.getDefaultAlgorithm());
      keyManagerFactory.init(keyStore, password);
      TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(
          TrustManagerFactory.getDefaultAlgorithm());
      trustManagerFactory.init(keyStore);
      TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
      
      if (trustManagers.length != 1 || !(trustManagers[0] instanceof X509TrustManager)) {
        throw new IllegalStateException("Unexpected default trust managers:"
            + Arrays.toString(trustManagers));
      }
      
      return (X509TrustManager) trustManagers[0];
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }
  
  private static KeyStore newEmptyKeyStore(char[] password) throws KeyStoreException, CertificateException, NoSuchAlgorithmException {
    try {
      KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
      keyStore.load(null, password);
      return keyStore;
    } catch (IOException e) {
      throw new AssertionError(e);
    }
  }
  
  /**
   * 根据默认文件生成deployment内容
   *
   * @param name
   * @return
   */
  public static V1Deployment generateDeployMents(String name) {
    InputStream inputStream = K8sUtils.class.getClassLoader().getResourceAsStream("default.yaml");
    InputStreamReader streamReader = new InputStreamReader(inputStream);
    V1Deployment beta1Deployment = Yaml.loadAs(streamReader, V1Deployment.class);
    beta1Deployment.getMetadata().getLabels().put("name", name);
    beta1Deployment.getMetadata().setName(name);
    beta1Deployment.getSpec().getTemplate().getMetadata().getLabels().put("name", name);
    beta1Deployment.getSpec().getTemplate().getSpec().getContainers().get(0).setName(name);
    return beta1Deployment;
  }
  
  /**
   * 生成svc的实体类内容
   *
   * @param name
   * @param targetPort
   * @param port
   * @param nodePort
   * @return
   */
  public static V1Service genV1Svc(String name, String namespace, Integer targetPort, Integer port, Integer nodePort) {
    V1Service v1Service = new V1Service();
    v1Service.setKind(K8sKindTypeEnum.SERVICE.getKindType());
    v1Service.setApiVersion(K8sApiversionTypeEnum.COREAPIV1.getApiVersionType());
    V1ObjectMeta objectMeta = new V1ObjectMeta();
    objectMeta.setName(name);
    objectMeta.setNamespace(namespace);
    Map<String, String> labelMap = new HashMap<>();
    labelMap.put("name", name);
    objectMeta.setLabels(labelMap);
    v1Service.setMetadata(objectMeta);
    V1ServiceSpec serviceSpec = new V1ServiceSpec();
    List<V1ServicePort> ports = new ArrayList<>();
    V1ServicePort servicePort = new V1ServicePort();
    servicePort.setPort(port);
    servicePort.setProtocol("TCP");
    if (nodePort != null && nodePort > 0) {
      serviceSpec.setType("NodePort");
      servicePort.setNodePort(nodePort);
    }
    if (targetPort != null && targetPort > 0) {
      servicePort.setTargetPort(new IntOrString(targetPort));
    }
    ports.add(servicePort);
    serviceSpec.setPorts(ports);
    Map<String, String> selector = new HashMap<>();
    selector.put("name", name);
    serviceSpec.setSelector(selector);
    v1Service.setSpec(serviceSpec);
    return v1Service;
  }
  
  public static String getMountLogPathFromPod(V1Pod v1Pod) {
    String mountPath = null;
    if (v1Pod != null && v1Pod.getStatus() != null) {
      List<V1VolumeMount> volumeMounts = v1Pod.getSpec()
          .getContainers().get(0).getVolumeMounts();
      
      if (volumeMounts != null && volumeMounts.size() > 0) {
        for (V1VolumeMount volumeMount : volumeMounts) {
          String name = volumeMount.getName();
          if (name.equals("app-log") || name.contains("app-log")) {
            mountPath = volumeMount.getMountPath();
            break;
          }
        }
        if (StringUtils.isBlank(mountPath)) {
          for (V1VolumeMount volumeMount : volumeMounts) {
            String name = volumeMount.getName();
            if (name.contains("log")) {
              mountPath = volumeMount.getMountPath();
              break;
            }
          }
        }
      }
    }
    return mountPath;
  }
  
  public static V1Deployment toV1Deploy(V1Deployment deployment) {
    V1Deployment v1Deployment = new V1Deployment();
    if (deployment != null) {
      v1Deployment.setApiVersion(deployment.getApiVersion());
      v1Deployment.setKind(deployment.getKind());
      v1Deployment.setMetadata(deployment.getMetadata());
      v1Deployment.setSpec(toV1Spec(deployment.getSpec()));
    }
    return v1Deployment;
  }
  
  public static V1DeploymentSpec toV1Spec(V1DeploymentSpec spec) {
    V1DeploymentSpec v1Spec = new V1DeploymentSpec();
    if (spec != null) {
      v1Spec.setReplicas(spec.getReplicas());
      v1Spec.setSelector(spec.getSelector());
      v1Spec.setMinReadySeconds(spec.getMinReadySeconds());
      v1Spec.setPaused(spec.getPaused());
      v1Spec.setProgressDeadlineSeconds(spec.getProgressDeadlineSeconds());
      v1Spec.setRevisionHistoryLimit(spec.getRevisionHistoryLimit());
      v1Spec.setTemplate(spec.getTemplate());
      v1Spec.setStrategy(toV1Strategy(spec.getStrategy()));
    }
    return v1Spec;
  }
  
  public static V1DeploymentStrategy toV1Strategy(V1DeploymentStrategy strategy) {
    V1DeploymentStrategy v1strategy = new V1DeploymentStrategy();
    if (strategy != null) {
      v1strategy.setType(strategy.getType());
      v1strategy.setRollingUpdate(toV1RollingUpdate(strategy.getRollingUpdate()));
    }
    return v1strategy;
  }
  
  public static V1RollingUpdateDeployment toV1RollingUpdate(V1RollingUpdateDeployment rollingUpdateDeployment) {
    V1RollingUpdateDeployment v1RollingUpdateDeployment = new V1RollingUpdateDeployment();
    if (rollingUpdateDeployment != null) {
      v1RollingUpdateDeployment.setMaxSurge(rollingUpdateDeployment.getMaxSurge());
      v1RollingUpdateDeployment.setMaxUnavailable(rollingUpdateDeployment.getMaxUnavailable());
    }
    return v1RollingUpdateDeployment;
  }
  
}
