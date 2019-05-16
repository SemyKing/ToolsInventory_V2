package com.gmail.grigorij.ui.views.navigation.admin;

import com.gmail.grigorij.backend.database.Facades.CompanyFacade;
import com.gmail.grigorij.backend.entities.company.Company;
import com.gmail.grigorij.ui.util.LumoStyles;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.ListDataProvider;


public class AdminCompanies extends Div {

	final static String TAB_NAME = "Companies";

	public AdminCompanies() {
		setId("admin-companies");
		initHeader();
		initGrid();

		AdminMain.detailsDrawerTitle.setTitle("Company Details");
		AdminMain.detailsDrawerFooter.addSaveListener(e -> saveCompany());
	}


	private void initHeader() {
		add(new TextField(" Companies Search"));
	}

	private void initGrid() {
		Grid<Company> grid = new Grid<>();
		grid.setId("companies-grid");
		grid.addSelectionListener(event -> event.getFirstSelectedItem().ifPresent(this::showDetails));
		ListDataProvider<Company> dataProvider = DataProvider.ofCollection(CompanyFacade.getInstance().listAllCompanies());
		grid.setDataProvider(dataProvider);

		grid.addColumn(Company::getId).setHeader("ID (Database)");
		grid.addColumn(Company::getCompanyName).setHeader("Name");

		add(grid);
	}


	private void showDetails(Company company) {
		AdminMain.detailsDrawer.setContent(createDetails(company));
		AdminMain.detailsDrawer.show();
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

	private void saveCompany() {
		System.out.println("saveCompany()");
	}
}

