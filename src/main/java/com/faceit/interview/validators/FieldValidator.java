package com.faceit.interview.validators;

import java.util.regex.Pattern;

public class FieldValidator {

	/**
	 * Validates email address to a pre-defined regex
	 *
	 * @param emailToValidate String to validate
	 * @return true if provided string is a valid email
	 */
	public static boolean validateEmail(final String emailToValidate) {
		final Pattern pattern = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)\\.(.+)$");

		return pattern.matcher(emailToValidate).matches();
	}

}
