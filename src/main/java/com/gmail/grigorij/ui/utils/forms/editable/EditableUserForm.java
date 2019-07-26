package com.gmail.grigorij.ui.utils.forms.editable;

import com.gmail.grigorij.backend.database.facades.CompanyFacade;
import com.gmail.grigorij.backend.entities.company.Company;
import com.gmail.grigorij.backend.access.AccessGroups;
import com.gmail.grigorij.backend.entities.embeddable.Location;
import com.gmail.grigorij.backend.entities.embeddable.Person;
import com.gmail.grigorij.backend.entities.user.User;
import com.gmail.grigorij.ui.utils.components.FlexBoxLayout;
import com.gmail.grigorij.ui.utils.css.size.Horizontal;
import com.gmail.grigorij.ui.utils.css.size.Vertical;
import com.gmail.grigorij.ui.utils.components.Divider;
import com.gmail.grigorij.ui.utils.UIUtils;
import com.gmail.grigorij.ui.utils.css.LumoStyles;
import com.gmail.grigorij.ui.views.authentication.AuthenticationService;
import com.gmail.grigorij.utils.ProjectConstants;
import com.gmail.grigorij.utils.converters.CustomConverter;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;

import java.util.EnumSet;


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
public class EditableUserForm extends FormLayout {

	private EditableLocationForm locationForm = new EditableLocationForm();
	private EditablePersonForm personForm = new EditablePersonForm();
	private Binder<User> binder = new Binder<>(User.class);

	private User user;
	private boolean isNew;

	public EditableUserForm() {

		User currentUser = AuthenticationService.getCurrentSessionUser();


		TextField usernameField = new TextField("Username");
		usernameField.setRequired(true);

		Select<String> status = null;
		if (currentUser.getAccessGroup() == AccessGroups.ADMIN.getIntValue()) {
			status = new Select<>(ProjectConstants.ACTIVE, ProjectConstants.INACTIVE);
			status.setWidth("25%");
			status.setLabel("Status");
		}

		//USERNAME & STATUS
		FlexBoxLayout usernameStatusLayout = UIUtils.getFormRowLayout(usernameField, status, false);


		PasswordField passwordField = new PasswordField("Password");
		passwordField.setRequired(true);


		ComboBox<AccessGroups> accessComboBox = new ComboBox<>();
		accessComboBox.setItems(EnumSet.allOf(AccessGroups.class));
		accessComboBox.setItemLabelGenerator(AccessGroups::getStringValue);
		accessComboBox.setLabel("Access Group");
		accessComboBox.setRequired(true);

		if (currentUser.getAccessGroup() != AccessGroups.ADMIN.getIntValue()) {
			accessComboBox.setEnabled(false);
		}

		Button editAccessButton = null;
		if (currentUser.getAccessGroup() == AccessGroups.ADMIN.getIntValue()) {
			editAccessButton = UIUtils.createIconButton(VaadinIcon.EDIT, ButtonVariant.LUMO_CONTRAST);
//			editAccessButton.addClickListener(e -> {
//				if (e != null) {
//				}
//			});
			UIUtils.setTooltip("Edit this user's selected access", editAccessButton);
		}

		//ACCESS COMBO BOX & EDIT ACCESS BUTTON
		FlexBoxLayout accessLayout = UIUtils.getFormRowLayout(accessComboBox, editAccessButton, false);


		ComboBox<Company> companyComboBox = new ComboBox<>();
		companyComboBox.setItems(CompanyFacade.getInstance().getAllCompanies());
		companyComboBox.setItemLabelGenerator(Company::getName);
		companyComboBox.setLabel("Company");
		companyComboBox.setRequired(true);

		if (currentUser.getAccessGroup() != AccessGroups.ADMIN.getIntValue()) {
			companyComboBox.setEnabled(false);
		}


		TextArea additionalInfo = new TextArea("Additional Info");
		additionalInfo.setMaxHeight("200px");


		if (status != null) {
			UIUtils.setColSpan(2, usernameStatusLayout, passwordField);
		}

		UIUtils.setColSpan(2, locationForm, personForm, additionalInfo);


		addClassNames(LumoStyles.Padding.Bottom.S, LumoStyles.Padding.Top.S);
		setResponsiveSteps(
				new FormLayout.ResponsiveStep("0", 1, FormLayout.ResponsiveStep.LabelsPosition.TOP),
				new FormLayout.ResponsiveStep(ProjectConstants.COL_2_MIN_WIDTH, 2, FormLayout.ResponsiveStep.LabelsPosition.TOP));
		add(usernameStatusLayout);
		add(passwordField);
		add(accessLayout);
		add(companyComboBox);

		add(new Divider(2, Horizontal.NONE, Vertical.S));
		add(UIUtils.createH4Label("Personal Info"));
		add(personForm);

		add(new Divider(2, Horizontal.NONE, Vertical.S));
		add(UIUtils.createH4Label("Address"));
		add(locationForm);

		add(new Divider(2, Horizontal.NONE, Vertical.S));
		add(additionalInfo);

		binder.forField(usernameField)
				.asRequired("Username is required")
				.bind(User::getUsername, User::setUsername);

		if (status != null) {
			binder.forField(status)
					.asRequired("Status is required")
					.withConverter(new CustomConverter.StatusConverter())
					.bind(User::isDeleted, User::setDeleted);
		}

		binder.forField(passwordField)
				.asRequired("Password is required")
				.bind(User::getPassword, User::setPassword);
		binder.forField(companyComboBox)
				.asRequired("Company is required")
				.bind(User::getCompany, User::setCompany);
		binder.forField(accessComboBox)
				.asRequired("Access Group is required")
				.withConverter(new CustomConverter.AccessGroupsConverter())
				.bind(User::getAccessGroup, User::setAccessGroup);
		binder.forField(additionalInfo)
				.bind(User::getAdditionalInfo, User::setAdditionalInfo);
	}

	public void setUser(User u) {
		binder.removeBean();
		isNew = false;

		user = u;
		if (user == null) {
			user = User.getEmptyUser();
			isNew = true;
		}

		binder.readBean(user);
		personForm.setPerson(user.getPerson());
		locationForm.setLocation(user.getAddress());
	}

	public User getUser() {
		try {
			binder.validate();

			if (binder.isValid()) {
				binder.writeBean(user);

				Location address = locationForm.getLocation();

				if (address == null) {
					return null;
				} else {
					user.setAddress(address);
				}


				Person person = personForm.getPerson();

				if (person == null) {
					return null;
				} else {
					user.setPerson(person);
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
