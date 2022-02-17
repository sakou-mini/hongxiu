package com.donglai.netty;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(value = "com.donglai.common")
@ComponentScan(value = "com.donglai.netty")
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
        //new NettyHttpServer().bind(null, 8565);
    }
}
