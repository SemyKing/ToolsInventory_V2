package com.gmail.grigorij.ui.views.authentication.passwordrecovery;

import com.gmail.grigorij.backend.database.facades.RecoveryLinkFacade;
import com.gmail.grigorij.backend.database.facades.UserFacade;
import com.gmail.grigorij.backend.entities.recoverylink.RecoveryLink;
import com.gmail.grigorij.backend.entities.user.User;
import com.gmail.grigorij.ui.utils.UIUtils;
import com.gmail.grigorij.ui.utils.components.FlexBoxLayout;
import com.gmail.grigorij.ui.utils.css.Display;
import com.gmail.grigorij.ui.utils.css.FlexDirection;
import com.gmail.grigorij.ui.utils.css.LumoStyles;
import com.gmail.grigorij.ui.utils.css.size.Top;
import com.gmail.grigorij.utils.ProjectConstants;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.HtmlImport;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.Lumo;


@Route(value = "reset-password")
@HtmlImport("frontend://styles/shared-styles.html")
public class PasswordResetView extends FlexBoxLayout implements HasUrlParameter<String> {

	private static final String CLASS_NAME = "password-reset-view";

	private FlexBoxLayout formLayout;
	private RecoveryLink link;

	public PasswordResetView() {
		setSizeFull();
		setClassName(CLASS_NAME);
		setJustifyContentMode(JustifyContentMode.CENTER);

		H2 logoText = new H2(ProjectConstants.PROJECT_NAME_FULL);

		Image logoImage = new Image("/" + ProjectConstants.IMAGES_PATH + ProjectConstants.LOGO_FULL_ROUND_SVG,"logo");
		logoImage.setClassName(CLASS_NAME + "__image");

		FlexBoxLayout logoWrapper = new FlexBoxLayout();
		logoWrapper.setClassName(CLASS_NAME + "__logo-wrapper");
		logoWrapper.setFlexDirection(FlexDirection.ROW);
		logoWrapper.setDisplay(Display.FLEX);
		logoWrapper.add(logoImage, logoText);


		FlexBoxLayout wrapper = new FlexBoxLayout();
		wrapper.setClassName(CLASS_NAME + "__form-wrapper");
		wrapper.setDisplay(Display.FLEX);

		formLayout = new FlexBoxLayout();
		formLayout.setClassName(CLASS_NAME + "__form-layout");
		formLayout.setFlexDirection(FlexDirection.COLUMN);
		formLayout.setDisplay(Display.FLEX);

		formLayout.add(logoWrapper);


		wrapper.add(formLayout);

		add(wrapper);
	}

	@Override
	public void setParameter(BeforeEvent event, String tokenParameter) {
		if (tokenParameter.length() < ProjectConstants.RECOVERY_TOKEN_LENGTH || tokenParameter.length() > ProjectConstants.RECOVERY_TOKEN_LENGTH) {
			System.err.println("Token length is Invalid");
			showTokenInvalid();
			return;
		}

		checkIfTokenValid(tokenParameter);

		getElement().setAttribute(LumoStyles.THEME, "");
		if (UI.getCurrent() != null) {
			UI.getCurrent().getElement().setAttribute(LumoStyles.THEME, Lumo.DARK);
		}
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
		message.getElement().getStyle().set("font-size", "25px");

		formLayout.add(message);
	}

	private void showPasswordReset() {
		formLayout.add(UIUtils.createH3Label("Reset Password"));

		PasswordField passwordField1 = new PasswordField("New Password");
		passwordField1.setWidthFull();
		passwordField1.setRequired(true);
		passwordField1.setPrefixComponent(VaadinIcon.LOCK.create());

		PasswordField passwordField2 = new PasswordField("Repeat Password");
		passwordField2.setWidthFull();
		passwordField2.setRequired(true);
		passwordField2.setPrefixComponent(VaadinIcon.LOCK.create());

		Button resetPasswordButton = UIUtils.createButton("Reset Password", ButtonVariant.LUMO_PRIMARY);
		resetPasswordButton.setWidthFull();
		resetPasswordButton.addClickListener(resetEvent -> {
			validatePasswords(passwordField1, passwordField2);
		});

		formLayout.add(passwordField1, passwordField2, resetPasswordButton);
		formLayout.setComponentMargin(resetPasswordButton, Top.M);
	}

	private void validatePasswords(PasswordField p1, PasswordField p2) {

		if (p1.getValue() == null) {
			p1.setErrorMessage("Please enter password");
			p1.setInvalid(true);
			return;
		} else {
			if (p1.getValue().length() < ProjectConstants.PASSWORD_MIN_LENGTH) {
				p1.setErrorMessage("Password length must be at least " + ProjectConstants.PASSWORD_MIN_LENGTH + " characters");
				p1.setInvalid(true);
				return;
			}
		}

		if (p2.getValue() == null) {
			p2.setErrorMessage("Please enter password");
			p2.setInvalid(true);
			return;
		} else {
			if (p2.getValue().length() < ProjectConstants.PASSWORD_MIN_LENGTH) {
				p2.setErrorMessage("Password length must be at least " + ProjectConstants.PASSWORD_MIN_LENGTH + " characters");
				p2.setInvalid(true);
				return;
			}
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
		User user = UserFacade.getInstance().getUserByEmail(link.getEmail());

		if (user == null) {
			UIUtils.showNotification("User is NULL", UIUtils.NotificationType.WARNING);
		} else {

			user.setPassword(newPassword);
			if (UserFacade.getInstance().update(user)) {
				UIUtils.showNotification("Password reset successfully", UIUtils.NotificationType.SUCCESS);


				// REMOVE LINK OR SET DELETED?
				if (!RecoveryLinkFacade.getInstance().remove(link)) {
					System.err.println("Error removing RecoveryLink");
				}

				if (UI.getCurrent() != null) {
					UI.getCurrent().navigate("");
				}
			} else {
				UIUtils.showNotification("Password reset Error", UIUtils.NotificationType.ERROR);
			}
		}
	}
}
