package com.donglai.web.config.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class MyHandlerInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Integer statusCode = (Integer) request
                .getAttribute("javax.servlet.error.status_code");
        if (statusCode != null) {
            switch (statusCode) {
                case 404:
                    throw new NoHandlerFoundException(request.getMethod(),request.getRequestURI(), HttpHeaders.EMPTY);
                case 500:
                    throw new Exception("");
            }
        }
        return true;
    }
}
