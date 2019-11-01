package com.gmail.grigorij.ui.application.views;

import com.gmail.grigorij.backend.database.facades.*;
import com.gmail.grigorij.backend.database.entities.Company;
import com.gmail.grigorij.backend.database.entities.Message;
import com.gmail.grigorij.backend.database.entities.User;
import com.gmail.grigorij.backend.database.enums.MessageType;
import com.gmail.grigorij.backend.database.enums.operations.Operation;
import com.gmail.grigorij.backend.database.enums.operations.OperationTarget;
import com.gmail.grigorij.backend.database.enums.permissions.PermissionLevel;
import com.gmail.grigorij.backend.database.enums.permissions.PermissionRange;
import com.gmail.grigorij.ui.components.detailsdrawer.DetailsDrawer;
import com.gmail.grigorij.ui.components.detailsdrawer.DetailsDrawerHeader;
import com.gmail.grigorij.ui.components.dialogs.CustomDialog;
import com.gmail.grigorij.ui.components.forms.MessageForm;
import com.gmail.grigorij.ui.utils.UIUtils;
import com.gmail.grigorij.ui.components.layouts.FlexBoxLayout;
import com.gmail.grigorij.ui.utils.css.LumoStyles;
import com.gmail.grigorij.ui.utils.css.size.*;
import com.gmail.grigorij.utils.AuthenticationService;
import com.gmail.grigorij.utils.Broadcaster;
import com.gmail.grigorij.utils.DateConverter;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.value.ValueChangeMode;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;


@CssImport("./styles/views/messages.css")
public class MessagesView extends Div {

	private static final String CLASS_NAME = "messages";
	private final MessageForm messageForm = new MessageForm(this);

	private DatePicker dateStartField, dateEndField;

	private Grid<Message> grid;
	private ListDataProvider<Message> dataProvider;

	private DetailsDrawer detailsDrawer;


	public MessagesView() {
		addClassName(CLASS_NAME);

		Div contentWrapper = new Div();
		contentWrapper.addClassName(CLASS_NAME + "__content-wrapper");

		contentWrapper.add(constructHeader());
		contentWrapper.add(constructContent());

		add(contentWrapper);
		add(constructDetails());
	}


	private Div constructHeader() {
		Div header = new Div();
		header.addClassName(CLASS_NAME + "__header");

		TextField searchField = new TextField("Search");
		searchField.setClearButtonVisible(true);
		searchField.setPrefixComponent(VaadinIcon.SEARCH.create());
		searchField.setPlaceholder("Search Messages");
		searchField.setValueChangeMode(ValueChangeMode.LAZY);
		searchField.addValueChangeListener(event -> filterGrid(searchField.getValue()));

		header.add(searchField);

		FlexBoxLayout additionalOptionsDiv = new FlexBoxLayout();
		additionalOptionsDiv.addClassName(CLASS_NAME + "__additional_options");
		additionalOptionsDiv.setAlignItems(FlexComponent.Alignment.BASELINE);
		additionalOptionsDiv.setMargin(Left.S);



		// DATES FILTER
		Div menuBarIconDiv = new Div();
		menuBarIconDiv.addClassName(CLASS_NAME + "__menu-bar-icon-div");
		menuBarIconDiv.add(new Icon(VaadinIcon.CALENDAR));

		MenuBar menuBar = new MenuBar();
		menuBar.addThemeVariants(MenuBarVariant.LUMO_TERTIARY, MenuBarVariant.LUMO_CONTRAST);
		MenuItem mainMenu = menuBar.addItem(menuBarIconDiv);
		mainMenu.getSubMenu().add(constructDatesFilterLayout());

		Button composeMessageButton = UIUtils.createButton("Compose", VaadinIcon.ENVELOPE , ButtonVariant.LUMO_PRIMARY);
		composeMessageButton.addClassName("compose-message-button");
		composeMessageButton.addClickListener(e -> constructMessageDialog(null, ""));


		if (!AuthenticationService.getCurrentSessionUser().getPermissionLevel().equalsTo(PermissionLevel.SYSTEM_ADMIN)) {
			if (!PermissionFacade.getInstance().isUserAllowedTo(Operation.SEND, OperationTarget.MESSAGES, PermissionRange.COMPANY)) {
				composeMessageButton.setEnabled(false);
			}
		}

		additionalOptionsDiv.add(menuBar);
		additionalOptionsDiv.add(composeMessageButton);

		header.add(additionalOptionsDiv);

		return header;
	}

