package com.donglai.web.web.dto.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiParam;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel("博客列表请求")
public class BlogsOrCommentListRequest implements Serializable {
    @ApiParam(value = "发布人", example = "张三")
    private String publishName;
    @ApiParam(value = "用户id", example = "10001")
    private String userId;
    @ApiParam(value = "发布开始时间", example = "150000000")
    private Long startTime;
    @ApiParam(value = "发布结束时间", example = "150000000")
    private Long endTime;
    @ApiParam(value = "查询状态-> -1全部  0 未审核  1 审核通过  2 审核不通过 3 审核中", example = "2")
    private int status;
    @ApiParam(value = "机审结果，查询状态-> -1全部  0审核通过  1审核不通过", example = "-1")
    private int systemAuditStatus;
    @ApiParam(value = "当前分页,必须大于0", example = "1")
    private int page;
    @ApiParam(value = "分页大小", example = "2")
    private int size;
}
