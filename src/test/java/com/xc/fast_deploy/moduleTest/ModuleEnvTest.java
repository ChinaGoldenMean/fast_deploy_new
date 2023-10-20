package com.xc.fast_deploy.moduleTest;

import com.alibaba.fastjson.JSONObject;
import com.xc.fast_deploy.dao.master_dao.*;
import com.xc.fast_deploy.model.master_model.ModuleCenter;
import com.xc.fast_deploy.model.master_model.ModuleCertificate;
import com.xc.fast_deploy.model.master_model.ModuleManage;
import com.xc.fast_deploy.utils.code_utils.ExcelPhraseUtils;
import com.xc.fast_deploy.vo.module_vo.ModuleEnvVo;
import com.xc.fast_deploy.vo.module_vo.ModuleManageDeployVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class ModuleEnvTest {
  
  @Autowired
  private ModuleEnvMapper envMapper;
  
  @Autowired
  private ModuleCertificateMapper moduleCertificateMapper;
  @Autowired
  private ModuleUserMapper userMapper;
  @Autowired
  private ModuleMirrorMapper mirrorMapper;
  @Autowired
  private ModuleDeployMapper moduleDeployMapper;
  @Autowired
  private ModuleManageMapper manageMapper;
  @Autowired
  private ModuleDeployYamlMapper deployYamlMapper;
  @Autowired
  private ModuleCenterMapper centerMapper;
  
  @Test
  public void testCer() {
    List<ModuleCertificate> allModuleCer = moduleCertificateMapper.getAllModuleCer();
    
    System.out.println("111");
  }
  
  @Test
  public void testEnv() {
//        ModuleEnv moduleEnv = envMapper.selectByPrimaryKey(1);
    ModuleEnvVo moduleEnvVo = envMapper.selectWithCertById(1);
    //    ModuleEnv moduleEnv = envMapper.selectWithBlobsByPrimaryKey(1);
    System.out.println(JSONObject.toJSONString(moduleEnvVo));
  }
  
  @Test
  public void testEnv2() throws IOException {
    List<ModuleCenter> moduleCenters = centerMapper.selectAll();
//        System.out.println(moduleCenters);
    Map<String, List<ModuleCenter>> centerMap = new HashMap<>();
    
    for (ModuleCenter moduleCenter : moduleCenters) {
      ModuleCenter center = new ModuleCenter();
      center.setCenterPath(moduleCenter.getCenterPath());
      center.setChildCenterName(moduleCenter.getChildCenterName());
      center.setChildCenterContentName(moduleCenter.getChildCenterContentName());
      center.setCenterName(moduleCenter.getCenterName());
      center.setCenterCode(moduleCenter.getCenterCode());
      if (!centerMap.containsKey(moduleCenter.getChildCenterName())) {
        List<ModuleCenter> centers = new ArrayList<>();
        centers.add(center);
        centerMap.put(moduleCenter.getChildCenterName(), centers);
      } else {
        centerMap.get(moduleCenter.getChildCenterName()).add(center);
      }
    }
    
    System.out.println(JSONObject.toJSONString(centerMap));
    
  }
  
  @Test
  public void testGetEnv3() throws IOException {
    List<ModuleManageDeployVO> manageDeployVOS = manageMapper.selectAllModuleByEnvId(30);
    FileInputStream inputStream = new FileInputStream(new File("F:\\img\\智慧BSS3.0 CRM域生产环境pod统计 - 201901022.xlsx"));
    XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
//        Set<String> podNamePreSet = new HashSet<>();
    Map<String, ModuleManage> podNamePreMap = new HashMap<>();
    XSSFSheet sheet = workbook.getSheetAt(0);
    int lastRowNum = sheet.getLastRowNum();
    for (int i = 1; i < 249; i++) {
      XSSFRow row = sheet.getRow(i);
      XSSFCell cell = row.getCell(4);
      XSSFCell cell1 = row.getCell(2);
      String cellValue = ExcelPhraseUtils.getCellValue(cell);
      String cellValue1 = ExcelPhraseUtils.getCellValue(cell1);
      
      XSSFCell rowCell = row.getCell(5);
      XSSFCell rowCell1 = row.getCell(6);
      XSSFCell rowCell7 = row.getCell(7);
      XSSFCell rowCell8 = row.getCell(8);
      
      String cellValue2 = ExcelPhraseUtils.getCellValue(rowCell);
      String cellValue3 = ExcelPhraseUtils.getCellValue(rowCell1);
      String ywValue = ExcelPhraseUtils.getCellValue(rowCell7);
      String reValue = ExcelPhraseUtils.getCellValue(rowCell8);
      
      ModuleManage moduleManage = new ModuleManage();
      moduleManage.setModuleContentName(cellValue);
      moduleManage.setModuleName(cellValue1);
      
      if (StringUtils.isNotBlank(cellValue2)) {
        String[] split = cellValue2.split("\n");
        if (split.length > 0) {
          StringBuilder sb1 = new StringBuilder();
          StringBuilder sb2 = new StringBuilder();
          for (String personInfo : split) {
            String[] strings = personInfo.split("\\d");
            List<String> asList = Arrays.asList(strings);
            String s1 = asList.get(0);
            String s2 = personInfo.replace(s1, "");
            sb1.append(s1).append("/");
            sb2.append(s2).append("/");
          }
          
          moduleManage.setChargePerson(sb1.delete(sb1.length() - 1, sb1.length()).toString());
          moduleManage.setChargeTelephone(sb2.delete(sb2.length() - 1, sb2.length()).toString());
        }
      }
      
      if (StringUtils.isNotBlank(cellValue3)) {
        String[] split = cellValue3.split("\n");
        if (split.length > 0) {
          StringBuilder sb1 = new StringBuilder();
          StringBuilder sb2 = new StringBuilder();
          for (String personInfo : split) {
            String[] strings = personInfo.split("\\d");
            List<String> asList = Arrays.asList(strings);
            String s1 = asList.get(0);
            String s2 = personInfo.replace(s1, "");
            sb1.append(s1).append("/");
            sb2.append(s2).append("/");
          }
          moduleManage.setOfficalChargePerson(sb1.delete(sb1.length() - 1, sb1.length()).toString());
          moduleManage.setOfficalChargeTelephone(sb2.delete(sb2.length() - 1, sb2.length()).toString());
        }
      }
      
      moduleManage.setRemark(reValue);
      moduleManage.setRemarkYw(ywValue);
      podNamePreMap.put(cellValue, moduleManage);
    }
    
    Map<String, ModuleManageDeployVO> yamlNameMap = new HashMap<>();
    System.out.println(manageDeployVOS.size());
    for (ModuleManageDeployVO manageDeployVO : manageDeployVOS) {
      yamlNameMap.put(manageDeployVO.getYamlName(), manageDeployVO);
    }
    System.out.println(yamlNameMap.size());
    List<String> notOkList = new ArrayList<>();
    for (String podNamePre : podNamePreMap.keySet()) {
      System.out.println("开始匹配: " + podNamePre);
      if (yamlNameMap.containsKey(podNamePre)) {
        System.out.println("匹配成功: " + podNamePre);
        ModuleManageDeployVO manageDeployVO = yamlNameMap.get(podNamePre);
        ModuleManage moduleManage = podNamePreMap.get(podNamePre);

//                moduleManage.setModuleName(null);
        moduleManage.setModuleContentName(null);
        moduleManage.setId(manageDeployVO.getModuleId());

//                if (!moduleManage.getChargePerson().contains(manageDeployVO.getChargePerson())
//                        || !moduleManage.getOfficalChargePerson().contains(manageDeployVO.getOfficalChargePerson())) {
//                    StringBuilder sb = new StringBuilder();
//
//
//                    notOkList.add(sb.toString());
//                }
        manageMapper.updateByPrimaryKeySelective(moduleManage);
//                System.out.println(JSONObject.toJSONString(moduleManage));
      } else {
        System.err.println("匹配失败: " + podNamePre);
      }
    }
    
    System.out.println(JSONObject.toJSONString(podNamePreMap));
    
  }
}
