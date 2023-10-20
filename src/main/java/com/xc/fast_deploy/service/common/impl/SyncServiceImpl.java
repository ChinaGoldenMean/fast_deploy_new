package com.xc.fast_deploy.service.common.impl;

import com.alibaba.fastjson.JSONObject;
import com.xc.fast_deploy.dto.UploadModuleStatusDTO;
import com.xc.fast_deploy.model.master_model.*;
import com.xc.fast_deploy.myException.SvnUrlNotExistException;
import com.xc.fast_deploy.myException.ValidateExcetion;
import com.xc.fast_deploy.myenum.ModuleTypeEnum;
import com.xc.fast_deploy.service.common.ModuleDeployLogService;
import com.xc.fast_deploy.service.common.ModuleManageService;
import com.xc.fast_deploy.service.common.ModuleNeedLogService;
import com.xc.fast_deploy.service.common.SyncService;
import com.xc.fast_deploy.utils.encyption_utils.EncryptUtil;
import com.xc.fast_deploy.utils.code_utils.ExcelPhraseUtils;
import com.xc.fast_deploy.utils.code_utils.GitUtils;
import com.xc.fast_deploy.utils.code_utils.SvnUtils;
import com.xc.fast_deploy.vo.module_vo.param.ModuleManageParamVo;
import com.xc.fast_deploy.vo.module_vo.param.ModulePackageParamVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.yeauty.pojo.Session;

