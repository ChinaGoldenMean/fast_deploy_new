package com.xc.fast_deploy.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.yeauty.standard.ServerEndpointExporter;

@Configuration
public class WebsocketConfig {
  
  @Bean
  @Primary
  public ServerEndpointExporter serverEndpointExporter() {
    return new ServerEndpointExporter();
  }
  
}
