package com.donglai.statistics.message.services.queue.handler;

import com.donglai.common.constant.QueueType;
import com.donglai.statistics.message.services.queue.handler.impl.DailyTaskHandler;
import com.donglai.statistics.message.services.queue.handler.impl.MinuteTaskHandler;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class StatisticsOfQueueHandlerFactory implements ApplicationContextAware {

    private final static Map<Class<?>, TriggerHandler> handlerBeans = new HashMap<>();

    public static TriggerHandler getTriggerHandlerByQueueType(QueueType queueType) {
        switch (queueType) {
            case DAILY_STATISTIC:
                return handlerBeans.get(DailyTaskHandler.class);
            case MINUTE_STATISTIC:
                return handlerBeans.get(MinuteTaskHandler.class);
            default:
                return null;
        }
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String, TriggerHandler> triggerHandlerMap = applicationContext.getBeansOfType(TriggerHandler.class);
        for (TriggerHandler bean : triggerHandlerMap.values()) {
            handlerBeans.put(AopUtils.getTargetClass(bean), bean);
        }
    }
}
