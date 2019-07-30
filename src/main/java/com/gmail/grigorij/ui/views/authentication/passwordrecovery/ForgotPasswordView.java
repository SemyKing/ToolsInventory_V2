package com.gmail.grigorij.ui.views.authentication.passwordrecovery;

import com.gmail.grigorij.backend.database.facades.RecoveryLinkFacade;
import com.gmail.grigorij.backend.database.facades.UserFacade;
import com.gmail.grigorij.backend.entities.embeddable.Person;
import com.gmail.grigorij.backend.entities.recoverylink.RecoveryLink;
import com.gmail.grigorij.backend.entities.user.User;
import com.gmail.grigorij.ui.utils.components.CustomDialog;
import com.gmail.grigorij.ui.utils.UIUtils;
import com.gmail.grigorij.ui.utils.css.size.Top;
import com.gmail.grigorij.utils.OperationStatus;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.validator.EmailValidator;


public class ForgotPasswordView {

	private OperationStatus operationStatus;

	public ForgotPasswordView(OperationStatus operationStatus) {
		this.operationStatus = operationStatus;
		constructPasswordRecoveryDialog();
	}

	private void constructPasswordRecoveryDialog() {
		EmailField emailField = new EmailField("E-mail");
		emailField.setMinWidth("400px");
		emailField.setValue("gs@mail.com");

		Binder<Person> binder = new Binder<>();
		binder.forField(emailField)
				.withValidator(new EmailValidator("This doesn't look like a valid email address"))
				.bind(Person::getEmail, Person::setEmail);

		CustomDialog dialog = new CustomDialog();
		dialog.setHeader(UIUtils.createH3Label("Password Recovery"));
		dialog.setContent(new Span("Enter your email to reset your password"), emailField);
		dialog.getContent().setPadding(Top.S);

		dialog.getCancelButton().addClickListener(e -> dialog.close());

		dialog.getConfirmButton().setText("Send");
		dialog.getConfirmButton().addClickListener(e -> {
			binder.validate();
			if (binder.isValid()) {

				generateRecoveryLink(emailField.getValue());

				constructRecoveryEmail(emailField.getValue());

				dialog.close();
				operationStatus.onSuccess("Password recovery dialog closed from button", null);
			}
		});

		dialog.addDetachListener((DetachEvent event) -> {
			operationStatus.onSuccess("Password recovery dialog closed from detach event", null);
		});
		dialog.open();
	}

	private void generateRecoveryLink(String emailAddress) {
		User user = UserFacade.getInstance().getUserByEmail(emailAddress);

		if (user == null) {
			UIUtils.showNotification("Email address not found", UIUtils.NotificationType.INFO);
			return;
		}

		if (user.isDeleted()) {
			UIUtils.showNotification("Your credentials have expired", UIUtils.NotificationType.INFO);
			return;
		}

		RecoveryLink link = RecoveryLink.generateRecoveryLink();
		link.setEmail(emailAddress);

		RecoveryLinkFacade.getInstance().insert(link);

		UIUtils.showNotification("/reset-password/" + link.getToken(), UIUtils.NotificationType.ERROR);
	}

	private void constructRecoveryEmail(String emailAddress) {

		//CONSTRUCT EMAIL

		sendPasswordRecoveryEmail(emailAddress);
	}

	private void sendPasswordRecoveryEmail(String emailAddress) {

		//SEND EMAIL

		UIUtils.showNotification("Password recovery link has been sent to: " + emailAddress, UIUtils.NotificationType.INFO);
	}
}
