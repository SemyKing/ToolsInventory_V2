package com.gmail.grigorij.ui.views.navigation.admin;

import com.gmail.grigorij.backend.database.Facades.CompanyFacade;
import com.gmail.grigorij.backend.database.Facades.UserFacade;
import com.gmail.grigorij.backend.entities.company.Company;
import com.gmail.grigorij.backend.entities.user.Person;
import com.gmail.grigorij.backend.entities.user.User;
import com.gmail.grigorij.ui.components.ListItem;
import com.gmail.grigorij.ui.components.detailsdrawer.DetailsDrawer;
import com.gmail.grigorij.ui.components.detailsdrawer.DetailsDrawerFooter;
import com.gmail.grigorij.ui.components.detailsdrawer.DetailsDrawerHeader;
import com.gmail.grigorij.ui.util.LumoStyles;
import com.gmail.grigorij.ui.util.UIUtils;
import com.gmail.grigorij.ui.util.css.FlexDirection;
import com.gmail.grigorij.utils.Constants;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.validator.EmailValidator;

import java.util.List;

class AdminPersonnel extends Div {

	private static final String CLASS_NAME = "admin-personnel";

	final static String TAB_NAME = "Personnel";
	private User selectedUser;
	private AdminMain adminMain;
	private DetailsDrawer detailsDrawer;

	AdminPersonnel(AdminMain adminMain) {
		this.adminMain = adminMain;
		setId("admin-personnel");
		createHeader();
		createGrid();
		createDetailsDrawer();
	}

	private TextField searchField;
	private Button newUserButton;

	private void createHeader() {
		HorizontalLayout headerHL = new HorizontalLayout();
		headerHL.setClassName(CLASS_NAME + "__header");

		searchField = new TextField("Personnel Search");
		searchField.setClassName(CLASS_NAME + "__search");
		searchField.setClearButtonVisible(true);

		HorizontalLayout emptySpace = new HorizontalLayout();
		emptySpace.setClassName(CLASS_NAME + "__empty-space");

		newUserButton = UIUtils.createButton("New User", VaadinIcon.PLUS, ButtonVariant.LUMO_PRIMARY);
		newUserButton.setClassName(CLASS_NAME + "__new-user-button");

		headerHL.add(searchField, emptySpace, newUserButton);
		add(headerHL);
	}


	private void createGrid() {
		Grid<User> grid = new Grid<>();
		grid.addSelectionListener(event -> event.getFirstSelectedItem().ifPresent(item -> {
			showDetails(item);
			selectedUser = item;
		}));
		ListDataProvider<User> dataProvider = DataProvider.ofCollection(UserFacade.getInstance().listAllUsers());
		grid.setDataProvider(dataProvider);
		grid.addColumn(User::getId).setHeader("ID");
		grid.addColumn(User::getUsername).setHeader("Username");
		grid.addColumn(new ComponentRenderer<>(this::createUserInfo)).setHeader("User").setWidth(UIUtils.COLUMN_WIDTH_XL);

		add(grid);
	}

	private Component createUserInfo(User user) {
		ListItem item = new ListItem(UIUtils.createInitials(user.getInitials()), user.getFirstName(), user.getEmail());
		item.setHorizontalPadding(false);
		return item;
	}


	private void createDetailsDrawer() {
		detailsDrawer = new DetailsDrawer(DetailsDrawer.Position.RIGHT);

		// Header
		DetailsDrawerHeader detailsDrawerTitle = new DetailsDrawerHeader("User Details");

		detailsDrawer.setHeader(detailsDrawerTitle);
		detailsDrawer.getHeader().setFlexDirection(FlexDirection.COLUMN);

		// Footer
		DetailsDrawerFooter detailsDrawerFooter = new DetailsDrawerFooter();
		detailsDrawerFooter.addSaveListener(e -> saveUserDetails());
		detailsDrawerFooter.addCancelListener(e -> detailsDrawer.hide());
		detailsDrawer.setFooter(detailsDrawerFooter);

		adminMain.setDetailsDrawer(detailsDrawer);
	}


