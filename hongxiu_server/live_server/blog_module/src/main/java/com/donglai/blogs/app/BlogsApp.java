package com.donglai.blogs.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
@EnableMongoRepositories(basePackages = {"com.donglai.blogs.db.repository", "com.donglai.model.db.repository"})
@ComponentScan(value = "com.donglai.common")
@ComponentScan(value = "com.donglai.blogs")
@ComponentScan(value = "com.donglai.model")
@PropertySources({@PropertySource("classpath:blog.properties"), @PropertySource("classpath:other.properties")})
public class BlogsApp {
    public static void main(String[] args) {
        SpringApplication.run(BlogsApp.class, args);
    }
}
