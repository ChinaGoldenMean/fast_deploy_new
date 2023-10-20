package com.xc.fast_deploy.websocketConfig;

import com.xc.fast_deploy.dao.master_dao.ModuleEnvMapper;
import com.xc.fast_deploy.model.master_model.ModuleEnv;
import com.xc.fast_deploy.service.common.ModuleUserService;
import com.xc.fast_deploy.shiro.token.JwtUtil;
import com.xc.fast_deploy.utils.PermissionJudgeUtils;
import com.xc.fast_deploy.utils.constant.K8sNameSpace;
import com.xc.fast_deploy.utils.k8s.K8sManagement;
import com.xc.fast_deploy.websocketConfig.myThread.OutputThread;
import com.xc.fast_deploy.ws.ExecSession;
import io.kubernetes.client.Exec;
import io.kubernetes.client.openapi.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.yeauty.annotation.*;
import org.yeauty.pojo.ParameterMap;
import org.yeauty.pojo.Session;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@ServerEndpoint(prefix = "netty.websocket", value = "/websocket/k8s/exec/pod/container")
@Component
@Slf4j
public class K8sContainerExecWebsocket {
  
  @Autowired
  private ModuleUserService userService;
  
  @Autowired
  private ModuleEnvMapper envMapper;
  
  private Map<String, ExecSession> execSessionMap = new ConcurrentHashMap<>();
  private Process process;
  private Session session;
  private String yamlNamespace;
  
  private ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(2,
      2, 0L,
      TimeUnit.SECONDS, new ArrayBlockingQueue<>(20));
  
  private OutputStream outputStream;
  
  @OnOpen
  public void onOpen(Session session, ParameterMap parameterMap) throws IOException {
    String moduleEnvIdString = parameterMap.getParameter("moduleEnvId");
    String podName = parameterMap.getParameter("podName");
    String containerName = parameterMap.getParameter("containerName");
    String token = parameterMap.getParameter("X-token");
    this.yamlNamespace = parameterMap.getParameter("yamlNamespace");
    log.info("参数传递: moduleEnvId: {}, podName: {},containerName:{},token:{}", moduleEnvIdString, podName, containerName, token);
    if (StringUtils.isNotBlank(moduleEnvIdString) && StringUtils.isNotBlank(podName) &&
        StringUtils.isNotBlank(containerName) && StringUtils.isNotBlank(token)) {
      Integer envId = null;
      this.session = session;
      try {
        envId = Integer.valueOf(moduleEnvIdString);
      } catch (NumberFormatException e) {
        session.sendText("输入参数不符合格式!");
        session.close();
      }
      session.setAttribute("podName", podName);
      //验证权限
      PermissionJudgeUtils.judgeUserPermission(userService, "link_container",
          JwtUtil.getUserIdFromToken(token), envId);
      ModuleEnv env = envMapper.selectOne(envId);
      
      K8sManagement.getCoreV1Api(env.getK8sConfig());
      
      this.session = session;
      
      Exec exec = new Exec();
      try {
        process = exec.exec(
            K8sNameSpace.DEFAULT,
            podName,
            new String[]{"/bin/bash"},
            containerName,
            true,
            true
        );
      } catch (IOException | ApiException e) {
        log.error("连接k8s pod出现错误: {}", e.getMessage());
      }
      outputStream = process.getOutputStream();
      threadPoolExecutor.submit(new OutputThread(process.getInputStream(), session));
      threadPoolExecutor.submit(new OutputThread(process.getErrorStream(), session));
      execSessionMap.put(session.getAttribute("podName"),
          new ExecSession(null, session, process, threadPoolExecutor));
    }
    
  }
  
  //接收字符串消息发送数据
  @SuppressWarnings("SuspiciousMethodCalls")
  @OnMessage
  public void onMessage(Session session, String message) {
    try {
      execSessionMap.get(session.getAttribute("podName")).getProcess()
          .getOutputStream().write(message.getBytes("UTF-8"));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  @OnClose
  public void onClose(Session session) throws IOException {
    log.info("websocket连接关闭: {}", session);
    if (session != null) {
      session.close();
      session.closeFuture();
      if (process != null) {
        try {
          process.waitFor(10, TimeUnit.SECONDS);
          process.destroy();
          process.exitValue();
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
      //关闭线程池
      threadPoolExecutor.shutdown();
      if (!threadPoolExecutor.isTerminated()) {
        try {
          threadPoolExecutor.awaitTermination(5, TimeUnit.SECONDS);
          threadPoolExecutor.shutdownNow();
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    }
  }
  
  @OnError
  public void onError(Session session, Throwable throwable) {
    throwable.printStackTrace();
  }
  
}
