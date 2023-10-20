package com.xc.fast_deploy.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class FastJsonConfiguration extends WebMvcConfigurationSupport {
  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");
    registry.addResourceHandler("/**").addResourceLocations(
        "classpath:/META-INF/resources/dist/");
    /** 配置knife4j 显示文档 */
    registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
    /**
     * 配置swagger-ui显示文档
     */
    registry.addResourceHandler("doc.html")
        .addResourceLocations("classpath:/META-INF/resources/");
    registry.addResourceHandler("swagger-ui.html")
        .addResourceLocations("classpath:/META-INF/resources/");
    super.addResourceHandlers(registry);
    
  }
  
  @Override
  protected void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
    for (HttpMessageConverter<?> converter : converters) {
      // 解决 Controller 返回普通文本中文乱码问题
      if (converter instanceof StringHttpMessageConverter) {
        ((StringHttpMessageConverter) converter).setDefaultCharset(StandardCharsets.UTF_8);
      }
      
      // 解决 Controller 返回json对象中文乱码问题
      if (converter instanceof MappingJackson2HttpMessageConverter) {
        ((MappingJackson2HttpMessageConverter) converter).setDefaultCharset(StandardCharsets.UTF_8);
      }
    }
  }
}