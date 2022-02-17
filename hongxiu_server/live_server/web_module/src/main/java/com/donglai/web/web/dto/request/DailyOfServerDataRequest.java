package com.donglai.web.web.dto.request;

import com.donglai.common.util.TimeUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel(value = "请求每日服务器数据")
@Data
public class DailyOfServerDataRequest {
    @ApiModelProperty(value = "开始日期", example = "1600000000")
    private long startTime;
    @ApiModelProperty(value = "结束日期", example = "1600000000")
    private long endTime;

    public long getStartTime() {
        return TimeUtil.getTimeDayStartTime(startTime);
    }

    public long getEndTime() {
        return TimeUtil.getTimeDayEndTime(endTime);
    }
}
