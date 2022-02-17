package com.donglaistd.jinli.processors.handler.gift;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.config.ConditionType;
import com.donglaistd.jinli.database.dao.UserDaoService;
import com.donglaistd.jinli.database.dao.system.GiftConfigDaoService;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.database.entity.system.GiftConfig;
import com.donglaistd.jinli.event.TaskEvent;
import com.donglaistd.jinli.processors.handler.MessageHandler;
import com.donglaistd.jinli.service.EventPublisher;
import com.donglaistd.jinli.util.platform.IPlatformService;
import com.donglaistd.jinli.util.platform.PlatformServiceFactory;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import static com.donglaistd.jinli.Constant.ResultCode.*;
import static com.donglaistd.jinli.util.MessageUtil.buildReply;

@Component
public class SendGiftRequestHandler extends MessageHandler {
    private static final Logger LOGGER = Logger.getLogger(SendGiftRequestHandler.class.getName());
    @Autowired
    private UserDaoService userDaoService;
    @Autowired
    GiftConfigDaoService giftConfigDaoService;

    //todo use better sync method
    private static final Map<Constant.PlatformType,Object> lockMap = new ConcurrentHashMap<>();

    @Override
    @Transactional
    public Jinli.JinliMessageReply doHandle(ChannelHandlerContext ctx, Jinli.JinliMessageRequest messageRequest, User user) {
        synchronized (lockMap.computeIfAbsent(user.getPlatformType(), k -> new Object())) {
            var message = messageRequest.getSendGiftRequest();
            var sendGiftReply = Jinli.SendGiftReply.newBuilder();
            String receiveUserId = message.getReceiveUserId();
            User receiveUser = userDaoService.findById(receiveUserId);
            LOGGER.info("send gift " + receiveUserId);
            if(receiveUser == null)  return buildReply(sendGiftReply, USER_NOT_FOUND);
            GiftConfig giftConfig = giftConfigDaoService.findByGiftIdAndPlatform(message.getGiftId(),user.getPlatformType());
            resultCode = verifySendGift(receiveUser, message, giftConfig);
            if(!Objects.equals(SUCCESS,resultCode)) {
                return buildReply(sendGiftReply, resultCode);
            } else {
                IPlatformService platformServiceByPlatform = PlatformServiceFactory.getPlatformServiceByPlatform(user.getPlatformType());
                assert platformServiceByPlatform != null;
                resultCode = platformServiceByPlatform.sendGift(user.getId(), receiveUser.getId(), giftConfig, message.getSendAmount());
                if (resultCode.equals(Constant.ResultCode.SUCCESS)) {
                    var totalPrice = giftConfig.getPrice() * message.getSendAmount();
                    EventPublisher.publish(TaskEvent.newInstance(user.getId(), ConditionType.tip,1));
                    if(giftConfig.isCoinGift()) EventPublisher.publish(TaskEvent.newInstance(user.getId(), ConditionType.sendGift,totalPrice));
                }
                return buildReply(sendGiftReply, resultCode);
            }
        }
    }

    public Constant.ResultCode verifySendGift(User receiver, Jinli.SendGiftRequest request, GiftConfig giftConfig ){
        if(Objects.isNull(receiver)) return USER_NOT_FOUND;
        else if(giftConfig == null) return GIFT_NOT_EXIST;
        else if(!giftConfig.isEnable()) return GIFT_IS_DISABLED;
        else if(request.getSendAmount() <= 0) return PARAM_ERROR;
        else if(Objects.equals(giftConfig.getGiftPriceType(), Constant.GiftPriceType.GIFT_PRICE_GAMECOIN) && !Objects.equals(request.getPrice(),giftConfig.getPrice())){
            return GIFT_CONFIG_NOT_RIGHT;
        }
        return SUCCESS;
    }
}
       