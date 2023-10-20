package com.xc.fast_deploy.service.common.impl;

import com.alibaba.fastjson.JSONObject;
import com.ctg.itrdc.cache.pool.CtgJedisPoolException;
import com.ctg.itrdc.cache.pool.ProxyJedis;
import com.xc.fast_deploy.dao.master_dao.*;
import com.xc.fast_deploy.dto.ResponseDTO;
import com.xc.fast_deploy.dto.k8s.K8sPodDTO;
import com.xc.fast_deploy.dto.k8s.K8sServiceDTO;
import com.xc.fast_deploy.model.master_model.ModuleEnv;
import com.xc.fast_deploy.model.master_model.ModuleManage;
import com.xc.fast_deploy.model.master_model.ModuleUser;
import com.xc.fast_deploy.model.master_model.PModuleUser;
import com.xc.fast_deploy.model.master_model.example.ModuleManageExample;
import com.xc.fast_deploy.myException.FileStoreException;
import com.xc.fast_deploy.myenum.UserRoleTypeEnum;
import com.xc.fast_deploy.rediscache.JedisManage;
import com.xc.fast_deploy.service.common.ModuleDeployListService;
import com.xc.fast_deploy.service.common.ModuleDeployService;
import com.xc.fast_deploy.shiro.token.JwtUtil;
import com.xc.fast_deploy.utils.DateUtils;
import com.xc.fast_deploy.utils.code_utils.ExcelPhraseUtils;
import com.xc.fast_deploy.utils.constant.RedisKeyPrefix;
import com.xc.fast_deploy.vo.module_vo.ModuleDeployVo;
import com.xc.fast_deploy.vo.module_vo.ModuleEnvCenterManageVo;
import com.xc.fast_deploy.vo.module_vo.ModuleEnvManageMirrorVo;
import com.xc.fast_deploy.vo.module_vo.ModuleManageDeployVO;
import com.xc.fast_deploy.vo.module_vo.param.ModuleDeployPodCenterManageVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.POIXMLException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

@Service
@Slf4j
public class ModuleDeployListServiceImpl implements ModuleDeployListService {
  
  @Resource
  private JedisManage jedisManage;
  
  @Autowired
  private ModuleDeployMapper deployMapper;
  @Autowired
  private ModuleDeployService moduleDeployService;
  @Autowired
  private ModuleMirrorMapper mirrorMapper;
  @Autowired
  private PModuleUserMapper userMapper;
  @Autowired
  private ModuleEnvMapper envMapper;
  @Autowired
  private ModuleManageMapper manageMapper;
  @Autowired
  private ModuleManageServiceImpl manageService;
  
  @Value("${myself.pspass.prod}")
  private boolean isProdEnv;
  @Value("${file.storge.path.moduleExportPath}")
  private String moduleExportPath;
  
