package com.xc.fast_deploy.websocketConfig;

import com.alibaba.fastjson.JSONObject;
import com.xc.fast_deploy.dao.master_dao.ModuleEnvMapper;
import com.xc.fast_deploy.dto.UploadModuleStatusDTO;
import com.xc.fast_deploy.model.master_model.ModuleCertificate;
import com.xc.fast_deploy.model.master_model.ModuleEnv;
import com.xc.fast_deploy.service.common.*;
import com.xc.fast_deploy.shiro.token.JwtUtil;
import com.xc.fast_deploy.utils.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.yeauty.annotation.*;
import org.yeauty.pojo.ParameterMap;
import org.yeauty.pojo.Session;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@ServerEndpoint(prefix = "netty.websocket", value = "/websocket/upload/moduleManage")
@Component
@Slf4j
public class UploadModuleManageWebsocket {
  
  @Autowired
  private SyncService syncService;
  @Autowired
  private ModuleUserService userService;
  @Autowired
  private ModuleManageService moduleManageService;
  @Autowired
  private ModuleEnvMapper envMapper;
  @Autowired
  private ModuleCertificateService certificateService;
  @Autowired
  private ModuleMirrorService moduleMirrorService;
  
  @Resource
  private Executor executor;
  
  private Session session;
  private String fileName;
  private ModuleCertificate certificate;
  private ModuleEnv moduleEnv;
  private String projectCode;
  private Integer envId;
  private String userId;
  private Integer moduleTypeCode;
  private String gitBranch;
  private Integer certificateId;
  
  @Value("${file.storge.path.uploadPath}")
  private String uploadStorgePath;
  
  @OnOpen
  public void onOpen(Session session, ParameterMap parameterMap) throws IOException {
    this.session = session;
    String envIdString = parameterMap.getParameter("envId");
    String certificateIdString = parameterMap.getParameter("certificateId");
    String projectCode = parameterMap.getParameter("projectCode");
    String token = parameterMap.getParameter("X-token");
    String isUpdateCode = parameterMap.getParameter("isUpdateCode");
    gitBranch = parameterMap.getParameter("gitBranch");
    
    log.info("参数传递: envIdString: {}, certificateIdString: {},projectCode:{},token:{}",
        envIdString, certificateIdString, projectCode, token);
    if (StringUtils.isNotBlank(envIdString)
        && StringUtils.isNotBlank(certificateIdString)
        && StringUtils.isNotBlank(token)
        && StringUtils.isNotBlank(projectCode) && StringUtils.isNotBlank(isUpdateCode)) {
      try {
        envId = Integer.valueOf(envIdString);
        certificateId = Integer.valueOf(certificateIdString);
        // 0 svn 源码  3 svn自动更新源码
        moduleTypeCode = Integer.valueOf(isUpdateCode);
      } catch (NumberFormatException e) {
        session.sendText("参数不符合格式!");
        session.close();
        return;
      }
      boolean flag = false;
      this.userId = JwtUtil.getUserIdFromToken(token);
      this.projectCode = projectCode;
      //校验project_code是否在所选环境中可以找到
      List<String> projectCodeList = moduleMirrorService.getHarborProjectCode(envId);
      if (!projectCodeList.contains(projectCode)) {
        session.sendText("project code 选择与实际环境对应不上: " + this.projectCode);
        return;
      }
      
      Map<Integer, Set<String>> envPermissionMap = userService.selectEnvPermissionByUserId(userId);
      //表明该用户无任何环境的权限或者无该环境的权限
      if (envPermissionMap.size() > 0 && envPermissionMap.containsKey(envId)) {
        Set<String> permissionSet = envPermissionMap.get(envId);
        flag = true;
      }
      if (!flag) {
        session.sendText("无权限访问!");
        session.close();
        return;
      }
      this.certificate = certificateService.selectById(this.certificateId);
      this.moduleEnv = envMapper.selectByPrimaryKey(this.envId);
      if (certificate == null || moduleEnv == null) {
        session.sendText("未查询到对应数据");
      }
    } else {
      session.sendText("参数不符合格式!");
      session.close();
    }
  }
  
  @OnClose
  public void onClose(Session session) {
    log.info("websocket connection closed");
    if (this.executor != null) {
      ThreadPoolExecutor poolExecutor = (ThreadPoolExecutor) this.executor;
      poolExecutor.shutdown();
      try {
        poolExecutor.awaitTermination(5, TimeUnit.SECONDS);
        if (!poolExecutor.isTerminated()) {
          poolExecutor.shutdownNow();
        }
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
    session.close();
  }
  
  @OnError
  public void onError(Session session, Throwable throwable) {
    throwable.printStackTrace();
  }
  
  @OnMessage
  public void onMessage(Session session, String message) {
    log.info("接收到文件名称:{}", message);
    this.fileName = message;
  }
  
  @OnBinary
  public void onBinary(Session session, byte[] bytes) {
    log.info("binary数据开始接收");
    StringBuilder sbfileName = new StringBuilder();
    File file = new File(sbfileName.append(uploadStorgePath)
        .append(fileName).append(DateUtils.generateDateString()).toString());
    FileOutputStream outputStream = null;
    try {
      outputStream = new FileOutputStream(file, true);
      outputStream.write(bytes);
      //异步处理数据
      syncService.addBatchData(this.moduleManageService, this.session, this.userId, file,
          this.moduleEnv, this.certificate, this.projectCode, this.moduleTypeCode, this.gitBranch);
    } catch (IOException e) {
      session.sendText(JSONObject.toJSONString(UploadModuleStatusDTO.fail(fileName, e.getMessage())));
      session.close();
    } finally {
      try {
        if (outputStream != null) {
          outputStream.close();
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
}
