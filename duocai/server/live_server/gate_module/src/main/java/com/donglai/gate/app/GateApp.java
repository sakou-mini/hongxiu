package com.donglai.gate.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@ComponentScan(value = "com.donglai.common")
@ComponentScan(value = "com.donglai.gate")
@EnableAsync
public class GateApp {
	public static void main(String[] args) {
		SpringApplication.run(GateApp.class, args);
	}
}
