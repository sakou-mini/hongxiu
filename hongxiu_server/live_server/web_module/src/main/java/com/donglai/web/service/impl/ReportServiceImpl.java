package com.donglai.web.service.impl;

import com.donglai.common.util.StringUtils;
import com.donglai.model.db.entity.blogs.Blogs;
import com.donglai.model.db.entity.blogs.BlogsComment;
import com.donglai.model.db.entity.common.User;
import com.donglai.model.db.entity.live.ReportComment;
import com.donglai.model.db.entity.live.ReportUser;
import com.donglai.model.db.entity.live.ReportVideo;
import com.donglai.model.db.service.blogs.BlogsCommentService;
import com.donglai.model.db.service.blogs.BlogsService;
import com.donglai.model.db.service.common.UserService;
import com.donglai.model.db.service.live.ReportCommentService;
import com.donglai.model.db.service.live.ReportUserService;
import com.donglai.model.db.service.live.ReportVideoService;
import com.donglai.web.db.server.service.ReportDbService;
import com.donglai.web.response.*;
import com.donglai.web.service.ReportService;
import com.donglai.web.util.ConvertUtils;
import com.donglai.web.web.dto.request.ReportFindListRequest;
import com.donglai.web.web.dto.request.ReportUpdateHandelRequest;
import com.donglai.web.web.vo.ReportCommentVO;
import com.donglai.web.web.vo.ReportUserVO;
import com.donglai.web.web.vo.ReportVideoVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * @author Moon
 * @date 2021-12-27 13:47
 */
@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    private ReportVideoService reportVideoService;
    @Autowired
    private ReportUserService reportUserService;
    @Autowired
    private ReportCommentService reportCommentService;
    @Autowired
    private UserService userService;
    @Autowired
    private BlogsService blogsService;
    @Autowired
    private BlogsCommentService commentsService;
    @Autowired
    private ReportDbService reportDbService;


    @Override
    public PageInfo<ReportCommentVO> findCommentList(ReportFindListRequest request) {
        PageInfo<ReportComment> reports = reportDbService.conditionGetComment(request);
        List<ReportCommentVO> list = ConvertUtils.recordCommentListToRecordCommentVOList(reports.getContent());
        for (ReportCommentVO reportCommentVO : list) {
            Long commentId = reportCommentVO.getCommentId();
            BlogsComment byId = commentsService.findById(commentId);

            User user = userService.findById(byId.getFromUser());

            reportCommentVO.setCreatedId(userService.findById(reportCommentVO.getCreatedId()).getNickname());
            reportCommentVO.setCommentAuthorId(user.getId());
            reportCommentVO.setCommentAuthorName(user.getNickname());
            reportCommentVO.setComment(byId.getText());

        }
        return new PageInfo<>(reports.getPageNum(), reports.getPageSize(), reports.getTotal(), list);
    }

    @Override
    public PageInfo<ReportVideoVO> findVideoList(ReportFindListRequest request) {
        PageInfo<ReportVideo> reports = reportDbService.conditionGetVideo(request);
        List<ReportVideoVO> list = ConvertUtils.recordVideoListToRecordVideoVOList(reports.getContent());

        for (ReportVideoVO reportVO : list) {
            Long blogId = reportVO.getBlogId();
            Blogs byId = blogsService.findById(blogId);
            User user = userService.findById(byId.getUserId());
            if(!StringUtils.isNullOrBlank(reportVO.getCreatedId())){
                Optional.ofNullable(userService.findById(reportVO.getCreatedId()))
                        .ifPresent(reportUser -> reportVO.setCreatedId(userService.findById(reportVO.getCreatedId()).getNickname()));
            }
            reportVO.setVideoAuthorId(byId.getUserId());
            reportVO.setVideoAuthorName(user.getNickname());
            reportVO.setVideoUrl(byId.getResourceUrl());
            reportVO.setVideoContent(byId.getContent());
        }

        return new PageInfo<>(reports.getPageNum(), reports.getPageSize(), reports.getTotal(), list);
    }

    @Override
    public PageInfo<ReportUserVO> findUserList(ReportFindListRequest request) {
        PageInfo<ReportUser> reports = reportDbService.conditionGetUser(request);
        List<ReportUserVO> list = ConvertUtils.recordUserListToRecordUserVOList(reports.getContent());

        for (ReportUserVO reportUserVO : list) {
            String userId = reportUserVO.getUserId();

            //被举报人信息
            User byId = userService.findById(userId);
            reportUserVO.setNickname(byId.getNickname());
            //举报人信息
            User byId1 = userService.findById(reportUserVO.getCreatedId());
            reportUserVO.setCreatedId(byId1.getNickname());
        }
        return new PageInfo<>(reports.getPageNum(), reports.getPageSize(), reports.getTotal(), list);
    }

    @Override
    public RestResponse updateUserHandel(ReportUpdateHandelRequest request) {
        if (Objects.isNull(request.getHandle())) {
            return new ErrorResponse(GlobalResponseCode.PARAM_ERROR);
        }
        List<ReportUser> users = reportUserService.findByIds(request.getIds());
        users.forEach(v -> v.setHandle(request.getHandle()));
        return new SuccessResponse().withData(reportUserService.saveAll(users));
    }


    @Override
    public RestResponse updateCommentHandel(ReportUpdateHandelRequest request) {
        if (Objects.isNull(request.getHandle())) {
            return new ErrorResponse(GlobalResponseCode.PARAM_ERROR);
        }
        List<ReportComment> comments = reportCommentService.findByIds(request.getIds());
        comments.forEach(v -> v.setHandle(request.getHandle()));
        return new SuccessResponse().withData(reportCommentService.saveAll(comments));
    }

    @Override
    public RestResponse updateVideoHandel(ReportUpdateHandelRequest request) {
        if (Objects.isNull(request.getHandle())) {
            return new ErrorResponse(GlobalResponseCode.PARAM_ERROR);
        }
        List<ReportVideo> videos = reportVideoService.findByIds(request.getIds());
        videos.forEach(v -> v.setHandle(request.getHandle()));
        return new SuccessResponse().withData(reportVideoService.saveAll(videos));
    }
}
