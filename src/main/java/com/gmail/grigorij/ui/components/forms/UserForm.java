package com.gmail.grigorij.ui.components.forms;

import com.gmail.grigorij.backend.database.entities.Company;
import com.gmail.grigorij.backend.database.entities.PermissionHolder;
import com.gmail.grigorij.backend.database.entities.User;
import com.gmail.grigorij.backend.database.entities.embeddable.Location;
import com.gmail.grigorij.backend.database.entities.embeddable.Permission;
import com.gmail.grigorij.backend.database.entities.embeddable.Person;
import com.gmail.grigorij.backend.database.enums.operations.Operation;
import com.gmail.grigorij.backend.database.enums.operations.OperationTarget;
import com.gmail.grigorij.backend.database.enums.permissions.PermissionLevel;
import com.gmail.grigorij.backend.database.enums.permissions.PermissionRange;
import com.gmail.grigorij.backend.database.facades.CompanyFacade;
import com.gmail.grigorij.backend.database.facades.PermissionFacade;
import com.gmail.grigorij.backend.database.facades.UserFacade;
import com.gmail.grigorij.ui.components.FlexBoxLayout;
import com.gmail.grigorij.ui.components.dialogs.ConfirmDialog;
import com.gmail.grigorij.ui.components.dialogs.CustomDialog;
import com.gmail.grigorij.ui.components.dialogs.password.ChangePasswordView;
import com.gmail.grigorij.ui.components.dialogs.permissions.PermissionsView;
import com.gmail.grigorij.ui.utils.UIUtils;
import com.gmail.grigorij.ui.utils.css.size.Left;
import com.gmail.grigorij.utils.authentication.AuthenticationService;
import com.gmail.grigorij.utils.authentication.PasswordUtils;
import com.gmail.grigorij.utils.ProjectConstants;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;

import java.util.ArrayList;
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
public class UserForm extends FormLayout {

	private final String CLASS_NAME = "form";

	private final PersonForm personForm = new PersonForm();
	private final LocationForm addressForm = new LocationForm();

	private PermissionsView permissionsView;
	private PermissionHolder permissionHolder;

	private Binder<User> binder;

	private User user, originalUser;
	private String initialUsername;
	private boolean isNew;

	private boolean dataLoaded = false;
	private boolean self = false;
	private boolean passwordChanged = false;


	// FORM ITEMS
	private Div entityStatusDiv;
	private Checkbox entityStatusCheckbox;
	private TextField usernameField;
	private FlexBoxLayout passwordLayout;
	private PasswordField passwordField;
	private Button changePasswordButton;
	private Button editPermissionsButton;
	private ComboBox<PermissionLevel> permissionLevelComboBox;
	private FlexBoxLayout permissionsLayout;
	private ComboBox<Company> companyComboBox;
	private TextArea additionalInfo;


	public UserForm() {
		addClassName(CLASS_NAME);

		constructFormItems();

		constructForm();

		constructBinder();
	}


