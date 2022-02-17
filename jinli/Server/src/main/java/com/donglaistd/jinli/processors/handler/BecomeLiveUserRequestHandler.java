package com.donglaistd.jinli.processors.handler;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.builder.LiveUserBuilder;
import com.donglaistd.jinli.config.ConditionType;
import com.donglaistd.jinli.database.dao.LiveUserDaoService;
import com.donglaistd.jinli.database.entity.LiveUser;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.event.TaskEvent;
import com.donglaistd.jinli.service.EventPublisher;
import com.donglaistd.jinli.util.DataManager;
import com.donglaistd.jinli.util.PlatformUtil;
import com.donglaistd.jinli.util.StringUtils;
import com.donglaistd.jinli.util.VerifyUtil;
import com.google.common.collect.Sets;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static com.donglaistd.jinli.Constant.GenderType.Gender_NULL;
import static com.donglaistd.jinli.Constant.LiveStatus.UNAPPROVED;
import static com.donglaistd.jinli.Constant.LiveStatus.UNUPLOAD_IMAGE;
import static com.donglaistd.jinli.Constant.PlatformType.PLATFORM_DEFAULT;
import static com.donglaistd.jinli.Constant.PlatformType.PLATFORM_JINLI;
import static com.donglaistd.jinli.Constant.ResultCode.*;
import static com.donglaistd.jinli.util.MessageUtil.buildReply;


@Component
public class BecomeLiveUserRequestHandler extends MessageHandler {
    @Autowired
    private LiveUserDaoService liveUserDaoService;
    @Autowired
    LiveUserBuilder liveUserBuilder;
    @Autowired
    private VerifyUtil verifyUtil;

    @Override
    @Transactional
    public synchronized Jinli.JinliMessageReply doHandle(ChannelHandlerContext ctx, Jinli.JinliMessageRequest messageRequest, User user) {
        var request = messageRequest.getBecomeLiveUserRequest();
        var reply = Jinli.BecomeLiveUserReply.newBuilder();
        Constant.ResultCode checkResult = verifyUtil.checkUserHasBindPhoneAndModifyName(user);
        if(!checkResult.equals(SUCCESS))
            return buildReply(reply, checkResult);
        if(!verifyBecomeLiveUserRequest(request))
            return buildReply(reply, MISSING_OR_ILLEGAL_PARAMETERS);
        if (!StringUtils.isNullOrBlank(user.getLiveUserId())) {
            var liveUser = liveUserDaoService.findByUserId(user.getId());
            if(verifyUtil.checkIsLiveUser(user) || Objects.nonNull(liveUser) && Objects.equals(UNAPPROVED,liveUser.getLiveStatus())) {
                return buildReply(reply, APPROVE_STATUS_ERROR);
            }
        }
        Constant.PlatformType platform = Objects.equals(request.getPlatformType(), PLATFORM_DEFAULT) ? PLATFORM_JINLI : request.getPlatformType();
        LiveUser liveUser = liveUserBuilder.becomeLiveUserByUserAndStatue(user, UNUPLOAD_IMAGE,platform);
        if(Objects.isNull(liveUser)) return buildReply(reply, APPROVE_STATUS_ERROR);
        liveUser.setRealName(request.getRealName());
        liveUser.setGender(request.getGender());
        liveUser.setCountry(request.getCountry());
        liveUser.setAddress(request.getAddress());
        liveUser.setPhoneNumber(request.getPhoneNumber());
        liveUser.setEmail(request.getEmail());
        liveUser.setContactWay(request.getContactWay());
        liveUser.setBirthDay(Long.parseLong(request.getBirthDay()));
        liveUser.setBankCard(request.getBankCard());
        liveUser.setBankName(request.getBankName());
        liveUser.setApplyTime(System.currentTimeMillis());
        DataManager.saveRoomKeyToChannel(ctx, liveUser.getRoomId());
        dataManager.saveLiveUser(liveUser);
        EventPublisher.publish(TaskEvent.newInstance(user.getId(), ConditionType.submitLiveUserRequest,1));
        return buildReply(reply, resultCode);
    }

    private boolean verifyBecomeLiveUserRequest(Jinli.BecomeLiveUserRequest request) {
        return !StringUtils.isNullOrBlank(request.getRealName()) && request.getGender() != null
                && request.getPlatformType() != null
                && !Objects.equals(request.getGender(), Gender_NULL) && !StringUtils.isNullOrBlank(request.getCountry())
                && !StringUtils.isNullOrBlank(request.getAddress())
                && !StringUtils.isNullOrBlank(request.getPhoneNumber()) && !StringUtils.isNullOrBlank(request.getEmail())
                && !StringUtils.isNullOrBlank(request.getContactWay())
                && !StringUtils.isNullOrBlank(request.getBirthDay())
                && !StringUtils.isNullOrBlank(request.getBankName())
                && !StringUtils.isNullOrBlank(request.getBankCard());
    }
}