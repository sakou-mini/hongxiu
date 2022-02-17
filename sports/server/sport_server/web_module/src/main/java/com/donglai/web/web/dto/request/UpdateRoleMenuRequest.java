package com.donglai.web.web.dto.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiParam;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("修改组别菜单角色")
public class UpdateRoleMenuRequest {
    @ApiParam(value = "角色id",example = "1")
    private String roleId;
    @ApiParam(value = "菜单 id",example = "1,2,3,4")
    private List<String> menuIds;
    @ApiParam(value = "角色名称",example = "开发部门")
    private String roleAlias;
}
