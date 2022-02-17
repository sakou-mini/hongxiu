package com.donglai.web.web.dto.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiParam;
import lombok.Data;

@Data
@ApiModel(value = "编辑音乐")
public class EditMusicRequest {
    @ApiParam(value = "音乐id", example = "1")
    public long musicId;
    @ApiParam(value = "音乐名", example = "十年")
    public String musicName;
    @ApiParam(value = "歌手", example = "张学友")
    public String musicAuthor;
}
