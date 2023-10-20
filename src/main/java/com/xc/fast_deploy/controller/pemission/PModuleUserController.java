package com.xc.fast_deploy.controller.pemission;

import com.xc.fast_deploy.dto.MyPageInfo;
import com.xc.fast_deploy.dto.ResponseDTO;
import com.xc.fast_deploy.model.master_model.ModuleUser;
import com.xc.fast_deploy.model.master_model.PModuleUser;
import com.xc.fast_deploy.myException.UnauthorcateException;
import com.xc.fast_deploy.myException.UnauthorizedException;
import com.xc.fast_deploy.myenum.ResponseEnum;
import com.xc.fast_deploy.service.common.ModuleUserService;
import com.xc.fast_deploy.service.permission.PModuleUserService;
import com.xc.fast_deploy.shiro.token.JwtUtil;
import com.xc.fast_deploy.utils.PermissionJudgeUtils;
import com.xc.fast_deploy.utils.code_utils.ExcelPhraseUtils;
import com.xc.fast_deploy.utils.encyption_utils.Pbkdf2Sha256;
import com.xc.fast_deploy.vo.module_vo.param.PModuleUserSelectParamVo;
import com.xc.fast_deploy.vo.module_vo.permission.ModuleUserEnvRoleDto;
import com.xc.fast_deploy.vo.module_vo.permission.ModuleUserEnvRoleParamVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
@RequestMapping(value = "/pUser")
@Slf4j
public class PModuleUserController {
  
  @Value("${myself.pspass.prod}")
  private boolean isProdEnv;
  @Autowired
  private PModuleUserService pModuleUserService;
  @Autowired
  private ModuleUserService userService;
  
  //注册用户 //并默认添加角色
  @PostMapping(value = "/register")
  @ResponseBody
  public ResponseDTO addUser(PModuleUser moduleUser) {
    ResponseDTO responseDTO = new ResponseDTO();
    responseDTO.fail("操作失败");
    log.info(moduleUser.toString());
    if (!validate(moduleUser)) {
      if (checkPassComplex(moduleUser.getPassword())) {
        responseDTO = pModuleUserService.addUser(moduleUser);
      } else {
        responseDTO.fail("密码长度在8-16位且包含数字、大小写和字符(~!@#$%&*_?:)");
      }
    } else {
      responseDTO.fail("输入参数不合规范");
    }
    return responseDTO;
  }
  
  //校验数据
  private boolean validate(PModuleUser moduleUser) {
    return (moduleUser == null
        || StringUtils.isBlank(moduleUser.getUsername())
        || StringUtils.isBlank(moduleUser.getPassword()));
  }
  
  //校验密码
  private boolean checkPassComplex(String pass) {
    //数字
    String REG_NUMBER = ".*\\d+.*";
    //小写字母
    String REG_UPPERCASE = ".*[A-Z]+.*";
    //大写字母
    String REG_LOWERCASE = ".*[a-z]+.*";
    //特殊符号
    String REG_SYMBOL = ".*[~!@#$%&*_?:]+.*";
    //密码为空或者长度小于8位大于16位则返回false
    if (pass.length() > 16 || pass.length() < 8) return false;
    //密码必须包含数字、大小写、字符(~!@#$%^&*_?:)
    if (pass.matches(REG_NUMBER) && pass.matches(REG_LOWERCASE) &&
        pass.matches(REG_UPPERCASE) && pass.matches(REG_SYMBOL)) return true;
    return false;
  }
  
  /**
   * 批量新增用户
   *
   * @param reportFile
   * @return
   */
  @PostMapping(value = "/registers")
  @ResponseBody
  public ResponseDTO batchAddUser(MultipartFile reportFile) {
    ResponseDTO responseDTO = new ResponseDTO();
    responseDTO.fail("添加失败");
    if (reportFile.isEmpty()) return responseDTO.fail("文件为空");
    List<PModuleUser> userList = ExcelPhraseUtils.getUserExcel(reportFile);
    Map<String, String> result = new HashMap<>();
    for (PModuleUser user : userList) {
      if (StringUtils.isBlank(user.getPassword()) &&
          !checkPassComplex(user.getPassword())) {
        result.put(user.getCname(), "密码长度在8-16位且包含数字、大小写和字符(~!@#$%&*_?:)");
        continue;
      }
      responseDTO = pModuleUserService.addUser(user);
      if (responseDTO.getCode().equals(ResponseEnum.FAIL.getStatus())) {
        result.put(user.getCname(), responseDTO.getMsg());
      }
    }
    if (!result.isEmpty()) responseDTO.fail(result);
    return responseDTO;
  }
  
