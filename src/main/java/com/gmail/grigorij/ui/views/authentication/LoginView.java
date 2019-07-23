package com.gmail.grigorij.ui.views.authentication;

import com.gmail.grigorij.backend.database.facades.TransactionFacade;
import com.gmail.grigorij.backend.entities.transaction.Transaction;
import com.gmail.grigorij.backend.entities.user.User;
import com.gmail.grigorij.ui.utils.components.FlexBoxLayout;
import com.gmail.grigorij.ui.utils.css.Display;
import com.gmail.grigorij.ui.utils.css.FlexDirection;
import com.gmail.grigorij.ui.utils.css.LumoStyles;
import com.gmail.grigorij.ui.utils.UIUtils;
import com.gmail.grigorij.ui.utils.css.size.Vertical;
import com.gmail.grigorij.utils.ProjectConstants;
import com.gmail.grigorij.utils.OperationStatus;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.ShortcutRegistration;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.PageTitle;
import javafx.scene.layout.Border;

/**
 * Log In UI.
 */
@PageTitle("Login")
public class LoginView extends Div {

	private static final String CLASS_NAME = "login-view";

	private FlexBoxLayout loginFailWrapper;
	private Paragraph loginFailContent;

	private H5 loginFailHeader = new H5();
	private Span loginFailMessage = new Span();

	private TextField usernameField;
	private PasswordField passwordField;

	private ShortcutRegistration registration;


	private OperationStatus operationStatus;


	public LoginView(OperationStatus operationStatus) {
		this.operationStatus = operationStatus;
		setSizeFull();
		setClassName(CLASS_NAME);

//		getElement().setAttribute(LumoStyles.THEME, LumoStyles.DARK);

        buildUI();
		usernameField.focus();


		//TODO:REMOVE
		usernameField.setValue("u");
		passwordField.setValue("p");
	}

	private void buildUI() {
		H2 logoText = new H2(ProjectConstants.PROJECT_NAME_FULL);

		Image logoImage = new Image("/" + ProjectConstants.IMAGES_PATH + ProjectConstants.LOGO_FULL_ROUND_SVG,"logo");
		logoImage.setClassName(CLASS_NAME + "__image");

		FlexBoxLayout oval = new FlexBoxLayout();
		oval.setClassName(CLASS_NAME + "__logo-bg");
		oval.setSizeFull();
		oval.setFlexDirection(FlexDirection.COLUMN);
		oval.setDisplay(Display.FLEX);
		oval.add(logoText, logoImage);

		FlexBoxLayout logoWrapper = new FlexBoxLayout();
		logoWrapper.setClassName(CLASS_NAME + "__logo-wrapper");
		logoWrapper.setFlexDirection(FlexDirection.COLUMN);
		logoWrapper.setDisplay(Display.FLEX);
		logoWrapper.add(oval);

		usernameField = new TextField("Username");
		usernameField.setId("username");
		usernameField.setWidth("100%");
		usernameField.setRequired(true);
		usernameField.setPrefixComponent(VaadinIcon.USER.create());

		passwordField = new PasswordField("Password");
		passwordField.setWidth("100%");
		passwordField.setRequired(true);
		passwordField.setPrefixComponent(VaadinIcon.LOCK.create());

		Checkbox rememberMe = new Checkbox("Remember me");
		Button forgotPasswordButton = UIUtils.createButton("Forgot password", ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_TERTIARY);

		FlexBoxLayout flexLayout = new FlexBoxLayout();
		flexLayout.setWidth("100%");
		flexLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
		flexLayout.setAlignItems(FlexComponent.Alignment.CENTER);
		flexLayout.setMargin(Vertical.S);
		flexLayout.add(rememberMe, forgotPasswordButton);

		Button loginButton = UIUtils.createPrimaryButton("LOG IN");
		loginButton.setWidth("100%");
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


		Binder<User> binder = new Binder<>(User.class);
		binder.setBean(User.getEmptyUser());

		binder.forField(usernameField)
				.asRequired("Username is required")
				.bind(User::getUsername, User::setUsername);

		binder.forField(passwordField)
				.asRequired("Password is required")
				.bind(User::getPassword, User::setPassword);


		loginButton.addClickListener(e -> {
			binder.validate();
			if (binder.isValid()) {
				validateAndLogIn(usernameField.getValue(), passwordField.getValue(), rememberMe.getValue());
			}
		});

		forgotPasswordButton.addClickListener(e -> {

			//remove ENTER key listener for sign in button
			registration.remove();

			//open password recovery dialog
			new PasswordRecovery(new OperationStatus() {
				@Override //Window closed -> reattach ENTER key listener for sign in button
				public void onSuccess(String msg, UIUtils.NotificationType type) {
					registration = loginButton.addClickShortcut(Key.ENTER);
				}

				@Override
				public void onFail(String msg, UIUtils.NotificationType type) {

				}
			});
		});
	}


	private void validateAndLogIn(String username, String password, boolean rememberMe) {
		loginFailWrapper.setComponentDisplay(loginFailContent, Display.NONE);
		loginFailHeader.setText("");
		loginFailMessage.setText("");

		if (AuthenticationService.signIn(username, password, rememberMe)) {

			if (!TransactionFacade.getInstance().insertLoginTransaction("")) {
				System.out.println("Login Transaction INSERT Error");
			}

			operationStatus.onSuccess("Login successful", null);
		} else {
			showLoginFail();
		}
	}


	private void showLoginFail() {
		operationStatus.onFail("Login fail", null);

		loginFailWrapper.setComponentDisplay(loginFailContent, Display.FLEX);
		loginFailHeader.setText("Incorrect username or password");
		loginFailMessage.setText("The username and password you entered do not match our records. Please double-check and try again");
	}
}
