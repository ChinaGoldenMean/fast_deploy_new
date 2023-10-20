package com.xc.fast_deploy.service.permission.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xc.fast_deploy.dao.master_dao.PModuleBasePermissionMapper;
import com.xc.fast_deploy.dao.master_dao.PModulePermissionMapper;
import com.xc.fast_deploy.dao.master_dao.PModuleRoleMapper;
import com.xc.fast_deploy.dto.MyPageInfo;
import com.xc.fast_deploy.model.master_model.PModuleBasePermission;
import com.xc.fast_deploy.model.master_model.PModulePermission;
import com.xc.fast_deploy.service.permission.PModulePermissionService;
import com.xc.fast_deploy.vo.module_vo.ModuleJobVo;
import com.xc.fast_deploy.vo.module_vo.permission.ModuleRolePermissionParamVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class PModulePermissionServiceImpl implements PModulePermissionService {
  @Autowired
  private PModulePermissionMapper permissionMapper;
  @Autowired
  private PModuleRoleMapper roleMapper;
  
  @Autowired
  private PModuleBasePermissionMapper basePermissionMapper;
  
  /**
   * 添加最小权限
   *
   * @param permission
   * @return
   */
  @Override
  public boolean addPermission(PModulePermission permission) {
    permission.setCreateTime(new Date());
    permission.setUpdateTime(new Date());
    return permissionMapper.insertSelective(permission) > 0;
  }
  
  /**
   * 更新最小权限
   *
   * @param permission
   * @return
   */
  @Override
  public boolean updatePermission(PModulePermission permission) {
    //首先确认该权限是否存在
    PModulePermission modulePermission = permissionMapper.selectByPrimaryKey(permission.getId());
    if (modulePermission != null) {
      permissionMapper.updateByPrimaryKeySelective(permission);
      return true;
    }
    return false;
  }
  
  /**
   * 删除最小权限
   *
   * @param permissionId
   * @return
   */
  @Override
  public boolean deleteById(Integer permissionId) {
    permissionMapper.deleteByPrimaryKey(permissionId);
    //同时删除关联表里面的关于此权限的信息
    //同时删除基础权限关联表的数据
    basePermissionMapper.deleteBindPermByPermId(permissionId);
//        roleMapper.deleteRolePermissionByPermissionId(permissionId);
    return true;
  }
  
  /**
   * 批量删除最小权限
   *
   * @param permissionIds
   * @return
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  public boolean deleteByIds(Integer[] permissionIds) {
    if (permissionIds != null && permissionIds.length > 0) {
      Set<Integer> pemissionIdSet = new HashSet<>(Arrays.asList(permissionIds));
      permissionMapper.deleteByIds(pemissionIdSet);
      basePermissionMapper.deleteBindPermByPermIds(pemissionIdSet);
//            roleMapper.deleteRolePermissionIds(pemissionIdSet);
    }
    return false;
  }
  
  /**
   * 分页查询所有最小权限信息
   *
   * @param pageNum
   * @param pageSize
   * @param permissionParamVo
   * @return
   */
  @Override
  public MyPageInfo<PModulePermission> selectPageAllInfo(Integer pageNum, Integer pageSize,
                                                         ModuleRolePermissionParamVo permissionParamVo) {
    MyPageInfo<PModulePermission> myPageInfo = new MyPageInfo<>();
    if (pageNum != null && pageNum > 0 && pageSize != null && pageSize > 0) {
      PageHelper.startPage(pageNum, pageSize);
      List<PModulePermission> list = permissionMapper.selectPermissionVoPageByVo(permissionParamVo);
      PageInfo<PModulePermission> pageInfo = new PageInfo<>(list);
      BeanUtils.copyProperties(pageInfo, myPageInfo);
    }
    return myPageInfo;
  }
}
