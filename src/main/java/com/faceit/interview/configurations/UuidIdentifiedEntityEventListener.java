package com.faceit.interview.configurations;

import com.faceit.interview.repositories.models.UserDbModel;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent;

import java.util.UUID;

/**
 * Required to handle {@link UUID} as an {@link Id} for the {@link UserDbModel}
 */
public class UuidIdentifiedEntityEventListener extends AbstractMongoEventListener<UserDbModel> {

	@Override
	public void onBeforeConvert(BeforeConvertEvent<UserDbModel> event) {
		super.onBeforeConvert(event);
		UserDbModel userDbModel = event.getSource();

		if (userDbModel.getId() == null) {
			userDbModel.setId(UUID.randomUUID());
		}
	}
}
