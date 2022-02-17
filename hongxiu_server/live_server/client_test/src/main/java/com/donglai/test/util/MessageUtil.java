package com.donglai.test.util;

import com.donglai.protocol.Account;
import com.donglai.protocol.Constant;
import com.donglai.protocol.HongXiu;
import com.donglai.protocol.Live;
import com.donglai.test.entity.UserCache;
import com.google.protobuf.Message;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;

import java.lang.reflect.InvocationTargetException;

public class MessageUtil {

    public static void sendMessage(Message message, Channel channel) {
        byte[] bytes = message.toByteArray();
        channel.writeAndFlush(new BinaryWebSocketFrame(Unpooled.copiedBuffer(bytes)));
    }

    public static HongXiu.HongXiuMessageRequest buildMessageRequest(Message message) {
        HongXiu.HongXiuMessageRequest.Builder request = HongXiu.HongXiuMessageRequest.newBuilder();
        try {
            String name = "set" + message.getDescriptorForType().getName().replaceAll("_", "");
            var f = request.getClass().getMethod(name, message.getClass());
            f.invoke(request, message);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
        request.setPlatform(Constant.PlatformType.HONG_XIU); //红秀平台
        return request.build();
    }


    public static void login(Channel channel) {
        UserCache userCache = DataManager.getUserCache(channel);
        Account.AccountOfLoginRequest.Builder loginRequest = Account.AccountOfLoginRequest.newBuilder().setAccountId(userCache.getAccount()).setPassword(userCache.getPassword());
        HongXiu.HongXiuMessageRequest message = HongXiu.HongXiuMessageRequest.newBuilder().setAccountOfLoginRequest(loginRequest).build();
        sendMessage(buildMessageRequest(loginRequest.build()), channel);
    }

    public static void login(Channel channel, String accountId, String password) {
        UserCache userCache = new UserCache(accountId, password);
        DataManager.saveUserCache(userCache, channel);
        Account.AccountOfLoginRequest.Builder loginRequest = Account.AccountOfLoginRequest.newBuilder().setAccountId(userCache.getAccount()).setPassword(userCache.getPassword());
        HongXiu.HongXiuMessageRequest message = HongXiu.HongXiuMessageRequest.newBuilder().setAccountOfLoginRequest(loginRequest).build();
        sendMessage(buildMessageRequest(loginRequest.build()), channel);
    }

    public static void regist(Channel channel) {
        Account.AccountOfRegisterRequest request = Account.AccountOfRegisterRequest.newBuilder().setMobileCode("asd51523").setPassword("123456").build();
        sendMessage(buildMessageRequest(request), channel);
    }

    public static void updatePassword(Channel channel) {
        UserCache userCache = DataManager.getUserCache(channel);
        Account.AccountOfUpdatePasswordRequest request = Account.AccountOfUpdatePasswordRequest.newBuilder().setOldPassword(userCache.getPassword()).setNewPassword("123456789").build();
        sendMessage(buildMessageRequest(request), channel);
    }

    public static void applyLiveUser(Channel channel) {
        Live.LiveOfApplyLiveUserRequest request = Live.LiveOfApplyLiveUserRequest.newBuilder().setRealName("realName")
                .setGender(Constant.GenderType.FEMALE).setCountry("country")
                .setAddress("address")
                .setEmail("email").setContactWay("contactWay").setBirthDay(System.currentTimeMillis() + "")
                .setBankName("nh").setPhoneNumber("phoneNumber")
                .setBankCard("1235353").addImages("/test").build();
        sendMessage(buildMessageRequest(request), channel);
    }

    public static void queryLiveUserInfo(Channel channel) {
        UserCache userCache = DataManager.getUserCache(channel);
        Live.LiveOfQueryLiveUserInfosRequest request = Live.LiveOfQueryLiveUserInfosRequest.newBuilder().addUserIds(userCache.getUserId()).build();
        sendMessage(buildMessageRequest(request), channel);
    }

    public static void startLiveRequest(Channel channel) {
        var request = Live.LiveOfStartLiveRequest.newBuilder()
                .setPattern(Constant.LivePattern.LIVE_AUDIO).setLiveTag(Constant.LiveTag.JIAOYOU)
                .setRoomTitle("title").setLiveLineCode(1).build();
        sendMessage(buildMessageRequest(request), channel);
    }

    public static void enterRoomLiveRequest(Channel channel, String roomId) {
        Live.LiveOfEnterLiveRoomRequest.Builder request = Live.LiveOfEnterLiveRoomRequest.newBuilder().setRoomId(roomId);
        sendMessage(buildMessageRequest(request.build()), channel);
    }

    public static void endLiveRequest(Channel channel) {
        var request = Live.LiveOfEndLiveRequest.newBuilder().setEndDelayTime(10000);
        sendMessage(buildMessageRequest(request.build()), channel);
    }

    public static void thirdPartySignUp(Channel channel) {
        var request = Account.AccountOfThirdPartySignUpRequest.newBuilder().setAvatarUrl("https://home.firefoxchina.cn/res/weather/weathericon3/0.png")
                .setNickname("第三方昵称").setSource("GOOGLE").setUuid("123456");
        sendMessage(buildMessageRequest(request.build()), channel);
    }
}
