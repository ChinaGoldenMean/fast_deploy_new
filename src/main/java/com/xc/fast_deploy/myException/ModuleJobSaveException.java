package com.xc.fast_deploy.myException;

public class ModuleJobSaveException extends RuntimeException {
  public ModuleJobSaveException() {
    super();
  }
  
  public ModuleJobSaveException(String message) {
    super(message);
  }
}
