package com.faceit.interview.services;

import com.faceit.interview.exceptions.EntityNotFoundException;
import com.faceit.interview.exceptions.InvalidEmailException;
import com.faceit.interview.messages.UserMessage;
import com.faceit.interview.repositories.UserRepository;
import com.faceit.interview.repositories.models.UserDbModel;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.openapitools.model.UserRequest;
import org.openapitools.model.UserResponse;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.data.domain.ExampleMatcher.GenericPropertyMatchers.exact;

public class UserServiceTest {

	private static final PodamFactory PODAM_FACTORY = new PodamFactoryImpl();
	private static final String VALID_EMAIL = "nik@mic.com";

	private UserRepository userRepository;
	private MessagingService messagingService;
	private UserService userService;

	@BeforeEach
	public void setUp() {
		this.userRepository = mock(UserRepository.class);
		this.messagingService = mock(MessagingService.class);
		this.userService = new UserService(userRepository, messagingService);
	}

	@AfterEach
	private void afterEach() {
		verifyNoMoreInteractions(userRepository, messagingService);
	}

	@Test
	public void getUsers_getAllUsers_allUsersReturned() {
		final List<UserDbModel> usersReturnedFromDb = PODAM_FACTORY.manufacturePojo(List.class, UserDbModel.class);

		when(userRepository.findAll()).thenReturn(usersReturnedFromDb);

		final List<UserResponse> usersReturnedFromService = userService.getUsers(null, 0, null);

		assertEquals(usersReturnedFromDb.size(), usersReturnedFromService.size(), "Number of users returned is not as expected.");

		verify(userRepository).findAll();
	}

	@Test
	public void getUsers_getPagedUsers_subsetOfUsersReturned() {
		final List<UserDbModel> usersReturnedFromDb = PODAM_FACTORY.manufacturePojo(List.class, UserDbModel.class);
		final int pageSize = usersReturnedFromDb.size();
		final Page<UserDbModel> usersPage = new PageImpl<>(usersReturnedFromDb);

		when(userRepository.findAll(any(PageRequest.class))).thenReturn(usersPage);

		final List<UserResponse> usersReturnedFromService = userService.getUsers(null, 0, pageSize);

		assertEquals(usersReturnedFromDb.size(), usersReturnedFromService.size(), "Number of users returned is not as expected.");

		final ArgumentCaptor<PageRequest> argumentCaptor = ArgumentCaptor.forClass(PageRequest.class);
		verify(userRepository).findAll(argumentCaptor.capture());
		assertEquals(0, argumentCaptor.getValue().getPageNumber(), "Page number does not match");
		assertEquals(pageSize, argumentCaptor.getValue().getPageSize(), "Page size does not match");
	}

	@Test
	public void getUsers_getAllUsersWithSpecificCountry_relevantUsersReturned() {
		final List<UserDbModel> usersReturnedFromDb = PODAM_FACTORY.manufacturePojo(List.class, UserDbModel.class);

		final String country = PODAM_FACTORY.manufacturePojo(String.class);
		final UserDbModel userDbModelExample = new UserDbModel();
		userDbModelExample.setCountry(country);

		final ExampleMatcher exampleMatcher = ExampleMatcher.matching().withMatcher("country", exact());

		when(userRepository.findAll(any(Example.class))).thenReturn(usersReturnedFromDb);

		final List<UserResponse> usersReturnedFromService = userService.getUsers(country, 0, null);

		assertEquals(usersReturnedFromDb.size(), usersReturnedFromService.size(), "Number of users returned is not as expected.");

		final ArgumentCaptor<Example> argumentCaptor = ArgumentCaptor.forClass(Example.class);
		verify(userRepository).findAll(argumentCaptor.capture());
		assertEquals(userDbModelExample, argumentCaptor.getValue().getProbe(), "Probe does not match");
		assertEquals(exampleMatcher, argumentCaptor.getValue().getMatcher(), "Matcher does not match");
	}

	@Test
	public void getUsers_getPagedUsersWithSpecificCountry_subsetOfContentReturnedWithSpecificCountry() {
		final List<UserDbModel> usersReturnedFromDb = PODAM_FACTORY.manufacturePojo(List.class, UserDbModel.class);

		final String country = PODAM_FACTORY.manufacturePojo(String.class);
		final UserDbModel userDbModelExample = new UserDbModel();
		userDbModelExample.setCountry(country);

		final ExampleMatcher exampleMatcher = ExampleMatcher.matching().withMatcher("country", exact());

		final int pageSize = usersReturnedFromDb.size();
		final Page<UserDbModel> usersPage = new PageImpl<>(usersReturnedFromDb);

		when(userRepository.findAll(any(Example.class), any(PageRequest.class))).thenReturn(usersPage);

		final List<UserResponse> usersReturnedFromService = userService.getUsers(country, 0, pageSize);

		assertEquals(usersReturnedFromDb.size(), usersReturnedFromService.size(), "Number of users returned is not as expected.");

		final ArgumentCaptor<PageRequest> pageArgumentCaptor = ArgumentCaptor.forClass(PageRequest.class);
		final ArgumentCaptor<Example> exampleArgumentCaptor = ArgumentCaptor.forClass(Example.class);
		verify(userRepository).findAll(exampleArgumentCaptor.capture(), pageArgumentCaptor.capture());

		assertEquals(0, pageArgumentCaptor.getValue().getPageNumber(), "Page number does not match");
		assertEquals(pageSize, pageArgumentCaptor.getValue().getPageSize(), "Page size does not match");
		assertEquals(userDbModelExample, exampleArgumentCaptor.getValue().getProbe(), "Probe does not match");
		assertEquals(exampleMatcher, exampleArgumentCaptor.getValue().getMatcher(), "Matcher does not match");
	}

