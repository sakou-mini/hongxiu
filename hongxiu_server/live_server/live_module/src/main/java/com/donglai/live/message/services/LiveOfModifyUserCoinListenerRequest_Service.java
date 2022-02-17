package com.donglai.live.message.services;

import com.donglai.common.dispatcher.TopicMessageServiceI;
import com.donglai.model.db.entity.common.User;
import com.donglai.model.db.service.common.UserService;
import com.donglai.protocol.HongXiu;
import com.donglai.protocol.Live;
import com.donglai.protocol.message.KafkaMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service("LiveOfModifyUserCoinListenerRequest")
@Slf4j
public class LiveOfModifyUserCoinListenerRequest_Service implements TopicMessageServiceI<String> {
    @Autowired
    UserService userService;

    @Override
    public KafkaMessage.TopicMessage Process(String userId, HongXiu.HongXiuMessageRequest message, Object... param) {
        var request = message.getLiveOfModifyUserCoinListenerRequest();
        var modifyUserList = request.getModifyUserList();
        User user;
        for (Live.LiveOfModifyUserCoinListenerRequest.ModifyUser modifyUser : modifyUserList) {
            user = userService.findById(modifyUser.getUserId());
            if (Objects.nonNull(user)) {
                log.info("modify user coin  before {} ", modifyUser.getCoin());
                user.addCoin(modifyUser.getCoin());
                log.info("modify user coin  after {} ", modifyUser.getCoin());
                userService.save(user);
            }
        }
        return null;
    }

    @Override
    public void Close(String s) {

    }
}
