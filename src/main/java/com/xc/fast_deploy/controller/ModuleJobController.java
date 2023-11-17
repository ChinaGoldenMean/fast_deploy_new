package com.xc.fast_deploy.controller;

import com.alibaba.fastjson.JSONObject;
import com.offbytwo.jenkins.JenkinsServer;
import com.offbytwo.jenkins.model.Job;
import com.xc.fast_deploy.dao.master_dao.ModuleBuildInfoMapper;
import com.xc.fast_deploy.dto.MyPageInfo;
import com.xc.fast_deploy.dto.ResponseDTO;
import com.xc.fast_deploy.dto.jenkins.JobDetailsDTO;
import com.xc.fast_deploy.dto.module.ModuleJobDTO;
import com.xc.fast_deploy.dto.module.ModuleMirrorDTO;
import com.xc.fast_deploy.model.master_model.ModuleBuildInfo;
import com.xc.fast_deploy.model.master_model.ModuleJob;
import com.xc.fast_deploy.model.master_model.ModuleManage;
import com.xc.fast_deploy.model.master_model.ModuleMirror;
import com.xc.fast_deploy.myException.FileStoreException;
import com.xc.fast_deploy.myException.ModuleJobSaveException;
import com.xc.fast_deploy.myException.UnauthorizedException;
import com.xc.fast_deploy.myenum.ComplieTypeEnum;
import com.xc.fast_deploy.myenum.DockerfileTypeEnum;
import com.xc.fast_deploy.myenum.ModuleTypeEnum;
import com.xc.fast_deploy.service.common.ModuleJobService;
import com.xc.fast_deploy.service.common.ModuleManageService;
import com.xc.fast_deploy.service.common.ModuleMirrorService;
import com.xc.fast_deploy.service.common.ModuleUserService;
import com.xc.fast_deploy.shiro.token.JwtUtil;
import com.xc.fast_deploy.utils.PermissionJudgeUtils;
import com.xc.fast_deploy.utils.jenkins.JenkinsManage;
import com.xc.fast_deploy.vo.RunJobDataVo;
import com.xc.fast_deploy.vo.module_vo.ModuleJobVo;
import com.xc.fast_deploy.vo.module_vo.param.ModuleJobParamVo;
import com.xc.fast_deploy.vo.module_vo.param.ModuleJobSelectParamVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.*;

@RequestMapping(value = "/moduleJob")
@RestController
@Slf4j
public class ModuleJobController {
  
  @Autowired
  private ModuleManageService manageService;
  @Autowired
  private ModuleJobService jobService;
  @Autowired
  private ModuleUserService userService;
  @Autowired
  private ModuleMirrorService mirrorService;
  
  @Autowired
  ModuleBuildInfoMapper infoMapper;
  
  /**
   * 添加一个模块镜像制作任务 formdata数据传输
   *
   * @param paramVo
   * @param dockerfile
   * @param compilerFile
   * @return
   */
  @PostMapping(value = "/insertJob")
  public String createModuleJob(ModuleJobParamVo paramVo, MultipartFile dockerfile, MultipartFile compilerFile) {
    log.info("创建job入参: " + JSONObject.toJSONString(paramVo));
    ResponseDTO responseDTO = new ResponseDTO();
    //基础字段和逻辑校验
    ModuleManage moduleManage = validate(paramVo, dockerfile, compilerFile);
    responseDTO.fail("创建job失败");
    if (moduleManage != null) {
      //校验通过的情况下查询权限信息,如果没有权限直接抛出无权限错误
      PermissionJudgeUtils.judgeUserPermission(userService, "image_make_create_task",
          JwtUtil.getUserIdFromToken((String) SecurityUtils.getSubject().getPrincipal()), paramVo.getEnvId());
      try {
        if (jobService.saveJob(paramVo, moduleManage, dockerfile, compilerFile)) {
          responseDTO.success("创建成功");
        }
      } catch (ModuleJobSaveException e) {
        responseDTO.fail(e.getMessage());
      } catch (FileStoreException e) {
        log.info("创建job任务,保存相关文件失败");
        responseDTO.fail(e.getMessage());
      }
    } else {
      responseDTO.fail("输入参数无法通过校验逻辑!");
    }
    return JSONObject.toJSONString(responseDTO);
  }
  
