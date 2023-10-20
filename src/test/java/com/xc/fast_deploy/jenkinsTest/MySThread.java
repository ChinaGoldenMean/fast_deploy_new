package com.xc.fast_deploy.jenkinsTest;

import com.xc.fast_deploy.utils.code_utils.SvnUtils;
import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNRevision;

import java.io.File;

public class MySThread implements Runnable {
  private String filePath;
  private String url;
  
  public MySThread(String filePath, String url) {
    this.filePath = filePath;
    this.url = url;
  }
  
  @Override
  public void run() {
    try {
      SVNClientManager clientManager = SvnUtils.getManager(MyJenkinsTest.username, MyJenkinsTest.password);
      SvnUtils.checkout(clientManager, SVNURL.parseURIEncoded(url),
          SVNRevision.create(SvnUtils.LATEST_REVERSION), new File(filePath),
          SVNDepth.INFINITY);
    } catch (SVNException e) {
      e.printStackTrace();
    }
  }
}
