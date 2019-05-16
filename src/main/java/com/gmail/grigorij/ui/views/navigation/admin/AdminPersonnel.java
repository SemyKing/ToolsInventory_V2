package com.gmail.grigorij.ui.views.navigation.admin;

import com.gmail.grigorij.backend.database.Facades.UserFacade;
import com.gmail.grigorij.backend.entities.company.Company;
import com.gmail.grigorij.backend.entities.user.User;
import com.gmail.grigorij.ui.util.LumoStyles;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.ListDataProvider;

public class AdminPersonnel extends Div{

	final static String TAB_NAME = "Personnel";

	public AdminPersonnel() {
		setId("admin-personnel");
		initHeader();
		initGrid();

		AdminMain.detailsDrawerTitle.setTitle("User Details");
		AdminMain.detailsDrawerFooter.addSaveListener(e -> saveUser());
	}


	private void initHeader() {
		add(new TextField(" Personnel Search"));
	}


	private void initGrid() {
		Grid<User> grid = new Grid<>();
		grid.addSelectionListener(event -> event.getFirstSelectedItem().ifPresent(this::showDetails));
		ListDataProvider<User> dataProvider = DataProvider.ofCollection(UserFacade.getInstance().listAllUsers());
		grid.setDataProvider(dataProvider);
//		grid.setHeight("100%");

		grid.addColumn(User::getId).setHeader("ID (Database)");
		grid.addColumn(User::getUsername).setHeader("Username");

		add(grid);
	}


	private void showDetails(User user) {
		AdminMain.detailsDrawer.setContent(createDetails(user));
		AdminMain.detailsDrawer.show();
	}

	private Component createDetails(User user) {
		TextField usernameField = new TextField("Username");
		usernameField.getStyle().set("width", "100%");
		usernameField.getStyle().set("padding", "5px");
		usernameField.setValue(user.getUsername());

		PasswordField passwordField = new PasswordField("Password");
		passwordField.getStyle().set("width", "100%");
		passwordField.getStyle().set("padding", "5px");
		passwordField.setValue(user.getPassword());


		Div details = new Div(usernameField, passwordField);
		details.addClassName(LumoStyles.Padding.Vertical.S);
		return details;
	}

	private void saveUser() {
		System.out.println("saveUser()");
	}
}
