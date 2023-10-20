package com.xc.fast_deploy.utils.code_utils;

import com.xc.fast_deploy.dto.module.ModuleDeployNeedDTO;
import com.xc.fast_deploy.model.master_model.ModuleDeployNeed;
import com.xc.fast_deploy.model.master_model.ModuleUser;
import com.xc.fast_deploy.model.master_model.PModuleUser;
import com.xc.fast_deploy.myException.ValidateExcetion;
import com.xc.fast_deploy.utils.FoldUtils;
import com.xc.fast_deploy.vo.module_vo.param.ModuleManageParamVo;
import com.xc.fast_deploy.vo.module_vo.param.ModulePackageParamVo;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class ExcelPhraseUtils {
  
  //解析excel模板文件并拿到文件数据集合
  public static List<ModulePackageParamVo> getAllExcelData(InputStream inputStream) throws IOException {
    XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
    //根据模板只处理第一个sheet中的数据
    XSSFSheet sheet = workbook.getSheetAt(0);
    List<ModulePackageParamVo> packageList = new ArrayList<>();
    int count = 0;
    Set<String> paramSet1 = new HashSet<>();
    Set<String> paramSet2 = new HashSet<>();
    for (int i = 6; i < sheet.getLastRowNum(); i++) {
      XSSFRow row = sheet.getRow(i);
      XSSFCell cell1 = row.getCell(1);
      XSSFCell cell2 = row.getCell(4);
      if (cell1 == null || cell2 == null) {
        break;
      } else {
        String codeUrl = getCellValue(cell1);
        String svnContent = getCellValue(cell2);
        //在这里判断package的contentName是否符合标准
        if (StringUtils.isNotBlank(codeUrl) &&
            StringUtils.isNotBlank(svnContent) &&
            FoldUtils.judgeContent(svnContent)) {
          ModulePackageParamVo paramVo = new ModulePackageParamVo();
          paramVo.setContentName(svnContent);
          paramVo.setCodeUrl(codeUrl);
          paramSet1.add(codeUrl);
          paramSet2.add(svnContent);
          count++;
          //验证svn_url和svn_content的重复性
          if (paramSet1.size() != count) {
            throw new ValidateExcetion("codeUrl 有重复的存在: " + codeUrl);
          }
          if (paramSet2.size() != count) {
            throw new ValidateExcetion("contentName 有重复的存在: " + svnContent);
          }
          packageList.add(paramVo);
        } else {
          break;
        }
      }
      
    }
    paramSet1.clear();
    paramSet2.clear();
    return packageList;
  }
  
  /*

  解析excel中的数据形成参数
   */
  public static List<ModuleManageParamVo> getModuleManageData(InputStream inputStream) throws IOException {
    List<ModuleManageParamVo> moduleManageParamVos = new ArrayList<>();
    XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
    int numberOfSheets = workbook.getNumberOfSheets();
    for (int i = 0; i < numberOfSheets; i++) {
      ModuleManageParamVo moduleManageParamVo = new ModuleManageParamVo();
      XSSFSheet sheet = workbook.getSheetAt(i);
      if (sheet != null) {
        XSSFRow row1 = sheet.getRow(1);
        String moduleName = getCellValue(row1.getCell(1));
        String centerName = getCellValue(row1.getCell(3));
        String moduleContentName = getCellValue(row1.getCell(5));
        
        XSSFRow row2 = sheet.getRow(2);
        String officalChargePerson = getCellValue(row2.getCell(1));
        String officalChargeTelephone = getCellValue(row2.getCell(5));
        
        XSSFRow row3 = sheet.getRow(3);
        String chargePerson = getCellValue(row3.getCell(1));
        String chargeTelephone = getCellValue(row3.getCell(5));
        
        List<ModulePackageParamVo> objects = new ArrayList<>();
        Set<String> paramSet1 = new HashSet<>();
        for (int j = 5; j <= sheet.getLastRowNum(); j++) {
          XSSFRow row = sheet.getRow(j);
          XSSFCell cell1 = row.getCell(1);
          XSSFCell cell2 = row.getCell(4);
          if (cell1 == null || cell2 == null) {
            break;
          }
          String codeUrl = getCellValue(cell1);
          String svnContent = getCellValue(cell2);
          if (StringUtils.isNotBlank(codeUrl)
              && StringUtils.isNotBlank(svnContent)
              && FoldUtils.judgeContent(svnContent)) {
            if (!paramSet1.contains(codeUrl)) {
              ModulePackageParamVo packageParamVo = new ModulePackageParamVo();
              packageParamVo.setCodeUrl(codeUrl);
              packageParamVo.setContentName(svnContent);
              paramSet1.add(codeUrl);
              objects.add(packageParamVo);
            } else {
              log.info("codeUrl 有重复的存在:{}", codeUrl);
            }
          }
        }
        moduleManageParamVo.setModuleName(moduleName);
        moduleManageParamVo.setChargePerson(chargePerson);
        moduleManageParamVo.setModuleContentName(moduleContentName);
        moduleManageParamVo.setCenterName(centerName);
        moduleManageParamVo.setPackageList(objects);
        moduleManageParamVo.setChargeTelephone(chargeTelephone);
        moduleManageParamVo.setOfficalChargePerson(officalChargePerson);
        moduleManageParamVo.setOfficalChargeTelephone(officalChargeTelephone);
        moduleManageParamVos.add(moduleManageParamVo);
        
      }
    }
    return moduleManageParamVos;
  }
  
  /**
   * 获取svn自动类型的data
   *
   * @param inputStream
   * @return
   * @throws IOException
   */
  public static List<ModuleManageParamVo> getModuleManageSvnAutoData(FileInputStream inputStream)
      throws IOException {
    List<ModuleManageParamVo> moduleManageParamVos = new ArrayList<>();
    //获取excel表格中的数据
    XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
    int numberOfSheets = workbook.getNumberOfSheets();
    for (int i = 0; i < numberOfSheets; i++) {
      XSSFSheet xssfSheet = workbook.getSheetAt(i);
      if (xssfSheet != null) {
        XSSFRow row1 = xssfSheet.getRow(1);
        String chargePerson = getCellValue(row1.getCell(1));
        String chargePersonTel = getCellValue(row1.getCell(3));
        XSSFRow row2 = xssfSheet.getRow(2);
        String officalChargePerson = getCellValue(row2.getCell(1));
        String officalChargePersonTel = getCellValue(row2.getCell(3));
        int lastRowNum = xssfSheet.getLastRowNum();
        for (int j = 4; j <= lastRowNum; j++) {
          XSSFRow row = xssfSheet.getRow(j);
          if (row == null) {
            break;
          }
          String moduleName = getCellValue(row.getCell(1));
          String centerName = getCellValue(row.getCell(2));
          String svnAutoUrl = getCellValue(row.getCell(3));
          //如果模块名获取不到 即结束循环取数据操作
          if (StringUtils.isBlank(moduleName) || StringUtils.isBlank(centerName) ||
              StringUtils.isBlank(svnAutoUrl)) {
            break;
          }
          //svn-co-smt-bss-order-batch-dubbo.sh
          String moduleContentName = null;
          if (StringUtils.isNotBlank(svnAutoUrl)) {
            String[] split = svnAutoUrl.split("/");
            String s = split[split.length - 1];
            if (s.contains("svn-co-smt-")) {
              String replace = s.replace("svn-co-smt-", "");
              moduleContentName = replace;
              if (replace.contains(".sh")) {
                moduleContentName = replace.replace(".sh", "");
              }
            } else {
              moduleContentName = s.replace(".sh", "");
            }
          }
          if (StringUtils.isBlank(moduleContentName)) {
            break;
          }
          ModuleManageParamVo manageParamVo = new ModuleManageParamVo();
          manageParamVo.setOfficalChargeTelephone(officalChargePersonTel);
          manageParamVo.setChargePerson(chargePerson);
          manageParamVo.setChargeTelephone(chargePersonTel);
          manageParamVo.setOfficalChargePerson(officalChargePerson);
          manageParamVo.setSvnAutoUpUrl(svnAutoUrl);
          manageParamVo.setModuleContentName(moduleContentName);
          manageParamVo.setModuleName(moduleName);
          manageParamVo.setCenterName(centerName);
          moduleManageParamVos.add(manageParamVo);
        }
      }
    }
    
    return moduleManageParamVos;
    
  }
  
  //根据单元格type的不同获取不同的内容信息
  public static String getCellValue(Cell cell) {
    String o = null;
    switch (cell.getCellTypeEnum()) {
      case BLANK:
        break;
      case STRING:
        o = cell.getStringCellValue().replaceAll(" +", "");
        break;
      case ERROR:
        break;
      case FORMULA:
        break;
      case NUMERIC:
        DecimalFormat df = new DecimalFormat("#");
        o = df.format(cell.getNumericCellValue());
        break;
      default:
        break;
    }
    return o;
  }
  
  @SneakyThrows
  public static void main(String[] args) {
    
    List<ModulePackageParamVo> paramVos =
        getAllGitShData(new FileInputStream("E:\\Users\\litiewang\\Downloads\\svn-co-smt-bss-operation-outdubbo-trunk.sh"));
    System.out.println(paramVos);
  }
  
  /**
   * 获取某个文件里的所有的关于
   *
   * @param inputStream
   * @return
   */
  
  public static List<ModulePackageParamVo> getAllGitShData(InputStream inputStream) {
    log.info("开始解析sh脚本中的内容");
    List<ModulePackageParamVo> packageList = new ArrayList<>();
//        int count = 0;
    Set<String> paramSet1 = new HashSet<>();
    BufferedReader br = null;
    try {
      br = new BufferedReader(new InputStreamReader(inputStream));
      while (br.ready()) {
        String s = br.readLine();
        if (StringUtils.isNotBlank(s)) {
          String trim = s.trim();
          if (trim.startsWith("git clone")) {
            String[] lines = trim.split("\n");
            for (String line : lines) {
              String[] split = line.split("\\s+");
              if (split.length <= 3) {
                ModulePackageParamVo modulePackageParamVo = new ModulePackageParamVo();
                String contentName;
                
                String[] urlSplit = split[2].split("/");
                contentName = urlSplit[urlSplit.length - 1];
                
                if (!paramSet1.contains(split[2])) {
                  modulePackageParamVo.setCodeUrl(split[2]);
                  modulePackageParamVo.setContentName(contentName);
                  packageList.add(modulePackageParamVo);
                  paramSet1.add(split[2]);
                } else {
                  System.out.println("packageContentName 有重复的存在: " + contentName);
                }
              } else {
                
                Pattern pattern = Pattern.compile("(https://[^\\s]+)");
                Matcher matcher = pattern.matcher(line);
                ModulePackageParamVo modulePackageParamVo = new ModulePackageParamVo();
                // 查找匹配项
                String contentName = null;
                if (matcher.find()) {
                  String url = matcher.group(1);
                  String[] urlSplit = url.split("/");
                  contentName = urlSplit[urlSplit.length - 1];
                  modulePackageParamVo.setCodeUrl(url);
                  modulePackageParamVo.setContentName(contentName);
                }
                String[] parts = line.split(" ");
                for (int i = 0; i < parts.length; i++) {
                  if (parts[i].equals("-b") && i < parts.length - 1) {
                    String branch = parts[i + 1];
                    modulePackageParamVo.setGitBranch(branch);
                    break;
                  }
                  
                }
                if (StringUtils.isBlank(modulePackageParamVo.getGitBranch())) {
                  //重命名
                  modulePackageParamVo.setContentName(parts[parts.length - 1]);
                }
                
                if (!paramSet1.contains(contentName)) {
                  packageList.add(modulePackageParamVo);
                  paramSet1.add(contentName);
                }
              }
            }
            
          }
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      try {
        if (br != null) {
          br.close();
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
      paramSet1.clear();
    }
    return packageList;
  }
  
  /**
   * 获取某个文件里的所有的关于
   *
   * @param inputStream
   * @return
   */
  public static List<ModulePackageParamVo> getAllShData(InputStream inputStream) {
    log.info("开始解析sh脚本中的内容");
    List<ModulePackageParamVo> packageList = new ArrayList<>();
//        int count = 0;
    Set<String> paramSet1 = new HashSet<>();
    BufferedReader br = null;
    try {
      br = new BufferedReader(new InputStreamReader(inputStream));
      while (br.ready()) {
        String s = br.readLine();
        if (StringUtils.isNotBlank(s)) {
          String trim = s.trim();
          if (trim.startsWith("svn co") && !trim.contains("--depth=empty")) {
            String[] split = trim.split("\\s+");
            ModulePackageParamVo modulePackageParamVo = new ModulePackageParamVo();
            String contentName = null;
            if (split.length == 4) {
              contentName = split[3];
            } else {
              String[] split1 = split[2].split("/");
              contentName = split1[split1.length - 1];
            }
            if (!paramSet1.contains(split[2])) {
              modulePackageParamVo.setCodeUrl(split[2]);
              modulePackageParamVo.setContentName(contentName);
              packageList.add(modulePackageParamVo);
              paramSet1.add(split[2]);
            } else {
              log.info("packageContentName 有重复的存在: {}", contentName);
            }
          }
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      try {
        if (br != null) {
          br.close();
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
      paramSet1.clear();
    }
    return packageList;
  }
  
  /**
   * 获取发布清单里所有待发布的模块
   *
   * @param inputStream
   * @return map<int, set < int>>
   */
  public static Map<String, List<String>> getDeployListExcelData(InputStream inputStream) throws IOException {
    Map<String, List<String>> resultMap = new HashMap<String, List<String>>();
    XSSFWorkbook wb = new XSSFWorkbook(inputStream);
    XSSFSheet sheet = wb.getSheet("TA发布清单");
    for (int i = 1; i <= sheet.getLastRowNum(); i++) {
      XSSFRow row = sheet.getRow(i);
      if (row == null) {
        break;
      }
      XSSFCell cell1 = row.getCell(1);
      XSSFCell cell2 = row.getCell(2);
      if (StringUtils.isNotBlank(getCellValue(cell1)) && StringUtils.isNotBlank(getCellValue(cell2))) {
        String cell1str = getCellValue(cell1);
        String cell2str = getCellValue(cell2);
        List<String> cell1list =
            new LinkedList<>(Arrays.asList(cell1str.split("\n")));
        for (String env : cell2str.split("/")) {
          if (resultMap.keySet().contains(env)) {
            resultMap.get(env).addAll(cell1list);
//                        List<String> list1 = new LinkedList<>(resultMap.get(env));
//                        list1.addAll(cell1list);
//                        resultMap.put(env, list1);
            continue;
          }
          resultMap.put(env, cell1list);
        }
      } else {
        continue;
      }
    }
    if (resultMap != null) log.info("发布清单解析成功");
    return resultMap;
  }
  
  /**
   * 设置sheet的列宽为自适应
   *
   * @param sheet
   * @param size
   */
  public static void setSizeColumn(Sheet sheet, int size) {
    for (int columnNum = 0; columnNum < size; columnNum++) {
      int columnWidth = sheet.getColumnWidth(columnNum) / 256;
      for (int rowNum = 0; rowNum <= sheet.getLastRowNum(); rowNum++) {
        Row currentRow;
        //当前行未被使用过
        if (sheet.getRow(rowNum) == null) {
          currentRow = sheet.createRow(rowNum);
        } else {
          currentRow = sheet.getRow(rowNum);
        }
        
        if (currentRow.getCell(columnNum) != null) {
          Cell currentCell = currentRow.getCell(columnNum);
          if (currentCell.getCellTypeEnum() == CellType.STRING) {
            int length = currentCell.getStringCellValue().getBytes(StandardCharsets.UTF_8).length;
            if (columnWidth < length) {
              columnWidth = length;
            }
          }
        }
      }
      sheet.setColumnWidth(columnNum, columnWidth * 256);
    }
  }
  
  /**
   * 解析批量新增用户信息
   *
   * @param reportFile
   * @return
   */
  public static List<PModuleUser> getUserExcel(MultipartFile reportFile) {
    List<PModuleUser> userList = new LinkedList<>();
    try {
      XSSFWorkbook sheets = new XSSFWorkbook(reportFile.getInputStream());
      XSSFSheet sheet = sheets.getSheet("帐号申请模版");
      for (int i = 1; i <= sheet.getLastRowNum(); i++) {
        String cname = getCellValue(sheet.getRow(i).getCell(3));
        String phone = getCellValue(sheet.getRow(i).getCell(4));
        String username = getCellValue(sheet.getRow(i).getCell(6));
        String pass = getCellValue(sheet.getRow(i).getCell(7));
        PModuleUser moduleUser = new PModuleUser();
        moduleUser.setCname(cname);
        moduleUser.setUsername(username);
        moduleUser.setMobile(phone);
        moduleUser.setPassword(pass);
        userList.add(moduleUser);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return userList;
  }
  
  public static List<ModuleDeployNeedDTO> getDeployNeedExcel(InputStream inputStream) throws IOException {
    XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
    XSSFSheet sheetAt = workbook.getSheetAt(0);
    List<ModuleDeployNeedDTO> deployNeedDTOS = new LinkedList<>();
    for (int i = 1; i <= sheetAt.getLastRowNum(); i++) {
      List<String> moduleNameList = new ArrayList<>();
      ModuleDeployNeed deployNeed = new ModuleDeployNeed();
      ModuleDeployNeedDTO moduleDeployNeedDTO = new ModuleDeployNeedDTO();
      XSSFRow row = sheetAt.getRow(i);
      deployNeed.setNeedNumber(row.getCell(0).getStringCellValue());
      deployNeed.setNeedDescribe(row.getCell(1).getStringCellValue());
      deployNeed.setNeedContent(row.getCell(2).getStringCellValue());
      deployNeed.setEnvName(row.getCell(3).getStringCellValue());
      deployNeed.setDeveloper(row.getCell(6).getStringCellValue());
      deployNeed.setPstTest("通过".equals(row.getCell(7).getStringCellValue()) ? 1 : 0);
      deployNeed.setDrTest("通过".equals(row.getCell(8).getStringCellValue()) ? 1 : 0);
      if (row.getCell(5) != null)
        moduleNameList = Arrays.asList(row.getCell(5).getStringCellValue().split(" "));
      moduleDeployNeedDTO.setDeployNeed(deployNeed);
      moduleDeployNeedDTO.setModuleNameList(moduleNameList);
      deployNeedDTOS.add(moduleDeployNeedDTO);
    }
    return deployNeedDTOS;
  }
  
}
