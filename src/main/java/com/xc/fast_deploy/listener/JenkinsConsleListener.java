package com.xc.fast_deploy.listener;

import com.offbytwo.jenkins.helper.BuildConsoleStreamListener;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.yeauty.pojo.Session;

@Slf4j
public class JenkinsConsleListener implements BuildConsoleStreamListener {
  
  private Session session;
  
  public JenkinsConsleListener(Session session) {
    this.session = session;
  }
  
  @Override
  public void onData(String s) {
    if (StringUtils.isNotBlank(s)) {
      s = s.trim();
    }
    session.sendText(s.trim());
  }
  
  @Override
  public void finished() {
    if (session != null) {
      session.sendText("消息发送结束");
      session.close();
    }
  }
}
