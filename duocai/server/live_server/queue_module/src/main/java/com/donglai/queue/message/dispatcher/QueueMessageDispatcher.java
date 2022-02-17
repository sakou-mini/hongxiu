package com.donglai.queue.message.dispatcher;

import com.donglai.common.contxet.SpringApplicationContext;
import com.donglai.common.dispatcher.TopicMessageServiceI;
import com.donglai.common.util.GlobalExceptionUtil;
import com.donglai.protocol.HongXiu;
import com.donglai.protocol.message.KafkaMessage.TopicMessage;
import com.donglai.protocol.util.PbRefUtil;
import com.donglai.queue.constant.QueueConstant;
import com.google.protobuf.InvalidProtocolBufferException;
import io.netty.util.internal.StringUtil;
import lombok.extern.slf4j.Slf4j;

import static com.donglai.protocol.ProtoBufMapper.MessageType.REQUEST_MSG;

/**
 * 消息分发
 * @author KR
 *
 */
@Slf4j
public class QueueMessageDispatcher {

	// 分发至services
	public static void dispatcher(TopicMessage message) {
		String messageName = null;
		try {
			messageName = PbRefUtil.getPbRefSimpleNameByMessageId(REQUEST_MSG, message.getMessageId());
			TopicMessageServiceI<String> context = getContext(messageName);
			HongXiu.HongXiuMessageRequest request = HongXiu.HongXiuMessageRequest.parseFrom(message.getContent());
			context.Process(message.getUserid(),request,message.getExtraParams());
		} catch (InvalidProtocolBufferException e) {
			log.error("message handler" +messageName + " throws exception:" + GlobalExceptionUtil.getExceptionInfo(e));
			e.printStackTrace();
		}
	}

	// 得到指定services
	public static TopicMessageServiceI<String> getContext(String messageName) {
		TopicMessageServiceI<String> p = null;
		try {
			// 分发services
			if (!StringUtil.isNullOrEmpty(messageName))
				p = (TopicMessageServiceI<String>) SpringApplicationContext.getBean(messageName);
		} catch (Exception e) {
		}
		// 默认处理
		if (p == null)
			p = (TopicMessageServiceI<String>) SpringApplicationContext.getBean(QueueConstant.getDefaultMessageService());
		return p;
	}

}
