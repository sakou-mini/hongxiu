package com.donglai.blogs.message.services;

import com.donglai.common.dispatcher.TopicMessageServiceI;
import com.donglai.protocol.HongXiu;
import com.donglai.protocol.message.KafkaMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service("BlogsOfPlayBlogsRequest")
@Slf4j
public class BlogsOfPlayBlogsRequest_Service implements TopicMessageServiceI<String> {
    @Override
    public KafkaMessage.TopicMessage Process(String s, HongXiu.HongXiuMessageRequest message, Object... param) {
        //TODO ,update blogs playNum
        return null;
    }

    @Override
    public void Close(String s) {

    }
}
