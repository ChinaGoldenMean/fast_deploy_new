package com.xc.fast_deploy.model.master_model;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.Date;

@Data
public class PModuleUser {
  
  //id
  private Integer id;
  
  //用户名
  private String username;
  
  //电话
  private String mobile;
  
  //微信号
  private String wxcode;
  
  //备注
  private String remark;
  
  //权限信息
  private String userPermissionInfo;
  
  //用户id
  private String userId;
  
  //密码
  private String password;
  
  //昵称
  private String nickname;
  
  //是否为超级管理员
  private Boolean isSuperuser;
  
  //首名字
  private String firstName;
  
  //尾名字
  private String lastName;
  
  //邮箱
  private String email;
  
  //是否激活可用
  private String isActive;
  
  //qq号
  private String qq;
  
  //中文名
  private String cname;
  
  //是否为staff
  private Boolean isStaff;
  
  //加入时间
  @JSONField(format = "yyyy-MM-dd HH:mm:ss")
  private Date dateJoined;
  
  //级别
  private Integer llevel;
  
  //上次登录时间
  @JSONField(format = "yyyy-MM-dd HH:mm:ss")
  private Date lastLogin;
  
  //部门id
  private String depidId;
  
  //上级用户id
  private String puserId;
  
  //某种类型
  private Integer orgdtype;
  
  //???
  private Integer trx;
  
  //登录失败次数
  private Integer loginErrCount;
  
  //用户锁定状态
  private Integer isLocked;
  
  //上次登录失败时间
  private Date lastLoginErrTime;
  
  //锁定时间
  @JSONField(format = "yyyy-MM-dd HH:mm:ss")
  private Date clocktime;
  
  @JSONField(format = "yyyy-MM-dd HH:mm:ss")
  private Date createTime;
  
  @JSONField(format = "yyyy-MM-dd HH:mm:ss")
  private Date updateTime;
  
}