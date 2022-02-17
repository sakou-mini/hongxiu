package com.donglai.web.web.controller.system;

import com.donglai.web.config.annotation.BackLog;
import com.donglai.web.response.RestResponse;
import com.donglai.web.response.SuccessResponse;
import com.donglai.web.service.RoleBackService;
import com.donglai.web.web.dto.request.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Moon
 * @date 2021-12-29 14:12
 */
@RestController
@RequestMapping("/api/v1/role")
@Api(value = "RoleController", tags = "组别管理")
public class RoleController {

    @Autowired
    private RoleBackService roleBackService;

    @GetMapping("/findConditionList")
    @ApiOperation(value = "组别列表")
    public RestResponse findConditionList(FindConditionListRequest request) {
        return new SuccessResponse().withData(roleBackService.findConditionList(request));
    }

    @PostMapping("/addRole")
    @ApiOperation(value = "组别 添加")
    @BackLog(name = "添加组别")
    public RestResponse addRole(AddRoleRequest request) {
        return roleBackService.addRole(request);
    }

    @PostMapping("/enableOrClose")
    @ApiOperation(value = "组别 启用/关闭")
    @BackLog(name = "启用/关闭组别")
    public RestResponse updateRoleStatus(UpdateRoleStatusRequest request) {
        return roleBackService.updateRoleStatus(request);
    }

    @PostMapping("/deleteRole")
    @ApiOperation(value = "组别 删除")
    @BackLog(name = "删除组别")
    public RestResponse deleteRole(DelRolesRequest request) {
        return roleBackService.deleteRole(request);
    }

    @GetMapping("/roleMenu")
    @ApiOperation(value = "组别 对应的菜单")
    public RestResponse findRoleMenu(String roleId) {
        return roleBackService.findRoleMenu(roleId);
    }

    @PostMapping("/roleUpdate")
    @ApiOperation(value = "组别 修改")
    @BackLog(name = "修改组别")
    public RestResponse updateRoleMenu(UpdateRoleMenuRequest request) {
        return roleBackService.updateRoleMenu(request);
    }

    @GetMapping("/roleItem")
    @ApiOperation(value = "组别 下拉选项")
    public RestResponse getAllRoles(){
        return roleBackService.getAllUsableRoles();
    }

}
