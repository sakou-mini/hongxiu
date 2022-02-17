/*
package com.donglai.model.service.impl.lucene;

import com.donglai.model.service.ILuceneService;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class LuceneServiceFactory implements ApplicationContextAware {
    private final static Map<Class<?>, ILuceneService<?>> beans = new HashMap<>();

    public static ILuceneService<?> getILuceneService(Class<?> entityClassName) {
        return beans.get(entityClassName);
    }

    @Autowired
    UserLuceneServiceImpl userLuceneService;

    public static void initIndex() {
        beans.get(UserLuceneServiceImpl.class).deleteAll();
        beans.values().forEach(ILuceneService::synCreatIndex);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String, ILuceneService> platformServiceMap = applicationContext.getBeansOfType(ILuceneService.class);
        for (ILuceneService<?> bean : platformServiceMap.values()) {
            beans.put(AopUtils.getTargetClass(bean), bean);
        }
    }
}
*/
