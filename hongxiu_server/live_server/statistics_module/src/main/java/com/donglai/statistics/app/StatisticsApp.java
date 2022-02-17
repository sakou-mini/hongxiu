package com.donglai.statistics.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@ComponentScan(value = {"com.donglai.common", "com.donglai.model", "com.donglai.statistics"})
@EnableTransactionManagement
@EnableMongoRepositories(basePackages = {"com.donglai.model.db.repository"})
@PropertySources({@PropertySource("classpath:other.properties")})
public class StatisticsApp {

    public static void main(String[] args) {
        SpringApplication.run(StatisticsApp.class, args);
    }
}
