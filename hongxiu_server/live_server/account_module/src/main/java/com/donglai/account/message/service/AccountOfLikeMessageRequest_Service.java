package com.donglai.account.message.service;

import com.donglai.common.dispatcher.TopicMessageServiceI;
import com.donglai.model.db.entity.blogs.Blogs;
import com.donglai.model.db.entity.blogs.BlogsLike;
import com.donglai.model.db.entity.common.User;
import com.donglai.model.db.service.blogs.BlogsLikeService;
import com.donglai.model.db.service.blogs.BlogsService;
import com.donglai.model.db.service.common.UserService;
import com.donglai.protocol.Account;
import com.donglai.protocol.Common;
import com.donglai.protocol.HongXiu;
import com.donglai.protocol.message.KafkaMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.donglai.account.util.MessageUtil.buildReply;
import static com.donglai.protocol.Constant.ResultCode.SUCCESS;

/**
 * @author Moon
 * @date 2022-01-07 11:28
 */
@Service("AccountOfLikeMessageRequest")
public class AccountOfLikeMessageRequest_Service implements TopicMessageServiceI<String> {

    @Autowired
    private UserService userService;
    @Autowired
    private BlogsService blogsService;
    @Autowired
    private BlogsLikeService blogLikeService;

    @Override
    public KafkaMessage.TopicMessage Process(String userId, HongXiu.HongXiuMessageRequest message, Object... param) {
        Account.AccountOfLikeMessageReply.Builder builder = Account.AccountOfLikeMessageReply.newBuilder();

        List<BlogsLike> likeBlogsByUserId = blogLikeService.findLikeBlogsByBlogsAuthor(userId);

        for (BlogsLike blogsLike : likeBlogsByUserId) {
            Account.InteractiveInfo.Builder interactive = Account.InteractiveInfo.newBuilder();
            long blogsId = blogsLike.getBlogsId();
            String likeUserId = blogsLike.getUserId();
            User byId = userService.findById(likeUserId);
            Common.UserInfo userInfo = byId.toSummaryProto();
            Blogs likeBlogs = blogsService.findById(blogsId);


            interactive.setBlogsCover(likeBlogs.getThumbnails().get(0));
            interactive.setBlogsId(String.valueOf(likeBlogs.getId()));
            interactive.setUserInfo(userInfo);
            interactive.setCreatedTime(blogsLike.getTime());

            builder.addInteractiveInfos(interactive);
        }


        return buildReply(userId, builder, SUCCESS);

    }

    @Override
    public void Close(String s) {

    }
}