  /**
   * 获取发布清单列表
   *
   * @param userId
   * @param envId
   * @param centerId
   * @param keyName
   * @return
   */
  @Override
  public Set<ModuleEnvCenterManageVo> getAllDeployListSelf(String userId, Integer envId,
                                                           Integer centerId, String keyName) {
    List<ModuleEnvCenterManageVo> resultManageVoList = new LinkedList<>();
    List<ModuleEnvCenterManageVo> moduleEnvCenterManageVoList = new LinkedList<>();
    PModuleUser moduleUser = userMapper.selectByUserId(userId);
    ProxyJedis jedis = null;
    Set<String> envIds = new HashSet<>();
    try {
      jedis = jedisManage.getJedis();
      Map<String, String> allMap;
      String keyPrefix;
      keyPrefix = isProdEnv ?
          RedisKeyPrefix.PROD_REDIS_PREFIX
          : RedisKeyPrefix.NOT_PROD_REDIS_PREFIX;
      if (jedis.type(keyPrefix).equals("hash")) {
        allMap = jedis.hgetAll(keyPrefix);
        //解析取出来的相关数据
        if (allMap != null && allMap.size() > 0) {
          envIds = allMap.keySet();
          if (envId != null) {
            String back = allMap.get(envId.toString());
            if (StringUtils.isNotBlank(back)) {
              List<ModuleEnvCenterManageVo> manageVoList =
                  JSONObject.parseArray(back, ModuleEnvCenterManageVo.class);
              moduleEnvCenterManageVoList.addAll(manageVoList);
            }
          } else {
            for (String eid : envIds) {
              String back = allMap.get(eid);
              if (StringUtils.isNotBlank(back)) {
                List<ModuleEnvCenterManageVo> manageVoList =
                    JSONObject.parseArray(back, ModuleEnvCenterManageVo.class);
                moduleEnvCenterManageVoList.addAll(manageVoList);
              }
            }
          }
        }
      }
      //开发角色要过滤掉没有权限的中心
      Map<Integer, Set<Integer>> filter = new HashMap<>();
      if (moduleUser != null &&
          !userMapper.selectRoleBindUser(moduleUser.getId(), UserRoleTypeEnum.DEVELOPER_ROLE.getCode(), null).isEmpty()) {
        
        ModuleUser user = JwtUtil.getModuleUserInfo();
        for (String id : envIds) {
          Set<Integer> centerIds =
              userMapper.selectUserAllCentersById(moduleUser.getId(), Integer.valueOf(id));
          if (!centerIds.isEmpty()) {
            filter.put(Integer.valueOf(id), centerIds);
          }
        }
        //如果为空表示没有权限直接返回null
        if (filter.isEmpty()) return null;
        Iterator<ModuleEnvCenterManageVo> iterator = moduleEnvCenterManageVoList.iterator();
        while (iterator.hasNext() && !moduleEnvCenterManageVoList.isEmpty()) {
          //先去掉没有权限的环境和中心
          ModuleEnvCenterManageVo centerManageVo = iterator.next();
          if (!filter.keySet().contains(centerManageVo.getEnvId())) {
            iterator.remove();
            continue;
          } else if (!filter.get(centerManageVo.getEnvId()).contains(centerManageVo.getCenterId())) {
            iterator.remove();
            continue;
          }
        }
      }
      
      // log.info("过滤环境后的数据 {}", JSONObject.toJSONString(moduleEnvCenterManageVoList));
      //过滤相关的数据根据相关的条件
      if (centerId != null || StringUtils.isNotBlank(keyName)) {
        for (ModuleEnvCenterManageVo moduleEnvCenterManageVo : moduleEnvCenterManageVoList) {
          if (centerId != null
              && StringUtils.isNotBlank(keyName)
              && centerId.equals(moduleEnvCenterManageVo.getCenterId())
              && moduleEnvCenterManageVo.getModuleName().contains(keyName)) {
            resultManageVoList.add(moduleEnvCenterManageVo);
          } else if (centerId != null
              && StringUtils.isBlank(keyName)
              && centerId.equals(moduleEnvCenterManageVo.getCenterId())) {
            resultManageVoList.add(moduleEnvCenterManageVo);
          } else if (centerId == null
              && StringUtils.isNotBlank(keyName)
              && moduleEnvCenterManageVo.getModuleName().contains(keyName)) {
            resultManageVoList.add(moduleEnvCenterManageVo);
          }
        }
        moduleEnvCenterManageVoList.clear();
      } else {
        return new HashSet<>(moduleEnvCenterManageVoList);
      }
      
    } catch (CtgJedisPoolException e) {
      e.printStackTrace();
    } finally {
      if (jedis != null) {
        jedis.close();
      }
    }
    return new HashSet<>(resultManageVoList);
  }
  
