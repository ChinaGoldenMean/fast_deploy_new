package com.xc.fast_deploy.configuration;

import com.xc.fast_deploy.shiro.filter.MyShiroFilter;
import com.xc.fast_deploy.shiro.realm.CustomRealm;
import com.xc.fast_deploy.utils.HttpUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.mgt.DefaultSessionStorageEvaluator;
import org.apache.shiro.mgt.DefaultSubjectDAO;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import javax.servlet.Filter;
import java.util.LinkedHashMap;
import java.util.Map;

@Configuration
@Slf4j
public class ShiroConfig {
  
  /**
   * 自定义身份验证
   *
   * @return
   */
  @Bean
  public CustomRealm customRealm() {
    return new CustomRealm();
  }
  
  /**
   * 注入securityManager安全管理器
   *
   * @param customRealm
   * @return
   */
  @Bean
  public DefaultSecurityManager defaultSecurityManager(CustomRealm customRealm) {
    DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
    securityManager.setRealm(customRealm);
    /*
     * 关闭shiro自带的session，详情见文档
     * http://shiro.apache.org/session-management.html#SessionManagement-StatelessApplications%28Sessionless%29
     */
    DefaultSubjectDAO subjectDAO = new DefaultSubjectDAO();
    DefaultSessionStorageEvaluator defaultSessionStorageEvaluator = new DefaultSessionStorageEvaluator();
    defaultSessionStorageEvaluator.setSessionStorageEnabled(false);
    subjectDAO.setSessionStorageEvaluator(defaultSessionStorageEvaluator);
    securityManager.setSubjectDAO(subjectDAO);
    return securityManager;
  }
  
  @Bean
  public ShiroFilterFactoryBean shirlFilter(DefaultSecurityManager securityManager) {
    ShiroFilterFactoryBean factoryBean = new ShiroFilterFactoryBean();
    
    //添加自己的过滤器
    Map<String, Filter> filterMap = new LinkedHashMap<>();
//        filterMap.put("jwt", new JwtFilter());
    filterMap.put("myShiroFilter", new MyShiroFilter());
    factoryBean.setFilters(filterMap);
    factoryBean.setSecurityManager(securityManager);
    //设置未登录的跳转的url
    factoryBean.setUnauthorizedUrl(HttpUtils.LOGIN_URL);
    //设置拦截器
    Map<String, String> filterRuleMap = new LinkedHashMap<>();
    //开发接口
    filterRuleMap.put("/swagger**", "anon");
    filterRuleMap.put("/swagger-resources/**", "anon");
    filterRuleMap.put("/webjars/**", "anon");
    filterRuleMap.put("/doc.html", "anon");
    filterRuleMap.put("/static/**", "anon");
    filterRuleMap.put("/favicon.ico", "anon");
    filterRuleMap.put("/v2/**", "anon");
    filterRuleMap.put("/actuator/**", "anon");
    
    //其余接口一律拦截
    filterRuleMap.put("/**", "myShiroFilter");
    
    factoryBean.setFilterChainDefinitionMap(filterRuleMap);
    return factoryBean;
  }
  
  /**
   * 下面的代码是添加注解支持
   */
  @Bean
  @DependsOn("lifecycleBeanPostProcessor")
  public DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator() {
    DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator = new DefaultAdvisorAutoProxyCreator();
    // 强制使用cglib，防止重复代理和可能引起代理出错的问题
    defaultAdvisorAutoProxyCreator.setProxyTargetClass(true);
    return defaultAdvisorAutoProxyCreator;
  }
  
  @Bean
  public LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
    return new LifecycleBeanPostProcessor();
  }
  
  @Bean
  public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(
      DefaultSecurityManager defaultSecurityManager) {
    AuthorizationAttributeSourceAdvisor advisor = new AuthorizationAttributeSourceAdvisor();
    advisor.setSecurityManager(defaultSecurityManager);
    return advisor;
  }
  
}
