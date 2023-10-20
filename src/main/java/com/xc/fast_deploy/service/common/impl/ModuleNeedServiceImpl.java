package com.xc.fast_deploy.service.common.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xc.fast_deploy.dao.master_dao.*;
import com.xc.fast_deploy.dto.MyPageInfo;
import com.xc.fast_deploy.dto.ResponseDTO;
import com.xc.fast_deploy.dto.module.ModuleDeployNeedDTO;
import com.xc.fast_deploy.model.master_model.*;
import com.xc.fast_deploy.model.master_model.example.ModuleManageExample;
import com.xc.fast_deploy.myException.FileStoreException;
import com.xc.fast_deploy.myException.ModuleJobSaveException;
import com.xc.fast_deploy.myenum.ApproveStatusEnum;
import com.xc.fast_deploy.myenum.NeedTestStatusEnum;
import com.xc.fast_deploy.myenum.ResponseEnum;
import com.xc.fast_deploy.myenum.UserRoleTypeEnum;
import com.xc.fast_deploy.service.common.*;
import com.xc.fast_deploy.utils.DateUtils;
import com.xc.fast_deploy.utils.PoolExcutorUtils;
import com.xc.fast_deploy.utils.code_utils.ExcelPhraseUtils;
import com.xc.fast_deploy.utils.constant.NeedOPActive;
import com.xc.fast_deploy.vo.module_vo.ModuleDeployVo;
import com.xc.fast_deploy.vo.module_vo.ModuleEnvCenterManageVo;
import com.xc.fast_deploy.vo.module_vo.ModuleManageDeployVO;
import com.xc.fast_deploy.vo.module_vo.ModuleUpgradeVo;
import com.xc.fast_deploy.vo.module_vo.param.ModuleNeedManageParamVo;
import com.xc.fast_deploy.vo.module_vo.param.ModuleNeedSelectParamVo;
import com.xc.fast_deploy.vo.module_vo.permission.ModuleUserEnvRoleVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.aspectj.weaver.patterns.IfPointcut;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.xc.fast_deploy.utils.FoldUtils.SEP;
import static com.xc.fast_deploy.utils.constant.NeedOPActive.*;

@Service
@Slf4j
public class ModuleNeedServiceImpl extends BaseServiceImpl<ModuleDeployNeed, Integer> implements ModuleNeedService {
  
  @Autowired
  private ModuleJobService jobService;
  @Autowired
  private ModuleDeployNeedMapper needMapper;
  @Autowired
  private PModuleUserMapper userMapper;
  @Autowired
  private ModuleEnvMapper envMapper;
  @Autowired
  private ModuleManageMapper manageMapper;
  @Autowired
  private ModuleDeployListService deployListService;
  @Autowired
  private ModuleDeployMapper deployMapper;
  @Autowired
  private SyncService syncService;
  @Autowired
  private ModuleNeedLogService needLogService;
  
  @Value("${file.storge.path.moduleUploadPath}")
  private String moduleUploadPath;
  @Value("${myself.pspass.realprodid}")
  private Integer[] realprodid;
  @Value("${myself.pspass.prodId}")
  private Integer[] prodid;
  
  @Value("${myself.pspass.prod}")
  private boolean isProdEnv;
  
