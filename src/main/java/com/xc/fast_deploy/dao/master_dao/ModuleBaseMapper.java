package com.xc.fast_deploy.dao.master_dao;

import java.io.Serializable;
import java.util.List;

public interface ModuleBaseMapper<T, PK extends Serializable> {
  
  T selectByPrimaryKey(PK id);
  
  int insert(T t);
  
  int insertSelective(T t);
  
  int deleteByPrimaryKey(PK id);
  
  int updateByPrimaryKeySelective(T t);
  
  int updateByPrimaryKey(T t);
  
  List<T> selectAll();
  
}
