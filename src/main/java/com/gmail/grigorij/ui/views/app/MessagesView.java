package com.gmail.grigorij.ui.views.app;

import com.gmail.grigorij.backend.database.entities.Company;
import com.gmail.grigorij.backend.database.entities.Message;
import com.gmail.grigorij.backend.database.entities.User;
import com.gmail.grigorij.backend.database.enums.operations.Operation;
import com.gmail.grigorij.backend.database.enums.operations.OperationTarget;
import com.gmail.grigorij.backend.database.enums.permissions.PermissionLevel;
import com.gmail.grigorij.backend.database.enums.permissions.PermissionRange;
import com.gmail.grigorij.backend.database.facades.CompanyFacade;
import com.gmail.grigorij.backend.database.facades.MessageFacade;
import com.gmail.grigorij.backend.database.facades.PermissionFacade;
import com.gmail.grigorij.backend.database.facades.UserFacade;
import com.gmail.grigorij.ui.components.FlexBoxLayout;
import com.gmail.grigorij.ui.components.detailsdrawer.DetailsDrawer;
import com.gmail.grigorij.ui.components.detailsdrawer.DetailsDrawerHeader;
import com.gmail.grigorij.ui.components.dialogs.CustomDialog;
import com.gmail.grigorij.ui.components.dialogs.message.MessageView;
import com.gmail.grigorij.ui.components.forms.MessageForm;
import com.gmail.grigorij.ui.utils.UIUtils;
import com.gmail.grigorij.ui.utils.css.LumoStyles;
import com.gmail.grigorij.utils.authentication.AuthenticationService;
import com.gmail.grigorij.utils.Broadcaster;
import com.gmail.grigorij.utils.DateConverter;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.theme.lumo.Lumo;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;


@CssImport("./styles/views/messages.css")
public class MessagesView extends Div {

	private static final String CLASS_NAME = "messages";
	private final MessageForm messageForm = new MessageForm(this);

	private TextField searchField;
	private Div filtersDiv;
	private DatePicker dateStartField, dateEndField;
	private Checkbox showReadMessagesCheckbox;

	private boolean filtersVisible = false;

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

