package com.donglai.protocol.util;

import com.donglai.protocol.Constant;
import com.donglai.protocol.HongXiu;
import com.donglai.protocol.ProtoBufMapper;
import com.google.protobuf.Message;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Optional;

public class PbRefUtil {
    public static byte[] getPbBytes(Object msg) {
        Method method;
        try {
            method = msg.getClass().getSuperclass().getSuperclass().getSuperclass().getDeclaredMethod("toByteArray");
            return (byte[]) method.invoke(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new byte[0];
    }

    public static Object getPbRefObj(ProtoBufMapper.MessageType messageType, int msgId, byte[] data) {
        try {
            String name = ProtoBufMapper.getById(messageType, msgId);
            if (name == null)
                return null;
            Class<?> clazz = Class.forName(name);
            Method method = clazz.getDeclaredMethod("parseFrom", byte[].class);
            return method.invoke(null, data);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getPbRefSimpleNameByMessageId(ProtoBufMapper.MessageType type, int opCode) {
        String key = ProtoBufMapper.getById(type,opCode);
        if (key == null)
            return null;
        return key.split("\\$")[1];
    }

    public static int getPbRefMsgId(ProtoBufMapper.MessageType messageType , Object msg) {
       return Optional.of(msg.getClass().getName()).map(b -> ProtoBufMapper.getByName(messageType,b.toString())).orElse(0);
    }

    public static String getSendTopic(ProtoBufMapper.MessageType messageType,int messageId) {
        return ProtoBufMapper.getTopicByOpCode(messageType, messageId);
    }

    public static HongXiu.HongXiuMessageReply buildReply(Message message, Constant.ResultCode resultCode){
        HongXiu.HongXiuMessageReply.Builder reply = HongXiu.HongXiuMessageReply.newBuilder();
        if (resultCode != null) reply.setResultCode(resultCode);
        try {
            String name = "set" + message.getDescriptorForType().getName().replaceAll("_",  "");
            var f = reply.getClass().getMethod(name, message.getClass());
            f.invoke(reply, message);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
        return reply.build();
    }

    public static HongXiu.HongXiuMessageRequest buildRequest(Message message){
        HongXiu.HongXiuMessageRequest.Builder request = HongXiu.HongXiuMessageRequest.newBuilder();
        try {
            String name = "set" + message.getDescriptorForType().getName().replaceAll("_",  "");
            var f = request.getClass().getMethod(name, message.getClass());
            f.invoke(request, message);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
        return request.build();
    }
}
