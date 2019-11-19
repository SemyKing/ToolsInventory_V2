package com.gmail.grigorij.ui.views.app;

import com.gmail.grigorij.backend.database.entities.Transaction;
import com.gmail.grigorij.backend.database.enums.operations.Operation;
import com.gmail.grigorij.backend.database.enums.operations.OperationTarget;
import com.gmail.grigorij.backend.database.enums.permissions.PermissionLevel;
import com.gmail.grigorij.backend.database.facades.PermissionFacade;
import com.gmail.grigorij.backend.database.facades.TransactionFacade;
import com.gmail.grigorij.ui.components.detailsdrawer.DetailsDrawer;
import com.gmail.grigorij.ui.components.detailsdrawer.DetailsDrawerFooter;
import com.gmail.grigorij.ui.components.detailsdrawer.DetailsDrawerHeader;
import com.gmail.grigorij.ui.components.forms.TransactionForm;
import com.gmail.grigorij.ui.utils.UIUtils;
import com.gmail.grigorij.utils.authentication.AuthenticationService;
import com.gmail.grigorij.utils.DateConverter;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.value.ValueChangeMode;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;


@CssImport("./styles/views/transactions.css")
public class TransactionsView extends Div {

	private static final String CLASS_NAME = "transactions";
	private final TransactionForm transactionsForm = new TransactionForm();

	private TextField searchField;
	private Div filtersDiv;
	private DatePicker dateStartField, dateEndField;

	private boolean filtersVisible = false;

	private Grid<Transaction> grid;
	private ListDataProvider<Transaction> dataProvider;

	private DetailsDrawer detailsDrawer;


	public TransactionsView() {
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
		header.setClassName(CLASS_NAME + "__header");

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
		searchField.setPlaceholder("Search Transactions");
		searchField.setValueChangeMode(ValueChangeMode.LAZY);
		searchField.addValueChangeListener(event -> applyFilters());
		headerTopDiv.add(searchField);

		Div headerBottomDiv = new Div();
		headerBottomDiv.addClassName(CLASS_NAME + "__header-bottom");
		headerBottomDiv.add(constructTransactionsFilterLayout());
		header.add(headerBottomDiv);

		return header;
	}

	private Div constructTransactionsFilterLayout() {
		filtersDiv = new Div();
		filtersDiv.addClassName(CLASS_NAME + "__filters");

		dateStartField = new DatePicker();
		dateStartField.setLabel("Start Date");
		dateStartField.setLocale(new Locale("fi"));
		dateStartField.setValue(LocalDate.now().minusMonths(1).withDayOfMonth(1));
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
		grid.setId("transactions-grid");
		grid.setClassName("grid-view");
		grid.setSizeFull();
		grid.asSingleSelect().addValueChangeListener(e -> {
			if (grid.asSingleSelect().getValue() != null) {
				showDetails(grid.asSingleSelect().getValue());
			} else {
				detailsDrawer.hide();
			}
		});

		if (AuthenticationService.getCurrentSessionUser().getPermissionLevel().equalsTo(PermissionLevel.SYSTEM_ADMIN)) {
			dataProvider = DataProvider.ofCollection(TransactionFacade.getInstance().getAllTransactionsBetweenDates(
					dateStartField.getValue(), dateEndField.getValue()));
		} else {
			dataProvider = DataProvider.ofCollection(TransactionFacade.getInstance().getAllTransactionsBetweenDatesByCompany(
					dateStartField.getValue(), dateEndField.getValue(), AuthenticationService.getCurrentSessionUser().getCompany().getId()));
		}

		grid.setDataProvider(dataProvider);
		grid.addColumn(transaction -> {
					try {
						if (transaction.getDate() == null) {
							return "";
						} else {
							return DateConverter.dateToStringWithTime(transaction.getDate());
						}
					} catch (Exception e) {
						return "";
					}
				})
				.setHeader("Date")
				.setAutoWidth(true)
				.setFlexGrow(0);

		grid.addColumn(transaction -> transaction.getDescription(false))
				.setHeader("Operation")
				.setAutoWidth(true)
				.setFlexGrow(0);

		grid.addColumn(transaction -> (transaction.getUser() == null) ? "" : transaction.getUser().getFullName())
				.setHeader("Who Did")
				.setAutoWidth(true)
				.setFlexGrow(1);

		return grid;
	}

