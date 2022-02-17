package com.donglai.test;

import com.donglai.test.netty.HttpClientNetty;
import com.donglai.test.netty.WebSocketClientHandler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(value = "com.donglai.common")
@ComponentScan(value = "com.donglai.test")
public class TestApp {
    public static void main(String[] args) {
        SpringApplication.run(TestApp.class, args);
        HttpClientNetty httpClientNetty = new HttpClientNetty("127.0.0.1", 7005);
        WebSocketClientHandler webSocketClientHandler = new WebSocketClientHandler();
    }
}
