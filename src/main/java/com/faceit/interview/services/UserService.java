package com.faceit.interview.services;

import com.faceit.interview.exceptions.EntityNotFoundException;
import com.faceit.interview.exceptions.InvalidEmailException;
import com.faceit.interview.mappers.UsersModelMapper;
import com.faceit.interview.messages.UserMessageOperationEnum;
import com.faceit.interview.repositories.UserRepository;
import com.faceit.interview.repositories.models.UserDbModel;
import com.faceit.interview.validators.FieldValidator;
import org.openapitools.model.UserRequest;
import org.openapitools.model.UserResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.springframework.data.domain.ExampleMatcher.GenericPropertyMatchers.exact;

@Service
public class UserService {

	private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

	private final UserRepository userRepository;

	private final MessagingService messagingService;

	public UserService(UserRepository userRepository, MessagingService messagingService) {
		this.userRepository = userRepository;
		this.messagingService = messagingService;
	}

	/**
	 * Retrieves users from the database and maps them to the API model.
	 * Returns only a specific page or users with a specific country depending on the specified parameters
	 *
	 * @param country  Retrieves users whose country matches with the value provided
	 * @param page     Determines the page to retrieve. Ignored if pageSize is null
	 * @param pageSize Determines how many entities to retrieve. Returns all users if value is null.
	 * @return List of users (matching parameters if required)
	 */
	public List<UserResponse> getUsers(final String country, final int page, final Integer pageSize) {
		LOGGER.info("Retrieving users. Country {}, Page {}, pageSize {}", country, page, pageSize);

		final List<UserDbModel> retrievedData = new ArrayList<>();

		if (country == null && pageSize == null) {
			retrievedData.addAll(userRepository.findAll());
		} else if (country == null && pageSize != null) {
			final PageRequest getPage = PageRequest.of(page, pageSize);

			retrievedData.addAll(userRepository.findAll(getPage).getContent());
		} else if (country != null && pageSize == null) {
			final UserDbModel userDbModelExample = new UserDbModel();
			userDbModelExample.setCountry(country);
			final ExampleMatcher exampleMatcher = ExampleMatcher.matching().withMatcher("country", exact()); // TODO: Test

			retrievedData.addAll(userRepository.findAll(Example.of(userDbModelExample, exampleMatcher)));
		} else if (country != null && pageSize != null) {
			final PageRequest getPage = PageRequest.of(page, pageSize);

			final UserDbModel userDbModelExample = new UserDbModel();
			userDbModelExample.setCountry(country);
			final ExampleMatcher exampleMatcher = ExampleMatcher.matching().withMatcher("country", exact());

			retrievedData.addAll(userRepository.findAll(Example.of(userDbModelExample, exampleMatcher), getPage).getContent());
		}

		LOGGER.info("{} entries retrieved.", retrievedData.size());

		return retrievedData.stream().map(UsersModelMapper::mapFromDbModelToApiResponse).collect(Collectors.toList());
	}

	/**
	 * Adds a new user in the database.
	 * {@link com.mongodb.DuplicateKeyException} thrown if user with the specified email already exists
	 *
	 * @param userRequest User representation
	 * @return DB User
	 */
	public UserResponse addUser(final UserRequest userRequest) {
		LOGGER.info("Adding user with parameters {} {} {} {} {}", userRequest.getFirstName(), userRequest.getLastName(), userRequest.getNickname(), userRequest.getEmail(), userRequest.getCountry());

		if (!FieldValidator.validateEmail(userRequest.getEmail())) {
			LOGGER.info("Provided email is invalid. {}", userRequest.getEmail());

			throw new InvalidEmailException("Provided email is invalid. " + userRequest.getEmail());
		}

		final UserDbModel userDbModel = UsersModelMapper.mapFromApiRequestToDbModel(userRequest);

		final UserDbModel newUserInDb = userRepository.save(userDbModel);

		LOGGER.info("User saved. Sending message...");

		messagingService.sendMessage(UsersModelMapper.mapFromDbModelToMessage(newUserInDb, UserMessageOperationEnum.CREATE));

		return UsersModelMapper.mapFromDbModelToApiResponse(newUserInDb);
	}

	/**
	 * Retrieves a specific user's details
	 * If user with ID is not found, {@link EntityNotFoundException} is thrown
	 *
	 * @param userId ID of the user to retrieve
	 * @return USer's details
	 */
	public UserResponse getUser(final UUID userId) {
		LOGGER.info("Retrieving user with ID {}", userId);

		final Optional<UserDbModel> userOpt = userRepository.findById(userId);

		if (userOpt.isPresent()) {
			LOGGER.info("Entity found. Returning entity.");

			return UsersModelMapper.mapFromDbModelToApiResponse(userOpt.get());
		} else {
			LOGGER.info("Entity not found.");

			throw new EntityNotFoundException("User with ID " + userId + " not found");
		}
	}

	/**
	 * Updates user details with a specified ID if user exists in the DB.
	 *
	 * @param userId      of user to update
	 * @param userRequest Updated user details
	 * @return Updated user details
	 */
	public UserResponse updateUser(final UUID userId, final UserRequest userRequest) {
		LOGGER.info("Adding user with parameters {} {} {} {} {} {}", userId, userRequest.getFirstName(), userRequest.getLastName(), userRequest.getNickname(), userRequest.getEmail(), userRequest.getCountry());

		final Optional<UserDbModel> userOpt = userRepository.findById(userId);

		if (userOpt.isPresent()) {
			LOGGER.info("Entity found. Updating entity.");

			if (!FieldValidator.validateEmail(userRequest.getEmail())) {
				LOGGER.info("Provided email is invalid. {}", userRequest.getEmail());

				throw new InvalidEmailException("Provided email is invalid. " + userRequest.getEmail());
			}

			final UserDbModel userDbModel = UsersModelMapper.mapFromApiRequestToDbModel(userRequest);
			userDbModel.setId(userId);
			userDbModel.setCreatedAt(userOpt.get().getCreatedAt());
			final UserDbModel updatedUser = userRepository.save(userDbModel);

			LOGGER.info("Entity updated. Sending message...");

			messagingService.sendMessage(UsersModelMapper.mapFromDbModelToMessage(updatedUser, UserMessageOperationEnum.UPDATE));

			return UsersModelMapper.mapFromDbModelToApiResponse(updatedUser);
		} else {
			LOGGER.info("Entity not found.");

			throw new EntityNotFoundException("User with ID " + userId + " not found");
		}
	}

	/**
	 * Deletes a user with a specified ID if the user exists in the DB.
	 * If user with ID is not found, {@link EntityNotFoundException} is thrown
	 *
	 * @param userId of user to delete
	 */
	public void deleteUser(final UUID userId) {
		LOGGER.info("Deleting user with ID {}", userId);

		final Optional<UserDbModel> userOpt = userRepository.findById(userId);

		if (userOpt.isPresent()) {
			LOGGER.info("Entity found. Deleting entity.");

			userRepository.deleteById(userId);

			LOGGER.info("Entity deleted. Sending message...");

			messagingService.sendMessage(UsersModelMapper.mapFromDbModelToMessage(userOpt.get(), UserMessageOperationEnum.DELETE));
		} else {
			LOGGER.info("Entity not found.");

			throw new EntityNotFoundException("User with ID " + userId + " not found");
		}
	}
}