		toggleFilters();
	}


	private Div constructHeader() {
		Div header = new Div();
		header.addClassName(CLASS_NAME + "__header");

		Div headerTopDiv = new Div();
		headerTopDiv.addClassName(CLASS_NAME + "__header-top");
		header.add(headerTopDiv);

		Button toggleFiltersButton = UIUtils.createButton("Filters", VaadinIcon.FILTER, ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ICON);
		toggleFiltersButton.addClassName("dynamic-label-button");
		toggleFiltersButton.addClickListener(e -> toggleFilters());
		headerTopDiv.add(toggleFiltersButton);

		searchField = new TextField();
		searchField.setClearButtonVisible(true);
		searchField.setPrefixComponent(VaadinIcon.SEARCH.create());
		searchField.setPlaceholder("Search Messages");
		searchField.setValueChangeMode(ValueChangeMode.LAZY);
		searchField.addValueChangeListener(event -> applyFilters());
		headerTopDiv.add(searchField);


		Button composeMessageButton = UIUtils.createButton("Compose", VaadinIcon.ENVELOPE , ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ICON);
		composeMessageButton.addClassName("dynamic-label-button");
		composeMessageButton.addClickListener(e -> constructMessageDialog(null));
		composeMessageButton.setEnabled(false);
		headerTopDiv.add(composeMessageButton);

		if (PermissionFacade.getInstance().isSystemAdminOrAllowedTo(Operation.SEND, OperationTarget.MESSAGES, PermissionRange.COMPANY)) {
			composeMessageButton.setEnabled(true);
		}

		Div headerBottomDiv = new Div();
		headerBottomDiv.addClassName(CLASS_NAME + "__header-bottom");
		headerBottomDiv.add(constructMessagesFilterLayout());
		header.add(headerBottomDiv);

		return header;
	}

	private Div constructMessagesFilterLayout() {
		filtersDiv = new Div();
		filtersDiv.addClassName(CLASS_NAME + "__filters");

		dateStartField = new DatePicker();
		dateStartField.setLabel("Start Date");
		dateStartField.setLocale(new Locale("fi"));
		dateStartField.setValue(LocalDate.now());
		dateStartField.setRequired(true);
		dateStartField.setErrorMessage("Invalid Date");
		dateStartField.addValueChangeListener(e -> {
			dateStartField.setInvalid(false);

			applyFilters();
		});
		filtersDiv.add(dateStartField);

		dateEndField = new DatePicker();
		dateEndField.setLabel("End Date");
		dateEndField.setLocale(new Locale("fi"));
		dateEndField.setValue(LocalDate.now());
		dateEndField.setRequired(true);
		dateEndField.setErrorMessage("Invalid Date");
		dateEndField.addValueChangeListener(e -> {
			dateEndField.setInvalid(false);

			applyFilters();
		});
		filtersDiv.add(dateEndField);

		showReadMessagesCheckbox = new Checkbox("Show Read Messages");
		showReadMessagesCheckbox.setValue(true);
		showReadMessagesCheckbox.addValueChangeListener(e -> applyFilters());
		filtersDiv.add(showReadMessagesCheckbox);

		return filtersDiv;
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
		grid.addClassNames("grid-view", "small-padding-cell");
		grid.setSizeFull();

//		dataProvider = DataProvider.ofCollection(MessageFacade.getInstance().getAllMessagesBetweenDatesByUser(dateStartField.getValue(), dateEndField.getValue(), AuthenticationService.getCurrentSessionUser().getId()));
		dataProvider = DataProvider.ofCollection(new ArrayList<>());

		getMessagesBetweenDates();

		grid.setDataProvider(dataProvider);

		// READ
		grid.addComponentColumn(msg -> {
					Icon icon;

					if (msg.isMessageRead()) {
						icon = new Icon(VaadinIcon.ENVELOPE_OPEN);
						icon.setColor("var(--lumo-header-text-color)");
					} else {
						icon = new Icon(VaadinIcon.ENVELOPE);
						icon.setColor("var(--lumo-primary-color)");
					}
					return icon;
				})
				.setTextAlign(ColumnTextAlign.CENTER)
				.setWidth("50px")
				.setFlexGrow(0);

		grid.addColumn(Message::getSender)
				.setHeader("Sender")
				.setAutoWidth(true)
				.setFlexGrow(0);

		grid.addColumn(Message::getSubject)
				.setHeader("Subject")
//				.setWidth("200px")
				.setAutoWidth(true)
				.setFlexGrow(0);

		grid.addColumn(Message::getText)
				.setHeader("Message")
				.setFlexGrow(1);

		grid.addColumn(Message::getDateWithTimeString)
				.setHeader("Received")
				.setAutoWidth(true)
				.setTextAlign(ColumnTextAlign.END)
				.setFlexGrow(0);

		grid.asSingleSelect().addValueChangeListener(e -> {
			if (grid.asSingleSelect().getValue() != null) {
				showDetails(grid.asSingleSelect().getValue());
			} else {
				detailsDrawer.hide();
			}
		});

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

		Button closeDetailsButton = UIUtils.createButton("Close", ButtonVariant.LUMO_PRIMARY);
		closeDetailsButton.addClickListener(e -> closeDetails());
		footer.add(closeDetailsButton);

		Button replyButton = UIUtils.createButton("Reply", VaadinIcon.REPLY, ButtonVariant.LUMO_SUCCESS, ButtonVariant.LUMO_PRIMARY);
		replyButton.addClickListener(reply -> {
			Message message = grid.asSingleSelect().getValue();

			if (message != null) {
				if (message.getSenderUser() == null) {
					UIUtils.showNotification("Cannot reply to: " + message.getSenderString(), NotificationVariant.LUMO_PRIMARY);
				} else {
					constructMessageDialog(message);
				}
			}
		});
		footer.add(replyButton);


		return footer;
	}


	private void toggleFilters() {
		if (dateStartField.isInvalid() || dateEndField.isInvalid()) {
			return;
		}

		filtersDiv.getElement().setAttribute("hidden", !filtersVisible);
		filtersVisible = !filtersVisible;
	}

	private void applyFilters() {
		dataProvider.clearFilters();

		if (dateStartField.isInvalid() || dateEndField.isInvalid()) {
			return;
		}

		try {
			DateConverter.localDateToString(dateStartField.getValue());
		} catch (Exception e) {
			dateStartField.setInvalid(true);

			if (!filtersVisible) {
				toggleFilters();
			}

			return;
		}

		try {
			DateConverter.localDateToString(dateEndField.getValue());
		} catch (Exception e) {
			dateEndField.setInvalid(true);

			if (!filtersVisible) {
				toggleFilters();
			}

			return;
		}

		if (dateStartField.getValue().isAfter(dateEndField.getValue())) {
			UIUtils.showNotification("Start Date cannot be after End Date", NotificationVariant.LUMO_PRIMARY);
			return;
		}

		getMessagesBetweenDates();

		dataProvider.addFilter(message -> {
			if (!message.isMessageRead()) {
				return true;
			} else {
				return message.isMessageRead() == showReadMessagesCheckbox.getValue();
			}
		});

		filterGrid(searchField.getValue());
	}

	private void filterGrid(String searchString) {

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
		List<Message> messages = MessageFacade.getInstance().getAllMessagesBetweenDatesByUser(
				dateStartField.getValue(),
				dateEndField.getValue(),
				AuthenticationService.getCurrentSessionUser().getId());
		
		messages.sort(Comparator.comparing(Message::getDate).reversed());

		dataProvider.getItems().clear();
		dataProvider.getItems().addAll(messages);

		dataProvider.refreshAll();
	}

	private void constructMessageDialog(Message replyMessage) {
		CustomDialog dialog = new CustomDialog();
		dialog.setCloseOnOutsideClick(false);
		dialog.closeOnCancel();

		dialog.setHeader(UIUtils.createH3Label("Compose Message"));

		MessageView messageView = new MessageView(replyMessage);
		dialog.setContent(messageView);

		dialog.getConfirmButton().setText("Send");
		dialog.getConfirmButton().addClickListener(e -> {

			boolean error = false;
			List<Message> messages = messageView.getMessageList();
			if (messages != null) {

				for (Message message : messages) {
					if (!sendMessage(message)) {
						UIUtils.showNotification("Message send failed for: " + UserFacade.getInstance().getUserById(message.getRecipientId()).getFullName(), NotificationVariant.LUMO_ERROR);
						error = true;
					}
				}

				if (!error) {
					String text = (messages.size() > 1) ? "Messages sent" : "Message sent";
					UIUtils.showNotification(text, NotificationVariant.LUMO_SUCCESS);
				}

				dialog.close();
			}
		});

		dialog.open();
	}

	private boolean sendMessage(Message message) {
		if (MessageFacade.getInstance().insert(message)) {
			Broadcaster.broadcastToUser(message.getRecipientId(), "You have new message");
			return true;
		} else {
			return false;
		}
	}
}
