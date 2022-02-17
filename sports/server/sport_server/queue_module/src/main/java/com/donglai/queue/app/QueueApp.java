package com.donglai.queue.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@ComponentScan(value = "com.donglai.common")
@ComponentScan(value = "com.donglai.queue")
@EnableTransactionManagement
@EnableMongoRepositories(basePackages ="com.donglai.queue.db.repository")
public class QueueApp {
    public static void main(String[] args) {
        SpringApplication.run(QueueApp.class, args);
    }
}
