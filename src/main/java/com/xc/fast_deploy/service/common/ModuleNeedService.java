package com.xc.fast_deploy.service.common;

import com.xc.fast_deploy.dto.MyPageInfo;
import com.xc.fast_deploy.dto.ResponseDTO;
import com.xc.fast_deploy.model.master_model.ModuleDeployNeed;
import com.xc.fast_deploy.model.master_model.ModuleManage;
import com.xc.fast_deploy.model.master_model.ModuleUser;
import com.xc.fast_deploy.vo.module_vo.ModuleEnvCenterManageVo;
import com.xc.fast_deploy.vo.module_vo.ModuleManageDeployVO;
import com.xc.fast_deploy.vo.module_vo.ModuleUpgradeVo;
import com.xc.fast_deploy.vo.module_vo.param.ModuleNeedManageParamVo;
import com.xc.fast_deploy.vo.module_vo.param.ModuleNeedSelectParamVo;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface ModuleNeedService extends BaseService<ModuleDeployNeed, Integer> {
  
  //插入一条需求
  ResponseDTO insertOneNeed(ModuleDeployNeed moduleDeployNeed, List<Integer> moduleIdList,
                            MultipartFile reportFile, ModuleUser moduleUser);
  
  //更新一条需求
  ResponseDTO updateNeed(ModuleDeployNeed moduleDeployNeed, MultipartFile reportFile, ModuleUser moduleUser);
  
  //删除一条需求
  boolean deleteNeed(Integer id, ModuleUser moduleUser);
  
  //查询开发用户所有需求
  MyPageInfo<ModuleDeployNeed> selectAllNeedByDeveloper(Integer pageNum, Integer pageSize,
                                                        ModuleNeedSelectParamVo paramVo, String userName);
  
  //查询审核用户所有需求
  MyPageInfo<ModuleDeployNeed> selectAllNeedByApprover(Integer pageNum, Integer pageSize,
                                                       ModuleNeedSelectParamVo paramVo, String userName);
  
  //更新需求和模块的关联
  ResponseDTO updateNeedModule(Integer needId, Integer[] moduleIds);
  
  //查询一个需求关联的模块
  List<ModuleManageDeployVO> selectOneNeedModule(Integer needId);
  
  //审批需求
  List<ModuleUpgradeVo> approveNeed(Integer[] needIds, Integer pass, ModuleUser moduleUser, Date deployTime, Boolean isPromptly);
  
  //提交到发布清单
  boolean insertModuleDeployList(Integer[] needIds, ModuleUser moduleUser);
  
  //需求提交给局方审核
  ResponseDTO commitApprove(Integer[] needIds, ModuleUser moduleUser);
  
  //提交报告
  ResponseDTO commitReport(Integer needId, MultipartFile reportFile, Integer[] envIds, Integer drTest);
  
  //导出需求
  File exportNeed(Integer[] needIds);
  
  //查询所有
  //  List<ModuleDeployNeed> selectAll();
  File viewReport(Integer needId);
  
  //将需求流转到灾备或生产
  ResponseDTO circulationNeed(ModuleNeedManageParamVo needVo, MultipartFile reportFile, ModuleUser moduleUser);
  
  //将需求撤回
  boolean recallNeed(ModuleNeedManageParamVo needVo, MultipartFile reportFile, ModuleUser moduleUser);
  
  //需求关闭
  boolean closeNeed(Integer needId, ModuleUser moduleUser);
  
  //excel批量导入发布需求
  ResponseDTO insertNeedFromExcel(MultipartFile file, ModuleUser moduleUser);
}
