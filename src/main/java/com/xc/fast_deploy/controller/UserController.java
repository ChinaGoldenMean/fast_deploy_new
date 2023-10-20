package com.xc.fast_deploy.controller;

import com.alibaba.fastjson.JSONObject;
import com.xc.fast_deploy.dto.ResponseDTO;
import com.xc.fast_deploy.dto.module.permission.ModuleUserPermissionDTO;
import com.xc.fast_deploy.model.master_model.ModuleUser;
import com.xc.fast_deploy.model.master_model.PModuleUser;
import com.xc.fast_deploy.service.common.ModuleUserService;
import com.xc.fast_deploy.service.permission.PModuleUserService;
import com.xc.fast_deploy.shiro.token.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping(value = "/user")
@Slf4j
public class UserController {
  
  @Autowired
  private PModuleUserService pModuleUserService;
  
  /**
   * 获取当前登录的user信息
   *
   * @return
   */
  @GetMapping(value = "/getUserInfo")
  public String getUserInfo(HttpServletRequest request) {
    String token = (String) SecurityUtils.getSubject().getPrincipal();
    //  log.info("当前登录信息中的token: {} ", token);
    ResponseDTO responseDTO = new ResponseDTO();
    responseDTO.fail("token 已经失效");
    if (StringUtils.isNotBlank(token)) {
      String username = JwtUtil.getUserNameFromToken(token);
      String id = JwtUtil.getUserIdFromToken(token);
      if (StringUtils.isNotBlank(username) && StringUtils.isNotBlank(id)) {
        PModuleUser pModuleUser = pModuleUserService.selectUserByName(username);
        PModuleUser moduleUser = new PModuleUser();
        moduleUser.setUserId(id);
        moduleUser.setUsername(username);
        moduleUser.setUpdateTime(pModuleUser.getUpdateTime());
        responseDTO.success(moduleUser);
      }
    }
    return JSONObject.toJSONString(responseDTO);
  }

//    /**
//     * 获取当前登录的用户的权限信息
//     *
//     * @param request
//     * @return
//     */
//    @GetMapping(value = "/getUserPermission")
//    public String getUserPermission(HttpServletRequest request) {
//        String token = (String) SecurityUtils.getSubject().getPrincipal();
//        log.info("当前登录信息中的token: {} ", token);
//        List<ModuleUserPermissionDTO> permissions = new ArrayList<>();
//        if (StringUtils.isNotBlank(token)) {
//            String id = JwtUtil.getUserIdFromToken(token);
//            if (StringUtils.isNotBlank(id)) {
//                permissions = userService.getUserEnvPermission(id);
//            }
//        }
//        return JSONObject.toJSONString(permissions);
//    }
  
  /**
   * 获取当前用户拥有的所有权限数据,并以相同的数据结构拿到数据
   *
   * @return
   */
  @GetMapping(value = "/getUserPermission")
  public String getUserAllPermission() {
    //根据保存着shiro中的token获取得到用户权限信息
    String token = (String) SecurityUtils.getSubject().getPrincipal();
    //  log.info("当前登录信息中的token: {} ", token);
    List<ModuleUserPermissionDTO> permissions = new ArrayList<>();
    if (StringUtils.isNotBlank(token)) {
      String username = JwtUtil.getUserNameFromToken(token);
      if (StringUtils.isNotBlank(username)) {
        permissions = pModuleUserService.getAllPermissionsByUserName(username);
      }
    }
    return JSONObject.toJSONString(permissions);
  }
}
