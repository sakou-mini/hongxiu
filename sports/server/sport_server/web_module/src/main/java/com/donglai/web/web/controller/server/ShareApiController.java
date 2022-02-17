package com.donglai.web.web.controller.server;

import com.donglai.model.db.entity.live.LiveDomain;
import com.donglai.model.db.service.live.LiveDomainService;
import com.donglai.web.process.CDNProcess;
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
@RequestMapping("/api/share")
@Api(value = "ShareApiController", tags = "公共API ,无权限限制")
public class ShareApiController {
    @Autowired
    LiveDomainService liveDomainService;
    @Autowired
    PlatformApiProcess platformApiProcess;
    @Autowired
    CDNProcess cdnProcess;

    @RequestMapping("/liveDomainList")
    public LiveDomain getDomainList(@RequestParam("lineCode") int lineCode) {
        return liveDomainService.findByLineCode(lineCode);
    }

    @PostMapping("/liveRoomLiveStream")
    public RestResponse liveRoomLiveStream(HttpServletRequest request, String roomId,String token){
        return platformApiProcess.getLiveRoomLiveStream(IpUtil.getIP(request),roomId, token);
    }

    @RequestMapping("/cdnLine")
    @ApiOperation("cdn可用线路")
    public RestResponse cdnLine(){
        return new SuccessResponse().withData(cdnProcess.getCdnLine());
    }

}
