package com.gmail.grigorij.ui.application.authentication.login;

import com.gmail.grigorij.backend.database.facades.TransactionFacade;
import com.gmail.grigorij.backend.entities.transaction.Transaction;
import com.gmail.grigorij.backend.entities.user.User;
import com.gmail.grigorij.backend.enums.transactions.TransactionTarget;
import com.gmail.grigorij.backend.enums.transactions.TransactionType;
import com.gmail.grigorij.ui.application.authentication.forgot.ForgotPasswordDialog;
import com.gmail.grigorij.ui.application.authentication.forgot.ForgotPasswordView;
import com.gmail.grigorij.ui.components.layouts.FlexBoxLayout;
import com.gmail.grigorij.ui.utils.css.Display;
import com.gmail.grigorij.ui.utils.css.FlexDirection;
import com.gmail.grigorij.ui.utils.css.LumoStyles;
import com.gmail.grigorij.ui.utils.UIUtils;
import com.gmail.grigorij.ui.utils.css.size.Vertical;
import com.gmail.grigorij.utils.AuthenticationService;
import com.gmail.grigorij.utils.ProjectConstants;
import com.gmail.grigorij.utils.OperationStatus;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.ShortcutRegistration;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.PageTitle;

/**
 * Log In UI.
 */
@PageTitle("Login")
@StyleSheet("context://styles/login.css")
public class LoginView extends Div {

	private static final String CLASS_NAME = "login-view";

	private FlexBoxLayout loginFailWrapper;
	private Paragraph loginFailContent;

	private TextField usernameField;
	private PasswordField passwordField;

	private ShortcutRegistration registration;

	private Binder<User> binder;

	private Checkbox rememberMeCheckBox;
	private Button loginButton;

	private final OperationStatus operationStatus;


	public LoginView(OperationStatus operationStatus) {
		this.operationStatus = operationStatus;

		setClassName(CLASS_NAME);


		add(constructContentHeader());
		add(constructContent());

		constructBinder();


//        buildUI();
		usernameField.focus();

		//TODO:REMOVE AT PRODUCTION
		usernameField.setValue("system_admin");
		passwordField.setValue("password");
	}

	private FlexBoxLayout constructContentHeader() {
		FlexBoxLayout contentHeader = new FlexBoxLayout();
		contentHeader.addClassName(CLASS_NAME + "__content-header");

		H2 logoText = new H2(ProjectConstants.PROJECT_NAME_FULL);

		return contentHeader;
	}


	private FlexBoxLayout constructContent() {
		FlexBoxLayout content = new FlexBoxLayout();
		content.addClassName(CLASS_NAME + "__content");



		Image logoImage = new Image("/" + ProjectConstants.IMAGES_PATH + ProjectConstants.LOGO_FULL_ROUND_SVG,"logo");
		logoImage.setClassName(CLASS_NAME + "__image");

		Div logoBackgroundDiv = new Div();
		logoBackgroundDiv.setClassName(CLASS_NAME + "__logo-bg");
		logoBackgroundDiv.add(logoText, logoImage);

		Div logoWrapper = new Div();
		logoWrapper.setClassName(CLASS_NAME + "__logo-wrapper");
		logoWrapper.add(logoBackgroundDiv);

		usernameField = new TextField("Username");
		usernameField.setId("username");
		usernameField.setWidth("100%");
		usernameField.setRequired(true);
		usernameField.setPrefixComponent(VaadinIcon.USER.create());

		passwordField = new PasswordField("Password");
		passwordField.setWidth("100%");
		passwordField.setRequired(true);
		passwordField.setPrefixComponent(VaadinIcon.LOCK.create());

		rememberMeCheckBox = new Checkbox("Remember me");

		Button forgotPasswordButton = UIUtils.createButton("Forgot password", ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_TERTIARY);

		FlexBoxLayout flexLayout = new FlexBoxLayout();
		flexLayout.setWidth("100%");
		flexLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
		flexLayout.setAlignItems(FlexComponent.Alignment.CENTER);
		flexLayout.setMargin(Vertical.S);
		flexLayout.add(rememberMeCheckBox, forgotPasswordButton);

		loginButton = UIUtils.createPrimaryButton("LOG IN");
		registration = loginButton.addClickShortcut(Key.ENTER);


		//Login Form
		FormLayout formLayout = new FormLayout();
		formLayout.addClassNames(LumoStyles.Padding.Bottom.L, LumoStyles.Padding.Horizontal.L, LumoStyles.Padding.Top.S);
		formLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1, FormLayout.ResponsiveStep.LabelsPosition.TOP));
		formLayout.setClassName(CLASS_NAME + "__form");
		formLayout.add(usernameField);
		formLayout.add(passwordField);
		formLayout.add(flexLayout);
		formLayout.add(loginButton);

		loginFailWrapper = new FlexBoxLayout();
		loginFailWrapper.setWidth("100%");
		loginFailWrapper.setClassName(CLASS_NAME + "__fail-wrapper");

		H5 loginFailHeader = new H5();
		Span loginFailMessage = new Span();

		loginFailHeader.setText("Incorrect username or password");
		loginFailMessage.setText("The username and password you entered do not match our records. Please double-check and try again");

		loginFailContent = new Paragraph(loginFailHeader, loginFailMessage);
		loginFailWrapper.add(loginFailContent);
		loginFailWrapper.setComponentDisplay(loginFailContent, Display.NONE);
		loginFailWrapper.setComponentFlexDirection(loginFailContent, FlexDirection.COLUMN);

		FlexBoxLayout formAndErrorWrapper = new FlexBoxLayout();
		formAndErrorWrapper.setFlexDirection(FlexDirection.COLUMN);
		formAndErrorWrapper.setSizeFull();
		formAndErrorWrapper.setFlexGrowSelf("1");
		formAndErrorWrapper.add(formLayout, loginFailWrapper);

		Div viewWrapper = new Div();
		viewWrapper.addClassName(CLASS_NAME + "__wrapper");
		viewWrapper.add(logoWrapper, formAndErrorWrapper);

		add(viewWrapper);


		loginButton.addClickListener(e -> {
			loginOnClick();
		});

		forgotPasswordButton.addClickListener(e -> {
			forgotPasswordOnClick();
		});

		return content;
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
		loginFailWrapper.setComponentDisplay(loginFailContent, Display.NONE);

		if (AuthenticationService.signIn(username, password, rememberMe)) {

			Transaction tr = new Transaction();
			tr.setWhoDid(AuthenticationService.getCurrentSessionUser());
			tr.setTransactionOperation(TransactionType.LOGIN);
			tr.setTransactionTarget(TransactionTarget.USER);

			TransactionFacade.getInstance().insert(tr);

			operationStatus.onSuccess("Login successful", null);
		} else {
			loginFailWrapper.setComponentDisplay(loginFailContent, Display.FLEX);
			operationStatus.onFail("Login fail", null);
		}
	}
}
