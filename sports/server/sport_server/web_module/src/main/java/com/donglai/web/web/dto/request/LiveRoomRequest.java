package com.donglai.web.web.dto.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiParam;
import lombok.Data;

@Data
@ApiModel(value = "直播监控请求")
public class LiveRoomRequest {
    @ApiParam(value = "直播间id")
    private String roomId;
    @ApiParam(value = "主播id")
    private String liveUserId;
    @ApiParam(value = "用户名")
    private String nickname;
    @ApiParam(value = "分页")
    private int page;
    @ApiParam(value = "分页大小")
    private int size;
}
