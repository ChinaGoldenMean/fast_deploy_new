package com.xc.fast_deploy.configuration;

import com.ctg.itrdc.cache.pool.CtgJedisPool;
import com.ctg.itrdc.cache.pool.CtgJedisPoolConfig;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisPoolConfig;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class RedisCacheConfig {
  
  @Value("${redisConfig.ip}")
  private String redisIp;
  @Value("${redisConfig.port}")
  private Integer redisPort;
  @Value("${redisConfig.database}")
  private Integer redisDataBase;
  @Value("${redisConfig.password}")
  private String redisPassword;
  @Value("${redisConfig.maxIdle}")
  private Integer maxIdle;
  @Value("${redisConfig.minIdle}")
  private Integer minIdle;
  @Value("${redisConfig.maxTotal}")
  private Integer maxTotal;
  @Value("${redisConfig.MaxWaitMillis}")
  private Integer maxWaitMillis;
  
  @Bean
  public CtgJedisPool ctgJedisPool() {
    List<HostAndPort> hostAndPortList = new ArrayList();
    String[] strArr = redisIp.split(",");
    for (String str : strArr) {
      HostAndPort host = new HostAndPort(str, redisPort);
      hostAndPortList.add(host);
    }
    GenericObjectPoolConfig poolConfig = new JedisPoolConfig();
    poolConfig.setMaxTotal(maxTotal);
    poolConfig.setMinIdle(minIdle);
    poolConfig.setMaxIdle(maxIdle);
    poolConfig.setMaxWaitMillis(maxWaitMillis);
    CtgJedisPoolConfig config = new CtgJedisPoolConfig(hostAndPortList);
    config.setDatabase(redisDataBase)
        .setPoolConfig(poolConfig)
        .setPassword(redisPassword)
        .setMonitorTimeout(20000).setConnectionTimeout(200000).setSoTimeout(200000);
  
    return new CtgJedisPool(config);
  }
}
