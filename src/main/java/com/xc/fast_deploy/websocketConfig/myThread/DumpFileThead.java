package com.xc.fast_deploy.websocketConfig.myThread;

import lombok.extern.slf4j.Slf4j;
import org.yeauty.pojo.Session;

import java.io.*;

@Slf4j
public class DumpFileThead extends Thread {
  
  private InputStream inputStream;
  private Session session;
  private String podName;
  private static final Integer TIME_OUT = 3 * 60 * 1000;
  
  public DumpFileThead(InputStream inputStream, Session session, String podName) {
    this.inputStream = inputStream;
    this.session = session;
    this.podName = podName;
  }
  
  @Override
  public void run() {
    try {
      long start = System.currentTimeMillis();
      byte[] bytes = new byte[4096];
      while (session.isRegistered()
          && !this.isInterrupted()
          && (System.currentTimeMillis() - start <= TIME_OUT)) {
        int n = this.inputStream.read(bytes);
        if (n > 0) {
          String msg = new String(bytes, 0, n);
          session.sendText(podName + ":  " + msg);
          if (msg.contains("/sbin")) {
            session.sendText("------------------------" + podName + ":dump失败-------------------------");
          }
        }
      }
    } catch (Exception e) {
      log.error("线程中断,线程关闭: {}", e.getMessage());
    } finally {
      log.info("inputStream关闭");
      if (inputStream != null) {
        try {
          inputStream.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
  }
}
