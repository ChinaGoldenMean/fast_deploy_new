package com.xc.fast_deploy.utils.jenkins;

import com.offbytwo.jenkins.JenkinsServer;
import com.offbytwo.jenkins.client.JenkinsHttpClient;
import com.offbytwo.jenkins.model.*;
import com.xc.fast_deploy.dto.jenkins.BuildDetailsDTO;
import com.xc.fast_deploy.dto.jenkins.JenkinsJobDTO;
import com.xc.fast_deploy.listener.JenkinsConsleListener;
import com.xc.fast_deploy.model.master_model.ModuleJob;
import com.xc.fast_deploy.myException.ModuleJobSaveException;
import com.xc.fast_deploy.myenum.ComplieTypeEnum;
import com.xc.fast_deploy.myenum.ModuleTypeEnum;
import com.xc.fast_deploy.utils.code_utils.XMLUtils;
import com.xc.fast_deploy.utils.constant.JenkinsJobModel;
import com.xc.fast_deploy.utils.constant.JenkinsOperate;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.yeauty.pojo.Session;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
public class JenkinsManage {
  
  @Value("${jenkins.url}")
  private String jenkinsUrl;
  
  @Value("${jenkins.username}")
  private String username;
  
  @Value("${jenkins.password}")
  private String password;
  
  private static final Integer CONNECT_TIMEOUT = 2000;
  private static final Integer SOCKET_TIMEOUT = 60000;
  
  private JenkinsServer jenkinsServer;
  
  private JenkinsHttpClient jenkinsHttpClient;

//    private static final PoolingHttpClientConnectionManager connectionManager;
//    private static final HttpClientBuilder clientBuilder;

//    static {
//        connectionManager = new PoolingHttpClientConnectionManager();
//        connectionManager.setMaxTotal(200);
//        RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(CONNECT_TIMEOUT)
//                .setSocketTimeout(SOCKET_TIMEOUT).build();
//        clientBuilder = HttpClients.custom().setConnectionManager(connectionManager).
//                setConnectionManagerShared(true).setDefaultRequestConfig(requestConfig);
//    }
  
  public void init() {
    try {
      this.jenkinsHttpClient = new JenkinsHttpClient(new URI(jenkinsUrl), username, password);
      this.jenkinsServer = new JenkinsServer(jenkinsHttpClient);
    } catch (URISyntaxException e) {
      this.jenkinsHttpClient = null;
      this.jenkinsServer = null;
      log.error("URI转换异常");
      e.printStackTrace();
    }
  }
  
  public JenkinsServer getJenkinsServer() {
    init();
    return jenkinsServer;
  }
  
  public JenkinsHttpClient getJenkinsHttpClient() {
    init();
    return jenkinsHttpClient;
  }
  
  /**
   * 根据jobName 获取对应的jobdto类包含job的一些信息
   *
   * @param jobName
   * @return
   */
  public JenkinsJobDTO selectByName(String jobName) {
    JenkinsServer jenkinsServer = getJenkinsServer();
    if (jenkinsServer != null && StringUtils.isNotBlank(jobName)) {
      try {
        JobWithDetails jobWithDetails = jenkinsServer.getJob(jobName);
        if (jobWithDetails != null) {
          JenkinsJobDTO jobDTO = new JenkinsJobDTO();
          BeanUtils.copyProperties(jobWithDetails, jobDTO);
          jobDTO.setJobName(jobName);
          return jobDTO;
        }
      } catch (IOException e) {
        log.error("获取job失败: jobName:" + jobName);
        e.printStackTrace();
      }
    }
    return null;
  }
  
  /**
   * 获取所有的job的信息
   *
   * @return
   */
  public List<JenkinsJobDTO> selectAll() throws IOException {
    List<JenkinsJobDTO> jobDTOList = new ArrayList<>();
    JenkinsServer jenkinsServer = getJenkinsServer();
    if (jenkinsServer != null) {
      Map<String, Job> jobs = jenkinsServer.getJobs();
      if (jobs != null && jobs.size() > 0) {
        for (String jobName : jobs.keySet()) {
          Job job = jobs.get(jobName);
          JenkinsJobDTO jobDTO = new JenkinsJobDTO();
          jobDTO.setJobName(job.getName());
          jobDTO.setJobUrl(job.getUrl());
          JobWithDetails details = job.details();
          jobDTO.setInQueue(details.isInQueue());
          jobDTO.setDisplayName(details.getDisplayName());
          jobDTO.setDescription(details.getDescription());
          jobDTOList.add(jobDTO);
        }
      }
    }
    return jobDTOList;
  }
  