	@Test
	public void addUser_validEmail_userAdded() {
		final UserRequest userRequest = PODAM_FACTORY.manufacturePojo(UserRequest.class);
		userRequest.setEmail(VALID_EMAIL);
		final UserDbModel userReturnedFromDb = PODAM_FACTORY.manufacturePojo(UserDbModel.class);

		when(userRepository.save(any(UserDbModel.class))).thenReturn(userReturnedFromDb);
		doNothing().when(messagingService).sendMessage(any(UserMessage.class));

		userService.addUser(userRequest);

		verify(userRepository).save(any(UserDbModel.class));
		verify(messagingService).sendMessage(any(UserMessage.class));
	}

	@Test
	public void addUser_invalidEmail_throwsInvalidEmailException() {
		final UserRequest userRequest = PODAM_FACTORY.manufacturePojo(UserRequest.class);

		assertThrows(InvalidEmailException.class, () -> userService.addUser(userRequest));
	}

	@Test
	public void getUser_validId_userReturned() {
		final UUID userId = PODAM_FACTORY.manufacturePojo(UUID.class);
		final UserDbModel userReturnedFromDb = PODAM_FACTORY.manufacturePojo(UserDbModel.class);
		userReturnedFromDb.setId(userId);

		when(userRepository.findById(userId)).thenReturn(Optional.of(userReturnedFromDb));

		final UserResponse userReturnedFromService = userService.getUser(userId);

		assertEquals(userReturnedFromDb.getId(), userReturnedFromService.getId(), "ID of user returned is not the same.");

		verify(userRepository).findById(userId);
	}

	@Test
	public void getUser_unknownId_throwsEntityNotFoundException() {
		final UUID userId = PODAM_FACTORY.manufacturePojo(UUID.class);

		when(userRepository.findById(userId)).thenReturn(Optional.empty());

		assertThrows(EntityNotFoundException.class, () -> userService.getUser(userId));

		verify(userRepository).findById(userId);
	}

	@Test
	public void updateUser_validIdAndEmail_userUpdated() {
		final UUID userId = PODAM_FACTORY.manufacturePojo(UUID.class);
		final UserRequest userRequest = PODAM_FACTORY.manufacturePojo(UserRequest.class);
		userRequest.setEmail(VALID_EMAIL);
		final UserDbModel userReturnedFromDbAfterFind = PODAM_FACTORY.manufacturePojo(UserDbModel.class);
		userReturnedFromDbAfterFind.setId(userId);
		final UserDbModel userReturnedFromDbAfterSave = PODAM_FACTORY.manufacturePojo(UserDbModel.class);

		when(userRepository.findById(userId)).thenReturn(Optional.of(userReturnedFromDbAfterFind));
		when(userRepository.save(any(UserDbModel.class))).thenReturn(userReturnedFromDbAfterSave);
		doNothing().when(messagingService).sendMessage(any(UserMessage.class));

		userService.updateUser(userId, userRequest);

		verify(userRepository).findById(userId);
		verify(userRepository).save(any(UserDbModel.class));
		verify(messagingService).sendMessage(any(UserMessage.class));
	}

	@Test
	public void updateUser_validIdButInvalidEmail_throwsInvalidEmailException() {
		final UUID userId = PODAM_FACTORY.manufacturePojo(UUID.class);
		final UserRequest userRequest = PODAM_FACTORY.manufacturePojo(UserRequest.class);
		final UserDbModel userReturnedFromDb = PODAM_FACTORY.manufacturePojo(UserDbModel.class);
		userReturnedFromDb.setId(userId);

		when(userRepository.findById(userId)).thenReturn(Optional.of(userReturnedFromDb));

		assertThrows(InvalidEmailException.class, () -> userService.updateUser(userId, userRequest));

		verify(userRepository).findById(userId);
	}

	@Test
	public void updateUser_invalidId_throwsEntityNotFoundException() {
		final UUID userId = PODAM_FACTORY.manufacturePojo(UUID.class);
		final UserRequest userRequest = PODAM_FACTORY.manufacturePojo(UserRequest.class);

		when(userRepository.findById(userId)).thenReturn(Optional.empty());

		assertThrows(EntityNotFoundException.class, () -> userService.updateUser(userId, userRequest));

		verify(userRepository).findById(userId);
	}

	@Test
	public void deleteUser_validId_userDeleted() {
		final UUID userId = PODAM_FACTORY.manufacturePojo(UUID.class);
		final UserDbModel userReturnedFromDb = PODAM_FACTORY.manufacturePojo(UserDbModel.class);
		userReturnedFromDb.setId(userId);

		when(userRepository.findById(userId)).thenReturn(Optional.of(userReturnedFromDb));
		doNothing().when(userRepository).deleteById(userId);
		doNothing().when(messagingService).sendMessage(any(UserMessage.class));

		userService.deleteUser(userId);

		verify(userRepository).findById(userId);
		verify(userRepository).deleteById(userId);
		verify(messagingService).sendMessage(any(UserMessage.class));
	}

	@Test
	public void deleteUser_unknownId_throwsEntityNotFoundException() {
		final UUID userId = PODAM_FACTORY.manufacturePojo(UUID.class);

		when(userRepository.findById(userId)).thenReturn(Optional.empty());

		assertThrows(EntityNotFoundException.class, () -> userService.deleteUser(userId));

		verify(userRepository).findById(userId);
	}
}
