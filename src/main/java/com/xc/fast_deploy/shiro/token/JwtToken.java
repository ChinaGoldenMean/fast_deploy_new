package com.xc.fast_deploy.shiro.token;

import org.apache.shiro.authc.AuthenticationToken;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class JwtToken implements AuthenticationToken {
  
  private String token;
  
  public JwtToken(String token) {
    this.token = token;
  }
  
  @Override
  public Object getPrincipal() {
    return token;
  }
  
  @Override
  public Object getCredentials() {
    return token;
  }
  
}