  /**
   * 插入发布清单相关数据
   *
   * @param envId
   * @param userId
   * @param moduleIds
   * @return
   */
  @Override
  public boolean insert2RedisModuleDeployList(Integer envId, String userId, Integer[] moduleIds) {
    boolean success = false;
    //首先查询添加的moduleIds的相关数据
    ModuleDeployVo deployVo = new ModuleDeployVo();
    deployVo.setEnvId(envId);
    if (moduleIds != null) {
      Set<Integer> moduleIdSet = new HashSet<>();
      boolean b = moduleIdSet.addAll(Arrays.asList(moduleIds));
      if (b && moduleIdSet.size() > 0) {
        deployVo.setModuleIds(moduleIdSet);
      }
    }
    List<ModuleEnvCenterManageVo> manageVoList = deployMapper.selectModuleEnvCenterAll(deployVo);
    Set<ModuleEnvCenterManageVo> manageVos = new HashSet<>();
    if (manageVoList != null && manageVoList.size() > 0) {
      Map<Integer, ModuleEnvCenterManageVo> centerManageVoMap = new HashMap<>();
      //然后获取已有的相关数据
      manageVos = getAllDeployListSelf(userId, envId, null, null);
      
      if (manageVos != null && manageVos.size() > 0) {
        for (ModuleEnvCenterManageVo centerManageVo : manageVos) {
          centerManageVoMap.put(centerManageVo.getModuleId(), centerManageVo);
        }
      }
      //比对模块id是否重复
      for (ModuleEnvCenterManageVo moduleEnvCenterManageVo : manageVoList) {
        if (!centerManageVoMap.containsKey(moduleEnvCenterManageVo.getModuleId())
            && manageVos != null) {
          manageVos.add(moduleEnvCenterManageVo);
        }
      }
    }
    if (manageVos != null && manageVos.size() > 0) {
      //覆盖添加到redis对应的key值中去
      ProxyJedis jedis = null;
      try {
        jedis = jedisManage.getJedis();
        String keyPrefix = isProdEnv ?
            RedisKeyPrefix.PROD_REDIS_PREFIX
            : RedisKeyPrefix.NOT_PROD_REDIS_PREFIX;
        jedis.hset(keyPrefix, envId.toString(), JSONObject.toJSONString(manageVos));
        success = true;
      } catch (CtgJedisPoolException e) {
        e.printStackTrace();
      } finally {
        if (jedis != null) {
          jedis.close();
        }
      }
    }
    return success;
  }
  
  /**
   * 批量移除待发布的模块清单
   *
   * @param moduleIds
   * @return
   */
  @Override
  public boolean removeModuleDeployList(Integer[] moduleIds) {
    boolean success = false;
    //获取redis中已存在的数据
    if (moduleIds != null && moduleIds.length > 0) {
      ProxyJedis jedis = null;
      Set<Integer> moduleIdSet = new HashSet<>(Arrays.asList(moduleIds));
      //PModuleUser moduleUser = userMapper.selectByUserId(userId);
      try {
        jedis = jedisManage.getJedis();
//                List<ModuleUserEnvRoleVo> userEnvRoleVos =
//                        userMapper.selectRoleBindUser(moduleUser.getId(), UserRoleTypeEnum.DEVELOPER_ROLE.getCode());
        String keyPrefix;
        keyPrefix = isProdEnv ?
            RedisKeyPrefix.PROD_REDIS_PREFIX
            : RedisKeyPrefix.NOT_PROD_REDIS_PREFIX;
        if (jedis.type(keyPrefix).equals("hash")) {
          Map<String, String> allMap = jedis.hgetAll(keyPrefix);
          log.info("测试：{}", keyPrefix + allMap);
          if (allMap != null && allMap.size() > 0) {
            Set<String> envIdSet = allMap.keySet();
            for (String envId : envIdSet) {
              String envBack = allMap.get(envId);
              if (StringUtils.isNotBlank(envBack)) {
                List<ModuleEnvCenterManageVo> realManageVoList = new ArrayList<>();
                List<ModuleEnvCenterManageVo> manageVoList =
                    JSONObject.parseArray(envBack, ModuleEnvCenterManageVo.class);
                //比对数据清除数据
                if (manageVoList != null && manageVoList.size() > 0) {
                  //需要清除的模块id集合不包含的即为需要保留的模块数据集合
                  for (ModuleEnvCenterManageVo manageVo : manageVoList) {
                    if (!moduleIdSet.contains(manageVo.getModuleId())) {
                      realManageVoList.add(manageVo);
                    }
                  }
                  //然后再重新set回去相关的数据 只有在数据数量不一样的情况下
                  if (realManageVoList.size() != manageVoList.size()) {
                    jedis.hset(keyPrefix, envId, JSONObject.toJSONString(realManageVoList));
                  }
                }
              }
            }
          }
        }
        success = true;
      } catch (CtgJedisPoolException e) {
        e.printStackTrace();
      } finally {
        if (jedis != null) {
          jedis.close();
        }
      }
    }
    return success;
  }
  
