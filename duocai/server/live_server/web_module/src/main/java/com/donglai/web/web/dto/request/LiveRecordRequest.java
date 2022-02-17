package com.donglai.web.web.dto.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiParam;
import lombok.Data;

@Data
@ApiModel(value =  "直播记录")
public class LiveRecordRequest {
    @ApiParam(value = "主播id")
    private String liveUserId;
    @ApiParam(value = "昵称")
    private String nickname;
    @ApiParam(value = "房间id")
    private String roomId;
    @ApiParam(value = "查询开始时间")
    private Long startTime;
    @ApiParam(value = "查询结束时间")
    private Long endTime;
    @ApiParam(value = "分页")
    private int page;
    @ApiParam(value = "分页大小,如果 小于等于0 则查询所有的记录，可用于批量导出使用",example = "1" )
    private int size;
}
