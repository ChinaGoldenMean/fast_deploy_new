package com.xc.fast_deploy.util;

import com.xc.fast_deploy.model.master_model.ModuleUser;
import com.xc.fast_deploy.utils.code_utils.ExcelPhraseUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.xssf.usermodel.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class ExcelText {
  
  @Test
  public void analysisUserExcel() {
    List<ModuleUser> users = new LinkedList<>();
    File file = new File("C:\\Users\\luochangbin\\Desktop\\paas运营管理帐号、权限申请模版(1).xlsx");
    try {
      XSSFWorkbook sheets = new XSSFWorkbook(file);
      XSSFSheet sheet = sheets.getSheet("帐号申请模版");
      for (int i = 1; i <= sheet.getLastRowNum(); i++) {
        XSSFRow row = sheet.getRow(i);
        String cname = ExcelPhraseUtils.getCellValue(sheet.getRow(i).getCell(3));
        String phone = ExcelPhraseUtils.getCellValue(sheet.getRow(i).getCell(4));
        String username = ExcelPhraseUtils.getCellValue(sheet.getRow(i).getCell(6));
        String pass = ExcelPhraseUtils.getCellValue(sheet.getRow(i).getCell(7));
        System.out.println("姓名:" + cname + ",账号名:" + username + ",密码:" + pass + ",手机号:" + phone);
      }
    } catch (IOException e) {
      e.printStackTrace();
    } catch (InvalidFormatException e) {
      e.printStackTrace();
    }
  }
}
