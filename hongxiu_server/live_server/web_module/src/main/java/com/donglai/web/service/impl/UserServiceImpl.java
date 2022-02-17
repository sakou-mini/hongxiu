package com.donglai.web.service.impl;

import com.donglai.common.util.PasswordUtil;
import com.donglai.model.db.entity.common.User;
import com.donglai.model.db.service.blogs.BlogsService;
import com.donglai.model.db.service.common.FollowListService;
import com.donglai.web.builder.UserBuilder;
import com.donglai.web.response.PageInfo;
import com.donglai.web.service.UserService;
import com.donglai.web.util.ConvertUtils;
import com.donglai.web.web.dto.request.AddUserRequest;
import com.donglai.web.web.dto.request.ResetUserRequest;
import com.donglai.web.web.dto.request.UpdateUserStatusRequest;
import com.donglai.web.web.dto.request.UserListRequest;
import com.donglai.web.web.vo.UserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.donglai.common.constant.PathConstant.DEFAULT_AVATAR_PATH;

/**
 * @author Moon
 * @date 2021-12-21 10:32
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private com.donglai.web.db.server.service.UserDbService userDbService;

    @Autowired
    private com.donglai.model.db.service.common.UserService userService;

    @Autowired
    private BlogsService blogsService;

    @Autowired
    private com.donglai.model.db.service.blogs.BlogsCommentService blogsCommentService;

    @Autowired
    private UserBuilder userBuilder;
    @Autowired
    private FollowListService followListService;

    @Override
    public User addUser(AddUserRequest request) {
        User user = userBuilder.createUser(request.getNickname(), request.getPhoneNumber(),
                PasswordUtil.encodePassword(request.getPassword()), request.getStatus());
        return userService.save(user);
    }

    @Override
    public List<User> updateStatus(UpdateUserStatusRequest request) {
        List<String> userIds = request.getUserIds();
        List<User> byIds = userService.findByIds(userIds);
        for (User byId : byIds) {
            byId.setStatus(request.getStatus());
            byId.setReason(request.getReason());
            byId.setUpdateTime(System.currentTimeMillis());
        }
        return userService.saveAll(byIds);
    }

    @Override
    public PageInfo<UserVO> getUserList(UserListRequest userListRequest) {
        PageInfo<User> userPageInfo = userDbService.conditionGetUser(userListRequest);
        List<UserVO> userVoS = ConvertUtils.userListToUserVOList(userPageInfo.getContent());
        for (UserVO userVo : userVoS) {
            //添加粉丝 评论 关注 作品数
            String userId = userVo.getId();
            //作品数量
            userVo.setBlogsCount((long) blogsService.findAllPassBlogs().size());
            //粉丝数
            userVo.setFansCount(followListService.countFollowerNumByUserId(userId));
            //关注数
            userVo.setFocusCount(followListService.countLeaderNumByUserId(userId));
            //评论数
            userVo.setCommentCount(blogsCommentService.findComentCountByUserId(userId));
        }
        return new PageInfo<>(userPageInfo.getPageNum(), userPageInfo.getPageSize(), userPageInfo.getTotal(), userVoS);
    }

    @Override
    public User getUserInfo(String userId) {
        return userService.findById(userId);
    }

    @Override
    public User resetUser(ResetUserRequest request) {
        String userId = request.getUserId();
        User byId = userService.findById(userId);
        switch (request.getResetType()) {
            case 0:
                //重置用户名
                byId.setNickname(byId.getAccountId());
                break;
            case 1:
                //重置头像
                byId.setAvatarUrl(DEFAULT_AVATAR_PATH);
                break;
            case 2:
                //重置简介
                byId.setSignatureText(null);
                break;
            default:
                return byId;
        }
        return userService.save(byId);
    }
}
