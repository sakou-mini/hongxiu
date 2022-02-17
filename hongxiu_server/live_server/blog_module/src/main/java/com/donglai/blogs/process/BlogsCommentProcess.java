package com.donglai.blogs.process;

import com.donglai.blogs.service.BlogsCommentCacheService;
import com.donglai.blogs.service.BlogsCommentLikeCacheService;
import com.donglai.model.util.WordFilterUtil;
import com.donglai.common.constant.UserPermissionSettingType;
import com.donglai.common.util.StringUtils;
import com.donglai.model.db.entity.blogs.Blogs;
import com.donglai.model.db.entity.blogs.BlogsComment;
import com.donglai.model.db.entity.blogs.BlogsCommentLike;
import com.donglai.model.db.entity.common.User;
import com.donglai.model.db.service.blogs.BlogsCommentLikeService;
import com.donglai.model.db.service.blogs.BlogsCommentService;
import com.donglai.model.service.UserPermissionService;
import com.donglai.protocol.Blog;
import com.donglai.protocol.Constant;
import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.donglai.protocol.Constant.ResultCode.*;

@Service
@Slf4j
public class BlogsCommentProcess {
    @Value("${blog.comment.content.max.length}")
    public int COMMENT_CONTENT_MAX_SIZE;
    final BlogsCommentCacheService blogsCommentCacheService;
    final BlogsCommentLikeCacheService blogsCommentLikeCacheService;
    final BlogsCommentLikeService blogsCommentLikeService;
    final BlogsCommentService blogsCommentService;
    final UserPermissionService userPermissionService;
    @Autowired
    WordFilterUtil wordFilterUtil;

    public BlogsCommentProcess(BlogsCommentCacheService blogsCommentCacheService, BlogsCommentLikeCacheService blogsCommentLikeCacheService,
                               BlogsCommentLikeService blogsCommentLikeService, BlogsCommentService blogsCommentService, UserPermissionService userPermissionService) {
        this.blogsCommentCacheService = blogsCommentCacheService;
        this.blogsCommentLikeCacheService = blogsCommentLikeCacheService;
        this.blogsCommentLikeService = blogsCommentLikeService;
        this.blogsCommentService = blogsCommentService;
        this.userPermissionService = userPermissionService;
    }

    public int getBlogsTotalCommentNum(long blogsId) {
        return blogsCommentCacheService.getBlogsCommentCount(blogsId);
    }

    public int getBlogsCommentTotalLikeNum(long blogsId) {
        return (int) blogsCommentCacheService.getBlogsComment(blogsId).stream().mapToLong(blogsCommentLikeCacheService::getLikeCount).sum();
    }

    public Blog.BlogsComment buildBlogsComment(BlogsComment blogsComment, String currentUser) {
        if (blogsComment == null) return null;
        var commentBuilder = blogsComment.toProto().toBuilder();
        long likeCount = blogsComment.getLikeNum();
        commentBuilder.setIsLike(blogsCommentLikeCacheService.hasLiked(currentUser, blogsComment.getId()))
                .setLikeNum(String.valueOf(likeCount));
        List<Long> commentReply = blogsCommentCacheService.getCommentReply(blogsComment.getId());
        //过滤审核通过的评论
        commentBuilder.addAllReplyCommentIds(commentReply.stream().filter(blogsCommentService::isPass).map(String::valueOf).collect(Collectors.toList()));
        return commentBuilder.build();
    }

    public Blog.BlogsComment buildBlogsComment(long commentId, String currentUser) {
        BlogsComment blogsComment = blogsCommentService.findById(commentId);
        return buildBlogsComment(blogsComment, currentUser);
    }

    public Blog.BlogsOfLikeCommentReply likeOrUnLikeComment(String userId, BlogsComment comment) {
        boolean hasLiked = blogsCommentLikeCacheService.hasLiked(userId, comment.getId());
        if (hasLiked) {
            blogsCommentLikeCacheService.unlike(userId, comment.getId());
            blogsCommentLikeService.deleteByCommentIdAndUserId(comment.getId(), userId);
        } else {
            blogsCommentLikeCacheService.like(userId, comment.getId());
            blogsCommentLikeService.save(BlogsCommentLike.newInstance(userId, comment.getId(), comment.getBlogsId()));
        }
        Integer likeCount = blogsCommentLikeCacheService.getLikeCount(comment.getId());
        return Blog.BlogsOfLikeCommentReply.newBuilder().setIsLike(!hasLiked).setLikeNum(String.valueOf(likeCount))
                .setCommentId(String.valueOf(comment.getId())).build();
    }


    public Constant.ResultCode verifyCommentRequest(User user, Blog.BlogsOfCommentBlogsRequest request, Blogs blogs) {
        String text = request.getText();
        if(user.isTourist())
            return UNOFFICIAL_USER;
        else if (Strings.isNullOrEmpty(text) || (!StringUtils.isNullOrBlank(request.getCommentId()) && StringUtils.isNullOrBlank(request.getReplyToUserId())))
            return MISSING_OR_ILLEGAL_PARAMETERS;
        else if (Objects.isNull(blogs))
            return BLOGS_NOT_EXIT;
        else if (text.length() > COMMENT_CONTENT_MAX_SIZE)
            return BLOGS_COMMENT_CONTENT_TOO_LONG;
        else if (!userPermissionService.permissionAllow(UserPermissionSettingType.PER_COMMENT, blogs.getUserId(), user.getId()))
            return NO_PERMISSION;
        else if (!Objects.equals(blogs.getBlogsStatus(),Constant.BlogsStatus.BLOGS_PASS)){
            return BLOGS_NOT_PASS;
        }
        return SUCCESS;
    }

    public Blog.BlogsComment commentOrReplyBlogs(String fromUserId, long blogsId, String text, String toUserId, Long parentCommentId) {
        long commentId;
        Constant.CommentStatus systemAuditStatus = wordFilterUtil.containSensitiveWord(text) ? Constant.CommentStatus.COMMENT_NO_PASS : Constant.CommentStatus.COMMENT_PASS;
        if (parentCommentId != null) {
            //回复评论
            if (Objects.isNull(blogsCommentService.findById(parentCommentId))) {
                log.warn(" Not Found ParentComment By Id {}", parentCommentId);
                return null;
            }
            BlogsComment commentReply = BlogsComment.createCommentReply(parentCommentId, blogsId, text, fromUserId, toUserId);
            commentReply.systemAudit(systemAuditStatus);
            commentReply = blogsCommentService.save(commentReply);
            commentId = commentReply.getId();
            if (Objects.equals(commentReply.getStatus(), Constant.CommentStatus.COMMENT_PASS))
                blogsCommentCacheService.replyComment(commentId, parentCommentId);
        } else {
            //评论博客
            BlogsComment comment = BlogsComment.createComment(blogsId, text, fromUserId);
            comment.systemAudit(systemAuditStatus);
            comment = blogsCommentService.save(comment);
            commentId = comment.getId();
            if (Objects.equals(comment.getStatus(), Constant.CommentStatus.COMMENT_PASS))
                blogsCommentCacheService.commentBlogs(comment.getId(), blogsId);
        }
        return buildBlogsComment(commentId, fromUserId);

    }
}
