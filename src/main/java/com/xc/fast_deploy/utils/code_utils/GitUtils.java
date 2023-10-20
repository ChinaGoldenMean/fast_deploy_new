package com.xc.fast_deploy.utils.code_utils;

import cn.hutool.core.io.FileUtil;
import cn.hutool.system.OsInfo;
import cn.hutool.system.SystemUtil;
import com.xc.fast_deploy.dto.CodeUpdateInfoDTO;
import com.xc.fast_deploy.utils.FoldUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListBranchCommand;
import org.eclipse.jgit.api.PullResult;
import org.eclipse.jgit.api.ResetCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.*;
import org.eclipse.jgit.lib.*;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.FetchResult;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.eclipse.jgit.treewalk.AbstractTreeIterator;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;

import java.io.*;
import java.util.*;

@Slf4j
public class GitUtils {
  public static void main(String[] args) {
    
    GitUtils.generateXML("E:\\Users\\litiewang\\Downloads\\", "svn-co-smt-bss-operation-outdubbo-trunk.sh");
  }
  
  @SneakyThrows
  public static void generateXML(String workingDir, String file) {
    // 创建进程并执行Shell命令
    
    try (InputStream inputStream = new FileInputStream(workingDir + file)) {
      // 使用 inputStream 进行操作
      // 例如，读取数据或将其传递给其他方法
      // ...
      BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
      String line;
      StringBuilder outputBuilder = new StringBuilder();
// 读取输出直到找到 echo 命令的输出行
      String echoOutput = "";
      boolean echoFound = false;
      while ((line = reader.readLine()) != null) {
        if (line.trim().startsWith("echo")) {
          echoFound = true;
        } else if (line.trim().startsWith("pom.xml")) {
          break;
        }
        
        if (echoFound) {
          outputBuilder.append(line).append(System.lineSeparator());
        }
      }
      echoOutput = outputBuilder.toString().replace("echo", "").replace(">pom.xml", "").replace("\"<", "<").replace("\\\"", "\"").replace(">\"", ">");
      try (BufferedWriter writer = new BufferedWriter(new FileWriter(workingDir + "pom.xml"))) {
        writer.write(echoOutput);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  @SneakyThrows
  public static void gitShClone(String workingDir, String file) {
    // 创建进程并执行Shell命令
    String command = "sh " + workingDir + file;
    
    ProcessBuilder processBuilder = new ProcessBuilder(command);
    processBuilder.directory(new File(workingDir));
    
    try {
      Process process = processBuilder.start();
      
      // 读取Shell命令的输出
      BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
      String line;
      while ((line = reader.readLine()) != null) {
        // 处理命令输出
        System.out.println(line);
      }
      // 等待命令执行完成
      int exitCode = process.waitFor();
      System.out.println("Command exited with code: " + exitCode);
    } catch (IOException e) {
      e.printStackTrace();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    
  }
  
  /**
   * git clone code 到本地,本地文件夹必须是空的新建的  支持http的连接 其他的不支持
   *
   * @param remoteUrl
   * @param path
   * @param username
   * @param password
   * @throws GitAPIException
   */
  public static void gitClone(String remoteUrl, String path, String username,
                              String password, String branchName) throws GitAPIException {
    log.info("git clone 代码执行 remoteUrl:{},path:{},username:{},branchName:{}",
        remoteUrl, path, username, branchName);
    if (StringUtils.isNotBlank(remoteUrl) && StringUtils.isNotBlank(path)) {
      if (StringUtils.isNotBlank(username) && StringUtils.isNotBlank(password)) {
        CredentialsProvider provider = new UsernamePasswordCredentialsProvider(username, password);
        Git.cloneRepository().setURI(remoteUrl).setCredentialsProvider(provider)
            .setDirectory(new File(path)).setBranch(branchName).setCloneAllBranches(true).call();
        
      } else {
        Git.cloneRepository().setURI(remoteUrl).setDirectory(new File(path)).call();
      }
    }
  }
  
  /**
   * 获取url地址对应的版本号
   *
   * @param codeUrl
   * @param username
   * @param password
   * @return
   * @throws GitAPIException
   */
  public static String getUrlReversion(String codeUrl, String username, String password) throws GitAPIException {
    if (StringUtils.isNotBlank(codeUrl) &&
        StringUtils.isNotBlank(username) && StringUtils.isNotBlank(password)) {
      CredentialsProvider provider = new UsernamePasswordCredentialsProvider(username, password);
      Collection<Ref> collection = Git.lsRemoteRepository()
          .setRemote(codeUrl).setCredentialsProvider(provider).call();
      if (collection != null && collection.size() > 0) {
        for (Ref ref : collection) {
          if (ref.getObjectId() != null) {
            return ref.getObjectId().getName();
          }
        }
      }
    }
    return null;
  }
  
  /**
   * 获取某个url路径上对应的所有日志
   *
   * @param path
   * @return
   * @throws IOException
   * @throws GitAPIException
   */
  public static List<CodeUpdateInfoDTO> gitLog(String path) throws IOException, GitAPIException {
    log.info("开始获取git——log日志");
    List<CodeUpdateInfoDTO> gitLogInfoDTOS = new ArrayList<>();
    if (StringUtils.isNotBlank(path) && new File(path).exists()) {
      File file = new File(path);
      Git git = Git.open(file);
      Repository repository = git.getRepository();
      RevWalk revWalk = new RevWalk(repository);
      int num = 0;
      Map<AnyObjectId, Set<Ref>> objectIdSetMap = repository.getAllRefsByPeeledObjectId();
      for (AnyObjectId anyObjectId : objectIdSetMap.keySet()) {
        revWalk.markStart(revWalk.parseCommit(anyObjectId));
        
      }
      List<RevCommit> commitList = new ArrayList<>();
      for (RevCommit revCommit : revWalk) {
        commitList.add(revCommit);
        num++;
        if (num >= 21) {
          break;
        }
      }
      
      if (commitList.size() >= 2) {
        for (int j = 0; j < commitList.size() - 1; j++) {
          CodeUpdateInfoDTO updateInfoDTO = new CodeUpdateInfoDTO();
          RevCommit revCommit = commitList.get(j);
          updateInfoDTO.setAuthor(revCommit.getAuthorIdent().getName());
          updateInfoDTO.setOpDate(new Date(revCommit.getCommitTime() * 1000L));
          updateInfoDTO.setCommitLog(revCommit.getFullMessage());
          updateInfoDTO.setReversion(revCommit.getId().getName());
          List<String> changeContentList = new ArrayList<>();
          AbstractTreeIterator newTree = prepareTreeParser(commitList.get(j), repository);
          AbstractTreeIterator oldTree = prepareTreeParser(commitList.get(j + 1), repository);
          List<DiffEntry> diff = git.diff().setOldTree(oldTree)
              .setNewTree(newTree).setShowNameAndStatusOnly(true).call();
          //每一个diffEntry都是第个文件版本之间的变动差异
          StringBuffer sbf = new StringBuffer();
          for (DiffEntry diffEntry : diff) {
            //打印文件差异具体内容
            String oldPath = diffEntry.getOldPath();
            String newPath = diffEntry.getNewPath();
            String nullPath = "/dev/null";
            if (nullPath.equals(diffEntry.getOldPath()) && !nullPath.equals(diffEntry.getNewPath())) {
              sbf.append(diffEntry.getChangeType()).append(" ").append(newPath);
            } else if (!nullPath.equals(diffEntry.getOldPath()) && nullPath.equals(diffEntry.getNewPath())) {
              sbf.append(diffEntry.getChangeType()).append(" ").append(oldPath);
            } else {
              sbf.append(diffEntry.getChangeType()).append(" ").append(oldPath).append("---").append(newPath);
            }
            changeContentList.add(sbf.toString());
            sbf.replace(0, sbf.length(), "");
          }
          updateInfoDTO.setChangeContent(changeContentList);
          gitLogInfoDTOS.add(updateInfoDTO);
        }
      }
    }
    return gitLogInfoDTOS;
  }
  
  public static void cloneOrPull(String repositoryURL, String path, String username, String password, String gitBranch) {
    Git git = null;
    CredentialsProvider provider = new UsernamePasswordCredentialsProvider(username, password);
    try {
      git = Git.open(new File(path));
      //不用设置分支去做git pull 默认使用当前分支去做git pull
      git.pull().setCredentialsProvider(provider).setTimeout(10).call();
      
    } catch (Exception e) {
      // 目录打开失败，不是Git代码库
      
      try {
        //先删除
        if (git != null) {
          git.close();
        }
        
        Boolean isDelete = FoldUtils.deleteFoldersByPath(path);
        //FileUtil.del(path);
        File file = new File(path);
        if (!file.exists()) {
          file.mkdir();
        }
        git = Git.cloneRepository()
            .setURI(repositoryURL)
            .setCredentialsProvider(provider)
            .setDirectory(file).setBranch(gitBranch)
            .call();
        //  System.out.println("Repository cloned successfully.");
      } catch (GitAPIException e1) {
        e1.printStackTrace();
      } finally {
        if (git != null) {
          git.close();
        }
      }
    }
    
  }
  
  /**
   * git pull 下拉代码
   *
   * @param path
   * @param username
   * @param password
   * @throws IOException
   * @throws GitAPIException
   */
  public static void gitPull(String path, String username, String password) throws IOException, GitAPIException {
    log.info("update reversion path:{},username:{},password:{}", path, username, password);
    if (StringUtils.isNotBlank(path)) {
      File file = new File(path);
      if (file.exists()) {
        Git git = Git.open(new File(path));
        PullResult result = null;
        if (StringUtils.isNotBlank(username) && StringUtils.isNotBlank(password)) {
          CredentialsProvider provider = new UsernamePasswordCredentialsProvider(username, password);
          //不用设置分支去做git pull 默认使用当前分支去做git pull
          result = git.pull().setCredentialsProvider(provider).setTimeout(10).call();
        } else {
          result = git.pull().call();
        }
        
        boolean successful = result.isSuccessful();
        log.info("git pull is {}", successful);
      }
    }
  }
  
  /**
   * gitReset 用于重新设置版本号即可退回原来的版本来完成操作
   *
   * @param path
   * @param reversion
   * @throws IOException
   * @throws GitAPIException
   */
  public static void gitReset(String path, String reversion) throws IOException, GitAPIException {
    if (StringUtils.isNotBlank(path)) {
      File file = new File(path);
      Git git = Git.open(file);
      git.reset().setMode(ResetCommand.ResetType.HARD).setRef(reversion).call();
    }
  }
  
  /**
   * 获取远程分支列表
   *
   * @param path
   * @param username
   * @param password
   * @return
   * @throws GitAPIException
   * @throws IOException
   */
  public static List<String> branchRemoteList(String path, String username, String password)
      throws GitAPIException, IOException {
    List<String> remoteBranchList = new ArrayList<>();
    File file = new File(path);
    Git git = Git.open(file);
    CredentialsProvider provider = new UsernamePasswordCredentialsProvider(username, password);
    FetchResult fetchResult = git.fetch().setCredentialsProvider(provider).call();
    List<Ref> call1 = git.branchList().setListMode(ListBranchCommand.ListMode.ALL).call();
    for (Ref ref : call1) {
      String name = ref.getName();
      if (StringUtils.isNotBlank(name) && name.contains("remotes")) {
        String replace = name.replace("refs/remotes/origin/", "");
        remoteBranchList.add(replace);
      }
    }
    return remoteBranchList;
  }
  
  /**
   * 判断本地分支是否存在
   *
   * @param git
   * @param branchName
   * @return
   * @throws GitAPIException
   */
  public static boolean branchNameExist(Git git, String branchName) throws GitAPIException {
    List<Ref> refs = git.branchList().call();
    for (Ref ref : refs) {
      if (ref.getName().contains(branchName)) {
        return true;
      }
    }
    return false;
  }
  
  /**
   * 切换分支,并拉取到最新的代码
   *
   * @param path
   * @param branchName
   */
  public static boolean gitCheckoutBranchAndPull(String path, String username,
                                                 String password, String branchName)
      throws IOException, GitAPIException {
    if (StringUtils.isNotBlank(path)) {
      File file = new File(path);
      if (file.exists()) {
        Git git = Git.open(file);
        if (branchNameExist(git, branchName)) {
          Ref ref = git.checkout().setCreateBranch(false).setName(branchName).call();
        } else {
          CredentialsProvider provider = new UsernamePasswordCredentialsProvider(username, password);
          //不用设置分支去做git pull 默认使用当前分支去做git pull
          git.fetch().setCredentialsProvider(provider).call();
          Ref ref = git.checkout().setCreateBranch(true)
              .setName(branchName).setStartPoint("origin/" + branchName).call();
        }
        log.info("git 切换分支成功");
        PullResult result = null;
        if (StringUtils.isNotBlank(username) && StringUtils.isNotBlank(password)) {
          CredentialsProvider provider = new UsernamePasswordCredentialsProvider(username, password);
          //不用设置分支去做git pull 默认使用当前分支去做git pull
          result = git.pull().setCredentialsProvider(provider).setTimeout(10).call();
        } else {
          result = git.pull().call();
        }
        log.info("git pull is {}", result.isSuccessful());
        return result.isSuccessful();
      }
    }
    return false;
  }
  
  private static AbstractTreeIterator prepareTreeParser(RevCommit commit, Repository repository) {
    try (RevWalk walk = new RevWalk(repository)) {
      RevTree tree = walk.parseTree(commit.getTree().getId());
      CanonicalTreeParser oldTreeParser = new CanonicalTreeParser();
      try (ObjectReader oldReader = repository.newObjectReader()) {
        oldTreeParser.reset(oldReader, tree.getId());
      }
      walk.dispose();
      return oldTreeParser;
    } catch (Exception e) {
      // TODO: handle exception
      e.printStackTrace();
    }
    return null;
  }

//    public static void main(String[] args) throws GitAPIException, IOException {
//        String path = "F:\\storge\\folder1";
//        List<CodeUpdateInfoDTO> infoDTOS = gitLog(path);
//        System.out.println(JSONObject.toJSONString(infoDTOS));
//        File file = new File(path);
//        Git git = Git.open(file);
//        Iterable<RevCommit> commitIterable = git.log().all().call();
//        Iterator<RevCommit> iterator = commitIterable.iterator();
//        System.out.println(System.currentTimeMillis());
//        while (iterator.hasNext()) {
//            RevCommit revCommit = iterator.next();
//
//            int commitTime = iterator.next().getCommitTime();
//            long l = commitTime * 1000L;
//            System.out.println(l);
//            String s = new Date(l).toLocaleString();
//            System.out.println(s);
//        }
//        gitPull(path, "jellf", "jellf");
//        System.out.println(getUrlReversion("http://134.108.3.224/git/test.git", "jellf", "jellf"));
//    }

}