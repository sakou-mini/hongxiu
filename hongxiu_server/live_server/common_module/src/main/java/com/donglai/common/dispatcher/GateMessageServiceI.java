package com.donglai.common.dispatcher;

import com.donglai.protocol.message.KafkaMessage.TopicMessage;
import com.google.protobuf.Message;

public interface GateMessageServiceI<T> {
    void Process(T t, TopicMessage topicMessage, Message message); //处理

    void Close(T t);//关闭
}
