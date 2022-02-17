package com.donglai.blogs.message.services;

import com.donglai.blogs.process.BlogsCommentProcess;
import com.donglai.blogs.service.BlogsCommentCacheService;
import com.donglai.common.dispatcher.TopicMessageServiceI;
import com.donglai.model.db.entity.blogs.Blogs;
import com.donglai.model.db.entity.blogs.BlogsComment;
import com.donglai.model.db.service.blogs.BlogsCommentService;
import com.donglai.model.db.service.blogs.BlogsService;
import com.donglai.protocol.Blog;
import com.donglai.protocol.Constant;
import com.donglai.protocol.HongXiu;
import com.donglai.protocol.message.KafkaMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.donglai.blogs.util.MessageUtil.buildReply;
import static com.donglai.protocol.Constant.ResultCode.BLOGS_NOT_EXIT;
import static com.donglai.protocol.Constant.ResultCode.SUCCESS;

@Service("BlogsOfQueryBlogsCommentRequest")
public class BlogsOfQueryBlogsCommentRequest_Service implements TopicMessageServiceI<String> {
    @Autowired
    BlogsCommentCacheService blogsCommentCacheService;
    @Autowired
    BlogsCommentProcess blogsCommentProcess;
    @Autowired
    BlogsService blogsService;
    @Autowired
    BlogsCommentService blogsCommentService;


    @Override
    public KafkaMessage.TopicMessage Process(String userId, HongXiu.HongXiuMessageRequest message, Object... param) {
        var request = message.getBlogsOfQueryBlogsCommentRequest();
        var replyBuilder = Blog.BlogsOfQueryBlogsCommentReply.newBuilder();
        var excludeReplyIds = request.getExcludeReplyIdsList().stream().map(Long::valueOf).collect(Collectors.toList());
        long blogsId = Long.parseLong(request.getBlogsId());
        Blogs blogs = blogsService.findById(blogsId);
        Constant.ResultCode resultCode;
        if (Objects.isNull(blogs)) {
            resultCode = BLOGS_NOT_EXIT;
        } else {
            resultCode = SUCCESS;
            List<Long> blogsComment = blogsCommentCacheService.getBlogsComment(blogsId);

            int totalCommentLikeNum = blogsCommentProcess.getBlogsCommentTotalLikeNum(blogsId);
            long totalCommentNum = blogsCommentProcess.getBlogsTotalCommentNum(blogsId);
            replyBuilder.setTotalCommentLikeNum(String.valueOf(totalCommentLikeNum)).setTotalCommentNum(String.valueOf(totalCommentNum)).setBlogsId(request.getBlogsId());
            //1.过滤掉已经查询过的评论id
            blogsComment.removeAll(excludeReplyIds);
            //2.筛选出玩家自己的和作者的评论置顶，并合并 （总排序 ：  我的评论-》作者评论-》其他评论）
            List<BlogsComment> userComment = blogsCommentService.findRootCommentByFromUser(userId, blogsId).stream().filter(comment -> !excludeReplyIds.contains(comment.getId())).collect(Collectors.toList());
            List<BlogsComment> totalCommentByList = new ArrayList<>(userComment);
            if (!Objects.equals(userId, blogs.getUserId())) {
                List<BlogsComment> authorComment = blogsCommentService.findRootCommentByFromUser(blogs.getUserId(), blogsId).stream()
                        .filter(comment -> !excludeReplyIds.contains(comment.getId())).collect(Collectors.toList());
                totalCommentByList.addAll(authorComment);
            }
            blogsComment.removeAll(totalCommentByList.stream().map(BlogsComment::getId).collect(Collectors.toList()));
            //4.剩余要评论数量
            int requestCommentSize = Math.min(request.getRows(), blogsComment.size());
            //4.过滤掉玩家和作者的评论后的评论
            if (requestCommentSize > 0) {
                List<BlogsComment> commonComment = blogsCommentService.findPassedPageOfCommentByList(blogsComment, PageRequest.of(0, requestCommentSize));
                totalCommentByList.addAll(commonComment);
            }
            if (!totalCommentByList.isEmpty()) {
                totalCommentByList.forEach(comment -> replyBuilder.addBlogsComments(blogsCommentProcess.buildBlogsComment(comment, userId)));
            }
        }
        return buildReply(userId, replyBuilder.build(), resultCode);
    }

    @Override
    public void Close(String s) {

    }

}
