package com.donglai.web.service.impl;

import com.donglai.common.util.StringUtils;
import com.donglai.web.db.backoffice.entity.BackOfficeUser;
import com.donglai.web.db.backoffice.entity.Menu;
import com.donglai.web.db.backoffice.entity.Role;
import com.donglai.web.db.backoffice.entity.RoleMenu;
import com.donglai.web.db.backoffice.service.BackOfficeUserService;
import com.donglai.web.db.backoffice.service.MenuService;
import com.donglai.web.db.backoffice.service.RoleMenuService;
import com.donglai.web.db.backoffice.service.RoleService;
import com.donglai.web.process.permission.MenuProcess;
import com.donglai.web.response.*;
import com.donglai.web.service.RoleBackService;
import com.donglai.web.util.ConvertUtils;
import com.donglai.web.web.dto.reply.RoleItemReply;
import com.donglai.web.web.dto.reply.RoleListReply;
import com.donglai.web.web.dto.request.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Moon
 * @date 2021-12-29 14:24
 */
@Service
@Slf4j
public class RoleBackServiceImpl implements RoleBackService {

    @Autowired
    private RoleMenuService roleMenuService;
    @Autowired
    private RoleService roleService;
    @Autowired
    MenuService menuService;
    @Autowired
    BackOfficeUserService backOfficeUserService;
    @Autowired
    MenuProcess menuProcess;

    @Override
    public PageInfo<RoleListReply> findConditionList(FindConditionListRequest request) {
        PageInfo<Role> rolePageInfo = roleService.findConditionList(request);
        List<Role> roleList = rolePageInfo.getContent();
        List<RoleListReply> roleListReplies = ConvertUtils.roleListToRoleListReplyList(rolePageInfo.getContent());
        return new PageInfo<>(rolePageInfo, roleListReplies);
    }


    @Override
    public RestResponse addRole(AddRoleRequest request) {
        if (Objects.isNull(request.getRoleName())
                || Objects.isNull(request.getStatus())
                || StringUtils.isNullOrBlank(request.getRoleName())
                || StringUtils.isNullOrBlank(request.getRoleAlias())) {
            return new ErrorResponse(GlobalResponseCode.PARAM_ERROR);
        }
        else if(Objects.nonNull(roleService.findByRoleName(request.getRoleName()))){
            return new ErrorResponse(GlobalResponseCode.ROLE_NAME_EXISTS);
        }
        else if(menuService.findByIds(request.getMenuIds()).size() != request.getMenuIds().size()){
            return new ErrorResponse(GlobalResponseCode.MENU_NOT_EXISTS);
        }else {
            var operator = (BackOfficeUser) SecurityUtils.getSubject().getPrincipal();
            Role role = Role.newInstance(request.getRoleName(),request.getRoleAlias());
            role.setEnabled(request.getStatus());
            role.setOperator_id(operator.getId());
            role = roleService.save(role);
            log.info("添加角色{}，菜单{}",role,request.getMenuIds());
            List<String> menuIds = menuProcess.formatMenuNode(request.getMenuIds());
            List<RoleMenu> roleMenus = new ArrayList<>(menuIds.size());
            for (String menuId : menuIds) {
                roleMenus.add(RoleMenu.newInstance(role.getId(), menuId));
            }
            roleMenuService.saveAll(roleMenus);
            return new SuccessResponse().withData(role);
        }
    }

    @Override
    public RestResponse updateRoleStatus(UpdateRoleStatusRequest request) {
        var operator = (BackOfficeUser) SecurityUtils.getSubject().getPrincipal();
        if (CollectionUtils.isEmpty(request.getRoleIds())
                || Objects.isNull(request.getStatus())) {
            return new ErrorResponse(GlobalResponseCode.PARAM_ERROR);
        }
        List<Role> roles = roleService.findByRoleIdIn(request.getRoleIds());
        if(roles.size()!=request.getRoleIds().size())
            return new ErrorResponse(GlobalResponseCode.ROLE_NOT_EXISTS);
        if(roles.stream().anyMatch(Role::isAdminRole)){
            return new ErrorResponse(GlobalResponseCode.ROLE_HAS_ADMIN);
        }
        roles.forEach(v -> {
            v.setEnabled(request.getStatus());
            v.setUpdateTime(System.currentTimeMillis());
            v.setOperator_id(operator.getId());
        });
        roleService.saveAll(roles);
        return new SuccessResponse();
    }

