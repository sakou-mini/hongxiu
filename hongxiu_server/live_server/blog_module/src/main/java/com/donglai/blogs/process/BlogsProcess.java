package com.donglai.blogs.process;

import com.donglai.blogs.message.producer.Producer;
import com.donglai.blogs.service.BlogsCommentCacheService;
import com.donglai.blogs.service.BlogsCommentLikeCacheService;
import com.donglai.blogs.service.BlogsLikeCacheService;
import com.donglai.blogs.service.UserBlogsLikeRankCacheService;
import com.donglai.common.util.RandomUtil;
import com.donglai.common.util.StringUtils;
import com.donglai.model.db.entity.blogs.*;
import com.donglai.model.db.entity.common.User;
import com.donglai.model.db.service.blogs.*;
import com.donglai.model.db.service.common.FollowListService;
import com.donglai.model.db.service.common.UserService;
import com.donglai.model.db.service.common.statistics.UserOperationRecordService;
import com.donglai.protocol.Blog;
import com.donglai.protocol.Constant;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Objects;

import static com.donglai.blogs.constant.Constant.ADD_NUMBER;
import static com.donglai.protocol.Constant.BlogsType.BLOGS_IMAGE;
import static com.donglai.protocol.Constant.ResultCode.*;

@Component
@Slf4j
public class BlogsProcess {
    @Value("${blog.image.max.num}")
    private int imageBlogMaxNum;
    @Value("${blog.video.max.num}")
    private int videoBlogMaxNum;
    @Value("${blog.content.max.length}")
    private int blogContentMaxLength;
    @Value("${blog.max.publish.num}")
    private int blogPublishMaxNum;

    @Autowired
    Producer producer;
    @Autowired
    BlogsService blogsService;
    @Autowired
    FollowListService followListService;
    @Autowired
    UserService userService;
    @Autowired
    BlogsLikeService blogsLikeService;
    @Autowired
    BlogsLikeCacheService blogsLikeCacheService;
    @Autowired
    UserBlogsLikeRankCacheService userBlogsLikeRankCacheService;
    @Autowired
    BlogsCommentCacheService blogsCommentCacheService;
    @Autowired
    BlogsCommentLikeCacheService blogsCommentLikeCacheService;
    @Autowired
    UserOperationRecordService userOperationRecordService;
    @Autowired
    BlogsCommentService blogsCommentService;
    @Autowired
    BlogsCommentLikeService blogsCommentLikeService;
    @Autowired
    BlogsInteractiveService blogsInteractiveService;
    @Autowired
    BlogsMusicService blogsMusicService;

    public boolean verifyTargetResourceNum(int resourceNum, Constant.BlogsType type) {
        if (resourceNum <= 0) {
            log.warn("blogs resource is empty");
            return false;
        }
        if (Objects.equals(BLOGS_IMAGE, type)) return resourceNum <= imageBlogMaxNum;
        else return resourceNum <= videoBlogMaxNum;
    }

    public Constant.ResultCode verifyCreateBlogs(String userId, Blog.BlogsOfCreateBlogsRequest request) {
        if (request.getContent().length() > blogContentMaxLength) {
            log.warn("blogs_content_empty_or_too_long");
            return BLOGS_CONTENT_EMPTY_OR_TOO_LONG;
        } else if (Objects.equals(Constant.BlogsType.BLOGS_VIDEO, request.getType()) && request.getThumbnailCount() < 1) {
            log.warn("blogs_thumbnail_empty");
            return BLOGS_THUMBNAIL_EMPTY;
        } else if (!verifyTargetResourceNum(request.getResourceUrlCount(), request.getType())) {
            log.warn("blogs_resource_over_limit");
            return BLOGS_RESOURCE_OVER_LIMIT;
        } else if (blogsService.countUserBlogs(userId) >= blogPublishMaxNum) {
            return BLOGS_PUBLISH_OVERLIMIT;
        } else if (!StringUtils.isNullOrBlank(request.getMusicId())) {
            BlogsMusic music = blogsMusicService.findById(Long.parseLong(request.getMusicId()));
            if (Objects.isNull(music)) return MUSIC_NOT_EXIT;
            else if (!music.isStatus()) return MUSIC_DISABLED;
        }
        return SUCCESS;
    }

    public Blog.BlogsDetail queryBlogInfoDetail(long blogsId, String requestUser) {
        Blog.BlogsDetail.Builder blogsBuilder = Blog.BlogsDetail.newBuilder();
        Blogs blogs = blogsService.findById(blogsId);
        if (Objects.isNull(blogs)) {
            log.warn("not found blogs by id:{}", blogsId);
            return blogsBuilder.build();
        } else {
            var blogsInfo = blogs.toProto().toBuilder();
            var isFollower = followListService.isUserFollower(blogs.getUserId(), requestUser);
            var userInfo = userService.findById(blogs.getUserId()).toDetailProto();
            var isLike = blogsLikeCacheService.hasLiked(requestUser, blogsId);
            var likeNum = blogsLikeCacheService.getLikeCount(blogsId);
            var blogsCommentNum = blogsCommentService.countBlogsAllCommentNum(blogsId);
            blogsInfo.setLikeNum(String.valueOf(likeNum)).setCommentNum(String.valueOf(blogsCommentNum));
            blogsBuilder.setBlogsInfo(blogsInfo).setIsFollow(isFollower).setUserInfo(userInfo).setIsLike(isLike);
            return blogsBuilder.build();
        }
    }

