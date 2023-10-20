package com.xc.fast_deploy.websocketConfig.myThread;

import com.google.common.io.ByteStreams;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.yeauty.pojo.Session;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

@Slf4j
public class OutputThread extends Thread {
  
  private InputStream inputStream;
  private Session session;
  private static final Integer TIME_OUT = 10 * 60 * 1000;
  
  public OutputThread(InputStream inputStream, Session session) {
    this.inputStream = inputStream;
    this.session = session;
  }
  
  @Override
  public void run() {
    try {
      long start = System.currentTimeMillis();
      byte[] bytes = new byte[4096];
//            StringWriter writer = new StringWriter();
//            while (true) {
//                IOUtils.copy(inputStream, writer, StandardCharsets.UTF_8.name());
//                session.sendText(writer.toString());
//            }
      
      while (session.isRegistered() && !this.isInterrupted()
          && (System.currentTimeMillis() - start <= TIME_OUT)) {
        int n = inputStream.read(bytes);
        if (n > 0) {
          String msg = new String(bytes, 0, n);
          session.sendText(msg);
        }
      }
    } catch (Exception e) {
      
      log.error("线程中断,线程关闭: {}", e.getMessage());
      e.printStackTrace();
    } finally {
      log.info("当前线程执行完毕,session: {}", session);
      if (this.inputStream != null) {
        try {
          this.inputStream.close();
          session.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
  }
}