import java.io.*;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class SyncServiceImpl implements SyncService {
  
  @Value("${file.storge.path.prefix}")
  private String storgePrefix;
  
  /**
   * 异步拉取代码的操作
   *
   * @param certificate
   */
  @Override
  @Async
  public void checkoutAllAsync(List<ModulePackage> packageDTOS, ModuleCertificate certificate) {
    log.info("执行异步拉取代码的操作: packageList: {}", packageDTOS);
    checkoutAll(packageDTOS, certificate);
  }
  
  /**
   * 同步下拉代码
   *
   * @param packageDTOS
   * @param certificate
   */
  @Override
  public void checkoutAllSync(List<ModulePackage> packageDTOS, ModuleCertificate certificate) {
    log.info("执行同步拉取代码的操作: packageList: {}", packageDTOS);
    checkoutAll(packageDTOS, certificate);
  }
  
  /**
   * 根据url和包路径名异步下拉代码
   *
   * @param packageList
   * @param certificate
   */
  private void checkoutAll(List<ModulePackage> packageList, ModuleCertificate certificate) {
    if (packageList != null && packageList.size() > 0 && certificate != null) {
      if (packageList.get(0).getCodeType().equals(ModuleTypeEnum.SVN_SOURCE_CODE.getModuleTypeCode())
          || packageList.get(0).getCodeType().equals(ModuleTypeEnum.SVN_AUTO_UP_CODE.getModuleTypeCode())) {
        SVNClientManager clientManager = SvnUtils.getManager(certificate.getUsername(),
            EncryptUtil.decrypt(certificate.getPassword()));
        if (clientManager != null) {
          for (ModulePackage modulePackage : packageList) {
            if (StringUtils.isNotBlank(modulePackage.getCodeUrl())
                && StringUtils.isNotBlank(modulePackage.getContentName())
                && StringUtils.isNotBlank(modulePackage.getPackagePathName())) {
              try {
                clientManager.createRepository(SVNURL
                    .parseURIEncoded(modulePackage.getCodeUrl()), true);
                File file = new File(storgePrefix + modulePackage.getPackagePathName());
                if (!file.exists()) {
                  boolean b = file.mkdirs();
                }
                SvnUtils.checkout(clientManager,
                    SVNURL.parseURIEncoded(modulePackage.getCodeUrl()),
                    SVNRevision.create(SvnUtils.LATEST_REVERSION),
                    file, SVNDepth.INFINITY);
              } catch (SVNException e) {
                e.printStackTrace();
              }
            }
          }
        }
      } else if (packageList.get(0).getCodeType()
          .equals(ModuleTypeEnum.GIT_SOURCE_CODE.getModuleTypeCode()) || packageList.get(0).getCodeType()
          .equals(ModuleTypeEnum.GIT_AUTO_UP_SOURCE_CODE.getModuleTypeCode())) {
        for (ModulePackage modulePackage : packageList) {
          if (StringUtils.isNotBlank(modulePackage.getCodeUrl())
              && StringUtils.isNotBlank(modulePackage.getContentName())
              && StringUtils.isNotBlank(modulePackage.getPackagePathName())) {
            try {
              String gitBranch = modulePackage.getGitBranch();
              if (StringUtils.isBlank(gitBranch)) {
                gitBranch = "master";
              }
              //git 下拉分支代码分支名称必须存在 否则报错了
              GitUtils.gitClone(modulePackage.getCodeUrl(),
                  storgePrefix + modulePackage.getPackagePathName(),
                  certificate.getUsername(),
                  EncryptUtil.decrypt(certificate.getPassword()), gitBranch);
            } catch (GitAPIException e) {
              e.printStackTrace();
            }
          }
        }
      }
      packageList.clear();
    }
  }
  
  @Override
  @Async
  public void saveModuleDeployLog(ModuleDeployLogService moduleDeployLogService, Integer moduleId, Integer envId, Integer deployId,
                                  String active, Map<String, Object> argsMap, boolean success, String userId, String username) {
    ModuleDeployLog deployLog = new ModuleDeployLog();
    deployLog.setDeployId(deployId);
    deployLog.setCreateTime(new Date());
    deployLog.setEnvId(envId);
    deployLog.setModuleId(moduleId);
    deployLog.setOpUserId(userId);
    deployLog.setOpUsername(username);
    deployLog.setOpResult(success ? 1 : 0);
    deployLog.setArgs(JSONObject.toJSONString(argsMap));
    deployLog.setOpActive(active);
    moduleDeployLogService.insertSelective(deployLog);
  }
  
  //超时时间设置为10分钟
  private static final Long TIME_OUT = 10 * 60 * 1000L;
  
  /**
   * 关闭session连接并且停止线程池的使用
   *
   * @param poolExecutor
   * @param session
   * @param size
   */
  @Override
  @Async
  public void shutdownExcutor(ThreadPoolExecutor poolExecutor, Session session, Integer size) {
    long start = System.currentTimeMillis();
    if (poolExecutor == null) {
      return;
    }
    while (true) {
      try {
        Thread.sleep(500);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      //   log.info("completedTaskCount: {},传入参数size:{}", poolExecutor.getCompletedTaskCount(), size);
      if (poolExecutor.getCompletedTaskCount() == size ||
          ((System.currentTimeMillis() - start) > TIME_OUT)) {
        try {
          poolExecutor.shutdown();
          poolExecutor.awaitTermination(5, TimeUnit.SECONDS);
          if (!poolExecutor.isTerminated()) {
            log.info("excutor执行shutdownnow方法");
            poolExecutor.shutdownNow();
          }
          break;
        } catch (InterruptedException e) {
          log.error("error");
        }
      }
    }
  }
  
  @Override
  @Async
  public void addBatchData(ModuleManageService moduleManageService, Session session,
                           String userId, File file, ModuleEnv moduleEnv, ModuleCertificate certificate,
                           String projectCode, Integer moduleTypeCode, String gitBranch) {
    FileInputStream inputStream = null;
    String fileName = file.getName();
    try {
      inputStream = new FileInputStream(file);
      ModuleTypeEnum typeEnum = ModuleTypeEnum.getTypeByCode(moduleTypeCode);
      if (typeEnum != null) {
        switch (typeEnum) {
          case SVN_AUTO_UP_CODE:
            List<ModuleManageParamVo> manageParamVos =
                ExcelPhraseUtils.getModuleManageSvnAutoData(inputStream);
            if (manageParamVos.size() > 0) {
              session.sendText(fileName + " : 文件解析成功");
              if (moduleManageService.saveAllAutoManageData(session, moduleEnv, manageParamVos,
                  userId, certificate, projectCode, fileName)) {
                session.sendText(JSONObject.toJSONString(UploadModuleStatusDTO.
                    success(fileName, "模块数据解析成功")));
                return;
              }
            } else {
              session.sendText(JSONObject.toJSONString(UploadModuleStatusDTO.
                  fail(fileName, "未解析到任何数据")));
            }
            break;
          
          case GIT_SOURCE_CODE:
          case SVN_SOURCE_CODE:
            List<ModuleManageParamVo> moduleManageDataList =
                ExcelPhraseUtils.getModuleManageData(inputStream);
            
            if (moduleManageDataList.size() > 0) {
              session.sendText(fileName + " : 文件上传成功");
              List<ModulePackageParamVo> packageList =
                  moduleManageDataList.get(0).getPackageList();
              if (packageList.size() > 0) {
                ModuleManageParamVo manageParamVo = moduleManageDataList.get(0);
                
                if (StringUtils.isBlank(gitBranch)) {
                  gitBranch = "master";
                }
                manageParamVo.setGitBranch(gitBranch);
                manageParamVo.setModuleTypeCode(moduleTypeCode);
                
                if (moduleManageService.saveAll(session,
                    userId, manageParamVo, moduleEnv,
                    certificate, projectCode, fileName)) {
                  session.sendText(JSONObject.toJSONString(UploadModuleStatusDTO.
                      success(fileName, "模块数据解析成功")));
                  return;
                }
              } else {
                session.sendText(JSONObject.toJSONString(UploadModuleStatusDTO.
                    fail(fileName, "未解析到url数据")));
              }
            } else {
              session.sendText(JSONObject.toJSONString(UploadModuleStatusDTO.
                  fail(fileName, "未解析到任何数据")));
            }
            break;
          
          default:
            break;
        }
      }
    } catch (IOException | ValidateExcetion | SvnUrlNotExistException e) {
      session.sendText(JSONObject.toJSONString(UploadModuleStatusDTO.fail(fileName, e.getMessage())));
      
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      try {
        if (inputStream != null) {
          inputStream.close();
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
  
  @Override
  @Async
  public void copyFile2File(File sourceFile, File destFile) {
    log.info("文件转移开始  sourceFile:{} destFile:{}", sourceFile.getAbsolutePath(), destFile.getAbsolutePath());
    FileOutputStream outputStream = null;
    FileInputStream inputStream = null;
    boolean success = false;
    try {
      if (sourceFile.exists()) {
        outputStream = new FileOutputStream(destFile);
        inputStream = new FileInputStream(sourceFile);
        byte[] bytes = new byte[1024];
        int len;
        while ((len = inputStream.read(bytes)) != -1) {
          outputStream.write(bytes, 0, len);
        }
        outputStream.flush();
        log.info("文件转移成功");
        success = true;
      }
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      if (outputStream != null) {
        try {
          outputStream.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
      if (inputStream != null) {
        try {
          inputStream.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
      //如果 原有的文件复制成功 原有的文件删除
      if (success) sourceFile.delete();
    }
  }
  
  @Override
  @Async
  public void saveModuleNeedLog(ModuleNeedLogService needLogService, Integer needId, String opActive,
                                String args, boolean success, String username) {
    ModuleNeedLog moduleNeedLog = new ModuleNeedLog();
    moduleNeedLog.setNeedId(needId);
    moduleNeedLog.setOpActive(opActive);
    moduleNeedLog.setOpArgs(args);
    moduleNeedLog.setOpResult(success ? "success" : "false");
    moduleNeedLog.setOpUser(username);
    needLogService.insertSelective(moduleNeedLog);
  }
  
}
