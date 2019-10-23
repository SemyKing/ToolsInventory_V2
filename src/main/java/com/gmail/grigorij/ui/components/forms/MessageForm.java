package com.gmail.grigorij.ui.components.forms;

import com.gmail.grigorij.backend.database.entities.Message;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ReadOnlyHasValue;


public class MessageForm extends FormLayout {

	private final String CLASS_NAME = "form";

	private Binder<Message> binder;
	private Message message;

	// FORM ITEMS
	private TextField senderField;
	private TextField subjectField;
	private TextArea messageField;


	// BINDER ITEMS
	private ReadOnlyHasValue<Message> sender;
	private ReadOnlyHasValue<Message> subject;
	private ReadOnlyHasValue<Message> text;


	public MessageForm() {
		addClassName(CLASS_NAME);

		constructFormItems();

		constructForm();

		constructBinder();
	}


	private void constructFormItems() {
		senderField = new TextField("Sender");
		senderField.setReadOnly(true);
		sender = new ReadOnlyHasValue<>(msg -> senderField.setValue(msg.getSenderUser() == null ? msg.getSenderString() : msg.getSenderUser().getFullName()));


		subjectField = new TextField("Subject");
		subjectField.setReadOnly(true);
		subject = new ReadOnlyHasValue<>(msg -> subjectField.setValue(msg.getSubject()));


		messageField = new TextArea("Message");
		messageField.setReadOnly(true);
		text = new ReadOnlyHasValue<>(msg -> messageField.setValue(msg.getText()));
	}

	private void constructForm() {
		setResponsiveSteps(
				new ResponsiveStep("0", 1, ResponsiveStep.LabelsPosition.TOP));
		add(senderField);
		add(subjectField);
		add(messageField);
	}

	private void constructBinder() {
		binder = new Binder<>(Message.class);
		binder.forField(sender)
				.bind(msg -> msg, null);
		binder.forField(subject)
				.bind(msg -> msg, null);
		binder.forField(text)
				.bind(msg -> msg, null);
	}


	private void initDynamicFormItems() {
	}


	public void setMessage(Message message) {
		if (message == null) {
			this.message = new Message();
		} else {
			this.message = message;
		}

		initDynamicFormItems();

		binder.readBean(this.message);
	}

//	public Message getMessage() {
//		try {
//			binder.validate();
//
//			if (binder.isValid()) {
//
//				binder.writeBean(message);
//
//				return message;
//			}
//		} catch (ValidationException e) {
//			e.printStackTrace();
//			return null;
//		}
//		return null;
//	}
}
