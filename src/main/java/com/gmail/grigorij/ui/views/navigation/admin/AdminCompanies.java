package com.gmail.grigorij.ui.views.navigation.admin;

import com.gmail.grigorij.backend.database.facades.CompanyFacade;
import com.gmail.grigorij.backend.database.facades.UserFacade;
import com.gmail.grigorij.backend.entities.company.Company;
import com.gmail.grigorij.backend.access.Status;
import com.gmail.grigorij.backend.entities.user.User;
import com.gmail.grigorij.ui.utils.components.CustomDialog;
import com.gmail.grigorij.ui.utils.components.ListItem;
import com.gmail.grigorij.ui.utils.components.detailsdrawer.DetailsDrawer;
import com.gmail.grigorij.ui.utils.components.detailsdrawer.DetailsDrawerFooter;
import com.gmail.grigorij.ui.utils.components.detailsdrawer.DetailsDrawerHeader;
import com.gmail.grigorij.ui.utils.UIUtils;
import com.gmail.grigorij.ui.utils.css.FlexDirection;
import com.gmail.grigorij.ui.utils.forms.CompanyForm;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;

import java.util.EnumSet;
import java.util.List;

class AdminCompanies extends Div {

	private static final String CLASS_NAME = "admin-companies";
	final static String TAB_NAME = "Companies";

	private AdminMain adminMain;
	private CompanyForm companyForm = new CompanyForm();

	private Grid<Company> grid;
	private ListDataProvider<Company>  dataProvider;

	private DetailsDrawer detailsDrawer;
	private DetailsDrawerFooter detailsDrawerFooter;


	AdminCompanies(AdminMain adminMain) {
		this.adminMain = adminMain;
		setId("admin-companies");
		setClassName(CLASS_NAME);
		setSizeFull();
		createHeader();
		createGrid();
		createDetailsDrawer();
	}


	private void createHeader() {
		FlexLayout header = new FlexLayout();
		header.setClassName(CLASS_NAME + "__header");

		TextField searchField = new TextField();
		searchField.setClassName(CLASS_NAME + "__search");
		searchField.setClearButtonVisible(true);
		searchField.setPrefixComponent(VaadinIcon.SEARCH.create());
		searchField.setPlaceholder("Search Companies");

		header.add(searchField);

		FlexLayout buttons = new FlexLayout();
		buttons.setClassName(CLASS_NAME + "__buttons");

		Button newCompanyButton = UIUtils.createButton("New Company", VaadinIcon.PLUS);
		newCompanyButton.addClickListener(e -> {
			grid.select(null);
			showDetails(null);
		});
		buttons.add(newCompanyButton);

		Button importCompaniesButton = UIUtils.createButton("Import", VaadinIcon.DOWNLOAD);
		importCompaniesButton.addClickListener(e -> importCompanies());
		buttons.add(importCompaniesButton);

		Button exportCompaniesButton = UIUtils.createButton("Export", VaadinIcon.UPLOAD);
		exportCompaniesButton.addClickListener(e -> exportCompanies());
		buttons.add(exportCompaniesButton);

		header.add(buttons);

		add(header);
	}


	private void createGrid() {
		grid = new Grid<>();
		grid.setId("companies-grid");
		grid.setClassName("grid-view");
		grid.setSizeFull();
		grid.asSingleSelect().addValueChangeListener(e -> {
			if (grid.asSingleSelect().getValue() != null) {
				showDetails(grid.asSingleSelect().getValue());
			} else {
				detailsDrawer.hide();
			}
		});

		dataProvider = DataProvider.ofCollection(CompanyFacade.getInstance().getAllCompanies());
		grid.setDataProvider(dataProvider);

		grid.addColumn(Company::getId).setHeader("ID")
				.setWidth(UIUtils.COLUMN_WIDTH_XS)
				.setFlexGrow(0);
		grid.addColumn(Company::getCompanyName).setHeader("Name");
		grid.addColumn(new ComponentRenderer<>(this::createContactPersonInfo)).setHeader("Contact Person")
				.setWidth(UIUtils.COLUMN_WIDTH_XL);
		grid.addColumn(new ComponentRenderer<>(selectedCompany -> UIUtils.createActiveGridIcon(selectedCompany.isDeleted()))).setHeader("Active")
				.setWidth(UIUtils.COLUMN_WIDTH_XS)
				.setFlexGrow(0);

		add(grid);
	}


	private Component createContactPersonInfo(Company company) {
		ListItem item = new ListItem(UIUtils.createInitials(company.getInitials()), company.getFirstName() + " " + company.getLastName(), company.getEmail());
		item.setHorizontalPadding(false);
		return item;
	}


