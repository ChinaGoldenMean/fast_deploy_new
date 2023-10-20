package com.xc.fast_deploy.service.common;

import com.xc.fast_deploy.model.master_model.ModuleCertificate;
import com.xc.fast_deploy.model.master_model.ModuleEnv;
import com.xc.fast_deploy.model.master_model.ModulePackage;
import org.yeauty.pojo.Session;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

public interface SyncService {
  
  void checkoutAllAsync(List<ModulePackage> packageDTOS, ModuleCertificate certificate);
  
  void checkoutAllSync(List<ModulePackage> packageDTOS, ModuleCertificate certificate);
  
  void saveModuleDeployLog(ModuleDeployLogService moduleDeployLogService, Integer moduleId, Integer envId,
                           Integer deployId, String active, Map<String, Object> argsMap, boolean success, String userId, String username);
  
  void shutdownExcutor(ThreadPoolExecutor poolExecutor, Session session, Integer size);
  
  void addBatchData(ModuleManageService moduleManageService, Session session, String userId, File file
      , ModuleEnv moduleEnv, ModuleCertificate certificate, String projectCode,
                    Integer moduleTypeCode, String gitBranch);
  
  void copyFile2File(File sourceFile, File destFile);
  
  void saveModuleNeedLog(ModuleNeedLogService needLogService, Integer needId, String opActive,
                         String args, boolean success, String username);
}
