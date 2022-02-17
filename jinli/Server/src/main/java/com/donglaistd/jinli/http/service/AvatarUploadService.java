package com.donglaistd.jinli.http.service;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.config.ConditionType;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.event.TaskEvent;
import com.donglaistd.jinli.http.entity.UploadFileInfo;
import com.donglaistd.jinli.service.EventPublisher;
import com.donglaistd.jinli.util.DataManager;
import com.donglaistd.jinli.util.HttpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

import static com.donglaistd.jinli.Constant.ResultCode.ILLEGAL_OPERATION;
import static com.donglaistd.jinli.Constant.ResultCode.SUCCESS;

@Component
public class AvatarUploadService extends RequestUploadService{
    private Logger logger = Logger.getLogger(AvatarUploadService.class.getName());
    @Value("${data.avatar.max_width}")
    private int limitWidth;
    @Value("${data.avatar.max_height}")
    private int limitHeight;

    @Autowired
    DataManager dataManager;
    @Autowired
    HttpUtil httpUtil;

    @Override
    public Constant.ResultCode handle(UploadFileInfo uploadFileInfo, User user) {
        String avatar = uploadFileInfo.getFile_path().get(0);
        user = dataManager.findUser(user.getId());
        if(uploadFileInfo.getFile_path().size()>1) {
            logger.info("user avatar required only one");
            return ILLEGAL_OPERATION;
        }
        user.setAvatarUrl(avatar);
        dataManager.saveUser(user);
        EventPublisher.publish(TaskEvent.newInstance(user.getId(), ConditionType.uploadAvatar,1));
        return Constant.ResultCode.SUCCESS;
    }
}
