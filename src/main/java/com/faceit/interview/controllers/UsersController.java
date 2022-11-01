package com.faceit.interview.controllers;

import com.faceit.interview.services.UserService;
import org.openapitools.api.UsersApiController;
import org.openapitools.model.UserRequest;
import org.openapitools.model.UserResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.NativeWebRequest;

import java.util.List;
import java.util.UUID;

@Component
public class UsersController extends UsersApiController {

	private final UserService userService;

	public UsersController(NativeWebRequest request, UserService userService) {
		super(request);
		this.userService = userService;
	}

	@Override
	public ResponseEntity<Void> deleteUser(String id) {
		userService.deleteUser(UUID.fromString(id));
		return ResponseEntity.noContent().build();
	}

	@Override
	public ResponseEntity<UserResponse> getUser(String id) {
		return ResponseEntity.ok(userService.getUser(UUID.fromString(id)));
	}

	@Override
	public ResponseEntity<List<UserResponse>> getUsers(String country, Integer page, Integer pageSize) {
		return ResponseEntity.ok(userService.getUsers(country, page != null ? page : 0, pageSize));
	}

	@Override
	public ResponseEntity<UserResponse> postUsers(UserRequest userRequest) {
		return new ResponseEntity<>(userService.addUser(userRequest), HttpStatus.CREATED);
	}

	@Override
	public ResponseEntity<UserResponse> updateUser(String id, UserRequest userRequest) {
		return ResponseEntity.ok(userService.updateUser(UUID.fromString(id), userRequest));
	}
}
