package com.donglai.web.web.dto.reply;

import com.donglai.model.db.entity.statistics.DailyOfServerData;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@ApiModel(value = "每日数据汇总")
@Data
@AllArgsConstructor
public class DailyOfServerDataReply {
    @ApiModelProperty(value = "汇总数据")
    private DailyOfServerData totalData;
    @ApiModelProperty(value = "每日数据详情")
    private List<DailyOfServerData> dailyData;
}