  @Override
  @Transactional(rollbackFor = Exception.class)
  public ResponseDTO insertOneNeed(ModuleDeployNeed moduleDeployNeed, List<Integer> moduleIdList,
                                   MultipartFile reportFile, ModuleUser moduleUser) {
    ResponseDTO responseDTO = new ResponseDTO();
    responseDTO.fail("需求添加失败");
    moduleIdList.removeIf(Objects::isNull);
    if (moduleIdList.size() <= 0) {
      responseDTO.fail("必须关联模块");
      return responseDTO;
    }
    if (StringUtils.isBlank(moduleDeployNeed.getNeedContent())
        || StringUtils.isBlank(moduleDeployNeed.getNeedDescribe()))
      return responseDTO.fail("需求内容和需求描述不能为空");
    //同一环境下需求描述不能重复
    if (needMapper.selectByNeedDescribe(moduleDeployNeed.getNeedDescribe()
        , moduleDeployNeed.getEnvId()) > 0) {
      return responseDTO.fail("该环境下已有此需求");
    }
    PModuleUser pModuleUser = userMapper.selectByUserName(moduleDeployNeed.getDeveloper());
    if (pModuleUser == null) return responseDTO.fail("开发人员不存在");
    ModuleEnv moduleEnv = envMapper.selectByPrimaryKey(moduleDeployNeed.getEnvId());
    moduleDeployNeed.setEnvName(moduleEnv.getEnvName());
    moduleDeployNeed.setDeveloper(pModuleUser.getCname());
    moduleDeployNeed.setApproveStatus(ApproveStatusEnum.APPROVE_NO_SUBMIT.getCode());
    int result = needMapper.insertSelective(moduleDeployNeed);
    if (result > 0) {
      synchronized (this) {
        //将需求关联模块
        List<ModuleNeed> moduleNeedList = new ArrayList<>();
        for (Integer moduleId : moduleIdList) {
          ModuleNeed moduleNeed = new ModuleNeed();
          moduleNeed.setNeedId(moduleDeployNeed.getId());
          moduleNeed.setModuleId(moduleId);
          moduleNeedList.add(moduleNeed);
        }
        needMapper.insertModuleNeed(moduleNeedList);
        responseDTO.success("需求添加成功");
      }
    }
    syncService.saveModuleNeedLog(needLogService, moduleDeployNeed.getId(), OP_INSERT_NEED,
        "", ResponseEnum.SUCCESS.getStatus().equals(responseDTO.getCode()),
        moduleUser.getUsername());
    return responseDTO;
  }
  
  @Override
  @Transactional(rollbackFor = Exception.class)
  public ResponseDTO updateNeed(ModuleDeployNeed moduleDeployNeed, MultipartFile reportFile, ModuleUser moduleUser) {
    ResponseDTO responseDTO = new ResponseDTO();
    responseDTO.fail("更新失败");
    if (StringUtils.isBlank(moduleDeployNeed.getNeedContent())
        || StringUtils.isBlank(moduleDeployNeed.getNeedDescribe()))
      return responseDTO.fail("需求内容和需求描述不能为空");
    ModuleDeployNeed deployNeed = needMapper.selectByPrimaryKey(moduleDeployNeed.getId());
    //审批通过或者已完成的需求不能更改
    if ((deployNeed.getApproveStatus() != ApproveStatusEnum.APPROVE_PASS.getCode()
        && deployNeed.getApproveStatus() != ApproveStatusEnum.APPROVE_COMPLETE.getCode())) {
      if (reportFile != null) {
        String[] reportFiles = reportFile.getOriginalFilename().split("\\.");
        StringBuilder fileName = new StringBuilder();
        fileName.append(moduleUploadPath).append(reportFiles[0])
            .append(DateUtils.generateDateOnlyString())
            .append(".").append(reportFiles[1]);
        File file = new File(fileName.toString());
        try {
          reportFile.transferTo(file);
        } catch (IOException e) {
          log.error("报告文件存储出现错误: {}", reportFile.getOriginalFilename());
          throw new FileStoreException("报告文件存储出现错误");
        }
        moduleDeployNeed.setTestReportPath(fileName.toString().replace(moduleUploadPath, ""));
        //删除原测试报告
        StringBuilder fn = new StringBuilder();
        File oldReport = new File(fn.append(moduleUploadPath).append(deployNeed.getTestReportPath()).toString());
        if (oldReport.exists()) oldReport.delete();
      }
      if (needMapper.updateByPrimaryKeySelective(moduleDeployNeed) > 0) {
        responseDTO.success("更新成功");
      }
    } else {
      responseDTO.fail("该需求不存在或者需求已审批通过");
    }
    syncService.saveModuleNeedLog(needLogService, moduleDeployNeed.getId(), OP_UPDATE_NEED,
        "", ResponseEnum.SUCCESS.getStatus().equals(responseDTO.getCode()),
        moduleUser.getUsername());
    return responseDTO;
  }
  
  @Override
  @Transactional(rollbackFor = Exception.class)
  public boolean deleteNeed(Integer needId, ModuleUser moduleUser) {
    boolean success = false;
    ModuleDeployNeed deployNeed = needMapper.selectByPrimaryKey(needId);
    //提交审核过后的需求不能删除
    if (!ApproveStatusEnum.APPROVE_NO_SUBMIT.getCode().equals(deployNeed.getApproveStatus()))
      return success;
    if (needMapper.deleteByPrimaryKey(needId) == 1) {
      File file = new File(moduleUploadPath + deployNeed.getTestReportPath());
      if (file.exists()) file.delete();
      //删除需求和模块的对应关系
      needMapper.deleteModuleNeedByNeedId(needId);
      success = true;
    }
    syncService.saveModuleNeedLog(needLogService, needId, OP_DELETE_NEED,
        "", success, moduleUser.getUsername());
    return success;
  }
  
