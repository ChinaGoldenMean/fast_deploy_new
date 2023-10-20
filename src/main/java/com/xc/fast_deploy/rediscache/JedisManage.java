package com.xc.fast_deploy.rediscache;

import com.ctg.itrdc.cache.pool.CtgJedisPool;
import com.ctg.itrdc.cache.pool.CtgJedisPoolException;
import com.ctg.itrdc.cache.pool.ProxyJedis;
import org.springframework.stereotype.Component;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;

import javax.annotation.Resource;
import java.util.List;

@Component
public class JedisManage {
  
  @Resource
  private CtgJedisPool jedisPool;
  
  public boolean setNx(String key, String value) {
    ProxyJedis jedis = null;
    try {
      jedis = jedisPool.getResource();
      return jedis.setnx(key, value) > 0;
    } catch (CtgJedisPoolException e) {
      e.printStackTrace();
    } finally {
      if (jedis != null) {
        jedis.close();
      }
    }
    return false;
  }
  
  public boolean setEx(String key, String value, int delaySeconds) {
    ProxyJedis jedis = null;
    try {
      jedis = jedisPool.getResource();
      return "OK".equals(jedis.setex(key, delaySeconds, value));
    } catch (CtgJedisPoolException e) {
      e.printStackTrace();
    } finally {
      if (jedis != null) {
        jedis.close();
      }
    }
    return false;
  }
  
  public boolean refresh(String key, int delaySeconds) {
    ProxyJedis jedis = null;
    try {
      jedis = jedisPool.getResource();
      return jedis.expire(key, delaySeconds) > 0;
    } catch (CtgJedisPoolException e) {
      e.printStackTrace();
    } finally {
      if (jedis != null) {
        jedis.close();
      }
    }
    return false;
  }
  
  public boolean setExHash(String key, String field, String value, int delaySeconds) {
    ProxyJedis jedis = null;
    try {
      jedis = jedisPool.getResource();
      Long hset = jedis.hset(key, field, value);
      Long expire = jedis.expire(key, delaySeconds);
      return hset > 0 && expire > 0;
    } catch (CtgJedisPoolException e) {
      e.printStackTrace();
    } finally {
      if (jedis != null) {
        jedis.close();
      }
    }
    return false;
  }
  
  public void set(String key, String value) {
    ProxyJedis jedis = null;
    try {
      jedis = jedisPool.getResource();
      jedis.set(key, value);
    } catch (CtgJedisPoolException e) {
      e.printStackTrace();
    } finally {
      if (jedis != null) {
        jedis.close();
      }
    }
  }
  
  public String get(String key) {
    ProxyJedis jedis = null;
    String back = null;
    try {
      jedis = jedisPool.getResource();
      back = jedis.get(key);
    } catch (CtgJedisPoolException e) {
      e.printStackTrace();
    } finally {
      if (jedis != null) {
        jedis.close();
      }
    }
    return back;
  }
  
  public void hSet(String key, String field, String value) {
    ProxyJedis jedis = null;
    try {
      jedis = jedisPool.getResource();
      Long aLong = jedis.hset(key, field, value);
    } catch (CtgJedisPoolException e) {
      e.printStackTrace();
    } finally {
      if (jedis != null) {
        jedis.close();
      }
    }
  }
  
  public String hGet(String key, String field) {
    ProxyJedis jedis = null;
    String value = null;
    try {
      jedis = jedisPool.getResource();
      value = jedis.hget(key, field);
    } catch (CtgJedisPoolException e) {
      e.printStackTrace();
    } finally {
      if (jedis != null) {
        jedis.close();
      }
    }
    return value;
  }
  
  public Long hdel(String key, String... fields) {
    ProxyJedis jedis = null;
    Long result = null;
    try {
      jedis = jedisPool.getResource();
      result = jedis.hdel(key, fields);
    } catch (CtgJedisPoolException e) {
      e.printStackTrace();
    } finally {
      if (jedis != null) {
        jedis.close();
      }
    }
    return result;
  }
  
  public List<String> keysGet(int size) {
    ProxyJedis jedis = null;
    List<String> back = null;
    try {
      jedis = jedisPool.getResource();
      ScanParams scanParams = new ScanParams();
      scanParams.count(size);
      ScanResult<String> scanResult = jedis.scan("", scanParams);
      back = scanResult.getResult();
    } catch (CtgJedisPoolException e) {
      e.printStackTrace();
    } finally {
      if (jedis != null) {
        jedis.close();
      }
    }
    return back;
  }
  
  //删除key
  public Long delete(String key) {
    ProxyJedis jedis = null;
    Long aLong = null;
    try {
      jedis = jedisPool.getResource();
      aLong = jedis.del(key);
    } catch (CtgJedisPoolException e) {
      e.printStackTrace();
    } finally {
      if (jedis != null) {
        jedis.close();
      }
    }
    return aLong;
  }
  
  public ProxyJedis getJedis() throws CtgJedisPoolException {
    return jedisPool.getResource();
  }
  
}
