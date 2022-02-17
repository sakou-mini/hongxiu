package com.donglai.statistics.app;

import com.donglai.statistics.process.QueueProcess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class SetUp implements CommandLineRunner {
    @Autowired
    QueueProcess queueProcess;

    @Override
    public void run(String... args) {
        queueProcess.initDailyTask();
        queueProcess.initMinuteTask();
    }

}