  /**
   * 创建一个job 根据模板job来去创建一个job
   *
   * @return
   */
  
  public boolean createJob(String jobName, String jobModelName, String crontabExpression,
                           String envViewCode) throws IOException, DocumentException {
    log.info("createJob: jobName:{},jobModelName:{} envViewCode:{}", jobName, jobModelName, envViewCode);
    init();
    if (jenkinsServer != null && StringUtils.isNotBlank(jobName) && StringUtils.isNotBlank(jobModelName)) {
      //jobName不能重复
      String modelJob = jenkinsServer.getJobXml(jobModelName);
      if (StringUtils.isNotBlank(modelJob)) {
        // String modelJob = modelJob.replace("k8s-jenkins-jnlp", "k8s-jenkins-jnlp-" +
        //         UUID.randomUUID().toString().split("-")[1]);
        //添加crontab的配置设置 该任务的定时效果
        if (StringUtils.isNotBlank(crontabExpression)) {
          Document document = XMLUtils.String2Document(modelJob);
          Element rootElement = document.getRootElement();
          Element element = rootElement
              .element("properties")
              .element("org.jenkinsci.plugins.workflow.job.properties.PipelineTriggersJobProperty")
              .element("triggers")
              .element("hudson.triggers.TimerTrigger")
              .element("spec");
          element.setText(crontabExpression);
          modelJob = XMLUtils.Document2String(document);
        }
        //根据模板job创建job
        //然后先创建view
        View view = null;
        try {
          view = this.jenkinsServer.getView(envViewCode);
        } catch (IOException e) {
          log.info("没查询view: " + envViewCode);
        }
        if (view == null) {
          //表示view不存在
          String viewXml = XMLUtils.generateViewXml(JenkinsJobModel.VIEW_MODEL_TEMP,
              jenkinsUrl);
          if (StringUtils.isNotBlank(viewXml)) {
            this.jenkinsServer.createView(envViewCode, viewXml);
          } else {
            return false;
          }
        }
        this.jenkinsServer.createJob(jobName, modelJob);
        StringBuilder postUrl = new StringBuilder();
        postUrl.append(jenkinsUrl).append("/view/").append(envViewCode).append("/")
            .append(JenkinsOperate.ADD_JOB_TO_VIEW).append("?name=").append(jobName);
        this.jenkinsHttpClient.post(postUrl.toString());
        return true;
      }
    }
    return false;
  }
  
  /**
   * 启动一个job
   *
   * @return
   */
  public Integer runJob(JenkinsServer jenkinsServer, String jobName, Map<String, String> args) throws IOException {
    Integer nextBuildNumber = null;
    if (jenkinsServer != null) {
      JobWithDetails job = jenkinsServer.getJob(jobName);
      nextBuildNumber = job.getNextBuildNumber();
      QueueReference reference = job.build(args);
      String itemUrlPart = reference.getQueueItemUrlPart();
      log.info("job run at queue: " + itemUrlPart);
      if (StringUtils.isNotBlank(itemUrlPart)) {
        return nextBuildNumber;
      }
    }
    return null;
  }
  
  /**
   * 根据jobName 获取该job的所有构建细节(不包括构建日志)
   *
   * @param jobName
   * @return
   */
  public List<BuildDetailsDTO> getBuildDetailsByJobName(String jobName) throws IOException {
    List<BuildDetailsDTO> buildDetailsDTOS = new ArrayList<>();
    JenkinsServer jenkinsServer = getJenkinsServer();
    if (StringUtils.isNotBlank(jobName) && jenkinsServer != null) {
      JobWithDetails jobWithDetails = jenkinsServer.getJob(jobName);
      if (jobWithDetails != null) {
        List<Build> allBuilds = jobWithDetails.getAllBuilds();
        if (allBuilds != null && allBuilds.size() > 0) {
          for (Build build : allBuilds) {
            BuildWithDetails details = build.details();
            if (details != null) {
              BuildDetailsDTO detailsDTO = new BuildDetailsDTO();
              BeanUtils.copyProperties(details, detailsDTO);
              detailsDTO.setResult(details.getResult().toString());
              buildDetailsDTOS.add(detailsDTO);
            }
          }
        }
      }
    }
    return buildDetailsDTOS;
  }
  
