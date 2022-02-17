package com.donglai.web.web.dto.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiParam;
import lombok.Data;

@Data
@ApiModel(value = "添加音乐")
public class AddMusicRequest {
    @ApiParam(value = "音乐名", example = "十年")
    public String musicName;
    @ApiParam(value = "歌手", example = "张学友")
    public String musicAuthor;
    @ApiParam(value = "音乐链接", example = "xxx.mp3")
    public String musicUrl;
    @ApiParam(value = "音乐封面", example = "xxx.jpg")
    public String musicCoverUrl;
    @ApiParam(value = "歌曲时长（秒为单位）", example = "120")
    public Integer duration;
}
