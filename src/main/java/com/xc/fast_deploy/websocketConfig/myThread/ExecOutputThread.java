package com.xc.fast_deploy.websocketConfig.myThread;

import com.xc.fast_deploy.utils.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.yeauty.pojo.Session;

import java.io.IOException;
import java.io.OutputStream;

@Slf4j
public class ExecOutputThread implements Runnable {
  
  private Process process;
  private Session session;
  private String mountPath;
  private String podName;
  private static final Integer TIME_OUT = 3 * 1000;
  private static final Integer TIME_OUT2 = 1000;
  private OutputStream outputStream;
  private StringBuffer filePrefix;
  
  public ExecOutputThread(Process process, Session session,
                          String mountPath, String podName, StringBuffer filePrefix) {
    this.session = session;
    this.process = process;
    this.mountPath = mountPath;
    this.podName = podName;
    this.filePrefix = filePrefix;
    this.outputStream = process.getOutputStream();
  }
  
  @Override
  public void run() {
    try {
      StringBuffer link = new StringBuffer();
      link.append("rm -rf ").append(filePrefix).append("\n");
      session.sendText(podName + ":  首先删除文件夹: " + link.toString());
      outputStream.write(link.toString().getBytes());
      Thread.sleep(TIME_OUT2);
      
      StringBuffer link0 = new StringBuffer();
      link0.append("mkdir ").append(filePrefix).append("\n");
      session.sendText(podName + ":  创建文件夹: " + link0.toString());
      outputStream.write(link0.toString().getBytes());
      Thread.sleep(TIME_OUT2);
      
      StringBuffer link1 = new StringBuffer();
      link1.append("jstack -l 1 > ").append(filePrefix)
          .append(podName).append("-").append(DateUtils.generateDateOnlyString())
          .append(".log \n");
      session.sendText(podName + ":  开始执行jstack 1 " + link1.toString());
      outputStream.write(link1.toString().getBytes());
      Thread.sleep(TIME_OUT);
      
      StringBuffer link4 = new StringBuffer();
      link4.append("top -H -p 1 -b -n 3 > ").append(filePrefix)
          .append(podName).append("-").append("top-")
          .append(DateUtils.generateDateOnlyString()).append(".log \n");
      
      session.sendText(podName + ":  开始执行top 1 " + link4.toString());
      outputStream.write(link4.toString().getBytes());
      Thread.sleep(TIME_OUT2);
      
      StringBuffer link2 = new StringBuffer();
      link2.append("jstack -l 1 > ").append(filePrefix)
          .append(podName).append("-").append(DateUtils.generateDateOnlyString())
          .append(".log \n");
      session.sendText(podName + ":  开始执行jstack 2 " + link2.toString());
      outputStream.write(link2.toString().getBytes());
      Thread.sleep(TIME_OUT);
      
      StringBuffer link5 = new StringBuffer();
      link5.append("top -H -p 1 -b -n 3 > ").append(filePrefix)
          .append(podName).append("-").append("top-")
          .append(DateUtils.generateDateOnlyString()).append(".log \n");
      
      session.sendText(podName + ":  开始执行top 2 " + link5.toString());
      outputStream.write(link5.toString().getBytes());
      Thread.sleep(TIME_OUT2);
      
      StringBuffer link3 = new StringBuffer();
      link3.append("jstack -l 1 > ").append(filePrefix)
          .append(podName).append("-").append(DateUtils.generateDateOnlyString())
          .append(".log \n");
      session.sendText(podName + ":  开始执行jstack 3 " + link3.toString());
      outputStream.write(link3.toString().getBytes());
      Thread.sleep(TIME_OUT);
      
      StringBuffer link6 = new StringBuffer();
      link6.append("top -H -p 1 -b -n 3 > ").append(filePrefix)
          .append(podName).append("-").append("top-")
          .append(DateUtils.generateDateOnlyString()).append(".log \n");
      session.sendText(podName + ":  开始执行top 3 " + link6.toString());
      outputStream.write(link6.toString().getBytes());
      Thread.sleep(TIME_OUT2);
      
      StringBuffer link7 = new StringBuffer();
      link7.append("cd ").append(filePrefix)
          .append(";for file in `ls`;do if [ ! -s $file ];then echo $PATH*********************$file;fi;done \n");
      this.outputStream.write(link7.toString().getBytes());
      Thread.sleep(TIME_OUT2);
      
    } catch (IOException e) {
      session.sendText("发送命令出错" + e.getMessage());
      e.printStackTrace();
    } catch (InterruptedException e) {
      session.sendText("线程等待出错");
      e.printStackTrace();
    } finally {
      if (outputStream != null) {
        try {
          outputStream.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
  }
}
