package com.donglai.blogs.message.services;

import com.donglai.blogs.process.BlogsProcess;
import com.donglai.common.dispatcher.TopicMessageServiceI;
import com.donglai.model.db.entity.blogs.Blogs;
import com.donglai.model.db.entity.common.FollowList;
import com.donglai.model.db.entity.common.User;
import com.donglai.model.db.service.blogs.BlogsService;
import com.donglai.model.db.service.common.FollowListService;
import com.donglai.model.db.service.common.UserService;
import com.donglai.protocol.Blog;
import com.donglai.protocol.Common;
import com.donglai.protocol.Constant;
import com.donglai.protocol.HongXiu;
import com.donglai.protocol.message.KafkaMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

import static com.donglai.blogs.util.MessageUtil.buildReply;

@Service("BlogsOfAttentionBlogsRequest")
@Slf4j
public class BlogsOfAttentionBlogsRequest_Service implements TopicMessageServiceI<String> {
    @Autowired
    private FollowListService followListService;
    @Autowired
    private BlogsService blogsService;
    @Autowired
    private UserService userService;
    @Autowired
    private BlogsProcess blogsProcess;

    @Override
    public KafkaMessage.TopicMessage Process(String userId, HongXiu.HongXiuMessageRequest message, Object... param) {
        var request = message.getBlogsOfAttentionBlogsRequest();
        var replyBuilder = Blog.BlogsOfAttentionBlogsReply.newBuilder();
        //查询用户的所有关注
        List<FollowList> followLists = followListService.findUserLeaders(userId);
        List<String> leaderIds = followLists.stream().map(FollowList::getLeaderId).collect(Collectors.toList());
        List<Blog.RecommendAuthor> recommendAuthors = new ArrayList<>();
        //判断是否没有关注或者关注视频总和为0
        if (CollectionUtils.isEmpty(followLists) || CollectionUtils.isEmpty(blogsService.findByBlogsStatusAndUserIdInOrderByCreateAtDesc(
                Constant.BlogsStatus.BLOGS_PASS, followLists.stream().map(FollowList::getLeaderId).collect(Collectors.toList()))) ||
                Objects.equals(request.getQueryType(), Constant.AttentionQueryType.ATTENTION_RECOMMEND_USER)) {
            //组装推荐关注
            //视频大于2的博主Id
            leaderIds.addAll(request.getUserIdsList());
            List<String> filterUserIds = blogsService.recommendBlogsUser(leaderIds, 2);
            filterUserIds = filterUserIds.subList(0, Math.min(filterUserIds.size(), 20));
            //本该查询的user
            List<User> recommendUser = userService.findByIds(filterUserIds);
            Collections.shuffle(recommendUser);
            //组装返回数据
            for (User user : recommendUser) {
                //推荐作者 对象
                Blog.RecommendAuthor.Builder builder = Blog.RecommendAuthor.newBuilder();
                //作者信息
                Common.UserInfo userInfo = user.toDetailProto();
                List<Blogs> maxLikeNumVideo = blogsService.getUserBlogsOrderByLikeNum(user.getId(), 1, 50);
                List<String> recommendBlogsIdList = maxLikeNumVideo.stream().map(Blogs::getStringId).collect(Collectors.toList());
                builder.setUserInfo(userInfo);
                //随机一个用户的动态
                Blogs blogs = blogsProcess.randomUserBlogs(user.getId());
                Optional.ofNullable(blogs).ifPresent(blogsTmp -> builder.setBackgroundResourceUrl(blogsTmp.getResourceUrl().get(0)));
                builder.addAllBlogsIds(recommendBlogsIdList);
                //添加到集合
                recommendAuthors.add(builder.build());
            }
            replyBuilder.addAllRecommendAuthor(recommendAuthors);
        } else {
            Set<String> leaderId = followLists.stream().map(FollowList::getLeaderId).collect(Collectors.toSet());
            List<String> blogsIds = blogsService.findBlogsByUserIdsAndSort(leaderId, request.getSort()).stream().map(Blogs::getStringId).collect(Collectors.toList());
            replyBuilder.addAllBlogsIds(blogsIds);
        }
        return buildReply(userId, replyBuilder.build(), Constant.ResultCode.SUCCESS);
    }

    @Override
    public void Close(String s) {

    }

    public static void main(String[] args) {
        List<Integer> redGroup = new ArrayList<>();
        for (int i = 1; i <= 32; i++) {
            redGroup.add(i);
        }
        Collections.shuffle(redGroup);

        List<Integer> blueGroup = new ArrayList<>();
        for (int i = 1; i <= 16; i++) {
            blueGroup.add(i);
        }
        Collections.shuffle(blueGroup);
        System.out.println(redGroup.subList(0, 6).toString() + "+" + blueGroup.subList(0, 2).toString());
    }
}
