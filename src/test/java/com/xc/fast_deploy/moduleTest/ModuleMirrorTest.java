package com.xc.fast_deploy.moduleTest;

import com.google.gson.Gson;
import com.xc.fast_deploy.dao.master_dao.ModuleDeployYamlMapper;
import com.xc.fast_deploy.dao.master_dao.ModuleManageMapper;
import com.xc.fast_deploy.dao.master_dao.ModuleMirrorMapper;
import com.xc.fast_deploy.model.master_model.ModuleDeployYaml;
import com.xc.fast_deploy.model.master_model.ModuleManage;
import com.xc.fast_deploy.myenum.k8sEnum.K8sKindTypeEnum;
import com.xc.fast_deploy.service.common.ModuleMirrorService;
import com.xc.fast_deploy.utils.HttpUtils;
import com.xc.fast_deploy.utils.code_utils.ExcelPhraseUtils;
import com.xc.fast_deploy.utils.k8s.K8sUtils;
import io.kubernetes.client.custom.Quantity;
import io.kubernetes.client.openapi.models.V1Container;
import io.kubernetes.client.openapi.models.V1Deployment;
import io.kubernetes.client.openapi.models.V1ResourceRequirements;
import io.kubernetes.client.util.Yaml;
import lombok.extern.slf4j.Slf4j;
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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class ModuleMirrorTest {
  
  @Autowired
  private ModuleMirrorService mirrorService;
  
  @Autowired
  private ModuleMirrorMapper mirrorMapper;
  @Autowired
  private ModuleDeployYamlMapper deployYamlMapper;
  @Autowired
  private ModuleManageMapper manageMapper;
  
  @Test
  public void getModuleMirrorInfo() {
    
    File file = new File("F:\\img\\生产POD数量清单.xlsx");
    try {
      FileInputStream inputStream = new FileInputStream(file);
      XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
      XSSFSheet sheetAt = workbook.getSheetAt(0);
      int lastRowNum = sheetAt.getLastRowNum();
      for (int i = 1; i <= lastRowNum; i++) {
        XSSFRow row = sheetAt.getRow(i);
        XSSFCell cell1 = row.getCell(1);
        String moduleName = ExcelPhraseUtils.getCellValue(cell1);
        List<ModuleManage> manageList = manageMapper.selectBYMdouleName(moduleName);
        if (manageList.size() <= 0) {
          System.err.println("找不到模块" + moduleName);
        }
        List<ModuleDeployYaml> deployYamlList =
            deployYamlMapper.selectYamlJsonByModuleId(manageList.get(0).getId());
        
        XSSFCell cell2 = row.getCell(2);
        String requestString = ExcelPhraseUtils.getCellValue(cell2);
        
        char[] chars = requestString.toCharArray();
        String requestCPU = String.valueOf(chars[0]);
        String requestMEMORY = String.valueOf(chars[2]);
        
        XSSFCell cell3 = row.getCell(3);
        String limitString = ExcelPhraseUtils.getCellValue(cell3);
        char[] chars2 = limitString.toCharArray();
        String limitCPU = String.valueOf(chars2[0]);
        String limitMEMORY = String.valueOf(chars2[2]);
        
        System.out.println(requestCPU);
        System.out.println(requestMEMORY);
        System.out.println(limitCPU);
        System.out.println(limitMEMORY);
        
        if (deployYamlList.size() > 0) {
          for (ModuleDeployYaml deployYaml : deployYamlList) {
            
            if (deployYaml.getYamlType().equals(K8sKindTypeEnum.DEPLOYMENT.getKindType())) {
              V1Deployment deployment =
                  K8sUtils.getObject(Yaml.load(deployYaml.getYamlJson()),
                      V1Deployment.class);
              
              V1Container container = deployment.getSpec().getTemplate()
                  .getSpec().getContainers().get(0);
              
              V1ResourceRequirements resourceRequirements = new V1ResourceRequirements();
              
              Map<String, Quantity> requestMap = new HashMap<>();
              requestMap.put("memory", new Quantity(requestMEMORY + "Gi"));
              requestMap.put("cpu", new Quantity(requestCPU));
              
              Map<String, Quantity> limitMap = new HashMap<>();
              limitMap.put("memory", new Quantity(limitMEMORY + "Gi"));
              limitMap.put("cpu", new Quantity(limitCPU));
              
              resourceRequirements.setLimits(limitMap);
              resourceRequirements.setRequests(requestMap);
              container.setResources(resourceRequirements);
              
              deployYaml.setYamlJson(new Gson().toJson(deployment));
              deployYamlMapper.updateByPrimaryKeySelective(deployYaml);
              break;
            }
          }
        }
        
      }
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    
  }
  
  @Test
  public void testHarborGc() {
    String url = "http://134.96.253.72:80/api/system/gc/schedule";
    String user = "admin";
    String pass = "Harbor12345";
    String json = "{  \"schedule\": {    \"type\": \"Manual\",    \"weekday\": 0,    \"offtime\": 0  }}";
    String result = HttpUtils.doPost(url, json, user, pass);
    System.out.println(result);
  }
  
  @Test
  public void testGetRepos() {
    String url = "http://134.108.3.158:80/api/repositories/pst/bss-cooperate-controller/tags/20200902152045_4f3c";
    String user = "admin";
    String pass = "Tasa!9$%";
    String result = HttpUtils.doGetHarborInfo(url, user, pass);
    System.out.println(result);
  }
  
}
