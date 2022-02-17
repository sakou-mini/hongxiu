package com.donglai.web.service;

import com.donglai.web.response.PageInfo;
import com.donglai.web.response.RestResponse;
import com.donglai.web.web.dto.reply.BackOfficeUserDto;
import com.donglai.web.web.dto.request.AddBackUserRequest;
import com.donglai.web.web.dto.request.BackOfficeUserFindListRequest;
import com.donglai.web.web.dto.request.UpdateBackOfficeUserRequest;
import com.donglai.web.web.dto.request.UpdateUserEnableRequest;

/**
 * @author Moon
 * @date 2021-12-29 10:26
 */
public interface BackOfficeUserBackService {
    PageInfo<BackOfficeUserDto> findList(BackOfficeUserFindListRequest request);

    RestResponse updateUserEnable(UpdateUserEnableRequest request);

    RestResponse updateBackOfficeUser(UpdateBackOfficeUserRequest request);

    RestResponse deleteBackUser(UpdateUserEnableRequest request);

    RestResponse addBackUser(AddBackUserRequest request);

    RestResponse updatePassword(String id, String newPassword);
}
