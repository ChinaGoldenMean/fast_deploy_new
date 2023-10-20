package com.xc.fast_deploy.websocketConfig.myThread;

import com.alibaba.fastjson.JSONObject;
import com.xc.fast_deploy.dto.StatusDTO;
import com.xc.fast_deploy.myException.ServiceException;
import com.xc.fast_deploy.service.common.ModuleJobService;
import com.xc.fast_deploy.utils.PoolExcutorUtils;
import com.xc.fast_deploy.utils.SpringUtil;
import com.xc.fast_deploy.vo.JenkinsManageVO;
import com.xc.fast_deploy.vo.RunJobDataVo;
import lombok.extern.slf4j.Slf4j;
import org.yeauty.pojo.Session;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class RunJobOutputThread extends Thread {
    
    private RunJobDataVo jobDataVo;
    
    public RunJobOutputThread(RunJobDataVo jobDataVo) {
        this.jobDataVo = jobDataVo;
    }
    
    @Override
    public void run() {
        boolean b = false;
        Session session = jobDataVo.getSession();
        try {
            ModuleJobService moduleJobService = SpringUtil.getBean(ModuleJobService.class);
            b = moduleJobService.runJob(session,
                jobDataVo.getToken(),
                jobDataVo);
        } catch (Exception e) {
            session.sendText(e.getMessage());
            log.error("run job Error :{}", e.getCause().getMessage());
            e.printStackTrace();
            //抛出异常到主线程
            throw new ServiceException(e.getMessage());
        } finally {
            PoolExcutorUtils.jobHashMap.remove(jobDataVo.getJobId());
        }
        log.info("当前线程 :{} 执行状态: {}", Thread.currentThread().getName(), b);
        StatusDTO statusDTO = new StatusDTO();
        statusDTO.setResult(b);
        statusDTO.setStatus(1);
        statusDTO.setJobId(jobDataVo.getJobId());
        
        try {
            jobDataVo.getSession().sendText(JSONObject.toJSONString(statusDTO)).sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        JenkinsManageVO jenkinsManageVO = jobDataVo.getJenkinsManageVO();
        if (b && jenkinsManageVO != null && jenkinsManageVO.getJenkinsManage() != null
            && session != null && session.isActive()) {
            jenkinsManageVO.getJenkinsManage().streamConsole(jenkinsManageVO.getBuildNumber(),
                jenkinsManageVO.getJobName(), session);
        }
    }
}
