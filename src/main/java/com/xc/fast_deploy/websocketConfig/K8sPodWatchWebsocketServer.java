package com.xc.fast_deploy.websocketConfig;

import com.xc.fast_deploy.service.k8s.K8sService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.yeauty.annotation.*;
import org.yeauty.annotation.ServerEndpoint;
import org.yeauty.pojo.Session;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@ServerEndpoint(prefix = "netty.websocket", value = "/websocket/k8s/watch/pods")
@Component
@Slf4j
public class K8sPodWatchWebsocketServer {
  
  @Autowired
  private K8sService k8sService;
  
  private static ExecutorService executorService = Executors.newSingleThreadExecutor();
  
  //concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。
  private static transient volatile Set<K8sPodWatchWebsocketServer> webSocketSet = ConcurrentHashMap.newKeySet();
  
  private Session session;
  
  /**
   * 连接建立成功是调用的方法
   */
  @OnOpen
  public void onOpen(Session session) {
    log.info("有新窗口开始监听:" + session.isRegistered());
    this.session = session;
    webSocketSet.add(this);
    k8sService.startWatchPods(this, executorService);
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
    webSocketSet.remove(this);
  }
  
}
