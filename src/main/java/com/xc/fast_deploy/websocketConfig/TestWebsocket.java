package com.xc.fast_deploy.websocketConfig;

import com.alibaba.fastjson.JSONObject;
import com.xc.fast_deploy.dto.module.ModuleManageDTO;
import com.xc.fast_deploy.service.common.ModuleManageService;
import com.xc.fast_deploy.service.common.SyncService;
import com.xc.fast_deploy.websocketConfig.myThread.TestThread;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.yeauty.annotation.*;
import org.yeauty.pojo.ParameterMap;
import org.yeauty.pojo.Session;

import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@ServerEndpoint(prefix = "netty.websocket", value = "/test/test/")
@Component
@Slf4j
public class TestWebsocket {
  
  @Autowired
  private SyncService syncService;
  @Autowired
  private ModuleManageService manageService;
  
  private ThreadPoolExecutor poolExecutor;
  
  @OnOpen
  public void onOpen(Session session, ParameterMap parameterMap) throws IOException {
    System.out.println(this);
    ModuleManageDTO moduleManageDTO = manageService.selectInfoById(37);
    System.out.println(JSONObject.toJSONString(moduleManageDTO));
    System.out.println("new connection");
  }
  
  @OnClose
  public void onClose(Session session) throws IOException {
    System.out.println("one connection closed");
    session.close();
    syncService.shutdownExcutor(poolExecutor, session, 1);
  }
  
  @OnError
  public void onError(Session session, Throwable throwable) {
    throwable.printStackTrace();
  }
  
  @OnMessage
  public void onMessage(Session session, String message) {
    System.out.println(message);
    poolExecutor = new ThreadPoolExecutor(10, 10, 0L,
        TimeUnit.SECONDS, new ArrayBlockingQueue<>(1000), new ThreadPoolExecutor.CallerRunsPolicy());
    poolExecutor.submit(new TestThread(session));
    
  }
  
  @OnBinary
  public void onBinary(Session session, byte[] bytes) {
    for (byte b : bytes) {
      System.out.println(b);
    }
    session.sendBinary(bytes);
  }
  
  @OnEvent
  public void onEvent(Session session, Object evt) {
    if (evt instanceof IdleStateEvent) {
      IdleStateEvent idleStateEvent = (IdleStateEvent) evt;
      switch (idleStateEvent.state()) {
        case READER_IDLE:
          System.out.println("read idle");
          break;
        case WRITER_IDLE:
          System.out.println("write idle");
          break;
        case ALL_IDLE:
          System.out.println("all idle");
          break;
        default:
          break;
      }
    }
  }
}
