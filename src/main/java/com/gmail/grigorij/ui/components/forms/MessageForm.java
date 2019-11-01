package com.gmail.grigorij.ui.components.forms;

import com.gmail.grigorij.backend.database.entities.InventoryItem;
import com.gmail.grigorij.backend.database.entities.Message;
import com.gmail.grigorij.backend.database.entities.Transaction;
import com.gmail.grigorij.backend.database.enums.inventory.ToolUsageStatus;
import com.gmail.grigorij.backend.database.enums.operations.Operation;
import com.gmail.grigorij.backend.database.enums.operations.OperationTarget;
import com.gmail.grigorij.backend.database.facades.InventoryFacade;
import com.gmail.grigorij.backend.database.facades.MessageFacade;
import com.gmail.grigorij.backend.database.facades.TransactionFacade;
import com.gmail.grigorij.ui.application.views.MessagesView;
import com.gmail.grigorij.ui.utils.UIUtils;
import com.gmail.grigorij.utils.AuthenticationService;
import com.gmail.grigorij.utils.ProjectConstants;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ReadOnlyHasValue;


public class MessageForm extends FormLayout {

	private final String CLASS_NAME = "form";
	private final MessagesView messages;

	private Binder<Message> binder;
	private Message message;

	// FORM ITEMS
	private TextField senderField;
	private TextField subjectField;
	private TextArea messageField;

	private TextField toolNameField;
	private Div actionsDiv;
	private Button takeToolButton;
	private Button cancelToolButton;


	// BINDER ITEMS
	private ReadOnlyHasValue<Message> sender;
	private ReadOnlyHasValue<Message> subject;
	private ReadOnlyHasValue<Message> text;


	public MessageForm(MessagesView messages) {
		this.messages = messages;

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


		toolNameField = new TextField("Tool");
		toolNameField.setReadOnly(true);

		cancelToolButton = UIUtils.createButton("Cancel", VaadinIcon.CLOSE_CIRCLE, ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);
		cancelToolButton.addClickListener(e -> {
			cancelTool();

			messages.closeDetails();
		});

		takeToolButton = UIUtils.createButton("Take", VaadinIcon.HAND, ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);
		takeToolButton.addClickListener(e -> {
			takeTool();

			messages.closeDetails();
		});

		actionsDiv = new Div();
		actionsDiv.addClassName(ProjectConstants.CONTAINER_SPACE_BETWEEN);
		actionsDiv.add(cancelToolButton, takeToolButton);
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
		try {
			remove(toolNameField);
			remove(actionsDiv);
		} catch (Exception ignored) {

		}

		if (message.getToolId() != null) {
			InventoryItem tool = InventoryFacade.getInstance().getById(message.getToolId());

			toolNameField.setValue(tool.getName());

			add(toolNameField);
			add(actionsDiv);
		}
	}


	public void setMessage(Message message) {
		if (message == null) {
			this.message = new Message();
		} else {
			this.message = message;
		}

		binder.readBean(this.message);

		initDynamicFormItems();
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


	private void cancelTool() {
		if (message == null || message.getToolId() == null) {
			UIUtils.showNotification("No Tool in message", UIUtils.NotificationType.INFO);
			return;
		}

		InventoryItem tool = InventoryFacade.getInstance().getById(message.getToolId());

		tool.setReservedUser(null);
		tool.setUsageStatus(ToolUsageStatus.FREE);

		if (InventoryFacade.getInstance().update(tool)) {

			Transaction transaction = new Transaction();
			transaction.setUser(AuthenticationService.getCurrentSessionUser());
			transaction.setCompany(AuthenticationService.getCurrentSessionUser().getCompany());
			transaction.setOperation(Operation.CANCEL_RESERVATION);
			transaction.setOperationTarget1(OperationTarget.INVENTORY_TOOL);
			transaction.setTargetDetails(tool.getName());
			TransactionFacade.getInstance().insert(transaction);

			UIUtils.showNotification("Tool reservation cancelled", UIUtils.NotificationType.SUCCESS);

			message.setToolId(null);
			MessageFacade.getInstance().update(message);
		} else {
			UIUtils.showNotification("Tool reservation cancel failed", UIUtils.NotificationType.ERROR);
		}
	}

	private void takeTool() {
		if (message == null || message.getToolId() == null) {
			UIUtils.showNotification("No Tool in message", UIUtils.NotificationType.INFO);
			return;
		}

		InventoryItem tool = InventoryFacade.getInstance().getById(message.getToolId());

		tool.setCurrentUser(AuthenticationService.getCurrentSessionUser());
		tool.setReservedUser(null);
		tool.setUsageStatus(ToolUsageStatus.IN_USE);

		if (InventoryFacade.getInstance().update(tool)) {

			Transaction transaction = new Transaction();
			transaction.setUser(AuthenticationService.getCurrentSessionUser());
			transaction.setCompany(AuthenticationService.getCurrentSessionUser().getCompany());
			transaction.setOperation(Operation.TAKE);
			transaction.setOperationTarget1(OperationTarget.INVENTORY_TOOL);
			transaction.setTargetDetails(tool.getName());
			TransactionFacade.getInstance().insert(transaction);

			UIUtils.showNotification("Tool taken", UIUtils.NotificationType.SUCCESS);

			message.setToolId(null);
			MessageFacade.getInstance().update(message);
		} else {
			UIUtils.showNotification("Tool take failed", UIUtils.NotificationType.ERROR);
		}
	}
}
