package com.donglai.web.web.dto.request;

/**
 * @author Moon
 * @date 2021-12-29 14:52
 */

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiParam;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@ApiModel("添加组别")
public class AddRoleRequest {
    @ApiParam(value = "组别标记名（英文名）" ,example = "development")
    private String roleName;
    @ApiParam(value = "组别别名字（显示名）" ,example = "开发部")
    private String roleAlias;
    @ApiParam(value = "状态， true 启用， false 禁用" ,example = "true")
    private Boolean status;
    @ApiParam(value = "菜单id" ,example = "1,2,3,4")
    private List<String> menuIds = new ArrayList<>();
}
