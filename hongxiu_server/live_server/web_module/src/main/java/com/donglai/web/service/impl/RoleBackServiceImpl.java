package com.donglai.web.service.impl;

import com.donglai.web.db.backoffice.entity.Role;
import com.donglai.web.db.backoffice.entity.RoleMenu;
import com.donglai.web.db.backoffice.service.RoleMenuService;
import com.donglai.web.db.backoffice.service.RoleService;
import com.donglai.web.db.server.service.RoleDbService;
import com.donglai.web.response.*;
import com.donglai.web.service.RoleBackService;
import com.donglai.web.web.dto.request.AddRoleRequest;
import com.donglai.web.web.dto.request.FindConditionListRequest;
import com.donglai.web.web.dto.request.UpdateRoleStatusRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @author Moon
 * @date 2021-12-29 14:24
 */
@Service
public class RoleBackServiceImpl implements RoleBackService {

    @Autowired
    private RoleMenuService roleMenuService;
    @Autowired
    private RoleService roleService;
    @Autowired
    private RoleDbService roleDbService;

    @Override
    public RestResponse addRole(AddRoleRequest request) {
        if (Objects.isNull(request.getRoleName())
                || Objects.isNull(request.getStatus())
                || CollectionUtils.isEmpty(request.getMenuIds())) {
            return new ErrorResponse(GlobalResponseCode.PARAM_ERROR);
        }
        Role role = new Role();
        role.setName(request.getRoleName());
        role.setCreated(new Date());
        role.setRoleAlias(request.getRoleName());
        role.setStatus(request.getStatus());
        role = roleService.save(role);

        for (String menuId : request.getMenuIds()) {
            RoleMenu roleMenu = new RoleMenu();
            roleMenu.setMenuId(menuId);
            roleMenu.setRoleId(role.getId());
            roleMenuService.save(roleMenu);
        }
        return new SuccessResponse().withData(role);
    }

    @Override
    public RestResponse findRoleMenu(String roleId) {
        if (StringUtils.isEmpty(roleId)) {
            return new ErrorResponse(GlobalResponseCode.PARAM_ERROR);
        }
        return new SuccessResponse().withData(roleMenuService.findByRoleId(roleId));
    }

    @Override
    public RestResponse deleteRole(UpdateRoleStatusRequest request) {
        if (CollectionUtils.isEmpty(request.getIds())) {
            return new ErrorResponse(GlobalResponseCode.PARAM_ERROR);
        }
        return new SuccessResponse().withData(roleService.deleteByIdIn(request.getIds()));
    }

    @Override
    public RestResponse updateRoleStatus(UpdateRoleStatusRequest request) {
        if (CollectionUtils.isEmpty(request.getIds())
                || Objects.isNull(request.getStatus())) {
            return new ErrorResponse(GlobalResponseCode.PARAM_ERROR);
        }
        List<Role> roles = roleService.findByRoleIdIn(request.getIds());

        roles.forEach(v -> {
            v.setStatus(request.getStatus());
            v.setUpdated(new Date());
        });

        return new SuccessResponse().withData(roleService.saveAll(roles));
    }

    @Override
    public PageInfo<Role> findConditionList(FindConditionListRequest request) {
        return roleDbService.findConditionList(request);
    }
}