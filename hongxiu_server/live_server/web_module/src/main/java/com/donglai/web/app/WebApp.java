package com.donglai.web.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@ComponentScan(value = {"com.donglai.common", "com.donglai.model", "com.donglai.web"})
@EnableMongoRepositories(basePackages = {"com.donglai.web.db.backoffice.repository", "com.donglai.model.db.repository"})
@PropertySources({@PropertySource("classpath:other.properties"), @PropertySource("classpath:blog.properties")})
public class WebApp {
    public static void main(String[] args) {
        SpringApplication.run(WebApp.class, args);
    }
}
