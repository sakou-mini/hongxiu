package com.donglaistd.jinli.util.platform;

import com.donglaistd.jinli.Constant;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class PlatformServiceFactory implements ApplicationContextAware {
    private final static Map<Class<?>, IPlatformService> beans = new HashMap<>();

    public static IPlatformService getPlatformServiceByPlatform(Constant.PlatformType platform){
        switch (platform){
            case PLATFORM_JINLI:
                return beans.get(PlatformJinliServiceImpl.class);
            case PLATFORM_Q:
                return beans.get(PlatformQServiceImpl.class);
            case PLATFORM_T:
                return beans.get(PlatformTServiceImpl.class);
            default: return null;
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String, IPlatformService> platformServiceMap = applicationContext.getBeansOfType(IPlatformService.class);
        for (IPlatformService bean : platformServiceMap.values()) {
            beans.put(AopUtils.getTargetClass(bean), bean);
        }
    }
}
