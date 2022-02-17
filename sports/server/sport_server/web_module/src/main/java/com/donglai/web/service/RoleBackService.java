package com.donglai.web.service;

import com.donglai.web.response.PageInfo;
import com.donglai.web.response.RestResponse;
import com.donglai.web.web.dto.reply.RoleListReply;
import com.donglai.web.web.dto.request.*;

/**
 * @author Moon
 * @date 2021-12-29 14:23
 */
public interface RoleBackService {
    PageInfo<RoleListReply> findConditionList(FindConditionListRequest request);

    RestResponse addRole(AddRoleRequest request);

    RestResponse updateRoleStatus(UpdateRoleStatusRequest request);

    RestResponse deleteRole(DelRolesRequest request);

    RestResponse findRoleMenu(String roleId);

    RestResponse updateRoleMenu(UpdateRoleMenuRequest request);

    RestResponse getAllUsableRoles();
}
