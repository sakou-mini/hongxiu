package com.donglai.protocol.message;

import com.donglai.protocol.Constant;
import com.google.protobuf.Message;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public interface KafkaMessage {
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    class TopicMessage implements Serializable {
        private String topic; // 消息主题
        private String userid;// 消息发送者~消息接收者
        private int messageId;
        private Constant.ResultCode resultCode;
        private byte[] content;
        private Map<ExtraParam, String> extraParams;//额外参数
        Set<String> broadCastIds = new HashSet<>(1);
        private Constant.PlatformType platform = Constant.PlatformType.HONG_XIU; //default platform
        private static final String SEPARATOR = "_";


        public TopicMessage(Constant.PlatformType platform, String topic, String userid, int messageId, Message content) {
            this.topic = topic;
            this.userid = userid;
            this.messageId = messageId;
            this.content = content.toByteArray();
            this.platform = platform;
        }

        public TopicMessage(Constant.PlatformType platform, String topic, String userid, int messageId, Message content, Constant.ResultCode resultCode) {
            this.topic = topic;
            this.userid = userid;
            this.messageId = messageId;
            this.content = content.toByteArray();
            this.resultCode = resultCode;
            this.platform = platform;
        }

        public TopicMessage(Constant.PlatformType platform, String topic, int messageId, Message content, Constant.ResultCode resultCode, Set<String> userIds) {
            this.topic = topic;
            this.messageId = messageId;
            this.content = content.toByteArray();
            this.resultCode = resultCode;
            this.broadCastIds = userIds;
            this.platform = platform;
        }

        /*public String getPlantFormTopic(){
            return platform.name() + SEPARATOR + topic;
        }*/
    }

    enum ExtraParam {
        CHANNEL_ID,
        USER_ID,
        IP
    }
}
