package com.donglai.web.service.impl;

import com.donglai.common.util.StringUtils;
import com.donglai.web.db.backoffice.entity.BackOfficeUser;
import com.donglai.web.db.backoffice.entity.Role;
import com.donglai.web.db.backoffice.service.BackOfficeUserService;
import com.donglai.web.db.backoffice.service.RoleService;
import com.donglai.web.process.ShiroProcess;
import com.donglai.web.response.*;
import com.donglai.web.service.BackOfficeUserBackService;
import com.donglai.web.util.ConvertUtils;
import com.donglai.web.web.dto.reply.BackOfficeUserDto;
import com.donglai.web.web.dto.request.AddBackUserRequest;
import com.donglai.web.web.dto.request.BackOfficeUserFindListRequest;
import com.donglai.web.web.dto.request.UpdateBackOfficeUserRequest;
import com.donglai.web.web.dto.request.UpdateUserEnableRequest;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

import static com.donglai.web.constant.WebConstant.PASSWORD_MAX_LENGTH;
import static com.donglai.web.constant.WebConstant.PASSWORD_MIN_LENGTH;

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
    ShiroProcess shiroProcess;

    @Override
    public PageInfo<BackOfficeUserDto> findList(BackOfficeUserFindListRequest request) {
        PageInfo<BackOfficeUser> backOfficeUserPageInfo = backOfficeUserService.findBackOfficeUserList(request);
        List<BackOfficeUserDto> contents = ConvertUtils.backOfficeUserListToBackOfficeUserVOList(backOfficeUserPageInfo.getContent());
        return new PageInfo<>(backOfficeUserPageInfo, contents);
    }

    @Override
    public RestResponse updateUserEnable(UpdateUserEnableRequest request) {
        if (CollectionUtils.isEmpty(request.getIds()) || Objects.isNull(request.getEnabled())) {
            return new ErrorResponse(GlobalResponseCode.PARAM_ERROR);
        }
        List<BackOfficeUser> backOfficeUsers = backOfficeUserService.findByIdIn(request.getIds());
        if(backOfficeUsers.stream().anyMatch(BackOfficeUser::hasAdminRole)){
            return new ErrorResponse(GlobalResponseCode.ROLE_HAS_ADMIN);
        }
        var operator = (BackOfficeUser) SecurityUtils.getSubject().getPrincipal();
        backOfficeUsers.forEach(v -> {
            v.setEnabled(request.getEnabled());
            v.setUpdateTime(System.currentTimeMillis());
            v.setOperator_id(operator.getId());
        });
        //如果禁用账号
        if(!request.getEnabled()){
            shiroProcess.logoutByBackUsers(backOfficeUsers);
        }
        return new SuccessResponse().withData(backOfficeUserService.saveAll(backOfficeUsers));
    }

    @Override
    public RestResponse updateBackOfficeUser(UpdateBackOfficeUserRequest request) {
        if (StringUtils.isNullOrBlank(request.getId())) {
            return new ErrorResponse(GlobalResponseCode.PARAM_ERROR);
        }
        BackOfficeUser backOfficeUser = backOfficeUserService.findById(request.getId());
        if (Objects.isNull(backOfficeUser)) {
            return new ErrorResponse(GlobalResponseCode.USER_NOT_FOUND);
        }else {
            backOfficeUser.setUpdateTime(System.currentTimeMillis());
            if(!StringUtils.isNullOrBlank(request.getRoleId())){
                //不允许修改超管角色
                if(backOfficeUser.hasAdminRole())
                    return new ErrorResponse(GlobalResponseCode.ROLE_HAS_ADMIN);
                Role role = roleService.findByRoleId(request.getRoleId());
                if(Objects.isNull(role))  return new ErrorResponse(GlobalResponseCode.ROLE_NOT_EXISTS);
                if(role.isAdminRole()) return new ErrorResponse(GlobalResponseCode.ROLE_ILLEGALITY);
                backOfficeUser.setRoles(Lists.newArrayList(role));
            }
            if (!StringUtils.isNullOrBlank(request.getNickname())) {
                backOfficeUser.setNickname(request.getNickname());
            }
            var operator = (BackOfficeUser) SecurityUtils.getSubject().getPrincipal();
            backOfficeUser.setOperator_id(operator.getId());
            backOfficeUser = backOfficeUserService.save(backOfficeUser);
            //TODO 如果修改了角色则 让该账号退出登录
        }
        return new SuccessResponse().withData(backOfficeUser);
    }

    /*逻辑删除*/
    @Override
    public RestResponse deleteBackUser(UpdateUserEnableRequest request) {
        if (CollectionUtils.isEmpty(request.getIds())) {
            return new ErrorResponse(GlobalResponseCode.PARAM_ERROR);
        }
        List<BackOfficeUser> backOfficeUsers = backOfficeUserService.findByIdIn(request.getIds());
        if(backOfficeUsers.stream().anyMatch(BackOfficeUser::hasAdminRole)){
            return new ErrorResponse(GlobalResponseCode.ROLE_HAS_ADMIN);
        }
        if(backOfficeUsers.stream().anyMatch(BackOfficeUser::hasAdminPlatformRole)){
            return new ErrorResponse(GlobalResponseCode.ROLE_IS_PLATFORM);
        }
        var operator = (BackOfficeUser) SecurityUtils.getSubject().getPrincipal();

        backOfficeUsers.forEach(backOfficeUser -> {
            backOfficeUser.setAccountNonLocked(false);
            backOfficeUser.setUpdateTime(System.currentTimeMillis());
            backOfficeUser.setOperator_id(operator.getId());
        });

        backOfficeUserService.saveAll(backOfficeUsers);
        return new SuccessResponse();
    }

    @Override
    public RestResponse addBackUser(AddBackUserRequest request) {
        if (StringUtils.isNullOrBlank(request.getNickname())
                || StringUtils.isNullOrBlank(request.getRoleId())
                || StringUtils.isNullOrBlank(request.getUsername())
                || Objects.isNull(request.getStatus())) {
            return new ErrorResponse(GlobalResponseCode.PARAM_ERROR);
        }
        else if(Objects.nonNull(backOfficeUserService.findByUserName(request.getUsername())))
            return new ErrorResponse(GlobalResponseCode.ACCOUNT_EXISTS);
        else if(verifyPasswordNotPass(request.getPwd()))
            return new ErrorResponse(GlobalResponseCode.PASSWORD_ILLEGALITY);
        Role role = roleService.findByRoleId(request.getRoleId());
        if(Objects.isNull(role))
            return new ErrorResponse(GlobalResponseCode.ROLE_NOT_EXISTS);
        if(role.isAdminRole())
            return new ErrorResponse(GlobalResponseCode.ROLE_ILLEGALITY);
        BackOfficeUser backOfficeUser = BackOfficeUser.newInstance(request.getUsername(), request.getPwd(), request.getNickname(), Lists.newArrayList(role));
        backOfficeUser.setEnabled(request.getStatus());
        var operator = (BackOfficeUser) SecurityUtils.getSubject().getPrincipal();
        backOfficeUser.setOperator_id(operator.getId());
        backOfficeUserService.save(backOfficeUser);
        return new SuccessResponse();
    }

    public boolean verifyPasswordNotPass(String newPassword){
        return StringUtils.isNullOrBlank(newPassword) || newPassword.length() < PASSWORD_MIN_LENGTH || newPassword.length() > PASSWORD_MAX_LENGTH;
    }

    @Override
    public RestResponse updatePassword(String id, String newPassword) {
        if (StringUtils.isNullOrBlank(id)) {
            return new ErrorResponse(GlobalResponseCode.PARAM_ERROR);
        }else if(verifyPasswordNotPass(newPassword)){
            return new ErrorResponse(GlobalResponseCode.PASSWORD_ILLEGALITY);
        }else{
            BackOfficeUser backOfficeUser = backOfficeUserService.findById(id);
            if (Objects.nonNull(backOfficeUser)) {
                backOfficeUser.setPassword(newPassword);
                backOfficeUserService.save(backOfficeUser);
                return new SuccessResponse();
            }else {
                return new ErrorResponse(GlobalResponseCode.USER_NOT_FOUND);
            }
        }
    }

}
