package com.xc.fast_deploy.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class MyPageInfo<T> implements Serializable {
  
  private static final long serialVersionUID = 3103695257858356340L;
  //当前页码
  private Integer pageNum;
  //当前页数量
  private Integer pageSize;
  //总数量
  private long total;
  //总页数
  private Integer pages;
  //数据
  private List<T> list;
}
