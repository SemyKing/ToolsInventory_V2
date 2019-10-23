package com.gmail.grigorij.ui.application.views.admin;

import com.gmail.grigorij.backend.database.facades.TransactionFacade;
import com.gmail.grigorij.backend.entities.transaction.Transaction;
import com.gmail.grigorij.ui.application.views.Admin;
import com.gmail.grigorij.ui.components.detailsdrawer.DetailsDrawer;
import com.gmail.grigorij.ui.components.detailsdrawer.DetailsDrawerFooter;
import com.gmail.grigorij.ui.components.detailsdrawer.DetailsDrawerHeader;
import com.gmail.grigorij.ui.components.forms.TransactionForm;
import com.gmail.grigorij.ui.components.layouts.FlexBoxLayout;
import com.gmail.grigorij.ui.utils.UIUtils;
import com.gmail.grigorij.ui.utils.css.size.Horizontal;
import com.gmail.grigorij.ui.utils.css.size.Left;
import com.gmail.grigorij.utils.DateConverter;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.value.ValueChangeMode;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

public class AdminTransactions extends FlexBoxLayout {

	private static final String CLASS_NAME = "admin-transactions";
	private final TransactionForm transactionsForm = new TransactionForm();
	private final Admin admin;

	private DatePicker dateStartField, dateEndField;

	private Grid<Transaction> grid;
	private ListDataProvider<Transaction> dataProvider;

	private DetailsDrawer detailsDrawer;


	public AdminTransactions(Admin admin) {
		this.admin = admin;
		addClassName(CLASS_NAME);

		add(constructHeader());
		add(constructContent());

		constructDetails();
	}


	private Div constructHeader() {
		Div header = new Div();
		header.setClassName(CLASS_NAME + "__header");

		TextField searchField = new TextField("Search");
		searchField.setClearButtonVisible(true);
		searchField.setPrefixComponent(VaadinIcon.SEARCH.create());
		searchField.setPlaceholder("Search Transactions");
		searchField.setValueChangeMode(ValueChangeMode.LAZY);
		searchField.addValueChangeListener(event -> filterGrid(searchField.getValue()));

		header.add(searchField);

		FlexBoxLayout datesLayout = new FlexBoxLayout();
		datesLayout.addClassName(CLASS_NAME + "__dates");
		datesLayout.setAlignItems(Alignment.BASELINE);
		datesLayout.setMargin(Left.S);

		LocalDate now = LocalDate.now();

		dateStartField = new DatePicker();
		dateStartField.setLabel("Start Date");
		dateStartField.setPlaceholder("Start Date");
		dateStartField.setLocale(new Locale("fi"));
		dateStartField.setValue(now.minusMonths(1).withDayOfMonth(1));

		datesLayout.add(dateStartField);

		dateEndField = new DatePicker();
		dateEndField.setLabel("End Date");
		dateEndField.setPlaceholder("End Date");
		dateEndField.setLocale(new Locale("fi"));
		dateEndField.setValue(now);

		datesLayout.add(dateEndField);
		datesLayout.setComponentMargin(dateEndField, Horizontal.S);

		Button applyDates = UIUtils.createButton("Apply", ButtonVariant.LUMO_CONTRAST);
		applyDates.addClickListener(e -> getTransactionsBetweenDates());

		datesLayout.add(applyDates);


		header.add(datesLayout);

		return header;
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

		dataProvider = DataProvider.ofCollection(TransactionFacade.getInstance().getAllTransactionsBetweenDates(dateStartField.getValue(), dateEndField.getValue()));
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

	private void constructDetails() {
		detailsDrawer = admin.getDetailsDrawer();

		DetailsDrawerHeader detailsDrawerHeader = new DetailsDrawerHeader("Transaction Details");
		detailsDrawerHeader.getClose().addClickListener(e -> closeDetails());
		detailsDrawer.setHeader(detailsDrawerHeader);

		detailsDrawer.setContent(transactionsForm);

		DetailsDrawerFooter detailsDrawerFooter = new DetailsDrawerFooter();
		detailsDrawerFooter.getClose().addClickListener(e -> closeDetails());
		detailsDrawerFooter.getSave().addClickListener(e -> saveOnClick());

		detailsDrawer.setFooter(detailsDrawerFooter);
	}


	private void filterGrid(String searchString) {
		dataProvider.clearFilters();
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
		//Handle errors

		if (dateStartField.isInvalid()) {
			UIUtils.showNotification("Invalid Start Date", UIUtils.NotificationType.INFO);
			dateStartField.focus();
			return;
		}

		if (dateEndField.isInvalid()) {
			UIUtils.showNotification("Invalid End Date", UIUtils.NotificationType.INFO);
			dateEndField.focus();
			return;
		}

		if (dateStartField.getValue().isAfter(dateEndField.getValue())) {
			UIUtils.showNotification("Start Date cannot be after End Date", UIUtils.NotificationType.INFO);
			return;
		}

		List<Transaction> transactions = TransactionFacade.getInstance().getAllTransactionsBetweenDates(dateStartField.getValue(), dateEndField.getValue());

		dataProvider.getItems().clear();
		dataProvider.getItems().addAll(transactions);

		dataProvider.refreshAll();
	}

	private void showDetails(Transaction transaction) {
		if (transaction == null) {
			UIUtils.showNotification("Cannot show details of NULL Transaction", UIUtils.NotificationType.ERROR);
			return;
		}

		transactionsForm.setTransaction(transaction);
		detailsDrawer.show();
	}

	private void closeDetails() {
		detailsDrawer.hide();
		grid.deselectAll();
	}

	private void saveOnClick() {
		Transaction transaction = transactionsForm.getTransaction();

		if (TransactionFacade.getInstance().update(transaction)) {
			UIUtils.showNotification("Transaction updated", UIUtils.NotificationType.SUCCESS);
		} else {
			UIUtils.showNotification("Transaction update failed", UIUtils.NotificationType.ERROR);
		}
	}
}