	private Div constructDatesFilterLayout() {
		Div datesFilterDiv = new Div();
		datesFilterDiv.addClassName(CLASS_NAME + "__dates-filter");

		Div datesDiv = new Div();
		datesDiv.addClassName(CLASS_NAME + "__dates");

		dateStartField = new DatePicker();
		dateStartField.setLabel("Start Date");
		dateStartField.setLocale(new Locale("fi"));
		dateStartField.setValue(LocalDate.now());
		dateStartField.setRequired(true);
		dateStartField.setErrorMessage("Invalid Date");
		dateStartField.addValueChangeListener(e -> {
			dateStartField.setInvalid(false);
		});

		datesDiv.add(dateStartField);

		dateEndField = new DatePicker();
		dateEndField.setLabel("End Date");
		dateEndField.setLocale(new Locale("fi"));
		dateEndField.setValue(LocalDate.now());
		dateEndField.setRequired(true);
		dateEndField.setErrorMessage("Invalid Date");
		dateEndField.addValueChangeListener(e -> {
			dateEndField.setInvalid(false);
		});

		datesDiv.add(dateEndField);

		datesFilterDiv.add(datesDiv);

		Button applyDatesButton = UIUtils.createButton("Apply", ButtonVariant.LUMO_PRIMARY);
		applyDatesButton.addClickListener(e -> getMessagesBetweenDates());

		datesFilterDiv.add(applyDatesButton);

		return datesFilterDiv;
	}

	private Div constructContent() {
		Div content = new Div();
		content.setClassName(CLASS_NAME + "__content");

		// GRID
		content.add(constructGrid());

		return content;
	}

	private Grid constructGrid() {
		grid = new Grid<>();
		grid.setClassName("grid-view");
		grid.setSizeFull();

		grid.asSingleSelect().addValueChangeListener(e -> {
			if (grid.asSingleSelect().getValue() != null) {
				showDetails(grid.asSingleSelect().getValue());
			} else {
				detailsDrawer.hide();
			}
		});

		dataProvider = DataProvider.ofCollection(MessageFacade.getInstance().getAllMessagesBetweenDatesByUser(dateStartField.getValue(), dateEndField.getValue(), AuthenticationService.getCurrentSessionUser().getId()));
		grid.setDataProvider(dataProvider);

		// READ
		grid.addComponentColumn(msg -> {
					Icon icon;

					if (msg.isMessageRead()) {
						icon = new Icon(VaadinIcon.ENVELOPE_OPEN);
						icon.setColor("var(--lumo-header-text-color)");
					} else {
						icon = new Icon(VaadinIcon.ENVELOPE);
						icon.setColor(LumoStyles.Color.Primary._100);
					}
					return icon;
				})
				.setAutoWidth(true)
				.setFlexGrow(0);

		grid.addColumn(msg -> {
					if (msg.getSenderUser() == null) {
						return msg.getSenderString();
					} else {
						return UserFacade.getInstance().getUserById(msg.getSenderUser().getId()).getFullName();
					}
				})
				.setHeader("Sender")
				.setAutoWidth(true)
				.setFlexGrow(0);

		grid.addColumn(Message::getSubject)
				.setHeader("Subject")
				.setWidth("200px")
				.setFlexGrow(0);

		grid.addColumn(Message::getText)
				.setHeader("Message")
				.setFlexGrow(1);

		grid.addColumn(msg -> {
					try {
						if (msg.getDate() == null) {
							return "";
						} else {
							return DateConverter.dateToStringWithTime(msg.getDate());
						}
					} catch (Exception e) {
						return "";
					}
				})
				.setHeader("Received")
				.setAutoWidth(true)
				.setTextAlign(ColumnTextAlign.END)
				.setFlexGrow(0);

		return grid;
	}

	private DetailsDrawer constructDetails() {
		detailsDrawer = new DetailsDrawer(DetailsDrawer.Position.RIGHT);
		detailsDrawer.setContent(messageForm);

		// Header
		DetailsDrawerHeader detailsDrawerHeader = new DetailsDrawerHeader("Message Details");
		detailsDrawerHeader.getClose().addClickListener(e -> closeDetails());

		detailsDrawer.setHeader(detailsDrawerHeader);

		// Footer
		detailsDrawer.setFooter(createDetailsFooter());

		return detailsDrawer;
	}

