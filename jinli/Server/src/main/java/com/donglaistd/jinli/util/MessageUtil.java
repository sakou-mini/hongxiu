package com.donglaistd.jinli.util;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Game;
import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.database.entity.Card;
import com.donglaistd.jinli.database.entity.LiveUser;
import com.donglaistd.jinli.database.entity.Room;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.http.message.HttpMessageReply;
import com.donglaistd.jinli.metadata.Metadata;
import com.google.gson.Gson;
import com.google.protobuf.Message;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.donglaistd.jinli.processors.handler.MessageHandler.ROOM_KEY;

public class MessageUtil {
    public static Jinli.JinliMessageReply buildReply(Message.Builder builder) {
        return buildReply(builder, null);
    }

    public static Jinli.JinliMessageReply buildReply(Message builder) {
        return buildReply(builder, null);
    }

    public static Jinli.JinliMessageReply buildReply(Message.Builder builder, Constant.ResultCode resultCode) {
        return buildReply(builder.build(), resultCode);
    }

    public static HttpMessageReply buildHttpMessageReply(int messageId, Constant.ResultCode resultCode,Object content) {
        return new HttpMessageReply(messageId,resultCode,content);
    }

    public static HttpMessageReply buildHttpMessageReply(int messageId, Constant.ResultCode resultCode) {
        return new HttpMessageReply(messageId,resultCode,null);
    }

    public static Jinli.JinliMessageReply buildReply(Message message, Constant.ResultCode resultCode) {
        var reply = Jinli.JinliMessageReply.newBuilder();
        if (resultCode != null) reply.setResultCode(resultCode);
        try {
            String name = "set" + message.getDescriptorForType().getName();
            var f = reply.getClass().getMethod(name, message.getClass());
            f.invoke(reply, message);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
        return reply.build();
    }

    static public void sendMessage(Channel channel, Message message) {
        channel.writeAndFlush(new BinaryWebSocketFrame(Unpooled.copiedBuffer(message.toByteArray())));
    }

    static public void sendMessageForTextWeb(Channel channel, HttpMessageReply message) {
        channel.writeAndFlush(new TextWebSocketFrame(new Gson().toJson(message)));
    }

    static public void sendMessage(String userId, Message message) {
        Channel channel = DataManager.getUserChannel(userId);
        if (Objects.nonNull(channel)) {
            sendMessage(channel, message);
        }
    }

    static public void sendHttpMessageTextWebByRoomId(String roomId , HttpMessageReply message) {
        if( DataManager.getHttpChannel().isEmpty()) return;
        DataManager.getHttpChannel().stream().filter(channel -> Objects.equals(roomId, channel.attr(ROOM_KEY).get()))
                .forEach(channel -> sendMessageForTextWeb(channel, message));
    }

    static public Game.Card.Builder getJinliCard(Card card) {
        var lc = Game.Card.newBuilder();
        lc.setCardType(card.getCardType());
        lc.setCardValue(card.getCardValue());
        return lc;
    }

    static public Jinli.BankerInfo buildBanker(User user, Integer coinAmount) {
        var bankInfo = Jinli.BankerInfo.newBuilder();
        bankInfo.setUserId(user.getId());
        bankInfo.setCoinAmount(coinAmount);
        return bankInfo.build();
    }

    static public List<Game.Card> getJinliCard(List<Card> cards) {
        List<Game.Card> cardList = new ArrayList<>();
        cards.forEach(card -> cardList.add(getJinliCard(card).build()));
        return cardList;
    }

    static public List<Card> getGameCards(List<Game.Card> jinliCards){
        List<Card> cards = new ArrayList<>(jinliCards.size());
        jinliCards.forEach(card -> cards.add(new Card(card.getCardValue(),card.getCardType())));
        return cards;
    }

    static public Jinli.LiveUserInfo GenerateLiveUserInfo(LiveUser liveUser, User user, int fanCount) {
        Integer nextLv = Optional.ofNullable(MetaUtil.getPlayerDefineByCurrentLevel(liveUser.getLevel())).map(Metadata.PlayerDefine::getNextLvl).orElse(0);
        Jinli.LiveUserInfo.Builder builder = Jinli.LiveUserInfo.newBuilder();
        builder.setUserId(String.valueOf(liveUser.getUserId())).setDisplayName(user.getDisplayName()).setAvatarUrl(user.getAvatarUrl())
                .setLiveUrl(liveUser.getLiveUrl()).setFanCount(fanCount).setLevel(liveUser.getLevel()).
                setCurExp(liveUser.getExp()).setNeedExp(MetaUtil.getPlayerDefineByCurrentLevel(nextLv).getAnnouncerExp() - liveUser.getExp())
                .setStatus(liveUser.getLiveStatus()).addAllDisablePermissions(liveUser.getDisablePermissions())
                .setQuickChat(liveUser.getQuickChat());
        if(liveUser.getPlatformType() != null) builder.setPlatformType(liveUser.getPlatformType());
        if(!StringUtils.isNullOrBlank(liveUser.getLiveNotice()))
            builder.setLiveNotice(liveUser.getLiveNotice());
        return builder.build();
    }

    static public Jinli.LiveUserInfo GenerateLiveUserInfo(LiveUser liveUser,User user,Room room){
        var liveUserInfoBuilder = Jinli.LiveUserInfo.newBuilder();
        liveUserInfoBuilder.setUserId(user.getId()).setAvatarUrl(user.getAvatarUrl())
                .setDisplayName(user.getDisplayName()).setRoomId(room.getId()).setStatus(liveUser.getLiveStatus())
                .setQuickChat(liveUser.getQuickChat());
        if (!StringUtils.isNullOrBlank(room.getDescription())) liveUserInfoBuilder.setDescription(room.getDescription());
        return liveUserInfoBuilder.build();
    }

    static public Jinli.RoomInfo GenerateRoomInfo(Room room, int hotValue, long totalAmount, Constant.GameType type, LiveUser liveUser, User user) {
        var builder = Jinli.RoomInfo.newBuilder();
        builder.setId(room.getId()).setRoomTitle(room.getRoomTitle()).setDescription(room.getDescription()).setRoomImage(room.getRoomImage())
                .setHotValue(hotValue).setGameType(type).setPattern(room.getPattern()).setTotalAmount(totalAmount).setUserId(liveUser.getUserId())
                .setLiveUserInfo(GenerateLiveUserInfo(liveUser, user, 0));
        return builder.build();
    }
}