	private DetailsDrawer constructDetails() {
		detailsDrawer = new DetailsDrawer(DetailsDrawer.Position.RIGHT);

		// Header
		DetailsDrawerHeader detailsDrawerHeader = new DetailsDrawerHeader("Transaction Details");
		detailsDrawerHeader.getClose().addClickListener(e -> closeDetails());
		detailsDrawer.setHeader(detailsDrawerHeader);

		detailsDrawer.setContent(transactionsForm);

		// Footer
		DetailsDrawerFooter detailsDrawerFooter = new DetailsDrawerFooter();
		detailsDrawerFooter.getSave().setEnabled(false);

		if (PermissionFacade.getInstance().isSystemAdminOrAllowedTo(Operation.EDIT, OperationTarget.TRANSACTIONS, null)) {
			detailsDrawerFooter.getSave().setEnabled(true);
			detailsDrawerFooter.getSave().addClickListener(e -> saveTransactionInDatabase());
		}
		detailsDrawerFooter.getClose().addClickListener(e -> closeDetails());

		detailsDrawer.setFooter(detailsDrawerFooter);

		return detailsDrawer;
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

		getTransactionsBetweenDates();

		filterGrid(searchField.getValue());
	}

	private void filterGrid(String searchString) {
		final String mainSearchString = searchString.trim();

		if (mainSearchString.contains("+")) {
			String[] searchParams = mainSearchString.split("\\+");

			dataProvider.addFilter(
					transaction -> {
						boolean res = true;
						for (String sParam : searchParams) {
							res =  StringUtils.containsIgnoreCase(transaction.getDescription(true), sParam) ||
									StringUtils.containsIgnoreCase((transaction.getOperation() == null) ? "" : transaction.getOperation().getName(), sParam) ||
									StringUtils.containsIgnoreCase((transaction.getOperationTarget1() == null) ? "" : transaction.getOperationTarget1().getName(), sParam) ||
									StringUtils.containsIgnoreCase((transaction.getOperationTarget2() == null) ? "" : transaction.getOperationTarget2().getName(), sParam) ||
									StringUtils.containsIgnoreCase((transaction.getUser() == null) ? "" : transaction.getUser().getFullName(), sParam) ||
									StringUtils.containsIgnoreCase((transaction.getCompany() == null) ? "" : transaction.getCompany().getName(), sParam) ||
									StringUtils.containsIgnoreCase((transaction.getDate() == null) ? "" : DateConverter.dateToStringWithTime(transaction.getDate()), sParam);
							if (!res)
								break;
						}
						return res;
					}
			);
		} else {
			dataProvider.addFilter(
					transaction -> StringUtils.containsIgnoreCase(transaction.getDescription(true), mainSearchString) ||
							StringUtils.containsIgnoreCase((transaction.getOperation() == null) ? "" : transaction.getOperation().getName(), mainSearchString) ||
							StringUtils.containsIgnoreCase((transaction.getOperationTarget1() == null) ? "" : transaction.getOperationTarget1().getName(), mainSearchString) ||
							StringUtils.containsIgnoreCase((transaction.getOperationTarget2() == null) ? "" : transaction.getOperationTarget2().getName(), mainSearchString) ||
							StringUtils.containsIgnoreCase((transaction.getUser() == null) ? "" : transaction.getUser().getFullName(), mainSearchString) ||
							StringUtils.containsIgnoreCase((transaction.getCompany() == null) ? "" : transaction.getCompany().getName(), mainSearchString) ||
							StringUtils.containsIgnoreCase((transaction.getDate() == null) ? "" : DateConverter.dateToStringWithTime(transaction.getDate()), mainSearchString)
			);
		}
	}


	private void getTransactionsBetweenDates() {
		List<Transaction> transactions;

		if (AuthenticationService.getCurrentSessionUser().getPermissionLevel().equalsTo(PermissionLevel.SYSTEM_ADMIN)) {
			transactions = TransactionFacade.getInstance().getAllTransactionsBetweenDates(
					dateStartField.getValue(), dateEndField.getValue());
		} else {
			transactions = TransactionFacade.getInstance().getAllTransactionsBetweenDatesByCompany(
					dateStartField.getValue(), dateEndField.getValue(), AuthenticationService.getCurrentSessionUser().getCompany().getId());
		}

		dataProvider.getItems().clear();
		dataProvider.getItems().addAll(transactions);

		dataProvider.refreshAll();
	}

	private void showDetails(Transaction transaction) {
		transactionsForm.setTransaction(transaction);
		detailsDrawer.show();
	}

	private void closeDetails() {
		detailsDrawer.hide();
		grid.deselectAll();
	}

	private void saveTransactionInDatabase() {
		Transaction transaction = transactionsForm.getTransaction();

		if (TransactionFacade.getInstance().update(transaction)) {
			UIUtils.showNotification("Transaction updated", NotificationVariant.LUMO_SUCCESS);
		} else {
			UIUtils.showNotification("Transaction update failed", NotificationVariant.LUMO_ERROR);
		}
	}
}
