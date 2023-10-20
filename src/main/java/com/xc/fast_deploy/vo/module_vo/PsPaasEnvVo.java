package com.xc.fast_deploy.vo.module_vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class PsPaasEnvVo implements Serializable {
  
  private static final long serialVersionUID = 7987139028945369974L;
  private Integer paasid;
  private String paasname;
  private String env;
  
}
