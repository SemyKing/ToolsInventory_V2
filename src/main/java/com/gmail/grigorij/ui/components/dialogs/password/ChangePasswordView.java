package com.gmail.grigorij.ui.components.dialogs.password;

import com.gmail.grigorij.backend.database.entities.User;
import com.gmail.grigorij.ui.utils.UIUtils;
import com.gmail.grigorij.utils.ProjectConstants;
import com.gmail.grigorij.utils.authentication.PasswordUtils;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.textfield.PasswordField;


public class ChangePasswordView extends Div {

	private final static String CLASS_NAME = "change-password-view";

	private Div currentPasswordDiv;
	private boolean validateCurrentPassword = false;

	private PasswordField currentPasswordField;
	private PasswordField passwordField1;
	private PasswordField passwordField2;

	private User user;


	public ChangePasswordView(User user) {
		this.user = user;

		addClassName(CLASS_NAME);

		add(constructContent());
	}


	private Div constructContent() {
		Div content = new Div();
		content.addClassName(CLASS_NAME + "__content");

		currentPasswordDiv = new Div();
		currentPasswordDiv.addClassName(CLASS_NAME + "__current_pw");
		content.add(currentPasswordDiv);

		passwordField1 = new PasswordField("New Password");
		passwordField1.setRequired(true);
		passwordField1.addValueChangeListener(e -> passwordField1.setInvalid(false));
		content.add(passwordField1);

		passwordField2 = new PasswordField("Repeat Password");
		passwordField2.setRequired(true);
		passwordField2.addValueChangeListener(e -> passwordField2.setInvalid(false));
		content.add(passwordField2);

		return content;
	}

	public void addCurrentPasswordView() {
		currentPasswordDiv.removeAll();

		currentPasswordField = new PasswordField("Current Password");
		currentPasswordField.setRequired(true);
		currentPasswordField.addValueChangeListener(e -> currentPasswordField.setInvalid(false));
		currentPasswordDiv.add(currentPasswordField);

		validateCurrentPassword = true;
	}

	public boolean isValid() {

		if (user == null) {
			UIUtils.showNotification("User not set", NotificationVariant.LUMO_ERROR, 5000);
			return false;
		}

		if (validateCurrentPassword) {
			if (currentPasswordField.getValue().length() <= 0) {
				UIUtils.showNotification("Please input your current password", NotificationVariant.LUMO_ERROR, 5000);
				currentPasswordField.setInvalid(true);
				return false;
			}

			if (!PasswordUtils.verifyUserPassword(currentPasswordField.getValue(), user.getPassword(), user.getSalt())) {
				UIUtils.showNotification("Current password invalid", NotificationVariant.LUMO_ERROR, 5000);
				currentPasswordField.setInvalid(true);
				return false;
			}
		}

		if (passwordField1.getValue().length() <= 0) {
			UIUtils.showNotification("Please input new password", NotificationVariant.LUMO_ERROR, 5000);
			passwordField1.setInvalid(true);
			return false;
		}

		if (passwordField1.getValue().length() < ProjectConstants.PASSWORD_MIN_LENGTH) {
			UIUtils.showNotification("Password must be at least " + ProjectConstants.PASSWORD_MIN_LENGTH + " characters long", NotificationVariant.LUMO_ERROR, 5000);
			passwordField1.setInvalid(true);
			return false;
		}


		if (passwordField2.getValue().length() <= 0) {
			UIUtils.showNotification("Please repeat new password", NotificationVariant.LUMO_ERROR, 5000);
			passwordField2.setInvalid(true);
			return false;
		}

		if (passwordField2.getValue().length() < ProjectConstants.PASSWORD_MIN_LENGTH) {
			UIUtils.showNotification("Password must be at least " + ProjectConstants.PASSWORD_MIN_LENGTH + " characters long", NotificationVariant.LUMO_ERROR, 5000);
			passwordField2.setInvalid(true);
			return false;
		}

		if (!passwordField1.getValue().equals(passwordField2.getValue())) {
			UIUtils.showNotification("Passwords don't match", NotificationVariant.LUMO_ERROR, 5000);
			passwordField1.setInvalid(true);
			passwordField2.setInvalid(true);
			return false;
		}


		return true;
	}

	public String getNewPassword() {
		return passwordField2.getValue();
	}
}
