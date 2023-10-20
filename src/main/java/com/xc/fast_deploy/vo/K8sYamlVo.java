package com.xc.fast_deploy.vo;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.xc.fast_deploy.dto.ResponseDTO;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Map;

@Data
public class K8sYamlVo implements Serializable {
  
  private static final long serialVersionUID = -7770484662988510394L;
  
  private String yamlPath;
  
  private String namespace;
  
  private String kind;
  
  private String apiVersion;
  
  private String metadataName;
  
  private Map<String, String> labelMap;
  
  @JSONField(serialize = false)
  private Object o;
}
