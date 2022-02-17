package com.donglai.web.web.dto.reply;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiParam;
import lombok.Data;

@Data
@ApiModel(value = "音乐列表")
public class BlogsMusicListReply {
    @ApiParam(value = "音乐id")
    private long id;
    @ApiParam(value = "音乐名")
    private String musicName;
    @ApiParam(value = "音乐连接")
    private String musicUrl;
    @ApiParam(value = "音乐作者")
    private String musicAuthor;
    @ApiParam(value = "音乐封面")
    private String musicCoverUrl;
    @ApiParam(value = "音乐状态")
    private boolean status;
    @ApiParam(value = "创建时间")
    private long createTime;
    @ApiParam(value = "更新时间")
    private long updateTime;
    @ApiParam(value = "操作人昵称")
    private String operatorName;
    @ApiParam(value = "使用次数")
    private long usedCount;
}
