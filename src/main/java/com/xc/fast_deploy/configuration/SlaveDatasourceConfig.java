package com.xc.fast_deploy.configuration;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;

@Configuration
@MapperScan(value = "com.xc.fast_deploy.dao.slave_dao", sqlSessionFactoryRef = "slaveSqlSessionFactory")
public class SlaveDatasourceConfig {
  
  @Bean(name = "slaveDatasource")
  @ConfigurationProperties(prefix = "spring.datasource.druid.slave")
  public DataSource slaveDataSource() {
    return DruidDataSourceBuilder.create().build();
  }
  
  @Bean(name = "slaveSqlSessionFactory")
  public SqlSessionFactory slaveSqlSessionFactory(@Qualifier("slaveDatasource") DataSource datasource)
      throws Exception {
    SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
    bean.setDataSource(datasource);
    bean.setMapperLocations(
        new PathMatchingResourcePatternResolver().getResources(
            "classpath*:second_mapper/*.xml"));
    bean.setTypeAliasesPackage("com.xc.fast_deploy.model.slave_model");
    return bean.getObject();
  }
  
  @Bean(name = "slaveTransactionManager")
  public DataSourceTransactionManager masterTransactionManager() {
    return new DataSourceTransactionManager(slaveDataSource());
  }
  
}
