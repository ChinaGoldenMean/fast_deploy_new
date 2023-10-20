package com.xc.fast_deploy.websocketConfig;

import com.xc.fast_deploy.dao.master_dao.ModuleEnvMapper;
import com.xc.fast_deploy.model.master_model.ModuleEnv;
import com.xc.fast_deploy.service.common.ModuleDeployLogService;
import com.xc.fast_deploy.service.common.ModuleUserService;
import com.xc.fast_deploy.service.common.SyncService;
import com.xc.fast_deploy.service.k8s.K8sPodService;
import com.xc.fast_deploy.shiro.token.JwtUtil;
import com.xc.fast_deploy.utils.constant.K8sOPActive;
import com.xc.fast_deploy.websocketConfig.myThread.ClearMemoryThread;
import io.kubernetes.client.Exec;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.models.V1Container;
import io.kubernetes.client.openapi.models.V1Pod;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.yeauty.annotation.OnClose;
import org.yeauty.annotation.OnOpen;
import org.yeauty.annotation.ServerEndpoint;
import org.yeauty.pojo.ParameterMap;
import org.yeauty.pojo.Session;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@ServerEndpoint(prefix = "netty.websocket", value = "/websocket/k8s/clearmemory/pods")
@Component
@Slf4j
public class ClearMemoryWebsocket {
  
  @Autowired
  private K8sPodService k8sPodService;
  @Autowired
  private SyncService syncService;
  @Autowired
  private ModuleUserService userService;
  @Autowired
  private ModuleEnvMapper envMapper;
  @Autowired
  private ModuleDeployLogService moduleDeployLogService;
  
  //concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。
//    private static transient volatile Set<Session> webSocketSet = ConcurrentHashMap.newKeySet();
  private Session session;
  private ThreadPoolExecutor threadPoolExecutor;
  private Integer podCountSize;
  private String yamlNamespace;
  
  /**
   * 连接建立成功是调用的方法
   */
  @OnOpen
  public void onOpen(Session session, ParameterMap parameterMap) {
    String moduleEnvIdString = parameterMap.getParameter("moduleEnvId");
    String podNames = parameterMap.getParameter("podNames");
    String moduleId = parameterMap.getParameter("moduleId");
    String token = parameterMap.getParameter("X-token");
    this.yamlNamespace = parameterMap.getParameter("yamlNamespace");
    log.info("参数传递: moduleEnvId: {}, podNames: {},moduleId:{},token:{}", moduleEnvIdString, podNames, moduleId, token);
    Map<String, Object> argsMap = new HashMap<>();
    argsMap.put("podName", podNames);
    if (StringUtils.isNotBlank(moduleEnvIdString) && StringUtils.isNotBlank(podNames) && StringUtils.isNotBlank(token)) {
      Integer envId;
      this.session = session;
      try {
        envId = Integer.valueOf(moduleEnvIdString);
        Integer.valueOf(moduleId);
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
        if (permissionSet.contains("module_pod_clear_memory")) {
          flag = true;
        }
      }
      if (!flag) {
        session.sendText("无权限访问!");
        session.close();
        return;
      }
      
      boolean isSuccess = false;
      ModuleEnv env = envMapper.selectOne(envId);
      if (env != null) {
        List<String> podNameList = new ArrayList<>();
        if (podNames.contains(",")) {
          String[] split = podNames.split(",");
          podNameList.addAll(Arrays.asList(split));
        } else {
          podNameList.add(podNames);
        }
        this.podCountSize = podNameList.size();
        threadPoolExecutor = new ThreadPoolExecutor(this.podCountSize, this.podCountSize, 0L,
            TimeUnit.SECONDS, new ArrayBlockingQueue<>(20));
        for (String podName : podNameList) {
          V1Pod pod = k8sPodService.getPodByPodName(env, podName, this.yamlNamespace);
          if (pod != null) {
            Exec exec = new Exec();
            List<V1Container> containers = pod.getSpec().getContainers();
            if (containers.size() > 0) {
              try {
                Process process = exec.exec(
                    this.yamlNamespace,
                    podName,
                    new String[]{"sh", "/app/jmx-tool/jmx-monitor-12345.sh"},
                    containers.get(0).getName(),
                    false,
                    false
                );
                threadPoolExecutor.submit(new ClearMemoryThread(process, this.session, podName));
                isSuccess = true;
              } catch (IOException | ApiException e) {
                log.error("清除pod内存出现错误: {}", e.getMessage());
                isSuccess = false;
              }
            } else {
              this.session.sendText("未找到容器");
              this.session.close();
            }
          } else {
            this.session.sendText("未找到对应pod");
            this.session.close();
          }
        }
      }
      syncService.saveModuleDeployLog(moduleDeployLogService, Integer.valueOf(moduleId), envId, null,
          K8sOPActive.OP_CLEAR_POD_MEMORY, argsMap, isSuccess, JwtUtil.getUserIdFromToken(token), JwtUtil.getUserNameFromToken(token));
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
    if (session != null) {
      session.close();
      session.closeFuture();
    }
    //关闭线程池
    syncService.shutdownExcutor(threadPoolExecutor, session, this.podCountSize);
  }
  
}

