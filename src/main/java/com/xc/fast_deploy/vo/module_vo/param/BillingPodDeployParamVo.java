package com.xc.fast_deploy.vo.module_vo.param;

import lombok.Data;

@Data
public class BillingPodDeployParamVo {
  
  private String beginTime;
  
  private String endTime;
  
  private Integer envId;
  
  private String nameCode;
  
  //是否已经下线或者删除
  private Integer isDelete;
  
}
