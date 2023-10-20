package com.xc.fast_deploy.utils.k8s.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserDTO implements Serializable {
  private static final long serialVersionUID = -6234538412540743726L;
  
  private UserVo user;
  private String name;
  
}
