package com.gmail.grigorij.ui.utils.forms;

import com.gmail.grigorij.backend.entities.company.Company;
import com.gmail.grigorij.backend.entities.location.Location;
import com.gmail.grigorij.backend.entities.user.User;
import com.gmail.grigorij.ui.utils.UIUtils;
import com.gmail.grigorij.ui.utils.components.CustomDialog;
import com.gmail.grigorij.ui.utils.components.Divider;
import com.gmail.grigorij.ui.utils.components.FlexBoxLayout;
import com.gmail.grigorij.ui.utils.css.size.Horizontal;
import com.gmail.grigorij.ui.utils.css.size.Vertical;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ReadOnlyHasValue;
import com.vaadin.flow.data.binder.ValidationException;

public class UserForm extends FormLayout {

	private LocationForm locationForm = new LocationForm();
	private PersonForm<User> personForm = new PersonForm<>();

	private Binder<User> userBinder = new Binder<>(User.class);

	private PasswordField passwordField;

	public UserForm() {
		TextField usernameField = new TextField("Username");
		usernameField.setReadOnly(true);
		ReadOnlyHasValue<User> username = new ReadOnlyHasValue<>(user ->
				usernameField.setValue(user.getUsername()));

		passwordField = new PasswordField("Password");
		passwordField.setReadOnly(true);

		Button changePasswordButton = UIUtils.createButton("Change");
		changePasswordButton.addClickListener(e -> openPasswordChangeDialog());

		FlexBoxLayout passwordLayout = UIUtils.getFormRowLayout(passwordField, changePasswordButton);

		TextField companyField = new TextField("Company");
		companyField.setReadOnly(true);
		ReadOnlyHasValue<User> company = new ReadOnlyHasValue<>(user ->
				companyField.setValue(user.getCompany().getName()));

		Label contactLabel = UIUtils.createH5Label("Personal Information");
		Label addressLabel = UIUtils.createH5Label("Address");

		TextArea additionalInfo = new TextArea("Additional Info");
		additionalInfo.setWidth("100%");
		additionalInfo.setMaxHeight("200px");


		// Form layout
//		addClassNames(LumoStyles.Padding.Bottom.L, LumoStyles.Padding.Horizontal.M, LumoStyles.Padding.Top.S);
		setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1, FormLayout.ResponsiveStep.LabelsPosition.TOP));
		add(usernameField);
		add(passwordLayout);
		add(companyField);
		add(new Divider(Horizontal.NONE, Vertical.S));
		add(contactLabel);
		add(personForm);
		add(new Divider(Horizontal.NONE, Vertical.S));
		add(addressLabel);
		add(locationForm);
		add(new Divider(Horizontal.NONE, Vertical.S));
		add(additionalInfo);

		userBinder.forField(username)
				.bind(user -> user, null);
		userBinder.forField(passwordField)
				.asRequired("Password is required")
				.bind(User::getPassword, User::setPassword);
		userBinder.forField(company)
				.bind(user -> user, null);
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

		PasswordField newPasswordField1 = new PasswordField("New password");
		newPasswordField1.setMinWidth("400px");
		newPasswordField1.setRequired(true);
		newPasswordField1.addValueChangeListener(e -> {
			if (e != null) {
				newPasswordField1.setInvalid(false);
			}
		});

		PasswordField newPasswordField2 = new PasswordField("Repeat password");
		newPasswordField2.setMinWidth("400px");
		newPasswordField2.setRequired(true);
		newPasswordField2.addValueChangeListener(e -> {
			if (e != null) {
				newPasswordField1.setInvalid(false);
			}
		});

		dialog.setContent( newPasswordField1, newPasswordField2 );


		dialog.getCancelButton().addClickListener(e -> dialog.close());
		dialog.setConfirmButton(UIUtils.createButton("Change", ButtonVariant.LUMO_SUCCESS, ButtonVariant.LUMO_TERTIARY));
		dialog.getConfirmButton().addClickListener(e -> {

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
