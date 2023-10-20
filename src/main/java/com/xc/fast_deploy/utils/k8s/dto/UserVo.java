package com.xc.fast_deploy.utils.k8s.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserVo implements Serializable {
  private static final long serialVersionUID = 2908259094624435963L;
  
  private String clientCertificateData;
  private String clientKeyData;
}
