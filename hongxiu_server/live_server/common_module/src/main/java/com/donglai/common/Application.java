package com.donglai.common;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

@SpringBootApplication
@PropertySources({@PropertySource("classpath:live.properties"), @PropertySource("classpath:tupu.properties")})
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
