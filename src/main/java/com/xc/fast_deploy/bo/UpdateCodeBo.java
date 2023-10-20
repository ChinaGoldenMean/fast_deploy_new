package com.xc.fast_deploy.bo;

import com.xc.fast_deploy.service.common.ModulePackageService;
import lombok.Data;
import lombok.experimental.Accessors;
import org.yeauty.pojo.Session;

/**
 * @Author litiewang
 * @Date 2022-09-08 16:40
 * @Version 1.0
 */
@Data
@Accessors(chain = true)
public class UpdateCodeBo {
  
  private Integer moduleId;
  private ModulePackageService packageService;
  private Session session;
}
