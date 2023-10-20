package com.xc.fast_deploy.utils.constant;

public interface JenkinsOperate {
  
  String ADD_JOB_TO_VIEW = "addJobToView";
  
  // jenkins 获取构建版本最大条数 10条
  Integer JOB_DETAILS_MAX_NUMBER = 10;
  
  //jenkins 日志数据最大值 1M
  Integer MAX_OFFSET_DATA_NUMBER = 1024 * 1024;
  
}
