package com.xc.fast_deploy.websocketConfig;

import com.offbytwo.jenkins.model.Build;
import com.offbytwo.jenkins.model.JobWithDetails;
import com.xc.fast_deploy.listener.JenkinsConsleListener;
import com.xc.fast_deploy.model.master_model.ModuleJob;
import com.xc.fast_deploy.service.common.ModuleJobService;
import com.xc.fast_deploy.service.common.ModuleUserService;
import com.xc.fast_deploy.shiro.token.JwtUtil;
import com.xc.fast_deploy.utils.PoolExcutorUtils;
import com.xc.fast_deploy.utils.jenkins.JenkinsManage;
import com.xc.fast_deploy.vo.JenkinsManageVO;
import com.xc.fast_deploy.vo.RunJobDataVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.yeauty.annotation.*;
import org.yeauty.pojo.ParameterMap;
import org.yeauty.pojo.Session;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 在pod显示页面 这里可以点击直接显示模块构建镜像的过程
 */
@ServerEndpoint(prefix = "netty.websocket", value = "/websocket/jenkins")
@Component
@Slf4j
public class JenkinsWebSocketServer {
  
  @Autowired
  private JenkinsManage jenkinsManage;
  @Autowired
  private ModuleJobService jobService;
  @Autowired
  private ModuleUserService userService;
  
  //concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。
  private static transient volatile Set<JenkinsWebSocketServer> webSocketSet = ConcurrentHashMap.newKeySet();

//    private Session session;
  
  /**
   * 连接建立成功是调用的方法
   */
  @OnOpen
  public void onOpen(Session session, ParameterMap parameterMap) {
    String moduleIdString = parameterMap.getParameter("moduleId");
    String envIdString = parameterMap.getParameter("envId");
    String token = parameterMap.getParameter("X-token");
    String isUpdate = parameterMap.getParameter("isUpdate");
    String isOfflineStr = parameterMap.getParameter("isOffline");
    String isGreenChannelStr = parameterMap.getParameter("isGreenChannel");
    String needIdStr = parameterMap.getParameter("needId");
    String isPromptly = parameterMap.getParameter("isPromptly"); //是否立即发布
    log.info("websocket输入参数： moduleId：{}，envId：{},isUpdate:{},token:{}",
        moduleIdString, envIdString, isUpdate, token);
    Integer moduleId = null;
    Integer envId = null;
    Integer isUpdateCode = null;
    Integer offline = null;
    try {
      moduleId = Integer.valueOf(moduleIdString);
      envId = Integer.valueOf(envIdString);
      isUpdateCode = Integer.valueOf(isUpdate);
      offline = Integer.valueOf(isOfflineStr);
    } catch (NumberFormatException e) {
      session.sendText("输入参数不符合格式!");
      session.close();
      return;
    }
    boolean isUpdateAllCode = (isUpdateCode == 1);
    boolean isPromptlyCode = false;
    Boolean isGreenChannel = isGreenChannelStr == null ? null : Integer.valueOf(isGreenChannelStr) == 1;
    boolean isOffline = (offline == 1);
    if (StringUtils.isNotBlank(isPromptly)) {
      isPromptlyCode = Integer.valueOf(isPromptly) == 1;
    }
    String userId = JwtUtil.getUserIdFromToken(token);
    boolean flag = false;
    if (StringUtils.isNotBlank(userId)) {
      Map<Integer, Set<String>> envPermissionMap = userService.selectEnvPermissionByUserId(userId);
      //表明该用户无任何环境的权限或者无该环境的权限
      if (envPermissionMap.size() > 0 && envPermissionMap.containsKey(envId)) {
        Set<String> permissionSet = envPermissionMap.get(envId);
        if (permissionSet.contains("module_build_image")) {
          flag = true;
        }
      }
    }
    if (!flag) {
      session.sendText("无权限访问!");
      session.close();
      return;
    }
    
    log.info("有新窗口开始监听: {}", session);
//        this.session = session;
    webSocketSet.add(this);
    ModuleJob moduleJob = jobService.selectJobByModuleAndEnvId(envId, moduleId);
    if (moduleJob == null) {
      session.sendText("未查询到对应的job");
      session.close();
      return;
    }
    try {
      JobWithDetails job = jenkinsManage.getJenkinsServer().getJob(moduleJob.getJobName());
      if (job == null) {
        session.sendText("未查询到对应的job");
        session.close();
        return;
      }
      Build build = job.getLastBuild();
      if (build.details().isBuilding()) {
        build.details().streamConsoleOutput(new JenkinsConsleListener(session),
            1, 60 * 1000 * 10);
      } else {
        int buildNumber = job.getNextBuildNumber();
        session.sendText("已启动构建job,请先等待 svn up >>>");
        session.sendText("当前等待队列数: " +
            PoolExcutorUtils.jobPoolExecutor.getQueue().size());
        
        //这部的功能是为了让执行的jobId不存在同一时间的同时执行
        if (PoolExcutorUtils.jobHashMap.containsKey(moduleJob.getId())) {
          session.sendText("该job正在执行中!!");
          //跳出该次执行
          session.close();
          return;
        }
        RunJobDataVo runJobDataVo = new RunJobDataVo();
        runJobDataVo.setIsUpdateAllCode(isUpdateAllCode);
        runJobDataVo.setIsPromptly(isPromptlyCode);
        runJobDataVo.setNeedIdStr(needIdStr);
        runJobDataVo.setIsNeedUpCode(true);
        runJobDataVo.setIsGreenChannel(isGreenChannel);
        
        runJobDataVo.setIsOffline(isOffline);
        runJobDataVo.setJenkinsManageVO(new JenkinsManageVO(jenkinsManage,
            moduleJob.getJobName(), buildNumber));
        runJobDataVo.setJobId(moduleJob.getId());
        runJobDataVo.setSession(session);
        runJobDataVo.setToken(token);
        PoolExcutorUtils.linkedQueue.put(runJobDataVo);
      }
    } catch (IOException | InterruptedException e) {
      log.error("websocket 出现错误!!!");
      e.printStackTrace();
    }
  }
  
  /**
   * 连接关闭调用的方法
   */
  @OnClose
  public void onClose(Session session) {
    if (session != null) {
      session.close();
    }
    log.info("websocket 关闭: {}", webSocketSet.remove(this));
  }
  
  /**
   * 收到客户端消息后调用的方法
   *
   * @param message 客户端发送过来的消息
   */
  @OnMessage
  public void onMessage(String message, Session session) {
    log.info("收到来自窗口" + session + "的信息:" + message);
  }
  
  /**
   * @param session
   * @param error
   */
  @OnError
  public void onError(Session session, Throwable error) {
    log.error("发生错误");
    error.printStackTrace();
  }
  
}
