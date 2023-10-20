package com.xc.fast_deploy.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.*;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.Base64;
import java.util.concurrent.TimeUnit;

import static com.xc.fast_deploy.utils.FoldUtils.SEP;

@Slf4j
@Configuration
public class HttpUtils {
  
  public static String GET_PERMISSION_URL;
  public static String JUDGE_IS_LOGIN;
  public static String REDIRECT_LOGIN;
  public static String LOGIN_URL;
  public static String LOGOUT_URL;
  public static String LOGIN_SERVERE_IP;
  public static String ZDYW_COOKEY_KEY;
  public static Integer QUEUE_MAX_SIZE;
  
  @Value("${myself.httpUrl.getPermissionUrl}")
  public void setGetPermissionUrl(String getPermissionUrl) {
    GET_PERMISSION_URL = getPermissionUrl;
  }
  
  @Value("${myself.queue.size}")
  public void setQueueMaxSize(Integer queueMaxSize) {
    QUEUE_MAX_SIZE = queueMaxSize;
  }
  
  @Value("${myself.httpUrl.judgeIsLogin}")
  public void setJudgeIsLogin(String judgeIsLoginUrl) {
    JUDGE_IS_LOGIN = judgeIsLoginUrl;
  }
  
  @Value("${myself.httpUrl.redirectLogin}")
  public void setRedirectLogin(String redirectLoginUrl) {
    REDIRECT_LOGIN = redirectLoginUrl;
  }
  
  @Value("${myself.httpUrl.loginUrl}")
  public void setLoginUrl(String loginUrl) {
    LOGIN_URL = loginUrl;
  }
  
  @Value("${myself.httpUrl.logoutUrl}")
  public void setLogoutUrl(String logoutUrl) {
    LOGOUT_URL = logoutUrl;
  }
  
  @Value("${myself.httpUrl.loginIp}")
  public void setLoginServereIp(String loginServereIp) {
    LOGIN_SERVERE_IP = loginServereIp;
  }
  
  @Value("${myself.httpUrl.zdywCookeyKey}")
  public void setZdywCookeyKey(String zdywCookeyKey) {
    ZDYW_COOKEY_KEY = zdywCookeyKey;
  }
  
  private static final Integer CONNECT_TIME_OUT = 6000;
  
  private static final Integer SOCKET_TIME_OUT = 30 * 1000;
  
  private static final PoolingHttpClientConnectionManager connectionManager;
  
  private static final HttpClientBuilder clientBuilder;
  
  private static final RequestConfig requestConfig;
  
  static {
    connectionManager = new PoolingHttpClientConnectionManager();
    connectionManager.setMaxTotal(10);
    requestConfig = RequestConfig.custom().setConnectTimeout(CONNECT_TIME_OUT)
        .setSocketTimeout(SOCKET_TIME_OUT).build();
    clientBuilder = HttpClients.custom().setConnectionTimeToLive(100, TimeUnit.SECONDS)
        .setConnectionManager(connectionManager)
        .setConnectionManagerShared(true)
        .setDefaultRequestConfig(requestConfig);
  }
  
