package com.xc.fast_deploy.utils.constant;

public interface K8sHttpUrlConstants {
  String V1_API_PREFIX = "/api/v1/namespaces/";
  String V1_SERVICES = "/services/";
  String V1_CONFIGMAPS = "/configmaps/";
  String V1_PODS = "/pods/";
  String V1_ENDPOINTS = "/endpoints/";
  String V1_NODE = "/api/v1/nodes/";
  String V1_REPLICATIONCONTROLLERS = "/replicationcontrollers/";
  String EXTENSION_V1BETA1_PREFIX = "/apis/extensions/v1beta1/namespaces/";
  String EXTENSION_DEPLOYMENTS = "/deployments/";
  String EXTENSION_DAEMONSETS = "/daemonsets/";
  String EXTENSION_REPLICASETS = "/replicasets/";
  String EXTENSION_INGRESSES = "/ingresses/";
  String APPS_V1 = "/apis/apps/v1/namespaces/";
  String APPS_V1BETA2 = "apps/v1beta2";
  String APPS_STATEFUL = "/statefulsets/";
}
