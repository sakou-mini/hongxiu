package com.donglai.queue.app;

import com.donglai.queue.process.QueueProcess;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class SetUp implements CommandLineRunner {

    @Override
    public void run(String... args) {
        // 初始化任務
        QueueProcess.init();
    }
}
