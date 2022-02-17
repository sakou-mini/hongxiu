package com.donglai.web.web.dto.request;

import com.donglai.protocol.Constant;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiParam;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@ApiModel("动态请求请求实体")
public class CreateBlogsRequest {
    @ApiParam(value = "动态内容描述")
    private String content;
    @ApiParam(value = "动态资源列表")
    private List<String> resourceUrl = new ArrayList<>();
    @ApiParam(value = "动态缩略图列表")
    private List<String> thumbnails = new ArrayList<>();
    @ApiParam(value = "动态类型 BLOGS_IMAGE->图片    BLOGS_VIDEO->视频 BLOGS_AUDIO->音频 ", example = "BLOGS_IMAGE")
    private Constant.BlogsType blogsType;
}
