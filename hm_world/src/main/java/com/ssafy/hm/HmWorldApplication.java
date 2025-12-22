package com.ssafy.hm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class HmWorldApplication {

	public static void main(String[] args) {
		SpringApplication.run(HmWorldApplication.class, args);
	}

}
