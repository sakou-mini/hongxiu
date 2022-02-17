package com.donglai.web.web.dto.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiParam;
import lombok.Data;

@Data
@ApiModel("主播列表请求")
public class LiveUserListRequest {
    @ApiParam(value = "昵称")
    private String nickname;
    @ApiParam(value = "主播id")
    private String liveUserId;
    @ApiParam(value = "直播间id")
    private String roomId;
    @ApiParam(value = "查询状态-> -1全部  0正常 1封禁", example = "0")
    private int status;
    @ApiParam(value = "分页")
    private int page;
    @ApiParam(value = "分页大小")
    private int size;
}
