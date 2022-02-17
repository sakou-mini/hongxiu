package com.donglaistd.jinli.util.live;

import com.donglaistd.jinli.Constant;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class LiveStreamFactory implements ApplicationContextAware {
    private final static Map<Class<?>, LiveStream> beans = new HashMap<>();

    public static LiveStream getLiveStreamByLiveSourceLine(Constant.LiveSourceLine line) {
        switch (line){
            case TENCENT_LINE:
                return beans.get(TencentLiveStream.class);
            case ALIYUN_LINE:
                return beans.get(AliYunLiveStream.class);
            case NGINX_LINE:
                return beans.get(NginxLiveStream.class);
            case WANGSU_LINE:
                return beans.get(WangsuLiveStream.class);
            default: return null;
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String, LiveStream> liveStreamMap = applicationContext.getBeansOfType(LiveStream.class);
        for (LiveStream bean : liveStreamMap.values()) {
            beans.put(AopUtils.getTargetClass(bean), bean);
        }
    }
}
