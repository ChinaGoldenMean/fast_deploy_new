package com.xc.fast_deploy.vo.module_vo.param;

import lombok.Data;

@Data
public class PModuleUserSelectParamVo {
  
  //模糊匹配关键字
  private String keyName;
  //更新时间的匹配
  private String beginTime;
  
  private String endTime;
  
  private Integer isActive = 1;
  
  //所属部门
  private Integer depId;
  
}
