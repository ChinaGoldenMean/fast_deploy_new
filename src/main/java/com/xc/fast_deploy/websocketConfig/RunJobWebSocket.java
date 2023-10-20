package com.xc.fast_deploy.websocketConfig;

import com.alibaba.fastjson.JSONObject;
import com.xc.fast_deploy.service.common.ModuleJobService;
import com.xc.fast_deploy.service.common.ModuleUserService;
import com.xc.fast_deploy.service.common.SyncService;
import com.xc.fast_deploy.shiro.token.JwtUtil;
import com.xc.fast_deploy.utils.PoolExcutorUtils;
import com.xc.fast_deploy.vo.JenkinsManageVO;
import com.xc.fast_deploy.vo.RunJobDataVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.yeauty.annotation.*;
import org.yeauty.pojo.ParameterMap;
import org.yeauty.pojo.Session;

import java.util.*;
import java.util.concurrent.*;

@ServerEndpoint(prefix = "netty.websocket", value = "/websocket/jenkins/runJob/")
@Component
@Slf4j
public class RunJobWebSocket {
    
    @Autowired
    private ModuleUserService userService;
    
    private Session session;
    private String token;
    private boolean isUpdateAllCode;
    private boolean isNeedUpCode;
    
    private boolean isOffline;
    
    @OnOpen
    public void onOpen(Session session, ParameterMap parameterMap) {
//        String envIdString = parameterMap.getParameter("envId");
        String token = parameterMap.getParameter("X-token");
        String isUpdate = parameterMap.getParameter("isUpdate");
        String isNeedUp = parameterMap.getParameter("isNeedUpCode");
        
        String isOfflineStr = parameterMap.getParameter("isOffline");
        log.info("打开websocket,参数token:{},isUpdate:{}", token, isUpdate);
        if (StringUtils.isNotBlank(token)
            && StringUtils.isNotBlank(isUpdate)) {
            Integer isUpdateCode;
            Integer isNeedUpInt;
            Integer offline = null;
            try {
                isUpdateCode = Integer.valueOf(isUpdate);
                isNeedUpInt = Integer.valueOf(isNeedUp);
                offline = Integer.valueOf(isOfflineStr);
            } catch (NumberFormatException e) {
                session.sendText("输入参数不符合格式");
                session.close();
                return;
            }
            this.isUpdateAllCode = (isUpdateCode == 1);
            this.isNeedUpCode = (isNeedUpInt == 1);
            boolean isOffline = (offline == 1);
            boolean flag = false;
            Map<Integer, Set<String>> envPermissionMap =
                userService.selectEnvPermissionByUserId(JwtUtil.getUserIdFromToken(token));
            //表明该用户无任何环境的权限或者无该环境的权限
            Set<Integer> keySet = envPermissionMap.keySet();
            if (keySet.size() > 0) {
                for (Integer envId : keySet) {
                    Set<String> permissionSet = envPermissionMap.get(envId);
                    if (permissionSet.contains("image_make_run_job")) {
                        flag = true;
                        break;
                    }
                }
            }
            
            if (!flag) {
                session.sendText("无权限访问!");
                session.close();
                return;
            }
            this.token = token;
            this.session = session;
        } else {
            session.sendText("输入参数不符合格式!");
            session.close();
        }
    }
    
    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose(Session session) {
        log.info("websocket 关闭: {}", session);
        if (session != null) {
            session.close();
        }
    }
    
    /**
     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的消息
     */
    @OnMessage
    public void onMessage(Session session, String message) {
        log.info("收到来自窗口{} 的信息: {}", session, message);
        if (StringUtils.isNotBlank(message) && StringUtils.isNotBlank(message.trim())) {
            List<Integer> list = new ArrayList<>();
            try {
                list = JSONObject.parseArray(message.trim(), Integer.class);
            } catch (Exception e) {
                log.error("数据接收转换为json格式错误！");
            }
            if (list.size() == 0) {
                session.sendText("数据接收格式错误");
                session.close();
                return;
            }
            HashSet<Integer> hashSet = new HashSet<>(list);
            
            for (Integer jobId : hashSet) {
                this.session.sendText("当前等待队列数: " +
                    PoolExcutorUtils.jobPoolExecutor.getQueue().size());
                //这部的功能是为了让执行的jobId不存在同一时间的同时执行
                if (PoolExcutorUtils.jobHashMap.containsKey(jobId)) {
                    session.sendText("该job正在执行中");
                    //跳出该次执行
                    continue;
                }
                RunJobDataVo runJobDataVo = new RunJobDataVo();
                runJobDataVo.setIsUpdateAllCode(isUpdateAllCode);
                runJobDataVo.setIsNeedUpCode(isNeedUpCode);
                runJobDataVo.setIsPromptly(false);
                runJobDataVo.setNeedIdStr(null);
                runJobDataVo.setJobId(jobId);
                runJobDataVo.setSession(session);
                runJobDataVo.setIsOffline(isOffline);
                runJobDataVo.setToken(token);
                try {
                    PoolExcutorUtils.linkedQueue.put(runJobDataVo);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                
            }
        }
    }
}
