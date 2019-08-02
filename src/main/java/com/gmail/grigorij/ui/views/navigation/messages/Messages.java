package com.gmail.grigorij.ui.views.navigation.messages;

import com.gmail.grigorij.backend.database.facades.InventoryFacade;
import com.gmail.grigorij.backend.database.facades.MessageFacade;
import com.gmail.grigorij.backend.database.facades.TransactionFacade;
import com.gmail.grigorij.backend.database.facades.UserFacade;
import com.gmail.grigorij.backend.entities.inventory.InventoryEntity;
import com.gmail.grigorij.backend.entities.inventory.ToolStatus;
import com.gmail.grigorij.backend.entities.message.Message;
import com.gmail.grigorij.backend.entities.message.MessageType;
import com.gmail.grigorij.backend.entities.transaction.OperationTarget;
import com.gmail.grigorij.backend.entities.transaction.OperationType;
import com.gmail.grigorij.backend.entities.transaction.Transaction;
import com.gmail.grigorij.backend.entities.user.User;
import com.gmail.grigorij.ui.utils.UIUtils;
import com.gmail.grigorij.ui.utils.components.FlexBoxLayout;
import com.gmail.grigorij.ui.utils.css.Display;
import com.gmail.grigorij.ui.utils.css.FlexDirection;
import com.gmail.grigorij.ui.utils.css.size.Horizontal;
import com.gmail.grigorij.ui.utils.css.size.Left;
import com.gmail.grigorij.ui.utils.css.size.Vertical;
import com.gmail.grigorij.ui.utils.components.frames.ViewFrame;
import com.gmail.grigorij.utils.AuthenticationService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.PageTitle;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;


/**
 * User receives Message when:
 * - The tool that user has reserved becomes Free
 * - Other user sends a Request for a tool that this user has
 * - Other user sends a simple Message
 */

@PageTitle("Messages")
public class Messages extends ViewFrame {

	private static final String CLASS_NAME = "messages_view";
	private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

	private List<Message> messages;
	private FlexBoxLayout contentLayout;

	public Messages() {
		setViewContent(createContent());
	}

	private FlexBoxLayout createContent() {

		messages = new ArrayList<>(AuthenticationService.getCurrentSessionUser().getMessages());

		contentLayout = new FlexBoxLayout();
		contentLayout.addClassName(CLASS_NAME + "__content");
		contentLayout.setFlexDirection(FlexDirection.COLUMN);
		contentLayout.setDisplay(Display.FLEX);

		String m = (messages.size() == 1) ? "message" : "messages";

		contentLayout.add(UIUtils.createH3Label(messages.size() + " unread " + m));

		for (Message message : messages) {
			contentLayout.add(constructMessageLayout(message));
		}

		return contentLayout;
	}


	private FlexBoxLayout constructMessageLayout(Message message) {
		FlexBoxLayout messageWrapper = new FlexBoxLayout();
		messageWrapper.addClassName(CLASS_NAME + "__message-wrapper");
		messageWrapper.setFlexDirection(FlexDirection.ROW);
		messageWrapper.setDisplay(Display.FLEX);
		messageWrapper.setMargin(Horizontal.M);
		messageWrapper.setMargin(Vertical.M);
		messageWrapper.setWidthFull();


		FlexBoxLayout messageContentLayout = new FlexBoxLayout();
		messageContentLayout.addClassName(CLASS_NAME + "__message-content");
		messageContentLayout.setFlexDirection(FlexDirection.COLUMN);
		messageContentLayout.setDisplay(Display.FLEX);
		messageContentLayout.setMargin(Horizontal.XS);
		messageContentLayout.setMargin(Vertical.XS);

		messageContentLayout.add(UIUtils.createH2Label(message.getMessageHeader()));
		messageContentLayout.add(new Span(message.getMessageText()));

		FlexBoxLayout messageFooterLayout = new FlexBoxLayout();
		messageFooterLayout.setFlexDirection(FlexDirection.ROW);
		messageFooterLayout.add(UIUtils.createH5Label("From: " + message.getSender().getFullName()));

		Label dateLabel = UIUtils.createH5Label("Sent: " + dateFormat.format(message.getDate()));
		messageFooterLayout.setComponentMargin(dateLabel, Left.AUTO);
		messageFooterLayout.add(dateLabel);

		messageContentLayout.add(messageFooterLayout);


		FlexBoxLayout messageButtonsLayout = new FlexBoxLayout();
		messageContentLayout.addClassName(CLASS_NAME + "__message-buttons");
		messageButtonsLayout.setFlexDirection(FlexDirection.COLUMN);

		Button dismissButton = UIUtils.createButton("Dismiss", VaadinIcon.CLOSE, ButtonVariant.LUMO_CONTRAST);
		dismissButton.addClickListener(e -> {
			dismissMessage(message, messageWrapper);
		});
		messageButtonsLayout.add(dismissButton);

		if (message.getMessageType().equals(MessageType.SIMPLE_MESSAGE) || message.getMessageType().equals(MessageType.TOOL_REQUEST)) {
			Button replyButton = UIUtils.createButton("Reply", VaadinIcon.REPLY, ButtonVariant.LUMO_SUCCESS);
			replyButton.addClickListener(e -> {
				replyToUser(message.getSender());

				dismissMessage(message, messageWrapper);
			});
			messageButtonsLayout.add(replyButton);
		}

		if (message.getMessageType().equals(MessageType.TOOL_FREE)) {
			Button takeToolButton = UIUtils.createButton("Take Tool", VaadinIcon.HAND, ButtonVariant.LUMO_SUCCESS);
			takeToolButton.addClickListener(e -> {
				takeTool(message.getTool());

				dismissMessage(message, messageWrapper);
			});
			messageButtonsLayout.add(takeToolButton);
		}



		return messageWrapper;
	}

	private void dismissMessage(Message message, FlexBoxLayout messageWrapper) {
		MessageFacade.getInstance().remove(message);

		contentLayout.remove(messageWrapper);
	}

	private void replyToUser(User sender) {
		System.out.println("REPLY");
	}

	private void takeTool(InventoryEntity tool) {
		tool.setUser(AuthenticationService.getCurrentSessionUser());
		tool.setUsageStatus(ToolStatus.IN_USE);

		if (InventoryFacade.getInstance().update(tool)) {

			AuthenticationService.getCurrentSessionUser().addToolInUse(tool);

			UserFacade.getInstance().update(AuthenticationService.getCurrentSessionUser());

			Transaction tr = new Transaction();
			tr.setTransactionTarget(OperationTarget.TOOL_STATUS);
			tr.setTransactionOperation(OperationType.CHANGE);
			tr.setWhoDid(AuthenticationService.getCurrentSessionUser());
			tr.setInventoryEntity(tool);
			tr.setAdditionalInfo("User took the tool.\nStatus change from: " + ToolStatus.FREE.getStringValue() + " to: " + ToolStatus.IN_USE.getStringValue());

			TransactionFacade.getInstance().insert(tr);

			UIUtils.showNotification("Tool taken successfully", UIUtils.NotificationType.SUCCESS);
		} else {
			UIUtils.showNotification("Tool take failed", UIUtils.NotificationType.ERROR);
		}
	}
}
