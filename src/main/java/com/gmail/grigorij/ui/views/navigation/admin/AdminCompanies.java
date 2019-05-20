package com.gmail.grigorij.ui.views.navigation.admin;

import com.gmail.grigorij.backend.database.Facades.CompanyFacade;
import com.gmail.grigorij.backend.entities.company.Company;
import com.gmail.grigorij.backend.entities.user.User;
import com.gmail.grigorij.ui.components.detailsdrawer.DetailsDrawer;
import com.gmail.grigorij.ui.components.detailsdrawer.DetailsDrawerFooter;
import com.gmail.grigorij.ui.components.detailsdrawer.DetailsDrawerHeader;
import com.gmail.grigorij.ui.util.LumoStyles;
import com.gmail.grigorij.ui.util.UIUtils;
import com.gmail.grigorij.ui.util.css.FlexDirection;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.ListDataProvider;


class AdminCompanies extends Div {

	private static final String CLASS_NAME = "admin-companies";

	final static String TAB_NAME = "Companies";
	private Company selectedCompany;
	private AdminMain adminMain;
	private DetailsDrawer detailsDrawer;


	AdminCompanies(AdminMain adminMain) {
		this.adminMain = adminMain;
		setId("admin-companies");
		createHeader();
		createGrid();
		createDetailsDrawer();
	}


	private TextField searchField;
	private Button newUserButton;

	private void createHeader() {
		HorizontalLayout headerHL = new HorizontalLayout();
		headerHL.setClassName(CLASS_NAME + "__header");

		searchField = new TextField("Companies Search");
		searchField.setClassName(CLASS_NAME + "__search");
		searchField.setClearButtonVisible(true);

		HorizontalLayout emptySpace = new HorizontalLayout();
		emptySpace.setClassName(CLASS_NAME + "__empty-space");

		newUserButton = UIUtils.createButton("New Company", VaadinIcon.PLUS, ButtonVariant.LUMO_PRIMARY);
		newUserButton.setClassName(CLASS_NAME + "__new-company-button");
		newUserButton.addClickListener(e -> constructNewCompanyDialog());

		headerHL.add(searchField, emptySpace, newUserButton);
		add(headerHL);
	}


	private void createGrid() {
		Grid<Company> grid = new Grid<>();
		grid.setId("companies-grid");
		grid.addSelectionListener(event -> event.getFirstSelectedItem().ifPresent(item -> {
			showDetails(item);
			selectedCompany = item;
		}));
		ListDataProvider<Company> dataProvider = DataProvider.ofCollection(CompanyFacade.getInstance().listAllCompanies());
		grid.setDataProvider(dataProvider);

		grid.addColumn(Company::getId).setHeader("ID (Database)");
		grid.addColumn(Company::getCompanyName).setHeader("Name");

		add(grid);
	}


	private void createDetailsDrawer() {
		detailsDrawer = new DetailsDrawer(DetailsDrawer.Position.RIGHT);

		// Header
		DetailsDrawerHeader detailsDrawerTitle = new DetailsDrawerHeader("Company Details");

		detailsDrawer.setHeader(detailsDrawerTitle);
		detailsDrawer.getHeader().setFlexDirection(FlexDirection.COLUMN);

		// Footer
		DetailsDrawerFooter detailsDrawerFooter = new DetailsDrawerFooter();
		detailsDrawerFooter.addSaveListener(e -> saveCompanyDetails());
		detailsDrawerFooter.addCancelListener(e -> detailsDrawer.hide());
		detailsDrawer.setFooter(detailsDrawerFooter);

		adminMain.setDetailsDrawer(detailsDrawer);
	}


	private void showDetails(Company company) {
		detailsDrawer.setContent(createDetails(company));
		detailsDrawer.show();
	}


	private Component createDetails(Company company) {
		TextField companyNameField = new TextField("Company Name");
		companyNameField.getStyle().set("width", "100%");
		companyNameField.getStyle().set("padding", "5px");
		companyNameField.setValue(company.getCompanyName());

		Div details = new Div(companyNameField);
		details.addClassName(LumoStyles.Padding.Vertical.S);
		return details;
	}


	private void saveCompanyDetails() {
		System.out.println("saveCompanyDetails()");


	}




	private void constructNewCompanyDialog() {
		Dialog newCompanyDialog = new Dialog();



	}
}

