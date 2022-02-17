package com.donglai.web.service;

import com.donglai.web.db.backoffice.entity.Role;
import com.donglai.web.response.PageInfo;
import com.donglai.web.response.RestResponse;
import com.donglai.web.web.dto.request.AddRoleRequest;
import com.donglai.web.web.dto.request.FindConditionListRequest;
import com.donglai.web.web.dto.request.UpdateRoleStatusRequest;

/**
 * @author Moon
 * @date 2021-12-29 14:23
 */
public interface RoleBackService {
    PageInfo<Role> findConditionList(FindConditionListRequest request);

    RestResponse addRole(AddRoleRequest request);

    RestResponse updateRoleStatus(UpdateRoleStatusRequest request);

    RestResponse deleteRole(UpdateRoleStatusRequest request);

    RestResponse findRoleMenu(String roleId);

}