	private void showDetails(User user) {
		detailsDrawer.setContent(createDetails(user));
		detailsDrawer.show();
	}


//	private TextField usernameField;
//	private PasswordField passwordField;
//	private RadioButtonGroup<String> status;


//	Binder<Trip> binder = new Binder<>(Trip.class);

	Binder<User> userBinder;
	Binder<Person> personBinder;

	private Component createDetails(User user) {

		TextField usernameField = new TextField();
		usernameField.setValue(user.getUsername());
		usernameField.setWidth("100%");

		PasswordField passwordField = new PasswordField();
		passwordField.setValue(user.getPassword());
		passwordField.setWidth("100%");

		RadioButtonGroup<String> status = new RadioButtonGroup<>();
		status.setItems(Constants.ACTIVE_STR, Constants.INACTIVE_STR );
		status.setValue(user.isDeleted() ? Constants.ACTIVE_STR : Constants.INACTIVE_STR);

		FlexLayout phone = UIUtils.createPhoneLayout();

		TextField emailField = new TextField();
		emailField.setValue(user.getEmail());
		emailField.setWidth("100%");

		ComboBox<Company> company = new ComboBox<>();
		List<Company> companies = CompanyFacade.getInstance().listAllCompanies();
        company.setItems(companies);
		company.setValue(null);

		for (Company value : companies) {
			if (user.getCompany_id() == value.getId()) {
				company.setValue(value);
				break;
			}
		}
		company.setWidth("100%");

		// Form layout
		FormLayout form = new FormLayout();
		form.addClassNames(LumoStyles.Padding.Bottom.L, LumoStyles.Padding.Horizontal.L, LumoStyles.Padding.Top.S);
		form.setResponsiveSteps(
				new FormLayout.ResponsiveStep("0", 1, FormLayout.ResponsiveStep.LabelsPosition.TOP),
				new FormLayout.ResponsiveStep("600px", 2, FormLayout.ResponsiveStep.LabelsPosition.TOP),
				new FormLayout.ResponsiveStep("1024px", 3, FormLayout.ResponsiveStep.LabelsPosition.TOP));
		form.addFormItem(usernameField, "First Name");
		form.addFormItem(passwordField, "Last Name");
		form.addFormItem(status, "Status");
		form.addFormItem(phone, "Phone");
		form.addFormItem(emailField, "Email");
		form.addFormItem(company, "Company");
//		form.addFormItem(new Upload(), "Image");


		userBinder = new Binder<>(User.class);
		personBinder = new Binder<>(Person.class);

		userBinder.forField(usernameField)
				.asRequired("Username must not be empty")
				.bind(User::getUsername, User::setUsername);

		userBinder.forField(passwordField)
				.asRequired("Password must not be empty")
				.bind(User::getPassword, User::setPassword);

		personBinder.forField(emailField)
			.withValidator(new EmailValidator("This doesn't look like a valid email address"))
			.bind(Person::getEmail, Person::setEmail);


		return form;



//		usernameField = new TextField("Username");
//		usernameField.getStyle().set("width", "100%");
//		usernameField.getStyle().set("padding", "5px");
//		usernameField.setValue(user.getUsername());
//
//		passwordField = new PasswordField("Password");
//		passwordField.getStyle().set("width", "100%");
//		passwordField.getStyle().set("padding", "5px");
//		passwordField.setValue(user.getPassword());
//
//		Div details = new Div(usernameField, passwordField);
//		details.addClassName(LumoStyles.Padding.Vertical.S);
//		return details;
	}

	private void saveUserDetails() {
		System.out.println("saveUserDetails()");

		userBinder.validate();
		personBinder.validate();

		if (userBinder.isValid()) {
			System.out.println("User Fields valid");
		}

		if (personBinder.isValid()) {
			System.out.println("Person Fields valid");
		}

//		User user = selectedUser;
//		selectedUser.setUsername(usernameField.getValue());
//		selectedUser.setPassword(passwordField.getValue());
//
//		if (UserFacade.getInstance().update(user)) {
//			UIUtils.showNotification("User updated successfully!");
//		} else {
//			UIUtils.showNotification("User update failed!");
//		}

	}
}
