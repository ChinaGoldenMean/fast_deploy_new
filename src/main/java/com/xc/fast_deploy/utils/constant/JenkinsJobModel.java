package com.xc.fast_deploy.utils.constant;

public interface JenkinsJobModel {
  
  String VIEW_MODEL_TEMP = "model_view";
  
  //只执行dockerfile打包并上传镜像的操作
  String JOB_DOCKERFILE = "dockerfile_job_model";
  
  //首先要经过编译之后才去进行dockerfile操作
  String JOB_COMPILE_COMMAND_DOCKERFILE = "compile_command_dockerfile_job_model";
  
  //首先要经过编译之后才去进行dockerfile操作
  String JOB_COMPILE_FILE_DOCKERFILE = "compile_file_dockerfile_job_model";
  
  String MIRROR_UPDATE_JOB = "mirror_update_job";
  
}
