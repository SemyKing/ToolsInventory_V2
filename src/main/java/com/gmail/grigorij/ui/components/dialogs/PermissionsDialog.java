package com.gmail.grigorij.ui.components.dialogs;

import com.gmail.grigorij.backend.database.facades.PermissionFacade;
import com.gmail.grigorij.backend.entities.user.PermissionTest;
import com.gmail.grigorij.backend.entities.user.User;
import com.gmail.grigorij.backend.enums.operations.Operation;
import com.gmail.grigorij.backend.enums.operations.OperationPermission;
import com.gmail.grigorij.backend.enums.operations.OperationTarget;
import com.gmail.grigorij.backend.enums.permissions.PermissionRange;
import com.gmail.grigorij.backend.enums.permissions.PermissionLevel;
import com.gmail.grigorij.ui.components.layouts.FlexBoxLayout;
import com.gmail.grigorij.ui.utils.UIUtils;
import com.gmail.grigorij.utils.AuthenticationService;
import com.gmail.grigorij.utils.ProjectConstants;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcon;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;


public class PermissionsDialog extends CustomDialog {

	private final static String CLASS_NAME = "permissions-dialog";
	private final User user;

	private List<PermissionTest> editedPermissions;
	private List<PermissionTest> permissions = new ArrayList<>();
	private List<PermissionRowLayout> permissionRows = new ArrayList<>();

	private FlexBoxLayout content;
	private Div contentHeader;

	private boolean systemAdmin, editOwnAllowed, editOthersAllowed, self;


	public PermissionsDialog(User user) {
		this.user = user;

		systemAdmin = AuthenticationService.getCurrentSessionUser().getPermissionLevel().equalsTo(PermissionLevel.SYSTEM_ADMIN);

		if (!systemAdmin) {
			editOwnAllowed = PermissionFacade.getInstance().isUserAllowedTo(Operation.EDIT, OperationTarget.PERMISSIONS, PermissionRange.OWN);
			editOthersAllowed = PermissionFacade.getInstance().isUserAllowedTo(Operation.EDIT, OperationTarget.PERMISSIONS, PermissionRange.COMPANY);
		}

		self = user.getId().equals(AuthenticationService.getCurrentSessionUser().getId());

		getContent().add(constructContentHeader());
		getContent().add(constructContent());

		if (!self) { // CANNOT ADD PERMISSIONS TO SELF
			if (systemAdmin || PermissionFacade.getInstance().isUserAllowedTo(Operation.ADD, OperationTarget.PERMISSIONS, PermissionRange.COMPANY)) {
				Button addPermissionButton = UIUtils.createButton("Add Permission");
				addPermissionButton.addClickListener(e -> {
					content.add(constructPermissionRow(null));
					content.add(new Hr());
				});

				getContent().add(addPermissionButton);
			}
		}


		getCancelButton().addClickListener(cancelEditOnClick -> {
			ConfirmDialog confirmDialog = new ConfirmDialog("Are you sure you want to cancel?" + ProjectConstants.NEW_LINE + "All changes will be lost");
			confirmDialog.closeOnCancel();
			confirmDialog.getConfirmButton().addClickListener(confirmOnClick -> {
				confirmDialog.close();
				this.close();
			});
			confirmDialog.open();
		});

		getConfirmButton().setText("Save");
	}


	private Div constructContentHeader() {
		contentHeader = new Div();
		contentHeader.addClassName(CLASS_NAME + "__content_header");

		return contentHeader;
	}

	private FlexBoxLayout constructContent() {
		content = new FlexBoxLayout();
		content.addClassName(CLASS_NAME + "__content");

		return content;
	}


