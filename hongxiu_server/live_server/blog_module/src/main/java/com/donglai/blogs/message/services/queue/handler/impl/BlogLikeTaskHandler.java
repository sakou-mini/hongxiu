package com.donglai.blogs.message.services.queue.handler.impl;

import com.donglai.blogs.message.services.queue.handler.TriggerHandler;
import com.donglai.blogs.process.QueueProcess;
import com.donglai.blogs.service.BlogsCommentCacheService;
import com.donglai.blogs.service.BlogsCommentLikeCacheService;
import com.donglai.blogs.service.BlogsLikeCacheService;
import com.donglai.model.db.entity.common.QueueExecute;
import com.donglai.model.db.service.common.QueueExecuteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class BlogLikeTaskHandler implements TriggerHandler {
    @Autowired
    BlogsLikeCacheService blogsLikeCacheService;
    @Autowired
    BlogsCommentLikeCacheService blogsCommentLikeCacheService;
    @Autowired
    BlogsCommentCacheService blogsCommentCacheService;

    @Autowired
    QueueProcess queueProcess;
    @Autowired
    QueueExecuteService queueExecuteService;

    @Override
    public void deal(QueueExecute queueExecute) {
        log.info("update blog like task:" + queueExecute);
        //同步动态点赞数
        blogsLikeCacheService.syncLikeDataToDatabase();
        //同步动态评论数
        blogsCommentCacheService.syncBlogsCommentDataToDatabase();
        //同步评论点赞数量
        blogsCommentLikeCacheService.syncLikeDataToDatabase();
        queueExecuteService.del(queueExecute);
        queueProcess.createUpdateBlogsLikeTaskQueue();
    }
}
