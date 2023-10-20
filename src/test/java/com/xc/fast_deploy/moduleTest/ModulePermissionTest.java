package com.xc.fast_deploy.moduleTest;

import com.alibaba.fastjson.JSONObject;
import com.xc.fast_deploy.dao.master_dao.*;
import com.xc.fast_deploy.dto.module.permission.ModuleUserPermissionDTO;
import com.xc.fast_deploy.model.master_model.PModulePermission;
import com.xc.fast_deploy.model.master_model.PModuleRole;
import com.xc.fast_deploy.model.master_model.PModuleUser;
import com.xc.fast_deploy.service.permission.PModuleUserService;
import com.xc.fast_deploy.vo.module_vo.permission.ModuleUserEnvRoleDto;
import com.xc.fast_deploy.vo.module_vo.permission.ModuleUserEnvRoleParamVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.*;
import java.util.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class ModulePermissionTest {
  
  @Autowired
  private ModuleUserMapper userMapper;
  
  @Autowired
  private ModulePermissionMapper permissionMapper;
  
  @Autowired
  private PModuleRoleMapper roleMapper;
  
  @Autowired
  private PModuleUserMapper pUserMapper;
  
  @Autowired
  private PModulePermissionMapper pModulePermissionMapper;
  
  @Test
  public void test3() {
//        PModuleUser pModuleUser = pUserMapper.selectByPrimaryKey(1);
//        System.out.println(pModuleUser);

//        PModulePermission permission = pModulePermissionMapper.selectByPrimaryKey(3);
//        System.out.println(permission);
//        Set<Integer> set = new HashSet<>();
//        set.add(2);
//        set.add(6);
//        Set<Integer> integerSet = pModulePermissionMapper.selectIds(set);
//        System.out.println(integerSet);
//        List<ModuleUserPermissionDTO> permissions = moduleUserService.getAllPermissionsByUserId(52);
//        List<ModulePermissionDTO> permissionDTOS = pUserMapper.selectPermissionById(52, 0);
//        System.out.println(permissions);
  }
  
  @Test
  public void testInsertPermission() {
    String permission = userMapper.selectUserPermissionById("ZJXCA0081");
    JSONObject jsonObject = JSONObject.parseObject(permission);
    
    Object prod = jsonObject.get("PROD");
    Object test = jsonObject.get("TEST");
    
    List<ModuleUserPermissionDTO> prodPermissionDTOList =
        JSONObject.parseArray(JSONObject.toJSONString(prod), ModuleUserPermissionDTO.class);
//        List<ModuleUserPermissionDTO> testPermissionDTOList =
//                JSONObject.parseArray(JSONObject.toJSONString(test), ModuleUserPermissionDTO.class);
    Set<String> permissionSet = prodPermissionDTOList.get(0).getPermissionSet();
    
    Map<String, String> permissionMap = new HashMap<>();
    BufferedReader reader = null;
    try {
      reader = new BufferedReader(new FileReader(new File("F:\\img\\permission_list.txt")));
      while (reader.ready()) {
        String s = reader.readLine();
        System.out.println(s);
        if (s != null && StringUtils.isNotBlank(s)) {
          String[] split = s.trim().split("\\s+");
          if (split.length > 1) {
            permissionMap.put(split[0], split[1].replace(":", ""));
          }
        }
      }
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      if (reader != null) {
        try {
          reader.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
    permissionMap.put("yx_config_hostname", "亚信更新hostname");
    permissionMap.put("yx_manage_pod_show", "亚信管理pod查看");
    List<PModulePermission> permissionList = new ArrayList<>();
    for (String pem : permissionSet) {
      String s = permissionMap.get(pem);
      if (StringUtils.isBlank(s)) {
        System.out.println("未匹配到-----------------" + pem);
      } else {
        
        System.out.println(s);
        PModulePermission pModulePermission = new PModulePermission();
        pModulePermission.setCreateTime(new Date());
        pModulePermission.setUpdateTime(new Date());
        pModulePermission.setAction(pem);
        pModulePermission.setActionName(s);
        permissionList.add(pModulePermission);
      }
    }
    pModulePermissionMapper.insertBatch(permissionList);
  }
  
  @Test
  public void testUserRoleEnvGet() {
    ModuleUserEnvRoleParamVo envRoleParamVo = new ModuleUserEnvRoleParamVo();
    envRoleParamVo.setEnvId(32);
    List<ModuleUserEnvRoleDto> roleDtos =
        pUserMapper.selectUserEnvRoleVoPageByVo(envRoleParamVo);
    System.out.println(roleDtos);
  }
  
  @Test
  public void testInsertRole() {
    PModuleRole moduleRole1 = new PModuleRole();
    moduleRole1.setUpdateTime(new Date());
    moduleRole1.setCreateTime(new Date());
    moduleRole1.setRoleCode("fast_deploy_all");
    moduleRole1.setRoleName("一键发布所有权限");
    PModuleRole moduleRole2 = new PModuleRole();
    moduleRole2.setUpdateTime(new Date());
    moduleRole2.setCreateTime(new Date());
    moduleRole2.setRoleCode("fast_deploy_normal_user");
    moduleRole2.setRoleName("一键发布普通用户");
    PModuleRole moduleRole3 = new PModuleRole();
    moduleRole3.setUpdateTime(new Date());
    moduleRole3.setCreateTime(new Date());
    moduleRole3.setRoleCode("fast_deploy_all_select_user");
    moduleRole3.setRoleName("一键发布所有查询权");
    roleMapper.insertSelective(moduleRole1);
    roleMapper.insertSelective(moduleRole2);
    roleMapper.insertSelective(moduleRole3);
  }
  
  @Autowired
  private PModuleBasePermissionMapper basePermissionMapper;
  
  @Test
  public void testPModuleUser() {
    basePermissionMapper.selectAll("test");
  }
  
  @Test
  public void testUserId() {
    PModuleUser user = pUserMapper.selectLastOne();
    Integer id = Integer.valueOf(user.getUserId().substring(5)) + 1;
    //String ids = id>=1000?id.toString():"0"+id;
    System.out.println("ZJXCA" + (id >= 1000 ? id.toString() : "0" + id));
  }
}
