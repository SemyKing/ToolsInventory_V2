//package com.gmail.grigorij.ui.application.views;
//
//import com.gmail.grigorij.backend.database.facades.InventoryFacade;
//import com.gmail.grigorij.backend.database.facades.MessageFacade;
//import com.gmail.grigorij.backend.database.facades.TransactionFacade;
//import com.gmail.grigorij.backend.database.facades.UserFacade;
//import com.gmail.grigorij.backend.entities.inventory.InventoryItem;
//import com.gmail.grigorij.backend.entities.message.Message;
//import com.gmail.grigorij.backend.entities.transaction.Transaction;
//import com.gmail.grigorij.backend.entities.user.User;
//import com.gmail.grigorij.backend.enums.MessageType;
//import com.gmail.grigorij.backend.enums.inventory.ToolUsageStatus;
//import com.gmail.grigorij.backend.enums.operations.Operation;
//import com.gmail.grigorij.backend.enums.operations.OperationTarget;
//import com.gmail.grigorij.backend.enums.permissions.PermissionLevel;
//import com.gmail.grigorij.ui.components.dialogs.ConfirmDialog;
//import com.gmail.grigorij.ui.components.dialogs.CustomDialog;
//import com.gmail.grigorij.ui.components.layouts.FlexBoxLayout;
//import com.gmail.grigorij.ui.components.layouts.ViewFrame;
//import com.gmail.grigorij.ui.utils.UIUtils;
//import com.gmail.grigorij.ui.utils.css.Display;
//import com.gmail.grigorij.ui.utils.css.FlexDirection;
//import com.gmail.grigorij.ui.utils.css.size.*;
//import com.gmail.grigorij.utils.AuthenticationService;
//import com.gmail.grigorij.utils.Broadcaster;
//import com.gmail.grigorij.utils.DateConverter;
//import com.vaadin.flow.component.button.Button;
//import com.vaadin.flow.component.button.ButtonVariant;
//import com.vaadin.flow.component.checkbox.Checkbox;
//import com.vaadin.flow.component.combobox.ComboBox;
//import com.vaadin.flow.component.datepicker.DatePicker;
//import com.vaadin.flow.component.dependency.CssImport;
//import com.vaadin.flow.component.html.Label;
//import com.vaadin.flow.component.html.Span;
//import com.vaadin.flow.component.icon.VaadinIcon;
//import com.vaadin.flow.component.orderedlayout.FlexComponent;
//import com.vaadin.flow.component.textfield.TextArea;
//import com.vaadin.flow.component.textfield.TextField;
//import com.vaadin.flow.router.PageTitle;
//
//import java.time.LocalDate;
//import java.util.Comparator;
//import java.util.List;
//import java.util.Locale;
//
//
///**
// * User receives Message when:
// * - The tool that user has reserved becomes Free
// * - Other user sends a simple Message
// */
//
//@PageTitle("Messages")
//@CssImport("./styles/views/messages.css")
//public class Messages_backup extends ViewFrame {
//
//	private static final String CLASS_NAME = "messages_view";
//	private static final String READ = "read";
//
//	private List<Message> messages;
//	private FlexBoxLayout contentLayout;
//
//	private DatePicker dateStartField, dateEndField;
//
//	private Label unreadMessagesLabel;
//	private boolean showReadMessages = true;
//
//
//	public Messages_backup() {
//		setViewContent(createContent());
//	}
//
//
//	private FlexBoxLayout createContent() {
//		//HEADER
//		FlexBoxLayout header = new FlexBoxLayout();
//		header.setClassName(CLASS_NAME + "__header");
//		header.setFlexDirection(FlexDirection.ROW);
//		header.setAlignItems(FlexComponent.Alignment.BASELINE);
//		header.setWidthFull();
//
//		FlexBoxLayout datesLayout = new FlexBoxLayout();
//		datesLayout.addClassName(CLASS_NAME + "__dates");
//		datesLayout.setFlexDirection(FlexDirection.ROW);
//		datesLayout.setAlignItems(FlexComponent.Alignment.BASELINE);
//
//		dateStartField = new DatePicker();
//		dateStartField.setLabel("Start Date");
//		dateStartField.setPlaceholder("Start Date");
//		dateStartField.setLocale(new Locale("fi"));
//		dateStartField.setValue(LocalDate.now());
//
//		datesLayout.add(dateStartField);
//
//		dateEndField = new DatePicker();
//		dateEndField.setLabel("End Date");
//		dateEndField.setPlaceholder("End Date");
//		dateEndField.setLocale(new Locale("fi"));
//		dateEndField.setValue(LocalDate.now());
//
//		datesLayout.add(dateEndField);
//		datesLayout.setComponentMargin(dateEndField, Horizontal.S);
//
//		Button applyDates = UIUtils.createButton("Apply", ButtonVariant.LUMO_PRIMARY);
//		applyDates.addClickListener(e -> {
//			getMessagesBetweenDates();
//			showMessages();
//		});
//
//		datesLayout.add(applyDates);
//
//
//		FlexBoxLayout datesActionsLayout = new FlexBoxLayout();
//		datesActionsLayout.addClassName(CLASS_NAME + "__dates_actions");
//		datesActionsLayout.setFlexDirection(FlexDirection.ROW);
//		datesActionsLayout.setAlignItems(FlexComponent.Alignment.BASELINE);
//
//
//		Button newMessage = UIUtils.createIconButton("Message", VaadinIcon.ENVELOPE, ButtonVariant.LUMO_CONTRAST);
//		newMessage.setClassName(CLASS_NAME + "__new-message-button");
//		newMessage.addClickListener(e -> {
//			constructNewMessageDialog(false, null);
//		});
//		datesActionsLayout.add(newMessage);
//		datesActionsLayout.setComponentMargin(newMessage, Horizontal.S);
//
//		Checkbox showReadCheckbox = new Checkbox("Show Read Messages");
//		showReadCheckbox.setClassName(CLASS_NAME + "__show-read-messages-checkbox");
//		showReadCheckbox.setValue(true);
//		showReadCheckbox.addClickListener(e -> {
//			showReadMessages = showReadCheckbox.getValue();
//			showMessages();
//		});
//		datesActionsLayout.add(showReadCheckbox);
//		datesActionsLayout.setComponentMargin(showReadCheckbox, Left.S);
//
//		header.add(datesLayout);
//		header.add(datesActionsLayout);
//
//
//		//CONTENT
//		contentLayout = new FlexBoxLayout();
//		contentLayout.setSizeFull();
//		contentLayout.addClassName(CLASS_NAME + "__content");
//		contentLayout.setFlexDirection(FlexDirection.COLUMN);
//		contentLayout.setAlignItems(FlexComponent.Alignment.CENTER);
//
//		unreadMessagesLabel = UIUtils.createH4Label("");
//		unreadMessagesLabel.addClassName(CLASS_NAME + "__unread_label");
//
//		FlexBoxLayout contentWrapperLayout = new FlexBoxLayout();
//		contentWrapperLayout.addClassName(CLASS_NAME + "__wrapper");
//		contentWrapperLayout.setFlexDirection(FlexDirection.COLUMN);
//
//		contentWrapperLayout.add(header);
//		contentWrapperLayout.add(unreadMessagesLabel);
//		contentWrapperLayout.add(contentLayout);
//
//		getMessagesBetweenDates();
//
//		showMessages();
//
//		return contentWrapperLayout;
//	}
//
//	private void getMessagesBetweenDates() {
//		//Handle errors
//		if (dateStartField.isInvalid()) {
//			UIUtils.showNotification("Invalid Start Date", UIUtils.NotificationType.INFO);
//			dateStartField.focus();
//			return;
//		}
//
//		if (dateEndField.isInvalid()) {
//			UIUtils.showNotification("Invalid End Date", UIUtils.NotificationType.INFO);
//			dateEndField.focus();
//			return;
//		}
//
//		if (dateStartField.getValue().isAfter(dateEndField.getValue())) {
//			UIUtils.showNotification("Start Date cannot be after End Date", UIUtils.NotificationType.INFO);
//			return;
//		}
//
//		messages = MessageFacade.getInstance().getAllMessagesBetweenDatesByUser(dateStartField.getValue(), dateEndField.getValue(), AuthenticationService.getCurrentSessionUser().getId());
//
//		messages.sort(Comparator.comparing(Message::getDate).reversed());
//
//		handleUnreadMessagesCount();
//	}
//
//	private void showMessages() {
//		contentLayout.removeAll();
//
//		for (Message message : messages) {
//			if (message.isMessageRead()) {
//				if (showReadMessages) {
//					contentLayout.add(constructMessageLayout(message));
//				}
//			} else {
//				contentLayout.add(constructMessageLayout(message));
//			}
//		}
//	}
//
//	private void handleUnreadMessagesCount() {
//		int unreadMessagesCount = 0;
//
//		for (Message message : messages) {
//			if (!message.isMessageRead()) {
//				unreadMessagesCount++;
//			}
//		}
//		unreadMessagesLabel.setText(unreadMessagesCount + " unread message(s)");
//	}
//
//
//	private FlexBoxLayout constructMessageLayout(Message message) {
//		FlexBoxLayout messageWrapper = new FlexBoxLayout();
//		messageWrapper.addClassName(CLASS_NAME + "__message-wrapper");
//		messageWrapper.setFlexDirection(FlexDirection.ROW);
//		messageWrapper.setDisplay(Display.FLEX);
//		messageWrapper.setMargin(Top.M);
//		messageWrapper.setPadding(Right.XS);
//
//		messageWrapper.getElement().setAttribute(READ, message.isMessageRead());
//
//
//		FlexBoxLayout messageContentLayout = new FlexBoxLayout();
//		messageContentLayout.addClassName(CLASS_NAME + "__message-content");
//		messageContentLayout.setFlexDirection(FlexDirection.COLUMN);
//		messageContentLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
//		messageContentLayout.setMargin(Horizontal.XS);
//		messageContentLayout.setMargin(Vertical.XS);
//		messageContentLayout.setWidthFull();
//
//		messageContentLayout.add(UIUtils.createH4Label(message.getMessageHeader()));
//		messageContentLayout.add(new Span(message.getMessageText()));
//
//		messageWrapper.add(messageContentLayout);
//
//		FlexBoxLayout messageFooterLayout = new FlexBoxLayout();
//		messageFooterLayout.addClassName(CLASS_NAME + "__message_footer");
//		messageFooterLayout.setFlexDirection(FlexDirection.ROW);
//
//		User senderUser = UserFacade.getInstance().getUserById(message.getSenderId());
//		String sender = (senderUser == null) ? message.getSender() : senderUser.getFullName();
//
//		messageFooterLayout.add(UIUtils.createH6Label("From: " + sender));
//
//		Label dateLabel = UIUtils.createH6Label("Sent: " + DateConverter.dateToStringWithTime(message.getDate()));
//		messageFooterLayout.setComponentMargin(dateLabel, Left.AUTO);
//		messageFooterLayout.add(dateLabel);
//
//		messageContentLayout.add(messageFooterLayout);
//
//		if (!message.isMessageRead()) {
//
//			FlexBoxLayout messageButtonsLayout = new FlexBoxLayout();
//			messageButtonsLayout.addClassName(CLASS_NAME + "__message-buttons");
//			messageButtonsLayout.setFlexDirection(FlexDirection.COLUMN);
//			messageButtonsLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
//
//			Button dismissButton = UIUtils.createSmallButton("Dismiss", VaadinIcon.CLOSE, ButtonVariant.LUMO_CONTRAST);
//			dismissButton.addClickListener(e -> {
//				confirmMarkMessageAsRead(message, messageWrapper);
//			});
//			messageButtonsLayout.add(dismissButton);
//
//			if (message.getMessageType().equals(MessageType.SIMPLE_MESSAGE) || message.getMessageType().equals(MessageType.TOOL_REQUEST)) {
//				Button replyButton = UIUtils.createSmallButton("Reply", VaadinIcon.REPLY, ButtonVariant.LUMO_PRIMARY);
//				replyButton.addClickListener(e -> {
//					constructNewMessageDialog(true, message);
//
//					confirmMarkMessageAsRead(message, messageWrapper);
//				});
//				messageButtonsLayout.add(replyButton);
//			}
//
//			if (message.getMessageType().equals(MessageType.TOOL_FREE)) {
//				Button takeToolButton = UIUtils.createSmallButton("Take Tool", VaadinIcon.HAND, ButtonVariant.LUMO_SUCCESS, ButtonVariant.LUMO_PRIMARY);
//				takeToolButton.addClickListener(e -> {
//					takeTool(message.getToolId());
//
//					confirmMarkMessageAsRead(message, messageWrapper);
//				});
//				messageButtonsLayout.add(takeToolButton);
//			}
//
//			messageWrapper.add(messageButtonsLayout);
//		}
//
//		return messageWrapper;
//	}
//
//	private void confirmMarkMessageAsRead(Message message, FlexBoxLayout messageWrapper) {
//
//		if (message.getMessageType().equals(MessageType.SIMPLE_MESSAGE)) {
//			markMessageAsRead(message, messageWrapper);
//			return;
//		}
//
//		if (message.getMessageType().equals(MessageType.TOOL_REQUEST)) {
//			ConfirmDialog dialog = new ConfirmDialog();
//			dialog.setMessage("This action will send a cancel message to sender. Proceed?");
//			dialog.closeOnCancel();
//
//			dialog.getConfirmButton().addClickListener(e -> {
//				dialog.close();
//
//				markMessageAsRead(message, messageWrapper);
//
//				Message cancelRequestMessage = new Message();
//				cancelRequestMessage.setMessageType(MessageType.SIMPLE_MESSAGE);
//				cancelRequestMessage.setMessageHeader("Your tool request has been cancelled.");
//				cancelRequestMessage.setMessageText("Tool: "+ InventoryFacade.getInstance().getById(message.getToolId()).getName());
//				cancelRequestMessage.setSenderId(AuthenticationService.getCurrentSessionUser().getId());
//				cancelRequestMessage.setRecipientId(message.getSenderId());
//
//				MessageFacade.getInstance().insert(cancelRequestMessage);
//
//				Broadcaster.broadcastToUser(message.getSenderId(), "You have new message");
//			});
//
//			dialog.open();
//			return;
//		}
//
//		if (message.getToolId() != null) {
//			InventoryItem tool = InventoryFacade.getInstance().getById(message.getToolId());
//
//			// USER TOOK THE TOOL
//			if (tool.getUsageStatus().equals(ToolUsageStatus.IN_USE)) {
//				markMessageAsRead(message, messageWrapper);
//			}
//
//			// USER DIDN'T TAKE THE TOOL -> CONFIRM TOOL RELEASE
//			if (tool.getUsageStatus().equals(ToolUsageStatus.RESERVED)) {
//				ConfirmDialog dialog = new ConfirmDialog();
//				dialog.setMessage("This action will mark the tool as " + ToolUsageStatus.FREE.getName() + ". Proceed?");
//
//				dialog.closeOnCancel();
//				dialog.getConfirmButton().addClickListener(e -> {
//					dialog.close();
//
//					tool.setUsageStatus(ToolUsageStatus.FREE);
//					tool.setReservedUser(null);
//
//					if (InventoryFacade.getInstance().update(tool)) {
//
////						Transaction tr = new Transaction();
////						tr.setTransactionTarget(TransactionTarget.TOOL_STATUS);
////						tr.setTransactionOperation(TransactionType.EDIT);
////						tr.setUser(AuthenticationService.getCurrentSessionUser());
////						tr.setInventoryEntity(tool);
////						tr.setAdditionalInfo("User released the tool.\nTool Status changed from: " + ToolUsageStatus.RESERVED.getName() + " to: " + ToolUsageStatus.FREE.getName());
//
////						TransactionFacade.getInstance().insert(tr);
//
//						UIUtils.showNotification("Tool released", UIUtils.NotificationType.SUCCESS);
//					} else {
//						UIUtils.showNotification("Tool release failed", UIUtils.NotificationType.ERROR);
//					}
//
//					markMessageAsRead(message, messageWrapper);
//				});
//
//				dialog.open();
//			}
//
//		} else {
//			markMessageAsRead(message, messageWrapper);
//		}
//	}
//
//	private void markMessageAsRead(Message message, FlexBoxLayout messageWrapper) {
//		message.setMessageRead(true);
//		messageWrapper.getElement().setAttribute(READ, true);
//
//		MessageFacade.getInstance().update(message);
//
//		showMessages();
//
//		handleUnreadMessagesCount();
//	}
//
//
//	private void constructNewMessageDialog(boolean isReply, Message originalMessage) {
//		CustomDialog dialog = new CustomDialog();
//		dialog.setCloseOnEsc(false);
//		dialog.setCloseOnOutsideClick(false);
//
//
//		ComboBox<User> recipientComboBox = new ComboBox<>("Recipient");
//		recipientComboBox.setItems();
//		recipientComboBox.setItemLabelGenerator(User::getFullName);
//		recipientComboBox.setRequired(true);
//
//		if (isReply) {
//			recipientComboBox.setValue(UserFacade.getInstance().getUserById(originalMessage.getSenderId()));
//			recipientComboBox.setReadOnly(true);
//		} else {
//			List<User> recipients;
//
//			if (AuthenticationService.getCurrentSessionUser().getPermissionLevel().equalsTo(PermissionLevel.SYSTEM_ADMIN)) {
//				recipients = UserFacade.getInstance().getAllUsers();
//			} else {
//				recipients = UserFacade.getInstance().getUsersInCompany(AuthenticationService.getCurrentSessionUser().getCompany().getId());
//			}
//
////			recipients.remove(AuthenticationService.getCurrentSessionUser());
//			recipientComboBox.setItems(recipients);
//		}
//
//
//		TextField titleField = new TextField("Title");
////		titleField.setMinWidth("400px");
//
//		if (isReply) {
//			titleField.setValue("RE: " + originalMessage.getMessageHeader());
//		}
//
//		TextArea messageField = new TextArea("Message");
//		messageField.setRequired(true);
//		messageField.setWidthFull();
//		messageField.setMinHeight("300px");
//
//		if (isReply) {
//			dialog.setHeader(UIUtils.createH3Label("Reply Message"));
//		} else {
//			dialog.setHeader(UIUtils.createH3Label("New Message"));
//		}
//
//
//		dialog.getContent().add(recipientComboBox, titleField, messageField);
//		dialog.getCancelButton().addClickListener(e -> dialog.close());
//
//		dialog.getConfirmButton().setText("Send");
//		dialog.getConfirmButton().addClickListener(e -> {
//
//			if (recipientComboBox.getValue() == null) {
//				recipientComboBox.setInvalid(true);
//				recipientComboBox.setErrorMessage("Select Recipient");
//				return;
//			}
//
//			if (messageField.getValue().length() <= 0) {
//				messageField.setInvalid(true);
//				messageField.setErrorMessage("Cannot send empty message");
//				return;
//			}
//
//			Message message = new Message();
//			message.setSenderId(AuthenticationService.getCurrentSessionUser().getId());
//			message.setMessageType(MessageType.SIMPLE_MESSAGE);
//			message.setRecipientId(recipientComboBox.getValue().getId());
//
//			message.setMessageHeader(titleField.getValue());
//			message.setMessageText(messageField.getValue());
//
//			if (MessageFacade.getInstance().insert(message)) {
//
////				Transaction tr = new Transaction();
////				tr.setTransactionOperation(TransactionType.SEND);
////				tr.setTransactionTarget(TransactionTarget.MESSAGE);
////				tr.setUser(AuthenticationService.getCurrentSessionUser());
////				tr.setDestinationUser(recipientComboBox.getValue());
////				tr.setAdditionalInfo("User sent message");
////
////				TransactionFacade.getInstance().insert(tr);
//
//				UIUtils.showNotification("Message sent", UIUtils.NotificationType.SUCCESS);
//			} else {
//				UIUtils.showNotification("Message send error", UIUtils.NotificationType.ERROR);
//			}
//
//			Broadcaster.broadcastToUser(recipientComboBox.getValue().getId(), "You have new message");
//
//			dialog.close();
//		});
//
//		dialog.open();
//	}
//
//
//	private void takeTool(Long toolId) {
//		InventoryItem tool = InventoryFacade.getInstance().getById(toolId);
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
//			UIUtils.showNotification("Tool taken", UIUtils.NotificationType.SUCCESS);
//		} else {
//			UIUtils.showNotification("Tool take failed", UIUtils.NotificationType.ERROR);
//		}
//	}
//}
