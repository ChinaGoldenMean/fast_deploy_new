package com.xc.fast_deploy.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class UploadModuleStatusDTO implements Serializable {
  
  private String fileName;
  
  private String msg;
  
  private Integer total;
  
  private Integer current;
  
  // 0 正在进行中 1 失败 2 成功
  private Integer status;
  
  private String moduleName;
  
  private Integer modulePackageTotal;
  
  private Integer modulePackageCurrent;
  
  private String modulePackageMsg;
  
  // 0 正在进行中 1 失败 2 成功
  private Integer modulePackageStatus;
  
  public static UploadModuleStatusDTO progress(String fileName, Integer current, Integer total) {
    UploadModuleStatusDTO statusDTO = new UploadModuleStatusDTO();
    statusDTO.setFileName(fileName);
    statusDTO.setCurrent(current);
    statusDTO.setTotal(total);
    statusDTO.setMsg("验证svn_url正确性中");
    statusDTO.setStatus(0);
    return statusDTO;
  }
  
  public static UploadModuleStatusDTO progressPackage(String fileName, Integer current, Integer total,
                                                      String moduleName, Integer modulePackageCurrent,
                                                      Integer modulePackageTotal, String codeUrl) {
    UploadModuleStatusDTO statusDTO = progress(fileName, current, total);
    statusDTO.setModuleName(moduleName);
    statusDTO.setModulePackageCurrent(modulePackageCurrent);
    statusDTO.setModulePackageTotal(modulePackageTotal);
    statusDTO.setModulePackageMsg(codeUrl);
    statusDTO.setModulePackageStatus(0);
    return statusDTO;
  }
  
  public static UploadModuleStatusDTO failPackage(String fileName, String msg,
                                                  String moduleName, String modulePackageMsg) {
    UploadModuleStatusDTO statusDTO = new UploadModuleStatusDTO();
    statusDTO.setFileName(fileName);
    statusDTO.setMsg(msg);
    statusDTO.setModuleName(moduleName);
    statusDTO.setModulePackageMsg(modulePackageMsg);
    statusDTO.setModulePackageStatus(1);
    return statusDTO;
  }
  
  public static UploadModuleStatusDTO successPackage(String fileName, String msg,
                                                     String moduleName, String modulePackageMsg) {
    UploadModuleStatusDTO statusDTO = new UploadModuleStatusDTO();
    statusDTO.setFileName(fileName);
    statusDTO.setMsg(msg);
    statusDTO.setModuleName(moduleName);
    statusDTO.setModulePackageMsg(modulePackageMsg);
    statusDTO.setModulePackageStatus(2);
    return statusDTO;
  }
  
  public static UploadModuleStatusDTO fail(String fileName, String msg) {
    UploadModuleStatusDTO statusDTO = new UploadModuleStatusDTO();
    statusDTO.setMsg(msg);
    statusDTO.setFileName(fileName);
    statusDTO.setStatus(1);
    return statusDTO;
  }
  
  public static UploadModuleStatusDTO success(String fileName, String msg) {
    UploadModuleStatusDTO statusDTO = new UploadModuleStatusDTO();
    statusDTO.setMsg(msg);
    statusDTO.setFileName(fileName);
    statusDTO.setStatus(2);
    return statusDTO;
  }
}
