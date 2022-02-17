package com.donglai.blogs.process;

import com.donglai.model.db.entity.blogs.Blogs;
import com.donglai.model.db.entity.common.User;
import com.donglai.model.db.service.blogs.BlogsLikeService;
import com.donglai.model.db.service.blogs.BlogsService;
import com.donglai.model.db.service.common.UserService;
import com.donglai.model.service.impl.es.BlogsElasticsearchServiceImpl;
import com.donglai.model.service.impl.es.UserElasticsearchServiceImpl;
import com.donglai.model.util.ComparatorUtil;
import com.donglai.protocol.Blog;
import com.donglai.protocol.Common;
import com.donglai.protocol.Constant;
import com.google.common.collect.Sets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class BlogsSearchProcess {
    @Autowired
    UserService userService;
    @Autowired
    BlogsLikeService blogsLikeService;
    @Autowired
    BlogsService blogsService;

    @Autowired
    BlogsElasticsearchServiceImpl blogsElasticsearchService;
    @Autowired
    UserElasticsearchServiceImpl userElasticsearchService;

    public Blog.UserBlogsInfo buildUserBlogsInfo(User user) {
        Common.UserInfo userInfo = user.toDetailProto();
        //获赞量
        long likeNum = blogsLikeService.countBlogsLikeByAuthor(user.getId());
        //视频量
        long blogsNum = blogsService.countUserBlogsNumByBlogStatus(user.getId(), Constant.BlogsStatus.BLOGS_PASS);
        return Blog.UserBlogsInfo.newBuilder().setUserInfo(userInfo).setLikeNum(likeNum).setBlogsNum(blogsNum).build();
    }

    public List<Blogs> findAllBlogsByKeyWords(String keyword, Constant.SortType sort) {
        Map<String, String> queryParam = new HashMap<>();
        queryParam.put("nickname", keyword);
        //关键字相关的用户
        List<User> users = userElasticsearchService.searchFuzzyQuery(queryParam);
        //Set<User> users = findAllBlogsUserByKeyWords(keyword);
        Set<Blogs> blogs = new HashSet<>();
        //1.关键字玩家相关的所有动态
        for (User user : users) {
            blogs.addAll( blogsService.findUserPassedBlogs(user.getId()));
        }
        //2.动态描述相关动态
        queryParam.clear();
        queryParam.put("content", keyword);
        blogs.addAll(blogsElasticsearchService.searchFuzzyQuery(queryParam));
        return blogs.stream().filter(blog -> Objects.equals(blog.getBlogsStatus(), Constant.BlogsStatus.BLOGS_PASS)).sorted(ComparatorUtil.getBlogsComparatorBySortType(sort)).collect(Collectors.toList());
    }

    //TODO
    public Set<User> findAllBlogsUserByKeyWords(String keyword) {
        //数据库模糊查询
        return Sets.newHashSet(userService.findByNicknameLike(keyword));
    }
}
