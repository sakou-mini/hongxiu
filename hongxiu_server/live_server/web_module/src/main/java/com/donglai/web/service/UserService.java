package com.donglai.web.service;

import com.donglai.model.db.entity.common.User;
import com.donglai.web.response.PageInfo;
import com.donglai.web.web.dto.request.AddUserRequest;
import com.donglai.web.web.dto.request.ResetUserRequest;
import com.donglai.web.web.dto.request.UpdateUserStatusRequest;
import com.donglai.web.web.dto.request.UserListRequest;
import com.donglai.web.web.vo.UserVO;

import java.util.List;

/**
 * @author Moon
 * @date 2021-12-21 10:32
 */
public interface UserService {
    /**
     * 返回用户列表
     *
     * @return 返回用户信息列表
     */
    PageInfo<UserVO> getUserList(UserListRequest userListRequest);

    List<User> updateStatus(UpdateUserStatusRequest updateUserStatusRequest);

    User addUser(AddUserRequest request);

    User resetUser(ResetUserRequest request);

    User getUserInfo(String userId);
}
