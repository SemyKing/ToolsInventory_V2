package com.gmail.grigorij.ui.views.navigation.messages;

import com.gmail.grigorij.backend.database.facades.InventoryFacade;
import com.gmail.grigorij.backend.database.facades.MessageFacade;
import com.gmail.grigorij.backend.database.facades.TransactionFacade;
import com.gmail.grigorij.backend.database.facades.UserFacade;
import com.gmail.grigorij.backend.entities.inventory.InventoryItem;
import com.gmail.grigorij.backend.entities.message.Message;
import com.gmail.grigorij.backend.entities.transaction.Transaction;
import com.gmail.grigorij.backend.entities.user.User;
import com.gmail.grigorij.backend.enums.MessageType;
import com.gmail.grigorij.backend.enums.inventory.ToolStatus;
import com.gmail.grigorij.backend.enums.transactions.TransactionTarget;
import com.gmail.grigorij.backend.enums.transactions.TransactionType;
import com.gmail.grigorij.ui.utils.UIUtils;
import com.gmail.grigorij.ui.utils.components.ConfirmDialog;
import com.gmail.grigorij.ui.utils.components.FlexBoxLayout;
import com.gmail.grigorij.ui.utils.components.frames.ViewFrame;
import com.gmail.grigorij.ui.utils.css.Display;
import com.gmail.grigorij.ui.utils.css.FlexDirection;
import com.gmail.grigorij.ui.utils.css.size.*;
import com.gmail.grigorij.utils.AuthenticationService;
import com.gmail.grigorij.utils.Broadcaster;
import com.gmail.grigorij.utils.converters.DateConverter;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.router.PageTitle;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;


/**
 * User receives Message when:
 * - The tool that user has reserved becomes Free
 * - Other user sends a Request for a tool that this user has
 * - Other user sends a simple Message
 */

@PageTitle("Messages")
public class Messages extends ViewFrame {

	private static final String CLASS_NAME = "messages_view";
	private static final String READ = "read";

	private List<Message> messages;
	private FlexBoxLayout contentLayout;

	private DatePicker dateStartField, dateEndField;
//	private Checkbox showReadCheckbox;

	private Label unreadMessagesLabel;
	private boolean showReadMessages = false;

	public Messages() {
		setViewContent(createContent());
	}


	private FlexBoxLayout createContent() {
		//HEADER
		FlexBoxLayout header = new FlexBoxLayout();
		header.setClassName(CLASS_NAME + "__header");
		header.setFlexDirection(FlexDirection.ROW);
		header.setAlignItems(FlexComponent.Alignment.BASELINE);
		header.setWidthFull();

		FlexBoxLayout datesLayout = new FlexBoxLayout();
		datesLayout.addClassName(CLASS_NAME + "__dates");
		datesLayout.setFlexDirection(FlexDirection.ROW);
		datesLayout.setAlignItems(FlexComponent.Alignment.BASELINE);

		dateStartField = new DatePicker();
		dateStartField.setLabel("Start Date");
		dateStartField.setPlaceholder("Start Date");
		dateStartField.setLocale(new Locale("fi"));
		dateStartField.setValue(LocalDate.now());

		datesLayout.add(dateStartField);

		dateEndField = new DatePicker();
		dateEndField.setLabel("End Date");
		dateEndField.setPlaceholder("End Date");
		dateEndField.setLocale(new Locale("fi"));
		dateEndField.setValue(LocalDate.now());

		datesLayout.add(dateEndField);
		datesLayout.setComponentMargin(dateEndField, Horizontal.S);

		Button applyDates = UIUtils.createButton("Apply", ButtonVariant.LUMO_PRIMARY);
		applyDates.addClickListener(e -> {
			getMessagesBetweenDates();
			showMessages();
		});

		datesLayout.add(applyDates);


		FlexBoxLayout datesActionsLayout = new FlexBoxLayout();
		datesActionsLayout.addClassName(CLASS_NAME + "__dates_actions");
		datesActionsLayout.setFlexDirection(FlexDirection.ROW);
		datesActionsLayout.setAlignItems(FlexComponent.Alignment.BASELINE);


		Button todayButton = UIUtils.createIconButton("Today", VaadinIcon.CALENDAR, ButtonVariant.LUMO_CONTRAST);
		todayButton.addClickListener(e -> {
			dateStartField.setValue(LocalDate.now());
			dateEndField.setValue(LocalDate.now());
			getMessagesBetweenDates();
			showMessages();
		});
		datesActionsLayout.add(todayButton);
		datesActionsLayout.setComponentMargin(todayButton, Horizontal.S);

		Checkbox showReadCheckbox = new Checkbox("Show Read Messages");
		showReadCheckbox.setValue(true);
		showReadCheckbox.addClickListener(e -> {
			showReadMessages = showReadCheckbox.getValue();
			showMessages();
		});
		datesActionsLayout.add(showReadCheckbox);
		datesActionsLayout.setComponentMargin(showReadCheckbox, Left.S);

		header.add(datesLayout);
		header.add(datesActionsLayout);


		//CONTENT
		contentLayout = new FlexBoxLayout();
		contentLayout.setSizeFull();
		contentLayout.addClassName(CLASS_NAME + "__content");
		contentLayout.setFlexDirection(FlexDirection.COLUMN);
		contentLayout.setAlignItems(FlexComponent.Alignment.CENTER);

		unreadMessagesLabel = UIUtils.createH4Label("");
		unreadMessagesLabel.addClassName(CLASS_NAME + "__unread_label");

		FlexBoxLayout contentWrapperLayout = new FlexBoxLayout();
		contentWrapperLayout.addClassName(CLASS_NAME + "__wrapper");
		contentWrapperLayout.setFlexDirection(FlexDirection.COLUMN);

		contentWrapperLayout.add(header);
		contentWrapperLayout.add(unreadMessagesLabel);
		contentWrapperLayout.add(contentLayout);

		getMessagesBetweenDates();

		showMessages();

		return contentWrapperLayout;
	}

