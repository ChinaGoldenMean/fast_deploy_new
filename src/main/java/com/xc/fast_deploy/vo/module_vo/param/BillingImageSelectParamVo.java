package com.xc.fast_deploy.vo.module_vo.param;

import lombok.Data;

import java.io.Serializable;

@Data
public class BillingImageSelectParamVo implements Serializable {
  
  private static final long serialVersionUID = 7194339157667448410L;
  
  private String nameCode;
  
  private Integer envId;
  
  private String beginTime;
  
  private String endTime;
  
  //是否通过审核 0否 1是 2待审核
  private Integer isReviewed;
  
}
