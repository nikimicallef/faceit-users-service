package com.faceit.interview;

import com.faceit.interview.configurations.UuidIdentifiedEntityEventListener;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

@SpringBootApplication
@EnableMongoAuditing
public class InterviewApplication {

	public static void main(String[] args) {
		SpringApplication.run(InterviewApplication.class, args);
	}

	@Bean
	public UuidIdentifiedEntityEventListener uuidIdentifiedEntityEventListener() {
		return new UuidIdentifiedEntityEventListener();
	}
}
