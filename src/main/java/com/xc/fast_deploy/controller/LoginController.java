package com.xc.fast_deploy.controller;

import com.alibaba.fastjson.JSONObject;
import com.xc.fast_deploy.dto.HttpResponseDTO;
import com.xc.fast_deploy.model.master_model.ModuleUser;
import com.xc.fast_deploy.rediscache.JedisManage;
import com.xc.fast_deploy.service.common.ModuleUserService;
import com.xc.fast_deploy.service.permission.PModuleUserService;
import com.xc.fast_deploy.shiro.token.JwtUtil;
import com.xc.fast_deploy.utils.HttpUtils;
import com.xc.fast_deploy.utils.SessionCookieUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

import static com.xc.fast_deploy.shiro.token.JwtUtil.REDIS_EXPIRE_TIME;

@Controller
@Slf4j
public class LoginController {
  
  @Autowired
  private ModuleUserService userService;
  
  @Autowired
  private PModuleUserService pModuleUserService;
  
  /**
   * 12-02 针对redis的临时操作方式
   */
  @Resource
  private JedisManage jedisManage;
  
  /**
   * 登录成功后生成token的操作
   *
   * @param request
   * @return
   */
  @GetMapping(value = "/login")
  public String login(HttpServletRequest request, HttpServletResponse response) {
    log.info("登录校验");
    //cookie 验证不通过则根据请求内容调用isLogin方法 判断是否登录状态
    //需要验证请求中是否有token信息 存在的情况下再去做验证
    Enumeration<String> names = request.getParameterNames();
    String key = null;
    while (names.hasMoreElements()) {
      key = names.nextElement();
    }
    //zdyw的相关token
    String token = request.getParameter(key);
    log.info("数据入参: key: " + key + " value: " + token);
    String s = HttpUtils.httpGetUrl(HttpUtils.JUDGE_IS_LOGIN, key, token);
    log.info("判断islogin 结果: {}", s);
    if (StringUtils.isNotBlank(s)) {
      HttpResponseDTO responseDTO = JSONObject.parseObject(s, HttpResponseDTO.class);
      if ("ok".equals(responseDTO.getStatus())) {
        ModuleUser moduleUser = JSONObject.parseObject(responseDTO.getDetail(), ModuleUser.class);
        String sign = JwtUtil.sign(moduleUser.getUsername(), moduleUser.getId(), JwtUtil.SECRET);
        String userId = moduleUser.getId();
        //记录zdyw的cookie key和value信息
        jedisManage.setExHash(userId, HttpUtils.ZDYW_COOKEY_KEY,
            token, REDIS_EXPIRE_TIME);
        //记录登录成功的信息
        jedisManage.setEx(JwtUtil.SELF_USER_NAME + userId,
            sign, REDIS_EXPIRE_TIME);
        Cookie mycookie = new Cookie("ticket", sign);
        mycookie.setPath("/");
        //cookie有效时间30分钟
        mycookie.setMaxAge(30 * 60 * 1000);
        //将cookie返回给页面
        response.addCookie(mycookie);
        log.info("执行相关校验成功,并成功返回");
        return "index";
      }
    }
    //其他情况直接跳转到登录页面
    return "redirect:" + HttpUtils.LOGIN_URL;
  }
  // TODO: 2020/5/14  结合zdyw时的登出接口
//    @GetMapping(value = "/logout")
//    public void logoutIndex(HttpServletRequest request, HttpServletResponse response) {
//        //然后再将token失效掉 暂时做不到 只能设置token有效性比较长,重新签发token
//        log.info("登出系统调用");
//        //清除数据里面的权限数据
//        String userId = JwtUtil.getUserIdFromToken((String) SecurityUtils.getSubject().getPrincipal());
//        if (StringUtils.isNotBlank(userId)) {
//            String sessionValue = jedisManage.hGet(userId, HttpUtils.ZDYW_COOKEY_KEY);
//            jedisManage.hdel(userId, HttpUtils.ZDYW_COOKEY_KEY);
//            jedisManage.delete(JwtUtil.SELF_USER_NAME + userId);
//            //todo 登出结合zdyw的时候是删除自己表中的权限数据
//            userService.deletePermissionByUserId(userId);
//            log.info("登出请求 key: {}, value: {}", HttpUtils.ZDYW_COOKEY_KEY, sessionValue);
//            if (StringUtils.isNotBlank(sessionValue)) {
//                HttpUtils.httpGetUrl(HttpUtils.LOGOUT_URL,
//                        HttpUtils.ZDYW_COOKEY_KEY, sessionValue);
//            }
//        }
//    }
  
  //新的登出接口
  @GetMapping(value = "/logout")
  public String logoutIndex(HttpServletRequest request, HttpServletResponse response) {
    //然后再将token失效掉 暂时做不到 只能设置token有效性比较长,重新签发token
    log.info("登出系统调用");
    //清除cookie内容即可
    String userId = JwtUtil.getUserIdFromToken((String) SecurityUtils.getSubject().getPrincipal());
    
    //兼容原有的登出请求
    String sessionValue = jedisManage.hGet(userId, HttpUtils.ZDYW_COOKEY_KEY);
    log.info("登出请求 key: {}, value: {}", HttpUtils.ZDYW_COOKEY_KEY, sessionValue);
    if (StringUtils.isNotBlank(sessionValue)) {
      jedisManage.hdel(userId, HttpUtils.ZDYW_COOKEY_KEY);
      jedisManage.delete(JwtUtil.SELF_USER_NAME + userId);
      HttpUtils.httpGetUrl(HttpUtils.LOGOUT_URL,
          HttpUtils.ZDYW_COOKEY_KEY, sessionValue);
    }
    //新的登出只需要清除session cookie即可
    if (StringUtils.isNotBlank(userId)) {
      SessionCookieUtils.clearCookieAll(request);
      return "login";
      //跳转到登录页面
//            try {
//                response.sendRedirect("/loginHtml");
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
    }
    return "redirect:" + HttpUtils.LOGIN_URL;
  }
  
  @GetMapping(value = "/index")
  public String showIndex() {
    return "index";
  }
  
  @GetMapping(value = "/loginHtml")
  public String showLogin() {
    return "login";
  }

//
//    @GetMapping(value = "/doc")
//    public String showDoc() {
//        return "fast_deploy";
//    }
//
//    @GetMapping(value = "/doc2")
//    public String showDoc2() {
//        return "fast_deploy_add";
//    }
  
  @GetMapping(value = "/redis/data/all")
  @ResponseBody
  public String getJedisAllDataToShow() {
    List<String> keyList = jedisManage.keysGet(900);
    return JSONObject.toJSONString(keyList);
  }
  
  @DeleteMapping(value = "/redis/data/")
  @ResponseBody
  public String deleteRedisData(String key, String... fields) {
    long num = 0;
    if (fields == null || fields.length <= 0) {
      num = jedisManage.delete(key);
    } else {
      num = jedisManage.hdel(key, fields);
    }
    return JSONObject.toJSONString(num);
  }
  
}
