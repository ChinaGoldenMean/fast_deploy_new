package com.xc.fast_deploy.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class BillingPackageVo implements Serializable {
  
  private static final long serialVersionUID = -2307049302590188400L;
  private Date beginTime;
  private Date endTime;
  private String name;
  private String size;
  private Integer status;
  private String targetAdderss;
  private String md5;
  
}
