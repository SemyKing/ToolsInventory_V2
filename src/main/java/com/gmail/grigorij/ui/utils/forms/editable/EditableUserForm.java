package com.gmail.grigorij.ui.utils.forms.editable;

import com.gmail.grigorij.backend.database.facades.AccessRightFacade;
import com.gmail.grigorij.backend.database.facades.CompanyFacade;
import com.gmail.grigorij.backend.database.facades.UserFacade;
import com.gmail.grigorij.backend.entities.company.Company;
import com.gmail.grigorij.backend.enums.permissions.AccessGroup;
import com.gmail.grigorij.backend.embeddable.Location;
import com.gmail.grigorij.backend.embeddable.Person;
import com.gmail.grigorij.backend.entities.user.User;
import com.gmail.grigorij.backend.enums.permissions.PermissionLevel;
import com.gmail.grigorij.backend.enums.permissions.PermissionOperation;
import com.gmail.grigorij.ui.utils.components.CustomDialog;
import com.gmail.grigorij.ui.utils.components.FlexBoxLayout;
import com.gmail.grigorij.ui.utils.css.size.Horizontal;
import com.gmail.grigorij.ui.utils.css.size.Vertical;
import com.gmail.grigorij.ui.utils.components.Divider;
import com.gmail.grigorij.ui.utils.UIUtils;
import com.gmail.grigorij.ui.utils.css.LumoStyles;
import com.gmail.grigorij.utils.AuthenticationService;
import com.gmail.grigorij.utils.ProjectConstants;
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
import com.vaadin.flow.data.converter.StringToBooleanConverter;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;


/**
 * Form layout with input fields for setting and modifying User
 *
 * {@link #setTargetUser(User user)}
 * Setting null user will clear all field
 * Setting non-null user will populate all field with user parameters
 *
 * {@link #getTargetUser()}
 * Validates all fields and returns null if at least one error, or edited user object if none
 *
 */
public class EditableUserForm extends FormLayout {

	private EditableLocationForm locationForm = new EditableLocationForm();
	private EditablePersonForm personForm = new EditablePersonForm();
	private EditableAccessRightsForm accessRightsForm = new EditableAccessRightsForm();
	private Binder<User> binder = new Binder<>(User.class);

	private User targetUser, currentUser;
	private String initialUsername;
	private boolean isNew;


	//Dynamic elements
	private Select<AccessGroup> accessGroupSelect;
	private Button editAccessButton;

	public EditableUserForm() {

		currentUser = AuthenticationService.getCurrentSessionUser();

		Select<String> statusSelector = new Select<>(ProjectConstants.ACTIVE, ProjectConstants.INACTIVE);

		TextField usernameField = new TextField("Username");
		usernameField.setRequired(true);
		usernameField.setPrefixComponent(VaadinIcon.USER.create());

		PasswordField passwordField = new PasswordField("Password");
		passwordField.setRequired(true);
		passwordField.setPrefixComponent(VaadinIcon.PASSWORD.create());


		accessGroupSelect = new Select<>();
		accessGroupSelect.setItems(EnumSet.allOf(AccessGroup.class));
		accessGroupSelect.setItemLabelGenerator(AccessGroup::getStringValue);
		accessGroupSelect.setLabel("Access Group");

		editAccessButton = UIUtils.createIconButton(VaadinIcon.EDIT, ButtonVariant.LUMO_CONTRAST);
		editAccessButton.addClickListener(e -> {
			if (e != null) {
				constructAccessRightsDialog();
			}
		});
		UIUtils.setTooltip("Edit User's Access Rights", editAccessButton);


		//ACCESS COMBO BOX & EDIT ACCESS BUTTON
		FlexBoxLayout accessLayout = UIUtils.getFormRowLayout(accessGroupSelect, editAccessButton, false);


		ComboBox<Company> companyComboBox = new ComboBox<>();
		companyComboBox.setLabel("Company");
		companyComboBox.setItems();
		companyComboBox.setItemLabelGenerator(Company::getName);
		companyComboBox.setRequired(true);
		companyComboBox.setEnabled(false);

		if (currentUser.getAccessGroup().getPermissionLevel().equalsTo(PermissionLevel.SYSTEM)) {
			companyComboBox.setItems(CompanyFacade.getInstance().getAllCompanies());
			companyComboBox.setEnabled(true);
		}

		TextArea additionalInfo = new TextArea("Additional Info");
		additionalInfo.setMaxHeight("200px");


		UIUtils.setColSpan(2, locationForm, personForm, additionalInfo);


		addClassNames(LumoStyles.Padding.Bottom.S, LumoStyles.Padding.Top.S);
		setResponsiveSteps(
				new FormLayout.ResponsiveStep("0", 1, FormLayout.ResponsiveStep.LabelsPosition.TOP),
				new FormLayout.ResponsiveStep(ProjectConstants.COL_2_MIN_WIDTH, 2, FormLayout.ResponsiveStep.LabelsPosition.TOP));

		add(UIUtils.createH4Label("User Info"));
		add(statusSelector);
		add(usernameField);
		add(passwordField);
		add(companyComboBox);
		add(accessLayout);

		add(new Divider(2, Horizontal.NONE, Vertical.S));
		add(UIUtils.createH4Label("Personal Info"));
		add(personForm);

		add(new Divider(2, Horizontal.NONE, Vertical.S));
		add(UIUtils.createH4Label("Address"));
		add(locationForm);

		add(new Divider(2, Horizontal.NONE, Vertical.S));
		add(additionalInfo);


		binder.forField(statusSelector)
				.withConverter(new StringToBooleanConverter("Error", ProjectConstants.INACTIVE, ProjectConstants.ACTIVE))
				.bind(User::isDeleted, User::setDeleted);

		binder.forField(usernameField)
				.asRequired("Username is required")
				.withValidator(un -> {
					if (isNew) {
						return UserFacade.getInstance().isUsernameAvailable(un);
					} else {
						if (!initialUsername.equals(usernameField.getValue())) {
							return UserFacade.getInstance().isUsernameAvailable(un);
						} else {
							return true;
						}
					}
				}, "Username already taken")
				.bind(User::getUsername, User::setUsername);

		binder.forField(passwordField)
				.asRequired("Password is required")
				.withValidator(pw -> pw.length() >= ProjectConstants.PASSWORD_MIN_LENGTH,
						"Password must be at least " + ProjectConstants.PASSWORD_MIN_LENGTH + " characters")
				.bind(User::getPassword, User::setPassword);

		binder.forField(accessGroupSelect)
				.bind(User::getAccessGroup, User::setAccessGroup);

		binder.forField(additionalInfo)
				.bind(User::getAdditionalInfo, User::setAdditionalInfo);

		binder.forField(companyComboBox)
				.bind(User::getCompany, User::setCompany);

	}

