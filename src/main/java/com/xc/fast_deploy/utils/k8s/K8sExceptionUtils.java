package com.xc.fast_deploy.utils.k8s;

import com.google.gson.JsonSyntaxException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class K8sExceptionUtils {
  
  public static boolean anylse(JsonSyntaxException e) {
    if (e.getCause() instanceof IllegalStateException) {
      IllegalStateException ise = (IllegalStateException) e.getCause();
      if (ise.getMessage() != null && ise.getMessage().contains("Expected a string but was BEGIN_OBJECT")) {
        log.debug("Catching exception because of issue https://github.com/kubernetes-client/java/issues/86", e);
        return true;
      } else throw e;
    } else throw e;
  }
}
