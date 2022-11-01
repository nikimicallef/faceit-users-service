package com.faceit.interview.controllers;

import com.faceit.interview.exceptions.EntityNotFoundException;
import com.faceit.interview.exceptions.InvalidEmailException;
import org.openapitools.model.Error;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.ArrayList;
import java.util.List;


@ControllerAdvice
public class GlobalExceptionHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	@ExceptionHandler(EntityNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public @ResponseBody Error handleEntityNotFoundException(final EntityNotFoundException e, final WebRequest request) {

		LOGGER.info("Entity not found exception encountered. Returning a {} response.", HttpStatus.NOT_FOUND.value());
		LOGGER.debug("Request: {}", request.toString(), e);

		final Error error = new Error();
		error.setMessage(e.getMessage() == null ? "Entity with the provided ID has not been found." : e.getMessage());
		error.setValidationErrors(new ArrayList<>());

		return error;
	}

	@ExceptionHandler(DuplicateKeyException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public @ResponseBody Error handleDuplicateKeyException(final DuplicateKeyException e, final WebRequest request) {

		LOGGER.info("Entity with the specified email already exists. Returning a {} response.", HttpStatus.BAD_REQUEST.value());
		LOGGER.debug("Request: {}", request, e);

		final Error error = new Error();
		error.setMessage("Incorrect data sent in the request body");
		error.setValidationErrors(List.of("An account with the specified email already exists. Please enter a different email."));

		return error;
	}

	@ExceptionHandler(InvalidEmailException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public @ResponseBody Error handleInvalidEmailException(final InvalidEmailException e, final WebRequest request) {

		LOGGER.info("Provided email does not match the specified format. Returning a {} response.", HttpStatus.BAD_REQUEST.value());
		LOGGER.debug("Request: {}", request, e);

		final Error error = new Error();
		error.setMessage("Incorrect data sent in the request body");
		error.setValidationErrors(List.of("The provided email is not a valid email."));

		return error;
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public @ResponseBody Error handleHttpMessageNotReadableException(final HttpMessageNotReadableException e, final WebRequest request) {

		LOGGER.info("Http Message Not Readable exception encountered. {} {}. Returning a {} response.",
		((ServletWebRequest) request).getHttpMethod(),
		((ServletWebRequest) request).getRequest().getRequestURI(),
		HttpStatus.BAD_REQUEST.value());
		LOGGER.debug("Exception: ", e);

		final Error error = new Error();
		error.setMessage("Incorrect data sent in the request body");

		final Throwable cause = e.getCause().getCause();
		if (cause != null) {
			error.setValidationErrors(List.of(cause.getLocalizedMessage()));
		}

		return error;
	}

	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public @ResponseBody Error handleMethodArgumentTypeMismatchException(final MethodArgumentTypeMismatchException e, final WebRequest request) {

		LOGGER.info("Method Argument Type Mismatch exception encountered. {} {}. Returning a {} response.",
		((ServletWebRequest) request).getHttpMethod(),
		((ServletWebRequest) request).getRequest().getRequestURI(),
		HttpStatus.BAD_REQUEST.value());
		LOGGER.debug("Exception: ", e);

		final Error error = new Error();
		error.setMessage("Incorrect data sent in the request");
		error.setValidationErrors(List.of("Value set for parameter " + e.getName() + " is not valid"));

		return error;
	}

	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	@ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
	public @ResponseBody Error handleHttpRequestMethodNotSupportedException(final HttpRequestMethodNotSupportedException e, final WebRequest request) {

		LOGGER.info("Method not allowed exception encountered. {} {}. Returning a {} response.",
		((ServletWebRequest) request).getHttpMethod(),
		((ServletWebRequest) request).getRequest().getRequestURI(),
		HttpStatus.METHOD_NOT_ALLOWED.value());
		LOGGER.debug("Exception: ", e);

		final Error error = new Error();
		error.setMessage(e.getMessage());
		error.setValidationErrors(new ArrayList<>());

		return error;
	}

	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public @ResponseBody Error handleGenericException(final Exception e, final WebRequest request) {

		LOGGER.info("Generic exception encountered. Returning a {} response.\nRequest: {} {}",
		HttpStatus.INTERNAL_SERVER_ERROR.value(),
		((ServletWebRequest) request).getHttpMethod(),
		((ServletWebRequest) request).getRequest().getRequestURI(),
		e);

		final Error error = new Error();
		error.setMessage("An internal error has occurred. Please try again later.");
		error.setValidationErrors(new ArrayList<>());

		return error;
	}

}