	private FlexBoxLayout createDetailsFooter() {
		FlexBoxLayout footer = new FlexBoxLayout();
		footer.setClassName(CLASS_NAME + "__details-footer");

		Button closeDetailsButton = UIUtils.createButton("Close", ButtonVariant.LUMO_CONTRAST);
		closeDetailsButton.addClickListener(e -> closeDetails());
		footer.add(closeDetailsButton);

		Button replyButton = UIUtils.createButton("Reply", VaadinIcon.REPLY, ButtonVariant.LUMO_PRIMARY);
		replyButton.addClickListener(reply -> {
			Message message = grid.asSingleSelect().getValue();
			if (message != null) {
				if (message.getSenderUser() == null) {
					UIUtils.showNotification("Cannot reply to: " + message.getSenderString(), UIUtils.NotificationType.INFO);
				} else {
					constructMessageDialog(message.getSenderUser(), message.getSubject());
				}
			}
		});
		footer.add(replyButton);


		return footer;
	}


	private void filterGrid(String searchString) {
		dataProvider.clearFilters();
		final String mainSearchString = searchString.trim();

		if (mainSearchString.contains("+")) {
			String[] searchParams = mainSearchString.split("\\+");

			dataProvider.addFilter(
					message -> {
						boolean res = true;
						for (String sParam : searchParams) {
							res =  StringUtils.containsIgnoreCase(message.getSubject(), sParam) ||
									StringUtils.containsIgnoreCase(message.getText(), sParam) ||
									StringUtils.containsIgnoreCase((message.getSenderUser() == null) ? "" : message.getSenderUser().getFullName(), sParam) ||
									StringUtils.containsIgnoreCase((message.getDate() == null) ? "" : DateConverter.dateToStringWithTime(message.getDate()), sParam);
							if (!res)
								break;
						}
						return res;
					}
			);
		} else {
			dataProvider.addFilter(
					message -> StringUtils.containsIgnoreCase(message.getSubject(), mainSearchString)  ||
							StringUtils.containsIgnoreCase(message.getText(), mainSearchString) ||
							StringUtils.containsIgnoreCase((message.getSenderUser() == null) ? "" : message.getSenderUser().getFullName(), mainSearchString) ||
							StringUtils.containsIgnoreCase((message.getDate() == null) ? "" : DateConverter.dateToStringWithTime(message.getDate()), mainSearchString)
			);
		}
	}

	private void showDetails(Message message) {
		messageForm.setMessage(message);
		detailsDrawer.show();

		if (!message.isMessageRead()) {
			message.setMessageRead(true);

			MessageFacade.getInstance().update(message);

			dataProvider.refreshItem(message);
		}
	}

	public void closeDetails() {
		detailsDrawer.hide();
		grid.deselectAll();
	}


	private void getMessagesBetweenDates() {
		if (dateStartField.getValue() == null || dateStartField.isInvalid()) {
			dateStartField.setInvalid(true);
			return;
		}
		if (dateEndField.getValue() == null || dateEndField.isInvalid()) {
			dateEndField.setInvalid(true);
			return;
		}
		if (dateStartField.getValue().isAfter(dateEndField.getValue())) {
			UIUtils.showNotification("Start Date cannot be after End Date", UIUtils.NotificationType.INFO);
			return;
		}

		List<Message> messages = MessageFacade.getInstance().getAllMessagesBetweenDatesByUser(dateStartField.getValue(), dateEndField.getValue(), AuthenticationService.getCurrentSessionUser().getId());
		messages.sort(Comparator.comparing(Message::getDate).reversed());

		dataProvider.getItems().clear();
		dataProvider.getItems().addAll(messages);

		dataProvider.refreshAll();
	}

