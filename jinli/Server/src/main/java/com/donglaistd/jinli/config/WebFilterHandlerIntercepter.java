package com.donglaistd.jinli.config;

import com.donglaistd.jinli.service.ServerAvailabilityCheckService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.logging.Logger;

@Component
public class WebFilterHandlerIntercepter implements HandlerInterceptor {
    Logger logger = Logger.getLogger(WebFilterHandlerIntercepter.class.getName());
    @Autowired
    ServerAvailabilityCheckService serverAvailabilityCheckService;
    public static final List<String> whiteList = Lists.newArrayList("/share/statusCheck");

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!serverAvailabilityCheckService.isActive() && !whiteList.contains(request.getRequestURI())) {
            response.setStatus(HttpStatus.SERVICE_UNAVAILABLE.value());
            response.setContentType("application/json;charset=utf-8");
            response.getWriter().write(new ObjectMapper().writeValueAsString("server is close access forbidden"));
            logger.info("server has closed, access forbidden request");
            return false;
        }
        return true;
    }
}
