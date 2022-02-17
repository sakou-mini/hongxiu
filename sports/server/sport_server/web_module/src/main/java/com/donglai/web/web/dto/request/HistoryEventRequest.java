package com.donglai.web.web.dto.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiParam;
import lombok.Data;

@Data
@ApiModel(value =  "赛事记录")
public class HistoryEventRequest {
/*    public Long startTime;
    public Long endTime;
    public String liveUserId;*/
    @ApiParam(value = "分页")
    public int page;
    @ApiParam(value = "分页大小",example = "1" )
    public int size;
}