  /**
   * 校验参数和上传文件的正确性
   *
   * @param paramVo      相关参数
   * @param dockerfile   docker文件
   * @param compilerFile 编译文件
   * @return 该模块id对应的模块类型
   */
  private ModuleManage validate(ModuleJobParamVo paramVo, MultipartFile dockerfile,
                                MultipartFile compilerFile) {
    ModuleManage moduleManage = manageService.selectById(paramVo.getModuleId());
    if (moduleManage != null && paramVo.getCenterId() != null
        && paramVo.getEnvId() != null
        && paramVo.getModuleId() != null
        && paramVo.getCompileType() != null
        && paramVo.getDockerfileType() != null) {
      Integer moduleType = moduleManage.getModuleType();
      //模块类型
      ModuleTypeEnum moduleTypeEnum = ModuleTypeEnum.getTypeByCode(moduleType);
      //模块编译类型
      ComplieTypeEnum complieTypeEnum = ComplieTypeEnum.getEnumByType(paramVo.getCompileType());
      //dockerfile指定类型
      Integer dockerfileType = paramVo.getDockerfileType();
      
      if (moduleTypeEnum != null && paramVo.getCenterId().equals(moduleManage.getCenterId())) {
        switch (moduleTypeEnum) {
          case SVN_SOURCE_CODE:
            if (complieTypeEnum != null) {
              switch (complieTypeEnum) {
                case COMMAND_COMPILIE:
                  if (StringUtils.isBlank(paramVo.getCompileCommand())) {
                    return null;
                  }
                  break;
                case FILE_COMPILIE:
                  if (compilerFile == null || compilerFile.isEmpty()) {
                    return null;
                  }
                  break;
                default:
                  break;
              }
            }
            break;
          default:
            break;
        }
        if (dockerfileType.equals(DockerfileTypeEnum.FILE_APPOINT.getCode())) {
          if (StringUtils.isBlank(paramVo.getDockerfilePath())) {
            return null;
          }
        } else if (dockerfileType.equals(DockerfileTypeEnum.FILE_UPLOAD.getCode())) {
          if (dockerfile == null || dockerfile.isEmpty()) {
            return null;
          }
        }
        return moduleManage;
      }
    }
    return null;
  }
  
  /**
   * 修改某个job的信息
   * 只能修改的是dockerfile 重新指定  complileCommand
   *
   * @param paramVo
   * @param dockerfile
   * @return
   */
  @PostMapping(value = "/updateJobInfo")
  public String updateModuleJob(ModuleJobParamVo paramVo, MultipartFile dockerfile, MultipartFile complieFile) {
    //修改的内容要做相关限制,
    log.info("更改job入参: " + JSONObject.toJSONString(paramVo));
    ResponseDTO responseDTO = new ResponseDTO();
    responseDTO.fail("修改失败");
    if (paramVo != null && paramVo.getJobId() != null) {
      PermissionJudgeUtils.judgeUserPermission(userService, "image_make_update",
          JwtUtil.getUserIdFromToken((String) SecurityUtils.getSubject().getPrincipal()), paramVo.getEnvId());
      ComplieTypeEnum complieTypeEnum = ComplieTypeEnum.getEnumByType(paramVo.getCompileType());
      DockerfileTypeEnum dockerfileTypeEnum = DockerfileTypeEnum.getEnumByType(paramVo.getDockerfileType());
      boolean flag = false;
      if (complieTypeEnum != null) {
        switch (complieTypeEnum) {
          case COMMAND_COMPILIE:
            if (StringUtils.isBlank(paramVo.getCompileCommand())) {
              flag = true;
            }
            break;
          case FILE_COMPILIE:
            if (complieFile == null || complieFile.isEmpty()) {
              flag = true;
            }
            break;
          default:
            break;
        }
      }
      if (!flag && dockerfileTypeEnum != null) {
        switch (dockerfileTypeEnum) {
          case FILE_UPLOAD:
            if (complieFile == null || complieFile.isEmpty()) {
              flag = true;
            }
            break;
          case FILE_APPOINT:
            if (StringUtils.isBlank(paramVo.getDockerfilePath())) {
              flag = true;
            }
            break;
          default:
            break;
        }
      }
      if (!flag && jobService.updateJobInfo(paramVo, dockerfile, complieFile)) {
        responseDTO.success("更新成功");
      }
    }
    return JSONObject.toJSONString(responseDTO);
  }
  
