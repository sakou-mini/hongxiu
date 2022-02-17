package com.donglai.web.web.controller.server;

import com.donglai.web.response.RestResponse;
import com.donglai.web.response.SuccessResponse;
import com.donglai.web.service.RoleBackService;
import com.donglai.web.web.dto.request.AddRoleRequest;
import com.donglai.web.web.dto.request.FindConditionListRequest;
import com.donglai.web.web.dto.request.UpdateRoleStatusRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Moon
 * @date 2021-12-29 14:12
 */
@RestController
@RequestMapping("/api/v1/role")
public class RoleController {

    @Autowired
    private RoleBackService roleBackService;

    @PostMapping("/findConditionList")
    public RestResponse findConditionList(FindConditionListRequest request) {
        return new SuccessResponse().withData(roleBackService.findConditionList(request));
    }

    @PostMapping("/addRole")
    public RestResponse addRole(AddRoleRequest request) {
        return roleBackService.addRole(request);
    }

    @PostMapping("/updateRoleStatus")
    public RestResponse updateRoleStatus(UpdateRoleStatusRequest request) {
        return roleBackService.updateRoleStatus(request);
    }

    @PostMapping("/deleteRole")
    public RestResponse deleteRole(UpdateRoleStatusRequest request) {
        return roleBackService.deleteRole(request);
    }

    @PostMapping("/findRoleMenu")
    public RestResponse findRoleMenu(String roleId) {
        return roleBackService.findRoleMenu(roleId);
    }

}
