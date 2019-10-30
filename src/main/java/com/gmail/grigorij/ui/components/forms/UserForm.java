package com.gmail.grigorij.ui.components.forms;

import com.gmail.grigorij.backend.database.entities.Transaction;
import com.gmail.grigorij.backend.database.facades.CompanyFacade;
import com.gmail.grigorij.backend.database.facades.PermissionFacade;
import com.gmail.grigorij.backend.database.facades.TransactionFacade;
import com.gmail.grigorij.backend.database.facades.UserFacade;
import com.gmail.grigorij.backend.database.entities.embeddable.Location;
import com.gmail.grigorij.backend.database.entities.embeddable.Person;
import com.gmail.grigorij.backend.database.entities.Company;
import com.gmail.grigorij.backend.database.entities.embeddable.Permission;
import com.gmail.grigorij.backend.database.entities.User;
import com.gmail.grigorij.backend.database.enums.operations.Operation;
import com.gmail.grigorij.backend.database.enums.operations.OperationTarget;
import com.gmail.grigorij.backend.database.enums.permissions.PermissionRange;
import com.gmail.grigorij.backend.database.enums.permissions.PermissionLevel;
import com.gmail.grigorij.ui.components.dialogs.ConfirmDialog;
import com.gmail.grigorij.ui.components.dialogs.PermissionsDialog;
import com.gmail.grigorij.ui.components.layouts.FlexBoxLayout;
import com.gmail.grigorij.ui.utils.UIUtils;
import com.gmail.grigorij.ui.utils.css.size.Right;
import com.gmail.grigorij.utils.AuthenticationService;
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
	private Binder<User> binder;

	private User user, originalUser;
	private List<Permission> tempPermissions;
	private String initialUsername;
	private boolean isNew;

	private boolean dataLoaded = false;

	// FORM ITEMS
	private Div entityStatusDiv;
	private Checkbox entityStatusCheckbox;
	private TextField usernameField;
	private PasswordField passwordField;
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
		entityStatusDiv.add(entityStatusCheckbox);

		setColspan(entityStatusDiv, 2);

		if (AuthenticationService.getCurrentSessionUser().getPermissionLevel().lowerThan(PermissionLevel.SYSTEM_ADMIN)) {
			if (!PermissionFacade.getInstance().isUserAllowedTo(Operation.DELETE, OperationTarget.USER, PermissionRange.COMPANY)) {
				entityStatusCheckbox.setReadOnly(true);
//				entityStatusDiv.getElement().setAttribute(ProjectConstants.INVISIBLE_ATTR, true);
				entityStatusDiv.getElement().getStyle().set("display", "none");
			}
		}

		usernameField = new TextField("Username");
		usernameField.setRequired(true);
		usernameField.setPrefixComponent(VaadinIcon.USER.create());

		passwordField = new PasswordField("Password");
		passwordField.setRequired(true);
		passwordField.setPrefixComponent(VaadinIcon.PASSWORD.create());


		companyComboBox = new ComboBox<>();
		companyComboBox.setLabel("Company");
		companyComboBox.setItems(CompanyFacade.getInstance().getAllCompanies());
		companyComboBox.setItemLabelGenerator(Company::getName);
		companyComboBox.setRequired(true);


		permissionLevelComboBox = new ComboBox<>();
		permissionLevelComboBox.setItems();
		permissionLevelComboBox.setItemLabelGenerator(PermissionLevel::getName);
		permissionLevelComboBox.setLabel("Permission Level");
		permissionLevelComboBox.addValueChangeListener(e -> {
			if (dataLoaded) {
				if (!e.getValue().equalsTo(e.getOldValue())) {
					if (e.getValue().equalsTo(PermissionLevel.VIEWER)) {
						ConfirmDialog confirmDialog = new ConfirmDialog();
						confirmDialog.setMessage("Would you like to set default permissions for: " + PermissionLevel.VIEWER.getName());
						confirmDialog.closeOnCancel();
						confirmDialog.getConfirmButton().addClickListener(confirmOnClick -> {
							confirmDialog.close();
							tempPermissions = PermissionFacade.getInstance().getDefaultViewerPermissions();
							user.setPermissions(tempPermissions);
						});
						confirmDialog.open();
					}

					if (e.getValue().equalsTo(PermissionLevel.USER)) {
						ConfirmDialog confirmDialog = new ConfirmDialog();
						confirmDialog.setMessage("Would you like to set default permissions for: " + PermissionLevel.USER.getName());
						confirmDialog.closeOnCancel();
						confirmDialog.getConfirmButton().addClickListener(confirmOnClick -> {
							confirmDialog.close();
							tempPermissions = PermissionFacade.getInstance().getDefaultUserPermissions();
							user.setPermissions(tempPermissions);
						});
						confirmDialog.open();
					}

					if (e.getValue().equalsTo(PermissionLevel.FOREMAN)) {
						ConfirmDialog confirmDialog = new ConfirmDialog();
						confirmDialog.setMessage("Would you like to set default permissions for: " + PermissionLevel.FOREMAN.getName());
						confirmDialog.closeOnCancel();
						confirmDialog.getConfirmButton().addClickListener(confirmOnClick -> {
							confirmDialog.close();
							tempPermissions = PermissionFacade.getInstance().getDefaultForemanPermissions();
							user.setPermissions(tempPermissions);
						});
						confirmDialog.open();
					}

					if (e.getValue().equalsTo(PermissionLevel.COMPANY_ADMIN)) {
						ConfirmDialog confirmDialog = new ConfirmDialog();
						confirmDialog.setMessage("Would you like to set default permissions for: " + PermissionLevel.COMPANY_ADMIN.getName());
						confirmDialog.closeOnCancel();
						confirmDialog.getConfirmButton().addClickListener(confirmOnClick -> {
							confirmDialog.close();
							tempPermissions = PermissionFacade.getInstance().getDefaultCompanyAdminPermissions();
							user.setPermissions(tempPermissions);
						});
						confirmDialog.open();
					}

					if (e.getValue().equalsTo(PermissionLevel.SYSTEM_ADMIN)) {
						ConfirmDialog confirmDialog = new ConfirmDialog();
						confirmDialog.setMessage("Would you like to set default permissions for: " + PermissionLevel.SYSTEM_ADMIN.getName());
						confirmDialog.closeOnCancel();
						confirmDialog.getConfirmButton().addClickListener(confirmOnClick -> {
							confirmDialog.close();
							tempPermissions = new ArrayList<>();
							user.setPermissions(tempPermissions);

						});
						confirmDialog.open();
					}
				}
			}
		});


		editPermissionsButton = UIUtils.createButton(VaadinIcon.EDIT, ButtonVariant.LUMO_CONTRAST);
		editPermissionsButton.addClickListener(e -> {
			constructAccessRightsDialog();
		});
		UIUtils.setTooltip("Edit User's Permissions", editPermissionsButton);

		//PERMISSIONS & BUTTON
		permissionsLayout = new FlexBoxLayout();
		permissionsLayout.addClassName(ProjectConstants.CONTAINER_SPACE_BETWEEN);
		permissionsLayout.add(permissionLevelComboBox, editPermissionsButton);
		permissionsLayout.setFlexGrow("1", permissionLevelComboBox);
		permissionsLayout.setComponentMargin(permissionLevelComboBox, Right.S);


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
		add(passwordField);
		add(companyComboBox);
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
				.bind(User::getPassword, User::setPassword);

		binder.forField(companyComboBox)
				.bind(User::getCompany, User::setCompany);

		binder.forField(permissionLevelComboBox)
				.bind(User::getPermissionLevel, User::setPermissionLevel);

		binder.forField(additionalInfo)
				.bind(User::getAdditionalInfo, User::setAdditionalInfo);
	}


	private void initDynamicFormItems() {
		initialUsername = user.getUsername();
		tempPermissions = new ArrayList<>();

		for (Permission permission : user.getPermissions()) {
			tempPermissions.add(new Permission(permission));
		}

		List<PermissionLevel> levels = new ArrayList<>(EnumSet.allOf(PermissionLevel.class));
		levels.removeIf(level -> level.higherThan(AuthenticationService.getCurrentSessionUser().getPermissionLevel()));

		if (AuthenticationService.getCurrentSessionUser().getPermissionLevel().equalsTo(PermissionLevel.COMPANY_ADMIN)) {
			levels.removeIf(level -> level.equalsTo(PermissionLevel.COMPANY_ADMIN));
		}

		permissionLevelComboBox.setItems(levels);

		companyComboBox.setReadOnly(true);
		permissionLevelComboBox.setReadOnly(true);
		editPermissionsButton.setEnabled(false);


		if (AuthenticationService.getCurrentSessionUser().getPermissionLevel().equalsTo(PermissionLevel.SYSTEM_ADMIN)) {
			companyComboBox.setReadOnly(false);
			permissionLevelComboBox.setReadOnly(false);
			editPermissionsButton.setEnabled(true);
		}

		if (AuthenticationService.getCurrentSessionUser().getPermissionLevel().equalsTo(PermissionLevel.COMPANY_ADMIN)) {

			// USER EDITING ITSELF
			if (AuthenticationService.getCurrentSessionUser().getId().equals(user.getId())) {
				if (PermissionFacade.getInstance().isUserAllowedTo(Operation.EDIT, OperationTarget.PERMISSION_LEVEL, PermissionRange.OWN)) {
					permissionLevelComboBox.setReadOnly(false);
				}
				if (PermissionFacade.getInstance().isUserAllowedTo(Operation.VIEW, OperationTarget.PERMISSIONS, PermissionRange.OWN)) {
					editPermissionsButton.setEnabled(true);
				}

			// USER EDITING OTHER USER
			} else {
				if (PermissionFacade.getInstance().isUserAllowedTo(Operation.EDIT, OperationTarget.PERMISSION_LEVEL, PermissionRange.COMPANY)) {
					permissionLevelComboBox.setReadOnly(false);
				}
				if (PermissionFacade.getInstance().isUserAllowedTo(Operation.VIEW, OperationTarget.PERMISSIONS, PermissionRange.COMPANY)) {
					editPermissionsButton.setEnabled(true);
				}
			}
		}
	}

	private void constructAccessRightsDialog() {

		if (AuthenticationService.getCurrentSessionUser().getId().equals(user.getId())) {
			if (AuthenticationService.getCurrentSessionUser().getPermissionLevel().equalsTo(PermissionLevel.SYSTEM_ADMIN)) {
				UIUtils.showNotification("As System Administrator, you have all permissions", UIUtils.NotificationType.INFO);
				return;
			}
		}

		PermissionsDialog dialog = new PermissionsDialog(user);
		dialog.getHeader().add(UIUtils.createH3Label("Permissions Details"));
		dialog.constructView();


		dialog.getConfirmButton().addClickListener(saveOnClick -> {
			List<Permission> permissions = dialog.getPermissions();
			if (permissions != null) {
				tempPermissions = permissions;
				dialog.close();

				UIUtils.showNotification("Permissions Edited", UIUtils.NotificationType.SUCCESS, 1000);

				Transaction transaction = new Transaction();
				transaction.setUser(AuthenticationService.getCurrentSessionUser());
				transaction.setCompany(AuthenticationService.getCurrentSessionUser().getCompany());
				transaction.setOperation(Operation.EDIT);
				transaction.setOperationTarget1(OperationTarget.PERMISSIONS);
				transaction.setTargetDetails(user.getFullName());
				transaction.setChanges(dialog.getChanges());
				TransactionFacade.getInstance().insert(transaction);

			}
		});
		dialog.getConfirmButton().setEnabled(false);

		if (AuthenticationService.getCurrentSessionUser().getId().equals(user.getId())) {
			if (PermissionFacade.getInstance().isUserAllowedTo(Operation.EDIT, OperationTarget.PERMISSIONS, PermissionRange.OWN)) {
				dialog.getConfirmButton().setEnabled(true);
			}
		} else {
			if (AuthenticationService.getCurrentSessionUser().getPermissionLevel().equalsTo(PermissionLevel.SYSTEM_ADMIN) ||
					PermissionFacade.getInstance().isUserAllowedTo(Operation.EDIT, OperationTarget.PERMISSIONS, PermissionRange.COMPANY)) {
				dialog.getConfirmButton().setEnabled(true);
			}
		}

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

		if (isNew) {
			companyComboBox.setValue(AuthenticationService.getCurrentSessionUser().getCompany());
		}
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

				user.setPermissions(tempPermissions);

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
		if (!originalUser.getPermissionLevel().equals(user.getPermissionLevel())) {
			changes.add("Permission level changed from: '" + originalUser.getPermissionLevel().getName() + "', to: '" + user.getPermissionLevel().getName() + "'");
		}
		if (!originalUser.getCompany().equals(user.getCompany())) {
			changes.add("User company changed from: '" + originalUser.getCompany().getName() + "', to: '" + user.getCompany().getName() + "'");
		}

		List<String> addressChanges = addressForm.getChanges();
		if (addressChanges.size() > 0) {
			changes.add("Address changed");
			changes.addAll(addressChanges);
		}

		List<String> personChanges = personForm.getChanges();
		if (personChanges.size() > 0) {
			changes.add("Personal information changed");
			changes.addAll(personChanges);
		}

		return changes;
	}
}