	private void constructFormItems() {
		entityStatusCheckbox = new Checkbox("Deleted");

		entityStatusDiv = new Div();
		entityStatusDiv.addClassName(ProjectConstants.CONTAINER_ALIGN_CENTER);

		setColspan(entityStatusDiv, 2);

		usernameField = new TextField("Username");
		usernameField.setRequired(true);
		usernameField.setPrefixComponent(VaadinIcon.USER.create());


		passwordField = new PasswordField("Password");
		passwordField.setRequired(true);
		passwordField.setPrefixComponent(VaadinIcon.PASSWORD.create());

		changePasswordButton = UIUtils.createButton("Change", ButtonVariant.LUMO_PRIMARY);
		changePasswordButton.addClickListener(e -> constructChangePasswordDialog());

		passwordLayout = new FlexBoxLayout();
		passwordLayout.addClassName(ProjectConstants.CONTAINER_ALIGN_CENTER);
		passwordLayout.add(passwordField);
		passwordLayout.setFlexGrow("1", passwordField);


		companyComboBox = new ComboBox<>();
		companyComboBox.setLabel("Company");
		companyComboBox.setItems(CompanyFacade.getInstance().getAllActiveCompanies());
		companyComboBox.setItemLabelGenerator(Company::getName);
		companyComboBox.setRequired(true);


		List<PermissionLevel> levels = new ArrayList<>(EnumSet.allOf(PermissionLevel.class));
		levels.removeIf(level -> level.higherThan(AuthenticationService.getCurrentSessionUser().getPermissionLevel()));

		if (AuthenticationService.getCurrentSessionUser().getPermissionLevel().equalsTo(PermissionLevel.COMPANY_ADMIN)) {
			levels.removeIf(level -> level.equalsTo(PermissionLevel.COMPANY_ADMIN));
		}

		permissionLevelComboBox = new ComboBox<>();
		permissionLevelComboBox.setItems(levels);
		permissionLevelComboBox.setItemLabelGenerator(PermissionLevel::getName);
		permissionLevelComboBox.setLabel("Permission Level");
		permissionLevelComboBox.addValueChangeListener(e -> {
			if (dataLoaded) {
				if (!e.getValue().equalsTo(e.getOldValue())) {
					if (PermissionFacade.getInstance().isSystemAdminOrAllowedTo(Operation.CHANGE, OperationTarget.PERMISSION_LEVEL, PermissionRange.COMPANY)) {
						handlePermissionLevelChange(e.getValue());
					}
				}
			}
		});


		//PERMISSIONS & BUTTON
		permissionsLayout = new FlexBoxLayout();
		permissionsLayout.addClassName(ProjectConstants.CONTAINER_SPACE_BETWEEN);
		permissionsLayout.add(permissionLevelComboBox);
		permissionsLayout.setFlexGrow("1", permissionLevelComboBox);

		editPermissionsButton = UIUtils.createButton("Permissions", VaadinIcon.EDIT, ButtonVariant.LUMO_PRIMARY);
		editPermissionsButton.addClickListener(e -> constructPermissionsDialog());

		additionalInfo = new TextArea("Additional Info");
		additionalInfo.setMaxHeight("200px");

		setColspan(additionalInfo, 2);

		setColspan(personForm, 2);
		setColspan(addressForm, 2);
	}

	private void constructForm() {
		setResponsiveSteps(
				new FormLayout.ResponsiveStep("0", 1, FormLayout.ResponsiveStep.LabelsPosition.TOP),
				new FormLayout.ResponsiveStep(ProjectConstants.COL_2_MIN_WIDTH, 2, FormLayout.ResponsiveStep.LabelsPosition.TOP));

		add(entityStatusDiv);
		add(usernameField);
		add(passwordLayout);
		if (AuthenticationService.getCurrentSessionUser().getPermissionLevel().equalsTo(PermissionLevel.SYSTEM_ADMIN)) {
			add(companyComboBox);
		}
		add(permissionsLayout);

		Hr hr = new Hr();
		setColspan(hr, 2);
		add(hr);

		Label personalInfo = UIUtils.createH4Label("Personal Information");
		setColspan(personalInfo, 2);
		add(personalInfo);

		add(personForm);

		hr = new Hr();
		setColspan(hr, 2);
		add(hr);

		Label addressInfo = UIUtils.createH4Label("Address");
		setColspan(addressInfo, 2);
		add(addressInfo);

		add(addressForm);

		hr = new Hr();
		setColspan(hr, 2);
		add(hr);

		add(additionalInfo);
	}

	private void constructBinder() {
		binder = new Binder<>(User.class);

		binder.forField(entityStatusCheckbox)
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
				.bind(User::getDummyPassword, User::setDummyPassword);

		binder.forField(companyComboBox)
				.bind(User::getCompany, User::setCompany);

		binder.forField(permissionLevelComboBox)
				.bind(User::getPermissionLevel, User::setPermissionLevel);

		binder.forField(additionalInfo)
				.bind(User::getAdditionalInfo, User::setAdditionalInfo);
	}


