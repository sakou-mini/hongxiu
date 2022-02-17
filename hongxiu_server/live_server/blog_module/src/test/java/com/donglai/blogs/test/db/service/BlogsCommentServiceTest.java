package com.donglai.blogs.test.db.service;

import com.donglai.blogs.test.BaseTest;
import com.donglai.model.db.entity.blogs.BlogsComment;
import com.donglai.model.db.service.blogs.BlogsCommentService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class BlogsCommentServiceTest extends BaseTest {
    @Autowired
    BlogsCommentService blogsCommentService;

    @Test
    public void saveTest() {
        BlogsComment comment = BlogsComment.createComment(1, "txt", "fromUser");
        blogsCommentService.save(comment);
    }
}