  //新权限登录接口校验 即登录成功之后直接获取到token,由客户端保存token
  //申请token的接口
  @PostMapping(value = "/pLogin")
  @ResponseBody
  public ResponseDTO pLoginUser(String username, String password, HttpServletResponse response) {
    log.info("登录输入参数为: username:{},password:{}", username, "***");
    ResponseDTO responseDTO = new ResponseDTO();
    String jwtToken = null;
    //判断用户是否登录成功
    if (StringUtils.isBlank(username) || StringUtils.isBlank(password)) {
      responseDTO.fail("请输入用户名和密码!");
    } else {
      try {
        PModuleUser moduleUser = pModuleUserService.selectUserByName(username);
        if (moduleUser == null) {
          return responseDTO.fail("该用户并不存在!");
        }
        Date thisErrorLoginTime = null;      // 修改的本次登陆错误时间
        Integer islocked = 0;         // 获取是否锁定状态
        if (moduleUser.getIsLocked() == null) {
          moduleUser.setIsLocked(0);
        } else {
          islocked = moduleUser.getIsLocked();
        }
        //密码校验
        if (!Pbkdf2Sha256.verification(password, moduleUser.getPassword())) {
          if (moduleUser.getLoginErrCount() == null) {
            moduleUser.setLoginErrCount(0);
          }
          Date date = new Date();
          SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
          String datestr = format.format(date);
          try {
            thisErrorLoginTime = format.parse(datestr);
          } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
          }
          if (islocked == 1) {                     // 账户被锁定 // 被锁定是登陆错误次数一定是10,所以只判断一次
            Date lastLoginErrorTime = null; // 最后一次登陆错误时间
            Long timeSlot = 0L;
            if (moduleUser.getLastLoginErrTime() == null) {
              lastLoginErrorTime = thisErrorLoginTime;
            } else {
              lastLoginErrorTime = moduleUser.getLastLoginErrTime();
              timeSlot = thisErrorLoginTime.getTime() - lastLoginErrorTime.getTime();
            }
            if (timeSlot < 300000) {   // 判断最后锁定时间,5分钟之内继续锁定
              return responseDTO.fail("您的账户已被锁定，请" + (5 - Math.ceil((double) timeSlot / 60000)) + "分钟之后再次尝试");
            } else {                           // 判断最后锁定时间,5分钟之后仍是错误,继续锁定5分钟
              moduleUser.setLastLoginErrTime(thisErrorLoginTime);
              pModuleUserService.updateByPrimaryKeySelective(moduleUser);
              return responseDTO.fail("密码错误");
            }
          } else if (moduleUser.getLoginErrCount() == 9) {   // 账户第9次登陆失败,此时登陆错误次数增加至10,以后错误仍是10,不再递增
            moduleUser.setLoginErrCount(10);
            moduleUser.setIsLocked(1);
            moduleUser.setLastLoginErrTime(thisErrorLoginTime);
            pModuleUserService.updateByPrimaryKeySelective(moduleUser);               //修改用户
            return responseDTO.fail("密码错误");
          } else {
            // 账户前5次登陆失败
            moduleUser.setLoginErrCount(moduleUser.getLoginErrCount() + 1);
            moduleUser.setLastLoginErrTime(thisErrorLoginTime);
            pModuleUserService.updateByPrimaryKeySelective(moduleUser);               //修改用户
            return responseDTO.fail("密码错误");
          }
        } else {
          if (islocked == 1) {
            Date lastLoginErrorTime = null; // 最后一次登陆错误时间
            Long timeSlot = 0L;
            if (moduleUser.getLastLoginErrTime() == null) {
              lastLoginErrorTime = new Date();
            } else {
              lastLoginErrorTime = moduleUser.getLastLoginErrTime();
              timeSlot = new Date().getTime() - lastLoginErrorTime.getTime();
            }
            if (timeSlot < 300000) {   // 判断最后锁定时间,5分钟之内继续锁定
              return responseDTO.fail("您的账户已被锁定，请" + (5 - Math.ceil((double) timeSlot / 60000)) + "分钟之后再次尝试");
            } else {                           // 判断最后锁定时间,5分钟之后登陆账户
              Date d = new Date();
              moduleUser.setLoginErrCount(0);
              moduleUser.setIsLocked(0);
              moduleUser.setLastLogin(d);
              pModuleUserService.updateByPrimaryKeySelective(moduleUser);//修改用户表登录时间
            }
          } else {
            Date d = new Date();
            moduleUser.setLoginErrCount(0);
            moduleUser.setIsLocked(0);
            moduleUser.setLastLogin(d);
            pModuleUserService.updateByPrimaryKeySelective(moduleUser);//修改用户表登录时间
          }
        }
        //throw new UnauthorcateException("密码错误!");
        
        //登录成功签名token
        jwtToken = JwtUtil.sign(moduleUser.getUsername(),
            moduleUser.getUserId(), JwtUtil.SECRET);
      } catch (UnauthorcateException e) {
        responseDTO.fail(e.getMessage());
      }
      //登录成功之后返回成功的token信息在cookie中
      Cookie mycookie = new Cookie("ticket", jwtToken);
      mycookie.setPath("/");
      //cookie有效时间30分钟
      mycookie.setMaxAge((int) JwtUtil.EXPIRE_TIME);
      Cookie isProd = new Cookie("isProd", String.valueOf(isProdEnv));
      isProd.setPath("/");
      //cookie有效时间30分钟
      isProd.setMaxAge((int) JwtUtil.EXPIRE_TIME);
      //将cookie返回给页面
      response.addCookie(isProd);
      response.addCookie(mycookie);
      //返回token
      responseDTO.success(jwtToken);
    }
    return responseDTO;
  }
  
  //添加用户关联角色信息
  @PostMapping(value = "/addUserRoleBind")
  @ResponseBody
  public ResponseDTO addUserRoleBind(Integer userId, Integer roleId,
                                     Integer[] envIds, Integer[] centerIds) {
    ResponseDTO responseDTO = new ResponseDTO();
    responseDTO.fail("操作失败");
    log.info("userid:", userId);
    if (roleId == null || userId == null || envIds == null || envIds.length <= 0) {
      responseDTO.fail("输入参数不符合要求");
    } else {
      responseDTO = pModuleUserService.addUserBindRole(userId, roleId, envIds, centerIds);
    }
    return responseDTO;
  }
  
  @PostMapping(value = "/batchAddUserRoleBind")
  @ResponseBody
  public ResponseDTO batchAddUserRoleBind(String userNames, Integer roleId,
                                          Integer envId, String centerNames) {
    ResponseDTO responseDTO = new ResponseDTO();
    responseDTO.fail("操作失败");
    if (roleId == null || userNames == null || envId == null || centerNames == null) {
      responseDTO.fail("输入参数不符合要求");
    } else {
      responseDTO = pModuleUserService.batchAddUserRoleBind(userNames, roleId, envId, centerNames);
    }
    return responseDTO;
  }
  
  //修改用户关联角色信息
  @PostMapping(value = "/updateUserRoleBind")
  @ResponseBody
  public ResponseDTO updateUserRoleBind(Integer userRoleBindId, Integer userId,
                                        Integer roleId, Integer[] envIds, Integer[] centerIds) {
    ResponseDTO responseDTO = new ResponseDTO();
    responseDTO.fail("操作失败");
    if (roleId == null || userId == null) {
      responseDTO.fail("输入参数不符合要求");
    } else {
      responseDTO = pModuleUserService.updateUserRoleBind(userRoleBindId, userId, roleId, envIds, centerIds);
    }
    return responseDTO;
  }
  
  //查询用户拥有的所有角色信息 用户--环境--角色 并分页显示
  @GetMapping(value = "/getUserRoleEnvBind")
  @ResponseBody
  public ResponseDTO getUserRoleEnvBind(
      @RequestParam(value = "pageNum", defaultValue = "1", required = false) Integer pageNum,
      @RequestParam(value = "pageSize", defaultValue = "10", required = false) Integer pageSize,
      ModuleUserEnvRoleParamVo userEnvRoleParamVo) {
    ResponseDTO responseDTO = new ResponseDTO();
    responseDTO.fail("未查询到任何数据");
    MyPageInfo<ModuleUserEnvRoleDto> jobVoMyPageInfo =
        pModuleUserService.selectUserRoleBindPageAllInfo(pageNum, pageSize,
            userEnvRoleParamVo);
    if (jobVoMyPageInfo.getList() != null && jobVoMyPageInfo.getList().size() > 0) {
      responseDTO.success(jobVoMyPageInfo);
    }
    return responseDTO;
  }
  
  //批量删除用户角色绑定关系
  @PostMapping(value = "/deleteUserRoleEnvBind")
  @ResponseBody
  public ResponseDTO deleteUserRoleEnvBind(Integer[] userRoleBindIds) {
    ResponseDTO responseDTO = new ResponseDTO();
    responseDTO.fail("删除失败");
    if (pModuleUserService.deleteUserRoleBind(userRoleBindIds)) {
      responseDTO.success("删除成功");
    }
    return responseDTO;
  }
  
  //删除用户 同时删除用户关联的角色信息
  @PostMapping(value = "/deleteUserInfo")
  @ResponseBody
  public ResponseDTO deleteUserInfo(Integer userId) {
    ResponseDTO responseDTO = new ResponseDTO();
    responseDTO.fail("删除失败");
    if (userId != null && pModuleUserService.deleteUserInfoByUserId(userId)) {
      responseDTO.success();
    }
    return responseDTO;
  }
  
  //分页查询所有用户数据
  @GetMapping(value = "/page/all")
  @ResponseBody
  public ResponseDTO getPageAllUser(
      @RequestParam(value = "pageNum", defaultValue = "1", required = false) Integer pageNum,
      @RequestParam(value = "pageSize", defaultValue = "10", required = false) Integer pageSize,
      PModuleUserSelectParamVo moduleUserSelectParamVo) {
    ResponseDTO responseDTO = new ResponseDTO();
    responseDTO.fail("未查询到任何数据");
    MyPageInfo<PModuleUser> moduleUserMyPageInfo =
        pModuleUserService.selectPageAllInfo(pageNum, pageSize, moduleUserSelectParamVo);
    if (moduleUserMyPageInfo.getList() != null && moduleUserMyPageInfo.getList().size() > 0) {
      responseDTO.success(moduleUserMyPageInfo);
    }
    return responseDTO;
  }
  
  /**
   * 获取单条用户数据的信息
   *
   * @param id
   * @return
   */
  @GetMapping(value = "/info/{id}")
  @ResponseBody
  public ResponseDTO selectOneUserById(@PathVariable Integer id) {
    ResponseDTO responseDTO = new ResponseDTO();
    PModuleUser moduleUser = pModuleUserService.selectUserInfoById(id);
    moduleUser.setPassword("***");
    responseDTO.success(moduleUser);
    return responseDTO;
  }
  
  /**
   * 修改用户信息
   *
   * @param pModuleUser
   * @return
   */
  @PostMapping(value = "/updateOne")
  @ResponseBody
  public ResponseDTO updatePModuleUser(PModuleUser pModuleUser) {
    ResponseDTO responseDTO = new ResponseDTO();
    responseDTO.fail("修改失败");
    responseDTO = pModuleUserService.updateUserInfo(pModuleUser);
    return responseDTO;
  }
  
  //    /**
//     * 修改自身密码
//     * @param pass
//     * @return
//     */
  @PostMapping(value = "/changePass")
  @ResponseBody
  public ResponseDTO changePass(String pass) {
    //log.info("传入参数userId,pass",pass);
    ResponseDTO responseDTO = new ResponseDTO();
    responseDTO.fail("修改失败");
    ModuleUser moduleUser = JwtUtil.getModuleUserInfo();
    //权限判断,判断用户在该环境下是否存在权限关联即可
//        PermissionJudgeUtils.judgeUserPermission(userService,
//                "fast_deploy_admin", moduleUser.getId());
    if (StringUtils.isNotBlank(pass) && checkPassComplex(pass)) {
      responseDTO = pModuleUserService.changePass(moduleUser.getId(), pass);
    } else {
      responseDTO.fail("密码长度在8-16位且包含数字、大小写和字符(~!@#$%&*_?:)");
    }
    return responseDTO;
  }
  
}
