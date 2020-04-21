package com.gmail.grigorij.ui.components.dialogs.message;

import com.gmail.grigorij.backend.database.entities.Company;
import com.gmail.grigorij.backend.database.entities.Message;
import com.gmail.grigorij.backend.database.entities.User;
import com.gmail.grigorij.backend.database.enums.permissions.PermissionLevel;
import com.gmail.grigorij.backend.database.facades.CompanyFacade;
import com.gmail.grigorij.backend.database.facades.MessageFacade;
import com.gmail.grigorij.backend.database.facades.UserFacade;
import com.gmail.grigorij.ui.components.dialogs.CustomDialog;
import com.gmail.grigorij.ui.utils.UIUtils;
import com.gmail.grigorij.utils.authentication.AuthenticationService;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;

import java.util.ArrayList;
import java.util.List;

@StyleSheet("context://styles/views/message.css")
public class MessageView extends Div {

	private static final String CLASS_NAME = "message-view";

	private ComboBox<User> recipientUsersComboBox;
	private ComboBox<Company> recipientCompanyComboBox;

	private TextArea subjectField;
	private TextArea messageField;

	private final Message replyMessage;
	private List<Message> messageList;

	private boolean systemAdmin;


	public MessageView(Message replyMessage) {
		this.replyMessage = replyMessage;

		addClassName(CLASS_NAME);

		systemAdmin = AuthenticationService.getCurrentSessionUser().getPermissionLevel().equalsTo(PermissionLevel.SYSTEM_ADMIN);

		add(constructContent());
	}


	private Div constructContent() {
		Div content = new Div();
		content.addClassName(CLASS_NAME + "__content");


		if (replyMessage != null) {
			TextField recipientField = new TextField("Recipient");
			recipientField.setReadOnly(true);
			recipientField.setValue(replyMessage.getSenderUser().getFullName());

			content.add(recipientField);
		} else {
			recipientUsersComboBox = new ComboBox<>("Recipient");
			recipientUsersComboBox.setItemLabelGenerator(User::getFullName);
			recipientUsersComboBox.setRequired(true);
			if (systemAdmin) {
				recipientUsersComboBox.setItems(UserFacade.getInstance().getAllActiveUsers());
			} else {
				recipientUsersComboBox.setItems(UserFacade.getInstance().getAllActiveUsersInCompany(AuthenticationService.getCurrentSessionUser().getCompany().getId()));
			}

			content.add(recipientUsersComboBox);

			if (systemAdmin) {
				recipientCompanyComboBox = new ComboBox<>("Recipients (All in Company)");
				recipientCompanyComboBox.setClearButtonVisible(true);
				recipientCompanyComboBox.setItemLabelGenerator(Company::getName);
				recipientCompanyComboBox.setItems(CompanyFacade.getInstance().getAllActiveCompanies());
				recipientCompanyComboBox.addValueChangeListener(e -> {
					if (e.getValue() != null) {
						recipientUsersComboBox.setRequired(false);
						recipientUsersComboBox.setInvalid(false);
					} else {
						recipientUsersComboBox.setRequired(true);
					}
				});

				content.add(recipientCompanyComboBox);
			}
		}


		subjectField = new TextArea("Subject");
		subjectField.setRequired(true);
		if (replyMessage != null) {
			subjectField.setValue("RE: " + replyMessage.getSubject());
		}

		content.add(subjectField);


		messageField = new TextArea("Message");
		messageField.setRequired(true);

		content.add(messageField);

		return content;
	}


	private boolean isValid() {
		if (recipientUsersComboBox != null) {
			if (recipientCompanyComboBox != null && recipientCompanyComboBox.getValue() == null) {
				if (recipientUsersComboBox.getValue() == null || recipientUsersComboBox.isInvalid()) {
					recipientUsersComboBox.setInvalid(true);
					UIUtils.showNotification("Recipient is required", NotificationVariant.LUMO_PRIMARY);
					return false;
				}
			}
		}

		if (subjectField.isEmpty() || subjectField.isInvalid()) {
			subjectField.setInvalid(true);
			UIUtils.showNotification("Subject is required", NotificationVariant.LUMO_PRIMARY);
			return false;
		}

		if (messageField.isEmpty() || messageField.isInvalid()) {
			messageField.setInvalid(true);
			UIUtils.showNotification("Message is required", NotificationVariant.LUMO_PRIMARY);
			return false;
		}

		constructMessages();

		return true;
	}

	private void constructMessages() {
		User currentUser = AuthenticationService.getCurrentSessionUser();

		Message message = new Message();

		if (replyMessage != null) {

			message.setSenderUser(currentUser);
			message.setRecipientId(replyMessage.getSenderUser().getId());
			message.setSubject(subjectField.getValue());
			message.setText(messageField.getValue());

			messageList.add(message);
		} else {
			for (User recipient : getAllRecipients()) {

				message = new Message();
				message.setSenderUser(currentUser);
				message.setRecipientId(recipient.getId());
				message.setSubject(subjectField.getValue());
				message.setText(messageField.getValue());

				messageList.add(message);
			}
		}
	}

	private List<User> getAllRecipients() {
		List<User> recipients = new ArrayList<>();

		if (recipientUsersComboBox != null) {
			if (recipientUsersComboBox.getValue() != null) {
				recipients.add(recipientUsersComboBox.getValue());
			}
		}

		if (recipientCompanyComboBox != null) {
			if (recipientCompanyComboBox.getValue() != null) {
				recipients.addAll(UserFacade.getInstance().getAllActiveUsersInCompany(recipientCompanyComboBox.getValue().getId()));
			}
		}

		return recipients;
	}


	public List<Message> getMessageList() {
		messageList = new ArrayList<>();

		if (isValid()) {
			return messageList;
		} else {
			return null;
		}
	}
}
