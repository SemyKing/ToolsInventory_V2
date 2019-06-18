package com.gmail.grigorij.ui.utils.forms;

import com.gmail.grigorij.backend.database.facades.CompanyFacade;
import com.gmail.grigorij.backend.database.facades.UserFacade;
import com.gmail.grigorij.backend.entities.company.Company;
import com.gmail.grigorij.backend.entities.user.Address;
import com.gmail.grigorij.backend.entities.user.User;
import com.gmail.grigorij.ui.MenuLayout;
import com.gmail.grigorij.ui.utils.UIUtils;
import com.gmail.grigorij.ui.utils.components.CustomDialog;
import com.gmail.grigorij.ui.utils.components.Divider;
import com.gmail.grigorij.ui.utils.components.FlexBoxLayout;
import com.gmail.grigorij.ui.utils.css.LumoStyles;
import com.gmail.grigorij.ui.views.authentication.AuthenticationService;
import com.gmail.grigorij.utils.converters.CustomConverter;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;

import java.util.List;

public class UserForm extends FormLayout {

	private Binder<User> userBinder = new Binder<>(User.class);
	private Binder<Address> addressBinder = new Binder<>(Address.class);

	private boolean newUser;

	private PasswordField passwordField;
	private Button changePasswordButton;

	public UserForm() {
		TextField username = new TextField();
		username.setWidth("100%");
//		username.setEnabled(false);
		username.setReadOnly(true);

		passwordField = new PasswordField();
		passwordField.setWidth("100%");
//		passwordField.setEnabled(false);
		passwordField.setReadOnly(true);

		changePasswordButton = UIUtils.createButton("Change");
		changePasswordButton.addClickListener(e -> openPasswordChangeDialog());
		changePasswordButton.getStyle().set("display", "table");
		changePasswordButton.getStyle().set("margin-left", "10px");

		FlexBoxLayout passwordLayout = new FlexBoxLayout();
		passwordLayout.setWidth("100%");
		passwordLayout.add(passwordField);
		passwordLayout.add(changePasswordButton);

		List<Company> companies = CompanyFacade.getInstance().getAllCompanies();

		ComboBox<Company> companyComboBox = new ComboBox<>();
		companyComboBox.setItems(companies);
		companyComboBox.setItemLabelGenerator(Company::getCompanyName);
		companyComboBox.setWidth("100%");
//		companyComboBox.setEnabled(false);
		companyComboBox.setReadOnly(true);


		Label contactLabel = UIUtils.createH5Label("Contact Information");

		TextField firstName = new TextField();
		firstName.setWidth("100%");
		TextField lastName = new TextField();
		lastName.setWidth("100%");
		TextField phone = new TextField();
		phone.getElement().setAttribute("type", "tel");
		phone.setWidth("100%");
		TextField email = new TextField();
		email.setWidth("100%");


		Label addressLabel = UIUtils.createH5Label("Address");

		TextField addressLine1 = new TextField();
		addressLine1.setWidth("100%");
		TextField addressLine2 = new TextField();
		addressLine2.setWidth("100%");
		TextField postcode = new TextField();
		postcode.setWidth("100%");
		TextField city = new TextField();
		city.setWidth("100%");
		TextField country = new TextField();
		country.setWidth("100%");

		TextArea additionalInfo = new TextArea();
		additionalInfo.setWidth("100%");
		additionalInfo.setMaxHeight("200px");


		// Form layout
//		addClassNames(LumoStyles.Padding.Bottom.L, LumoStyles.Padding.Horizontal.M, LumoStyles.Padding.Top.S);
		setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1, FormLayout.ResponsiveStep.LabelsPosition.TOP));
		addFormItem(username, "Username");
		addFormItem(passwordLayout, "Password");
		addFormItem(companyComboBox, "Company");
		addFormItem(new Divider("1px"), "");
		addFormItem(contactLabel, "");
		addFormItem(firstName, "First Name");
		addFormItem(lastName, "Last Name");
		addFormItem(phone, "Phone Number");
		addFormItem(email, "Email");
		addFormItem(new Divider("1px"), "");
		addFormItem(addressLabel, "");
		addFormItem(addressLine1, "Address Line 1");
		addFormItem(addressLine2, "Address Line 2");
		addFormItem(postcode, "Postcode");
		addFormItem(city, "City");
		addFormItem(country, "Country");
		addFormItem(new Divider("1px"), "");
		addFormItem(additionalInfo, "Additional Info");

		userBinder.forField(username)
				.asRequired("Username is required")
				.bind(User::getUsername, User::setUsername);
		userBinder.forField(passwordField)
				.asRequired("Password is required")
				.bind(User::getPassword, User::setPassword);
		userBinder.forField(companyComboBox)
				.asRequired("Company is required")
				.withConverter(new CustomConverter.CompanyConverter())
				.bind(User::getCompany_id, User::setCompany_id);
		userBinder.forField(firstName)
				.bind(User::getFirstName, User::setFirstName);
		userBinder.forField(lastName)
				.bind(User::getLastName, User::setLastName);
		userBinder.forField(phone)
				.bind(User::getPhoneNumber, User::setPhoneNumber);
		userBinder.forField(email)
				.bind(User::getEmail, User::setEmail);
		userBinder.forField(additionalInfo)
				.bind(User::getAdditionalInfo, User::setAdditionalInfo);


		addressBinder.forField(addressLine1)
				.bind(Address::getAddressLine1, Address::setAddressLine1);
		addressBinder.forField(addressLine2)
				.bind(Address::getAddressLine2, Address::setAddressLine2);
		addressBinder.forField(postcode)
				.bind(Address::getPostcode, Address::setPostcode);
		addressBinder.forField(city)
				.bind(Address::getCity, Address::setCity);
		addressBinder.forField(country)
				.bind(Address::getCountry, Address::setCountry);
	}

	private User user;

	public boolean setUser(User u) {
		if (u == null) {
			System.out.println("User cannot be NULL");
			return false;
		}

		userBinder.removeBean();
		addressBinder.removeBean();

		user = u;

		userBinder.readBean(user);
		addressBinder.readBean(user.getAddress());

		return true;
	}


	public User getUser() {
		try {
			userBinder.validate();

			if (userBinder.isValid()) {
				userBinder.writeBean(user);

				Address address = new Address();
				addressBinder.writeBean(address);

				user.setAddress(address);

				return user;
			}
		} catch (ValidationException e) {
			e.printStackTrace();
			return null;
		}
		return null;
	}


	private void openPasswordChangeDialog() {
		CustomDialog dialog = new CustomDialog();
		dialog.setHeader(UIUtils.createH4Label("Change Password"));
		dialog.setConfirmButton(UIUtils.createButton("Change", ButtonVariant.LUMO_SUCCESS, ButtonVariant.LUMO_TERTIARY));

		PasswordField currentPasswordField = new PasswordField("Input current password");
		currentPasswordField.setRequired(true);
		currentPasswordField.addValueChangeListener(e -> {
			if (e != null) {
				currentPasswordField.setInvalid(false);
			}
		});

		PasswordField newPasswordField1 = new PasswordField("New password");
		newPasswordField1.setRequired(true);
		newPasswordField1.addValueChangeListener(e -> {
			if (e != null) {
				newPasswordField1.setInvalid(false);
			}
		});

		PasswordField newPasswordField2 = new PasswordField("Repeat password");
		newPasswordField2.setRequired(true);
		newPasswordField2.addValueChangeListener(e -> {
			if (e != null) {
				newPasswordField1.setInvalid(false);
			}
		});

		dialog.setContent(
				currentPasswordField,
				newPasswordField1,
				newPasswordField2
		);

		dialog.getConfirmButton().addClickListener(e -> {
			if (currentPasswordField.getValue().length() <= 0) {
				currentPasswordField.setErrorMessage("Input your current password");
				currentPasswordField.setInvalid(true);
				return;
			} else {
				if (!currentPasswordField.getValue().equals(AuthenticationService.getSessionData().getUser().getPassword())) {
					currentPasswordField.setErrorMessage("Invalid password");
					currentPasswordField.setInvalid(true);
					return;
				}
			}

			if (newPasswordField1.getValue().length() <= 0) {
				newPasswordField1.setErrorMessage("Input new password");
				newPasswordField1.setInvalid(true);
				return;
			}

			if (newPasswordField2.getValue().length() <= 0) {
				newPasswordField2.setErrorMessage("Input new password again");
				newPasswordField2.setInvalid(true);
				return;
			}

			if (!newPasswordField1.getValue().equals(newPasswordField2.getValue())) {
				newPasswordField1.setErrorMessage("passwords don't match");
				newPasswordField1.setInvalid(true);

				newPasswordField2.setErrorMessage("passwords don't match");
				newPasswordField2.setInvalid(true);
				return;
			}



//			User currentUser = AuthenticationService.getSessionData().getUser();
//			currentUser.setPassword(newPasswordField2.getValue());
//
//			if (UserFacade.getInstance().update(currentUser)) {
//				AuthenticationService.getSessionData().setUser(currentUser);
//				UIUtils.showNotification("Password updated successfully", UIUtils.NotificationType.SUCCESS);
//				passwordField.setValue(newPasswordField2.getValue());
//				dialog.close();
//			} else {
//				UIUtils.showNotification("Password updated error", UIUtils.NotificationType.ERROR);
//			}

			passwordField.setValue(newPasswordField2.getValue());
			dialog.close();
		});
		dialog.open();
	}
}
