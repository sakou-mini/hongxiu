package com.donglai.model.service.impl;

import com.donglai.common.constant.UserPermissionSettingType;
import com.donglai.model.db.entity.common.PersonalSetting;
import com.donglai.model.db.service.common.FollowListService;
import com.donglai.model.db.service.common.PersonalSettingService;
import com.donglai.model.service.UserPermissionService;
import com.donglai.protocol.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class UserPermissionServiceImpl implements UserPermissionService {
    @Autowired
    PersonalSettingService personalSettingService;
    @Autowired
    FollowListService followListService;

    @Override
    public boolean permissionAllow(UserPermissionSettingType permissionType, String verifyUserId, String requestUser) {
        PersonalSetting personalSetting = personalSettingService.findByUserId(verifyUserId);
        if (Objects.isNull(personalSetting)) return false;
        switch (permissionType) {
            case PER_COMMENT:   //博客的评论权限
                return verifyUserPermissionByPermissionVisibleType(personalSetting.getCommentPermission(), verifyUserId, requestUser);
            case PER_PRIVATE_CHAT: //是否允许私信
                return verifyUserPermissionByPermissionVisibleType(personalSetting.getPrivateChatPermission(), verifyUserId, requestUser);
            case PER_SHOW_BLOGS_PRAISE: //是否显示我的点赞
                return personalSetting.isShowMyBlogsPraise();
            case PER_SHOW_FANS_LEADER_LIST://是否显示我的粉丝和关注列表
                return personalSetting.isShowFansAndLeaderList();
        }
        return false;
    }

    public boolean verifyUserPermissionByPermissionVisibleType(Constant.PermissionVisibleType permissionVisibleType, String verifyUserId, String requestUser) {
        switch (permissionVisibleType) {
            case PERMISSION_ALL: //所有玩家
                return true;
            case PERMISSION_FRIEND: //互关
                return followListService.isUserFollower(verifyUserId, requestUser)
                        && followListService.isUserFollower(requestUser, verifyUserId);
            case PERMISSION_LEADER: //博主
                return followListService.isUserFollower(verifyUserId, requestUser);
            case PERMISSION_PRIVATE: //不开放
            default:
                return false;
        }
    }
}
