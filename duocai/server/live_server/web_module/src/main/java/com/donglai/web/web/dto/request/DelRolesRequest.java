package com.donglai.web.web.dto.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiParam;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@ApiModel("删除角色")
public class DelRolesRequest {
    @ApiParam(value = "角色id 列表",example = "1,2,3,4")
    List<String> roleIds = new ArrayList<>();
}
