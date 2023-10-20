package com.xc.fast_deploy.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class HttpResponseDTO implements Serializable {
  
  private static final long serialVersionUID = -2341577248581411560L;
  
  private String status;
  
  private String detail;

//    public static void main(String[] args) {
//        String s = "{\"status\":\"ok\",\"detail\":{\"username\":\"jellf\",\"cname\":\"msmmd\",\"QQ\":\"323546\"}}";
//
//        HttpResponseDTO httpResponse = JSONObject.parseObject(s, HttpResponseDTO.class);
//        String detail = httpResponse.getDetail();
//        System.out.println(detail);
//        ModuleUser user = JSONObject.parseObject(detail, ModuleUser.class);
//        System.out.println(user);
//
//    }
}
