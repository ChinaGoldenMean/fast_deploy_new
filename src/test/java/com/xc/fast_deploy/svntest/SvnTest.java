package com.xc.fast_deploy.svntest;

import com.alibaba.fastjson.JSONObject;
import com.xc.fast_deploy.dto.CodeUpdateInfoDTO;
import com.xc.fast_deploy.service.common.ModuleManageService;
import com.xc.fast_deploy.utils.code_utils.SvnUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.tmatesoft.svn.core.*;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.wc.*;

import java.io.File;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class SvnTest {
  
  //        String url = "https://192.168.132.138/project/";
//    String password = "admin";
//    String username = "admin";
  String url = "F:\\project\\back";
  String password = "jellf";
  String username = "jellf";
  
  @Test
  public void testCheckout() throws SVNException {
    SVNRepository svnRepository = SvnUtils.authSvn(url, username, password);
    List<CodeUpdateInfoDTO> updateInfo = SvnUtils.getUpdateInfo(svnRepository, url);
    System.out.println(JSONObject.toJSONString(updateInfo));
  }
  
  @Test
  public void testSVN() throws SVNException {
    SVNClientManager clientManager = SvnUtils.getManager(username, password);
//        SVNRepository repository = clientManager.createRepository(SVNURL.parseURIEncoded(url), false);
//        SVNNodeKind svnNodeKind = repository.checkPath("F:\\storge\\test1", 1);
//        long datedRevision = repository.getDatedRevision(new Date());

//        SVNUpdateClient updateClient = clientManager.getUpdateClient();
//        long checkout = updateClient.doCheckout(SVNURL.parseURIEncoded(url), new File("F:\\storge\\test1"), SVNRevision.create(-1), SVNRevision.create(-1),
//                SVNDepth.INFINITY, false);
//        System.out.println(checkout);

//        long l = updateClient.doUpdate(new File("F:\\storge\\test1"), SVNRevision.create(3), SVNDepth.INFINITY
//                , false, false);
//        System.out.println(l);
    
    SVNLogClient logClient = clientManager.getLogClient();
//        logClient.doLog(new File[]{new File("F:\\project\\back")}, null, SVNRevision.create(0), SVNRevision.create(-1),
//                true, true, null, new ISVNLogEntryHandler() {
//                    @Override
//                    public void handleLogEntry(SVNLogEntry svnLogEntry) throws SVNException {
//                        logEntries.add(svnLogEntry);
//                    }
//                });
  }
  
  @Autowired
  private ModuleManageService manageService;
  
  @Test
  public void test9898() {

//        CoreV1Api coreV1Api = K8sManagement.getCoreV1Api(K8sUtils.MY_OWN);
//        V1DeleteOptions deleteOptions = new V1DeleteOptions();
//        deleteOptions.setGracePeriodSeconds(120L);
//        deleteOptions.setOrphanDependents(false);
//        try {
//            V1Status v1Status = coreV1Api.deleteNamespacedPod("boot-demo2-7f85874f44-n48t7",
//                    K8sNameSpace.DEFAULT, deleteOptions, null, null,
//                    null, false, null);
//            System.out.println(v1Status.getCode());
//            if (v1Status.getCode() == 200) {
//                System.out.println("删除成功");
//            }
//        } catch (ApiException e) {
//            e.printStackTrace();
//        } catch (JsonSyntaxException e) {
//            K8sExceptionUtils.anylse(e);
//        }

//        String deleteBack = K8sUtils.okhttpDeleteBack(K8sUtils.MY_OWN,
//                "/api/v1/namespaces/default/pods/boot-demo2-7f85874f44-6q4xh?gracePeriodSeconds=120");
//
//        System.out.println(deleteBack);
    
    File file = manageService.genManageFile(30);
    
  }
}
