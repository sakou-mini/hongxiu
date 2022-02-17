package com.donglai.gate.message.dispatcher;

import com.donglai.common.contxet.SpringApplicationContext;
import com.donglai.common.dispatcher.GateMessageServiceI;
import com.donglai.common.util.GlobalExceptionUtil;
import com.donglai.protocol.ProtoBufMapper;
import com.donglai.protocol.message.KafkaMessage.TopicMessage;
import com.donglai.protocol.util.PbRefUtil;
import com.google.protobuf.Message;
import io.netty.util.internal.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static com.donglai.gate.constant.GateConstant.DefaultMessageService;

@Service
@Slf4j
public class GateMessageDispatcher {

    // 分发至services
    public static void dispatcher(TopicMessage topicMessage) {
        //生成PB对象
        String messageName = "";
        try {
            messageName = PbRefUtil.getPbRefSimpleNameByMessageId(ProtoBufMapper.MessageType.REPLY_MSG, topicMessage.getMessageId());
            Message message = (Message) PbRefUtil.getPbRefObj(ProtoBufMapper.MessageType.REPLY_MSG, topicMessage.getMessageId(), topicMessage.getContent());
            log.info("收到回复 Message :{} ,状态码为 is {} 回复给 {}", messageName, topicMessage.getResultCode(), topicMessage.getUserid());
            GateMessageServiceI<String> context = getContext(messageName);
            context.Process(topicMessage.getUserid(),topicMessage,message);
        } catch (Exception e) {
            log.error("message handler" +messageName + " throws exception:" + GlobalExceptionUtil.getExceptionInfo(e));
        }
    }

    private static GateMessageServiceI<String> getContext(String messageName) {
        GateMessageServiceI<String> p = null;
        try {
            if (!StringUtil.isNullOrEmpty(messageName))
                p = (GateMessageServiceI<String>) SpringApplicationContext.getBean(messageName);
        } catch (Exception ignored) {
        }
        if (p == null)
            p = (GateMessageServiceI<String>) SpringApplicationContext.getBean(DefaultMessageService);
        return  p ;
    }

}