  @PostMapping(value = "listBuildInfo")
  public String listBuildInfo(Integer moduleId, Integer envId) {
    ResponseDTO responseDTO = new ResponseDTO();
    List<ModuleBuildInfo> list = new ArrayList<>();
    responseDTO.fail("查询失败");
    if (moduleId != null && envId != null) {
      list = infoMapper.listBuildInfo(moduleId, envId);
    }
    return JSONObject.toJSONString(list);
  }
  
  /**
   * 删除job内容
   *
   * @param jobId
   * @return
   */
  @PostMapping(value = "/deleteJob")
  public String deleteJobById(Integer jobId) {
    log.info("删除job入参: jobId: " + jobId);
    ResponseDTO responseDTO = new ResponseDTO();
    responseDTO.fail("删除失败");
    if (jobId != null) {
      ModuleJob moduleJob = jobService.selectById(jobId);
      if (moduleJob != null) {
        PermissionJudgeUtils.judgeUserPermission(userService, "image_make_delete",
            JwtUtil.getUserIdFromToken((String) SecurityUtils.getSubject().getPrincipal()), moduleJob.getModuleEnvId());
        if (jobService.deleteJobById(jobId)) {
          responseDTO.success("删除成功");
        }
      }
    }
    return JSONObject.toJSONString(responseDTO);
  }
  
  /**
   * 获取某个job对应的信息
   *
   * @param jobId
   * @return
   */
  @GetMapping(value = "/getOneJob")
  public String getOneJob(Integer jobId) {
    log.info("getOneJob 入参: jobId: " + jobId);
    ResponseDTO responseDTO = new ResponseDTO();
    responseDTO.fail("未查询到相关数据");
    if (jobId != null) {
      ModuleJob moduleJob = jobService.selectById(jobId);
      if (moduleJob != null) {
        PermissionJudgeUtils.judgeUserPermission(userService, null,
            JwtUtil.getUserIdFromToken((String) SecurityUtils.getSubject().getPrincipal()), moduleJob.getModuleEnvId());
        ModuleJobDTO jobDTO = jobService.getOneJobById(jobId);
        responseDTO.success(jobDTO);
      }
    }
    return JSONObject.toJSONString(responseDTO);
  }
  
  /**
   * 分页查询所有的job信息
   *
   * @param pageNum
   * @param pageSize
   * @param selectParamVo 查询参数
   * @return
   */
  @GetMapping(value = "/pageJobAll")
  public String getAllPageJobInfo(
      @RequestParam(value = "pageNum", defaultValue = "1", required = false) int pageNum,
      @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize,
      ModuleJobSelectParamVo selectParamVo) {
    // log.info("pageJobAll 入参: " + JSONObject.toJSONString(selectParamVo));
    ResponseDTO responseDTO = new ResponseDTO();
    responseDTO.fail("未查询到任何数据");
    Map<Integer, Set<String>> envPermissionMap = userService.selectEnvPermissionByUserId(JwtUtil.getUserIdFromToken(
        (String) SecurityUtils.getSubject().getPrincipal()));
    boolean flag = false;
    if (envPermissionMap.size() > 0) {
      if (selectParamVo.getEnvId() != null) {
        if (envPermissionMap.containsKey(selectParamVo.getEnvId())) {
          flag = true;
        }
      } else {
        selectParamVo.setEnvIds(envPermissionMap.keySet());
        flag = true;
      }
    }
    if (!flag) {
      throw new UnauthorizedException();
    }
    MyPageInfo<ModuleJobVo> jobVoMyPageInfo = jobService.selectPageAllInfo(pageNum, pageSize, selectParamVo);
    if (jobVoMyPageInfo.getList() != null && jobVoMyPageInfo.getList().size() > 0) {
      responseDTO.success(jobVoMyPageInfo);
    }
    return JSONObject.toJSONString(responseDTO);
  }
  
