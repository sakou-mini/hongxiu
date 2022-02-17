package com.donglai.web.web.controller.server;

import com.donglai.model.db.entity.live.LiveDomain;
import com.donglai.model.db.service.live.LiveDomainService;
import com.donglai.web.process.PlatformApiProcess;
import com.donglai.web.response.RestResponse;
import com.donglai.web.response.SuccessResponse;
import com.donglai.web.util.IpUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/v1/share")
@Api(value = "公共API ,无权限限制", tags = "ShareApiController")
public class ShareApiController {
    @Autowired
    LiveDomainService liveDomainService;
    @Autowired
    PlatformApiProcess platformApiProcess;
    @Autowired
    com.donglai.model.db.service.blogs.BlogsLabelsConfigService blogsLabelsConfigService;

    @ApiOperation(value = "获取直播域名列表")
    @RequestMapping("/liveDomainList")
    public LiveDomain getDomainList(@RequestParam("lineCode") int lineCode) {
        return liveDomainService.findByLineCode(lineCode);
    }

    @ApiOperation(value = "获取直播拉流地址")
    @PostMapping("/liveRoomLiveStream")
    public RestResponse liveRoomLiveStream(HttpServletRequest request, String roomId, String token) {
        return platformApiProcess.getLiveRoomLiveStream(IpUtil.getIP(request), roomId, token);
    }

    @ApiOperation(value = "获取动态标签")
    @GetMapping("/blogsLabel")
    public RestResponse liveRoomLiveStream() {
        return new SuccessResponse().withData(blogsLabelsConfigService.findAll());
    }

}
