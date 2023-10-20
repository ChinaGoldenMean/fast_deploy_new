package com.xc.fast_deploy.controller;

import com.alibaba.fastjson.JSONObject;
import com.xc.fast_deploy.dto.ResponseDTO;
import com.xc.fast_deploy.model.master_model.ModuleCertificate;
import com.xc.fast_deploy.myException.UnauthorizedException;
import com.xc.fast_deploy.myException.ValidateExcetion;
import com.xc.fast_deploy.service.common.ModuleCertificateService;
import com.xc.fast_deploy.service.common.ModuleUserService;
import com.xc.fast_deploy.shiro.token.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping(value = "/certificate")
@Slf4j
public class ModuleCertificateController {
  
  @Autowired
  private ModuleCertificateService moduleCertificateService;
  @Autowired
  private ModuleUserService userService;
  
  /**
   * 插入一条moduleCertificate数据
   *
   * @param moduleCertificate
   * @return
   */
  @PostMapping(value = "/insertOneCer")
  public String insertOneCer(ModuleCertificate moduleCertificate) {
    log.info("ModuleCertificate调用添加凭证配置模块,入参:" + JSONObject.toJSONString(moduleCertificate));
    ResponseDTO responseDTO = new ResponseDTO();
    responseDTO.fail("凭证配置模块添加失败");
    
    if (validate(moduleCertificate)) {
      Set<String> permissionSet = userService.getAllPermission(JwtUtil.getUserIdFromToken((String)
          SecurityUtils.getSubject().getPrincipal()));
      if (permissionSet == null || !permissionSet.contains("base_config_certificate_add")) {
        throw new UnauthorizedException();
      }
      int insert = moduleCertificateService.insert(moduleCertificate);
      if (insert == 1) {
        responseDTO.success("凭证配置模块添加成功");
      } else if (insert == 0) {
        responseDTO.fail("name已存在");
      } else {
        responseDTO.fail("凭证配置模块添加失败");
      }
    }
    return JSONObject.toJSONString(responseDTO);
  }
  
  /**
   * 校验凭证类型
   *
   * @param moduleCertificate
   * @return
   */
  private boolean validate(ModuleCertificate moduleCertificate) {
    if (moduleCertificate != null && moduleCertificate.getType() != null
        && StringUtils.isNotBlank(moduleCertificate.getUsername())
        && StringUtils.isNotBlank(moduleCertificate.getPassword())) {
      if (moduleCertificate.getType() == 1 || moduleCertificate.getType() == 2
          || moduleCertificate.getType() == 3) {
        return true;
      }
    }
    return false;
  }
  
  /**
   * 根据id单条查询；为更新做准备
   *
   * @param id
   * @return
   */
  @GetMapping(value = "/selectOneCer")
  public String selectOneCer(Integer id) {
    ResponseDTO responseDTO = new ResponseDTO();
    ModuleCertificate moduleCertificate = moduleCertificateService.selectById(id);
    if (moduleCertificate == null) {
      responseDTO.fail("凭证配置模块单条查询失败");
    } else {
      responseDTO.success(moduleCertificate);
    }
    return JSONObject.toJSONString(responseDTO);
  }
  
  /**
   * 更新本条数据
   *
   * @param moduleCertificate
   * @return
   */
  @PostMapping(value = "/update")
  public String update(ModuleCertificate moduleCertificate) {
    ResponseDTO responseDTO = new ResponseDTO();
    log.info("ModuleCenter调用更新中心配置模块,入参:" + JSONObject.toJSONString(moduleCertificate));
    responseDTO.fail("凭证更新失败");
    Set<String> permissionSet = userService.getAllPermission(JwtUtil.getUserIdFromToken((String)
        SecurityUtils.getSubject().getPrincipal()));
    if (permissionSet == null || !permissionSet.contains("base_config_certificate_update")) {
      throw new UnauthorizedException();
    }
    try {
      if (moduleCertificateService.update(moduleCertificate) > 0) {
        responseDTO.success("更新成功");
      }
    } catch (ValidateExcetion e) {
      responseDTO.fail(e.getMessage());
    }
    return JSONObject.toJSONString(responseDTO);
  }
  
  /**
   * 给数据做删除标记
   *
   * @param id
   * @return
   */
  @PostMapping(value = "/deleteOneCer")
  public String deleteOneCer(Integer id) {
    ResponseDTO responseDTO = new ResponseDTO();
    Set<String> permissionSet = userService.getAllPermission(JwtUtil.getUserIdFromToken(
        (String) SecurityUtils.getSubject().getPrincipal()));
    if (permissionSet == null || !permissionSet.contains("base_config_certificate_delete")) {
      throw new UnauthorizedException();
    }
    boolean b = moduleCertificateService.updateById(id);
    if (b) {
      responseDTO.success("凭证配置模块删除标记成功");
    } else {
      responseDTO.fail("凭证配置模块删除标记失败");
    }
    return JSONObject.toJSONString(responseDTO);
  }
  
  /**
   * 查询全部数据
   *
   * @return
   */
  @GetMapping(value = "/selectAll")
  public String selectAll() {
    ResponseDTO responseDTO = new ResponseDTO();
    List<ModuleCertificate> moduleCertificates = moduleCertificateService.getAllModuleCer();
    if (moduleCertificates == null || moduleCertificates.size() <= 0) {
      responseDTO.fail("未查看到任何数据");
    } else {
      responseDTO.success(moduleCertificates);
    }
    return JSONObject.toJSONString(responseDTO);
  }
  
  /**
   * 根据凭证类型查询凭证列表
   *
   * @param type
   * @return
   */
  @GetMapping(value = "/selectType")
  public String selectType(Integer type) {
    ResponseDTO responseDTO = new ResponseDTO();
    if (type == 5) {
      type = 3;
    }
    List<ModuleCertificate> typeModuleCer = moduleCertificateService.getTypeModuleCer(type);
    if (typeModuleCer == null || typeModuleCer.size() <= 0) {
      responseDTO.fail("未查看到任何数据");
    } else {
      responseDTO.success(typeModuleCer);
    }
    return JSONObject.toJSONString(responseDTO);
  }
  
}
