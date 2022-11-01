package com.faceit.interview.services;

import com.faceit.interview.configurations.KafkaConfig;
import com.faceit.interview.messages.UserMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

@Service
public class MessagingService {

	private static final Logger LOGGER = LoggerFactory.getLogger(MessagingService.class);

	private final KafkaTemplate<String, UserMessage> kafkaTemplate;

	public MessagingService(KafkaTemplate<String, UserMessage> kafkaTemplate) {
		this.kafkaTemplate = kafkaTemplate;
	}

	public void sendMessage(final UserMessage message) {
		final ListenableFuture<SendResult<String, UserMessage>> future = kafkaTemplate.send(KafkaConfig.TOPIC_NAME, message.getId().toString(), message);

		future.addCallback(new ListenableFutureCallback<>() {
			@Override
			public void onSuccess(final SendResult<String, UserMessage> result) {
				UserMessage sentMessage = result.getProducerRecord().value();
				LOGGER.info("Message sent for user with ID {} and operation {}", sentMessage.getId(), sentMessage.getOperation());
				LOGGER.debug("Entire message: {}", sentMessage);
			}

			@Override
			public void onFailure(final Throwable ex) {
				LOGGER.info("Failed to send message for user with ID {} and operation {}. Reason: {}", message.getId(), message.getOperation(), ex.getMessage());
			}
		});
	}
}
