package com.donglai.protocol;

import com.google.protobuf.Descriptors;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.donglai.protocol.ProtoBufMapper.MessageType.REPLY_MSG;
import static com.donglai.protocol.ProtoBufMapper.MessageType.REQUEST_MSG;

public class ProtoBufMapper {
	static {
		nameReflectMapper = new HashMap<>();
		idReflectMapper = new HashMap<>();
		opCodeTopic = new HashMap<>();
		initReflectMapper();
	}
	public enum MessageType{
		REQUEST_MSG, REPLY_MSG,
	}

	private static final Map<MessageType, Map<String, Integer>> nameReflectMapper;
	private static final Map<MessageType, Map<Integer, String>> idReflectMapper;
	private static final Map<MessageType, Map<Integer, String>> opCodeTopic;

	//TODO  if has other proto，must define in here
	private static String getTopic(String messageName){
		messageName = messageName.toUpperCase();
		if(messageName.startsWith(HongXiu.TopicEnum.ACCOUNT.name())){
			return HongXiu.TopicEnum.ACCOUNT.name();
		}else if(messageName.startsWith(HongXiu.TopicEnum.LIVE.name())){
			return HongXiu.TopicEnum.LIVE.name();
		}else if(messageName.startsWith(HongXiu.TopicEnum.GATE.name())){
			return HongXiu.TopicEnum.GATE.name();
		}else if(messageName.startsWith(HongXiu.TopicEnum.QUEUE.name())){
			return HongXiu.TopicEnum.QUEUE.name();
		}
		return "";
	}

	//TODO  if has other proto，must define in here
	private static String getMessagePackageName(String messageName){
		messageName = messageName.toUpperCase();
		List<String> accountMessages = Account.getDescriptor().getMessageTypes().stream().map(type -> type.getFullName().toUpperCase()).collect(Collectors.toList());
		List<String> commonMessages = Common.getDescriptor().getMessageTypes().stream().map(type -> type.getFullName().toUpperCase()).collect(Collectors.toList());
		List<String> liveMessages = Live.getDescriptor().getMessageTypes().stream().map(type -> type.getFullName().toUpperCase()).collect(Collectors.toList());
		if(commonMessages.contains(messageName)){
			return Common.class.getName().split("\\$")[0];
		}else if(accountMessages.contains(messageName)){
			return Account.class.getName().split("\\$")[0];
		}else if(liveMessages.contains(messageName)) {
			return Live.class.getName().split("\\$")[0];
		}
		return "";
	}


	//==================================================================
	public static void initReflectMapper() {
		if(!nameReflectMapper.isEmpty() || !idReflectMapper.isEmpty() || !opCodeTopic.isEmpty())
			return;
		//REQUEST_MSG   REPLY_MSG   init nameReflectMapper
		//Request
		//String basePackage = Live.class.getPackageName();
		//String liveBasePackage = Live.class.getName().split("\\$")[0];
		String classPath;
		List<Descriptors.FieldDescriptor> requestMessages = HongXiu.HongXiuMessageRequest.getDescriptor().getFields();
		for (Descriptors.FieldDescriptor requestMessage : requestMessages) {
			if(requestMessage.getJsonName().equals("platform")) continue;
			String messageName = requestMessage.getMessageType().getFullName();
			classPath = getMessagePackageName(messageName) + "$" + messageName;
			nameReflectMapper.computeIfAbsent(REQUEST_MSG, k -> new HashMap<>()).put(classPath, requestMessage.getNumber());
			idReflectMapper.computeIfAbsent(REQUEST_MSG, k -> new HashMap<>()).put(requestMessage.getNumber(), classPath);
			opCodeTopic.computeIfAbsent(REQUEST_MSG, k -> new HashMap<>()).put(requestMessage.getNumber(), getTopic(requestMessage.getMessageType().getFullName()));
		}

		//Reply
		List<Descriptors.FieldDescriptor> replyMessages = HongXiu.HongXiuMessageReply.getDescriptor().getFields();
		String replyMessageName;
		for (Descriptors.FieldDescriptor replyMessage : replyMessages) {
			if(replyMessage.getJsonName().equals("resultCode")) continue;
			replyMessageName = replyMessage.getMessageType().getFullName();
			classPath = getMessagePackageName(replyMessageName) + "$" + replyMessageName;
			nameReflectMapper.computeIfAbsent(REPLY_MSG, k -> new HashMap<>()).put(classPath, replyMessage.getNumber());
			idReflectMapper.computeIfAbsent(REPLY_MSG, k -> new HashMap<>()).put(replyMessage.getNumber(), classPath);
			opCodeTopic.computeIfAbsent(REPLY_MSG, k -> new HashMap<>()).put(replyMessage.getNumber(), HongXiu.TopicEnum.GATE.name());
		}
	}

	public static String getById(MessageType type,int id) {
		return idReflectMapper.get(type).get(id);
	}

	public static int getByName(MessageType type,String name) {
		return nameReflectMapper.get(type).get(name);
	}

	public static String getTopicByOpCode(MessageType type,int opCode) {
		return opCodeTopic.get(type).get(opCode);
	}

	public static Map<MessageType, Map<String, Integer>> getNameReflectMapper() {
		return nameReflectMapper;
	}

	public static Map<MessageType, Map<Integer, String>> getIdReflectMapper() {
		return idReflectMapper;
	}

	public static Map<MessageType, Map<Integer, String>> getOpCodeTopic() {
		return opCodeTopic;
	}
}