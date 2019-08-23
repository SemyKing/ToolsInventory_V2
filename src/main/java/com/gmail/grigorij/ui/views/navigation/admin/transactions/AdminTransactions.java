package com.gmail.grigorij.ui.views.navigation.admin.transactions;

import com.gmail.grigorij.backend.database.facades.TransactionFacade;
import com.gmail.grigorij.backend.entities.transaction.Transaction;
import com.gmail.grigorij.ui.utils.UIUtils;
import com.gmail.grigorij.ui.utils.components.FlexBoxLayout;
import com.gmail.grigorij.ui.utils.components.detailsdrawer.DetailsDrawer;
import com.gmail.grigorij.ui.utils.components.detailsdrawer.DetailsDrawerFooter;
import com.gmail.grigorij.ui.utils.components.detailsdrawer.DetailsDrawerHeader;
import com.gmail.grigorij.ui.utils.css.Display;
import com.gmail.grigorij.ui.utils.css.FlexDirection;
import com.gmail.grigorij.ui.utils.css.size.Horizontal;
import com.gmail.grigorij.ui.utils.css.size.Left;
import com.gmail.grigorij.ui.utils.css.size.Right;
import com.gmail.grigorij.ui.utils.css.size.Top;
import com.gmail.grigorij.ui.utils.forms.readonly.ReadOnlyTransactionForm;
import com.gmail.grigorij.ui.views.navigation.admin.AdminMain;
import com.gmail.grigorij.utils.ProjectConstants;
import com.gmail.grigorij.utils.converters.DateConverter;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.value.ValueChangeMode;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

public class AdminTransactions extends FlexBoxLayout {

	private static final String CLASS_NAME = "admin-transactions";

	private final AdminMain adminMain;

	private ReadOnlyTransactionForm transactionsForm = new ReadOnlyTransactionForm();

	private Grid<Transaction> grid;
	private ListDataProvider<Transaction> dataProvider;

	private DetailsDrawer detailsDrawer;

	private DatePicker dateStartField, dateEndField;

	public AdminTransactions(AdminMain adminMain) {
		this.adminMain = adminMain;
		setClassName(CLASS_NAME);
		setSizeFull();
		setDisplay(Display.FLEX);
		setFlexDirection(FlexDirection.COLUMN);

		createHeader();
		createGrid();
		createDetailsDrawer();
	}

	private void createHeader() {
		FlexBoxLayout header = new FlexBoxLayout();
		header.setClassName(CLASS_NAME + "__header");
		header.setAlignItems(Alignment.BASELINE);
		header.setWidthFull();

		TextField searchField = new TextField();
		searchField.setWidth("100%");
		searchField.setClearButtonVisible(true);
		searchField.setPrefixComponent(VaadinIcon.SEARCH.create());
		searchField.setPlaceholder("Search Transactions");
		searchField.setValueChangeMode(ValueChangeMode.LAZY);
//		searchField.addValueChangeListener(event -> filterGrid(searchField.getValue()));

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

		add(header);
	}

	private void createGrid() {
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
					return DateConverter.toStringDateWithTime(transaction.getDate());
				}
			} catch (Exception e) {
				return "";
			}
		})
				.setHeader("Date")
				.setWidth(UIUtils.COLUMN_WIDTH_L)
				.setFlexGrow(0);

		grid.addColumn(Transaction::getShortName)
				.setHeader("Operation")
				.setWidth(UIUtils.COLUMN_WIDTH_M)
				.setFlexGrow(0);

		grid.addColumn(transaction -> (transaction.getWhoDid() == null) ? "" : transaction.getWhoDid().getUsername())
				.setHeader("Who Did")
				.setWidth(UIUtils.COLUMN_WIDTH_M);

		add(grid);
	}

	private void getTransactionsBetweenDates() {
		//Handle errors
		if (dateStartField.getValue() == null) {
			UIUtils.showNotification("Select Start Date", UIUtils.NotificationType.INFO);
			dateStartField.focus();
			return;
		}

		if (dateEndField.getValue() == null) {
			UIUtils.showNotification("Select End Date", UIUtils.NotificationType.INFO);
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





	private void createDetailsDrawer() {
		detailsDrawer = new DetailsDrawer(DetailsDrawer.Position.RIGHT);
		detailsDrawer.getElement().setAttribute(ProjectConstants.FORM_LAYOUT_LARGE_ATTR, true);
		detailsDrawer.setContent(transactionsForm);
		detailsDrawer.setContentPadding(Left.M, Right.S);

		// Header
		DetailsDrawerHeader detailsDrawerHeader = new DetailsDrawerHeader("Transaction Details");
		detailsDrawerHeader.getClose().addClickListener(e -> closeDetails());

		detailsDrawer.setHeader(detailsDrawerHeader);
		detailsDrawer.getHeader().setFlexDirection(FlexDirection.COLUMN);

		// Footer
		DetailsDrawerFooter detailsDrawerFooter = new DetailsDrawerFooter();
		detailsDrawerFooter.removeButton(detailsDrawerFooter.getSave());
		detailsDrawerFooter.getCancel().addClickListener(e -> closeDetails());
		detailsDrawer.setFooter(detailsDrawerFooter);

		adminMain.setDetailsDrawer(detailsDrawer);
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
		grid.select(null);
	}
}
