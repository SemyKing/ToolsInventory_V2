package com.gmail.grigorij.ui.components.dialogs.password;

import com.gmail.grigorij.backend.database.entities.RecoveryLink;
import com.gmail.grigorij.backend.database.entities.Transaction;
import com.gmail.grigorij.backend.database.entities.User;
import com.gmail.grigorij.backend.database.entities.embeddable.Person;
import com.gmail.grigorij.backend.database.enums.operations.Operation;
import com.gmail.grigorij.backend.database.enums.operations.OperationTarget;
import com.gmail.grigorij.backend.database.facades.RecoveryLinkFacade;
import com.gmail.grigorij.backend.database.facades.TransactionFacade;
import com.gmail.grigorij.backend.database.facades.UserFacade;
import com.gmail.grigorij.ui.components.FlexBoxLayout;
import com.gmail.grigorij.ui.components.dialogs.CustomDialog;
import com.gmail.grigorij.ui.utils.UIUtils;
import com.gmail.grigorij.utils.ProjectConstants;
import com.gmail.grigorij.utils.email.Email;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.validator.EmailValidator;


public class ForgotPasswordDialog extends CustomDialog {

	private final static String CLASS_NAME = "forgot-password-dialog";

	private FlexBoxLayout content;

	private Binder<Person> binder;
	private EmailField emailField;

	public ForgotPasswordDialog() {
		getHeader().add(UIUtils.createH3Label("Forgot Password"));
		getContent().add(constructContent());
		setWidth("auto");

		closeOnCancel();

		getConfirmButton().setText("Send");
		getConfirmButton().addClickListener(e -> {
			sendOnClick();
		});

		constructBinder();
	}

	private FlexBoxLayout constructContent() {
		content = new FlexBoxLayout();
		content.addClassName(CLASS_NAME + "__content");

		emailField = new EmailField("E-mail");
		emailField.setMinWidth("400px");
		emailField.setClearButtonVisible(true);
		emailField.setErrorMessage("Invalid email address");
		emailField.focus();

		content.add(emailField);
		return content;
	}

	private void constructBinder() {
		binder = new Binder<>();
		binder.forField(emailField)
				.asRequired()
				.withValidator(new EmailValidator("Invalid email address"))
				.bind(Person::getEmail, Person::setEmail);
	}


	private void sendOnClick() {
		binder.validate();

		if (binder.isValid()) {
			generateRecoveryLink(emailField.getValue());

			this.close();
		}
	}



	private void generateRecoveryLink(String emailAddress) {
		User user = UserFacade.getInstance().getUserByEmail(emailAddress);

		if (user == null) {
			UIUtils.showNotification("User with given email not found", NotificationVariant.LUMO_PRIMARY);
			return;
		}

		if (user.isDeleted()) {
			UIUtils.showNotification("Your credentials have expired", NotificationVariant.LUMO_PRIMARY);
			return;
		}

		RecoveryLink link = new RecoveryLink();
		link.setUser(user);
		RecoveryLinkFacade.getInstance().insert(link);

		Transaction transaction = new Transaction();
		transaction.setUser(user);
		transaction.setCompany(user.getCompany());
		transaction.setOperation(Operation.REQUEST_T);
		transaction.setOperationTarget1(OperationTarget.PASSWORD_RESET_EMAIL_T);
		TransactionFacade.getInstance().insert(transaction);


		Email email = new Email();
		if (email.constructAndSendMessage(emailAddress,
				ProjectConstants.PROJECT_URL + "/" + ProjectConstants.RESET_PASSWORD_URL + "/" + link.getToken())) {

			UIUtils.showNotification("Password reset link has been sent to email you provided", NotificationVariant.LUMO_SUCCESS, 10000);
		} else {
			UIUtils.showNotification("Password reset link sending error", NotificationVariant.LUMO_ERROR);
		}
	}
}