  /**
   * 获取实时的jenkins的输出日志
   *
   * @param jobReversion
   * @param jobName
   * @param session
   * @return
   */
  public boolean streamConsole(Integer jobReversion, String jobName, Session session) {
    boolean success = false;
    Integer maxWaitTime = 60 * 1000;
    try {
      JobWithDetails jobWithDetails = getJenkinsServer().getJob(jobName);
      if (jobWithDetails != null) {
        int number = jobWithDetails.getLastBuild().getNumber();
        BuildWithDetails details = null;
        long currentTime = System.currentTimeMillis();
        
        while (number != jobReversion) {
          number = getJenkinsServer().getJob(jobName).getLastBuild().getNumber();
          log.info("number: {}, jobReversion:{}", number, jobReversion);
          Thread.sleep(1000);
          if (number == jobReversion) {
            details = getJenkinsServer().getJob(jobName).getLastBuild().details();
            break;
          }
          session.sendText("just wait ...");
          if (System.currentTimeMillis() - currentTime > maxWaitTime) {
            break;
          }
        }
        if (details != null && session != null) {
          details.streamConsoleOutput(new JenkinsConsleListener(session),
              1, 60 * 1000 * 10);
          success = true;
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    } catch (InterruptedException e) {
      log.error("中断异常");
      e.printStackTrace();
    }
    return success;
  }
  
  /**
   * 根据moduleJob提供的信息创建job
   *
   * @param moduleJob
   * @param moduleTypeEnum
   * @param enumByType
   * @param envCode
   * @return
   */
  public boolean createRealJob(ModuleJob moduleJob, ModuleTypeEnum moduleTypeEnum, ComplieTypeEnum enumByType, String envCode) {
    boolean b = false;
    if (moduleJob.getId() != null && moduleTypeEnum != null) {
      try {
        switch (moduleTypeEnum) {
          case GIT_SOURCE_CODE:
          case SVN_SOURCE_CODE:
            if (enumByType != null) {
              switch (enumByType) {
                case FILE_COMPILIE:
                  b = createJob(moduleJob.getJobName(),
                      JenkinsJobModel.JOB_COMPILE_FILE_DOCKERFILE,
                      moduleJob.getCrontabExpression(), envCode);
                  break;
                case COMMAND_COMPILIE:
                  b = createJob(moduleJob.getJobName(),
                      JenkinsJobModel.JOB_COMPILE_COMMAND_DOCKERFILE,
                      moduleJob.getCrontabExpression(), envCode);
                  break;
                default:
                  break;
              }
            }
            break;
          case SVN_AUTO_UP_CODE:
          case GIT_AUTO_UP_SOURCE_CODE:
            b = createJob(moduleJob.getJobName(),
                JenkinsJobModel.JOB_COMPILE_COMMAND_DOCKERFILE,
                moduleJob.getCrontabExpression(), envCode);
            break;
          case PROJECT_PACKAGE:
            //不需要编译的类型
            b = createJob(moduleJob.getJobName(), JenkinsJobModel.JOB_DOCKERFILE,
                moduleJob.getCrontabExpression(), envCode);
            break;
          default:
            break;
        }
      } catch (IOException e) {
        e.printStackTrace();
        throw new ModuleJobSaveException("jenkins连接创建job失败");
        
      } catch (DocumentException e) {
        e.printStackTrace();
        log.error("生成document文件失败");
        throw new ModuleJobSaveException("生成document文件失败");
      }
      
      if (!b) {
        throw new ModuleJobSaveException("创建job失败");
      } else {
        return true;
      }
    }
    return false;
  }
  
  public boolean isJobRunning(String jobName) throws IOException {
    JobWithDetails withDetails = getJenkinsServer().getJob(jobName);
    Build build = withDetails.getLastBuild();
    if (build != null) {
      if (build.getQueueId() != 0) {
        return true;
      }
      if (build.details().isBuilding()) {
        return true;
      }
    }
    return false;
  }
}
