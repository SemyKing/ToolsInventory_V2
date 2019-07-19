package com.gmail.grigorij.ui.utils.forms.admin;

import com.gmail.grigorij.backend.database.facades.CompanyFacade;
import com.gmail.grigorij.backend.entities.company.Company;
import com.gmail.grigorij.backend.access.AccessGroups;
import com.gmail.grigorij.backend.entities.location.Location;
import com.gmail.grigorij.backend.entities.user.User;
import com.gmail.grigorij.ui.utils.components.FlexBoxLayout;
import com.gmail.grigorij.ui.utils.css.FlexDirection;
import com.gmail.grigorij.ui.utils.css.size.Horizontal;
import com.gmail.grigorij.ui.utils.css.size.Left;
import com.gmail.grigorij.ui.utils.css.size.Vertical;
import com.gmail.grigorij.ui.utils.forms.LocationForm;
import com.gmail.grigorij.ui.utils.forms.PersonForm;
import com.gmail.grigorij.ui.views.authentication.AuthenticationService;
import com.gmail.grigorij.ui.utils.components.Divider;
import com.gmail.grigorij.ui.utils.UIUtils;
import com.gmail.grigorij.ui.utils.css.LumoStyles;
import com.gmail.grigorij.utils.ProjectConstants;
import com.gmail.grigorij.utils.converters.CustomConverter;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;

import java.util.EnumSet;
import java.util.List;


/**
 * Form layout with input fields for setting and modifying User
 *
 * {@link #setUser(User user)}
 * Setting null user will clear all field
 * Setting non-null user will populate all field with user parameters
 *
 * {@link #getUser()}
 * Validates all fields and returns null if at least one error, or edited user object if none
 *
 */
public class AdminUserForm extends FormLayout {

	private LocationForm locationForm = new LocationForm();
	private PersonForm<User> personForm = new PersonForm<>();
	private Binder<User> userBinder = new Binder<>(User.class);

	private User user;
	private boolean isNew;
	private TextField username;

	public AdminUserForm() {
		username = new TextField("Username");
		username.setRequired(true);

		Select<String> userStatus = new Select<>(ProjectConstants.ACTIVE, ProjectConstants.INACTIVE);
		userStatus.setWidth("25%");
		userStatus.setLabel("Status");

		FlexBoxLayout userLayout = new FlexBoxLayout();
		userLayout.setFlexDirection(FlexDirection.ROW);
		userLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
		userLayout.add(username, userStatus);
		userLayout.setComponentMargin(userStatus, Left.M);
		userLayout.setFlexGrow("1", username);

		PasswordField password = new PasswordField("Password");
		password.setRequired(true);

		Label contactLabel = UIUtils.createH5Label("Personal Information");

		//TODO: Handle user access by AccessGroups if necessary (if this class is used by normal users)

		List<Company> companies = CompanyFacade.getInstance().getAllCompanies();

		ComboBox<Company> companyComboBox = new ComboBox<>();
		companyComboBox.setItems(companies);
		companyComboBox.setItemLabelGenerator(Company::getName);
		companyComboBox.setLabel("Company");
		companyComboBox.setRequired(true);

		ComboBox<AccessGroups> accessComboBox = new ComboBox<>();
		accessComboBox.setItems(EnumSet.allOf(AccessGroups.class));
		accessComboBox.setItemLabelGenerator(AccessGroups::getStringValue);
		accessComboBox.setLabel("Access Group");
		accessComboBox.setRequired(true);

		Button editAccessButton = UIUtils.createIconButton(VaadinIcon.EDIT, ButtonVariant.LUMO_CONTRAST);
		editAccessButton.addClickListener(e -> {
			if (e != null) {

			}
		});
		UIUtils.setTooltip("Edit this user's selected access", editAccessButton);

		FlexBoxLayout accessLayout = new FlexBoxLayout();
		accessLayout.setWidth("100%");
		accessLayout.setFlexDirection(FlexDirection.ROW);
		accessLayout.add(accessComboBox, editAccessButton);
		accessLayout.setComponentMargin(editAccessButton, Left.S);
		accessLayout.setFlexGrow("1", accessComboBox);
		accessLayout.setAlignItems(FlexComponent.Alignment.BASELINE);

		//Only Admin can change AccessGroup
		if (AuthenticationService.getCurrentSessionUser().getAccessGroup() != AccessGroups.ADMIN.getIntValue()) {
			accessComboBox.setEnabled(false);
		}

		Label addressLabel = UIUtils.createH5Label("Address");

		TextArea additionalInfo = new TextArea("Additional Info");
		additionalInfo.setWidth("100%");
		additionalInfo.setMaxHeight("200px");

		Divider divider1 = new Divider(Horizontal.NONE, Vertical.S);
		Divider divider2 = new Divider(Horizontal.NONE, Vertical.S);
		Divider divider3 = new Divider(Horizontal.NONE, Vertical.S);

		UIUtils.setColSpan(2, userLayout, companyComboBox, divider1, divider2, divider3, locationForm, personForm, additionalInfo);


		// Form layout
		addClassNames(LumoStyles.Padding.Bottom.L, LumoStyles.Padding.Horizontal.M, LumoStyles.Padding.Top.S);
		setResponsiveSteps(
				new FormLayout.ResponsiveStep("0", 1, FormLayout.ResponsiveStep.LabelsPosition.TOP),
				new FormLayout.ResponsiveStep(ProjectConstants.COL_2_MIN_WIDTH, 2, FormLayout.ResponsiveStep.LabelsPosition.TOP));
		add(userLayout);
		add(password);
		add(accessLayout);
		add(companyComboBox);
		add(divider1);
		add(contactLabel);
		add(personForm);
		add(divider2);
		add(addressLabel);
		add(locationForm);
		add(divider3);
		add(additionalInfo);

		userBinder.forField(username)
				.asRequired("Username is required")
				.bind(User::getUsername, User::setUsername);
		userBinder.forField(userStatus)
				.asRequired("Status is required")
				.withConverter(new CustomConverter.StatusConverter())
				.bind(User::isDeleted, User::setDeleted);
		userBinder.forField(password)
				.asRequired("Password is required")
				.bind(User::getPassword, User::setPassword);
		userBinder.forField(companyComboBox)
				.asRequired("Company is required")
				.bind(User::getCompany, User::setCompany);
		userBinder.forField(accessComboBox)
				.asRequired("Access Group is required")
				.withConverter(new CustomConverter.AccessGroupsConverter())
				.bind(User::getAccessGroup, User::setAccessGroup);
		userBinder.forField(additionalInfo)
				.bind(User::getAdditionalInfo, User::setAdditionalInfo);
	}

	public void setUser(User u) {
		userBinder.removeBean();
		isNew = false;

		user = u;
		if (user == null) {
			user = User.getEmptyUser();
			isNew = true;
			username.focus();
		}

		userBinder.readBean(user);
		personForm.setPerson(user);
		locationForm.setLocation(user.getAddress());
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

	public boolean isNew() {
		return isNew;
	}
}