	private void handleDynamicFormElements() {
		editAccessButton.setEnabled(true);
		accessGroupSelect.setEnabled(true);
		accessGroupSelect.setItems(EnumSet.allOf(AccessGroup.class));

		if (currentUser.getId().equals(targetUser.getId())) {
			if (!AccessRightFacade.getInstance().isUserAllowedTo(PermissionOperation.VIEW_USER_ACCESS_RIGHTS, PermissionLevel.OWN)) {
				editAccessButton.setEnabled(false);
			}

			if (!AccessRightFacade.getInstance().isUserAllowedTo(PermissionOperation.EDIT_USER_ACCESS_GROUP, PermissionLevel.OWN)) {
				accessGroupSelect.setEnabled(false);
			} else {
				setMaxPermissionLevelList();
			}

		} else {
			if (!AccessRightFacade.getInstance().isUserAllowedTo(PermissionOperation.VIEW_USER_ACCESS_RIGHTS, PermissionLevel.COMPANY, PermissionLevel.SYSTEM)) {
				editAccessButton.setEnabled(false);
			}

			if (!AccessRightFacade.getInstance().isUserAllowedTo(PermissionOperation.EDIT_USER_ACCESS_GROUP, PermissionLevel.COMPANY, PermissionLevel.SYSTEM)) {
				accessGroupSelect.setEnabled(false);
			} else {

				if (targetUser.getAccessGroup().getPermissionLevel().higherThan(currentUser.getAccessGroup().getPermissionLevel())) {
					accessGroupSelect.setEnabled(false);
				} else {
					setMaxPermissionLevelList();
				}
			}
		}
	}

	private void setMaxPermissionLevelList() {
		List<AccessGroup> accessGroups = new ArrayList<>(EnumSet.allOf(AccessGroup.class));
		PermissionLevel usersPermissionLevel = currentUser.getAccessGroup().getPermissionLevel();

		accessGroups.removeIf(accessGroup -> accessGroup.getPermissionLevel().higherThan(usersPermissionLevel));
		accessGroupSelect.setItems(accessGroups);
	}

	private void constructAccessRightsDialog() {
		CustomDialog dialog = new CustomDialog();
		dialog.setHeader(UIUtils.createH3Label("Access Rights"));

		accessRightsForm.setTargetUser(targetUser);
		dialog.setContent(accessRightsForm);

		dialog.closeOnCancel();

		dialog.getConfirmButton().setText("Save");
		dialog.getConfirmButton().addClickListener(e -> {
			targetUser.setAccessRights(accessRightsForm.getAccessRights());
			dialog.close();
		});

		dialog.open();
	}


	public void setTargetUser(User user) {
		binder.removeBean();
		isNew = false;

		targetUser = user;
		if (targetUser == null) {
			targetUser = new User();
			isNew = true;
		}

		initialUsername = targetUser.getUsername();

		handleDynamicFormElements();

		binder.readBean(targetUser);
		personForm.setPerson(targetUser.getPerson());
		locationForm.setLocation(targetUser.getAddress());
	}

	public User getTargetUser() {
		try {
			binder.validate();

			if (binder.isValid()) {
				binder.writeBean(targetUser);

				Location address = locationForm.getLocation();

				if (address == null) {
					return null;
				} else {
					targetUser.setAddress(address);
				}


				Person person = personForm.getPerson();

				if (person == null) {
					return null;
				} else {
					targetUser.setPerson(person);
				}

				return targetUser;
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
