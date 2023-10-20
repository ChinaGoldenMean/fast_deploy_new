package com.xc.fast_deploy.service.permission.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xc.fast_deploy.dao.master_dao.PModuleBasePermissionMapper;
import com.xc.fast_deploy.dao.master_dao.PModulePermissionMapper;
import com.xc.fast_deploy.dao.master_dao.PModuleRoleMapper;
import com.xc.fast_deploy.dto.MyPageInfo;
import com.xc.fast_deploy.dto.ResponseDTO;
import com.xc.fast_deploy.model.master_model.PModuleBasePermission;
import com.xc.fast_deploy.model.master_model.PModulePermission;
import com.xc.fast_deploy.service.permission.PModuleBasePermissionService;
import com.xc.fast_deploy.vo.module_vo.permission.ModuleBaseBindPermissionVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Slf4j
public class PModuleBasePermissionServiceImpl implements PModuleBasePermissionService {
  
  @Autowired
  private PModuleBasePermissionMapper basePermissionMapper;
  @Autowired
  private PModulePermissionMapper permissionMapper;
  @Autowired
  private PModuleRoleMapper pModuleRoleMapper;
  
  @Override
  public MyPageInfo<PModuleBasePermission> selectAll(Integer pageNum, Integer pageSize, String keyName) {
    MyPageInfo<PModuleBasePermission> myPageInfo = new MyPageInfo<>();
    if (pageNum != null && pageNum > 0 && pageSize != null && pageSize > 0) {
      PageHelper.startPage(pageNum, pageSize);
      List<PModuleBasePermission> list = basePermissionMapper.selectAll(keyName);
      PageInfo<PModuleBasePermission> pageInfo = new PageInfo<>(list);
      BeanUtils.copyProperties(pageInfo, myPageInfo);
    }
    return myPageInfo;
  }
  
  @Override
  public Integer insertOne(PModuleBasePermission basePermission) {
    //加锁防止重复提交
    synchronized (this) {
      basePermission.setCreateTime(new Date());
      basePermission.setUpdateTime(new Date());
      if (StringUtils.isBlank(basePermission.getBasePerCode())) {
        basePermission.setBasePerCode(UUID.randomUUID().toString().replace("-", ""));
      }
      return basePermissionMapper.insert(basePermission);
    }
  }
  
  @Override
  public void updateOne(PModuleBasePermission basePermission) {
    synchronized (this) {
      basePermission.setUpdateTime(new Date());
      basePermissionMapper.updateByPrimaryKeySelective(basePermission);
    }
  }
  
  @Override
  public PModuleBasePermission selectById(Integer basePermissionId) {
    return basePermissionMapper.selectByPrimaryKey(basePermissionId);
  }
  
  @Override
  public List<PModulePermission> selectPermissionList(Integer basePermissionId) {
    return basePermissionMapper.selectPerListById(basePermissionId);
  }
  
  /**
   * 绑定基础权限和权限的关系
   *
   * @param basePermissionId
   * @param permIds
   * @return
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  public ResponseDTO bindBasePerm(Integer basePermissionId, Integer[] permIds) {
    ResponseDTO responseDTO = new ResponseDTO();
    //首先验证基础权限是否存在
    PModuleBasePermission basePermission = basePermissionMapper.selectByPrimaryKey(basePermissionId);
    synchronized (this) {
      if (basePermission != null) {
        //然后验证权限ids是否都存在
        Set<Integer> ids = new HashSet<>(Arrays.asList(permIds));
        Set<Integer> permissionIdSet = permissionMapper.selectIds(ids);
        if (permissionIdSet != null && permissionIdSet.size() != ids.size()) {
          responseDTO.fail("权限不存在异常");
          return responseDTO;
        }
        //加锁删除后添加
        //删除以前的关联数据后
        basePermissionMapper.deleteBindPermById(basePermissionId);
        //添加好新的关联关系
        List<ModuleBaseBindPermissionVo> baseBindPermissionVoList = new ArrayList<>();
        for (Integer permId : permIds) {
          ModuleBaseBindPermissionVo baseBindPermissionVo = new ModuleBaseBindPermissionVo();
          baseBindPermissionVo.setBasePermId(basePermissionId);
          baseBindPermissionVo.setPermissionId(permId);
          baseBindPermissionVoList.add(baseBindPermissionVo);
        }
        basePermissionMapper.insertBatchBindBasePerm(baseBindPermissionVoList);
        responseDTO.success("操作成功");
      }
    }
    return responseDTO;
  }
  
  /**
   * 删除基础权限信息 并同时删除与最小权限的关联信息以及角色关联基础权限的信息
   *
   * @param basePermissionId
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  public void delBasePerm(Integer basePermissionId) {
    if (basePermissionMapper.selectByPrimaryKey(basePermissionId) != null) {
      //删除原有的基础权限数据
      basePermissionMapper.deleteByPrimaryKey(basePermissionId);
      //删除对应的绑定的基础权限数据
      basePermissionMapper.deleteBindPermById(basePermissionId);
      //删除角色关联的基础权限的数据
      pModuleRoleMapper.deleteRoleBasePermByBaseId(basePermissionId);
    }
  }
  
}
