package com.xc.fast_deploy.configuration;

import com.xc.fast_deploy.annotation.JudgePermission;
import com.xc.fast_deploy.myException.UnauthorizedException;
import com.xc.fast_deploy.service.common.ModuleManageService;
import com.xc.fast_deploy.service.common.ModuleUserService;
import com.xc.fast_deploy.shiro.token.JwtUtil;
import com.xc.fast_deploy.utils.PermissionJudgeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.util.Set;

@Aspect
@Slf4j
@Configuration
public class AspConfig {

//    @Pointcut(value = "execution(* com.xc.fast_deploy.controller.*.*(..)) && " +
//            "!execution(* com.xc.fast_deploy.controller.LoginController.*(..)) &&" +
//            "!execution(* com.xc.fast_deploy.controller.UserController.*(..)) &&" +
//            "!execution(* com.xc.fast_deploy.controller.ModuleShowController.*(..)) &&" +
//            "!execution(* com.xc.fast_deploy.controller.K8sBillingController.*(..))")
//    public void allContoller() {
//    }

//    @Pointcut(value = "execution(* com.xc.fast_deploy.controller.K8sBillingController.*(..)) ")
//    public void billingContoller() {
//    }
  
  @Autowired
  private ModuleManageService manageService;
  
  @Autowired
  private ModuleUserService userService;
  
  //针对计费用户的接口做的aop控制
//    @Before(value = "billingContoller()")
//    public void beforeBillController() {
////        log.info("billingcontroller 相关验证");
//
//        ModuleUser userInfo = JwtUtil.getModuleUserInfo();
//        if (!JwtUtil.BILLING_USER_ID.equals(userInfo.getId())) {
//            throw new UnauthorizedException();
//        }
//        ModuleManage moduleManage = manageService.selectById(23);
//        log.info("查询结果为:{}", JSONObject.toJSONString(moduleManage));
//    }
  
  @Before(value = "@annotation(judgePermission)")
  public void before(JoinPoint joinPoint, JudgePermission judgePermission) {
    int i = judgePermission.envIdIndex();
    String permissionName = judgePermission.permissionName();
    //权限验证名不可少
    if (StringUtils.isBlank(permissionName)) {
      throw new RuntimeException("权限验证注解错误");
    }
    
    //这种情况不限定环境,即任一环境下有这个权限即可访问
    if (i == -1) {
      //权限判断,判断用户在该环境下是否存在权限关联即可
      Set<String> permissionSets = userService.getAllPermission(JwtUtil.getUserIdFromToken(
          (String) SecurityUtils.getSubject().getPrincipal()));
      if (permissionSets == null || !permissionSets.contains(permissionName)) {
        throw new UnauthorizedException();
      }
    } else {
      //这种方式即表明已经指定envId的位置然后获取的环境id之后进行相关的判断
      Object[] args = joinPoint.getArgs();
      if (args.length <= 0 || args[i] == null || !(args[i] instanceof Integer)) {
        throw new RuntimeException("权限验证注解错误");
      }
      Integer envId = (Integer) args[i];
      PermissionJudgeUtils.judgeUserPermission(userService,
          permissionName,
          JwtUtil.getUserIdFromToken((String)
              SecurityUtils.getSubject().getPrincipal()),
          envId);
    }
    
  }
}
