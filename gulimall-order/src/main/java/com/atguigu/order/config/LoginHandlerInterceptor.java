package com.atguigu.order.config;

import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

public class LoginHandlerInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String uri = request.getRequestURI();
        AntPathMatcher antPathMatcher = new AntPathMatcher();
        boolean match = antPathMatcher.match("/alipay/notify", uri);
        boolean match2 = antPathMatcher.match("/rabbit", uri);
        if (match||match2){
            return true;
        }
        Object userSession = request.getSession().getAttribute("userSession");
        if (userSession!=null){
            return true;
        }else {
//            request.getRequestDispatcher("http://gulimall.com/").forward(request,response);
            response.sendRedirect("http://auth.gulimall.com/login.html");
            return false;
        }

    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}


