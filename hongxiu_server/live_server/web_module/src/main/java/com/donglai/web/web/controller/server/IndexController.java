package com.donglai.web.web.controller.server;

import com.donglai.web.process.IndexProcess;
import com.donglai.web.response.RestResponse;
import com.donglai.web.response.SuccessResponse;
import com.donglai.web.web.dto.reply.DailyOfServerDataReply;
import com.donglai.web.web.dto.request.DailyOfServerDataRequest;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = {"首页Api接口"})
@RestController
@RequestMapping("/api/v1/index")
public class IndexController {
    @Autowired
    IndexProcess indexProcess;

    @PostMapping("/dailyOfServerData")
    @ApiOperation(value = "获取每日统计数据")
    public RestResponse dailyOfServerData(DailyOfServerDataRequest request) {
        DailyOfServerDataReply reply = indexProcess.queryDailyOfServerData(request);
        return new SuccessResponse().withData(reply);
    }

    @GetMapping("/todayServerData")
    @ApiOperation(value = "获取今日统计数据")
    public RestResponse todayServerData() {
        return new SuccessResponse().withData(indexProcess.queryTodayServerData());
    }
}
