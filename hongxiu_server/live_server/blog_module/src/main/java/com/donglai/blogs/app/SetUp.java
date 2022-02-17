package com.donglai.blogs.app;

import com.donglai.blogs.process.QueueProcess;
import com.donglai.blogs.util.MockUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class SetUp implements CommandLineRunner {
    @Value("${mock.blogs}")
    private boolean mockData;

    @Autowired
    QueueProcess queueProcess;
    @Autowired
    MockUtil mockUtil;

    @Override
    public void run(String... args) throws Exception {
        if (mockData) mockUtil.mockBlogsData();
        mockUtil.mockBanner();
        initQueueTask();
    }

    public void initQueueTask() {
        //queueProcess.createReviewBlogsQueue();
        queueProcess.createUpdateBlogsLikeTaskQueue();
    }
}
