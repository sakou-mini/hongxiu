package com.donglai.account.service.impl.login;


import com.donglai.account.service.LoginService;
import com.donglai.protocol.Constant;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Moon
 * @date 2021-11-18 14:38
 */
@Component
public class LoginServiceFactory implements ApplicationContextAware {

    private final static Map<Class<?>, LoginService> beans = new HashMap<>();

    public LoginService getLoginService(Constant.LoginType type) {
        if (Constant.LoginType.THIRD_PARTY_LOGIN.equals(type)) {
            return beans.get(LoginThirdPartyServiceImpl.class);
        } else if (Constant.LoginType.PHONE_LOGIN.equals(type)) {
            return beans.get(LoginPhoneServiceImpl.class);
        } else if (Constant.LoginType.ACCOUNT_LOGIN.equals(type)) {
            return beans.get(LoginAccountServiceImpl.class);
        }
        return null;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String, LoginService> platformServiceMap = applicationContext.getBeansOfType(LoginService.class);
        for (LoginService bean : platformServiceMap.values()) {
            beans.put(AopUtils.getTargetClass(bean), bean);
        }
    }
}
