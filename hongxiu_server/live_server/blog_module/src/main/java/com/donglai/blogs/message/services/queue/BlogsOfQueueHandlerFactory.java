package com.donglai.blogs.message.services.queue;

import com.donglai.blogs.message.services.queue.handler.TriggerHandler;
import com.donglai.blogs.message.services.queue.handler.impl.BlogLikeTaskHandler;
import com.donglai.blogs.message.services.queue.handler.impl.BlogsReviewHandler;
import com.donglai.common.constant.QueueType;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class BlogsOfQueueHandlerFactory implements ApplicationContextAware {

    private final static Map<Class<?>, TriggerHandler> handlerBeans = new HashMap<>();

    public static TriggerHandler getTriggerHandlerByQueueType(QueueType queueType) {
        switch (queueType) {
            case REVIEW_BOLGS:
                return handlerBeans.get(BlogsReviewHandler.class);
            case BLOGS_UPLOAD_LIKE:
                return handlerBeans.get(BlogLikeTaskHandler.class);
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
