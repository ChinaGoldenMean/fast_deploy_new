//package com.xc.fast_deploy.ws;
//
//import com.xc.fast_deploy.dao.master_dao.ModuleEnvMapper;
//import com.xc.fast_deploy.model.master_model.ModuleEnv;
//import com.xc.fast_deploy.utils.constant.K8sNameSpace;
//import com.xc.fast_deploy.utils.k8s.K8sManagement;
//import com.xc.fast_deploy.websocketConfig.myThread.ExecOutputThread;
//import io.kubernetes.client.Exec;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.socket.CloseStatus;
//import org.springframework.web.socket.TextMessage;
//import org.springframework.web.socket.WebSocketSession;
//import org.springframework.web.socket.handler.TextWebSocketHandler;
//
//import java.io.OutputStream;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.concurrent.ArrayBlockingQueue;
//import java.util.concurrent.ThreadPoolExecutor;
//import java.util.concurrent.TimeUnit;
//
///**
// * 连接容器执行命令.
// *
// * @author will
// */
//@Slf4j
//public class ContainerExecWSHandler extends TextWebSocketHandler {
//
//    private Map<String, ExecSession> execSessionMap = new HashMap<>();
//
//    @Autowired
//    private ModuleEnvMapper envMapper;
//
//    @Override
//    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
//        //获得传参
//        String envId = session.getAttributes().get("moduleEnvId").toString();
//        String podName = session.getAttributes().get("podName").toString();
//        String containerName = session.getAttributes().get("containerName").toString();
//        String token = session.getAttributes().get("X-token").toString();
//        //创建bash
//        Process process = createExec(session, envId, podName, containerName);
//        //获取输出
//        getExecMessage(session, process);
//    }
//
//    private void getExecMessage(WebSocketSession session, Process process) {
//        ThreadPoolExecutor poolExecutor = new ThreadPoolExecutor(1, 1, 0L,
//                TimeUnit.SECONDS, new ArrayBlockingQueue<>(20));
//        poolExecutor.submit(new ExecOutputThread(process.getInputStream(), session));
//        execSessionMap.put(session.getId(), new ExecSession(session.getId(), session, process, poolExecutor));
//    }
//
//    /**
//     * 创建bash.
//     *
//     * @return 命令id
//     * @throws Exception
//     */
//    private Process createExec(WebSocketSession session, String envId, String podName, String containerName) throws Exception {
//        ModuleEnv env = envMapper.selectOne(Integer.valueOf(envId));
//        K8sManagement.getCoreV1Api(env.getK8sConfig());
//        Exec exec = new Exec();
//        final Process process = exec.exec(
//                K8sNameSpace.DEFAULT,
//                podName,
//                new String[]{"/bin/bash"},
//                containerName,
//                true,
//                true
//        );
//        return process;
//    }
//
//    /**
//     * 获得先输入.
//     *
//     * @param session
//     * @param message 输入信息
//     * @throws Exception
//     */
//    @Override
//    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
//        Process process = execSessionMap.get(session.getId()).getProcess();
//        OutputStream out = process.getOutputStream();
//        out.write(message.asBytes());
//        out.flush();
//    }
//
//    /**
//     * websocket关闭后关闭线程.
//     *
//     * @param session
//     * @param closeStatus
//     * @throws Exception
//     */
//    @Override
//    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
//        Process process = execSessionMap.get(session.getId()).getProcess();
//        ThreadPoolExecutor poolExecutor = execSessionMap.get(session.getId()).getPoolExecutor();
//        if (process != null) {
//            process.destroy();
//        }
//        if (poolExecutor != null) {
//            poolExecutor.shutdown();
//            if (!poolExecutor.isTerminated()) {
//                poolExecutor.awaitTermination(5, TimeUnit.SECONDS);
//                poolExecutor.shutdownNow();
//            }
//        }
//    }
//
//
//}
