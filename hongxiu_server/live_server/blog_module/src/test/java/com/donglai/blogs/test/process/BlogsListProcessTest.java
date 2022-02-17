package com.donglai.blogs.test.process;

import com.donglai.blogs.process.BlogsListProcess;
import com.donglai.blogs.test.BaseTest;
import com.donglai.model.db.entity.common.User;
import com.donglai.model.db.service.common.UserService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class BlogsListProcessTest extends BaseTest {
    @Autowired
    BlogsListProcess blogsListProcess;
    @Autowired
    UserService userService;

    @Test
    public void test() {
        //List<Long> blogIds = blogsListProcess.randomGetUserBlogsList(user.getId());
        //System.out.println(blogIds);
        //blogIds = blogsListProcess.randomGetUserBlogsList(user.getId());
        //System.out.println(blogIds);
        //blogIds = blogsListProcess.randomGetUserBlogsList(user.getId());
        //System.out.println(blogIds);
        //blogIds = blogsListProcess.randomGetUserBlogsList(user.getId());
        //System.out.println(blogIds);
        //List<User> byNicknameLike = userService.findByNicknameLike("/lzh/");
        List<User> byNicknameLike = userService.findByNicknameLike(" ");
        System.out.println(byNicknameLike);
    }
}