    public Blog.BlogsOfLikeBlogsReply likeOrUnlikeBlogs(String userId, Blogs blogs) {
        boolean hasLiked = blogsLikeCacheService.hasLiked(userId, blogs.getId());
        User byId = userService.findById(userId);
        if (hasLiked) {
            log.info("user {} cancel like blogs {}", userId, blogs.getId());
            userBlogsLikeRankCacheService.updateUserBlogsLikeNum(userId, -ADD_NUMBER);
            blogsLikeService.deleteByUserIdAndBlogsId(userId, blogs.getId());
            blogsLikeCacheService.unlike(userId, blogs.getId());
        } else {
            log.info("user {} like blogs {}", userId, blogs.getId());
            userBlogsLikeRankCacheService.updateUserBlogsLikeNum(userId, ADD_NUMBER);
            blogsLikeCacheService.like(userId, blogs.getId());
            BlogsLike blogsLike = BlogsLike.newInstance(userId, blogs.getId(), blogs.getUserId());
            blogsLike.setTourist(byId.isTourist());
            blogsLikeService.save(blogsLike);
            //通知
            User to = userService.findById(blogs.getUserId());
            if (to.isOnline()) {
                Blog.BlogsOfInteractiveBroadcastMessage.Builder builder = Blog.BlogsOfInteractiveBroadcastMessage.newBuilder();
                Blog.Interactive.Builder interactive = Blog.Interactive.newBuilder()
                        .setBlogsId(String.valueOf(blogs.getId())).setFromUserId(byId.getId())
                        .setCreateTime(String.valueOf(System.currentTimeMillis())).setInteractive(Constant.InteractiveType.LIKE);
                builder.setInteractive(interactive);
                producer.broadCastReplyMessage(Sets.newHashSet(blogs.getUserId()), builder.build(), SUCCESS);
            } else {
                BlogsInteractive blogsInteractive = BlogsInteractive.newInstance(blogs.getId(),
                        System.currentTimeMillis(), userId, blogs.getUserId(), null, Constant.InteractiveType.LIKE);
                blogsInteractiveService.save(blogsInteractive);
            }


        }
        return Blog.BlogsOfLikeBlogsReply.newBuilder().setIsLike(!hasLiked)
                .setLikeNum(String.valueOf(blogsLikeCacheService.getLikeCount(blogs.getId())))
                .setBlogsId(String.valueOf(blogs.getId())).build();
    }

    public void recordPublishBlogs(String userId, Blogs blogs) {
        var operationRecord = userOperationRecordService.findByUserId(userId);
        operationRecord.setLastPublishBlogsTime(blogs.getCreateAt());
        userOperationRecordService.save(operationRecord);
    }

    public Blogs randomUserBlogs(String userId) {
        List<Blogs> blogsList = blogsService.findByUserIdInAndBlogsStatusIsOrderByCreateAt(Sets.newHashSet(userId), Constant.BlogsStatus.BLOGS_PASS);
        if (CollectionUtils.isEmpty(blogsList)) return null;
        return blogsList.get(RandomUtil.getRandomInt(0, blogsList.size() - 1, null));
    }

    /*真正删除*/
    @Transactional
    public void deleteBlogs(String blogsUserId, long blogId) {
        //1.clean blogs like
        List<BlogsLike> blogsLikes = blogsLikeService.deleteAllBlogsLikeByBlogsId(blogId);
        blogsLikeCacheService.deletePostLikeSet(blogId);
        //2.更新玩家动态点赞排行榜
        userBlogsLikeRankCacheService.updateUserBlogsLikeNum(blogsUserId, (long) -blogsLikes.size());

        //3.clean blogs comment
        List<BlogsComment> blogsComments = blogsCommentService.deleteBlogsAllComment(blogId);
        //删除 评论的点赞记录
        blogsCommentLikeService.deleteByBlogsId(blogId);
        for (BlogsComment blogsComment : blogsComments) {
            //清除 评论的点赞
            blogsCommentLikeCacheService
                    .deletePostLikeSet(blogsComment.getId());
            //清除评论的 回复
            if (Objects.nonNull(blogsComment.getParentCommentId()))
                blogsCommentCacheService.deleteBlogsComment(blogId, blogsComment.getParentCommentId());
        }
        //删除动态
        blogsService.deleteById(blogId);
    }

    /*逻辑删除*/
    public void removeBlogs(long blogId){
        Blogs blogs = blogsService.findById(blogId);
        blogs.setBlogsStatus(Constant.BlogsStatus.BLOG_DELETE);
    }
}
