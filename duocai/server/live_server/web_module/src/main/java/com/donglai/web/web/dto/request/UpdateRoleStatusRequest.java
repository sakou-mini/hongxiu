package com.donglai.web.web.dto.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiParam;
import lombok.Data;

import java.util.List;

/**
 * @author Moon
 * @date 2021-12-29 15:16
 */
@Data
@ApiModel("修改后台角色")
public class UpdateRoleStatusRequest {
    @ApiParam(value = "角色id",example = "1,2,3,4")
    private List<String> roleIds;
    @ApiParam(value = "角色状态 ， 删除时可以不传",example = "true 启用，false禁用")
    private Boolean status;
}