  /**
   * 获取发布清单中的所有模块数据并加
   *
   * @param envId
   * @param userId
   * @param centerId
   * @param keyName
   * @return
   */
  @Override
  public List<ModuleDeployPodCenterManageVo> getAllDeployListSelfPods(Integer envId, String userId,
                                                                      Integer centerId, String keyName) {
    List<ModuleDeployPodCenterManageVo> podCenterManageVos = new LinkedList<>();
    Set<ModuleEnvCenterManageVo> manageVos = getAllDeployListSelf(userId, envId, centerId, keyName);
    if (manageVos != null && manageVos.size() > 0) {
      for (ModuleEnvCenterManageVo manageVo : manageVos) {
        ModuleDeployPodCenterManageVo podCenterManageVo = new ModuleDeployPodCenterManageVo();
        //获取列表的pod的相关数据
        List<K8sPodDTO> k8sPodDTOS =
            moduleDeployService.getDeployModuleInfo(manageVo.getModuleId(), manageVo.getEnvId());
        K8sServiceDTO svcInfo = moduleDeployService.getSvcInfo(manageVo.getModuleId(),
            manageVo.getEnvId());
        podCenterManageVo.setK8sPodDTOS(k8sPodDTOS);
        podCenterManageVo.setK8sServiceDTO(svcInfo);
        BeanUtils.copyProperties(manageVo, podCenterManageVo);
        podCenterManageVos.add(podCenterManageVo);
      }
    }
    return podCenterManageVos;
  }
  
  /**
   * 批量替换对应模块的镜像为可用的最新镜像
   *
   * @param moduleUser
   * @param moduleIds
   * @return
   */
  @Override
  public Map<String, Object> replaceBatchMirrors(ModuleUser moduleUser, Integer[] moduleIds) {
    Map<String, Object> resultMap = new HashMap<>();
    if (moduleIds != null && moduleIds.length > 0 && StringUtils.isNotBlank(moduleUser.getId())) {
      Set<Integer> moduleIdSet = new HashSet<>(Arrays.asList(moduleIds));
      List<ModuleEnvManageMirrorVo> mirrorList =
          mirrorMapper.selectModuleMirrorLatestBatch(moduleIdSet);
      if (mirrorList != null && mirrorList.size() > 0) {
        //循环遍历替换镜像
        for (ModuleEnvManageMirrorVo envManageMirrorVo : mirrorList) {
          ResponseDTO resp = moduleDeployService.changeMirror(envManageMirrorVo.getModuleId(),
              envManageMirrorVo.getEnvId(), envManageMirrorVo.getMirrorId(), moduleUser);
          resultMap.put(envManageMirrorVo.getModuleName(), resp.getCode() == 200);
        }
      }
    }
    return resultMap;
  }
  
