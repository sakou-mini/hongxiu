package com.donglaistd.jinli.util.platform;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.database.entity.system.GiftConfig;
import com.donglaistd.jinli.event.ModifyCoinEvent;
import com.donglaistd.jinli.event.ModifyUserResourceEvent;
import com.donglaistd.jinli.http.service.CoinFlowService;
import com.donglaistd.jinli.service.EventPublisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Objects;

import static com.donglaistd.jinli.Constant.GiftPriceType.GIFT_PRICE_GAMECOIN_VALUE;
import static com.donglaistd.jinli.Constant.GiftPriceType.GIFT_PRICE_GOLDBEAN_VALUE;

@Component
public class PlatformCommonService {
    @Value("${jinli.gift.payrate}")
    private BigDecimal giftPayRate;

    @Autowired
    private CoinFlowService coinFlowService;

    public static boolean checkUserHasEnoughCoinForSendGift(GiftConfig giftConfig, int sendAmount, User sender){
        var totalPrice = giftConfig.getPrice() * sendAmount;
        switch (giftConfig.getPriceType()){
            case GIFT_PRICE_GAMECOIN_VALUE:
                return sender.getGameCoin() >= totalPrice;
            case GIFT_PRICE_GOLDBEAN_VALUE:
                return sender.getGoldBean() >= totalPrice;
        }
        return false;
    }

    /**
     * @param localCoin  when it is true will desc jinli gameCoin ,else only record flow
     */
    public void updateUserCoinBySendGift(User sender, User receiver, int priceType , long totalPrice,boolean localCoin){
        switch (priceType){
            case GIFT_PRICE_GOLDBEAN_VALUE:
                EventPublisher.publish(ModifyUserResourceEvent.newInstance(sender.getId(),-totalPrice, ModifyUserResourceEvent.ModifyType.goldBean));
                break;
            case GIFT_PRICE_GAMECOIN_VALUE:
                var payAmount = giftPayRate.multiply(new BigDecimal(totalPrice)).longValue();
                //TODO 送礼，主播不会得到金币，仅记录流水
                if(payAmount>0) coinFlowService.addGiftCoinFlow(receiver.getId(), totalPrice,false);
                if(localCoin) EventPublisher.publish(new ModifyCoinEvent(sender.getId(), -totalPrice));
                coinFlowService.addGiftCoinFlow(sender.getId(), totalPrice,true);
                break;
        }
    }
}
