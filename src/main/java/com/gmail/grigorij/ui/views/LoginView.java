package com.gmail.grigorij.ui.views;

import com.gmail.grigorij.backend.database.entities.User;
import com.gmail.grigorij.ui.components.FlexBoxLayout;
import com.gmail.grigorij.ui.components.dialogs.password.ForgotPasswordDialog;
import com.gmail.grigorij.ui.utils.UIUtils;
import com.gmail.grigorij.utils.authentication.AuthenticationService;
import com.gmail.grigorij.utils.OperationStatus;
import com.gmail.grigorij.utils.ProjectConstants;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.ShortcutRegistration;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;

/**
 * Log In UI.
 */
@CssImport("./styles/views/login.css")
public class LoginView extends Div {

	private static final String CLASS_NAME = "login-view";
	private final OperationStatus operationStatus;

	private TextField usernameField;
	private PasswordField passwordField;
	private Checkbox rememberMeCheckBox;
	private Button loginButton;

	private Div loginErrorLayout;

	private ShortcutRegistration registration;
	private Binder<User> binder;


	public LoginView(OperationStatus operationStatus) {
		this.operationStatus = operationStatus;

		setClassName(CLASS_NAME);

		Div wrapper = new Div();
		wrapper.addClassName(CLASS_NAME + "__wrapper");

		wrapper.add(constructContentHeader());
		wrapper.add(constructContent());

		add(wrapper);

		constructBinder();

		usernameField.focus();

		//TODO:REMOVE AT PRODUCTION
		usernameField.setValue("system.admin");
		passwordField.setValue("password");
	}


	private FlexBoxLayout constructContentHeader() {
		FlexBoxLayout contentHeader = new FlexBoxLayout();
		contentHeader.addClassName(CLASS_NAME + "__content-header");

		H2 logoText = new H2(ProjectConstants.PROJECT_NAME_FULL);

		Image logoImage = new Image("/" + ProjectConstants.IMAGES_PATH + ProjectConstants.LOGO_FULL_ROUND_SVG,"logo");
		logoImage.setClassName(CLASS_NAME + "__image");

		Div logoBackgroundDiv = new Div();
		logoBackgroundDiv.setClassName(CLASS_NAME + "__logo-bg");
		logoBackgroundDiv.add(logoText, logoImage);

		contentHeader.add(logoBackgroundDiv);

		return contentHeader;
	}


	private FlexBoxLayout constructContent() {
		FlexBoxLayout content = new FlexBoxLayout();
		content.addClassName(CLASS_NAME + "__content");

		usernameField = new TextField("Username");
		usernameField.setId("username");
		usernameField.setRequired(true);
		usernameField.setPrefixComponent(VaadinIcon.USER.create());

		content.add(usernameField);

		passwordField = new PasswordField("Password");
		passwordField.setRequired(true);
		passwordField.setPrefixComponent(VaadinIcon.LOCK.create());

		content.add(passwordField);

		rememberMeCheckBox = new Checkbox("Remember me");

		Button forgotPasswordButton = UIUtils.createButton("Forgot password", ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_TERTIARY);
		forgotPasswordButton.addClickListener(e -> {
			forgotPasswordOnClick();
		});

		Div layout = new Div();
		layout.addClassName(CLASS_NAME + "__content-space-between");
		layout.add(rememberMeCheckBox, forgotPasswordButton);

		content.add(layout);

		loginButton = UIUtils.createButton("LOG IN", ButtonVariant.LUMO_PRIMARY);
		loginButton.addClickListener(e -> {
			loginOnClick();
		});

		registration = loginButton.addClickShortcut(Key.ENTER);

		content.add(loginButton);
		content.add(constructLoginErrorLayout());

		return content;
	}

	private Div constructLoginErrorLayout() {
		loginErrorLayout = new Div();
		loginErrorLayout.setClassName(CLASS_NAME + "__error-wrapper");

		Paragraph loginFailContent = new Paragraph(
				new H5("Incorrect username or password"),
				new Span("The username and password you entered do not match our records. Please double-check and try again"));

		loginErrorLayout.add(loginFailContent);
		loginErrorLayout.getElement().setAttribute("visible", false);

		return loginErrorLayout;
	}

	private void constructBinder() {
		binder = new Binder<>(User.class);
		binder.setBean(new User());

		binder.forField(usernameField)
				.asRequired("Username is required")
				.bind(User::getUsername, User::setUsername);

		binder.forField(passwordField)
				.asRequired("Password is required")
				.bind(User::getPassword, User::setPassword);
	}


	private void forgotPasswordOnClick() {
		//remove ENTER key listener for sign in button
		registration.remove();

		ForgotPasswordDialog dialog = new ForgotPasswordDialog();
		dialog.addDetachListener((DetachEvent event) -> {
			registration = loginButton.addClickShortcut(Key.ENTER);
		});
		dialog.open();
	}

	private void loginOnClick() {
		binder.validate();

		if (binder.isValid()) {
			validateAndLogIn(usernameField.getValue(), passwordField.getValue(), rememberMeCheckBox.getValue());
		}
	}


	private void validateAndLogIn(String username, String password, boolean rememberMe) {
		loginErrorLayout.getElement().setAttribute("visible", false);

		if (AuthenticationService.signIn(username, password, rememberMe)) {

			operationStatus.onSuccess("");
		} else {
			loginErrorLayout.getElement().setAttribute("visible", true);
			operationStatus.onFail();
		}
	}
}