	public void constructView() {
		for (PermissionTest permission : user.getPermissions()) {
			permissions.add(new PermissionTest(permission));
		}

		contentHeader.add(UIUtils.createH4Label("Permissions for:   " + user.getFullName()));
		contentHeader.add(UIUtils.createH4Label("Company:           " + user.getCompany().getName()));
		contentHeader.add(UIUtils.createH4Label("Permission Level:  " + user.getPermissionLevel().getName()));
		contentHeader.add(new Hr());

		Div permissionHeaders = new Div();
		permissionHeaders.addClassName(CLASS_NAME + "__permission-headers");

		Label operationLabel = UIUtils.createH5Label("Operation");
		operationLabel.addClassName(CLASS_NAME + "__permission-headers__h1");
		permissionHeaders.add(operationLabel);

		Label targetLabel = UIUtils.createH5Label("Target");
		targetLabel.addClassName(CLASS_NAME + "__permission-headers__h2");
		permissionHeaders.add(targetLabel);

		Label ownLabel = UIUtils.createH5Label("Own");
		ownLabel.addClassName(CLASS_NAME + "__permission-headers__h3");
		permissionHeaders.add(ownLabel);

		Label companyLabel = UIUtils.createH5Label("Company");
		companyLabel.addClassName(CLASS_NAME + "__permission-headers__h4");
		permissionHeaders.add(companyLabel);

		if (systemAdmin) {
			Label systemLabel = UIUtils.createH5Label("System");
			systemLabel.addClassName(CLASS_NAME + "__permission-headers__h5");
			permissionHeaders.add(systemLabel);
		}
		if (!self) {
			Label visibleLabel = UIUtils.createH5Label("Visible");
			visibleLabel.addClassName(CLASS_NAME + "__permission-headers__h6");
			permissionHeaders.add(visibleLabel);
		}


		content.add(permissionHeaders);

		populateData();
	}

	private void populateData() {
		for (PermissionTest permission : permissions) {

			if (systemAdmin) {
				content.add(constructPermissionRow(permission));
				content.add(new Hr());
			} else {
				if (self) {
					if (permission.isVisible()) {
						content.add(constructPermissionRow(permission));
						content.add(new Hr());
					}
				} else {
					content.add(constructPermissionRow(permission));
					content.add(new Hr());
				}
			}
		}
	}

	private PermissionRowLayout constructPermissionRow(PermissionTest permission) {
		PermissionRowLayout permissionRow = new PermissionRowLayout();

		if (permission != null) {
			permissionRow.getOperationComboBox().setValue(permission.getOperation());
			permissionRow.getTargetComboBox().setValue(permission.getOperationTarget());
			permissionRow.getPermissionOwnComboBox().setValue(permission.getPermissionOwn());
			permissionRow.getPermissionCompanyComboBox().setValue(permission.getPermissionCompany());

			if (systemAdmin) {
				permissionRow.addPermissionSystemComboBox();
				permissionRow.getPermissionSystemComboBox().setValue(permission.getPermissionSystem());

				permissionRow.addVisibleCheckBox();
				permissionRow.getVisibleCheckbox().setValue(permission.isVisible());

				initDeleteButton(permissionRow);
			} else {
				if (!self) {
					permissionRow.addVisibleCheckBox();
					permissionRow.getVisibleCheckbox().setValue(permission.isVisible());

					if (PermissionFacade.getInstance().isUserAllowedTo(Operation.DELETE, OperationTarget.PERMISSIONS, PermissionRange.COMPANY)) {
						initDeleteButton(permissionRow);
					}

					if (!editOthersAllowed) {
						permissionRow.getOperationComboBox().setReadOnly(true);
						permissionRow.getTargetComboBox().setReadOnly(true);
						permissionRow.getPermissionOwnComboBox().setReadOnly(true);
						permissionRow.getPermissionCompanyComboBox().setReadOnly(true);
						permissionRow.getPermissionSystemComboBox().setReadOnly(true);
						permissionRow.getVisibleCheckbox().setReadOnly(true);
					}
				} else {
					if (!editOwnAllowed) {

						permissionRow.getOperationComboBox().setReadOnly(true);
						permissionRow.getTargetComboBox().setReadOnly(true);
						permissionRow.getPermissionOwnComboBox().setReadOnly(true);
						permissionRow.getPermissionCompanyComboBox().setReadOnly(true);
						permissionRow.getPermissionSystemComboBox().setReadOnly(true);
						permissionRow.getVisibleCheckbox().setReadOnly(true);
					}
				}
			}
		} else {
			if (systemAdmin) {
				permissionRow.addPermissionSystemComboBox();
				permissionRow.addVisibleCheckBox();

				initDeleteButton(permissionRow);
			} else {
				if (!self) {
					permissionRow.addVisibleCheckBox();

					if (PermissionFacade.getInstance().isUserAllowedTo(Operation.DELETE, OperationTarget.PERMISSIONS, PermissionRange.COMPANY)) {
						initDeleteButton(permissionRow);
					}
				}
			}
		}

		permissionRows.add(permissionRow);

		return permissionRow;
	}

