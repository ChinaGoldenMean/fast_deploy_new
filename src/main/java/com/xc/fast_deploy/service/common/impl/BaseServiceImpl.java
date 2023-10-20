package com.xc.fast_deploy.service.common.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xc.fast_deploy.dao.master_dao.BaseMapper;

import com.xc.fast_deploy.service.common.BaseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.List;

@Service
@Slf4j
public class BaseServiceImpl<T, PK extends Serializable> implements BaseService<T, PK> {
  
  private BaseMapper<T, PK> baseMapper;
  
  void init(BaseMapper<T, PK> mapper) {
    this.baseMapper = mapper;
  }
  
  @Override
  @Transactional(rollbackFor = Exception.class)
  public int insert(T t) {
    return baseMapper.insert(t);
  }
  
  @Override
  @Transactional(rollbackFor = Exception.class)
  public int insertSelective(T t) {
    return baseMapper.insertSelective(t);
  }
  
  @Override
  public T selectById(PK id) {
    return baseMapper.selectByPrimaryKey(id);
  }
  
  @Override
  public List<T> selectAll() {
    return baseMapper.selectAll();
  }
  
  @Override
  public PageInfo<T> selectPageAll(Integer pageNum, Integer pageSize) {
    if (pageNum != null && pageNum > 0 && pageSize != null && pageSize > 0) {
      PageHelper.startPage(pageNum, pageSize);
      List<T> list = baseMapper.selectAll();
      return new PageInfo<T>(list);
    }
    return null;
  }
  
  @Override
  @Transactional(rollbackFor = Exception.class)
  public int update(T t) {
    return baseMapper.updateByPrimaryKeySelective(t);
  }
  
  @Override
  public int deleteById(PK id) {
    return baseMapper.deleteByPrimaryKey(id);
  }
  
}
