package com.xc.fast_deploy.service.slave.impl;

import com.alibaba.fastjson.JSONObject;
import com.xc.fast_deploy.dao.slave_dao.BillingOpOnOffMapper;
import com.xc.fast_deploy.model.slave_model.BillingOpOnOff;
import com.xc.fast_deploy.model.slave_model.example.BillingOpOnOffExample;
import com.xc.fast_deploy.rediscache.JedisManage;
import com.xc.fast_deploy.service.slave.IBillingOpOnOffService;
import com.xc.fast_deploy.utils.constant.RedisKeyPrefix;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.*;

import static com.xc.fast_deploy.utils.constant.RedisKeyPrefix.ENV_HOSTNAME_PREFIX;

@Service
@Slf4j
public class BillingOpOnOffService implements IBillingOpOnOffService {
  
  @Autowired
  private BillingOpOnOffMapper billingOpOnOffMapper;
  @Resource
  private JedisManage jedisManage;
  
  @Override
  public List<BillingOpOnOff> selectAll(Set<Integer> envIdSet) {
    BillingOpOnOffExample opOnOffExample = new BillingOpOnOffExample();
    BillingOpOnOffExample.Criteria criteria = opOnOffExample.createCriteria();
    criteria.andEnvIdIn(new ArrayList<>(envIdSet));
    return billingOpOnOffMapper.selectByExample(opOnOffExample);
  }
  
  @Override
  @Transactional(rollbackFor = Exception.class)
  public boolean updateAbleNext(Integer envId, Integer isAbleNext) {
    BillingOpOnOffExample onOffExample = new BillingOpOnOffExample();
    BillingOpOnOffExample.Criteria criteria = onOffExample.createCriteria();
    criteria.andEnvIdEqualTo(envId);
    List<BillingOpOnOff> opOnOffList = billingOpOnOffMapper.selectByExample(onOffExample);
    if (opOnOffList != null && opOnOffList.size() > 0) {
      BillingOpOnOff opOnOff = new BillingOpOnOff();
      opOnOff.setId(opOnOffList.get(0).getId());
      opOnOff.setIsAbleNext(isAbleNext);
      //更改完数据库后更改redis中的数据
      billingOpOnOffMapper.updateByPrimaryKeySelective(opOnOff);
      //删除缓存
      jedisManage.delete(RedisKeyPrefix.ISONOFF_PREFIX + opOnOffList.get(0).getEnvCode());
      return true;
    }
    return true;
  }
  
  /**
   * 根据当前代码所在环境的不同展示不同的环境下拉选项
   *
   * @param isProdEnv
   * @return
   */
  @Override
  public Set<Integer> selectEnvIds(boolean isProdEnv) {
    Set<Integer> envIds = new HashSet<>();
    List<BillingOpOnOff> billingOpOnOffs = billingOpOnOffMapper.selectAll();
    if (billingOpOnOffs != null && billingOpOnOffs.size() > 0) {
      for (BillingOpOnOff billingOpOnOff : billingOpOnOffs) {
        if (isProdEnv && billingOpOnOff.getIsProd() == 1) {
          envIds.add(billingOpOnOff.getEnvId());
        } else if (!isProdEnv && billingOpOnOff.getIsProd() == 0) {
          envIds.add(billingOpOnOff.getEnvId());
        }
      }
    }
    return envIds;
  }
  
  /**
   * 根据环境id获取hostnames列表,即只能访问的hostnames限制
   *
   * @param envId
   * @return
   */
  @Override
  public List<String> selectHostNamesByEnvId(Integer envId) {
    List<String> hostNameList = new ArrayList<>();
    BillingOpOnOff billingOpOnOff = billingOpOnOffMapper.selectByEnvId(envId);
    if (billingOpOnOff != null) {
      String s = jedisManage.get(ENV_HOSTNAME_PREFIX + billingOpOnOff.getEnvCode());
      log.info("redis取出的数据为: {}", s);
      String hostnames = billingOpOnOff.getHostnames();
      if (StringUtils.isNotBlank(hostnames)) {
        String[] split = hostnames.split(",");
        hostNameList.addAll(Arrays.asList(split));
      }
    }
    return hostNameList;
  }
  
  /**
   * 更新可操作的hostname列表
   *
   * @param envId
   * @param hostnames
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  public boolean updateHostNames(Integer envId, List<String> hostnames) {
    boolean success = false;
    if (envId != null && hostnames != null && hostnames.size() > 0) {
      //去重设置
      hostnames = new ArrayList<>(new HashSet<>(hostnames));
      BillingOpOnOff billingOpOnOff = billingOpOnOffMapper.selectByEnvId(envId);
      if (billingOpOnOff != null) {
        StringBuilder hostnameSb = new StringBuilder();
        for (int i = 0; i < hostnames.size(); i++) {
          if (i != hostnames.size() - 1) {
            hostnameSb.append(hostnames.get(i)).append(",");
          } else {
            hostnameSb.append(hostnames.get(i));
          }
        }
        billingOpOnOff.setHostnames(hostnameSb.toString());
        //然后先更新数据库中的信息
        if (billingOpOnOffMapper.updateHostNamesByEnvId(billingOpOnOff) > 0) {
          //更新成功后更新redis里的信息
          jedisManage.set(ENV_HOSTNAME_PREFIX + billingOpOnOff.getEnvCode(),
              JSONObject.toJSONString(hostnames));
          success = true;
        }
      }
    }
    return success;
  }
  
  @Override
  public void CronUpdateAble() {
    BillingOpOnOff opOnOff = billingOpOnOffMapper.selectByEnvId(34);
    //20点开，早8点关    1：开，0：关
    Integer isAbleNext = LocalDateTime.now().getHour() == 8 ? 0 : 1;
    BillingOpOnOff newOpOnOff = new BillingOpOnOff();
    newOpOnOff.setId(opOnOff.getId());
    newOpOnOff.setIsAbleNext(isAbleNext);
    billingOpOnOffMapper.updateByPrimaryKeySelective(newOpOnOff);
  }
}
