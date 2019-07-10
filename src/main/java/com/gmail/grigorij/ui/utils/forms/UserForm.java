package com.gmail.grigorij.ui.utils.forms;

import com.gmail.grigorij.backend.database.facades.CompanyFacade;
import com.gmail.grigorij.backend.entities.company.Company;
import com.gmail.grigorij.backend.entities.location.Location;
import com.gmail.grigorij.backend.entities.user.User;
import com.gmail.grigorij.ui.utils.UIUtils;
import com.gmail.grigorij.ui.utils.components.CustomDialog;
import com.gmail.grigorij.ui.utils.components.Divider;
import com.gmail.grigorij.ui.utils.components.FlexBoxLayout;
import com.gmail.grigorij.ui.utils.css.FlexDirection;
import com.gmail.grigorij.ui.utils.css.size.Horizontal;
import com.gmail.grigorij.ui.utils.css.size.Left;
import com.gmail.grigorij.ui.utils.css.size.Vertical;
import com.gmail.grigorij.ui.views.authentication.AuthenticationService;
import com.gmail.grigorij.utils.converters.CustomConverter;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;

import java.util.List;

public class UserForm extends FormLayout {

	private LocationForm locationForm = new LocationForm();
	private PersonForm<User> personForm = new PersonForm<>();

	private Binder<User> userBinder = new Binder<>(User.class);

	private PasswordField passwordField;

	public UserForm() {
		TextField username = new TextField("Username");
		username.setReadOnly(true);

		passwordField = new PasswordField("Password");
		passwordField.setReadOnly(true);

		Button changePasswordButton = UIUtils.createButton("Change");
		changePasswordButton.addClickListener(e -> openPasswordChangeDialog());

		FlexBoxLayout passwordLayout = new FlexBoxLayout();
		passwordLayout.setFlexDirection(FlexDirection.ROW);
		passwordLayout.add(passwordField, changePasswordButton);
		passwordLayout.setFlexGrow("1", passwordField);
		passwordLayout.setComponentMargin(changePasswordButton, Left.S);
		passwordLayout.setAlignItems(FlexComponent.Alignment.BASELINE);

		List<Company> companies = CompanyFacade.getInstance().getAllCompanies();

		ComboBox<Company> companyComboBox = new ComboBox<>();
		companyComboBox.setItems(companies);
		companyComboBox.setItemLabelGenerator(Company::getName);
		companyComboBox.setLabel("Company");
		companyComboBox.setReadOnly(true);
		companyComboBox.setRequired(true);

		Label contactLabel = UIUtils.createH5Label("Personal Information");
		Label addressLabel = UIUtils.createH5Label("Address");

		TextArea additionalInfo = new TextArea("Additional Info");
		additionalInfo.setWidth("100%");
		additionalInfo.setMaxHeight("200px");


		// Form layout
//		addClassNames(LumoStyles.Padding.Bottom.L, LumoStyles.Padding.Horizontal.M, LumoStyles.Padding.Top.S);
		setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1, FormLayout.ResponsiveStep.LabelsPosition.TOP));
		add(username);
		add(passwordLayout);
		add(companyComboBox);
		add(new Divider(Horizontal.NONE, Vertical.S));
		add(contactLabel);
		add(personForm);
		add(new Divider(Horizontal.NONE, Vertical.S));
		add(addressLabel);
		add(locationForm);
		add(new Divider(Horizontal.NONE, Vertical.S));
		add(additionalInfo);

		userBinder.forField(username)
				.asRequired("Username is required")
				.bind(User::getUsername, User::setUsername);
		userBinder.forField(passwordField)
				.asRequired("Password is required")
				.bind(User::getPassword, User::setPassword);
		userBinder.forField(companyComboBox)
				.asRequired("Company is required")
//				.withConverter(new CustomConverter.CompanyConverter())
				.bind(User::getCompany, User::setCompany);
		userBinder.forField(additionalInfo)
				.bind(User::getAdditionalInfo, User::setAdditionalInfo);
	}

	private User user;

	public boolean setUser(User u) {
		if (u == null) {
			System.out.println("User cannot be NULL");
			return false;
		}
		userBinder.removeBean();
		user = u;

		userBinder.readBean(user);
		personForm.setPerson(user);
		locationForm.setLocation(user.getAddress());

		return true;
	}


	public User getUser() {
		try {
			userBinder.validate();

			if (userBinder.isValid()) {
				userBinder.writeBean(user);

				Location userAddress = locationForm.getLocation();
				if (userAddress != null) {
					user.setAddress(userAddress);
				}

				Object personInfo = personForm.getPerson();

				if (personInfo != null) {
					if (personInfo instanceof Company) {
						user.setFirstName(((Company) personInfo).getFirstName());
						user.setLastName(((Company) personInfo).getLastName());
						user.setPhoneNumber(((Company) personInfo).getPhoneNumber());
						user.setEmail(((Company) personInfo).getEmail());
					}
				}

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
		dialog.getCancelButton().addClickListener(e -> dialog.close());
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

			passwordField.setValue(newPasswordField2.getValue());
			dialog.close();
		});
		dialog.open();
	}
}
