package com.xc.fast_deploy.websocketConfig;

import com.xc.fast_deploy.configuration.MyThreadFactory;
import com.xc.fast_deploy.dao.master_dao.ModuleEnvMapper;
import com.xc.fast_deploy.model.master_model.ModuleEnv;
import com.xc.fast_deploy.service.common.ModuleDeployLogService;
import com.xc.fast_deploy.service.common.ModuleUserService;
import com.xc.fast_deploy.service.common.SyncService;
import com.xc.fast_deploy.service.k8s.K8sPodService;
import com.xc.fast_deploy.service.k8s.K8sService;
import com.xc.fast_deploy.shiro.token.JwtUtil;
import com.xc.fast_deploy.utils.constant.K8sOPActive;
import com.xc.fast_deploy.utils.k8s.K8sUtils;
import com.xc.fast_deploy.websocketConfig.myThread.DumpFileThead;
import com.xc.fast_deploy.websocketConfig.myThread.ExecOutputThread;
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

import static com.xc.fast_deploy.utils.constant.HarborContants.CONTACT;

@ServerEndpoint(prefix = "netty.websocket", value = "/websocket/k8s/dump/java/logs")
@Component
@Slf4j
public class DumpJavaWebsocket {
  
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
  @Autowired
  private K8sService k8sService;
  
  private Session session;
  private ThreadPoolExecutor threadPoolExecutor;
  private Integer podCountSize;
  private Process process;
  private String yamlNamespace;
  
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
    if (StringUtils.isNotBlank(moduleEnvIdString)
        && StringUtils.isNotBlank(podNames)
        && StringUtils.isNotBlank(token)) {
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
      
      //权限验证
      Map<Integer, Set<String>> envPermissionMap = userService.selectEnvPermissionByUserId(JwtUtil.getUserIdFromToken(token));
//            表明该用户无任何环境的权限或者无该环境的权限
      if (envPermissionMap.size() > 0 && envPermissionMap.containsKey(envId)) {
        Set<String> permissionSet = envPermissionMap.get(envId);
        if (permissionSet.contains("module_pod_dump_logs_show")) {
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
        threadPoolExecutor = new ThreadPoolExecutor(this.podCountSize * 3,
            this.podCountSize * 3, 0L,
            TimeUnit.SECONDS, new ArrayBlockingQueue<>(20),
            new MyThreadFactory("dumpjava"));
        
        for (String podName : podNameList) {
          StringBuffer filePrefix = new StringBuffer();
          V1Pod pod = k8sPodService.getPodByPodName(env, podName, this.yamlNamespace);
          if (pod != null) {
            if (pod.getStatus() != null) {
              session.sendText(podName + " 所在nodeIp: " + pod.getStatus().getHostIP());
            }
            String mountPath = K8sUtils.getMountLogPathFromPod(pod);
            if (StringUtils.isBlank(mountPath)) {
              session.sendText("未找到log_path路径");
              session.close();
              return;
            }
            filePrefix.append(mountPath)
                .append(CONTACT)
                .append(K8sUtils.THREAD_DUMP_JAVAPATH)
                .append(podName)
                .append(CONTACT);

//                        filePrefix.append(mountPath)
//                                .append("/thread-dump-java").append(podName).append("/");
            
            Exec exec = new Exec();
            List<V1Container> containers = pod.getSpec().getContainers();
            if (containers.size() > 0) {
              try {
                process = exec.exec(
                    this.yamlNamespace,
                    podName,
                    new String[]{"/bin/bash"},
                    containers.get(0).getName(),
                    true,
                    true
                );
                threadPoolExecutor.submit(new ExecOutputThread(process,
                    this.session, mountPath, podName, filePrefix));
                threadPoolExecutor.submit(
                    new DumpFileThead(process.getInputStream(), session, podName));
                isSuccess = true;
              } catch (IOException | ApiException e) {
                log.error("执行jstack出现错误: {}", e.getMessage());
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
      //异步存入操作日志
      syncService.saveModuleDeployLog(moduleDeployLogService,
          Integer.valueOf(moduleId), envId, null,
          K8sOPActive.OP_DUMP_JAVA_THREAD, argsMap,
          isSuccess, JwtUtil.getUserIdFromToken(token),
          JwtUtil.getUserNameFromToken(token));
    }
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
      try {
        process.waitFor(10, TimeUnit.SECONDS);
        process.destroy();
        process.exitValue();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
    //关闭线程池
    log.info("关闭线程池");
    threadPoolExecutor.shutdown();
    if (!threadPoolExecutor.isTerminated()) {
      try {
        threadPoolExecutor.awaitTermination(5, TimeUnit.SECONDS);
        threadPoolExecutor.shutdownNow();
      } catch (InterruptedException e) {
        log.error("关闭线程出错");
      }
    }
  }
  
}