  @Override
  public MyPageInfo<ModuleDeployNeed> selectAllNeedByDeveloper(Integer pageNum, Integer pageSize,
                                                               ModuleNeedSelectParamVo paramVo, String userName) {
    PModuleUser pModuleUser = userMapper.selectByUserName(userName);
    paramVo.setDeveloper(pModuleUser.getCname());
    paramVo.setEnvIds(new HashSet<>(Arrays.asList(prodid)));
    MyPageInfo<ModuleDeployNeed> myPageInfo = new MyPageInfo<>();
    PageHelper.startPage(pageNum, pageSize);
    List<ModuleDeployNeed> moduleDeployNeedList = needMapper.selectAllNeedByDeveloper(paramVo);
    PageInfo<ModuleDeployNeed> pageInfo = new PageInfo<>(moduleDeployNeedList);
    BeanUtils.copyProperties(pageInfo, myPageInfo);
    return myPageInfo;
  }
  
  @Override
  public MyPageInfo<ModuleDeployNeed> selectAllNeedByApprover(Integer pageNum, Integer pageSize,
                                                              ModuleNeedSelectParamVo paramVo, String username) {
    Set<Integer> envIds = userMapper.selectApproverEnvByUsername(username);
    MyPageInfo<ModuleDeployNeed> myPageInfo = new MyPageInfo<>();
    if (envIds.isEmpty()) return myPageInfo;
    paramVo.setEnvIds(envIds);
    PageHelper.startPage(pageNum, pageSize);
    List<ModuleDeployNeed> moduleDeployNeedList = needMapper.selectAllNeed(paramVo);
    PageInfo<ModuleDeployNeed> pageInfo = new PageInfo<>(moduleDeployNeedList);
    BeanUtils.copyProperties(pageInfo, myPageInfo);
    return myPageInfo;
  }
  
  @Override
  public ResponseDTO updateNeedModule(Integer needId, Integer[] moduleIds) {
    ResponseDTO responseDTO = new ResponseDTO();
    responseDTO.fail("更新失败");
    ModuleDeployNeed moduleDeployNeed = needMapper.selectByPrimaryKey(needId);
    List<Integer> moduleIdList = new ArrayList<>(Arrays.asList(moduleIds));
    moduleIdList.removeIf(Objects::isNull);
    if (moduleIdList.size() <= 0) {
      responseDTO.fail("必须关联模块");
      return responseDTO;
    }
    if (moduleDeployNeed != null) {
      if (moduleDeployNeed.getApproveStatus().equals(ApproveStatusEnum.APPROVE_PASS.getCode())) {
        responseDTO.fail("需求已审批通过无法更改");
        return responseDTO;
      }
      //先删除原来的关联
      needMapper.deleteModuleNeedByNeedId(needId);
      synchronized (this) {
        //重新关联模块
        List<ModuleNeed> moduleNeedList = new ArrayList<>();
        for (Integer moduleId : moduleIdList) {
          ModuleNeed moduleNeed = new ModuleNeed();
          moduleNeed.setNeedId(moduleDeployNeed.getId());
          moduleNeed.setModuleId(moduleId);
          moduleNeedList.add(moduleNeed);
        }
        needMapper.insertModuleNeed(moduleNeedList);
        responseDTO.success("更新成功");
      }
    }
    return responseDTO;
  }
  