  /**
   * 开始构建一个job,todo 不能直接调用 用做测试
   *
   * @param jobId job的id
   * @return
   */
  @PostMapping(value = "/runJob")
  public String runJob(Integer jobId) throws Exception {
    ResponseDTO responseDTO = new ResponseDTO();
    responseDTO.fail("job start fail");
    String token = (String) SecurityUtils.getSubject().getPrincipal();
    RunJobDataVo jobDataVo = new RunJobDataVo();
    jobDataVo.setJobId(jobId);
    jobDataVo.setIsNeedUpCode(false);
    jobDataVo.setIsNeedUpCode(true);
    jobDataVo.setIsPromptly(false);
    jobDataVo.setIsOffline(true);
    if (jobId != null && jobService.runJob(null, token, jobDataVo)) {
      responseDTO.success("job start success");
    }
    return JSONObject.toJSONString(responseDTO);
  }
  
  /**
   * 以下3个接口也是测试用的 , 用做直接获取jenkins中的数据所用
   */
  @Resource
  private JenkinsManage jenkinsManage;
  
  @GetMapping(value = "/createJobByXml")
  public String createJobByXml(String jobName, String xml) {
    log.info("createJobByXml 入参: jobName" + jobName + " xml: " + xml);
    JenkinsServer jenkinsServer = jenkinsManage.getJenkinsServer();
    if (jenkinsServer != null && StringUtils.isNotBlank(jobName) && StringUtils.isNotBlank(xml)) {
      try {
        jenkinsServer.createJob(jobName, xml);
        return "OK";
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    return "error";
  }
  
  @GetMapping(value = "/getJobXML")
  public String getJobByJobName(String jobName) {
    log.info("createJobByXml 入参: jobName :{}", jobName);
    JenkinsServer jenkinsServer = jenkinsManage.getJenkinsServer();
    String jobXml = null;
    try {
      jobXml = jenkinsServer.getJobXml(jobName);
      System.out.println(jobXml);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return jobXml;
  }
  
  @GetMapping(value = "/getJenkinsJobs")
  public String getJenkinsJob() {
    log.info("getJenkinsJob ");
    ResponseDTO responseDTO = new ResponseDTO();
    JenkinsServer jenkinsServer = jenkinsManage.getJenkinsServer();
    try {
      Map<String, Job> jobs = jenkinsServer.getJobs();
      responseDTO.success(jobs);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return JSONObject.toJSONString(responseDTO);
  }
  
  /**
   * 获取job详情信息展示
   *
   * @param jobId
   * @return
   */
  @GetMapping(value = "/getJobDetails")
  public String getJobDetails(Integer jobId) {
    log.info("getJobDetails 入参: " + jobId);
    ResponseDTO responseDTO = new ResponseDTO();
    responseDTO.fail("无法获取数据");
    if (jobId != null) {
      ModuleJob moduleJob = jobService.selectById(jobId);
      if (moduleJob != null) {
        PermissionJudgeUtils.judgeUserPermission(userService, null,
            JwtUtil.getUserIdFromToken((String) SecurityUtils.getSubject().getPrincipal()), moduleJob.getModuleEnvId());
        JobDetailsDTO jobDetailsDTO = jobService.getJobDetails(jobId, moduleJob);
        if (jobDetailsDTO != null && StringUtils.isNotBlank(jobDetailsDTO.getJobName())) {
          responseDTO.success(jobDetailsDTO);
        }
      }
    }
    return JSONObject.toJSONString(responseDTO);
  }
  
  /**
   * 获取job日志展示
   *
   * @param jobId
   * @param buildNumber
   * @return
   */
  @GetMapping(value = "/getJobConsoleOutput")
  public String getJobConsoleOutput(Integer jobId, Integer buildNumber) {
    log.info("getJobDetails 入参: jobId" + jobId + " buildNumber: " + buildNumber);
    ResponseDTO responseDTO = new ResponseDTO();
    responseDTO.fail("无法获取数据");
    if (jobId != null && buildNumber != null) {
      ModuleJob moduleJob = jobService.selectById(jobId);
      if (moduleJob != null) {
        PermissionJudgeUtils.judgeUserPermission(userService, "image_make_run_job_log",
            JwtUtil.getUserIdFromToken((String) SecurityUtils.getSubject().getPrincipal()), moduleJob.getModuleEnvId());
        StringBuilder sb = jobService.getConsoleOutput(jobId, buildNumber, moduleJob);
        if (StringUtils.isNotBlank(sb.toString())) {
          responseDTO.success(sb.toString());
        }
      }
    }
    return JSONObject.toJSONString(responseDTO);
  }
  
  /**
   * 根据job获取镜像信息,如果是环境用户则只能查看
   *
   * @param jobId
   * @return
   */
  @GetMapping(value = "/getMirrorByJobId")
  public String getAvailableMirrorByJobId(Integer jobId) {
    log.info("getMirrorByJobId 入参: jobId" + jobId);
    ResponseDTO responseDTO = new ResponseDTO();
    responseDTO.fail("无法获取数据");
    if (jobId != null) {
      ModuleJob moduleJob = jobService.selectById(jobId);
      if (moduleJob != null) {
        PermissionJudgeUtils.judgeUserPermission(userService, null,
            JwtUtil.getUserIdFromToken((String) SecurityUtils.getSubject().getPrincipal()),
            moduleJob.getModuleEnvId());
        List<ModuleMirror> mirrorList = jobService.getAvailableMirrorByJobId(jobId);
        if (mirrorList != null && mirrorList.size() > 0) {
          responseDTO.success(mirrorList);
        }
      }
    }
    return JSONObject.toJSONString(responseDTO);
  }
  
  /**
   * 持续调用接口返回当前构建状态
   *
   * @param size
   * @return
   */
  @GetMapping(value = "/pollingJob")
  public String getMirrorJobInfo(@RequestParam(defaultValue = "10") Integer size,
                                 @RequestParam(defaultValue = "2") Integer isUsed) {
    ////log.info("传入参数:size {}", size);
    ResponseDTO responseDTO = new ResponseDTO();
    responseDTO.fail("暂无消息");
    String userIdFromToken = JwtUtil.getUserIdFromToken(
        (String) SecurityUtils.getSubject().getPrincipal());
    Map<Integer, Set<String>> envPermissionMap = userService.selectEnvPermissionByUserId(userIdFromToken);
    Set<Integer> envIdSet = new HashSet<>();
    if (envPermissionMap.size() > 0) {
      Set<Integer> keySet = envPermissionMap.keySet();
      for (Integer envId : keySet) {
        if (envPermissionMap.get(envId).contains("image_make_records")) envIdSet.add(envId);
      }
    }
    //对应的生产和测试的环境id已经在获取权限的时候已经分开过了 只需要判断是否有权限获取消息就行了
    if (envIdSet.size() > 0) {
      List<ModuleMirrorDTO> mirrorDTOS =
          mirrorService.getPollingMirrorJobInfoByEnvId(envIdSet, size, isUsed, userIdFromToken);
      List<ModuleMirrorDTO> returnMirrorDTOS = new ArrayList<>();
      //未发布的情况之下需要过滤掉失败不可用的信息
      if (isUsed.equals(0)) {
        for (ModuleMirrorDTO mirrorDTO : mirrorDTOS) {
          Integer isAvailable = mirrorDTO.getIsAvailable();
          if (isAvailable != 0) {
            returnMirrorDTOS.add(mirrorDTO);
          }
        }
        responseDTO.success(returnMirrorDTOS);
        return JSONObject.toJSONString(responseDTO);
      }
      
      responseDTO.success(mirrorDTOS);
    }
    return JSONObject.toJSONString(responseDTO);
  }
  
  /**
   * 终止正在打包的job
   *
   * @param moduleId
   * @param envId
   * @return
   */
  @PostMapping(value = "/stopJob")
  public String stopJob(Integer moduleId, Integer envId) {
    ////log.info("传入参数moduleId,envId:",moduleId,envId);
    ResponseDTO responseDTO = new ResponseDTO();
    responseDTO.fail("操作失败");
    if (moduleId != null && envId != null) {
      responseDTO = jobService.stopJob(moduleId, envId);
    } else {
      responseDTO.fail("参数错误");
    }
    return JSONObject.toJSONString(responseDTO);
  }
}