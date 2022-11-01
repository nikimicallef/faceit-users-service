package com.faceit.interview.mappers;

import com.faceit.interview.messages.UserMessage;
import com.faceit.interview.messages.UserMessageOperationEnum;
import com.faceit.interview.repositories.models.UserDbModel;
import org.openapitools.model.UserRequest;
import org.openapitools.model.UserResponse;

public class UsersModelMapper {

	public static UserResponse mapFromDbModelToApiResponse(final UserDbModel dbModel) {
		final UserResponse userResponse = new UserResponse();
		userResponse.setId(dbModel.getId());
		userResponse.setFirstName(dbModel.getFirstName());
		userResponse.setLastName(dbModel.getLastName());
		userResponse.setNickname(dbModel.getNickname());
		userResponse.setEmail(dbModel.getEmail());
		userResponse.setCountry(dbModel.getCountry());

		return userResponse;
	}

	public static UserDbModel mapFromApiRequestToDbModel(final UserRequest userRequest) {
		return new UserDbModel(userRequest.getFirstName(),
		userRequest.getLastName(),
		userRequest.getNickname(),
		userRequest.getPassword(),
		userRequest.getEmail(),
		userRequest.getCountry());
	}

	public static UserMessage mapFromDbModelToMessage(final UserDbModel dbModel, final UserMessageOperationEnum operation) {
		return new UserMessage(operation, dbModel.getId(), dbModel.getFirstName(), dbModel.getLastName(), dbModel.getNickname(), dbModel.getEmail(), dbModel.getCountry());
	}
}