  @Transactional
  @Override
  public List<ModuleUpgradeVo> approveNeed(Integer[] needIds, Integer pass, ModuleUser moduleUser, Date deployTime, Boolean isPromptly) {
    PModuleUser pModuleUser = userMapper.selectByUserName(moduleUser.getUsername());
    Set<Integer> envIds = userMapper.selectApproverEnvByUsername(pModuleUser.getUsername());
    Integer status =
        pass == 1 ? ApproveStatusEnum.APPROVE_PASS.getCode() : ApproveStatusEnum.APPROVE_NO_PASS.getCode();
    List<ModuleUpgradeVo> upgradeVos = new ArrayList<>();
    ModuleDeployNeed moduleDeployNeed = new ModuleDeployNeed();
    //立即发布
    if (isPromptly) {
      //查询环境与模块信息返回
      upgradeVos = needMapper.findModuleEnvByNeeds(Arrays.asList(needIds));
      upgradeVos.stream().forEach(vo -> {
        ModuleJob moduleJob = jobService.selectJobByModuleAndEnvId(vo.getEnvId(), vo.getModuleId());
        if (isPromptly && PoolExcutorUtils.jobHashMap.containsKey(moduleJob.getId())) {
          //如果队列中已经存在，则不能立即发布
          throw new ModuleJobSaveException("该任务已经存在，则不能立即发布");
        }
      });
    }
    
    for (Integer needId : needIds) {
      ModuleDeployNeed deployNeed = needMapper.selectByPrimaryKey(needId);
      //审核员需在该环境下有审核权限且已完成的需求不能再审核
      if (envIds.contains(deployNeed.getEnvId()) &&
          !ApproveStatusEnum.APPROVE_COMPLETE.getCode().equals(deployNeed.getApproveStatus())) {
        moduleDeployNeed.setId(deployNeed.getId());
        moduleDeployNeed.setApproveStatus(status);
        moduleDeployNeed.setApprover(pModuleUser.getCname());
        moduleDeployNeed.setApproveTime(new Date());
        moduleDeployNeed.setDeployTime(deployTime);
        needMapper.updateByPrimaryKeySelective(moduleDeployNeed);
        syncService.saveModuleNeedLog(needLogService, needId, OP_APPROVE_NEED,
            status.toString(), true,
            moduleUser.getUsername());
      }
    }
    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
    if ((pass == 1 && sdf.format(new Date()).equals(sdf.format(deployTime))) || isPromptly) {
      insertModuleDeployList(needIds, moduleUser);
    }
    
    return upgradeVos;
  }
  
  @Override
  public boolean insertModuleDeployList(Integer[] needIds, ModuleUser moduleUser) {
    boolean success = false;
    List<Integer> needIdList = new ArrayList<>(Arrays.asList(needIds));
    needIdList.removeIf(Objects::isNull);
    if (needIdList.size() <= 0) return success;
    Set<Integer> moduleSet = new HashSet<>();
    ModuleDeployNeed deployNeed;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
    for (Integer needId : needIdList) {
      deployNeed = needMapper.selectByPrimaryKey(needId);
      
      //审批通过且在窗口当天才能添加到发布清单
      if (ApproveStatusEnum.APPROVE_PASS.getCode().equals(deployNeed.getApproveStatus())
          && sdf.format(new Date()).equals(sdf.format(deployNeed.getDeployTime()))) {
        moduleSet = needMapper.selectAllModuleByneedId(needId, deployNeed.getEnvId());
        Integer[] moduleIds = moduleSet.toArray(new Integer[moduleSet.size()]);
        success = deployListService.insert2RedisModuleDeployList(deployNeed.getEnvId(), null, moduleIds);
        syncService.saveModuleNeedLog(needLogService, needId, OP_COMMIT_LIST,
            moduleSet.toString(), success,
            moduleUser.getUsername());
      }
      
    }
    return success;
  }
  
  @Override
  public ResponseDTO commitApprove(Integer[] needIds, ModuleUser moduleUser) {
    ResponseDTO responseDTO = new ResponseDTO();
    responseDTO.success("提交成功");
    Map<String, String> resultMap = new HashMap<>();
    List<Integer> needIdList = new ArrayList<>(Arrays.asList(needIds));
    needIdList.removeIf(Objects::isNull);
    if (needIdList.size() <= 0) return responseDTO.fail("没有需求被选中");
    ModuleDeployNeed deployNeed = new ModuleDeployNeed();
    for (Integer needId : needIdList) {
      ModuleDeployNeed need = needMapper.selectByPrimaryKey(needId);
      //只有未提交过的需求才能提交审批
      if (!need.getApproveStatus()
          .equals(ApproveStatusEnum.APPROVE_NO_SUBMIT.getCode())) {
        resultMap.put(need.getNeedContent(), "该需求已提交过");
        continue;
      }
//            if (need.getDeployTime() == null) {
//                resultMap.put(need.getNeedContent(), "请设置发布时间");
//                continue;
//            }
      deployNeed.setId(need.getId());
      deployNeed.setApproveStatus(ApproveStatusEnum.APPROVE_SUBMIT.getCode());
      needMapper.updateByPrimaryKeySelective(deployNeed);
      syncService.saveModuleNeedLog(needLogService, needId, OP_COMMIT_APPROVE,
          "", true,
          moduleUser.getUsername());
    }
    if (resultMap.size() > 0) responseDTO.fail(resultMap);
    return responseDTO;
  }
  
