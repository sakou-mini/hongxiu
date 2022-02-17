package com.donglai.web.web.dto.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiParam;
import lombok.Data;

@Data
@ApiModel(value = "音乐列表查询")
public class MusicListRequest {
    @ApiParam(value = "歌曲名", example = "十年")
    private String musicName;
    @ApiParam(value = "歌手", example = "张学友")
    private String musicAuthor;
    @ApiParam(value = "发布开始时间", example = "150000000")
    private Long startTime;
    @ApiParam(value = "发布开始时间", example = "150000000")
    private Long endTime;
    @ApiParam(value = "查询状态-> -1全部  0 禁用  1 启用", example = "0")
    private int status;
    @ApiParam(value = "当前分页,必须大于0", example = "1")
    private int page;
    @ApiParam(value = "分页大小", example = "2")
    private int size;
}
