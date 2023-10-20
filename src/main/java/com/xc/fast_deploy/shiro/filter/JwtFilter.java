//package com.xc.fast_deploy.shiro.filter;
//
//import com.xc.fast_deploy.shiro.token.JwtUtil;
//import com.xc.fast_deploy.utils.SessionCookieUtils;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.lang.StringUtils;
//import org.apache.shiro.web.filter.authc.BasicHttpAuthenticationFilter;
//
//import javax.servlet.ServletRequest;
//import javax.servlet.ServletResponse;
//import javax.servlet.http.Cookie;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//
//@Slf4j
//public class JwtFilter extends BasicHttpAuthenticationFilter {
//
//    @Override
//    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
//        //此处判断是否通过验证
//        log.info("判断是否放行");
//
//        //首先验证是否已登录 //并验证是否是访问首页
//        HttpServletRequest servletRequest = (HttpServletRequest) request;
//        HttpServletResponse servletResponse = (HttpServletResponse) response;
//        String requestURI = servletRequest.getRequestURI();
//
//        if ("/".equals(requestURI) || "/index".equals(requestURI)) {
//            //只在访问首页的时候验证登录的有效性
//            // 首先是自己系统的验证 如果能通过则可以直接放行
//            String ticket = SessionCookieUtils.getCookie(servletRequest, "ticket");
//            log.info("ticket:{}", ticket);
//            if (StringUtils.isNotBlank(ticket) && ticket.equals(servletRequest.getSession().getAttribute("ticket"))) {
//                //验证通过 可以直接返回true 放行
//                log.info("验证通过直接放行");
//                return true;
//            } else {
//                //无法验证通过的情况下需要重定向到zdyw去获取token信息
//                try {
//                    servletResponse.sendRedirect("http://134.96.252.42:8000/multiauth/Session_online/sso/?loginurl=http://134.108.3.223:8093/login/");
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//        //其他情况的url接口访问需要权限验证
//        //验证是否有token信息
//        if (StringUtils.isNotBlank(servletRequest.getHeader("X-token"))
//                || StringUtils.isNotBlank(servletRequest.getHeader("access-token"))) {
//            try {
//                executeLogin(request, response);
//            } catch (Exception e) {
//                log.error("token 登录出现其他异常,尝试重新申请token");
//                //token失效后直接验证是否还在登录中,如果是重新生成token ,如果否跳转到登录页面
//                String ticket = SessionCookieUtils.getCookie(servletRequest, "ticket");
//                if (StringUtils.isNotBlank(ticket) && ticket.equals(servletRequest.getSession().getAttribute("ticket"))) {
//                    //验证通过 可以直接返回true 放行
//                    log.info("重新生成token");
//                    SessionCookieUtils.clearCookieAll(servletRequest);
//                    servletRequest.getSession().removeAttribute("ticket");
//                    String newToken = JwtUtil.sign(JwtUtil.getUserNameFromToken(ticket),
//                            JwtUtil.getUserIdFromToken(ticket), JwtUtil.SECRET);
//                    servletRequest.getSession().setAttribute("ticket", newToken);
//                    Cookie newCookie = new Cookie("ticket", newToken);
//                    servletResponse.addCookie(newCookie);
//                } else {
//                    try {
////                        servletResponse.sendRedirect("http://134.96.252.42:8000/multiauth/Session_online/sso/");
//                        servletResponse.sendRedirect("http://134.96.252.43/sfpd/login.jsp");
//                    } catch (IOException e1) {
//                        e1.printStackTrace();
//                    }
//                    return false;
//                }
//            }
//        }
//
//        return true;
//    }
//
////    @Override
////    protected boolean executeLogin(ServletRequest request, ServletResponse response) {
////        HttpServletRequest servletRequest = (HttpServletRequest) request;
////        String token1 = servletRequest.getHeader("X-token");
////        String token2 = servletRequest.getHeader("access-token");
////        if (StringUtils.isNotBlank(token1)) {
////            JwtToken jwtToken = new JwtToken(token1);
////            //提交给realm进行登录
////            getSubject(request, response).login(jwtToken);
////            //如果没有抛出异常则代表登入成功
////            return true;
////        }
////        if (StringUtils.isNotBlank(token2)) {
////            JwtToken jwtToken = new JwtToken(token2);
////            //提交给realm进行登录
////            getSubject(request, response).login(jwtToken);
////            //如果没有抛出异常则代表登入成功
////            return true;
////        }
////        return false;
////    }
//
//    /**
//     * 对跨域提供支持
//     */
////    @Override
////    protected boolean preHandle(ServletRequest request, ServletResponse response) throws Exception {
////        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
////        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
////        httpServletResponse.setHeader("Access-control-Allow-Origin", httpServletRequest.getHeader("Origin"));
////        httpServletResponse.setHeader("Access-Control-Allow-Methods", "GET,POST,OPTIONS,PUT,DELETE");
////        httpServletResponse.setHeader("Access-Control-Allow-Headers", httpServletRequest.getHeader("Access-Control-Request-Headers"));
////        // 跨域时会首先发送一个option请求，这里我们给option请求直接返回正常状态
////        if (httpServletRequest.getMethod().equals(RequestMethod.OPTIONS.name())) {
////            httpServletResponse.setStatus(HttpStatus.OK.value());
////            return false;
////        }
////        return super.preHandle(request, response);
////    }
//
//    /**
//     * 构建未授权的请求返回,filter层的异常不受exceptionAdvice控制,这里返回401,把返回的json丢到response中
//     * 将非法请求跳转到 /401
//     */
////    private void response401(ServletRequest req, ServletResponse resp, String msg) {
////        try {
////            HttpServletResponse response = WebUtils.toHttp(resp);
////            String contentType = "application/json;charset=utf-8";
////            response.setStatus(HttpStatus.UNAUTHORIZED.value());
////            response.setContentType(contentType);
////            ResponseDTO responseDTO = new ResponseDTO(401, "请重新登录获取token");
////            response.getWriter().append(JSONObject.toJSONString(responseDTO));
////        } catch (IOException e) {
////            log.error(e.getMessage());
////        }
////    }
//
//}
