package com.xc.fast_deploy.dto.module;

import com.xc.fast_deploy.vo.module_vo.ModuleJobVo;
import lombok.Data;

import java.io.Serializable;
import java.util.Map;

@Data
public class ModuleJobDTO extends ModuleJobVo implements Serializable {
  
  private static final long serialVersionUID = 8357105454613032367L;
  
  private String crontabExpression;
  
  private String compileFilePath;
  
  private String compileCommand;
  
  private String dockerfilePath;
  
  private String dockerfileContent;
  
  private Map<String, String> argsMap;
  
}
