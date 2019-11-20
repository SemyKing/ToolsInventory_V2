package com.gmail.grigorij.ui.views.routes;

import com.gmail.grigorij.backend.database.entities.RecoveryLink;
import com.gmail.grigorij.backend.database.entities.Transaction;
import com.gmail.grigorij.backend.database.entities.User;
import com.gmail.grigorij.backend.database.enums.operations.Operation;
import com.gmail.grigorij.backend.database.enums.operations.OperationTarget;
import com.gmail.grigorij.backend.database.facades.RecoveryLinkFacade;
import com.gmail.grigorij.backend.database.facades.TransactionFacade;
import com.gmail.grigorij.backend.database.facades.UserFacade;
import com.gmail.grigorij.ui.utils.UIUtils;
import com.gmail.grigorij.utils.ProjectConstants;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Route;

/**
 * Page accessible form URL containing .../reset-password/ + unique code
 */

@Route(value = "reset-password")
@StyleSheet("context://styles/views/password-reset.css")
public class ResetPasswordView extends Div implements HasUrlParameter<String> {

	private static final String CLASS_NAME = "password-reset-view";

	private Div content;
	private RecoveryLink link;

	public ResetPasswordView() {
		setClassName(CLASS_NAME);

		add(constructContent());
	}

	private Div constructContent() {
		Div wrapperDiv = new Div();
		wrapperDiv.addClassName(CLASS_NAME+"__wrapper");

		Image logoImage = new Image("/" + ProjectConstants.IMAGES_PATH + ProjectConstants.LOGO_FULL_ROUND_SVG,"logo");
		logoImage.setClassName(CLASS_NAME + "__image");

		H2 logoText = new H2(ProjectConstants.PROJECT_NAME_FULL);

		Div logoWrapper = new Div();
		logoWrapper.setClassName(CLASS_NAME + "__logo-wrapper");
		logoWrapper.add(logoText, logoImage);

		wrapperDiv.add(logoWrapper);

		content = new Div();
		content.setClassName(CLASS_NAME + "__content");

		wrapperDiv.add(content);

		return wrapperDiv;
	}


	@Override
	public void setParameter(BeforeEvent event, String tokenParameter) {
		tokenParameter = tokenParameter.replaceAll("[^a-zA-Z]", "");

		if (tokenParameter.length() < ProjectConstants.RECOVERY_TOKEN_LENGTH || tokenParameter.length() > ProjectConstants.RECOVERY_TOKEN_LENGTH) {
			System.err.println("Token length is Invalid");
			showTokenInvalid();
			return;
		}

		checkIfTokenValid(tokenParameter);

//		getElement().setAttribute(LumoStyles.THEME, "");
//
//		final UI ui = UI.getCurrent();
//		if (ui != null) {
//			ui.getElement().setAttribute(LumoStyles.THEME, Lumo.DARK);
//		}
	}

	private void checkIfTokenValid(String token) {
		link = RecoveryLinkFacade.getInstance().getRecoveryLinkByToken(token);

		if (link == null) {
			System.err.println("RecoveryLink is NULL");
			showTokenInvalid();
		} else {
			if (link.isDeleted()) {
				System.err.println("RecoveryLink is 'DELETED'");
				showTokenInvalid();
			}

			showPasswordReset();
		}
	}


	private void showTokenInvalid() {
		Span message = new Span("Password reset link is invalid or has expired");
		message.addClassName(CLASS_NAME + "__exp-link");

		content.removeAll();
		content.add(message);
	}

	private void showPasswordReset() {
		content.removeAll();
		content.add(UIUtils.createH3Label("Reset Password"));

		PasswordField passwordField1 = new PasswordField("New Password");
		passwordField1.setRequired(true);
		passwordField1.setPrefixComponent(VaadinIcon.LOCK.create());

		content.add(passwordField1);

		PasswordField passwordField2 = new PasswordField("Repeat Password");
		passwordField2.setRequired(true);
		passwordField2.setPrefixComponent(VaadinIcon.LOCK.create());

		content.add(passwordField2);

		Button savePasswordButton = UIUtils.createButton("Save Password", ButtonVariant.LUMO_PRIMARY);
		savePasswordButton.addClickShortcut(Key.ENTER);
		savePasswordButton.addClickListener(resetEvent -> {
			validatePasswords(passwordField1, passwordField2);
		});

		content.add(savePasswordButton);
	}

	private void validatePasswords(PasswordField p1, PasswordField p2) {

		if (p1.getValue().length() < ProjectConstants.PASSWORD_MIN_LENGTH) {
			p1.setErrorMessage("Password length must be at least " + ProjectConstants.PASSWORD_MIN_LENGTH + " characters");
			p1.setInvalid(true);
			return;
		}

		if (p2.getValue().length() < ProjectConstants.PASSWORD_MIN_LENGTH) {
			p2.setErrorMessage("Password length must be at least " + ProjectConstants.PASSWORD_MIN_LENGTH + " characters");
			p2.setInvalid(true);
			return;
		}

		if (!p1.getValue().equals(p2.getValue())) {
			p1.setErrorMessage("Passwords do not match");
			p1.setInvalid(true);

			p2.setErrorMessage("Passwords do not match");
			p2.setInvalid(true);
		} else {
			resetPassword(p1.getValue());
		}
	}

	private void resetPassword(String newPassword) {
		User user = link.getUser();

		if (user == null) {
			System.err.println("USER IS NULL IN RECOVERYLINK -> PASSWORD RECOVERY");
			UIUtils.showNotification("Error occurred, please contact System Administrator", NotificationVariant.LUMO_ERROR);
		} else {
			user.setPassword(newPassword);

			if (UserFacade.getInstance().update(user)) {
				UIUtils.showNotification("Password reset", NotificationVariant.LUMO_SUCCESS);

				Transaction transaction = new Transaction();
				transaction.setUser(user);
				transaction.setCompany(user.getCompany());
				transaction.setOperation(Operation.CHANGE);
				transaction.setOperationTarget1(OperationTarget.PASSWORD);
				TransactionFacade.getInstance().insert(transaction);

				if (!RecoveryLinkFacade.getInstance().remove(link)) {
					System.err.println("Error removing RecoveryLink");

					link.setDeleted(true);
					RecoveryLinkFacade.getInstance().update(link);
				}

				if (UI.getCurrent() != null) {
					UI.getCurrent().navigate("");
				}
			} else {
				UIUtils.showNotification("Password reset Error", NotificationVariant.LUMO_ERROR);
			}
		}
	}
}
