package com.donglaistd.jinli.processors.handler;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.config.ConditionType;
import com.donglaistd.jinli.database.dao.UserDaoService;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.event.TaskEvent;
import com.donglaistd.jinli.service.EventPublisher;
import com.donglaistd.jinli.util.DataManager;
import com.donglaistd.jinli.util.StringUtils;
import com.donglaistd.jinli.util.WordFilterUtil;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.logging.Logger;

import static com.donglaistd.jinli.Constant.ResultCode.DISPLAY_NAME_EXIST;
import static com.donglaistd.jinli.Constant.ResultCode.ILLEGAL_DISPLAY_NAME_LENGTH;
import static com.donglaistd.jinli.util.MessageUtil.buildReply;

@Component
public class UpdateUserInfoRequestHandler extends MessageHandler {
    private static final Logger logger = Logger.getLogger(UpdateUserInfoRequestHandler.class.getName());

    @Autowired
    private UserDaoService userDaoService;

    @Autowired
    DataManager dataManager;

    @Value("${data.avatar.max_width}")
    private int IMAGE_WIDTH;
    @Value("${data.avatar.max_height}")
    private int IMAGE_HEIGHT;

    @Value("${data.avatar.save_path}")
    private String savePath;
    @Value("${min.display.name.length}")
    private int minDisplayNameLength;
    @Value("${max.display.name.length}")
    private int maxDisplayNameLength;

    @Override
    public Jinli.JinliMessageReply doHandle(ChannelHandlerContext ctx, Jinli.JinliMessageRequest messageRequest, User user) {
        var message = messageRequest.getUpdateUserInfoRequest();
        var reply = Jinli.UpdateUserInfoReply.newBuilder();
        var displayName = message.getDisplayName();
        if (!StringUtils.isNullOrBlank(displayName)) {
            if(userDaoService.existByDisplayName(displayName)){
                return buildReply(reply, DISPLAY_NAME_EXIST);
            }
        }
        int displayNameLength = WordFilterUtil.getWordCount(displayName);
        if(displayNameLength < minDisplayNameLength || displayNameLength > maxDisplayNameLength)
            return buildReply(reply, ILLEGAL_DISPLAY_NAME_LENGTH);
        var bytes = message.getAvatarData();
        if (bytes.size() != 0) {
            var timeString = new SimpleDateFormat("yyyyMMddhhmmss").format(Calendar.getInstance().getTime());
            resultCode = saveImage(bytes, savePath, IMAGE_WIDTH, IMAGE_HEIGHT, timeString, user);
            if (Constant.ResultCode.SUCCESS.equals(resultCode)) {
                user.setAvatarUrl(user.getId() + "/" + timeString);
                logger.info("user:"+user.getDisplayName()+" update image: the AvatarUrl is:------>  "+user.getAvatarUrl());
            } else {
                logger.info("user:"+user.getDisplayName()+" update image: the AvatarUrl is:failed");
                return buildReply(reply, resultCode);
            }
        }

        if (!StringUtils.isNullOrBlank(displayName)) {
            user.increaseModifyNameCount();
            user.setDisplayName(displayName);
            resultCode = Constant.ResultCode.SUCCESS;
            EventPublisher.publish(TaskEvent.newInstance(user.getId(), ConditionType.changeName,1));
        }
        dataManager.saveUser(user);
        return buildReply(reply, resultCode);
    }
}