  @Override
  public ResponseDTO commitReport(Integer needId, MultipartFile reportFile,
                                  Integer[] envIds, Integer drTest) {
    ResponseDTO responseDTO = new ResponseDTO();
    ModuleDeployNeed moduleDeployNeed = needMapper.selectByPrimaryKey(needId);
    List<ModuleNeed> needList = needMapper.selectModuleNeedById(needId);
    List<Integer> envList = new ArrayList<>(Arrays.asList(envIds));
    if (envList.contains(moduleDeployNeed.getEnvId())) return responseDTO.fail("环境选择冲突");
    if (moduleDeployNeed.getApproveStatus().equals(ApproveStatusEnum.APPROVE_PASS.getCode()))
      return responseDTO.fail("该需求已审核通过");
    envList.removeIf(Objects::isNull);
    responseDTO.fail("操作失败");
    if (moduleDeployNeed != null && envList.size() > 0) {
      //存测试报告文件
      StringBuilder filePrefix = new StringBuilder();
      filePrefix.append(moduleUploadPath).append(SEP);
      StringBuilder fileName = new StringBuilder();
      fileName.append(filePrefix).append(reportFile.getOriginalFilename())
          .append(DateUtils.generateDateString())
          .append(".txt");
      File file = new File(fileName.toString());
      try {
        reportFile.transferTo(file);
      } catch (IOException e) {
        log.error("报告文件存储出现错误: {}", reportFile.getOriginalFilename());
        throw new FileStoreException("报告文件存储出现错误");
      }
      for (Integer envId : envList) {
        ModuleEnv moduleEnv = envMapper.selectByPrimaryKey(envId);
        if (moduleEnv == null) return responseDTO.fail("环境不存在");
        ModuleDeployNeed deployNeed = moduleDeployNeed;
        int count = needMapper.selectByNeedDescribe(deployNeed.getNeedDescribe(), envId);
        if (count > 0) return responseDTO.fail("该需求已提交");
        deployNeed.setId(null);
        deployNeed.setPstTest(NeedTestStatusEnum.TEST_PASS.getCode());
        //如果没有灾备环境，灾备测试只能是0(未通过)
        if (moduleDeployNeed.getDr() != 1) {
          deployNeed.setDrTest(NeedTestStatusEnum.TEST_NOPASS.getCode());
        } else {
          deployNeed.setDrTest(drTest);
        }
        deployNeed.setEnvId(envId);
        deployNeed.setEnvName(moduleEnv.getEnvName());
        deployNeed.setTestReportPath(fileName.toString().replace(moduleUploadPath, ""));
        deployNeed.setCreateTime(new Date());
        deployNeed.setUpdateTime(new Date());
        int result = needMapper.insertSelective(deployNeed);
        if (result > 0) {
          synchronized (this) {
            //将需求关联对应环境的模块
            List<ModuleNeed> moduleNeedList = new ArrayList<>();
            for (ModuleNeed need : needList) {
              ModuleManage moduleManage = manageMapper.selectByPrimaryKey(need.getModuleId());
              ModuleManageExample manageExample = new ModuleManageExample();
              manageExample.createCriteria().andEnvIdEqualTo(envId)
                  .andIsDeleteEqualTo(0)
                  .andModuleNameLike(moduleManage.getModuleName());
              List<ModuleManage> manageList = manageMapper.selectByExample(manageExample);
              if (manageList.size() == 0) continue;
              ModuleNeed moduleNeed = new ModuleNeed();
              moduleNeed.setNeedId(deployNeed.getId());
              moduleNeed.setModuleId(manageList.get(0).getId());
              moduleNeedList.add(moduleNeed);
            }
            needMapper.insertModuleNeed(moduleNeedList);
          }
        }
      }
      responseDTO.success("操作成功");
    }
    return responseDTO;
  }
  
  @Override
  public List<ModuleManageDeployVO> selectOneNeedModule(Integer needId) {
    ModuleDeployNeed deployNeed = needMapper.selectByPrimaryKey(needId);
    Set<Integer> moduleIds = needMapper.selectAllModuleByneedId(needId, deployNeed.getEnvId());
    return moduleIds.isEmpty() ? null : deployMapper.selectModuleCenter(moduleIds);
  }
  
