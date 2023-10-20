package com.xc.fast_deploy.utils.code_utils;

import com.alibaba.fastjson.JSONObject;
import com.xc.fast_deploy.dto.module.ModuleManageDTO;
import com.xc.fast_deploy.dto.module.ModulePackageDTO;
import com.xc.fast_deploy.model.master_model.ModuleManage;
import com.xc.fast_deploy.vo.module_vo.param.ModuleManageParamVo;
import com.xc.fast_deploy.vo.module_vo.param.ModulePackageParamVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static com.xc.fast_deploy.utils.FoldUtils.SEP;
import static com.xc.fast_deploy.utils.constant.HarborContants.CONTACT;

@Slf4j
public class XMLUtils {
  
  /**
   * 生成pom文件
   *
   * @param storgePrefix
   * @return
   */
  public static String genPOMXml(ModuleManageDTO manageDTO, String storgePrefix) {
    String compilePath = null;
    log.info("生成POMxml文件参数 moduleContentName:{},storgePrefix:{}",
        manageDTO.getModuleContentName()
        , storgePrefix);
    StringBuffer moduleContentPath = new StringBuffer();
    moduleContentPath.append(storgePrefix).append(manageDTO.getCenterPath())
        .append(CONTACT).append(manageDTO.getModuleContentName());
    File moduleFile = new File(moduleContentPath.toString());
    if (moduleFile.exists()) {
      File[] packageFiles = moduleFile.listFiles();
      List<String> packageNames = new ArrayList<>();
      if (packageFiles != null && packageFiles.length > 0) {
        for (File file : packageFiles) {
          File[] files = file.listFiles();
          if (files != null && files.length > 0) {
            for (File childFile : files) {
              if ("pom.xml".equals(childFile.getName())) {
                packageNames.add(file.getName());
                compilePath = file.getAbsolutePath();
              }
            }
          }
          //如果当前目录存在Pom文件，就不再生成
        }
      }
      FileOutputStream outputStream = null;
      XMLWriter writer = null;
      try {
        if (packageNames.size() > 1) {
          //有多个满足条件的pom文件,需要生成一个统一的pom文件
          Document document = DocumentHelper.createDocument();
          Element element = document.addElement("project");
          element.addNamespace("xmlns", "http://maven.apache.org/POM/4.0.0");
          element.addAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
          element.addAttribute("xsi:schemaLocation",
              "http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd");
          Element modelVersion = element.addElement("modelVersion");
          modelVersion.addText("4.0.0");
          Element groupId = element.addElement("groupId");
          groupId.addText("com.smt.jenkins.bss");
          Element artifactId = element.addElement("artifactId");
          artifactId.addText(manageDTO.getModuleContentName());
          Element name = element.addElement("name");
          name.addText(manageDTO.getModuleContentName());
          Element packaging = element.addElement("packaging");
          packaging.addText("pom");
          Element version = element.addElement("version");
          version.addText("1.0-SNAPSHOT");
          Element modules = element.addElement("modules");
          for (String packageContentName : packageNames) {
            Element module = modules.addElement("module");
            module.addText(packageContentName);
          }
          outputStream = new FileOutputStream(new File(moduleContentPath.toString() + "/pom.xml"));
          OutputFormat format = OutputFormat.createPrettyPrint();
          format.setEncoding("utf-8");
          writer = new XMLWriter(outputStream, format);
          writer.write(document);
          log.info("generate xml ok!");
          compilePath = moduleContentPath.toString();
        }
      } catch (IOException e) {
        e.printStackTrace();
      } finally {
        if (outputStream != null) {
          try {
            outputStream.close();
          } catch (IOException e) {
            e.printStackTrace();
          }
        }
        if (writer != null) {
          try {
            writer.close();
          } catch (IOException e) {
            e.printStackTrace();
          }
        }
      }
    }
    log.info("编译路径为: {}", compilePath);
    return compilePath;
  }
  
  public static Document String2Document(String xmlString) throws DocumentException {
    Document document = null;
    if (StringUtils.isNotBlank(xmlString)) {
      document = DocumentHelper.parseText(xmlString);
    }
    return document;
  }
  
  public static String Document2String(Document document) {
    String s = null;
    if (document != null) {
      s = document.asXML();
    }
    return s;
  }
  
  public static String generateViewXml(String viewName, String jenkinsUrl) {
    String xml = null;
    if (StringUtils.isNotBlank(viewName)) {
      Document document = DocumentHelper.createDocument();
      Element element = document.addElement("listView");
      element.addAttribute("_class", "hudson.model.ListView");
      Element nameElement = element.addElement("name");
      nameElement.addText(viewName);
      Element urlElement = element.addElement("url");
      urlElement.addText(jenkinsUrl + "/view/" + viewName);
      xml = document.asXML();
    }
    return xml;
  }
  
}
