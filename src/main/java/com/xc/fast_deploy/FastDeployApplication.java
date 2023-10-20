package com.xc.fast_deploy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
//@MapperScan("com.xc.fast_deploy.dao")
//@EnableAsync
public class FastDeployApplication {
  
  public static void main(String[] args) {
    SpringApplication.run(FastDeployApplication.class, args);
  }
  
}