  @Override
  public File exportNeed(Integer[] needIds) {
    List<ModuleDeployNeed> deployNeedList = needMapper.selectModuleNeedByNeedIds(needIds);
    if (deployNeedList.size() > 0) {
      Workbook wb0 = new XSSFWorkbook();
      CellStyle cellStyle = wb0.createCellStyle();
      cellStyle.setAlignment(HorizontalAlignment.CENTER);
      cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
      cellStyle.setBorderBottom(BorderStyle.THIN);
      cellStyle.setBorderLeft(BorderStyle.THIN);
      cellStyle.setBorderRight(BorderStyle.THIN);
      Sheet sheet = wb0.createSheet();
      
      Row row = sheet.createRow(0);
      Cell cell0 = row.createCell(0);
      cell0.setCellValue("序号");
      cell0.setCellStyle(cellStyle);
      
      Cell cell1 = row.createCell(1);
      cell1.setCellValue("需求编号");
      cell1.setCellStyle(cellStyle);
      
      Cell cell2 = row.createCell(2);
      cell2.setCellValue("需求内容");
      cell2.setCellStyle(cellStyle);
      
      Cell cell3 = row.createCell(3);
      cell3.setCellValue("需求描述");
      cell3.setCellStyle(cellStyle);
      
      Cell cell4 = row.createCell(4);
      cell4.setCellValue("环境");
      cell4.setCellStyle(cellStyle);
      
      Cell cell5 = row.createCell(5);
      cell5.setCellValue("灾备");
      cell5.setCellStyle(cellStyle);
      
      Cell cell6 = row.createCell(6);
      cell6.setCellValue("PST测试");
      cell6.setCellStyle(cellStyle);
      
      Cell cell7 = row.createCell(7);
      cell7.setCellValue("灾备测试");
      cell7.setCellStyle(cellStyle);
      
      Cell cell8 = row.createCell(8);
      cell8.setCellValue("发布时间");
      cell8.setCellStyle(cellStyle);
      
      Cell cell9 = row.createCell(9);
      cell9.setCellValue("申请人");
      cell9.setCellStyle(cellStyle);
      
      log.info("size:" + deployNeedList.size());
      for (int i = 0; i < deployNeedList.size(); i++) {
        log.info(deployNeedList.get(i).toString());
        Row row1 = sheet.createRow(i + 1);
        Cell cl0 = row1.createCell(0);
        cl0.setCellValue(i + 1);
        cl0.setCellStyle(cellStyle);
        
        Cell cl1 = row1.createCell(1);
        if (StringUtils.isNotBlank(deployNeedList.get(i).getNeedNumber())) {
          cl1.setCellValue(deployNeedList.get(i).getNeedNumber());
        } else cl1.setCellValue("");
        cl1.setCellStyle(cellStyle);
        
        Cell cl2 = row1.createCell(2);
        if (StringUtils.isNotBlank(deployNeedList.get(i).getNeedContent())) {
          cl2.setCellValue(deployNeedList.get(i).getNeedContent());
        } else cl2.setCellValue("");
        cl2.setCellStyle(cellStyle);
        
        Cell cl3 = row1.createCell(3);
        if (StringUtils.isNotBlank(deployNeedList.get(i).getNeedDescribe())) {
          cl3.setCellValue(deployNeedList.get(i).getNeedDescribe());
        } else cl3.setCellValue("");
        cl3.setCellStyle(cellStyle);
        
        Cell cl4 = row1.createCell(4);
        cl4.setCellValue(deployNeedList.get(i).getEnvName());
        cl4.setCellStyle(cellStyle);
        
        Cell cl5 = row1.createCell(5);
        if (deployNeedList.get(i).getDr() == 1) {
          cl5.setCellValue("有");
        } else cl5.setCellValue("无");
        cl5.setCellStyle(cellStyle);
        
        Cell cl6 = row1.createCell(6);
        if (deployNeedList.get(i).getPstTest() == 1) {
          cl6.setCellValue("通过");
        } else cl6.setCellValue("未通过");
        cl6.setCellStyle(cellStyle);
        
        Cell cl7 = row1.createCell(7);
        if (deployNeedList.get(i).getDrTest() == 1) {
          cl7.setCellValue("通过");
        } else cl7.setCellValue("未通过");
        cl7.setCellStyle(cellStyle);
        
        Cell cl8 = row1.createCell(8);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String deployTime = dateFormat.format(deployNeedList.get(i).getDeployTime());
        cl8.setCellValue(deployTime);
        cl8.setCellStyle(cellStyle);
        
        Cell cl9 = row1.createCell(9);
        cl9.setCellValue(deployNeedList.get(i).getDeveloper());
        cl9.setCellStyle(cellStyle);
      }
      
      ExcelPhraseUtils.setSizeColumn(sheet, 10);
      StringBuilder sb = new StringBuilder();
      sb.append(moduleUploadPath).append("needFile").append(DateUtils.generateDateOnlyString()).append(".xlsx");
      File file = new File(sb.toString());
      if (!file.exists()) {
        try {
          file.createNewFile();
          FileOutputStream fos = new FileOutputStream(file);
          wb0.write(fos);
          fos.close();
          log.info("生成excel文件成功: {}", file.getName());
          return file;
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
    return null;
  }
  
  @Override
  public File viewReport(Integer needId) {
    ModuleDeployNeed deployNeed = needMapper.selectByPrimaryKey(needId);
    if (deployNeed != null) {
      StringBuilder sb = new StringBuilder();
      sb.append(moduleUploadPath).append(deployNeed.getTestReportPath());
      File file = new File(sb.toString());
      if (file.exists()) {
        return file;
      } else {
        log.error("报告文件不存在");
      }
    } else {
      log.error("未找到对应需求");
    }
    return null;
  }
  
  @Override
  public ResponseDTO circulationNeed(ModuleNeedManageParamVo needVo, MultipartFile reportFile,
                                     ModuleUser moduleUser) {
    ResponseDTO responseDTO = new ResponseDTO();
    responseDTO.success("更改成功");
    ModuleDeployNeed deployNeed = needMapper.selectByPrimaryKey(needVo.getNeedId());
    if (!ApproveStatusEnum.APPROVE_PASS.getCode().equals(deployNeed.getApproveStatus()))
      return responseDTO.fail("审核通过才能操作");
    //环境相同不操作，如果是生产环境不能操作
    if (deployNeed.getEnvId().equals(needVo.getEnvId())
        || Arrays.asList(realprodid).contains(deployNeed.getEnvId()))
      return responseDTO.fail("无变更操作");
    List<ModuleManageDeployVO> moduleManageDeployVOS = selectOneNeedModule(needVo.getNeedId());
    ArrayList<String> nameList = new ArrayList<>();
    List<ModuleNeed> needList = new LinkedList<>();
    for (ModuleManageDeployVO deployVO : moduleManageDeployVOS) {
      ModuleManageExample manageExample = new ModuleManageExample();
      manageExample.createCriteria().andIsDeleteEqualTo(0).andModuleNameEqualTo(deployVO.getModuleName())
          .andEnvIdEqualTo(needVo.getEnvId());
      List<ModuleManage> manageList = manageMapper.selectByExample(manageExample);
      if (manageList.size() == 1) {
        ModuleNeed moduleNeed = new ModuleNeed();
        moduleNeed.setNeedId(needVo.getNeedId());
        moduleNeed.setModuleId(manageList.get(0).getId());
        needList.add(moduleNeed);
      } else {
        nameList.add(deployVO.getModuleName());
      }
    }
    if (needList.isEmpty()) return responseDTO.fail("环境设置有误");
    needMapper.insertModuleNeed(needList);
    needVo.setStatus(ApproveStatusEnum.APPROVE_SUBMIT.getCode());
    recallNeed(needVo, reportFile, moduleUser);
    if (!nameList.isEmpty()) return responseDTO.fail("以下模块名不匹配：" + nameList);
    return responseDTO;
  }
  
  @Override
  public boolean recallNeed(ModuleNeedManageParamVo needVo, MultipartFile reportFile,
                            ModuleUser moduleUser) {
    if (ApproveStatusEnum.getStatusByCode(needVo.getStatus()) == null) return false;
    ModuleEnv moduleEnv = envMapper.selectOne(needVo.getEnvId());
    ModuleDeployNeed deployNeed = needMapper.selectByPrimaryKey(needVo.getNeedId());
    ModuleDeployNeed moduleDeployNeed = new ModuleDeployNeed();
    moduleDeployNeed.setId(needVo.getNeedId());
    moduleDeployNeed.setApproveStatus(needVo.getStatus());
    moduleDeployNeed.setEnvId(needVo.getEnvId());
    moduleDeployNeed.setEnvName(moduleEnv.getEnvName());
    String opAction = OP_RECALL_NEED;
    if (reportFile != null) {
      String[] reportFiles = reportFile.getOriginalFilename().split("\\.");
      StringBuilder fileName = new StringBuilder();
      fileName.append(moduleUploadPath).append(reportFiles[0])
          .append(DateUtils.generateDateOnlyString())
          .append(".").append(reportFiles[1]);
      File file = new File(fileName.toString());
      try {
        reportFile.transferTo(file);
        moduleDeployNeed.setTestReportPath(fileName.toString().replace(moduleUploadPath, ""));
        moduleDeployNeed.setPstTest(needVo.getPstTest());
        moduleDeployNeed.setDrTest(needVo.getDrTest());
        opAction = OP_CIRCULATION_NEED;
      } catch (IOException e) {
        log.error("报告文件存储出现错误: {}", reportFile.getOriginalFilename());
        throw new FileStoreException("报告文件存储出现错误");
      }
    }
    needMapper.updateByPrimaryKeySelective(moduleDeployNeed);
    //需求提交后，原环境下的模块从发布清单里清除
    Set<Integer> moduleIds = needMapper.selectAllModuleByneedId(deployNeed.getId(), deployNeed.getEnvId());
    deployListService.removeModuleDeployList(moduleIds.toArray(new Integer[moduleIds.size()]));
    syncService.saveModuleNeedLog(needLogService, needVo.getNeedId(), opAction,
        "", true,
        moduleUser.getUsername());
    return true;
  }
  
  @Override
  public boolean closeNeed(Integer needId, ModuleUser moduleUser) {
    boolean success = false;
    ModuleDeployNeed deployNeed = needMapper.selectByPrimaryKey(needId);
    PModuleUser pModuleUser = userMapper.selectByUserId(moduleUser.getId());
    Set<Integer> envIds =
        userMapper.selectApproverEnvByUsername(pModuleUser.getUsername());
    if (deployNeed != null && envIds.contains(deployNeed.getEnvId())) {
      ModuleDeployNeed moduleDeployNeed = new ModuleDeployNeed();
      moduleDeployNeed.setId(needId);
      moduleDeployNeed.setApprover(pModuleUser.getCname());
      moduleDeployNeed.setApproveStatus(ApproveStatusEnum.APPROVE_COMPLETE.getCode());
      needMapper.updateByPrimaryKeySelective(moduleDeployNeed);
      Set<Integer> moduleIds = needMapper.selectAllModuleByneedId(needId, null);
      success = deployListService.removeModuleDeployList(moduleIds.toArray(new Integer[moduleIds.size()]));
    }
    syncService.saveModuleNeedLog(needLogService, needId, OP_CLOSE_NEED,
        "", success,
        moduleUser.getUsername());
    return success;
  }
  
  @Override
  public ResponseDTO insertNeedFromExcel(MultipartFile file, ModuleUser moduleUser) {
    ResponseDTO responseDTO = new ResponseDTO();
    Map<String, Object> resultMap = new HashMap<>();
    try {
      List<ModuleDeployNeedDTO> deployNeedExcel
          = ExcelPhraseUtils.getDeployNeedExcel(file.getInputStream());
      for (int i = 0; i < deployNeedExcel.size(); i++) {
        List<Integer> moduleIdList = new LinkedList<>();
        ModuleDeployNeedDTO moduleDeployNeedDTO = deployNeedExcel.get(i);
        ModuleDeployNeed deployNeed = moduleDeployNeedDTO.getDeployNeed();
        Integer envId = envMapper.getEnvIdByEnvName(deployNeed.getEnvName());
        deployNeed.setEnvId(envId);
        List<String> moduleNameList = moduleDeployNeedDTO.getModuleNameList();
        if (moduleNameList.isEmpty()) {
          resultMap.put(deployNeed.getNeedNumber(), "必须绑定模块");
          continue;
        }
        ModuleManageExample manageExample = new ModuleManageExample();
        manageExample.createCriteria().andEnvIdEqualTo(envId)
            .andIsDeleteEqualTo(0).andModuleNameIn(moduleNameList);
        List<ModuleManage> manageList = manageMapper.selectByExample(manageExample);
        manageList.stream().forEach(e -> moduleIdList.add(e.getId()));
        responseDTO = insertOneNeed(deployNeed, moduleIdList, null, moduleUser);
        resultMap.put(deployNeed.getNeedNumber(), responseDTO.getData());
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return responseDTO.success(resultMap);
  }
}