	private void initDeleteButton(PermissionRowLayout permissionRow) {
		permissionRow.addDeleteButton();
		permissionRow.getDeleteButton().addClickListener(e -> {

			ConfirmDialog dialog = new ConfirmDialog("Delete permission?");
			dialog.closeOnCancel();
			dialog.getConfirmButton().addClickListener(confirmEvent -> {
				int itemIndex = content.indexOf(permissionRow);
				Hr hrBelow = null;

				if (itemIndex >= 0) {
					int nextItemIndex = ++itemIndex;

					Component component = content.getComponentAt((nextItemIndex));

					if (component instanceof Hr) {
						hrBelow = (Hr)content.getComponentAt((nextItemIndex));
					}
				}

				content.remove(permissionRow);

				if (hrBelow != null) {
					content.remove(hrBelow);
				}
				permissionRows.remove(permissionRow);
				dialog.close();
			});
			dialog.open();
		});
	}


	public List<PermissionTest> getPermissions() {
		if (validate()) {
			return editedPermissions;
		} else {
			return null;
		}
	}


	private boolean validate() {
		editedPermissions = new ArrayList<>();

		if (systemAdmin) {
			for (PermissionRowLayout permissionRow : permissionRows) {

				if (permissionRow.getOperationComboBox().getValue() == null) {
					permissionRow.getOperationComboBox().setInvalid(true);
					return false;
				}

				if (permissionRow.getTargetComboBox().getValue() == null) {
					permissionRow.getTargetComboBox().setInvalid(true);
					return false;
				}

				if (permissionRow.getPermissionOwnComboBox().getValue() == null) {
					permissionRow.getPermissionOwnComboBox().setInvalid(true);
					return false;
				}

				if (permissionRow.getPermissionCompanyComboBox().getValue() == null) {
					permissionRow.getPermissionCompanyComboBox().setInvalid(true);
					return false;
				}

				if (permissionRow.getPermissionSystemComboBox().getValue() == null) {
					permissionRow.getPermissionSystemComboBox().setInvalid(true);
					return false;
				}

				PermissionTest permission = new PermissionTest();
				permission.setOperation(permissionRow.getOperationComboBox().getValue());
				permission.setOperationTarget(permissionRow.getTargetComboBox().getValue());
				permission.setPermissionOwn(permissionRow.getPermissionOwnComboBox().getValue());
				permission.setPermissionCompany(permissionRow.getPermissionCompanyComboBox().getValue());
				permission.setPermissionSystem(permissionRow.getPermissionSystemComboBox().getValue());
				permission.setVisible(permissionRow.getVisibleCheckbox().getValue());

				editedPermissions.add(permission);
			}
		} else {

			if (!editOwnAllowed || !editOthersAllowed) {
				editedPermissions = permissions;
				return true;
			}

			// COMPANY ADMIN
			if (self) {

				// ADD HIDDEN PERMISSIONS
				for (PermissionTest permission : permissions) {
					if (!permission.isVisible()) {
						editedPermissions.add(permission);
					}
				}
			}

			for (PermissionRowLayout permissionRow : permissionRows) {
				if (permissionRow.getOperationComboBox().getValue() == null) {
					permissionRow.getOperationComboBox().setInvalid(true);
					return false;
				}

				if (permissionRow.getTargetComboBox().getValue() == null) {
					permissionRow.getTargetComboBox().setInvalid(true);
					return false;
				}

				if (permissionRow.getPermissionOwnComboBox().getValue() == null) {
					permissionRow.getPermissionOwnComboBox().setInvalid(true);
					return false;
				}

				if (permissionRow.getPermissionCompanyComboBox().getValue() == null) {
					permissionRow.getPermissionCompanyComboBox().setInvalid(true);
					return false;
				}

				PermissionTest permission = new PermissionTest();
				permission.setOperation(permissionRow.getOperationComboBox().getValue());
				permission.setOperationTarget(permissionRow.getTargetComboBox().getValue());
				permission.setPermissionOwn(permissionRow.getPermissionOwnComboBox().getValue());
				permission.setPermissionCompany(permissionRow.getPermissionCompanyComboBox().getValue());
				permission.setPermissionSystem(OperationPermission.NO);
				permission.setVisible(permissionRow.getVisibleCheckbox().getValue());

				editedPermissions.add(permission);
			}
		}

		return true;
	}


	private static class PermissionRowLayout extends Div {

		private final static String CLASS_NAME = "permission-row";

		private Div permissionsDiv;
		private Div visibleDeleteDiv;

		private ComboBox<Operation> operationComboBox;
		private ComboBox<OperationTarget> targetComboBox;
		private ComboBox<OperationPermission> permissionOwnComboBox;
		private ComboBox<OperationPermission> permissionCompanyComboBox;
		private ComboBox<OperationPermission> permissionSystemComboBox;
		private Checkbox visibleCheckbox;

		private Button deleteButton;