  /**
   * 从excel中获取发布清单 region表示CRM或者计费
   *
   * @param region
   * @param excelFile
   * @return
   */
  @Override
  public List<Map<String, Object>> fromExcelInsertModuleDeployList(String region, String userId, MultipartFile excelFile) {
    try {
      Map<String, List<String>> moduleNameMap =
          ExcelPhraseUtils.getDeployListExcelData(excelFile.getInputStream());
      List<Map<String, Object>> resultList = new ArrayList<>();
      for (String env : moduleNameMap.keySet()) {
        Map<String, Object> resultMap = new LinkedHashMap<>();
        ResponseDTO responseDTO = new ResponseDTO();
        //存放每个模块匹配的状态
        List<Map<String, String>> moduleMateList = new LinkedList<>();
        Set<String> moduleNameSet = new HashSet<>(moduleNameMap.get(env));
        //excel中去重后的模块名
        List<String> moduleNames = new ArrayList<>(moduleNameSet);
        String envName = region + "-" + env + "环境";
        //存放最终放入redis里的模块id
        List<Integer> moduleIdList = new LinkedList<>();
        //从数据库里查询的模块
        List<ModuleManage> moduleManages = new ArrayList<>();
        Integer envId = envMapper.getEnvIdByEnvName(envName);
        ModuleManageExample moduleManageExample = new ModuleManageExample();
        if (envId != null) {
          ModuleManageExample.Criteria criteria = moduleManageExample.createCriteria();
          criteria.andModuleNameIn(moduleNames).andEnvIdEqualTo(envId).andIsDeleteEqualTo(0);
          moduleManages = manageMapper.selectByExample(moduleManageExample);
        }
        if (envId == null || moduleManages.size() <= 0) {
          responseDTO.fail("环境不匹配");
          resultMap.put("env", env);
          resultMap.put("responseDTO", responseDTO);
          resultList.add(resultMap);
          continue;
        }
        for (ModuleManage moduleManage : moduleManages) {
          if (moduleManage.getEnvId() == envId) {
            if (moduleNames.contains(moduleManage.getModuleName())) {
              Map<String, String> map = new HashMap<>();
              moduleIdList.add(moduleManage.getId());
              //保留未在数据库中查询到的模块名
              moduleNames.remove(moduleManage.getModuleName());
              map.put("moduleName", moduleManage.getModuleName());
              map.put("status", "true");
              moduleMateList.add(map);
            }
          }
        }
        for (String moduleName : moduleNames) {
          Map<String, String> map = new HashMap<>();
          map.put("moduleName", moduleName);
          map.put("status", "false");
          moduleMateList.add(map);
        }
        Integer[] moduleIds = new Integer[moduleIdList.size()];
        if (insert2RedisModuleDeployList(envId, userId, moduleIdList.toArray(moduleIds))) {
          responseDTO.success(moduleMateList);
        } else {
          responseDTO.fail(moduleMateList);
        }
        resultMap.put("env", env);
        resultMap.put("responseDTO", responseDTO);
        resultList.add(resultMap);
      }
      return resultList;
    } catch (POIXMLException | IOException e) {
      log.error("excel文件解析出错");
      throw new FileStoreException("发布清单excel文件解析出错");
    }
  }
  
  /**
   * 获取发布清单中的所有模块数据并加
   *
   * @param envId
   * @param moduleId
   * @return
   */
  @Override
  public boolean judgeModuleInDeployList(Integer envId, Integer moduleId, String userId) {
    boolean success = false;
    Integer centerId = null;
    String keyName = null;
    ModuleEnv moduleEnv = envMapper.selectOne(envId);
    if (moduleEnv.getIsProd() == 1) {
      Set<ModuleEnvCenterManageVo> moduleEnvCenterManageVoList =
          getAllDeployListSelf(userId, envId, centerId, keyName);
      for (ModuleEnvCenterManageVo moduleEnvCenterManageVo : moduleEnvCenterManageVoList) {
        if (moduleEnvCenterManageVo.getModuleId() == moduleId) {
          success = true;
          break;
        }
      }
    }
    return success;
  }
  
