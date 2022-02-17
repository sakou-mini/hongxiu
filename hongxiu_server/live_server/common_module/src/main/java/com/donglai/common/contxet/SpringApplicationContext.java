package com.donglai.common.contxet;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class SpringApplicationContext implements ApplicationContextAware {
    private static ApplicationContext applicationContext;

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        if (SpringApplicationContext.applicationContext == null)
            SpringApplicationContext.applicationContext = applicationContext;
    }

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public static Object getBean(String name) {
        return getApplicationContext().getBean(name);
    }

    public static <T> T getBean(Class<T> clazz) {
        if (getApplicationContext() == null)
            System.err.println("error , initApplicationContext plz");
        return getApplicationContext().getBean(clazz);
    }

    public static <T>Map<String, T> getBeanOfType(Class<T> clazz) {
        if (getApplicationContext() == null)
            System.err.println("error , initApplicationContext plz");
        return getApplicationContext().getBeansOfType(clazz);
    }

    public static <T> T getBean(String name, Class<T> clazz) {
        return getApplicationContext().getBean(name, clazz);
    }
}
