package com.donglai.common.dispatcher;

import com.donglai.protocol.HongXiu;
import com.donglai.protocol.message.KafkaMessage;

public interface TopicMessageServiceI<T> {
    KafkaMessage.TopicMessage Process(T t, HongXiu.HongXiuMessageRequest message, Object... param); //处理

    void Close(T t);//关闭
}
