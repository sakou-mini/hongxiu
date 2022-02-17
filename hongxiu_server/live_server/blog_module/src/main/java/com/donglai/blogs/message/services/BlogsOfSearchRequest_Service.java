package com.donglai.blogs.message.services;

import com.donglai.blogs.process.BlogsSearchProcess;
import com.donglai.common.dispatcher.TopicMessageServiceI;
import com.donglai.common.util.StringUtils;
import com.donglai.model.db.entity.blogs.Blogs;
import com.donglai.protocol.Blog;
import com.donglai.protocol.HongXiu;
import com.donglai.protocol.message.KafkaMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.donglai.blogs.util.MessageUtil.buildReply;
import static com.donglai.protocol.Constant.ResultCode.SUCCESS;
import static com.donglai.protocol.Constant.SearchType.SEARCH_BLOGS;
import static com.donglai.protocol.Constant.SearchType.SEARCH_USER;

/**
 * @author Moon
 * @date 2021-11-22 14:41
 */
@Service("BlogsOfSearchRequest")
public class BlogsOfSearchRequest_Service implements TopicMessageServiceI<String> {
    @Autowired
    BlogsSearchProcess blogsSearchProcess;

    @Override
    public KafkaMessage.TopicMessage Process(String userId, HongXiu.HongXiuMessageRequest message, Object... param) {
        var request = message.getBlogsOfSearchRequest();
        var replyBuilder = Blog.BlogsOfSearchReply.newBuilder();
        //关键字
        String keyword = StringUtils.isNullOrBlank(request.getKeyword())  ? "" : request.getKeyword().replaceAll(" ", "");
        //参数校验
        if (StringUtils.isNullOrBlank(keyword)) {
            return buildReply(userId, replyBuilder, SUCCESS);
        }
        if (Objects.equals(SEARCH_BLOGS, request.getSearchType())) {
            List<String> blogsId = blogsSearchProcess.findAllBlogsByKeyWords(keyword, request.getSort()).stream().map(Blogs::getStringId).collect(Collectors.toList());
            replyBuilder.addAllBlogsIds(blogsId);
        } else if (Objects.equals(SEARCH_USER, request.getSearchType())) {
            List<Blog.UserBlogsInfo> userBlogsInfos = blogsSearchProcess.findAllBlogsUserByKeyWords(keyword).stream().map(user -> blogsSearchProcess.buildUserBlogsInfo(user))
                    .collect(Collectors.toList());
            replyBuilder.addAllUserBlogsInfo(userBlogsInfos);
        }
        replyBuilder.setKeyword(request.getKeyword()).setSearchType(request.getSearchType());
        return buildReply(userId, replyBuilder, SUCCESS);
    }

    @Override
    public void Close(String s) {

    }
}
