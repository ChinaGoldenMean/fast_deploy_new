package com.xc.fast_deploy.quartz_job.job;

import com.ctg.itrdc.cache.pool.CtgJedisPoolException;
import com.ctg.itrdc.cache.pool.ProxyJedis;
import com.xc.fast_deploy.dao.master_dao.ModuleDeployNeedMapper;
import com.xc.fast_deploy.model.master_model.ModuleDeployNeed;
import com.xc.fast_deploy.model.master_model.example.ModuleDeployNeedExample;
import com.xc.fast_deploy.myenum.ApproveStatusEnum;
import com.xc.fast_deploy.rediscache.JedisManage;
import com.xc.fast_deploy.service.common.impl.ModuleNeedServiceImpl;
import com.xc.fast_deploy.utils.constant.RedisKeyPrefix;
import com.xc.fast_deploy.vo.module_vo.param.ModuleNeedSelectParamVo;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.quartz.QuartzJobBean;

import javax.annotation.Resource;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Slf4j
public class ClearDeployList extends QuartzJobBean {
  
  @Autowired
  private ModuleDeployNeedMapper needMapper;
  @Resource
  private JedisManage jedisManage;
  @Value("${myself.pspass.prod}")
  private boolean isProdEnv;
  @Value("${myself.pspass.realprodid}")
  private Integer[] realprodid;
  @Value("${myself.pspass.prodId}")
  private String[] prodId;
  
  @Override
  protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
    ProxyJedis jedis = null;
    try {
      if (isProdEnv) {
        log.info("清理生产发布清单任务开始");
        //清空生产发布清单中的模块
        jedis = jedisManage.getJedis();
        jedis.hdel(RedisKeyPrefix.PROD_REDIS_PREFIX, prodId);
        //将过了发布时间且审核通过的生产需求设置为已完成状态
        ModuleDeployNeedExample needExample = new ModuleDeployNeedExample();
        needExample.createCriteria().andDeployTimeLessThanOrEqualTo(new Date())
            .andApproveStatusEqualTo(ApproveStatusEnum.APPROVE_PASS.getCode())
            .andEnvIdIn(Arrays.asList(realprodid));
        List<ModuleDeployNeed> deployNeeds = needMapper.selectByExample(needExample);
        if (!deployNeeds.isEmpty()) {
          ModuleDeployNeed deployNeed = new ModuleDeployNeed();
          deployNeed.setApproveStatus(ApproveStatusEnum.APPROVE_COMPLETE.getCode());
          for (ModuleDeployNeed need : deployNeeds) {
            deployNeed.setId(need.getId());
            deployNeed.setUpdateTime(new Date());
            needMapper.updateByPrimaryKeySelective(deployNeed);
          }
        }
      }
    } catch (CtgJedisPoolException e) {
      e.printStackTrace();
    } finally {
      if (jedis != null) {
        jedis.close();
      }
    }
  }
}