    @Override
    public RestResponse deleteRole(DelRolesRequest request) {
        if (CollectionUtils.isEmpty(request.getRoleIds())) {
            return new ErrorResponse(GlobalResponseCode.PARAM_ERROR);
        }
        List<Role> roles = roleService.findByRoleIdIn(request.getRoleIds());
        if(roles.size() != request.getRoleIds().size())
            return new ErrorResponse(GlobalResponseCode.ROLE_NOT_EXISTS);
        else if(roles.stream().anyMatch(role -> role.isAdminRole() || role.isPlatformRole())){
            return new ErrorResponse(GlobalResponseCode.ROLE_HAS_ADMIN);
        }
        //1.清除所有相关角色的 用户角色信息
        List<BackOfficeUser> rolesOfBackUserList = backOfficeUserService.findByHasRoles(roles);
        rolesOfBackUserList.forEach(BackOfficeUser ::cleanRole);
        backOfficeUserService.saveAll(rolesOfBackUserList);
        //2. 清除角色对应的菜单信息
        roleMenuService.deleteByRolesId(request.getRoleIds());
        //3.删除权限
        roleService.deleteByIdIn(request.getRoleIds());
        return new SuccessResponse();
    }

    @Override
    public RestResponse findRoleMenu(String roleId) {
        if (StringUtils.isNullOrBlank(roleId)) {
            return new ErrorResponse(GlobalResponseCode.PARAM_ERROR);
        }
        Role role = roleService.findByRoleId(roleId);
        if(Objects.isNull(role))
            return new ErrorResponse(GlobalResponseCode.ROLE_NOT_EXISTS);
        return new SuccessResponse().withData(menuProcess.getBackUserMenus(role).stream().filter(Menu::notGroupType).collect(Collectors.toList()));
    }

    @Override
    public RestResponse updateRoleMenu(UpdateRoleMenuRequest request) {
        if (StringUtils.isNullOrBlank(request.getRoleId())) {
            return new ErrorResponse(GlobalResponseCode.PARAM_ERROR);
        }else {
            Role role = roleService.findByRoleId(request.getRoleId());
            if(Objects.isNull(role))
                return new ErrorResponse(GlobalResponseCode.ROLE_NOT_EXISTS);
            else if(role.isAdminRole() || role.isPlatformRole())
                return new ErrorResponse(GlobalResponseCode.ROLE_HAS_ADMIN);
            else {
                roleMenuService.deleteByRoleId(request.getRoleId());
                List<String> menuIds = menuProcess.formatMenuNode(request.getMenuIds());
                Set<RoleMenu> roleMenus = menuIds.stream().map((menuId -> RoleMenu.newInstance(request.getRoleId(), menuId))).collect(Collectors.toSet());
                roleMenuService.saveAll(roleMenus);
                if(!StringUtils.isNullOrBlank(request.getRoleAlias())){
                    role.setRoleAlias(request.getRoleAlias());
                    roleService.save(role);
                }
                return new SuccessResponse();
            }
        }
    }

    @Override
    public RestResponse getAllUsableRoles() {
        List<Role> roles = roleService.getAllUsableRoles().stream().filter(role -> !role.isAdminRole()).collect(Collectors.toList());
        List<RoleItemReply> roleItemReplies = roles.stream().map(RoleItemReply::newInstance).collect(Collectors.toList());
        return new SuccessResponse().withData(roleItemReplies);
    }
}