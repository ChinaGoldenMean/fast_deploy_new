package com.xc.fast_deploy.gitTest;

import ch.qos.logback.classic.turbo.TurboFilter;
import com.xc.fast_deploy.FastDeployApplication;
import com.xc.fast_deploy.dao.master_dao.ModuleCertificateMapper;
import com.xc.fast_deploy.dao.master_dao.ModulePackageMapper;
import com.xc.fast_deploy.dto.module.ModuleManageDTO;
import com.xc.fast_deploy.model.master_model.ModuleCertificate;
import com.xc.fast_deploy.model.master_model.ModulePackage;
import com.xc.fast_deploy.model.master_model.example.ModulePackageExample;
import com.xc.fast_deploy.rediscache.JedisManage;
import com.xc.fast_deploy.service.common.*;
import com.xc.fast_deploy.utils.HttpUtils;
import com.xc.fast_deploy.utils.encyption_utils.EncryptUtil;
import com.xc.fast_deploy.utils.code_utils.GitUtils;
import com.xc.fast_deploy.vo.module_vo.DeployModuleVo;
import com.xc.fast_deploy.vo.module_vo.ModuleDeployVo;
import com.xc.fast_deploy.vo.module_vo.ModuleEnvCenterManageVo;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONArray;
import org.apache.catalina.valves.RemoteIpValve;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.aspectj.util.FileUtil;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListBranchCommand;
import org.eclipse.jgit.api.errors.*;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.StoredConfig;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.transport.RefSpec;
import org.eclipse.jgit.transport.URIish;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.info.ProjectInfoProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.tmatesoft.svn.core.SVNException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class FirstGitTest {
  
  @Autowired
  private ModuleDeployService deployService;
  @Autowired
  private ModulePackageService packageService;
  @Autowired
  private ModuleManageService manageService;
  @Autowired
  private ModuleCertificateMapper certificateMapper;
  @Autowired
  private SyncService syncService;
  @Autowired
  private ModulePackageMapper packageMapper;
  
  public static String username = "deploy";
  public static String remoteUrl = "http://134.108.7.28:32081/deploy/git_test.git";
  public static String password = "cicd#repo998";
  public static String path = "C:\\Users\\yanleideveloper\\Desktop\\git_add\\test";
  
  @Test
  public void uploadGit() {

//        Set<Integer> set = new HashSet<>();
//        set.add(30);
//        set.add(35);
//        ModuleDeployVo deployVo = new ModuleDeployVo();
//        deployVo.setEnvIds(set);
    Map<String, String> gitMap = new HashMap<>();
    for (int i = 1; i <= 5; i++) {
      String url = "http://10.128.22.195:8080/api/v3/projects?private_token=yWFrXFNGw7bQDTnirxoT&page=6&per_page=100";
      url = url.replace("page=6", "page=" + i);
      String result = HttpUtils.httpGetUrl(url, null, null);
      JSONArray jsonArray = JSONArray.fromObject(result);
      List<Map<String, Object>> list = (List<Map<String, Object>>) jsonArray.toCollection(jsonArray, Map.class);
      for (int j = 0; j < list.size(); j++) {
        String moduleName = (String) list.get(j).get("name");
        String gitUrl = (String) list.get(j).get("http_url_to_repo");
        gitMap.put(moduleName, gitUrl);
        //    deployVo.setKeyName(moduleName);
//                List<ModuleEnvCenterManageVo> manageVoList = deployService.selectByDeployVO(deployVo);
//                if (manageVoList != null && manageVoList.size() > 0){
//                    ModulePackageExample example = new ModulePackageExample();
//                    example.createCriteria().andModuleIdEqualTo(manageVoList.get(0).getModuleId());
//                    List<ModulePackage> modulePackages = packageMapper.selectByExample(example);
//                    ModuleCertificate moduleCertificate =
//                            certificateMapper.selectCertByModuleId(manageVoList.get(0).getModuleId());
//                    syncService.checkoutAllAsync(modulePackages,moduleCertificate);
//                } else {
//                    continue;
//                }
      }
    }
    UsernamePasswordCredentialsProvider provider =
        new UsernamePasswordCredentialsProvider("71071805_zj", "Repo#922lib");
    
    File[] files = new File[]{new File("D:/storge/crm-prod30"), new File("D:/storge/billing-prod35")};
    for (File file : files) {
      File[] childFiles = file.listFiles();
      for (File centerdir : childFiles) {
        File[] childFiles2 = centerdir.listFiles();
        for (File moduledir : childFiles2) {
          if (moduledir.isDirectory() && gitMap.keySet().contains(moduledir.getName())) {
            String git_url = gitMap.get(moduledir.getName());
            try {
              FileUtils.deleteDirectory(new File(moduledir.getAbsolutePath() + "\\.git"));
              Git git = Git.init().setDirectory(new File(moduledir.getAbsolutePath())).call();
              git.remoteAdd().setUri(new URIish(git_url)).setName("origin").call();
              git.add().addFilepattern(".").call();
              git.commit().setMessage("luochangbin").call();
              git.push().setCredentialsProvider(provider).call();
              gitMap.remove(moduledir.getName());
              System.out.println(moduledir.getName() + "-------------->完成");
            } catch (GitAPIException | URISyntaxException | IOException e) {
              e.printStackTrace();
            }
          }
        }
      }
    }
    System.out.println(gitMap);
    
    //GitUtils.gitClone(remoteUrl,git_test,username,password,"gitTest");
//            Git git = Git.open(new File(git_test + "\\.git"));
//            git.add().addFilepattern(".").call();
//            git.commit().setMessage("testUploadFile").call();
//            git.push().call();
//            GitUtils.gitClone(remoteUrl, path, username, password, "path1");
//            GitUtils.gitPull(path, username, password);
//            List<CodeUpdateInfoDTO> updateInfoDTOS = GitUtils.gitLog(path);
//            System.out.println(updateInfoDTOS);
//            List<String> branchRemoteList = GitUtils.branchRemoteList(path, username, password);
    // boolean path3 = GitUtils.gitCheckoutBranchAndPull(path, username, password, "path1");
//            String reversion = GitUtils.getUrlReversion(remoteUrl, username, password);
//            System.out.println(reversion);
//            GitUtils.gitReset(path,"cf7d1d0e294336e5e29c40e58aeb914ae17f16af");
  
  }
  
  @Autowired
  private JedisManage jedisManage;
  
  @Test
  public void testJedis() {
    String decrypt = EncryptUtil.decrypt("SetCti9u9iYrAsbeFDAPAg==");
    System.out.println(decrypt);
  }
  
  @Test
  public void testuploadGit2() {
    String url = "http://10.128.22.195:8080/zj-cus-service/bss-customer-jt-write-controller.git";
    String git_test = "D:\\storge\\crm-prod30\\cus-service\\bss-customer-jt-write-controller";
    try {
      FileUtils.deleteDirectory(new File("D:\\tmp"));
      UsernamePasswordCredentialsProvider provider =
          new UsernamePasswordCredentialsProvider("71071805_zj", "Repo#922lib");
      Git git = Git.cloneRepository().setCredentialsProvider(provider).setURI(url)
          .setDirectory(new File("D:\\tmp")).call();
      FileUtils.deleteDirectory(new File(git_test + "\\.git"));
      FileUtils.copyDirectoryToDirectory(new File("D:\\tmp\\.git"), new File(git_test));
      Git git2 = Git.open(new File(git_test + "\\.git"));
      git2.add().addFilepattern(".").call();
      git2.commit().setMessage("luochangbin").call();
      git2.push().setCredentialsProvider(provider).setRemote("origin").call();
      // FileUtils.deleteDirectory(new File("D:\\tmp"));
    } catch (UnmergedPathsException e) {
      e.printStackTrace();
    } catch (AbortedByHookException e) {
      e.printStackTrace();
    } catch (WrongRepositoryStateException e) {
      e.printStackTrace();
    } catch (ConcurrentRefUpdateException e) {
      e.printStackTrace();
    } catch (NoHeadException e) {
      e.printStackTrace();
    } catch (NoFilepatternException e) {
      e.printStackTrace();
    } catch (NoMessageException e) {
      e.printStackTrace();
    } catch (GitAPIException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  @Test
  public void uploadGit3() {
    String url = "http://10.128.22.195:8080/zj-cus-service/bss-customer-jt-write-controller.git";
    String git_test = "D:\\tmp\\";
    UsernamePasswordCredentialsProvider provider =
        new UsernamePasswordCredentialsProvider("71071805_zj", "Repo#922lib");
    try {
      Git git = Git.init().setDirectory(new File(git_test)).call();
      git.remoteAdd().setName("origin").setUri(new URIish(url)).call();
      // git.branchCreate().setName("master").setForce(true).call();
      git.add().addFilepattern(".").call();
      git.commit().setMessage("test").call();
      git.push().setCredentialsProvider(provider).call();
    } catch (Exception e) {
      e.printStackTrace();
    }
    
  }
}
