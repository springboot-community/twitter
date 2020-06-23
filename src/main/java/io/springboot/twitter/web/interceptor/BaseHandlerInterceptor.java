package io.springboot.twitter.web.interceptor;

import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract class BaseHandlerInterceptor extends HandlerInterceptorAdapter {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod)){
            return Boolean.TRUE;
        }
        return this.preHandle(request, response, (HandlerMethod)handler);
    }

    public abstract boolean preHandle(HttpServletRequest request, HttpServletResponse response,HandlerMethod handlerMethod)throws Exception;
}
