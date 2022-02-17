package com.donglai.web.web.controller.server;

import com.donglai.protocol.Constant;
import com.donglai.web.process.BlogsProcess;
import com.donglai.web.response.*;
import com.donglai.web.util.WebRequestVerifyUtil;
import com.donglai.web.web.dto.reply.BlogsCommentListReply;
import com.donglai.web.web.dto.reply.BlogsDetailReply;
import com.donglai.web.web.dto.reply.BlogsListReply;
import com.donglai.web.web.dto.request.ApprovalRequest;
import com.donglai.web.web.dto.request.BlogsOrCommentListRequest;
import com.donglai.web.web.dto.request.CreateBlogsRequest;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;
import java.util.Set;

@RestController
@Api(value = "动态管理", tags = "动态管理")
@RequestMapping("/api/v1/blogs")
public class BlogsController {
    @Autowired
    BlogsProcess blogsProcess;

    //动态管理
    @ApiOperation(value = "添加动态")
    @PostMapping(value = "/addBlogs")
    public RestResponse createBlogs(CreateBlogsRequest createBlogsRequest) {
        Constant.ResultCode resultCode = blogsProcess.createBlogs(createBlogsRequest);
        return Objects.equals(Constant.ResultCode.SUCCESS, resultCode) ? new SuccessResponse() : new ErrorResponse(GlobalResponseCode.CREATE_BLOGS_FAILED);
    }

    @ApiOperation(value = "动态列表查询")
    @PostMapping(value = "/blogsList")
    public RestResponse blogsList(BlogsOrCommentListRequest request) {
        if (!WebRequestVerifyUtil.verifyPageRequest(request.getPage(), request.getSize())) {
            return new ErrorResponse(GlobalResponseCode.PAGE_REQUEST_PARAM_ERROR);
        } else {
            PageInfo<BlogsListReply> blogsPageInfo = blogsProcess.queryBlogsByBlogsListRequest(request);
            return new SuccessResponse().withData(blogsPageInfo);
        }
    }

    @ApiOperation(value = "审核动态")
    @PostMapping(value = "/approvalBlogs")
    public RestResponse approvalBlogs(ApprovalRequest request) {
        GlobalResponseCode globalResponseCode = blogsProcess.manualApprovalBlogs(request);
        return globalResponseCode.equals(GlobalResponseCode.SUCCESS) ? new SuccessResponse() : new ErrorResponse(globalResponseCode);
    }

    @ApiOperation(value = "下架动态")
    @PostMapping(value = "/delBlogs")
    public RestResponse delBlogs(Long blogsId) {
        return new SuccessResponse().withData(blogsProcess.delBlogs(blogsId));
    }

    @ApiOperation(value = "动态详情")
    @GetMapping(value = "/blogsDetail")
    public RestResponse blogsDetail(@ApiParam(value = "动态id", example = "1") long blogsId) {
        BlogsDetailReply blogsDetailReply = blogsProcess.blogsDetail(blogsId);
        return new SuccessResponse().withData(blogsDetailReply);
    }

    @ApiOperation(value = "修改动态描述")
    @PostMapping(value = "/editBlogs")
    public RestResponse editBlogs(@ApiParam(value = "动态id", example = "1") long blogsId, String content) {
        GlobalResponseCode globalResponseCode = blogsProcess.modifyBlog(blogsId, content);
        return globalResponseCode.equals(GlobalResponseCode.SUCCESS) ? new SuccessResponse() : new ErrorResponse(globalResponseCode);
    }

    //评论管理
    @ApiOperation(value = "评论列表查询")
    @PostMapping(value = "/commentList")
    public RestResponse commentList(BlogsOrCommentListRequest request) {
        if (!WebRequestVerifyUtil.verifyPageRequest(request.getPage(), request.getSize())) {
            return new ErrorResponse(GlobalResponseCode.PAGE_REQUEST_PARAM_ERROR);
        } else {
            PageInfo<BlogsCommentListReply> commentList = blogsProcess.queryCommentList(request);
            return new SuccessResponse().withData(commentList);
        }
    }

    @ApiOperation(value = "审核评论")
    @PostMapping(value = "/approvalComment")
    public RestResponse approvalComment(ApprovalRequest request) {
        GlobalResponseCode globalResponseCode = blogsProcess.manualApprovalComment(request);
        return globalResponseCode.equals(GlobalResponseCode.SUCCESS) ? new SuccessResponse() : new ErrorResponse(globalResponseCode);
    }


    @ApiOperation(value = "修改视频标签")
    @PostMapping("/updateBlogsLabels")
    public RestResponse updateBlogsLabels(Long blogsId, Set<Integer> labels) {
        return blogsProcess.updateBlogsLabels(blogsId, labels);
    }

    @ApiOperation(value = "查询视频标签")
    @PostMapping("/findBlogsLabels")
    public RestResponse findBlogsLabels(Long blogsId) {
        return new SuccessResponse().withData(blogsProcess.findBlogsLabels(blogsId));
    }
}