	private void initDynamicFormItems() {
		self = AuthenticationService.getCurrentSessionUser().getId().equals(user.getId());
		initialUsername = user.getUsername();
		passwordChanged = false;

		usernameField.setReadOnly(!isNew);
		passwordField.setReadOnly(!isNew);
		passwordField.setRevealButtonVisible(isNew);

		permissionHolder = null;

		try {
			entityStatusDiv.remove(entityStatusCheckbox);
			passwordLayout.remove(changePasswordButton);
			permissionsLayout.remove(editPermissionsButton);
		} catch (Exception ignored) {}


		if (self || PermissionFacade.getInstance().isSystemAdminOrAllowedTo(Operation.CHANGE, OperationTarget.PASSWORD, PermissionRange.COMPANY)) {
			passwordLayout.add(changePasswordButton);
			passwordLayout.setComponentMargin(changePasswordButton, Left.S);
		}

		if (!self && PermissionFacade.getInstance().isSystemAdminOrAllowedTo(Operation.DELETE, OperationTarget.USER, PermissionRange.COMPANY)) {
			entityStatusDiv.add(entityStatusCheckbox);
		}


		permissionLevelComboBox.setEnabled(false);
		permissionLevelComboBox.setReadOnly(true);


		if (user.getPermissionLevel().lowerOrEqualsTo(AuthenticationService.getCurrentSessionUser().getPermissionLevel())) {
			if (!(self && AuthenticationService.getCurrentSessionUser().getPermissionLevel().equalsTo(PermissionLevel.SYSTEM_ADMIN))) {
				if (self && PermissionFacade.getInstance().isSystemAdminOrAllowedTo(Operation.EDIT, OperationTarget.PERMISSION_LEVEL, PermissionRange.OWN) ||
						!self && PermissionFacade.getInstance().isSystemAdminOrAllowedTo(Operation.EDIT, OperationTarget.PERMISSION_LEVEL, PermissionRange.COMPANY)) {

					permissionLevelComboBox.setEnabled(true);
					permissionLevelComboBox.setReadOnly(false);
				}
			}

			if (self && PermissionFacade.getInstance().isSystemAdminOrAllowedTo(Operation.EDIT, OperationTarget.PERMISSIONS, PermissionRange.OWN) ||
					!self && PermissionFacade.getInstance().isSystemAdminOrAllowedTo(Operation.EDIT, OperationTarget.PERMISSIONS, PermissionRange.COMPANY)) {

				permissionsLayout.add(editPermissionsButton);
				permissionsLayout.setComponentMargin(editPermissionsButton, Left.S);
			}
		}
	}

	private void handlePermissionLevelChange(PermissionLevel level) {
		ConfirmDialog confirmDialog = new ConfirmDialog();
		confirmDialog.setMessage("Would you like to set default permissions for: " + level.getName());
		confirmDialog.closeOnCancel();
		confirmDialog.getConfirmButton().addClickListener(confirmOnClick -> {
			confirmDialog.close();

			switch (level) {
				case VIEWER:
					user.getPermissionHolder().setPermissions(PermissionFacade.getInstance().getDefaultViewerPermissions());
					break;
				case USER:
					user.getPermissionHolder().setPermissions(PermissionFacade.getInstance().getDefaultUserPermissions());
					break;
				case FOREMAN:
					user.getPermissionHolder().setPermissions(PermissionFacade.getInstance().getDefaultForemanPermissions());
					break;
				case COMPANY_ADMIN:
					user.getPermissionHolder().setPermissions(PermissionFacade.getInstance().getDefaultCompanyAdminPermissions());
					break;
				case SYSTEM_ADMIN:
					user.getPermissionHolder().setPermissions(null);
					break;
			}
		});
		confirmDialog.open();
	}

	private void constructChangePasswordDialog() {
		CustomDialog dialog = new CustomDialog();
		dialog.setHeader(UIUtils.createH3Label("Change Password"));

		ChangePasswordView view = new ChangePasswordView(user);

		if (self || AuthenticationService.getCurrentSessionUser().getPermissionLevel().lowerThan(PermissionLevel.COMPANY_ADMIN)) {
			view.addCurrentPasswordView();
		}

		dialog.setContent(view);

		dialog.closeOnCancel();

		dialog.getConfirmButton().setText("Change");
		dialog.getConfirmButton().addClickListener(e -> {
			if (view.isValid()) {

				String salt = PasswordUtils.getSalt(30);
				user.setSalt(salt);
				user.setPassword(PasswordUtils.generateSecurePassword(view.getNewPassword(), salt));
				passwordField.setValue(PasswordUtils.generateDummyPassword(view.getNewPassword()));

				passwordChanged = true;

				dialog.close();
			}
		});

		dialog.open();
	}

