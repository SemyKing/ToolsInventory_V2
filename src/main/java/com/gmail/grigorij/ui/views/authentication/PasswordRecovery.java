package com.gmail.grigorij.ui.views.authentication;

import com.gmail.grigorij.backend.entities.user.Person;
import com.gmail.grigorij.ui.utils.components.CustomDialog;
import com.gmail.grigorij.ui.utils.UIUtils;
import com.gmail.grigorij.utils.OperationStatus;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.validator.EmailValidator;


class PasswordRecovery {

	private static final String CLASS_NAME = "password-recovery-dialog";
	private OperationStatus operationStatus;

	PasswordRecovery(OperationStatus operationStatus) {
		this.operationStatus = operationStatus;
		constructPasswordRecoveryDialog();
	}

	private void constructPasswordRecoveryDialog() {
		EmailField emailField = new EmailField("E-mail");
		UIUtils.setWidth("100%", emailField);

		Binder<Person> binder = new Binder<>();
		binder.forField(emailField)
				.withValidator(new EmailValidator("This doesn't look like a valid email address"))
				.bind(Person::getEmail, Person::setEmail);

		CustomDialog dialog = new CustomDialog();
		dialog.setHeader(UIUtils.createH2Label("Password recovery"));
		dialog.setContent(new Span("Enter your email to reset your password"), emailField);

		Button send = UIUtils.createButton("Send", ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_SUCCESS);
		dialog.setConfirmButton(send);
		dialog.getConfirmButton().addClickListener(e -> {
			binder.validate();
			if (binder.isValid()) {
				constructRecoveryEmail(emailField.getValue());
				dialog.close();
				operationStatus.onSuccess("Password recovery dialog closed from button");
			}
		});

		dialog.addDetachListener((DetachEvent event) -> {
			operationStatus.onSuccess("Password recovery dialog closed from detach event");
		});
		dialog.open();
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
