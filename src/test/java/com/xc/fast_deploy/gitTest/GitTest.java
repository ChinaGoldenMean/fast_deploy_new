package com.xc.fast_deploy.gitTest;

import com.xc.fast_deploy.utils.code_utils.GitUtils;
import com.xc.fast_deploy.utils.encyption_utils.EncryptUtil;
import lombok.SneakyThrows;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PullCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

/**
 * @Author litiewang
 * @Date 2023-07-07 14:19
 * @Version 1.0
 */
public class GitTest {
  @SneakyThrows
  public static void main(String[] args) {
    String password = "8UUnEHx0EAyvO8TLCmIjxuXS5VCJWNBoNgVzF+jI4Q";
    
    String username = "srd13335713271";
    String path = "D:/storge/crm-sit37/cus-web/bss-cust-asset-query-in-controller/smt-bss-cust-asset-query-in-controller";
    String repositoryPath = "D:/storge/crm-sit37/cus-web/bss-cust-asset-query-in-controller";
    String workDirPath = "D:/storge/crm-sit37/cus-web/bss-cust-asset-query-in-controller"; // 设置工作目录
    
    GitUtils.gitCheckoutBranchAndPull(workDirPath,
        username, password, "master");
  }
  
  @SneakyThrows
  public static void pull1(String path, String username, String password) {
    GitUtils.gitPull(path, username, password);
    
  }
  
  @SneakyThrows
  public static void clone1() {
    String password = "8UUnEHx0EAyvO8TLCmIjxuXS5VCJWNBoNgVzF+jI4Q";
    String repositoryURL = "https://yd-code.srdcloud.cn/a/zj-crm-ord/bss-dev/smt-bss-order-shopcart-dubbo";
    GitUtils.gitClone(repositoryURL,
        "D:\\storge\\crm-pst25\\opt\\test-git\\test",
        "srd13335713271",
        password,
        null);
  }
  
  public static void clone2(String repositoryURL, String path, String username, String password) {
    
    CredentialsProvider credentialsProvider = new UsernamePasswordCredentialsProvider(username, password);
    
    try {
      Git.cloneRepository()
          .setURI(repositoryURL)
          .setCredentialsProvider(credentialsProvider)
          .setDirectory(new File(path))
          .call();
      System.out.println("Repository cloned successfully.");
    } catch (GitAPIException e) {
      e.printStackTrace();
    }
  }
  
  public static void pull() {
  
  }
  
  @SneakyThrows
  public static void sh() {
    String command = "sh D:/storge/crm-pst25/opt/git-test/svn-co-smt-bss-order-shopcart-dubbo.sh";
    String workingDir = "D:/storge/crm-pst25/opt/git-test/";
    // 创建进程并执行Shell命令
    Process process = Runtime.getRuntime().exec(command, null, new File(workingDir));
    try {
      // 读取Shell命令的输出
      BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
      String line;
      
      // 等待命令执行完成
      int exitCode = process.waitFor();
      
      System.out.println("命令执行完成，退出码: " + exitCode);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}
