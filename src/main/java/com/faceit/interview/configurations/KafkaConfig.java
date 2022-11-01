package com.faceit.interview.configurations;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Required to auto create the topic
 */
@Configuration
public class KafkaConfig {

	public static final String TOPIC_NAME = "users-topic";

	@Bean
	public NewTopic usersTopic() {
		return new NewTopic(TOPIC_NAME, 1, (short) 1);
	}
}
