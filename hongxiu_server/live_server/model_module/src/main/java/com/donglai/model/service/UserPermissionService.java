package com.donglai.model.service;

import com.donglai.common.constant.UserPermissionSettingType;

public interface UserPermissionService {
    boolean permissionAllow(UserPermissionSettingType permissionType, String verifyUserId, String requestUser);
}
