package com.donglaistd.jinli.listener;

import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.database.dao.RoomDaoService;
import com.donglaistd.jinli.database.dao.UserDaoService;
import com.donglaistd.jinli.event.BaseEvent;
import com.donglaistd.jinli.event.ModifyCoinEvent;
import com.donglaistd.jinli.service.EventPublisher;
import com.donglaistd.jinli.util.DataManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.logging.Logger;

import static com.donglaistd.jinli.util.MessageUtil.buildReply;
import static com.donglaistd.jinli.util.MessageUtil.sendMessage;
/**
 *
 * @deprecated
 */
@Component
public class RedPacketEndListener implements EventListener {
    private static final java.util.logging.Logger logger = Logger.getLogger(RedPacketEndListener.class.getName());
    @Autowired
    UserDaoService userDaoService;
    @Autowired
    DataManager dataManager;
    @Autowired
    RoomDaoService roomDaoService;

    @Override
    public boolean handle(BaseEvent event) {
      /*  var e = (RedPacketEndEvent) event;
        List<RedPacket> redPacketList = e.getRedPacketList();
        Map<String, Long> backCoin = new HashMap<>(redPacketList.size());
        redPacketList.forEach(redPacket -> backCoin.compute(redPacket.getUserId(), (k, v) -> {
            logger.info("退还红包" + redPacket.getId());
            if (v == null) v = 0L;
            return v + redPacket.getLeftAmountCoin();
        }));
        backCoinToUser(backCoin);
        Room room = e.getRoom();
        if (e.isCleanAll()) room.cleanRedPacketQueue();
        if (DataManager.findOnlineRoom(room.getId()) != null) {
            dataManager.saveRoom(room);
        } else roomDaoService.save(room);*/
        return true;
    }

    public void backCoinToUser(Map<String, Long> backCoinMap) {
        backCoinMap.forEach((userId, backCoin) -> {
            logger.info("red packet back coin :" + backCoin + " to:" + userId);
            EventPublisher.publish(new ModifyCoinEvent(userId, backCoin));
            var user = userDaoService.findById(userId);
            sendMessage(userId, buildReply(Jinli.GiveBackBroadcastMessage.newBuilder().setTotalCoin(user.getGameCoin()).build()));
            logger.info("user coin :" + user.getGameCoin());
        });
    }

    @Override
    public boolean isDisposable() {
        return false;
    }
}
