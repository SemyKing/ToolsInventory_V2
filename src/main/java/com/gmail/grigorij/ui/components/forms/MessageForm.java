package com.gmail.grigorij.ui.components.forms;

import com.gmail.grigorij.backend.database.entities.Message;
import com.gmail.grigorij.backend.database.entities.Tool;
import com.gmail.grigorij.backend.database.entities.Transaction;
import com.gmail.grigorij.backend.database.enums.operations.Operation;
import com.gmail.grigorij.backend.database.enums.operations.OperationTarget;
import com.gmail.grigorij.backend.database.enums.tools.ToolUsageStatus;
import com.gmail.grigorij.backend.database.facades.InventoryFacade;
import com.gmail.grigorij.backend.database.facades.MessageFacade;
import com.gmail.grigorij.backend.database.facades.TransactionFacade;
import com.gmail.grigorij.ui.components.dialogs.CustomDialog;
import com.gmail.grigorij.ui.utils.UIUtils;
import com.gmail.grigorij.ui.views.app.MessagesView;
import com.gmail.grigorij.utils.authentication.AuthenticationService;
import com.gmail.grigorij.utils.ProjectConstants;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.NotificationVariant;
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

//	private TextField toolNameField;
	private Div actionsDiv;
	private Button showToolDetailsButton;
//	private Button cancelToolButton;
//	private Button cancelToolButton;


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


//		toolNameField = new TextField("Tool");
//		toolNameField.setReadOnly(true);

//		showToolDetailsButton = UIUtils.createButton("Show Tool Details", VaadinIcon.INFO, ButtonVariant.LUMO_PRIMARY);
//		showToolDetailsButton.addClickListener(e -> {
//			constructToolDetailsDialog();
//		});

//		actionsDiv = new Div();
//		actionsDiv.addClassName(ProjectConstants.CONTAINER_ALIGN_CENTER);
//		actionsDiv.add(showToolDetailsButton);
	}

	private void constructForm() {
		setResponsiveSteps(
				new ResponsiveStep("0", 1, ResponsiveStep.LabelsPosition.TOP));
		add(senderField);
		add(subjectField);
		add(messageField);
//		add(actionsDiv);
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
//		if (message.getToolId() == null) {
//			try {
//				actionsDiv.remove(showToolDetailsButton);
//			} catch (Exception ignored) {}
//		} else {
//			Tool tool = InventoryFacade.getInstance().getToolById(message.getToolId());
//
//			if (tool.getCurrentUser() != null) {
//				if (tool.getCurrentUser().getId().equals(AuthenticationService.getCurrentSessionUser().getId())) {
//					try {
//						actionsDiv.remove(showToolDetailsButton);
//					} catch (Exception ignored) {
//					}
//
//					message.setToolId(null);
//
//					MessageFacade.getInstance().update(message);
//				}
//			}
//		}
	}

//	private void constructToolDetailsDialog() {
//		Tool tool = InventoryFacade.getInstance().getToolById(message.getToolId());
//
//		ReadOnlyToolForm toolForm = new ReadOnlyToolForm();
//		toolForm.setTool(tool);
//
//		Div toolActionsDiv = new Div();
//		toolActionsDiv.addClassName(CLASS_NAME + "__tool-actions");
//
//		Button reportToolButton = UIUtils.createButton("Report", VaadinIcon.EXCLAMATION, ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_PRIMARY);
//		reportToolButton.addClickListener(e -> {
//			//TODO IMPLEMENTATION
//		});
//		toolActionsDiv.add(reportToolButton);
//
//		Button cancelToolButton = UIUtils.createButton("Cancel", VaadinIcon.CLOSE_CIRCLE, ButtonVariant.LUMO_CONTRAST, ButtonVariant.LUMO_PRIMARY);
//		cancelToolButton.addClickListener(e -> {
//			cancelTool(tool);
//		});
//		toolActionsDiv.add(cancelToolButton);
//
//		Button takeToolButton = UIUtils.createButton("Take", VaadinIcon.HAND, ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);
//		takeToolButton.addClickListener(e -> {
//			takeTool(tool);
//		});
//		toolActionsDiv.add(takeToolButton);
//
//
//		CustomDialog dialog = new CustomDialog();
//		dialog.setCloseOnOutsideClick(false);
//
//		dialog.setHeader(UIUtils.createH3Label("Tool Details"));
//		dialog.getContent().add(toolForm);
//		dialog.getContent().add(toolActionsDiv);
//		dialog.setConfirmButton(null);
//		dialog.getCancelButton().setText("Close");
//		dialog.closeOnCancel();
//
//		dialog.open();
//	}


	public void setMessage(Message message) {
		if (message == null) {
			this.message = new Message();
		} else {
			this.message = message;
		}

		binder.readBean(this.message);

		initDynamicFormItems();
	}


//	private void cancelTool(Tool tool) {
////		if (message == null || message.getToolId() == null) {
////			UIUtils.showNotification("No tool in message", NotificationVariant.LUMO_PRIMARY);
////			return;
////		}
//
////		Tool tool = InventoryFacade.getInstance().getToolById(message.getToolId());
//
//		tool.setReservedUser(null);
//		tool.setUsageStatus(ToolUsageStatus.FREE);
//
//		if (InventoryFacade.getInstance().update(tool)) {
//
//			Transaction transaction = new Transaction();
//			transaction.setUser(AuthenticationService.getCurrentSessionUser());
//			transaction.setCompany(AuthenticationService.getCurrentSessionUser().getCompany());
//			transaction.setOperation(Operation.CANCEL_RESERVATION_T);
//			transaction.setOperationTarget1(OperationTarget.INVENTORY_TOOL);
//			transaction.setTargetDetails(tool.getName());
//			TransactionFacade.getInstance().insert(transaction);
//
//			UIUtils.showNotification("Tool reservation cancelled", NotificationVariant.LUMO_SUCCESS);
//
//			message.setToolId(null);
//			MessageFacade.getInstance().update(message);
//		} else {
//			UIUtils.showNotification("Tool reservation cancel failed", NotificationVariant.LUMO_ERROR);
//		}
//	}

//	private void takeTool(Tool tool) {
////		if (message == null || message.getToolId() == null) {
////			UIUtils.showNotification("No Tool in message", NotificationVariant.LUMO_PRIMARY);
////			return;
////		}
//
////		Tool tool = InventoryFacade.getInstance().getToolById(message.getToolId());
//
//		tool.setCurrentUser(AuthenticationService.getCurrentSessionUser());
//		tool.setReservedUser(null);
//		tool.setUsageStatus(ToolUsageStatus.IN_USE);
//
//		if (InventoryFacade.getInstance().update(tool)) {
//
//			Transaction transaction = new Transaction();
//			transaction.setUser(AuthenticationService.getCurrentSessionUser());
//			transaction.setCompany(AuthenticationService.getCurrentSessionUser().getCompany());
//			transaction.setOperation(Operation.TAKE);
//			transaction.setOperationTarget1(OperationTarget.INVENTORY_TOOL);
//			transaction.setTargetDetails(tool.getName());
//			TransactionFacade.getInstance().insert(transaction);
//
//			UIUtils.showNotification("Tool taken", NotificationVariant.LUMO_SUCCESS);
//
//			message.setToolId(null);
//			MessageFacade.getInstance().update(message);
//		} else {
//			UIUtils.showNotification("Tool take failed", NotificationVariant.LUMO_ERROR);
//		}
//	}
}
