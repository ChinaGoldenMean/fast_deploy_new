package com.xc.fast_deploy.dto.module;

import com.xc.fast_deploy.model.master_model.ModuleMirror;
import lombok.Data;

import java.io.Serializable;

@Data
public class ModuleMirrorCertificateEnvDTO extends ModuleMirror implements Serializable {
  
  private Integer envId;
  private String username;
  private String password;
  private String harborUrl;
  
}
