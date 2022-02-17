package com.donglai.web.web.controller.server;

import com.donglai.web.process.BlogsMusicProcess;
import com.donglai.web.response.*;
import com.donglai.web.web.dto.reply.BlogsMusicListReply;
import com.donglai.web.web.dto.request.AddMusicRequest;
import com.donglai.web.web.dto.request.ApprovalRequest;
import com.donglai.web.web.dto.request.EditMusicRequest;
import com.donglai.web.web.dto.request.MusicListRequest;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/music")
@Api(value = "音乐管理", tags = "音乐管理")
public class BlogsMusicController {
    @Autowired
    BlogsMusicProcess musicProcess;

    @ApiOperation(value = "动态列表查询")
    @PostMapping("/musicList")
    public RestResponse musicList(MusicListRequest request) {
        PageInfo<BlogsMusicListReply> pageInfo = musicProcess.queryMusicList(request);
        return new SuccessResponse().withData(pageInfo);
    }

    @ApiOperation("审核音乐")
    @PostMapping("/auditMusic")
    public RestResponse auditMusic(ApprovalRequest request) {
        GlobalResponseCode globalResponseCode = musicProcess.auditMusic(request);
        return globalResponseCode.equals(GlobalResponseCode.SUCCESS) ? new SuccessResponse() : new ErrorResponse(globalResponseCode);
    }

    @ApiOperation("新增音乐")
    @PostMapping("/addMusic")
    public RestResponse addMusic(AddMusicRequest addMusicRequest) {
        GlobalResponseCode globalResponseCode = musicProcess.addMusic(addMusicRequest);
        return globalResponseCode.equals(GlobalResponseCode.SUCCESS) ? new SuccessResponse() : new ErrorResponse(globalResponseCode);
    }

    @ApiOperation("删除音乐")
    @PostMapping("/delMusic")
    public RestResponse addMusic(long musicId) {
        GlobalResponseCode globalResponseCode = musicProcess.delMusic(musicId);
        return globalResponseCode.equals(GlobalResponseCode.SUCCESS) ? new SuccessResponse() : new ErrorResponse(globalResponseCode);
    }

    @ApiOperation("编辑音乐")
    @PostMapping("/editMusic")
    public RestResponse addMusic(EditMusicRequest request) {
        GlobalResponseCode globalResponseCode = musicProcess.editMusic(request);
        return globalResponseCode.equals(GlobalResponseCode.SUCCESS) ? new SuccessResponse() : new ErrorResponse(globalResponseCode);
    }
}
