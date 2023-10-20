package com.xc.fast_deploy.gitTest;

/**
 * @Author litiewang
 * @Date 2023-07-07 16:32
 * @Version 1.0
 */

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ShellFileParser {
  public static void main(String[] args) {
    String fileContent = "git clone https://yd-code.srdcloud.cn/a/zj-crm-ord/smt-bss-parent\n"
        + "git clone https://yd-code.srdcloud.cn/a/zj-crm-ord/smt-bss-data-core";
    
    List<ModulePackageParamVo> packageList = new ArrayList<>();
    Set<String> paramSet1 = new HashSet<>();
    
    String[] lines = fileContent.split("\n");
    for (String line : lines) {
      String[] split = line.split("\\s+");
      
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
    }
    
    // 打印解析结果
    for (ModulePackageParamVo vo : packageList) {
      System.out.println("Code URL: " + vo.getCodeUrl());
      System.out.println("Content Name: " + vo.getContentName());
      System.out.println();
    }
  }
  
  static class ModulePackageParamVo {
    private String codeUrl;
    private String contentName;
    
    public String getCodeUrl() {
      return codeUrl;
    }
    
    public void setCodeUrl(String codeUrl) {
      this.codeUrl = codeUrl;
    }
    
    public String getContentName() {
      return contentName;
    }
    
    public void setContentName(String contentName) {
      this.contentName = contentName;
    }
  }
}
