package com.atguigu.cart.config;

import com.atguigu.common.vo.MemberVO;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class LoginHandlerInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession();
        MemberVO userSession = (MemberVO) session.getAttribute("userSession");
        if (userSession!=null){
            return true;
        }else {
//            request.getRequestDispatcher("http://gulimall.com/").forward(request,response);
//            request.getRequestDispatcher("http://gulimall.com/").forward();
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


