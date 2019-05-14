package com.gmail.grigorij.ui.authentication;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;


class PasswordRecoveryDialog extends Dialog {

	private static final String CLASS_NAME = "password-recovery-dialog";

	PasswordRecoveryDialog() {
		this.setCloseOnEsc(true);
		this.setCloseOnOutsideClick(true);

		constructPasswordRecoveryDialog();
	}


	private void constructPasswordRecoveryDialog() {
		VerticalLayout verticalLayout = new VerticalLayout();
		verticalLayout.setClassName(CLASS_NAME + "__vertical-layout");

		final Button sendEmailBtn = new Button("Send");
		final EmailField email = new EmailField("E-mail");

		sendEmailBtn.addClickListener(event -> {
			if (email.isInvalid() || email.getValue().isEmpty()) {
				Notification.show("Invalid E-mail pattern, example@email.com", 10000, Notification.Position.TOP_CENTER);
			} else {
				this.close();
				sendPasswordRecoveryEmail(email.getValue());
			}
		});

		verticalLayout.add(new Span(new H2("Forgot Password")));
		verticalLayout.add(new Span("Enter your email to reset your password"));
		verticalLayout.add(email);
		verticalLayout.add(sendEmailBtn);

		add(verticalLayout);
	}

	private void sendPasswordRecoveryEmail(String emailAddress) {






		//SEND EMAIL






		Notification notification = new Notification();

		Button closeBtn = new Button("Close");
		closeBtn.addClickListener(event -> notification.close());

		notification.add(new Label("Password recovery link has been sent to the E-mail that you provided."));
		notification.add(closeBtn);
		//5 min duration
		notification.setDuration(300000);
		notification.setPosition(Notification.Position.TOP_CENTER);
		notification.open();
	}
}
