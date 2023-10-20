package com.xc.fast_deploy.utils;

import com.xc.fast_deploy.myException.UnauthorizedException;
import com.xc.fast_deploy.service.common.ModuleUserService;
import org.apache.commons.lang.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class PermissionJudgeUtils {
  
  /**
   * 判断用户在某个环境下是否有某个权限
   *
   * @param userService 用户service
   * @param permission  权限
   * @param userId      用户id
   * @param envId       环境id
   */
  public static void judgeUserPermission(ModuleUserService userService,
                                         String permission, String userId, Integer envId) {
    boolean flag = false;
    Map<Integer, Set<String>> envPermissionMap = userService.selectEnvPermissionByUserId(userId);
//        表明该用户无任何环境的权限或者无该环境的权限
    if (envPermissionMap.size() > 0 && envPermissionMap.containsKey(envId)) {
      Set<String> permissionSet = envPermissionMap.get(envId);
      if (StringUtils.isBlank(permission) || permissionSet.contains(permission)) {
        flag = true;
      }
    }
    if (!flag) {
      throw new UnauthorizedException();
    }
  }
  
  /**
   * 判断用户是否在任意的环境下有该权限
   *
   * @param userService
   * @param permission
   * @param userId
   */
  public static void judgeUserPermission(ModuleUserService userService,
                                         String permission, String userId) {
    boolean flag = false;
    if (StringUtils.isNotBlank(permission) && StringUtils.isNotBlank(userId)) {
      Map<Integer, Set<String>> envPermissionMap = userService.selectEnvPermissionByUserId(userId);
      if (envPermissionMap.size() > 0) {
        Set<Integer> keySet = envPermissionMap.keySet();
        for (Integer envKey : keySet) {
          Set<String> permissionSet = envPermissionMap.get(envKey);
          if (permissionSet.contains(permission)) {
            flag = true;
            break;
          }
        }
      }
    }
    if (!flag) {
      throw new UnauthorizedException();
    }
    
  }
}