  public static String doPost(String url, String json, String username, String password) {
    log.info("doPost url: {}, username: {}", url, username);
    String responseEntity = null;
    CloseableHttpClient httpClient = HttpClients.custom().build();
    HttpPost httpPost = new HttpPost();
    try {
      httpPost.setConfig(requestConfig);
      httpPost.setURI(new URI(url));
      httpPost.addHeader("Content-type", "application/json; charset=utf-8");
      httpPost.addHeader("Authorization", "Basic " +
          Base64.getUrlEncoder().encodeToString((username + ":" + password).getBytes()));
      httpPost.setEntity(new StringEntity(json, Charset.forName("UTF-8")));
      HttpResponse httpResponse = httpClient.execute(httpPost);
      HttpEntity entity = httpResponse.getEntity();
      responseEntity = EntityUtils.toString(entity);
    } catch (IOException | URISyntaxException e) {
      e.printStackTrace();
    } finally {
      if (httpClient != null) {
        try {
          httpClient.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
    return responseEntity;
  }
  
  /**
   * http get 方式请求获取数据 并且覆盖token
   *
   * @param url
   * @param key
   * @param token
   * @return
   */
  public static String httpGetUrl(String url, String key, String token) {
    String result = null;
    CloseableHttpClient httpClient = null;
    if (StringUtils.isNotBlank(key) && StringUtils.isNotBlank(token)) {
      log.info("加入cookie访问: key {} value {}", key, token);
      CookieStore cookieStore = new BasicCookieStore();
      BasicClientCookie cookie = new BasicClientCookie(key, token);
      cookie.setDomain(LOGIN_SERVERE_IP);
      cookie.setPath(SEP);
      cookieStore.addCookie(cookie);
      httpClient = HttpClients.custom().setDefaultCookieStore(cookieStore).build();
    } else {
      httpClient = HttpClients.custom().build();
    }
    HttpGet httpGet = new HttpGet();
    try {
      httpGet.setConfig(requestConfig);
      httpGet.setURI(new URI(url));
      HttpResponse response = httpClient.execute(httpGet);
      if (HttpStatus.OK.value() == response.getStatusLine().getStatusCode()) {
        result = EntityUtils.toString(response.getEntity());
        log.info("成功获取返回信息:{}", url);
      } else {
        log.info("http 连接异常: {}", url);
      }
    } catch (URISyntaxException e) {
      log.error("URI地址格式不正确!");
      e.printStackTrace();
    } catch (ClientProtocolException e) {
      log.error("验证失败!");
      e.printStackTrace();
    } catch (IOException e) {
      log.error("登录验证失败!");
      e.printStackTrace();
    } finally {
      if (httpClient != null) {
        try {
          httpClient.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
    return result;
  }
  
  public static String doGetHarborInfo(String url, String username, String password) {
    log.info("doGetHarborInfo url: {}, username: {}", url, username);
    String result = null;
    if (StringUtils.isNotBlank(url) && StringUtils.isNotBlank(username)
        && StringUtils.isNotBlank(password)) {
      CloseableHttpClient httpClient = HttpClients.custom().build();
      HttpGet httpGet = new HttpGet();
      try {
        httpGet.setConfig(requestConfig);
        httpGet.setURI(new URI(url));
        httpGet.addHeader("Content-Type", "application/json;charset=UTF-8");
        //添加用户名和密码验证
        httpGet.addHeader("Authorization", "Basic " +
            Base64.getUrlEncoder().encodeToString((username + ":" + password).getBytes()));
        HttpResponse response = httpClient.execute(httpGet);
        if (HttpStatus.OK.value() == response.getStatusLine().getStatusCode()) {
          result = EntityUtils.toString(response.getEntity());
        } else {
          result = "harbor 连接异常";
          log.info(result);
        }
      } catch (URISyntaxException e) {
        log.error("url格式不正确 : {} url: {}", e.getMessage(), url);
      } catch (ClientProtocolException e) {
        log.error("http连接发生异常 : {}", e.getMessage());
      } catch (IOException e) {
        //   log.error("IO 异常 :{}", e.getMessage());
      } finally {
        if (httpClient != null) {
          try {
            httpClient.close();
          } catch (IOException e) {
            e.printStackTrace();
          }
        }
      }
    }
    return result;
  }
  
  public static String doDeleteHarborInfo(String url, String username, String password) {
    log.info("doDeleteHarborInfo url: {}, username: {}, password: {}", url, username, password);
    String result = null;
    if (StringUtils.isNotBlank(url) && StringUtils.isNotBlank(username)
        && StringUtils.isNotBlank(password)) {
      HttpDelete httpDelete = new HttpDelete();
      CloseableHttpClient httpClient = HttpClients.custom().build();
      try {
        httpDelete.setConfig(requestConfig);
        httpDelete.setURI(new URI(url));
        httpDelete.addHeader("Content-Type", "application/json;charset=UTF-8");
        httpDelete.addHeader("Authorization", "Basic " +
            Base64.getUrlEncoder().encodeToString((username + ":" + password).getBytes()));
        HttpResponse response = httpClient.execute(httpDelete);
        if (HttpStatus.OK.value() == response.getStatusLine().getStatusCode()) {
          result = EntityUtils.toString(response.getEntity());
        } else {
          result = "harbor 请求执行异常";
          log.info("harbor 请求执行异常{}", response.getStatusLine().getStatusCode());
        }
      } catch (URISyntaxException e) {
        log.error("url格式不正确 : {} url: {}", e.getMessage(), url);
      } catch (ClientProtocolException e) {
        log.error("http连接发生异常 : {}", e.getMessage());
      } catch (IOException e) {
        // log.error("IO 异常 :{}", e.getMessage());
      } finally {
        if (httpClient != null) {
          try {
            httpClient.close();
          } catch (IOException e) {
            e.printStackTrace();
          }
        }
      }
    }
    return result;
  }
  
}
