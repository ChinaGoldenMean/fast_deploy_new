package com.xc.fast_deploy.jenkinsTest;

import com.offbytwo.jenkins.client.JenkinsHttpClient;
import com.offbytwo.jenkins.model.*;
import com.xc.fast_deploy.dao.master_dao.ModuleJobMapper;
import com.xc.fast_deploy.dao.master_dao.ModulePackageMapper;
import com.xc.fast_deploy.dto.module.ModulePackageDTO;
import com.xc.fast_deploy.model.master_model.ModuleJob;
import com.xc.fast_deploy.utils.jenkins.JenkinsManage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.http.client.HttpResponseException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class MyJenkinsTest {
  
  @Autowired
  private JenkinsManage jenkinsManage;
  @Autowired
  private ModulePackageMapper packageMapper;
  @Autowired
  private ModuleJobMapper jobMapper;
  
  public static String username = "zhenglt";
  public static String password = "zhenglt_123";
  private static String desFilePath = "F:\\img\\test";
  
  @Test
  public void getOneTest() {
    List<ModulePackageDTO> packageDTOS = packageMapper.selectPackageInfoByModuleId(251);
    for (int i = 0; i < packageDTOS.size(); i++) {
      ThreadPoolExecutor poolExecutor = new ThreadPoolExecutor(100, 100,
          0L, TimeUnit.SECONDS, new ArrayBlockingQueue<>(100));
      poolExecutor.submit(new MySThread(
          desFilePath + "_" + i, packageDTOS.get(i).getCodeUrl()));
    }
    while (true) {
      try {
        Thread.sleep(1000);
        System.out.println("deng dai");
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }
  
  @Test
  public void testGetConsoleOutput() {
    try {
      System.out.println(jenkinsManage);
      JobWithDetails module_job_svn = jenkinsManage.getJenkinsServer().getJob("module_job_svn");
      BuildWithDetails details = module_job_svn.getLastBuild().details();
//            details.streamConsoleOutput(new JenkinsConsleListener("324", "24"), 1, 5 * 60 * 1000);
    } catch (IOException e) {
      e.printStackTrace();
    }
    
  }
  
  @Test
  public void testCreateJob() {
    try {
//            boolean success = jenkinsManage.createJob("compile_command_dockerfile_job_model", "compile_file_dockerfile_job_model", null);
//            System.out.println(success);
      
      View view = jenkinsManage.getJenkinsServer().getView("test_view");
      
      JenkinsHttpClient jenkinsHttpClient = jenkinsManage.getJenkinsHttpClient();
      System.out.println(view.getUrl());
//            Map<String, String> args = new HashMap();
//            args.put("name", "one");
//            String args = "one";
      String postUrl = view.getUrl() + "addJobToView?name=23534r";
      System.out.println(postUrl);
      jenkinsHttpClient.post(postUrl);
//            System.out.println(view1.getName());

//            jenkinsHttpClient.post()
//            System.out.println(jenkinsHttpClient);
//            Map<String, Job> jobs = jenkinsManage.getJenkinsServer().getJobs();
//            if (jobs != null && jobs.size() > 0) {
//                for (String key : jobs.keySet()) {
//                    Job job = jobs.get(key);
//                    System.out.println(job.getName());
//
//                }
//            }

//            jenkinsManage.getJenkinsServer().createJob("folder1");
    } catch (Exception e) {
      log.error("无法获取到模板job");
    }
//        jenkinsManage.getJenkinsServer().createJob("");
  }
  
  @Test
  public void testRenameJob() throws IOException {
    
    Map<String, Job> jobs = jenkinsManage.getJenkinsServer().getJobs("billing-pt");
    if (jobs != null) {
      Set<String> jobNameSet = jobs.keySet();
      for (String jobname : jobNameSet) {
        System.out.println(jobname);
      }
//            String jobnames = "";
//            String[] splits = jobnames.split("\n");
      
      if (true) {
        boolean flag = false;
        for (String jobName : jobNameSet) {
          System.out.println(jobName);
          String jobXml = jenkinsManage
              .getJenkinsServer()
              .getJobXml(jobName);
          if (jobXml.contains("/data111")) {
//                        String replaceJob = jobXml.replace("    - mountPath: /data111",
//                                "    - mountPath: /data1").replace("      path: /data111",
//                                "      path: /data1").replace("      server: 134.108.27.81",
//                                "      server: 134.108.27.80")
//                                .replace("http://134.108.27.81:8093/mirror/updateMirror",
//                                        "http://134.108.27.80:8093/mirror/updateMirror");
            String replaceJob = jobXml.replace("/data111", "/data1");
            jenkinsManage.getJenkinsServer().updateJob(jobName, replaceJob);
            System.out.println(jobName + ": 更换成功");
          }
        }
        if (!flag) {
          System.out.println("无替换操作");
        }
      }
    }
  }

//            String jobName = "crm-pstbss-customer-custview-controllere4a3";
//            for (String jobName : jobNameSet) {
//                String jobXml = jenkinsManage
//                        .getJenkinsServer()
//                        .getJobXml(jobName);
//

//            }
  
  /**
   * 更改module_job表中的编译命令，对maven编译进行优化
   */
  @Test
  public void changeCompileCommand() {
    List<ModuleJob> jobs = jobMapper.selectAll();
    int[] envids = {22, 24, 25, 26, 27, 30, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 44, 45};
    //int[] envids = {34};
    Set<Integer> envSet = new HashSet<>();
    for (int i = 0; i < envids.length; i++) {
      envSet.add(envids[i]);
    }
    for (ModuleJob moduleJob : jobs) {
      String compileCommand = moduleJob.getCompileCommand();
      Integer moduleEnvId = moduleJob.getModuleEnvId();
      if (StringUtils.isNotBlank(compileCommand) && compileCommand.contains("mvn clean package")
          && envSet.contains(moduleEnvId)) {
        ModuleJob job = new ModuleJob();
        job.setId(moduleJob.getId());
        job.setCompileCommand(compileCommand.replace("clean",
            "-T 4 clean"));
        jobMapper.updateByPrimaryKeySelective(job);
      }
    }
  }
  
  /**
   * 替换command里面的settings文件
   */
  @Test
  public void renameXml() {
    List<ModuleJob> jobs = jobMapper.selectAll();
    
    System.out.println(jobs);
    int[] arr = {33, 36, 37, 38};
    
    Set<Integer> arrSet = new HashSet<>();
    for (int i = 0; i < arr.length; i++) {
      arrSet.add(arr[i]);
    }
    
    for (ModuleJob moduleJob : jobs) {
      String compileCommand = moduleJob.getCompileCommand();
      Integer moduleEnvId = moduleJob.getModuleEnvId();
      if (StringUtils.isNotBlank(compileCommand) &&
          compileCommand.contains("mvn clean install")
          && compileCommand.contains("/data/maven/settings2.xml")
          && arrSet.contains(moduleEnvId)) {
        ModuleJob job = new ModuleJob();
        job.setId(moduleJob.getId());
        job.setCompileCommand(compileCommand.replace("/data/maven/settings2.xml",
            "/data1/maven/settings2.xml"));
        jobMapper.updateByPrimaryKeySelective(job);
      }
    }
  }
  
  @Test
  public void getLabel() throws IOException {
    Map<String, Job> jobs = jenkinsManage.getJenkinsServer().getJobs();
    Set<String> jobNames = jobs.keySet();
    Pattern compile = Pattern.compile(".*&apos;(k8s-.*)?&apos;.*");
    for (String jobName : jobNames) {
      String jobXml = jenkinsManage.getJenkinsServer().getJobXml(jobName);
      Matcher matcher = compile.matcher(jobXml);
      while (matcher.find()) {
        String group2 = matcher.group(1);
        if ("k8s-jenkins-jnlp".equals(group2)) {
          System.out.println(jobName);
          String replaceJob = jobXml.replace("k8s-jenkins-jnlp", "k8s-jenkins-jnlp-" +
              UUID.randomUUID().toString().split("-")[1]);
          jenkinsManage.getJenkinsServer().updateJob(jobName, replaceJob);
        }
      }
    }
    
  }
  
  @Test
  public void stopJob() {
    try {
      Build build = jenkinsManage.getJenkinsServer().getJob("crm_mst_code-boot-demo-a3c1").getLastBuild();
      if (build.details().isBuilding()) {
        String result = build.Stop();
        log.info(result);
      } else {
        log.info("没有正在执行的job");
      }
    } catch (HttpResponseException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
