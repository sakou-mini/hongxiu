package com.donglai.web.web.dto.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiParam;
import lombok.Data;

@Data
@ApiModel(value =  "赛事配置")
public class SettingEventLiveRequest {
    @ApiParam(value = "赛事id")
    public String eventId;
    @ApiParam(value = "用户id")
    public String userId;
    @ApiParam(value = "开播时间")
    public Long liveBeginTime;
}
