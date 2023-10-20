package com.xc.fast_deploy.utils.constant;

public interface K8sOPActive {
  
  String OP_OFFLINE_MODULE = "下线模块";
  String OP_ONLINE_MODULE = "发布上线";
  String OP_CHANGE_MIRROR = "更换镜像";
  String OP_SCALE_SIZE = "扩缩容";
  String OP_ONLINE_SVC = "发布svc";
  String OP_DELETE_PODS = "删除pods";
  String OP_DELETE_SVC = "删除svc";
  String OP_CLEAR_POD_MEMORY = "清除pod缓存";
  String OP_AUTOSCALE_CREATE = "创建自动扩缩容HPA";
  String OP_AUTOSCALE_DELETE = "删除自动扩缩容HPA";
  String OP_HOT_UPDATE_DEPLOYMENT = "热配置修改DEPLOYMENT";
  String OP_DUMP_JAVA_THREAD = "dump日志java线程";
  String OP_DUMP_JAVA_MEMORY = "dump日志java内存";
  String OP_REPLACE_YAML = "更新yaml文件";
}
