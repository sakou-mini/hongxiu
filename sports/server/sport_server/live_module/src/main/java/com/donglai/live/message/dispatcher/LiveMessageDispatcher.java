package com.donglai.live.message.dispatcher;

import com.donglai.common.contxet.SpringApplicationContext;
import com.donglai.common.dispatcher.TopicMessageServiceI;
import com.donglai.common.util.GlobalExceptionUtil;
import com.donglai.live.constant.Constant;
import com.donglai.live.message.producer.Producer;
import com.donglai.protocol.Common;
import com.donglai.protocol.HongXiu;
import com.donglai.protocol.ProtoBufMapper;
import com.donglai.protocol.message.KafkaMessage.TopicMessage;
import com.donglai.protocol.util.PbRefUtil;
import lombok.extern.slf4j.Slf4j;

import static com.donglai.protocol.Constant.ResultCode.UNKNOWN;

/**
 * @author KR
 * @deprecated 消息分发
 */
@Slf4j
public class LiveMessageDispatcher {

    public static final Producer producer = SpringApplicationContext.getBean(Producer.class);

    // 分发至services
    public static void dispatcher(TopicMessage msg) {
        String messageName = "";
        try {
            HongXiu.HongXiuMessageRequest requestMessage = HongXiu.HongXiuMessageRequest.parseFrom(msg.getContent());
            log.info("listen message :" + requestMessage.getRequestCase());
            messageName = PbRefUtil.getPbRefSimpleNameByMessageId(ProtoBufMapper.MessageType.REQUEST_MSG, msg.getMessageId());
            TopicMessageServiceI<String> context = getContext(messageName);
            TopicMessage topicMessage = context.Process(msg.getUserid(), requestMessage, msg.getExtraParams());
            if (topicMessage != null) {
                producer.send(topicMessage);
            }
        } catch (Exception e) {
            log.error("message handler" +messageName + " throws exception:" + GlobalExceptionUtil.getExceptionInfo(e));
            sendErrorMessage(msg);
        }
    }

    // 得到指定services
    public static TopicMessageServiceI<String> getContext(String messageName) {
        TopicMessageServiceI<String> p = null;
        try {
            // 分发services
            if (messageName != null)
                p = (TopicMessageServiceI<String>) SpringApplicationContext.getBean(messageName);
        } catch (Exception e) {
        }
        // 默认处理
        if (p == null)
            p = (TopicMessageServiceI<String>) SpringApplicationContext.getBean(Constant.DefaultMessageService);
        return p;
    }

    public static void sendErrorMessage(TopicMessage msg) {
        producer.sendReply(msg.getUserid(), Common.GateOfErrorReply.newBuilder().build(), UNKNOWN, msg.getExtraParams());
    }

}
