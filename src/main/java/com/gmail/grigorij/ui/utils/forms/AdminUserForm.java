package com.gmail.grigorij.ui.utils.forms;

import com.gmail.grigorij.backend.database.facades.CompanyFacade;
import com.gmail.grigorij.backend.entities.company.Company;
import com.gmail.grigorij.backend.access.AccessGroups;
import com.gmail.grigorij.backend.access.EntityStatus;
import com.gmail.grigorij.backend.entities.location.Address;
import com.gmail.grigorij.backend.entities.location.Location;
import com.gmail.grigorij.backend.entities.user.User;
import com.gmail.grigorij.ui.views.authentication.AuthenticationService;
import com.gmail.grigorij.ui.utils.components.Divider;
import com.gmail.grigorij.ui.utils.UIUtils;
import com.gmail.grigorij.ui.utils.css.LumoStyles;
import com.gmail.grigorij.utils.converters.CustomConverter;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.renderer.TextRenderer;

import java.util.EnumSet;
import java.util.List;


/**
 * Form layout with input fields for setting and modifying User
 *
 * {@link #setUser(User user)}
 * Setting null user will clear all field
 * Setting nonnull user will populate all field with user parameters
 *
 * {@link #getUser()}
 * Validates all fields and returns null if at least one error, or edited user object if none
 *
 */
public class AdminUserForm extends FormLayout {

	private Binder<User> userBinder = new Binder<>(User.class);
	private Binder<Location> locationBinder = new Binder<>(Location.class);

	private boolean newUser;

	private TextField username;

	public AdminUserForm() {
		username = new TextField();
		username.setRequired(true);
		username.setWidth("100%");

		PasswordField password = new PasswordField();
		password.setRequired(true);
		password.setWidth("100%");

		RadioButtonGroup<EntityStatus> status = new RadioButtonGroup<>();
		status.setItems(EnumSet.allOf(EntityStatus.class));
		status.setRenderer(new TextRenderer<>(EntityStatus::getStringValue));

		Label contactLabel = UIUtils.createH5Label("Contact Person");

		TextField firstName = new TextField();
		firstName.setWidth("100%");

		TextField lastName = new TextField();
		lastName.setWidth("100%");

		TextField phone = new TextField();
		phone.getElement().setAttribute("type", "tel");
		phone.setWidth("100%");

		TextField email = new TextField();
		email.setWidth("100%");


		//TODO: Handle user access by AccessGroups if necessary (if this class is used by normal users)

		List<Company> companies = CompanyFacade.getInstance().getAllCompanies();

		ComboBox<Company> companyComboBox = new ComboBox<>();
		companyComboBox.setItems(companies);
		companyComboBox.setItemLabelGenerator(Company::getCompanyName);
		companyComboBox.setWidth("100%");
		companyComboBox.setRequired(true);

		ComboBox<AccessGroups> accessGroupComboBox = new ComboBox<>();
		accessGroupComboBox.setItems(EnumSet.allOf(AccessGroups.class));
		accessGroupComboBox.setItemLabelGenerator(AccessGroups::getStringValue);
		accessGroupComboBox.setWidth("100%");
		accessGroupComboBox.setRequired(true);

		//Only Admin can change AccessGroup
		if (AuthenticationService.getSessionData().getUser().getAccessGroup() != AccessGroups.ADMIN.getIntValue()) {
			accessGroupComboBox.setEnabled(false);
		}

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
		addClassNames(LumoStyles.Padding.Bottom.L, LumoStyles.Padding.Horizontal.M, LumoStyles.Padding.Top.S);
		setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1, FormLayout.ResponsiveStep.LabelsPosition.TOP));
		addFormItem(username, "Username *");
		addFormItem(password, "Password *");
		addFormItem(companyComboBox, "Company *");
		addFormItem(accessGroupComboBox, "Access Group *");
		addFormItem(status, "Status");
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
		userBinder.forField(password)
				.asRequired("Password is required")
				.bind(User::getPassword, User::setPassword);
		userBinder.forField(companyComboBox)
				.asRequired("Company is required")
				.withConverter(new CustomConverter.CompanyConverter())
				.bind(User::getCompanyId, User::setCompanyId);
		userBinder.forField(accessGroupComboBox)
				.asRequired("Access Group is required")
				.withConverter(new CustomConverter.AccessGroupsConverter())
				.bind(User::getAccessGroup, User::setAccessGroup);
		userBinder.forField(status)
				.asRequired("Select one")
				.withConverter(new CustomConverter.StatusConverter())
				.bind(User::isDeleted, User::setDeleted);
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



		locationBinder.forField(addressLine1)
				.bind(Address::getAddressLine1, Address::setAddressLine1);
		locationBinder.forField(addressLine2)
				.bind(Address::getAddressLine2, Address::setAddressLine2);
		locationBinder.forField(postcode)
				.bind(Address::getPostcode, Address::setPostcode);
		locationBinder.forField(city)
				.bind(Address::getCity, Address::setCity);
		locationBinder.forField(country)
				.bind(Address::getCountry, Address::setCountry);
	}

	private User user;

	public void setUser(User u) {
		userBinder.removeBean();
		locationBinder.removeBean();
		newUser = false;

		user = u;
		if (user == null) {
			user = User.getEmptyUser();
			newUser = true;
			username.focus();
		}

		userBinder.readBean(user);
		locationBinder.readBean(user.getPersonLocation());
	}


	public User getUser() {
		try {
			userBinder.validate();

			if (userBinder.isValid()) {
				userBinder.writeBean(user);

				Location location = new Location();
				locationBinder.writeBean(location);

				user.setPersonLocation(location);

				return user;
			}
		} catch (ValidationException e) {
			e.printStackTrace();
			return null;
		}
		return null;
	}

	public boolean isNewUser() {
		return newUser;
	}
}
