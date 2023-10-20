package com.xc.fast_deploy.vo.k8s_vo;

import lombok.Data;

@Data
public class K8sResourceVo {
  
  private Integer envId;
  
  private Integer centerId;
  
  private Integer type;  //发布文件的类型选择
  
  private Integer moduleId;
  
  //发布ID 修改yaml文件时使用
  private Integer deployId;
  
  private Object deployResource;
  
  //是否需要创建svc端口
  private Integer isNeedSvc;  //0 否 1 是
  
  private Integer port;
  
  private Integer nodePort;
  
  private Integer targetPort;
  
  private String namespace;
  
}
