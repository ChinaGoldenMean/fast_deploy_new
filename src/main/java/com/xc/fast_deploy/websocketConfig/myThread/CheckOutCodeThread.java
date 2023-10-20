package com.xc.fast_deploy.websocketConfig.myThread;

import com.alibaba.fastjson.JSONObject;
import com.xc.fast_deploy.dto.StatusDTO;
import com.xc.fast_deploy.model.master_model.ModuleCertificate;
import com.xc.fast_deploy.myException.SvnUrlNotExistException;
import com.xc.fast_deploy.utils.encyption_utils.EncryptUtil;
import com.xc.fast_deploy.utils.code_utils.SvnUtils;
import com.xc.fast_deploy.vo.module_vo.param.ModulePackageParamVo;
import lombok.extern.slf4j.Slf4j;
import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.yeauty.pojo.Session;

import java.io.File;

@Slf4j
public class CheckOutCodeThread implements Runnable {
  
  private ModuleCertificate certificate;
  private ModulePackageParamVo packageParamVo;
  private String filePrefix;
  private Session session;
  private StatusDTO statusDTO;
  
  public CheckOutCodeThread(ModuleCertificate certificate, ModulePackageParamVo packageParamVo,
                            String filePrefix, Session session, StatusDTO statusDTO) {
    this.certificate = certificate;
    this.packageParamVo = packageParamVo;
    this.filePrefix = filePrefix;
    this.session = session;
    this.statusDTO = statusDTO;
  }
  
  @Override
  public void run() {
    try {
      log.info("check out 数据:{} filePrefix:{}", JSONObject.toJSONString(packageParamVo), filePrefix);
      SVNClientManager clientManager = SvnUtils.getManager(certificate.getUsername(),
          EncryptUtil.decrypt(certificate.getPassword()));
//           clientManager.createRepository(SVNURL.parseURIEncoded(packageParamVo.getCodeUrl()), true);
      //checkout 新代码到模块目录下
      SvnUtils.checkout(clientManager, SVNURL.parseURIEncoded(packageParamVo.getCodeUrl()),
          SVNRevision.create(SvnUtils.LATEST_REVERSION),
          new File(filePrefix + packageParamVo.getContentName()), SVNDepth.INFINITY);
      if (session != null) {
        session.sendText(JSONObject.toJSONString(statusDTO));
      }
    } catch (SVNException e) {
      log.error("svn check out 出现异常: {}", e.getMessage());
      e.printStackTrace();
      throw new SvnUrlNotExistException(e.getMessage());
    }
  }
}
