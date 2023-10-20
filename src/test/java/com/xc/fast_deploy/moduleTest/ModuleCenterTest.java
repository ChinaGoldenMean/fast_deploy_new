package com.xc.fast_deploy.moduleTest;

import com.alibaba.fastjson.JSONObject;
import com.xc.fast_deploy.dao.master_dao.*;
import com.xc.fast_deploy.model.master_model.*;
import com.xc.fast_deploy.model.master_model.example.*;
import com.xc.fast_deploy.rediscache.JedisManage;
import com.xc.fast_deploy.vo.module_vo.ModuleManageDeployVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class ModuleCenterTest {
  
  @Autowired
  private ModuleCenterMapper centerMapper;
  @Autowired
  private ModuleManageMapper manageMapper;
  @Autowired
  private ModulePackageMapper packageMapper;
  @Autowired
  private ModuleJobMapper moduleJobMapper;
  @Autowired
  private ModuleDeployMapper deployMapper;
  @Autowired
  private ModuleDeployYamlMapper deployYamlMapper;
  
  @Resource
  private JedisManage jedisManage;

//    @Test
//    public void testJedis() throws ApiException, IOException {
////        List<String> stringList = jedisManage.keysGet(10);
////        for (String key : stringList) {
////            System.out.println("key: " + key + " value: " + jedisManage.get(key));
////        }
//
////        CoreV1Api coreV1Api = K8sManagement.getCoreV1Api(K8sUtils.PST);
//
//        V1Pod v1Pod = coreV1Api.readNamespacedPod("bss-order-rule-dispatcher-57c754f8b7-2nfgg",
//                K8sNameSpace.DEFAULT, null, null, null);
//
//        Copy copy = new Copy();
//        String source = "/app/dubbo/logs/bss-order-rule-dispatcher-2019-10-19.log";
////        InputStream inputStream = copy.copyFileFromPod(v1Pod, "bss-order-rule-dispatcher", source);
////        File file = new File("f:/img/test");
////        FileOutputStream outputStream = new FileOutputStream(file);
//        Path path = Paths.get("f:/img/", "testsss");
//
//
//        copy.copyDirectoryFromPod(v1Pod, "bss-order-rule-dispatcher", "/app/dubbo/logs", path);
////        byte[] bytes = new byte[1024];
////        int byteCount = 0;
////        while ((byteCount = inputStream.read(bytes)) != -1) {
////            outputStream.write(bytes, 0, byteCount);
////            outputStream.flush();
////        }
////        outputStream.close();
////        inputStream.close();
//
//    }
  
  @Test
  public void testUpdateModuleManageByCenter() throws FileNotFoundException {
//        ModuleManage moduleManage = manageMapper.selectByPrimaryKey(438);
//        System.out.println(JSONObject.toJSONString(moduleManage));
//        moduleManage.setCenterId(189);
//        moduleManage.setShPath(moduleManage.getShPath().replace("cpcp-service", "cpc-center-ablity"));
//        int i = manageMapper.updateByPrimaryKeySelective(moduleManage);
//        ModuleManageSelectParamVo manageSelectParamVo = new ModuleManageSelectParamVo();
//        manageSelectParamVo.setEnvId(34);
//        manageSelectParamVo.setCenterId();
//        manageMapper.selectModuleVoPageByVo()
//        Date date = new Date();
//        System.out.println(date.getTime());
//        date.setTime(1570901480000L);

//        date.setTime(1570930659720L);
//        System.out.println(date.toLocaleString());
    Map<String, List<ModuleManageDeployVO>> deployVOMap = new HashMap<>();
    List<ModuleManageDeployVO> manageDeployVOList = manageMapper.selectAllModuleByEnvId(34);
    for (ModuleManageDeployVO deployVo : manageDeployVOList) {
      if (!deployVOMap.containsKey(deployVo.getCenterName())) {
        List<ModuleManageDeployVO> deployVOList = new ArrayList<>();
        deployVOList.add(deployVo);
        deployVOMap.put(deployVo.getCenterName(), deployVOList);
      } else {
        deployVOMap.get(deployVo.getCenterName()).add(deployVo);
      }
    }
    
    Workbook wb0 = new XSSFWorkbook();
    CellStyle cellStyle = wb0.createCellStyle();
    cellStyle.setAlignment(HorizontalAlignment.CENTER);
    cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
    cellStyle.setBorderBottom(BorderStyle.THIN);
    cellStyle.setBorderLeft(BorderStyle.THIN);
    cellStyle.setBorderRight(BorderStyle.THIN);
    Set<String> centerNameSet = deployVOMap.keySet();
    for (String key : centerNameSet) {
      Sheet sheet = wb0.createSheet(key);
      Row row = sheet.createRow(0);
      Cell cell0 = row.createCell(0);
      cell0.setCellValue("序号");
      cell0.setCellStyle(cellStyle);
      
      Cell cell1 = row.createCell(1);
      cell1.setCellValue("模块名称");
      cell1.setCellStyle(cellStyle);
      
      Cell cell2 = row.createCell(2);
      cell2.setCellValue("deployment_name");
      cell2.setCellStyle(cellStyle);
      
      Cell cell3 = row.createCell(3);
      cell3.setCellValue("子中心名称");
      cell3.setCellStyle(cellStyle);
      
      List<ModuleManageDeployVO> deployVOList = deployVOMap.get(key);
      int count = 1;
      for (int i = 0; i < deployVOList.size(); i++) {
        Row rowi = sheet.createRow(i + 1);
        
        Cell celli0 = rowi.createCell(0);
        celli0.setCellValue(count++);
        celli0.setCellStyle(cellStyle);
        
        Cell celli1 = rowi.createCell(1);
        celli1.setCellValue(deployVOList.get(i).getModuleName());
        celli1.setCellStyle(cellStyle);
        
        Cell celli2 = rowi.createCell(2);
        celli2.setCellValue(deployVOList.get(i).getYamlName());
        celli2.setCellStyle(cellStyle);
        
        Cell celli3 = rowi.createCell(3);
        celli3.setCellValue(deployVOList.get(i).getChildCenterName());
        celli3.setCellStyle(cellStyle);
      }
      setSizeColumn(sheet, 8);
    }
    
    File file = new File("f:/img/crm-gray-test.xlsx");
    
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
    
    System.out.println(JSONObject.toJSONString(deployVOMap));
    
  }
  
  @Test
  public void testCenter() {
    boolean updateOrNot = true;
    Integer centerId = 169;
    ModuleCenter moduleCenter = centerMapper.selectByPrimaryKey(centerId);
    String srcCenterPath = moduleCenter.getCenterPath();
    moduleCenter.setCenterPath("crm-sit37/bas-service");
    moduleCenter.setChildCenterContentName("bas-service");
    if (updateOrNot) {
      centerMapper.updateByPrimaryKeySelective(moduleCenter);
    }
    ModuleManageExample manageExample = new ModuleManageExample();
    ModuleManageExample.Criteria criteria = manageExample.createCriteria();
    criteria.andCenterIdEqualTo(centerId).andIsDeleteEqualTo(0);
    List<ModuleManage> moduleManages = manageMapper.selectByExample(manageExample);
    for (ModuleManage moduleManage : moduleManages) {
      if (StringUtils.isNotBlank(moduleManage.getShPath())) {
        moduleManage.setShPath(moduleManage.getShPath().replace(
            srcCenterPath, moduleCenter.getCenterPath()));
        System.out.println(moduleManage.getShPath());
        if (updateOrNot) {
          manageMapper.updateByPrimaryKeySelective(moduleManage);
        }
      }
      ModulePackageExample packageExample = new ModulePackageExample();
      ModulePackageExample.Criteria packageExampleCriteria = packageExample.createCriteria();
      packageExampleCriteria.andModuleIdEqualTo(moduleManage.getId());
      List<ModulePackage> modulePackages = packageMapper.selectByExample(packageExample);
      for (ModulePackage modulePackage : modulePackages) {
        if (StringUtils.isNotBlank(modulePackage.getPackagePathName())) {
          modulePackage.setPackagePathName(modulePackage.getPackagePathName().replace(
              srcCenterPath, moduleCenter.getCenterPath()));
          System.out.println(modulePackage.getPackagePathName());
          if (updateOrNot) {
            packageMapper.updateByPrimaryKeySelective(modulePackage);
          }
        }
      }
      ModuleJobExample moduleJobExample = new ModuleJobExample();
      ModuleJobExample.Criteria jobExampleCriteria = moduleJobExample.createCriteria();
      jobExampleCriteria.andModuleIdEqualTo(moduleManage.getId()).andIsDeleteEqualTo(0);
      List<ModuleJob> moduleJobs = moduleJobMapper.selectByExample(moduleJobExample);
      
      if (moduleJobs != null && moduleJobs.size() > 0
          && StringUtils.isNotBlank(moduleJobs.get(0).getDockerfilePath())) {
        ModuleJob moduleJob = moduleJobs.get(0);
        moduleJob.setDockerfilePath(moduleJob.getDockerfilePath().replace(
            srcCenterPath, moduleCenter.getCenterPath()));
        System.out.println(moduleJob.getDockerfilePath());
        if (updateOrNot) {
          moduleJobMapper.updateByPrimaryKeySelective(moduleJob);
        }
      }
      ModuleDeployExample moduleDeployExample = new ModuleDeployExample();
      ModuleDeployExample.Criteria deployExampleCriteria = moduleDeployExample.createCriteria();
      deployExampleCriteria.andModuleIdEqualTo(moduleManage.getId()).andIsDeleteEqualTo(0);
      List<ModuleDeploy> moduleDeploys = deployMapper.selectByExample(moduleDeployExample);
      if (moduleDeploys != null && moduleDeploys.size() > 0 && moduleDeploys.get(0) != null) {
        ModuleDeploy moduleDeploy = moduleDeploys.get(0);
        ModuleDeployYamlExample deployYamlExample = new ModuleDeployYamlExample();
        ModuleDeployYamlExample.Criteria yamlExampleCriteria = deployYamlExample.createCriteria();
        yamlExampleCriteria.andDeployIdEqualTo(moduleDeploy.getId());
        List<ModuleDeployYaml> deployYamlList = deployYamlMapper.selectByExample(deployYamlExample);
        if (deployYamlList != null && deployYamlList.size() > 0) {
          for (ModuleDeployYaml moduleDeployYaml : deployYamlList) {
            String yamlPath = moduleDeployYaml.getYamlPath();
            if (StringUtils.isNotBlank(yamlPath)) {
              moduleDeployYaml.setYamlPath(yamlPath.replace(
                  srcCenterPath, moduleCenter.getCenterPath()));
              System.out.println(moduleDeployYaml.getYamlPath());
              if (updateOrNot) {
                deployYamlMapper.updateByPrimaryKeySelective(moduleDeployYaml);
              }
            }
          }
        } else {
          System.err.println(moduleManage.getModuleName() + "YAML数据EERRRRRROEOROROEOROERO");
        }
      } else {
        System.err.println(moduleManage.getModuleName() + "发布数据EERRRRRROEOROROEOROERO");
      }
    }
  }
  
  @Test
//    public void test77() {
//        List<ModuleManageDeployVO> manageDeployVOList = manageMapper.selectAllModuleByEnvId(25);
//        System.out.println(manageDeployVOList.size());
//        Map<String, ModuleManageDeployVO> deployVOMap = new HashMap<>();
//        for (ModuleManageDeployVO deployVO : manageDeployVOList) {
//            deployVOMap.put(deployVO.getYamlName(), deployVO);
//        }
//        System.out.println(deployVOMap.size());
//        ExtensionsV1beta1Api extensionApi = K8sManagement.getExtensionApi(K8sUtils.PST);
//        Workbook wb0 = new XSSFWorkbook();
//        CellStyle cellStyle = wb0.createCellStyle();
//        cellStyle.setAlignment(HorizontalAlignment.CENTER);
//        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
//        cellStyle.setBorderBottom(BorderStyle.THIN);
//        cellStyle.setBorderLeft(BorderStyle.THIN);
//        cellStyle.setBorderRight(BorderStyle.THIN);
//
//        Sheet sheet = wb0.createSheet("CRM-PST");
//
//        Row row = sheet.createRow(0);
//        Cell cell0 = row.createCell(0);
//        cell0.setCellValue("序号");
//        cell0.setCellStyle(cellStyle);
//
//        Cell cell1 = row.createCell(1);
//        cell1.setCellValue("yamlname");
//        cell1.setCellStyle(cellStyle);
//
//        Cell cell2 = row.createCell(2);
//        cell2.setCellValue("模块名称");
//        cell2.setCellStyle(cellStyle);
//
//        Cell cell3 = row.createCell(3);
//        cell3.setCellValue("nodeselector");
//        cell3.setCellStyle(cellStyle);
//
//
//        try {
//            ExtensionsV1beta1DeploymentList deploymentList =
//                    extensionApi.listNamespacedDeployment(K8sNameSpace.DEFAULT, null, null,
//                            null, null, null, null, null,
//                            null, null);
//            List<ExtensionsV1beta1Deployment> listItems = deploymentList.getItems();
//            System.out.println(listItems.size());
//            int count = 1;
//            for (int i = 0; i < listItems.size(); i++) {
//                String name = listItems.get(i).getMetadata().getName();
//
//
//                Row rowi = sheet.createRow(i + 1);
//                Cell celli0 = rowi.createCell(0);
//                celli0.setCellValue(count++);
//                celli0.setCellStyle(cellStyle);
//
//                Cell celli1 = rowi.createCell(1);
//                celli1.setCellValue(name);
//                celli1.setCellStyle(cellStyle);
//
//                Cell celli2 = rowi.createCell(2);
//                if (deployVOMap.containsKey(name)) {
//                    System.out.println(name + "---" + deployVOMap.get(name).getModuleName() +
//                            " nodeSelector: " + listItems.get(i).getSpec().getTemplate().getSpec().getNodeSelector());
//                    celli2.setCellValue(deployVOMap.get(name).getModuleName());
//                    celli2.setCellStyle(cellStyle);
//                } else {
//                    System.out.println(name + "---模块不存在 nodeSelector: "
//                            + listItems.get(i).getSpec().getTemplate().getSpec().getNodeSelector());
//                    celli2.setCellValue("未配置模块");
//                    celli2.setCellStyle(cellStyle);
//                }
//
//
//                Cell celli3 = rowi.createCell(3);
//                Map<String, String> nodeSelector = listItems.get(i).getSpec().getTemplate().getSpec().getNodeSelector();
//                if (nodeSelector != null) {
//
//                    celli3.setCellValue(nodeSelector.toString());
//                } else {
//                    celli3.setCellValue("无标签配置");
//                }
//                celli3.setCellStyle(cellStyle);
//            }
//
//            setSizeColumn(sheet, 8);
//
//            File file = new File("f:/img/crm-pst.xlsx");
//            if (!file.exists()) {
//                try {
//                    boolean b = file.createNewFile();
//                    FileOutputStream fos = new FileOutputStream(file);
//                    wb0.write(fos);
//                    fos.close();
//                    log.info("生成excel文件成功: {}", file.getName());
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//
//        } catch (ApiException e) {
//            e.printStackTrace();
//        }
//    }
  
  private void setSizeColumn(Sheet sheet, int size) {
    for (int columnNum = 0; columnNum < size; columnNum++) {
      int columnWidth = sheet.getColumnWidth(columnNum) / 256;
      for (int rowNum = 0; rowNum < sheet.getLastRowNum(); rowNum++) {
        Row currentRow;
        //当前行未被使用过
        if (sheet.getRow(rowNum) == null) {
          currentRow = sheet.createRow(rowNum);
        } else {
          currentRow = sheet.getRow(rowNum);
        }
        
        if (currentRow.getCell(columnNum) != null) {
          Cell currentCell = currentRow.getCell(columnNum);
          if (currentCell.getCellType() == XSSFCell.CELL_TYPE_STRING) {
            int length = currentCell.getStringCellValue().getBytes().length;
            if (columnWidth < length) {
              columnWidth = length;
            }
          }
        }
      }
      sheet.setColumnWidth(columnNum, columnWidth * 256);
    }
  }
  
}
