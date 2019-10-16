package com.gmail.grigorij.ui.components.dialogs;

import com.gmail.grigorij.backend.database.facades.RecoveryLinkFacade;
import com.gmail.grigorij.backend.database.facades.TransactionFacade;
import com.gmail.grigorij.backend.database.facades.UserFacade;
import com.gmail.grigorij.backend.embeddable.Person;
import com.gmail.grigorij.backend.entities.recoverylink.RecoveryLink;
import com.gmail.grigorij.backend.entities.transaction.Transaction;
import com.gmail.grigorij.backend.entities.user.User;
import com.gmail.grigorij.backend.enums.transactions.TransactionTarget;
import com.gmail.grigorij.backend.enums.transactions.TransactionType;
import com.gmail.grigorij.ui.components.dialogs.CustomDialog;
import com.gmail.grigorij.ui.components.layouts.FlexBoxLayout;
import com.gmail.grigorij.ui.utils.UIUtils;
import com.gmail.grigorij.ui.utils.css.size.All;
import com.gmail.grigorij.ui.utils.css.size.Top;
import com.gmail.grigorij.utils.OperationStatus;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.html.Span;
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
		emailField.setErrorMessage("Please enter a valid email address");

		//TODO: REMOVE AT PRODUCTION
		emailField.setValue("gs@mail.com");

		content.add(emailField);
		return content;
	}

	private void constructBinder() {
		binder = new Binder<>();
		binder.forField(emailField)
				.asRequired()
				.withValidator(new EmailValidator("This doesn't look like a valid email address"))
				.bind(Person::getEmail, Person::setEmail);
	}


	private void sendOnClick() {
		binder.validate();

		if (binder.isValid()) {
			generateRecoveryLink(emailField.getValue());

			constructRecoveryEmail(emailField.getValue());

			this.close();
		}
	}




	private void generateRecoveryLink(String emailAddress) {
		User user = UserFacade.getInstance().getUserByEmail(emailAddress);

		if (user.isDeleted()) {
			UIUtils.showNotification("Your credentials have expired", UIUtils.NotificationType.INFO);
			return;
		}

		RecoveryLink link = RecoveryLink.generateRecoveryLink();
		link.setEmail(emailAddress);

		RecoveryLinkFacade.getInstance().insert(link);

		Transaction tr = new Transaction();
		tr.setTransactionOperation(TransactionType.EDIT);
		tr.setTransactionTarget(TransactionTarget.USER);
		tr.setWhoDid(user);
		tr.setAdditionalInfo("User has requested password reset link. Email: " + emailAddress);
		TransactionFacade.getInstance().insert(tr);

		UIUtils.showNotification("https://localhost:8443/reset-password/" + link.getToken(), UIUtils.NotificationType.SUCCESS, 0);
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
