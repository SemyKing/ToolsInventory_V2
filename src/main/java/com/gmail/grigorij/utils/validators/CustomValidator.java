package com.gmail.grigorij.utils.validators;

import com.vaadin.flow.data.validator.RegexpValidator;

public class CustomValidator {

	public static class PhoneNumberValidator extends RegexpValidator {
		private static final String PATTERN = "^\\+?\\d+$";

		public PhoneNumberValidator(String errorMessage) {
			super(errorMessage, PATTERN, true);
		}
	}
}
