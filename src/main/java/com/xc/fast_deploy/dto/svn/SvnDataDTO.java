package com.xc.fast_deploy.dto.svn;

import com.xc.fast_deploy.vo.module_vo.param.ModulePackageParamVo;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class SvnDataDTO implements Serializable {
  
  private static final long serialVersionUID = 6096619903397105018L;
  private String projectName;
  private List<ModulePackageParamVo> packageList;
}
