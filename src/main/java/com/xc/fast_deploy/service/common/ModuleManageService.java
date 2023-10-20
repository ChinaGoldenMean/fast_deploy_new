package com.xc.fast_deploy.service.common;

import com.xc.fast_deploy.dto.MyPageInfo;
import com.xc.fast_deploy.dto.StatusDTO;
import com.xc.fast_deploy.dto.module.ModuleManageDTO;
import com.xc.fast_deploy.model.master_model.ModuleCertificate;
import com.xc.fast_deploy.model.master_model.ModuleEnv;
import com.xc.fast_deploy.vo.FoldDataVo;
import com.xc.fast_deploy.vo.module_vo.ModuleManageUpdateVo;
import com.xc.fast_deploy.vo.module_vo.ModuleMangeVo;
import com.xc.fast_deploy.vo.module_vo.param.ModuleManageParamVo;
import com.xc.fast_deploy.model.master_model.ModuleManage;
import com.xc.fast_deploy.vo.module_vo.param.ModuleManageSelectParamVo;
import com.xc.fast_deploy.vo.module_vo.param.ModulePackageParamVo;
import org.springframework.web.multipart.MultipartFile;
import org.tmatesoft.svn.core.SVNException;
import org.yeauty.pojo.Session;

import java.io.File;
import java.util.List;
import java.util.Map;

public interface ModuleManageService extends BaseService<ModuleManage, Integer> {
  boolean saveGitModulePackage(ModuleCertificate certificate, ModuleManage moduleManage, List<ModulePackageParamVo> packageList, StringBuilder filePrefix, StatusDTO statusDTO, Session session);
  
  MyPageInfo<ModuleMangeVo> getAllModuleDTOBySelect(Integer pageNum, Integer pageSize, ModuleManageSelectParamVo manageSelectParamVo);
  
  boolean saveAll(String userId, ModuleManageParamVo mangeVo, MultipartFile excelFile, MultipartFile codeExcelFile);
  
  ModuleManageDTO selectInfoById(Integer moduleId);
  
  boolean updateInfo(ModuleManageUpdateVo updateVo, MultipartFile excelFile, MultipartFile[] packageFiles);
  
  boolean deleteInfoById(Integer moduleId, ModuleManage moduleManage);
  
  FoldDataVo getAllManageFolders(Integer moduleId);
  
  List<ModuleManage> selectAllByCenterId(Integer centerId);
  
  int updateBySelective(ModuleManage moduleManage, Integer moduleType, String svnUrlPath) throws SVNException;
  
  boolean saveAll(Session session, String userId, ModuleManageParamVo paramVo, ModuleEnv moduleEnv,
                  ModuleCertificate certificate, String projectCode, String fileName);
  
  List<ModuleManage> selectByIds(Integer[] moduleIds);
  
  String getFilePath(Integer moduleId, Integer type);
  
  List<ModuleManage> selectAllNotInJobById(Integer centerId);
  
  //自动更新的类型 批量添加数据
  boolean saveAllAutoManageData(Session session, ModuleEnv moduleEnv, List<ModuleManageParamVo> manageParamVos,
                                String userId, ModuleCertificate certificate, String projectCode, String fileName);
  
  Map<String, String> copyModule2OtherEnv(List<Integer> moduleIds, Integer srcEnvId,
                                          Integer desEnvId, String userId, String hProjectCode);
  
  File genManageFile(Integer envId);
}
