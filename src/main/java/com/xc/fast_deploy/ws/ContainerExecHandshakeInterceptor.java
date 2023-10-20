//package com.xc.fast_deploy.ws;
//
//import org.springframework.http.server.ServerHttpRequest;
//import org.springframework.http.server.ServerHttpResponse;
//import org.springframework.http.server.ServletServerHttpRequest;
//import org.springframework.web.socket.WebSocketHandler;
//import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;
//
//import java.util.Map;
//
///**
// * websocket拦截器.
// * <p>主要用于获得传参，ip,containerId,width,height</p>
// *
// * @author will
// */
//public class ContainerExecHandshakeInterceptor extends HttpSessionHandshakeInterceptor {
//    @Override
//    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
//                                   Map<String, Object> attributes) throws Exception {
//        if (request.getHeaders().containsKey("Sec-WebSocket-Extensions")) {
//            request.getHeaders().set("Sec-WebSocket-Extensions", "permessage-deflate");
//        }
//        String moduleEnvId = ((ServletServerHttpRequest) request).getServletRequest().getParameter("moduleEnvId");
//        String podName = ((ServletServerHttpRequest) request).getServletRequest().getParameter("podName");
//        String containerName = ((ServletServerHttpRequest) request).getServletRequest().getParameter("containerName");
//        String token = ((ServletServerHttpRequest) request).getServletRequest().getParameter("X-token");
//        attributes.put("moduleEnvId", moduleEnvId);
//        attributes.put("podName", podName);
//        attributes.put("containerName", containerName);
//        attributes.put("X-token", token);
//        return super.beforeHandshake(request, response, wsHandler, attributes);
//    }
//
//    @Override
//    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
//                               Exception ex) {
//        super.afterHandshake(request, response, wsHandler, ex);
//    }
//}