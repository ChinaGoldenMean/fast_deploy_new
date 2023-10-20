package com.xc.fast_deploy.service.common;

import com.github.pagehelper.PageInfo;

import java.io.Serializable;
import java.util.List;

public interface BaseService<T, PK extends Serializable> {
  
  //添加(只返回成功数)
  int insert(T t);
  
  //添加
  int insertSelective(T t);
  
  //查询通过id
  T selectById(PK id);
  
  //查询所有
  List<T> selectAll();
  
  //分页查询所有
  PageInfo<T> selectPageAll(Integer pageNum, Integer pageSize);
  
  //更新(null的不更新,非null更新)
  int update(T t);
  
  //删除(通过id)
  int deleteById(PK id);
  
}
