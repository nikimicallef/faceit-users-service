package com.faceit.interview.mappers;

import com.faceit.interview.messages.UserMessage;
import com.faceit.interview.messages.UserMessageOperationEnum;
import com.faceit.interview.repositories.models.UserDbModel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.openapitools.model.UserRequest;
import org.openapitools.model.UserResponse;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserModelMapperTest {

	private static final PodamFactory PODAM_FACTORY = new PodamFactoryImpl();

	@Test
	public void mapFromDbModelToApiResponse_randomDbModel_correctMapping() {
		final UserDbModel dbModel = PODAM_FACTORY.manufacturePojo(UserDbModel.class);

		final UserResponse apiModel = UsersModelMapper.mapFromDbModelToApiResponse(dbModel);

		assertEquals(dbModel.getId(), apiModel.getId(), "ID does not match");
		assertEquals(dbModel.getFirstName(), apiModel.getFirstName(), "First name does not match");
		assertEquals(dbModel.getLastName(), apiModel.getLastName(), "Last name does not match");
		assertEquals(dbModel.getNickname(), apiModel.getNickname(), "Nickname does not match");
		assertEquals(dbModel.getEmail(), apiModel.getEmail(), "Email does not match");
		assertEquals(dbModel.getCountry(), apiModel.getCountry(), "Country does not match");
	}

	@Test
	public void mapFromApiRequestToDbModel_randomApiRequest_correctMapping() {
		final UserRequest apiRequest = PODAM_FACTORY.manufacturePojo(UserRequest.class);

		final UserDbModel dbModel = UsersModelMapper.mapFromApiRequestToDbModel(apiRequest);

		assertEquals(apiRequest.getFirstName(), dbModel.getFirstName(), "First name does not match");
		assertEquals(apiRequest.getLastName(), dbModel.getLastName(), "Last name does not match");
		assertEquals(apiRequest.getNickname(), dbModel.getNickname(), "Nickname does not match");
		assertEquals(apiRequest.getEmail(), dbModel.getEmail(), "Email does not match");
		assertEquals(apiRequest.getCountry(), dbModel.getCountry(), "Country does not match");
	}

	@ParameterizedTest
	@EnumSource(UserMessageOperationEnum.class)
	public void mapFromDbModelToMessage_randomDbModel_correctMapping(final UserMessageOperationEnum operation) {
		final UserDbModel dbModel = PODAM_FACTORY.manufacturePojo(UserDbModel.class);

		final UserMessage message = UsersModelMapper.mapFromDbModelToMessage(dbModel, operation);

		assertEquals(operation, message.getOperation(), "Operation does not match");
		assertEquals(dbModel.getId(), message.getId(), "ID does not match");
		assertEquals(dbModel.getFirstName(), message.getFirstName(), "First name does not match");
		assertEquals(dbModel.getLastName(), message.getLastName(), "Last name does not match");
		assertEquals(dbModel.getNickname(), message.getNickname(), "Nickname does not match");
		assertEquals(dbModel.getEmail(), message.getEmail(), "Email does not match");
		assertEquals(dbModel.getCountry(), message.getCountry(), "Country does not match");
	}
}