	private void createDetailsDrawer() {
		detailsDrawer = new DetailsDrawer(DetailsDrawer.Position.RIGHT);
		detailsDrawer.setContent(companyForm);

		// Header
		DetailsDrawerHeader detailsDrawerTitle = new DetailsDrawerHeader("Company Details");

		detailsDrawer.setHeader(detailsDrawerTitle);
		detailsDrawer.getHeader().setFlexDirection(FlexDirection.COLUMN);

		// Footer
		detailsDrawerFooter = new DetailsDrawerFooter();
		detailsDrawerFooter.getSave().addClickListener(e -> updateCompany());
		detailsDrawerFooter.getCancel().addClickListener(e -> detailsDrawer.hide());
		detailsDrawerFooter.getDelete().addClickListener(e -> confirmCompanyDelete());
		detailsDrawer.setFooter(detailsDrawerFooter);

		adminMain.setDetailsDrawer(detailsDrawer);
	}

	private boolean previousStatus;

	private void showDetails(Company company) {
		detailsDrawerFooter.getDelete().setEnabled( company != null );

		if (company != null)
			previousStatus = company.isDeleted();

		companyForm.setCompany(company);
		detailsDrawer.show();
	}


	private void updateCompany() {
		System.out.println("updateCompany()");

		Company editedCompany = companyForm.getCompany();

		if (editedCompany != null) {
			if (CompanyFacade.getInstance().update(editedCompany)) {
				if (companyForm.isNewCompany()) {
					dataProvider.getItems().add(editedCompany);
					dataProvider.refreshAll();
				} else {
					if ((!previousStatus && editedCompany.isDeleted()) || (previousStatus && !editedCompany.isDeleted())) {
						confirmAllEmployeesInCompanyStatusChange(editedCompany);
					}
					dataProvider.refreshItem(grid.asSingleSelect().getValue());
				}

				grid.select(editedCompany);
			}
		}
	}

	/**
	 * Prompt to change status of all employees to company status
	 */
	private void confirmAllEmployeesInCompanyStatusChange(Company company) {
		String status = "";

		for (Status s : EnumSet.allOf(Status.class)) {
			if (s.getBooleanValue() == !previousStatus) {
				status = s.getStringValue();
			}
		}

		CustomDialog dialog = new CustomDialog();
		dialog.setHeader(UIUtils.createH4Label("Employees status change"));
		dialog.setContent(
				new Span("Would you also like to set all employees in:"),
				UIUtils.createBoldText(company.getCompanyName()),
				new Span("as " + status + "?"));

		dialog.setConfirmButton(UIUtils.createButton("Set", ButtonVariant.LUMO_SUCCESS, ButtonVariant.LUMO_TERTIARY));
		dialog.getConfirmButton().addClickListener(e -> {

			List<User> employeesInSelectedCompany = UserFacade.getInstance().listUsersByCompanyId(company.getId());

			for (User user : employeesInSelectedCompany) {
				user.setDeleted(!previousStatus);
				UserFacade.getInstance().update(user);
			}
			dialog.close();
		});
		dialog.open();
	}

	private void confirmCompanyDelete() {
		System.out.println("Delete selected company...");

		if (detailsDrawer.isOpen()) {

			System.out.println("selectedCompany: " + grid.asSingleSelect().getValue());
			final Company selectedCompany = grid.asSingleSelect().getValue();

			if (selectedCompany != null) {

				CustomDialog dialog = new CustomDialog();
				dialog.setHeader(UIUtils.createH4Label("Confirm delete"));

				dialog.setConfirmButton(UIUtils.createButton("Delete", VaadinIcon.TRASH, ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_TERTIARY));
				dialog.getConfirmButton().setEnabled(false);

				TextField confirmInputField = new TextField("Input company name to confirm action");
				confirmInputField.setRequired(true);
				confirmInputField.addValueChangeListener(e -> {
					dialog.getConfirmButton().setEnabled(false);

					if (e.getValue() != null) {
						if (e.getValue().length() > 0) {
							if (e.getValue().equals(selectedCompany.getCompanyName())) {
								dialog.getConfirmButton().setEnabled(true);
							}
						}
					}
				});

				dialog.setContent(
						new Span("Are you sure you want to delete this company?"),
						new Span("This will completely remove selected company from Database."),
						new HorizontalLayout(new Span("Deleting company: "), UIUtils.createBoldText(selectedCompany.getCompanyName())),
						confirmInputField
				);

				dialog.getConfirmButton().addClickListener(e -> {
					if (CompanyFacade.getInstance().remove(selectedCompany)) {
						dataProvider.getItems().remove(selectedCompany);
						dataProvider.refreshAll();
						detailsDrawer.hide();
					}
					dialog.close();
				});
				dialog.open();
			}
		}
	}


	private void exportCompanies() {
		System.out.println("Export companies...");


	}


	private void importCompanies() {
		System.out.println("Import companies...");


	}
}

