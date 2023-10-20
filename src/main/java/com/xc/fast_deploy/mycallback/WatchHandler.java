package com.xc.fast_deploy.mycallback;

import com.xc.fast_deploy.websocketConfig.K8sPodWatchWebsocketServer;
import io.kubernetes.client.openapi.models.V1Pod;
import io.kubernetes.client.util.Watch;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;

@Slf4j
public class WatchHandler implements Runnable {
  
  private Watch<V1Pod> watch;
  
  private K8sPodWatchWebsocketServer watchWebsocketServer;
  
  public WatchHandler(Watch<V1Pod> watch, K8sPodWatchWebsocketServer watchWebsocketServer) {
    this.watchWebsocketServer = watchWebsocketServer;
    this.watch = watch;
  }
  
  @Override
  public void run() {
    if (this.watch != null && watchWebsocketServer != null) {
      while (true) {
        log.info("当前handler 启动run: " + new Date().toLocaleString());
        Watch.Response<V1Pod> podResponse = watch.next();
        
        watch.forEach(v1PodResponse -> {
          if (v1PodResponse != null) {
            watchWebsocketServer.sendMessage(new Date().toLocaleString() +
                " 当前pod: " + v1PodResponse.object.getMetadata().getName() + " hostIP: " +
                v1PodResponse.object.getStatus().getHostIP() +
                " phase: " + v1PodResponse.object.getStatus().getPhase() +
                " pod的状态: " + v1PodResponse.status + " type: " + v1PodResponse.type);
            log.info("当前pod: " + v1PodResponse.object.getMetadata().getName() + " hostIP: " +
                v1PodResponse.object.getStatus().getHostIP() +
                " phase: " + v1PodResponse.object.getStatus().getPhase() +
                " pod的状态: " + v1PodResponse.status + " type: " + v1PodResponse.type);
          } else {
            watchWebsocketServer.sendMessage("response: " + v1PodResponse);
            log.info("response: " + v1PodResponse);
          }
        });
        
        //每隔5s打印一次
        try {
          Thread.sleep(5 * 1000);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    }
  }
}