		PermissionRowLayout() {
			addClassName(CLASS_NAME);

			Div operationAndTargetDiv = new Div();
			operationAndTargetDiv.addClassName(CLASS_NAME + "__operation_target");

			operationComboBox = new ComboBox<>();

			List<Operation> operations = new ArrayList<>(EnumSet.allOf(Operation.class));
			operations.removeIf(operation -> operation.getMinimalPermissionLevel().higherThan(AuthenticationService.getCurrentSessionUser().getPermissionLevel()));

			operationComboBox.setItems(operations);
			operationComboBox.setItemLabelGenerator(Operation::getName);
			operationComboBox.setErrorMessage("Value Required");
			operationComboBox.setRequired(true);
			operationComboBox.addValueChangeListener(e -> {
				operationComboBox.setInvalid(false);
			});

			List<OperationTarget> targets = new ArrayList<>(EnumSet.allOf(OperationTarget.class));
			targets.removeIf(target -> target.getMinimalPermissionLevel().higherThan(AuthenticationService.getCurrentSessionUser().getPermissionLevel()));

			targetComboBox = new ComboBox<>();
			targetComboBox.setItems(targets);
			targetComboBox.setItemLabelGenerator(OperationTarget::getName);
			targetComboBox.setErrorMessage("Value Required");
			targetComboBox.setRequired(true);
			targetComboBox.addValueChangeListener(e -> {
				targetComboBox.setInvalid(false);
			});

			operationAndTargetDiv.add(operationComboBox, targetComboBox);


			permissionsDiv = new Div();
			permissionsDiv.addClassName(CLASS_NAME + "__permissions");

			permissionOwnComboBox = new ComboBox<>();
			permissionOwnComboBox.setItems(EnumSet.allOf(OperationPermission.class));
			permissionOwnComboBox.setItemLabelGenerator(OperationPermission::getName);
			permissionOwnComboBox.setErrorMessage("Value Required");
			permissionOwnComboBox.setRequired(true);
			permissionOwnComboBox.addValueChangeListener(e -> {
				permissionOwnComboBox.setInvalid(false);
			});

			permissionCompanyComboBox = new ComboBox<>();
			permissionCompanyComboBox.setItems(EnumSet.allOf(OperationPermission.class));
			permissionCompanyComboBox.setItemLabelGenerator(OperationPermission::getName);
			permissionCompanyComboBox.setErrorMessage("Value Required");
			permissionCompanyComboBox.setRequired(true);
			permissionCompanyComboBox.addValueChangeListener(e -> {
				permissionCompanyComboBox.setInvalid(false);
			});

			permissionSystemComboBox = new ComboBox<>();
			permissionSystemComboBox.setItems(EnumSet.allOf(OperationPermission.class));
			permissionSystemComboBox.setItemLabelGenerator(OperationPermission::getName);
			permissionSystemComboBox.setErrorMessage("Value Required");
			permissionSystemComboBox.setRequired(true);
			permissionSystemComboBox.addValueChangeListener(e -> {
				permissionSystemComboBox.setInvalid(false);
			});

			permissionsDiv.add(permissionOwnComboBox, permissionCompanyComboBox);


			visibleDeleteDiv = new Div();
			visibleDeleteDiv.addClassName(CLASS_NAME + "__vis_del");

			visibleCheckbox = new Checkbox();
			deleteButton = UIUtils.createButton(VaadinIcon.TRASH, ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ERROR);

			add(operationAndTargetDiv);
			add(permissionsDiv);
			add(visibleDeleteDiv);
		}

		private void addPermissionSystemComboBox() {
			permissionsDiv.add(permissionSystemComboBox);
		}

		private void addVisibleCheckBox() {
			visibleDeleteDiv.add(visibleCheckbox);
		}

		private void addDeleteButton() {
			visibleDeleteDiv.add(deleteButton);
		}

		private ComboBox<Operation> getOperationComboBox() {
			return operationComboBox;
		}
		private ComboBox<OperationTarget> getTargetComboBox() {
			return targetComboBox;
		}
		private ComboBox<OperationPermission> getPermissionOwnComboBox() {
			return permissionOwnComboBox;
		}
		private ComboBox<OperationPermission> getPermissionCompanyComboBox() {
			return permissionCompanyComboBox;
		}
		private ComboBox<OperationPermission> getPermissionSystemComboBox() {
			return permissionSystemComboBox;
		}

		private Checkbox getVisibleCheckbox() {
			return visibleCheckbox;
		}

		private Button getDeleteButton() {
			return deleteButton;
		}
	}
}
