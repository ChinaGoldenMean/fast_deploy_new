package com.xc.fast_deploy.moduleTest;

import com.alibaba.fastjson.JSONObject;
import com.xc.fast_deploy.dao.master_dao.ModuleDeployMapper;
import com.xc.fast_deploy.dao.master_dao.ModuleManageMapper;
import com.xc.fast_deploy.dao.master_dao.ModulePackageMapper;
import com.xc.fast_deploy.dto.MyPageInfo;
import com.xc.fast_deploy.dto.CodeUpdateInfoDTO;
import com.xc.fast_deploy.dto.module.ModuleManageDTO;
import com.xc.fast_deploy.model.master_model.ModuleManage;
import com.xc.fast_deploy.model.master_model.ModulePackage;
import com.xc.fast_deploy.vo.module_vo.ModuleManageDeployVO;
import com.xc.fast_deploy.vo.module_vo.ModuleManageUpdateVo;
import com.xc.fast_deploy.vo.module_vo.ModuleMangeVo;
import com.xc.fast_deploy.myenum.ModuleTypeEnum;
import com.xc.fast_deploy.service.common.*;
import com.xc.fast_deploy.vo.module_vo.param.ModuleManageSelectParamVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.*;
import java.util.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class FristModuleTest {
  
  @Autowired
  private ModuleManageMapper manageMapper;
  @Autowired
  private ModuleCertificateService certificateService;
  
  @Autowired
  private ModuleCenterService catalogService;
  @Autowired
  private ModuleUserService userService;
  @Autowired
  private ModuleManageService manageService;
  @Autowired
  private ModulePackageMapper packageMapper;
  @Autowired
  private ModulePackageService packageService;
  
  /**
   * 测试添加模块
   */
  @Test
  public void testInsertModule() {
    ModuleManage moduleManage = new ModuleManage();
    moduleManage.setCertificateId(0);
    moduleManage.setModuleName("订单查询");
    moduleManage.setUpdateTime(new Date());
    moduleManage.setCreateTime(new Date());
    moduleManage.setCenterId(1);
    moduleManage.setModuleContentName("order_query");
    
    moduleManage.setModuleType(ModuleTypeEnum.SVN_SOURCE_CODE.getModuleTypeCode());
    
    int i = manageMapper.insertSelective(moduleManage);
    
    System.out.println(moduleManage.getId());
    System.out.println(i);
  }
  
  @Test
  public void testModuleCertificate() {
//        PageInfo<ModuleUser> pageInfo = userService.selectPageAll(1, 3);
//        ResponseDTO<MyPageInfo<ModuleMangeVo>> responseDTO = manageService.getAllModuleDTO(1, 1);
    ModuleManageSelectParamVo manageSelectParamVo = new ModuleManageSelectParamVo();
    MyPageInfo<ModuleMangeVo> myPageInfo = manageService.getAllModuleDTOBySelect(1, 10, manageSelectParamVo);
    System.out.println(JSONObject.toJSONString(myPageInfo));
  }
  
  @Test
  public void testMyOwn() {
    List<ModulePackage> list = new ArrayList<>();
    ModulePackage modulePackage1 = new ModulePackage();
    ModulePackage modulePackage2 = new ModulePackage();
    ModulePackage modulePackage3 = new ModulePackage();
    ModulePackage modulePackage4 = new ModulePackage();
    modulePackage1.setModuleId(3);
    modulePackage1.setPackagePathName("fdsfsadf");
    modulePackage1.setCreateTime(new Date());
    modulePackage2.setModuleId(3);
    modulePackage2.setPackagePathName("fdsfsadf");
    modulePackage2.setCreateTime(new Date());
    modulePackage3.setModuleId(3);
    modulePackage3.setPackagePathName("fdsfsadf");
    modulePackage3.setCreateTime(new Date());
    
    modulePackage4.setModuleId(3);
    modulePackage4.setPackagePathName("fdsfsadf");
    modulePackage4.setCreateTime(new Date());
    
    list.add(modulePackage1);
    list.add(modulePackage2);
    list.add(modulePackage3);
    list.add(modulePackage4);
    packageService.insertAll(list);
  }
  
  @Test
  public void testGetInfo() {

//        List<ModuleManageDeployVO> manageDeployVOList = manageMapper.selectAllModuleByEnvId(30);
//        System.out.println(moduleManages);
    List<ModuleManage> moduleManages = manageMapper.selectAll();
    Map<String, Map<String, List<String>>> map = new HashMap<>();
    for (ModuleManage moduleManage : moduleManages) {
      String moduleName = moduleManage.getModuleName();
      String envCode = moduleManage.getEnvCode();
      if (!map.containsKey(moduleName)) {
        Map<String, List<String>> map1 = new HashMap<>();
        if (!map1.containsKey(envCode)) {
          List<String> strings = new ArrayList<>();
          strings.add(moduleManage.getModuleContentName());
          map1.put(envCode, strings);
        } else {
          map1.get(envCode).add(moduleManage.getModuleContentName());
        }
        map.put(moduleName, map1);
      } else {
        Map<String, List<String>> listMap = map.get(moduleName);
        if (!listMap.containsKey(envCode)) {
          List<String> strings = new ArrayList<>();
          strings.add(moduleManage.getModuleContentName());
          listMap.put(envCode, strings);
        } else {
          listMap.get(envCode).add(moduleManage.getModuleContentName());
        }
      }
    }
    
    Set<String> set = map.keySet();
    for (String moduleName : set) {
      Map<String, List<String>> listMap = map.get(moduleName);
      if (listMap != null && listMap.size() > 0) {
        Set<String> keySet = listMap.keySet();
        String key1 = null;
        String key2 = null;
        for (String envCode : keySet) {
          if ("crm-prod".equals(envCode)) {
            key1 = listMap.get(envCode).get(0);
          }
          if ("crm-pst".equals(envCode)) {
            key2 = listMap.get(envCode).get(0);
          }
        }
        if (StringUtils.isNotBlank(key1) && StringUtils.isNotBlank(key2)) {
          if (!key1.equals(key2)) {
            System.out.println("模块名称: " + moduleName +
                " key1: " + key1 + " key2: " + key2);
          }
        }
      }
    }

//        ModuleManageDTO manageDTO = manageService.selectInfoById(263);
//        System.out.println(JSONObject.toJSONString(manageDTO));
  }
  
  @Test
  public void testUpdateInfo() {
    ModuleManageUpdateVo updateVo = new ModuleManageUpdateVo();
    updateVo.setModuleId("8");
    updateVo.setCenterId("3");
    updateVo.setModuleType("0");
    updateVo.setCertificateId("2");
    updateVo.setModuleName("订单查询111");
    boolean b = manageService.updateInfo(updateVo, null, null);
  }
  
  @Test
  public void testDeleteManageInfo() {
    Workbook wb0 = new XSSFWorkbook();
    CellStyle cellStyle = wb0.createCellStyle();
    cellStyle.setAlignment(HorizontalAlignment.CENTER);
    cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
    cellStyle.setBorderBottom(BorderStyle.THIN);
    cellStyle.setBorderLeft(BorderStyle.THIN);
    cellStyle.setBorderRight(BorderStyle.THIN);
    
    List<ModuleManageDeployVO> deployVOS = manageMapper.selectAllModuleByEnvId(34);
    Sheet sheet = wb0.createSheet(deployVOS.get(0).getEnvName());
    Row row = sheet.createRow(0);
    Cell cell0 = row.createCell(0);
    cell0.setCellValue("序号");
    cell0.setCellStyle(cellStyle);
    
    Cell cell1 = row.createCell(1);
    cell1.setCellValue("中心名称");
    cell1.setCellStyle(cellStyle);
    
    Cell cell2 = row.createCell(2);
    cell2.setCellValue("子中心名称");
    cell2.setCellStyle(cellStyle);
    
    Cell cell3 = row.createCell(3);
    cell3.setCellValue("模块名称");
    cell3.setCellStyle(cellStyle);
    
    Cell cell4 = row.createCell(4);
    cell4.setCellValue("pod数目");
    cell4.setCellStyle(cellStyle);
    
    Cell cell5 = row.createCell(5);
    cell5.setCellValue("pod前缀");
    cell5.setCellStyle(cellStyle);
    
    Cell cell6 = row.createCell(6);
    cell6.setCellValue("负责人");
    cell6.setCellStyle(cellStyle);
    
    Cell cell7 = row.createCell(7);
    cell7.setCellValue("局方负责人");
    cell7.setCellStyle(cellStyle);
    int count = 0;
    for (int i = 0; i < deployVOS.size(); i++) {
      Row rowi = sheet.createRow(i + 1);
      Cell celli0 = rowi.createCell(0);
      celli0.setCellValue(count++);
      celli0.setCellStyle(cellStyle);
      
      Cell celli1 = rowi.createCell(1);
      celli1.setCellValue(deployVOS.get(i).getCenterName());
      celli1.setCellStyle(cellStyle);
      
      Cell celli2 = rowi.createCell(2);
      celli2.setCellValue(deployVOS.get(i).getChildCenterName());
      celli2.setCellStyle(cellStyle);
      
      Cell celli3 = rowi.createCell(3);
      celli3.setCellValue(deployVOS.get(i).getModuleName());
      celli3.setCellStyle(cellStyle);
      
      Cell celli4 = rowi.createCell(4);
      celli4.setCellValue(4);
      celli4.setCellStyle(cellStyle);
      
      Cell celli5 = rowi.createCell(5);
      celli5.setCellValue(deployVOS.get(i).getYamlName());
      celli5.setCellStyle(cellStyle);
      
      Cell celli6 = rowi.createCell(6);
      celli6.setCellValue(deployVOS.get(i).getChargePerson() + deployVOS.get(i).getChargeTelephone());
      celli6.setCellStyle(cellStyle);
      
      Cell celli7 = rowi.createCell(7);
      celli7.setCellValue(deployVOS.get(i).getOfficalChargePerson() +
          deployVOS.get(i).getOfficalChargeTelephone());
      celli7.setCellStyle(cellStyle);
    }
    File file = new File("f:/img/module_file.xlsx");
    if (!file.exists()) {
      try {
        boolean b = file.createNewFile();
        FileOutputStream fos = new FileOutputStream(file);
        wb0.write(fos);
        fos.close();
        log.info("生成excel文件成功: {}", file.getName());
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    
  }
  
  @Autowired
  private ModuleDeployService deployService;
  
  @Test
  public void testGetPackageInfo() throws IOException {
    List<String> yamlList = new ArrayList<>();
    File file = new File("F:\\home\\crm-prod.txt");
    FileInputStream in = new FileInputStream(file);
    BufferedReader reader = new BufferedReader(new InputStreamReader(in, "utf-8"));
    while (reader.ready()) {
      String s = reader.readLine();
      if (StringUtils.isNotBlank(s)) {
        String[] split = s.split("\\|");
        yamlList.add(split[1].trim());
      }
    }
    reader.close();
    
    List<String> deploymentList = new ArrayList<>();
    File file2 = new File("F:\\home\\temp.txt");
    FileInputStream in2 = new FileInputStream(file2);
    BufferedReader reader2 = new BufferedReader(new InputStreamReader(in2, "utf-8"));
    while (reader2.ready()) {
      String s = reader2.readLine();
      deploymentList.add(s);
    }
    reader2.close();
    
    System.out.println(yamlList);
    System.out.println(deploymentList);
    for (String deploymentName : deploymentList) {
      if (!yamlList.contains(deploymentName)) {
        System.out.println("不存在: " + deploymentName);
      }
    }
  }
  
}

