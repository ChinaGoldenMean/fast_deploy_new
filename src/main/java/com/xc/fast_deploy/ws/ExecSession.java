package com.xc.fast_deploy.ws;

import lombok.Data;
import org.yeauty.pojo.Session;
//import org.springframework.web.socket.WebSocketSession;

import java.util.concurrent.ThreadPoolExecutor;

@Data
public class ExecSession {
  
  private String id;
  //    private WebSocketSession session;
  private Session session;
  private Process process;
  private ThreadPoolExecutor poolExecutor;
  
  public ExecSession(String id, Session session, Process process, ThreadPoolExecutor poolExecutor) {
    this.id = id;
    this.session = session;
    this.process = process;
    this.poolExecutor = poolExecutor;
  }
}