	private void getMessagesBetweenDates() {
		messages = MessageFacade.getInstance().getAllMessagesBetweenDates(dateStartField.getValue(), dateEndField.getValue(), AuthenticationService.getCurrentSessionUser().getId());

		messages.sort(Comparator.comparing(Message::getDate).reversed());

		handleUnreadMessagesCount();
	}

	private void showMessages() {
		contentLayout.removeAll();

		for (Message message : messages) {
			if (message.isMessageRead()) {
				if (showReadMessages) {
					contentLayout.add(constructMessageLayout(message));
				}
			} else {
				contentLayout.add(constructMessageLayout(message));
			}
		}
	}

	private void handleUnreadMessagesCount() {
		int unreadMessagesCount = 0;

		for (Message message : messages) {
			if (!message.isMessageRead()) {
				unreadMessagesCount++;
			}
		}
		unreadMessagesLabel.setText(unreadMessagesCount + " unread message(s)");
	}


	private FlexBoxLayout constructMessageLayout(Message message) {
		FlexBoxLayout messageWrapper = new FlexBoxLayout();
		messageWrapper.addClassName(CLASS_NAME + "__message-wrapper");
		messageWrapper.setFlexDirection(FlexDirection.ROW);
		messageWrapper.setDisplay(Display.FLEX);
		messageWrapper.setMargin(Top.M);
		messageWrapper.setPadding(Right.XS);

		messageWrapper.getElement().setAttribute(READ, message.isMessageRead());


		FlexBoxLayout messageContentLayout = new FlexBoxLayout();
		messageContentLayout.addClassName(CLASS_NAME + "__message-content");
		messageContentLayout.setFlexDirection(FlexDirection.COLUMN);
		messageContentLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
		messageContentLayout.setMargin(Horizontal.XS);
		messageContentLayout.setMargin(Vertical.XS);
		messageContentLayout.setWidthFull();

		messageContentLayout.add(UIUtils.createH4Label(message.getMessageHeader()));
		messageContentLayout.add(new Span(message.getMessageText()));

		messageWrapper.add(messageContentLayout);

		FlexBoxLayout messageFooterLayout = new FlexBoxLayout();
		messageFooterLayout.addClassName(CLASS_NAME + "__message_footer");
		messageFooterLayout.setFlexDirection(FlexDirection.ROW);

		User senderUser = UserFacade.getInstance().getUserById(message.getSenderId());
		String sender = (senderUser == null) ? message.getSender() : senderUser.getFullName();

		messageFooterLayout.add(UIUtils.createH6Label("From: " + sender));

		Label dateLabel = UIUtils.createH6Label("Sent: " + DateConverter.toStringDateWithTime(message.getDate()));
		messageFooterLayout.setComponentMargin(dateLabel, Left.AUTO);
		messageFooterLayout.add(dateLabel);

		messageContentLayout.add(messageFooterLayout);

		if (!message.isMessageRead()) {

			FlexBoxLayout messageButtonsLayout = new FlexBoxLayout();
			messageButtonsLayout.addClassName(CLASS_NAME + "__message-buttons");
			messageButtonsLayout.setFlexDirection(FlexDirection.COLUMN);
			messageButtonsLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);

			Button dismissButton = UIUtils.createSmallButton("Dismiss", VaadinIcon.CLOSE, ButtonVariant.LUMO_CONTRAST);
			dismissButton.addClickListener(e -> {
				confirmMarkMessageAsRead(message, messageWrapper);
			});
			messageButtonsLayout.add(dismissButton);

			if (message.getMessageType().equals(MessageType.SIMPLE_MESSAGE) || message.getMessageType().equals(MessageType.TOOL_REQUEST)) {
				Button replyButton = UIUtils.createSmallButton("Reply", VaadinIcon.REPLY, ButtonVariant.LUMO_PRIMARY);
				replyButton.addClickListener(e -> {
					replyToUser(message.getSenderId(), messageWrapper);
				});
				messageButtonsLayout.add(replyButton);
			}

			if (message.getMessageType().equals(MessageType.TOOL_FREE)) {
				Button takeToolButton = UIUtils.createSmallButton("Take Tool", VaadinIcon.HAND, ButtonVariant.LUMO_SUCCESS, ButtonVariant.LUMO_PRIMARY);
				takeToolButton.addClickListener(e -> {
					takeTool(message.getToolId());

					confirmMarkMessageAsRead(message, messageWrapper);
				});
				messageButtonsLayout.add(takeToolButton);
			}

			messageWrapper.add(messageButtonsLayout);
		}

