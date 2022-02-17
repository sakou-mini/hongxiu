package com.donglai.account.service.impl.platform;

import com.donglai.account.service.PlatformService;
import com.donglai.protocol.Constant;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class PlatformServiceFactory implements ApplicationContextAware {
    private final static Map<Class<?>, PlatformService> beans = new HashMap<>();

    public static PlatformService getPlatformService(Constant.PlatformType platform) {
        switch (platform) {
            case DUOCAI:
                return beans.get(DuoCaiPlatformServiceImpl.class);
            case HONG_XIU:
                return beans.get(HongXiuPlatformServiceImpl.class);
            default:
                return beans.get(HongXiuPlatformServiceImpl.class);
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String, PlatformService> platformServiceMap = applicationContext.getBeansOfType(PlatformService.class);
        for (PlatformService bean : platformServiceMap.values()) {
            beans.put(AopUtils.getTargetClass(bean), bean);
        }
    }
}
