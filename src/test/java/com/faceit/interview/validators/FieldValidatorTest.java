package com.faceit.interview.validators;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FieldValidatorTest {

	@Test
	public void validateEmail_gmail_valid() {
		assertTrue(FieldValidator.validateEmail("nikmic94@gmail.com"));
	}

	@Test
	public void validateEmail_gov_valid() {
		assertTrue(FieldValidator.validateEmail("nikmic94@gmail.gov"));
	}

	@Test
	public void validateEmail_symbols_valid() {
		assertTrue(FieldValidator.validateEmail("nikmic-_+.94@gmail.com"));
	}

	@Test
	public void validateEmail_multipleEnds_valid() {
		assertTrue(FieldValidator.validateEmail("nikmic94@gmail.com.at"));
	}

	@Test
	public void validateEmail_noAtSign_invalid() {
		assertFalse(FieldValidator.validateEmail("nikmic94gmail.com"));
	}

	@Test
	public void validateEmail_noDot_invalid() {
		assertFalse(FieldValidator.validateEmail("nikmic94@gmail"));
	}

	@Test
	public void validateEmail_noEnd_invalid() {
		assertFalse(FieldValidator.validateEmail("nikmic94@gmail"));
	}

	@Test
	public void validateEmail_noStart_invalid() {
		assertFalse(FieldValidator.validateEmail("@gmail.com"));
	}

	@Test
	public void validateEmail_noEndButWithDot_invalid() {
		assertFalse(FieldValidator.validateEmail("nikmic94@gmail."));
	}
}
