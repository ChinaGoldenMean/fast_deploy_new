package com.xc.fast_deploy.websocketConfig.myThread;

import lombok.extern.slf4j.Slf4j;
import org.yeauty.pojo.Session;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

@Slf4j
public class ClearMemoryThread implements Runnable {
  
  private Process process;
  private Session session;
  private String podName;
  
  public ClearMemoryThread(Process process, Session session, String podName) {
    this.process = process;
    this.session = session;
    this.podName = podName;
  }
  
  @Override
  public void run() {
    log.info("执行: podName:{}", podName);
    this.session.sendText(podName + ":执行清缓存,请等待...");
    InputStream inputStream = process.getInputStream();
    printOutMessage(inputStream);
    try {
      this.process.waitFor(3, TimeUnit.SECONDS);
      Thread.sleep(500);
      this.process.destroy();
      log.info("process exitValue:{}", this.process.exitValue());
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
  
  private void printOutMessage(InputStream inputStream) {
    byte[] bytes = new byte[20480];
    try {
      int len;
      while (true) {
        len = inputStream.read(bytes);
        this.session.sendText(podName + " : " + new String(bytes, 0, len));
        if (len == -1) {
          return;
        }
      }
    } catch (IOException e) {
      log.error("出现相关错误:{}", e.getMessage());
    } finally {
      log.info("执行finally方法");
      if (inputStream != null) {
        try {
          inputStream.close();
        } catch (Exception e) {
          log.error("出现相关错误:{}", e.getMessage());
        }
      }
      session.sendText("清除结束");
    }
  }
  
}