	private void constructMessageDialog(User recipient, String RE_subject) {
		ComboBox<User> recipientComboBox = new ComboBox<>();
		recipientComboBox.setLabel("Recipient");
		recipientComboBox.setItems();
		recipientComboBox.setItemLabelGenerator(User::getFullName);

		if (recipient != null) {
			recipientComboBox.setValue(recipient);
			recipientComboBox.setReadOnly(true);
		} else {
			List<User> recipients;
			if (AuthenticationService.getCurrentSessionUser().getPermissionLevel().equalsTo(PermissionLevel.SYSTEM_ADMIN)) {
				recipients = UserFacade.getInstance().getAllUsers();
			} else {
				recipients = UserFacade.getInstance().getUsersInCompany(AuthenticationService.getCurrentSessionUser().getCompany().getId());

				// ADD SYSTEM ADMINS FOR CONTACTING
				List<User> systemAdmins = UserFacade.getInstance().getSystemAdmins();

				for (User sysAdmin : systemAdmins) {
					recipients.add(0, sysAdmin);
				}
			}

			recipientComboBox.setItems(recipients);
			recipientComboBox.setRequired(true);
		}


		Div messageAllUsersDiv = null;
		Checkbox useCompanyRecipients = new Checkbox("Use This");
		ComboBox<Company> allUsersInCompany = new ComboBox<>();
		boolean system_admin = false;

		if (AuthenticationService.getCurrentSessionUser().getPermissionLevel().equalsTo(PermissionLevel.SYSTEM_ADMIN)) {
			messageAllUsersDiv = new Div();
			messageAllUsersDiv.addClassName(CLASS_NAME + "__message_a_u_i_c");

			allUsersInCompany.setLabel("All Recipients In");
			allUsersInCompany.setItems(CompanyFacade.getInstance().getAllCompanies());
			allUsersInCompany.setItemLabelGenerator(Company::getName);
			allUsersInCompany.setReadOnly(true);

			useCompanyRecipients.addValueChangeListener(cb -> {
				allUsersInCompany.setReadOnly(!cb.getValue());
				recipientComboBox.setReadOnly(cb.getValue());
			});

			messageAllUsersDiv.add(allUsersInCompany);
			messageAllUsersDiv.add(useCompanyRecipients);

			system_admin = true;
		}

		TextArea subjectField = new TextArea("Subject");
		subjectField.setRequired(true);
		if (RE_subject.length() > 0) {
			subjectField.setValue("RE: " + RE_subject);
		}

		TextArea messageArea = new TextArea("Message");
		messageArea.setRequired(true);


		// DIALOG
		CustomDialog dialog = new CustomDialog();
		dialog.setHeader(UIUtils.createH3Label("Compose Message"));
		dialog.setCloseOnOutsideClick(false);
		dialog.setCloseOnEsc(false);

		dialog.getContent().add(recipientComboBox);
		if (system_admin) {
			dialog.getContent().add(messageAllUsersDiv);
		}
		dialog.getContent().add(subjectField);
		dialog.getContent().add(messageArea);

		dialog.closeOnCancel();
		dialog.getConfirmButton().setText("Send");


		dialog.getConfirmButton().addClickListener(send -> {

			if (subjectField.getValue().length() > 255) {
				UIUtils.showNotification("Maximum amount of characters for Subject is 255", UIUtils.NotificationType.WARNING);
				return;
			}
			if (messageArea.getValue().length() > 255) {
				UIUtils.showNotification("Maximum amount of characters for Message is 255", UIUtils.NotificationType.WARNING);
				return;
			}

			if (AuthenticationService.getCurrentSessionUser().getPermissionLevel().equalsTo(PermissionLevel.SYSTEM_ADMIN)) {
				if (useCompanyRecipients.getValue()) {
					List<User> recipients = UserFacade.getInstance().getUsersInCompany(allUsersInCompany.getValue().getId());

					for (User user : recipients) {
						sendMessage(user.getId(), subjectField.getValue(), messageArea.getValue());
						dialog.close();
					}
				} else {
					if (recipientComboBox.getValue() == null) {
						UIUtils.showNotification("Select Recipient", UIUtils.NotificationType.INFO);
						return;
					}

					sendMessage(recipientComboBox.getValue().getId(), subjectField.getValue(), messageArea.getValue());
					dialog.close();
				}
			} else {
				if (recipientComboBox.getValue() == null) {
					UIUtils.showNotification("Select Recipient", UIUtils.NotificationType.INFO);
					return;
				}

				sendMessage(recipientComboBox.getValue().getId(), subjectField.getValue(), messageArea.getValue());
				dialog.close();
			}
		});

		dialog.open();
	}

	private void sendMessage(long recipientId, String subject, String text) {
		Message message = new Message();
		message.setMessageType(MessageType.SIMPLE_MESSAGE);
		message.setRecipientId(recipientId);
		message.setSenderUser(AuthenticationService.getCurrentSessionUser());
		message.setSubject(subject);
		message.setText(text);

		if (MessageFacade.getInstance().insert(message)) {
			UIUtils.showNotification("Message sent", UIUtils.NotificationType.SUCCESS);

			Broadcaster.broadcastToUser(recipientId, "You have new message");
		} else {
			UIUtils.showNotification("Message sending failed", UIUtils.NotificationType.ERROR);
		}
	}
}
