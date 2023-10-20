package com.xc.fast_deploy.redis;

import com.alibaba.fastjson.JSONObject;
import com.ctg.itrdc.cache.pool.CtgJedisPoolException;
import com.ctg.itrdc.cache.pool.ProxyJedis;
import com.xc.fast_deploy.dao.master_dao.ModuleDeployMapper;
import com.xc.fast_deploy.dto.module.ModuleDeployEnvDTO;
import com.xc.fast_deploy.rediscache.JedisManage;
import com.xc.fast_deploy.service.common.ModuleDeployListService;
import com.xc.fast_deploy.service.common.ModuleDeployService;
import com.xc.fast_deploy.utils.constant.RedisKeyPrefix;
import com.xc.fast_deploy.vo.module_vo.ModuleDeployVo;
import com.xc.fast_deploy.vo.module_vo.ModuleEnvCenterManageVo;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;

import javax.annotation.Resource;
import java.util.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class JedisTest {
  
  @Resource
  JedisManage jedisManage;
  @Autowired
  private ModuleDeployMapper deployMapper;
  @Value("${myself.pspass.prodId}")
  private String[] prodId;
  @Value("${myself.pspass.prod}")
  private boolean isProdEnv;
  
  @Test
  public void testJedis() {
    ProxyJedis jedis = null;
    try {
      if (isProdEnv) {
        System.out.println("开始清理生产发布清单：" + prodId);
        jedis = jedisManage.getJedis();
        jedis.hdel(RedisKeyPrefix.PROD_REDIS_PREFIX, prodId);
      }
      //String hget = jedis.get(RedisKeyPrefix.ISONOFF_PREFIX + "pst_env_code");
//            ModuleDeployVo deployVo = new ModuleDeployVo();
//            deployVo.setEnvId(34);
//            List<ModuleEnvCenterManageVo> moduleEnvCenterManageVos =
//                    deployMapper.selectModuleEnvCenterAll(deployVo);
//            jedis.hset("userid1", "prod2", JSONObject.toJSONString(moduleEnvCenterManageVos));
//            String hget = jedis.hget("userid1", "prod2");
    } catch (CtgJedisPoolException e) {
      e.printStackTrace();
    } finally {
      if (jedis != null) {
        jedis.close();
      }
    }
  }
  
  @Autowired
  private ModuleDeployListService deployListService;
  
  @Test
  public void testJedis2() {
//        ProxyJedis jedis = null;
//        try {
//            jedis = jedisManage.getJedis();
////            Map<String, String> map = jedis.hgetAll("NONE_PORD_ZJXCA0027");
////            String hget = jedis.hget("NONE_PORD_ZJXCA0027", "10");
////            System.out.println(hget);
////            String key = RedisKeyPrefix.PROD_REDIS_PREFIX + "ZJXCA0081";
//            Map<String, String> hgetAll = jedis.hgetAll("PORD_ZJXCA0081");
//            System.out.println(hgetAll);
//
////            System.out.println(map);
//        } catch (CtgJedisPoolException e) {
//            e.printStackTrace();
//        } finally {
//            if (jedis != null) {
//                jedis.close();
//            }
//        }

//        deployListService.removeModuleDeployList(new Integer[]{1158},"ZJXCA0081");
    
    String userId = "ZJXCA0081";

//        String keyPrefix = RedisKeyPrefix.PROD_REDIS_PREFIX userId;
    long start = System.currentTimeMillis();

//            Map<String, String> allMap = jedisManage.getJedis().hgetAll(keyPrefix);
    Set<ModuleEnvCenterManageVo> allDeployListSelf = deployListService.getAllDeployListSelf(userId, null, null, null);
    
    long end = System.currentTimeMillis();
    System.out.println("耗时: " + (end - start));
//            System.out.println(allMap);
  
  }
  
  @Test
  public void testEx() {
//        boolean setEx = jedisManage.setEx("key111", "测试属性", 2);

//        String key111 = jedisManage.get("key111");
//        System.out.println(key111);
//
    jedisManage.setEx("key2", "valueEx", 10);
    
    boolean exHash = jedisManage.setExHash("key2", "field2", "value2", 3);
    String value = jedisManage.hGet("key2", "field2");
    System.out.println(value);
//        jedisManage.refresh("key2", 10);
    try {
      Thread.sleep(5000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    
    value = jedisManage.hGet("key2", "field2");
    System.out.println(value);

//        String key112 = jedisManage.get("key111");
//        System.out.println(key112);

//        boolean b = jedisManage.setNx("key111", "testnx");
//        System.out.println(b);
//
//        System.out.println(jedisManage.get("key111"));
  }
  
  @Test
  public void testJedisKeys() {
    ProxyJedis jedis = null;
    try {
      jedis = jedisManage.getJedis();
      String key1 = "userid";
      //System.out.println(jedis.hget("userid1","prod2"));
      ScanParams scanParams = new ScanParams().match(key1 + "*");
      String cur = scanParams.SCAN_POINTER_START;
      do {
        ScanResult<String> scanResult = jedis.scan(cur, scanParams);
        
        // work with result
        for (String key : scanResult.getResult()) {
          if (jedis.type(key).equals("hash")) {
            System.out.println(key + jedis.hgetAll(key));
          }
        }
        
        cur = scanResult.getStringCursor();
      } while (!cur.equals(scanParams.SCAN_POINTER_START));
//            ModuleDeployVo deployVo = new ModuleDeployVo();
//            deployVo.setEnvId(34);
//            List<ModuleEnvCenterManageVo> moduleEnvCenterManageVos =
//                    deployMapper.selectModuleEnvCenterAll(deployVo);
//            jedis.hset("userid1", "prod2", "test1");
//            jedis.hset("userid2", "prod2", "test2");
//            jedis.hset("userid3", "prod2", "test3");
////            jedis.hset("userid4", "prod2", "test4");
    } catch (CtgJedisPoolException e) {
      e.printStackTrace();
    } finally {
      if (jedis != null) {
        jedis.close();
      }
    }
  }
}
