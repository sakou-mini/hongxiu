package com.donglai.live.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
@EnableMongoRepositories(basePackages = {"com.donglai.live.db.repository", "com.donglai.model.db.repository"})
@ComponentScan(value = {"com.donglai.common", "com.donglai.live", "com.donglai.model"})
@PropertySources({@PropertySource("classpath:other.properties")})
public class LiveLogicApp {
    public static void main(String[] args) {
        SpringApplication.run(LiveLogicApp.class, args);
    }
}
