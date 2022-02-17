package com.donglai.common.util.live;

import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

import static com.donglai.common.constant.LineSourceConstant.*;

@Service
public class LiveStreamFactory implements ApplicationContextAware {
    private final static Map<Class<?>, LiveStream> beans = new HashMap<>();

    public static LiveStream getLiveStreamByLiveSourceLine(int lineCode) {
        switch (lineCode) {
            case WANGSU_LINE:
                return beans.get(WangsuLiveStream.class);
            case TENCENT_LINE:
                return beans.get(TencentLiveStream.class);
            case ALI_LINE:
                return beans.get(AliYunLiveStream.class);
            default:
                return null;
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String, LiveStream> liveStreamMap = applicationContext.getBeansOfType(LiveStream.class);
        for (LiveStream bean : liveStreamMap.values()) {
            beans.put(AopUtils.getTargetClass(bean), bean);
        }
    }

    public enum LiveStreamUrlType {
        flv(0),
        m3u8(1);
        int value;

        LiveStreamUrlType(int value) {
            this.value = value;
        }

        public LiveStreamUrlType valueOf(int typeValue) {
            switch (typeValue) {
                case 0:
                    return flv;
                case 1:
                    return m3u8;
                default:
                    return null;
            }
        }
    }
}
