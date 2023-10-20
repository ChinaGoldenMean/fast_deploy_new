package com.xc.fast_deploy.quartz_job.job;

import com.xc.fast_deploy.dao.master_dao.ModuleDeployNeedMapper;
import com.xc.fast_deploy.model.master_model.ModuleDeployNeed;
import com.xc.fast_deploy.model.master_model.example.ModuleDeployNeedExample;
import com.xc.fast_deploy.myenum.ApproveStatusEnum;
import com.xc.fast_deploy.service.common.ModuleDeployListService;
import com.xc.fast_deploy.service.common.ModuleNeedLogService;
import com.xc.fast_deploy.service.common.SyncService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;

import static com.xc.fast_deploy.utils.constant.NeedOPActive.OP_COMMIT_LIST;

@Slf4j
public class JoinDeployList extends QuartzJobBean {
  
  @Autowired
  private ModuleDeployNeedMapper needMapper;
  @Autowired
  private ModuleDeployListService deployListService;
  @Autowired
  private ModuleNeedLogService needLogService;
  @Autowired
  private SyncService syncService;
  
  @Value("${myself.pspass.prod}")
  private boolean isProdEnv;
  @Value("${myself.pspass.prodId}")
  private Integer[] prodId;
  
  @Override
  protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
    if (isProdEnv) {
      log.info("生产环境加入发布清单任务开始");
      ModuleDeployNeedExample needExample = new ModuleDeployNeedExample();
      needExample.createCriteria()
          .andApproveStatusEqualTo(ApproveStatusEnum.APPROVE_PASS.getCode())
          .andEnvIdIn(Arrays.asList(prodId));
      List<ModuleDeployNeed> deployNeeds = needMapper.selectByExample(needExample);
      SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
      Set<Integer> moduleSet;
      for (ModuleDeployNeed deployNeed : deployNeeds) {
        if (ApproveStatusEnum.APPROVE_PASS.getCode().equals(deployNeed.getApproveStatus())
            && sdf.format(new Date()).equals(sdf.format(deployNeed.getDeployTime()))) {
          moduleSet = needMapper.selectAllModuleByneedId(deployNeed.getId(), deployNeed.getEnvId());
          Integer[] moduleIds = moduleSet.toArray(new Integer[moduleSet.size()]);
          boolean success = deployListService.insert2RedisModuleDeployList(deployNeed.getEnvId(), "ZJXCA0029", moduleIds);
          syncService.saveModuleNeedLog(needLogService, deployNeed.getId(), OP_COMMIT_LIST,
              moduleSet.toString(), success, "QuartzJob");
        }
      }
    }
  }
}