  /**
   * 导出发布清单
   *
   * @param moduleIds
   * @return
   */
  @Override
  public File exportModuleDeployList(Integer[] moduleIds) {
    Set<Integer> moduleIdSet = new HashSet<>(Arrays.asList(moduleIds));
    List<ModuleManageDeployVO> moduleManageDeployVOS = deployMapper.selectDeployListModule(moduleIdSet);
    if (!moduleManageDeployVOS.isEmpty()) {
      Workbook wb0 = new XSSFWorkbook();
      CellStyle cellStyle = wb0.createCellStyle();
      cellStyle.setAlignment(HorizontalAlignment.CENTER);
      cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
      cellStyle.setBorderBottom(BorderStyle.THIN);
      cellStyle.setBorderLeft(BorderStyle.THIN);
      cellStyle.setBorderRight(BorderStyle.THIN);
      
      Sheet sheet = wb0.createSheet("发布清单列表");
      
      Row row = sheet.createRow(0);
      Cell cell0 = row.createCell(0);
      cell0.setCellValue("序号");
      cell0.setCellStyle(cellStyle);
      
      Cell cell1 = row.createCell(1);
      cell1.setCellValue("模块名称");
      cell1.setCellStyle(cellStyle);
      
      Cell cell2 = row.createCell(2);
      cell2.setCellValue("环境名称");
      cell2.setCellStyle(cellStyle);
      
      Cell cell3 = row.createCell(3);
      cell3.setCellValue("中心名称");
      cell3.setCellStyle(cellStyle);
      
      Cell cell4 = row.createCell(4);
      cell4.setCellValue("子中心名称");
      cell4.setCellStyle(cellStyle);
      
      Cell cell5 = row.createCell(5);
      cell5.setCellValue("负责人");
      cell5.setCellStyle(cellStyle);
      
      Cell cell6 = row.createCell(6);
      cell6.setCellValue("局方负责人");
      cell6.setCellStyle(cellStyle);
      
      int count = 1;
      
      for (int i = 0; i < moduleManageDeployVOS.size(); i++) {
        Row rowi = sheet.createRow(i + 1);
        Cell celli0 = rowi.createCell(0);
        celli0.setCellValue(count++);
        celli0.setCellStyle(cellStyle);
        
        Cell celli1 = rowi.createCell(1);
        celli1.setCellValue(moduleManageDeployVOS.get(i).getModuleName());
        celli1.setCellStyle(cellStyle);
        
        Cell celli2 = rowi.createCell(2);
        celli2.setCellValue(moduleManageDeployVOS.get(i).getEnvName());
        celli2.setCellStyle(cellStyle);
        
        Cell celli3 = rowi.createCell(3);
        celli3.setCellValue(moduleManageDeployVOS.get(i).getCenterName());
        celli3.setCellStyle(cellStyle);
        
        Cell celli4 = rowi.createCell(4);
        celli4.setCellValue(moduleManageDeployVOS.get(i).getChildCenterName());
        
        Cell celli6 = rowi.createCell(5);
        String chargePerson = moduleManageDeployVOS.get(i).getChargePerson();
        String chargeTelephone = moduleManageDeployVOS.get(i).getChargeTelephone();
        if (StringUtils.isBlank(chargePerson)) {
          chargePerson = "";
        }
        if (StringUtils.isBlank(chargeTelephone)) {
          chargeTelephone = "";
        }
        
        celli6.setCellValue(chargePerson + chargeTelephone);
        celli6.setCellStyle(cellStyle);
        
        Cell celli7 = rowi.createCell(6);
        String officalChargePerson = moduleManageDeployVOS.get(i).getOfficalChargePerson();
        String officalChargeTelephone = moduleManageDeployVOS.get(i).getOfficalChargeTelephone();
        if (StringUtils.isBlank(officalChargePerson)) {
          officalChargePerson = "";
        }
        if (StringUtils.isBlank(officalChargeTelephone)) {
          officalChargeTelephone = "";
        }
        celli7.setCellValue(officalChargePerson + officalChargeTelephone);
        celli7.setCellStyle(cellStyle);
        
      }
      
      //设置自适应样式
      manageService.setSizeColumn(sheet, 9);
      
      StringBuilder fileNameSb = new StringBuilder();
      fileNameSb.append(moduleExportPath).append("deploy_list")
          .append(DateUtils.generateDateOnlyString()).append(".xlsx");
      File file = new File(fileNameSb.toString());
      if (!file.exists()) {
        try {
          boolean b = file.createNewFile();
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
}
