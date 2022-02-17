package com.donglai.web.config.aop;

import com.alibaba.fastjson.JSONObject;
import com.donglai.web.config.annotation.BackLog;
import com.donglai.web.db.backoffice.entity.BackOfficeLog;
import com.donglai.web.db.backoffice.entity.BackOfficeUser;
import com.donglai.web.db.backoffice.service.BackOfficeLogService;
import com.donglai.web.response.RestResponse;
import com.donglai.web.response.SuccessResponse;
import com.donglai.web.util.IpUtil;
import org.apache.shiro.SecurityUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

/**
 * @author Moon
 * @date 2021-12-30 11:22
 */

@Aspect
@Component
public class BackLogAspect {

    @Autowired
    private BackOfficeLogService backOfficeLogService;

    @Pointcut(value = "@annotation(com.donglai.web.config.annotation.BackLog)")
    public void validatorPointcut() {
    }

    @AfterReturning(returning = "res", pointcut = "validatorPointcut() && @annotation(backLog)")
    public void backLogAspect(JoinPoint point, BackLog backLog, Object res) {
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        String url = request.getRequestURL().toString();

        String ip = IpUtil.getIP(request);
        // 参数名
        String[] argNames = ((MethodSignature) point.getSignature()).getParameterNames();

        //获取方法参数
        Object[] args = point.getArgs();
        //操作人
        BackOfficeUser backOfficeUser = (BackOfficeUser) SecurityUtils.getSubject().getPrincipal();
        //创建日志
        BackOfficeLog backOfficeLog = new BackOfficeLog();
        backOfficeLog.setUrl(url);
        backOfficeLog.setInterfaceName(backLog.name());
        backOfficeLog.setCreatedId(backOfficeUser.getId());
        backOfficeLog.setCreatedName(backOfficeUser.getNickname() + "->" + backOfficeUser.getUsername());
        backOfficeLog.setCreatedIp(ip);
        backOfficeLog.setCreatedTime(System.currentTimeMillis());
        RestResponse genericResponse = (RestResponse) res;
        backOfficeLog.setResDesc(genericResponse.getMessage());
        JSONObject jsonObject = new JSONObject();
        for (int i = 0; i < args.length; i++) {
            String argName = argNames[i];
            Object arg = args[i];
            jsonObject.put(argName, arg);
        }
        backOfficeLog.setLogTxt(jsonObject.toJSONString());
        backOfficeLog.setRes(res instanceof SuccessResponse);
        backOfficeLogService.save(backOfficeLog);
    }
}