		return messageWrapper;
	}


	/*
		MESSAGE ACTIONS
	 */
	private void confirmMarkMessageAsRead(Message message, FlexBoxLayout messageWrapper) {

		if (message.getMessageType().equals(MessageType.SIMPLE_MESSAGE)) {
			markMessageAsRead(message, messageWrapper);
			return;
		}

		if (message.getMessageType().equals(MessageType.TOOL_REQUEST)) {
			ConfirmDialog dialog = new ConfirmDialog("This action will send a cancel message to sender. Proceed?");
			dialog.closeOnCancel();

			dialog.getConfirmButton().addClickListener(e -> {
				dialog.close();

				markMessageAsRead(message, messageWrapper);

				Message cancelRequestMessage = new Message();
				cancelRequestMessage.setMessageType(MessageType.SIMPLE_MESSAGE);
				cancelRequestMessage.setMessageHeader("Your tool request has been cancelled.");
				cancelRequestMessage.setMessageText("Tool: "+ InventoryFacade.getInstance().getToolById(message.getToolId()).getName());
				cancelRequestMessage.setSenderId(AuthenticationService.getCurrentSessionUser().getId());
				cancelRequestMessage.setRecipientId(message.getSenderId());

				MessageFacade.getInstance().insert(cancelRequestMessage);

				Broadcaster.broadcastToUser(message.getSenderId(), "You have new message");
			});

			dialog.open();
			return;
		}

		if (message.getMessageType().equals(MessageType.TOOL_FREE)) {
			if (message.getToolId() == null) {
				System.err.println("'TOOL FREE' MESSAGE WITH NULL TOOL, MESSAGE ID: " + message.getId());
				return;
			}

			InventoryItem tool = InventoryFacade.getInstance().getToolById(message.getToolId());

			//USER TOOK THE TOOL
			if (tool.getUsageStatus().equals(ToolStatus.IN_USE)) {
				markMessageAsRead(message, messageWrapper);
				return;
			}

			//USER DIDN'T TAKE TOOL
			if (tool.getUsageStatus().equals(ToolStatus.RESERVED)) {
				ConfirmDialog dialog = new ConfirmDialog("This action will mark the tool as " + ToolStatus.FREE.getStringValue() + ". Proceed?");

				dialog.closeOnCancel();

				dialog.getConfirmButton().addClickListener(e -> {
					dialog.close();

					tool.setUsageStatus(ToolStatus.FREE);
					tool.setReservedByUser(null);

					if (InventoryFacade.getInstance().update(tool)) {

//						AuthenticationService.getCurrentSessionUser().removeToolReserved(message.getToolId());
//						UserFacade.getInstance().update(AuthenticationService.getCurrentSessionUser());

						Transaction tr = new Transaction();
						tr.setTransactionTarget(TransactionTarget.TOOL_STATUS);
						tr.setTransactionOperation(TransactionType.EDIT);
						tr.setWhoDid(AuthenticationService.getCurrentSessionUser());
						tr.setInventoryEntity(tool);
						tr.setAdditionalInfo("User released the tool.\nTool Status changed from: " + ToolStatus.RESERVED.getStringValue() + " to: " + ToolStatus.FREE.getStringValue());

						TransactionFacade.getInstance().insert(tr);

						UIUtils.showNotification("Tool released", UIUtils.NotificationType.SUCCESS);
					} else {
						UIUtils.showNotification("Tool release failed", UIUtils.NotificationType.ERROR);
					}


					markMessageAsRead(message, messageWrapper);
				});

				dialog.open();
			}

		}


			if (message.getToolId() != null) {
				InventoryItem tool = InventoryFacade.getInstance().getToolById(message.getToolId());

				if (tool.getUsageStatus().equals(ToolStatus.IN_USE)) {
					markMessageAsRead(message, messageWrapper);
				}

				if (tool.getUsageStatus().equals(ToolStatus.RESERVED)) {
					ConfirmDialog dialog = new ConfirmDialog("This action will mark the tool as " + ToolStatus.FREE.getStringValue() + ". Proceed?");

					dialog.closeOnCancel();

					dialog.getConfirmButton().addClickListener(e -> {
						dialog.close();

						tool.setUsageStatus(ToolStatus.FREE);
						tool.setReservedByUser(null);

						if (InventoryFacade.getInstance().update(tool)) {

//							AuthenticationService.getCurrentSessionUser().removeToolReserved(message.getToolId());
//							UserFacade.getInstance().update(AuthenticationService.getCurrentSessionUser());

							Transaction tr = new Transaction();
							tr.setTransactionTarget(TransactionTarget.TOOL_STATUS);
							tr.setTransactionOperation(TransactionType.EDIT);
							tr.setWhoDid(AuthenticationService.getCurrentSessionUser());
							tr.setInventoryEntity(tool);
							tr.setAdditionalInfo("User released the tool.\nTool Status changed from: " + ToolStatus.RESERVED.getStringValue() + " to: " + ToolStatus.FREE.getStringValue());

							TransactionFacade.getInstance().insert(tr);

							UIUtils.showNotification("Tool released", UIUtils.NotificationType.SUCCESS);
						} else {
							UIUtils.showNotification("Tool release failed", UIUtils.NotificationType.ERROR);
						}


						markMessageAsRead(message, messageWrapper);
					});

					dialog.open();
				}
			} else {
				markMessageAsRead(message, messageWrapper);
			}
		}

	private void markMessageAsRead(Message message, FlexBoxLayout messageWrapper) {
		message.setMessageRead(true);
		MessageFacade.getInstance().update(message);

		messageWrapper.getElement().setAttribute(READ, true);

		if (!showReadMessages) {
			contentLayout.remove(messageWrapper);
		}

		handleUnreadMessagesCount();
	}

	private void replyToUser(long senderId, FlexBoxLayout messageWrapper) {
		System.out.println("REPLY");

		if (sendReply()) {
			contentLayout.remove(messageWrapper);
		}
	}

	private boolean sendReply() {

		return true;
	}

	private void takeTool(Long toolId) {
		InventoryItem tool = InventoryFacade.getInstance().getToolById(toolId);

		tool.setInUseByUser(AuthenticationService.getCurrentSessionUser());
		tool.setUsageStatus(ToolStatus.IN_USE);

		if (InventoryFacade.getInstance().update(tool)) {

			Transaction tr = new Transaction();
			tr.setTransactionTarget(TransactionTarget.TOOL_STATUS);
			tr.setTransactionOperation(TransactionType.EDIT);
			tr.setWhoDid(AuthenticationService.getCurrentSessionUser());
			tr.setInventoryEntity(tool);
			tr.setAdditionalInfo("User took the tool.\nTool Status changed from: " + ToolStatus.FREE.getStringValue() + " to: " + ToolStatus.IN_USE.getStringValue());

			TransactionFacade.getInstance().insert(tr);

			UIUtils.showNotification("Tool taken successfully", UIUtils.NotificationType.SUCCESS);
		} else {
			UIUtils.showNotification("Tool take failed", UIUtils.NotificationType.ERROR);
		}
	}
}
