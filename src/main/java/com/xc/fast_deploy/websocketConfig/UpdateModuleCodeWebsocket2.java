package com.xc.fast_deploy.websocketConfig;

import com.alibaba.fastjson.JSONObject;
import com.xc.fast_deploy.bo.UpdateCodeBo;
import com.xc.fast_deploy.dto.StatusDTO;
import com.xc.fast_deploy.service.common.ModulePackageService;
import com.xc.fast_deploy.service.common.ModuleUserService;
import com.xc.fast_deploy.service.common.SyncService;
import com.xc.fast_deploy.shiro.token.JwtUtil;
import com.xc.fast_deploy.thread.TaskExecutor;
import com.xc.fast_deploy.websocketConfig.myThread.RunJobOutputThread;
import com.xc.fast_deploy.websocketConfig.myThread.UpdateCodeThread;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.yeauty.annotation.*;
import org.yeauty.pojo.ParameterMap;
import org.yeauty.pojo.Session;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 更新代码实时查看更新进度
 */
@ServerEndpoint(prefix = "netty.websocket", value = "/websocket/update/module/package2")
@Component
@Slf4j
public class UpdateModuleCodeWebsocket2 {
  
  @Autowired
  private ModuleUserService userService;
  @Autowired
  private ModulePackageService packageService;
  @Autowired
  private SyncService syncService;
  
  //concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。
  private static transient volatile Set<Session> webSocketSessionSet = ConcurrentHashMap.newKeySet();
  private Session session;
  
  @OnOpen
  public void onOpen(Session session, ParameterMap parameterMap) throws IOException {
    String envIdString = parameterMap.getParameter("envId");
    String token = parameterMap.getParameter("X-token");
    log.info("参数接收: envId:{} token:{}", envIdString, token);
    Integer envId = null;
    try {
      envId = Integer.valueOf(envIdString);
    } catch (NumberFormatException e) {
      session.sendText("输入参数不符合格式!!");
      session.close();
      return;
    }
    boolean flag = false;
    Map<Integer, Set<String>> envPermissionMap = userService.selectEnvPermissionByUserId(JwtUtil.getUserIdFromToken(token));
    //表明该用户无任何环境的权限或者无该环境的权限
    if (envPermissionMap.size() > 0 && envPermissionMap.containsKey(envId)) {
//            Set<String> permissionSet = envPermissionMap.get(envId);
//            if (permissionSet.contains("module_pod_log_show")) {
      flag = true;
//            }
    }
    if (!flag) {
      session.sendText("无权限访问!");
      session.close();
      return;
    }
    this.session = session;
    webSocketSessionSet.add(session);
  }
  
  @OnError
  public void onError(Session session, Throwable throwable) {
    throwable.printStackTrace();
  }
  
  @OnMessage
  public void onMessage(Session session, String message) {
    //接收到批量的moduleIds去做批量的拉取或者更新代码的操作
    log.info("收到来自窗口{} 的信息: {}", session, message);
    if (StringUtils.isNotBlank(message) && StringUtils.isNotBlank(message.trim())) {
      List<Integer> list = new ArrayList<>();
      try {
        list = JSONObject.parseArray(message.trim(), Integer.class);
      } catch (Exception e) {
        log.error("数据接收转换为json格式错误！");
      }
      if (list.size() == 0) {
        session.sendText("数据接收格式错误");
        session.close();
        return;
      }
      HashSet<Integer> hashSet = new HashSet<>(list);
      TaskExecutor taskExecutor = new TaskExecutor(hashSet.size());
      
      for (Integer moduleId : hashSet) {
        UpdateCodeBo bo = new UpdateCodeBo();
        bo.setModuleId(moduleId).setPackageService(packageService).setSession(session);
        
        taskExecutor.execute(base -> {
          boolean b = false;
          try {
            b = packageService.updateModuleAllCode(moduleId, this.session);
          } catch (Exception e) {
            session.sendText(e.getMessage());
            log.error("run job error: {}", e.getMessage());
            e.printStackTrace();
          }
          log.info("当前线程 :{} 执行状态: {}", Thread.currentThread().getName(), b);
          StatusDTO statusDTO = new StatusDTO();
          statusDTO.setResult(b);
          statusDTO.setStatus(1);
          statusDTO.setModuleId(moduleId);
          try {
            this.session.sendText(JSONObject.toJSONString(statusDTO)).sync();
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
          return null;
        }, bo);
        
        //      poolExecutor.submit(new UpdateCodeThread(moduleId, packageService, session));
      }
      //    syncService.shutdownExcutor(poolExecutor, session, hashSet.size());
    }
  }
  
  @OnClose
  public void onClose(Session session) {
    webSocketSessionSet.remove(session);
    session.close();
  }
  
}
