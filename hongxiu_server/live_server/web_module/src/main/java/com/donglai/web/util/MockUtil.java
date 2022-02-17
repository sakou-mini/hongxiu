package com.donglai.web.util;

import com.donglai.common.util.RandomUtil;
import com.donglai.model.db.entity.blogs.Blogs;
import com.donglai.model.db.entity.blogs.BlogsComment;
import com.donglai.model.db.entity.common.User;
import com.donglai.model.db.entity.live.FeedBack;
import com.donglai.model.db.entity.live.ReportComment;
import com.donglai.model.db.entity.live.ReportUser;
import com.donglai.model.db.entity.live.ReportVideo;
import com.donglai.model.db.service.blogs.BlogsCommentService;
import com.donglai.model.db.service.blogs.BlogsService;
import com.donglai.model.db.service.common.UserService;
import com.donglai.model.db.service.live.FeedBackService;
import com.donglai.model.db.service.live.ReportCommentService;
import com.donglai.model.db.service.live.ReportUserService;
import com.donglai.model.db.service.live.ReportVideoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static com.donglai.common.constant.SystemConstant.OFFICIAL_ACCOUNT;

@Component
@Slf4j
public class MockUtil {
    @Autowired
    UserService userService;
    @Autowired
    BlogsService blogsService;
    @Autowired
    ReportVideoService reportVideoService;
    @Autowired
    ReportCommentService reportCommentService;
    @Autowired
    BlogsCommentService blogsCommentService;
    @Autowired
    ReportUserService reportUserService;
    @Autowired
    FeedBackService feedBackService;

    public void mockReportVideo(){
        User user = userService.findByAccountId(OFFICIAL_ACCOUNT);
        long count = reportVideoService.count();
        if(count>0) return;
        log.info("模拟了举报的视频");
        List<Blogs> allPassBlogs = blogsService.findAllPassBlogs();
        List<ReportVideo> reportVideos = new ArrayList<>(allPassBlogs.size());
        ReportVideo reportVideo;
        for (Blogs blogs : allPassBlogs) {
            reportVideo = ReportVideo.newInstance(blogs.getId(), "我看着不爽，所以禁用了" + blogs.getId());
            reportVideo.setCreatedId(user.getId());
            reportVideos.add(reportVideo);
        }
        reportVideoService.saveAll(reportVideos);
    }

    //举报评论
    public void mockReportComment(){
        User user = userService.findByAccountId(OFFICIAL_ACCOUNT);
        long count = reportCommentService.count();
        if(count>0) return;
        log.info("模拟了举报的评论");
        List<Blogs> allPassBlogs = blogsService.findAllPassBlogs();
        List<ReportComment> reportComments = new ArrayList<>(allPassBlogs.size());
        BlogsComment comment;
        ReportComment reportComment;
        for (Blogs blogs : allPassBlogs) {
            comment = BlogsComment.createComment(blogs.getId(), "我是动态" + blogs.getId() + "的评论", user.getId());
            comment = blogsCommentService.save(comment);
            reportComment = ReportComment.newInstance("举报评论测试" + comment.getId(), comment.getId());
            reportComment.setCreatedId(user.getId());
            reportComments.add(reportComment);

        }
        reportCommentService.saveAll(reportComments);
    }

    //举报用户
    public void mockReportUser(){
        User reportUser = userService.findByAccountId(OFFICIAL_ACCOUNT);
        long count = reportUserService.count();
        if(count>0) return;
        log.info("模拟了举报的用户");
        List<User> allUser = userService.findAllUser();
        List<ReportUser> reportUsers = new ArrayList<>(allUser.size());
        ReportUser report;
        for (User temUser : allUser) {
            report = ReportUser.newInstance(temUser.getId(), "举报用户测试" + temUser.getId());
            report.setCreatedId(reportUser.getId());
            reportUsers.add(report);
        }
        reportUserService.saveAll(reportUsers);
    }

    //反馈
    public void mockFeedBack(){
        long count = feedBackService.count();
        if(count>0) return;
        log.info("模拟了举报的用户");
        List<User> allUser = userService.findAllUser();
        List<FeedBack> feedBacksList = new ArrayList<>(allUser.size());
        for (User user : allUser) {
            FeedBack feedBack = new FeedBack("反馈测试" + user.getId(), RandomUtil.getRandomInt(0, 1, null), user.getId(), "15215000187", "192.168.0.113", 0);
            feedBacksList.add(feedBack);
        }
        feedBackService.saveAll(feedBacksList);
    }
}
