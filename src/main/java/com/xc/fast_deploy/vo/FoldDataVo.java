package com.xc.fast_deploy.vo;

import lombok.Data;

import java.util.List;

@Data
public class FoldDataVo {
  
  private String name;
  
  private boolean isDir;
  
  private List<FoldDataVo> children;
  
  private String absolutePath;
  
}
