package com.xc.fast_deploy.websocketConfig;

import com.xc.fast_deploy.service.common.ModuleUserService;
import com.xc.fast_deploy.service.common.SyncService;
import com.xc.fast_deploy.service.k8s.K8sPodService;
import com.xc.fast_deploy.shiro.token.JwtUtil;
import com.xc.fast_deploy.websocketConfig.myThread.OutputThread;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Call;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.yeauty.annotation.OnClose;
import org.yeauty.annotation.OnOpen;
import org.yeauty.annotation.ServerEndpoint;
import org.yeauty.pojo.ParameterMap;
import org.yeauty.pojo.Session;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@ServerEndpoint(prefix = "netty.websocket", value = "/websocket/k8s/pod/logs")
@Component
@Slf4j
public class K8sWebsocketServer {
  
  @Autowired
  private K8sPodService k8sPodService;
  @Autowired
  private SyncService syncService;
  @Autowired
  private ModuleUserService userService;
  
  //concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。
  private static transient volatile Set<Session> webSocketSessionSet = ConcurrentHashMap.newKeySet();
  
  private Session session;
  private ThreadPoolExecutor threadPoolExecutor;
  private Call call;
  private ResponseBody responseBody;
  private String yamlNamespace;
  
  /**
   * 连接建立成功是调用的方法
   */
  @OnOpen
  public void onOpen(Session session, ParameterMap parameterMap) {
    String moduleEnvIdString = parameterMap.getParameter("moduleEnvId");
    String podName = parameterMap.getParameter("podName");
    String containerName = parameterMap.getParameter("containerName");
    String token = parameterMap.getParameter("X-token");
    this.yamlNamespace = parameterMap.getParameter("yamlNamespace");
    log.info("参数传递: moduleEnvId: {}, podName: {}, containerName: {},token:{}", moduleEnvIdString, podName, containerName, token);
    if (StringUtils.isNotBlank(moduleEnvIdString) && StringUtils.isNotBlank(podName) && StringUtils.isNotBlank(containerName)
        && StringUtils.isNotBlank(token)) {
      Integer envId = null;
      try {
        envId = Integer.valueOf(moduleEnvIdString);
      } catch (NumberFormatException e) {
        session.sendText("输入参数不符合格式!");
        session.close();
        return;
      }
      boolean flag = false;
      Map<Integer, Set<String>> envPermissionMap = userService.selectEnvPermissionByUserId(JwtUtil.getUserIdFromToken(token));
      //表明该用户无任何环境的权限或者无该环境的权限
      if (envPermissionMap.size() > 0 && envPermissionMap.containsKey(envId)) {
        Set<String> permissionSet = envPermissionMap.get(envId);
        if (permissionSet.contains("module_pod_log_show")) {
          flag = true;
        }
      }
      if (!flag) {
        session.sendText("无权限访问!");
        session.close();
        return;
      }
      this.session = session;
      webSocketSessionSet.add(session);
      // call = k8sPodService.getConnect2PodLogs(envId, this.yamlNamespace, podName, containerName);
      
      InputStream inputStream = k8sPodService.getConnect2PodLogsByInputStream(envId, this.yamlNamespace, podName, containerName);
      try {
        //Response response = call.execute();
        //if (response == null) {
        //    session.sendText("未找到对应的pod的log日志");
        //    session.close();
        //    return;
        //}
        if (inputStream == null) {
          session.sendText("未找到对应的pod的log日志");
          session.close();
          return;
        }
        
        //responseBody = response.body();
        //InputStream inputStream = responseBody.byteStream();
        
        //单线程池工具
        threadPoolExecutor = new ThreadPoolExecutor(1, 1, 0L,
            TimeUnit.SECONDS, new ArrayBlockingQueue<>(10),
            new ThreadPoolExecutor.CallerRunsPolicy());
        threadPoolExecutor.submit(new OutputThread(inputStream, session));
      } catch (Exception e) {
        e.printStackTrace();
      }
    } else {
      session.sendText("输入参数不符合格式!");
      session.close();
    }
    
  }
  
  /**
   * 实现服务器主动推送
   */
  public boolean sendMessage(String message) {
    boolean success = false;
    if (this.session != null) {
      this.session.sendText(message);
      success = true;
    }
    return success;
  }
  
  /**
   * 连接关闭调用的方法
   */
  @OnClose
  public void onClose() {
    log.info("websocket连接关闭: {}", session);
    webSocketSessionSet.remove(session);
    if (session != null) {
      session.close();
      session.closeFuture();
    }
    if (call != null) {
      call.cancel();
      log.info("k8s call cancel:{}", call.isCanceled());
    }
    if (threadPoolExecutor != null) {
      threadPoolExecutor.shutdown();
      if (!threadPoolExecutor.isTerminated()) {
        try {
          threadPoolExecutor.awaitTermination(5, TimeUnit.SECONDS);
          threadPoolExecutor.shutdownNow();
          responseBody.close();
        } catch (InterruptedException e) {
          e.printStackTrace();
        } catch (Exception f) {
          log.info("关闭input stream出错:{}", f.getMessage());
        }
      }
    }
    
  }
}
