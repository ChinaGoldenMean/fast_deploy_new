package com.xc.fast_deploy.utils.constant;

public interface K8sPatchMirror {
  
  String TEMPLATE_CONTAINER_MIRROR_PATH = "/spec/template/spec/containers/0/image";
  
  String CONTAINER_MIRROR_PATH = "/spec/containers/0/image";
  
  String NODE_LABEL = "/metadata/labels";
  
  String NODE_UNSCHEDULABLE = "/spec/unschedulable";
  
  String TEMPLATE_CONTAINERS_RESOURCE_LIMIT = "/spec/template/spec/containers/0/resources/limits";
  
  String TEMPLATE_SPEC_NODESELECTOR = "/spec/template/spec/nodeSelector";
  
  String SPEC_METADATA_LABELS = "/spec/template/metadata/labels";
  
  String TEMPLATE_SPEC_VOLUME = "/spec/template/spec/volumes";
  
  String TEMPLATE_CONTAINERS_VOLUMEMOUNTS = "/spec/template/spec/containers/0/volumeMounts";
  
  String SPEC_SELECTOR_MATCHLABELS = "/spec/selector/matchLabels";
  
  String SPEC_STRATEGY = "/spec/strategy";
  
  String SPEC_REPLICAS = "/spec/replicas";
  
}
