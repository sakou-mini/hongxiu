package com.donglai.web.service.impl;

import com.donglai.web.db.backoffice.entity.BackOfficeUser;
import com.donglai.web.db.backoffice.entity.Role;
import com.donglai.web.db.backoffice.service.BackOfficeUserService;
import com.donglai.web.db.backoffice.service.RoleService;
import com.donglai.web.db.server.service.BackOfficeUserDbService;
import com.donglai.web.response.*;
import com.donglai.web.service.BackOfficeUserBackService;
import com.donglai.web.util.ConvertUtils;
import com.donglai.web.web.dto.request.AddBackUserRequest;
import com.donglai.web.web.dto.request.BackOfficeUserFindListRequest;
import com.donglai.web.web.dto.request.UpdateBackOfficeUserRequest;
import com.donglai.web.web.dto.request.UpdateUserEnableRequest;
import com.donglai.web.web.vo.BackOfficeUserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * @author Moon
 * @date 2021-12-29 10:26
 */
@Service
public class BackOfficeUserBackServiceImpl implements BackOfficeUserBackService {
    @Autowired
    private RoleService roleService;
    @Autowired
    private BackOfficeUserService backOfficeUserService;
    @Autowired
    private BackOfficeUserDbService backOfficeUserDbService;

    @Override
    public RestResponse addBackUser(AddBackUserRequest request) {
        if (Objects.isNull(request.getNickname())
                || Objects.isNull(request.getRoleId())
                || Objects.isNull(request.getEnabled())) {
            return new ErrorResponse(GlobalResponseCode.PARAM_ERROR);

        }
        Role byRoleId = roleService.findByRoleId(request.getRoleId());
        BackOfficeUser backOfficeUser = new BackOfficeUser();
        backOfficeUser.setEnabled(request.getEnabled());
        backOfficeUser.setNickname(request.getNickname());
        backOfficeUser.setRoles(Collections.singletonList(byRoleId));
        backOfficeUser.setCreateTime(System.currentTimeMillis());
        backOfficeUser.setUpdateTime(System.currentTimeMillis());
        return new SuccessResponse().withData(backOfficeUserService.save(backOfficeUser));
    }

    @Override
    public RestResponse updatePassword(String id, String oldPassword, String newPassword) {
        if (Objects.isNull(newPassword) || Objects.isNull(id) || Objects.isNull(oldPassword) || newPassword.equals(oldPassword)) {
            return new ErrorResponse(GlobalResponseCode.PARAM_ERROR);
        }
        BackOfficeUser byId = backOfficeUserService.findById(id);
        if (Objects.nonNull(byId)) {
            if (!oldPassword.equals(byId.getPassword())) {
                return new ErrorResponse(10000, "旧密码错误");
            }
            byId.setPassword(newPassword);
        }
        return new SuccessResponse().withData(backOfficeUserService.save(byId));
    }

    @Override
    public RestResponse updateBackOfficeUser(UpdateBackOfficeUserRequest request) {
        if (Objects.isNull(request.getId())) {
            return new ErrorResponse(GlobalResponseCode.PARAM_ERROR);
        }
        BackOfficeUser byId = backOfficeUserService.findById(request.getId());
        if (Objects.nonNull(byId)) {
            byId.setUpdateTime(System.currentTimeMillis());
            if (Objects.nonNull(request.getAvatar())) {
                byId.setAvatar(request.getAvatar());
            }
            if (Objects.nonNull(request.getEmail())) {
                byId.setEmail(request.getEmail());
            }
            if (Objects.nonNull(request.getPhone())) {
                byId.setPhone(request.getPhone());
            }
            if (Objects.nonNull(request.getUsername())) {
                byId.setUsername(request.getUsername());
            }
        }
        return new SuccessResponse().withData(backOfficeUserService.save(byId));
    }

    @Override
    public RestResponse updateUserEnable(UpdateUserEnableRequest request) {
        if (CollectionUtils.isEmpty(request.getIds()) || Objects.isNull(request.getEnabled())) {
            return new ErrorResponse(GlobalResponseCode.PARAM_ERROR);
        }
        List<BackOfficeUser> backOfficeUsers = backOfficeUserService.findByIdIn(request.getIds());
        backOfficeUsers.forEach(v -> {
            v.setEnabled(request.getEnabled());
            v.setUpdateTime(System.currentTimeMillis());
        });
        return new SuccessResponse().withData(backOfficeUserService.saveAll(backOfficeUsers));
    }

    @Override
    public RestResponse deleteBackUser(UpdateUserEnableRequest request) {
        if (CollectionUtils.isEmpty(request.getIds())) {
            return new ErrorResponse(GlobalResponseCode.PARAM_ERROR);
        }
        return new SuccessResponse().withData(backOfficeUserService.deleteByIdIn(request.getIds()));
    }

    @Override
    public PageInfo<BackOfficeUserVO> findList(BackOfficeUserFindListRequest request) {
        PageInfo<BackOfficeUser> backOfficeUserPageInfo = backOfficeUserDbService.conditionGetKeyword(request);

        List<BackOfficeUserVO> vos = ConvertUtils.backOfficeUserListToBackOfficeUserVOList(backOfficeUserPageInfo.getContent());
        return new PageInfo<>(backOfficeUserPageInfo.getPageSize(), backOfficeUserPageInfo.getPageNum(), backOfficeUserPageInfo.getTotal(), vos);
    }
}
