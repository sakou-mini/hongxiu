package com.donglai.web.web.dto.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiParam;
import lombok.Data;

import java.util.List;

/**
 * @author Moon
 * @date 2021-12-29 13:42
 */
@Data
@ApiModel("修改后台用户状态请求")
public class UpdateUserEnableRequest {
    @ApiParam(value = "账号id")
    private List<String> ids;
    @ApiParam(value = "是否启用， （非必要参数）仅在启用/关闭 时 赋值")
    private Boolean enabled;
}
