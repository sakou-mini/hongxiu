package com.donglaistd.jinli.task;

import com.donglaistd.jinli.service.ServerAvailabilityCheckService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

@Component
@EnableAsync
public class SecondJob {

    Logger logger = Logger.getLogger(SecondJob.class.getName());

    @Autowired
    ServerAvailabilityCheckService serverAvailabilityCheckService;
    @Async
    @Scheduled(cron = "${data.task.cron.secondJob}")
    public void secondJob() {
        serverAvailabilityCheckService.startCheckServer();
    }
}