	private void constructPermissionsDialog() {
		if (self && AuthenticationService.getCurrentSessionUser().getPermissionLevel().equalsTo(PermissionLevel.SYSTEM_ADMIN)) {
			UIUtils.showNotification("As System Administrator, you have all permissions", NotificationVariant.LUMO_PRIMARY);
			return;
		}

		permissionsView = new PermissionsView(user);

		CustomDialog dialog = new CustomDialog();

		dialog.setCloseOnOutsideClick(false);
		dialog.setCloseOnEsc(false);

		dialog.setHeader(UIUtils.createH3Label("User Permissions"));
		dialog.setContent(permissionsView);

		dialog.getConfirmButton().setEnabled(false);

		if (self && PermissionFacade.getInstance().isSystemAdminOrAllowedTo(Operation.EDIT, OperationTarget.PERMISSIONS, PermissionRange.OWN) ||
				!self && PermissionFacade.getInstance().isSystemAdminOrAllowedTo(Operation.EDIT, OperationTarget.PERMISSIONS, PermissionRange.COMPANY)) {
			dialog.getConfirmButton().setEnabled(true);
		}

		dialog.getConfirmButton().setText("Save");
		dialog.getConfirmButton().addClickListener(saveOnClick -> {
			PermissionHolder permissionHolder = permissionsView.getPermissionHolder();
			if (permissionHolder != null) {
				this.permissionHolder = permissionHolder;
				dialog.close();
			}
		});

		dialog.getCancelButton().addClickListener(cancelEditOnClick -> {
			ConfirmDialog confirmDialog = new ConfirmDialog();
			confirmDialog.setMessage("Are you sure you want to cancel?" + ProjectConstants.NEW_LINE + "All changes will be lost");
			confirmDialog.closeOnCancel();
			confirmDialog.getConfirmButton().addClickListener(confirmOnClick -> {
				permissionsView.setChanges(null);
				confirmDialog.close();
				dialog.close();
			});
			confirmDialog.open();
		});

		dialog.open();
	}


	public void setUser(User user) {
		isNew = false;
		dataLoaded = false;

		if (user == null) {
			this.user = new User();
			isNew = true;
		} else {
			this.user = user;
		}

		initDynamicFormItems();

		originalUser = new User(this.user);

		addressForm.setLocation(this.user.getAddress());
		personForm.setPerson(this.user.getPerson());

		binder.readBean(this.user);
		dataLoaded = true;
	}

	public User getUser() {
		try {
			binder.validate();

			if (binder.isValid()) {

				Location address = addressForm.getLocation();
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

				if (isNew) {
					if (!passwordChanged) {
						String salt = PasswordUtils.getSalt(30);
						user.setSalt(salt);
						user.setPassword(PasswordUtils.generateSecurePassword(passwordField.getValue(), salt));
						passwordField.setValue(PasswordUtils.generateDummyPassword(passwordField.getValue()));
					}
				}

				if (permissionHolder != null) {
					user.setPermissionHolder(permissionHolder);
				}

				binder.writeBean(user);
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

	public List<String> getChanges() {
		List<String> changes = new ArrayList<>();

		if (Boolean.compare(originalUser.isDeleted(), user.isDeleted()) != 0) {
			changes.add("User status changed from: '" + UIUtils.entityStatusToString(originalUser.isDeleted()) + "', to: '" + UIUtils.entityStatusToString(user.isDeleted()) + "'");
		}
		if (!originalUser.getUsername().equals(user.getUsername())) {
			changes.add("Username changed from: '" + originalUser.getUsername() + "', to: '" + user.getUsername() + "'");
		}
		if (!originalUser.getPassword().equals(user.getPassword())) {
			changes.add("Password changed");
		}
		if (!originalUser.getPermissionLevel().equals(user.getPermissionLevel())) {
			changes.add("Permission level changed from: '" + originalUser.getPermissionLevel().getName() + "', to: '" + user.getPermissionLevel().getName() + "'");
		}
		if (!originalUser.getCompany().equals(user.getCompany())) {
			changes.add("User company changed from: '" + originalUser.getCompany().getName() + "', to: '" + user.getCompany().getName() + "'");
		}
		if (!originalUser.getAdditionalInfo().equals(user.getAdditionalInfo())) {
			changes.add("Additional Info changed from: '" + originalUser.getAdditionalInfo() + "', to: '" + user.getAdditionalInfo() + "'");
		}

		List<String> otherChanges = addressForm.getChanges();
		if (otherChanges.size() > 0) {
			changes.add("-- Address changed");
			changes.addAll(otherChanges);
		}

		otherChanges = personForm.getChanges();
		if (otherChanges.size() > 0) {
			changes.add("-- Personal information changed");
			changes.addAll(otherChanges);
		}

		if (permissionHolder != null) {
			otherChanges = permissionsView.getChanges();
			if (otherChanges != null) {
				if (otherChanges.size() > 0) {
					changes.add("-- Permissions changed");
					changes.addAll(otherChanges);
				}
			}
		}

		return changes;
	}
}
