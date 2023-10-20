package com.xc.fast_deploy.service.common;

import com.xc.fast_deploy.dto.CodeUpdateInfoDTO;
import com.xc.fast_deploy.dto.StatusDTO;
import com.xc.fast_deploy.dto.module.ModulePackageDTO;
import com.xc.fast_deploy.dto.module.permission.ModuleSubPackageDTO;
import com.xc.fast_deploy.model.master_model.ModuleCertificate;
import com.xc.fast_deploy.model.master_model.ModuleManage;
import com.xc.fast_deploy.model.master_model.ModulePackage;
import com.xc.fast_deploy.vo.module_vo.param.ModuleManageParamVo;
import com.xc.fast_deploy.vo.module_vo.param.ModulePackageParamVo;
import org.tmatesoft.svn.core.SVNException;
import org.yeauty.pojo.Session;

import java.io.FileNotFoundException;
import java.util.List;

public interface ModulePackageService extends BaseService<ModulePackage, Integer> {
  boolean gitAutoType(ModuleCertificate certificate, ModuleManage moduleManage, StatusDTO statusDTO, Session session, StringBuilder filePrefixBase);
  
  boolean saveGitModulePackage(ModuleCertificate certificate, ModuleManage moduleManage, List<ModulePackageParamVo> packageList, StringBuilder filePrefix, StatusDTO statusDTO, Session session);
  
  //批量添加数据
  int insertAll(List<ModulePackage> list);
  
  //根据模块id查询该模块对应的package记录
  List<ModulePackageDTO> selectByModuleId(Integer moduleId);
  
  //获取svnUrl对应的日志信息
  List<CodeUpdateInfoDTO> getCodeUpdateInfo(Integer packageId);
  
  //根据模块id删除相关的记录
  boolean deleteByModuleId(Integer moduleId);
  
  boolean insertInfo(ModuleSubPackageDTO modulePackage);
  
  boolean deletePackageInfo(Integer packageId);
  
  boolean updatePackageInfo(Integer packageId, String codeUrl, String contentName, String gitBranch);
  
  boolean updatePackageReversion(Integer packageId, String reversion);
  
  Integer selectEnvIdByPackageId(Integer packageId);
  
  void downSvnAll();
  
  boolean updateModuleAllCode(Integer moduleId, Session session) throws SVNException, FileNotFoundException;
  
  List<String> getRemoteBranches(Integer envId, Integer packageId);
  
  boolean chanageBranch(Integer envId, Integer packageId, String branchName);
}
