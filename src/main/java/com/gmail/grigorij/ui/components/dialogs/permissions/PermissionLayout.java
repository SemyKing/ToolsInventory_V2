package com.gmail.grigorij.ui.components.dialogs.permissions;

import com.gmail.grigorij.backend.database.entities.embeddable.Permission;
import com.gmail.grigorij.backend.database.enums.operations.Operation;
import com.gmail.grigorij.backend.database.enums.operations.OperationPermission;
import com.gmail.grigorij.backend.database.enums.operations.OperationTarget;
import com.gmail.grigorij.backend.database.enums.permissions.PermissionRange;
import com.gmail.grigorij.utils.ProjectConstants;
import com.gmail.grigorij.utils.authentication.AuthenticationService;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.Div;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class PermissionLayout extends Div {

	private final String CLASS_NAME = "permission_layout";

	private ComboBox<Operation> operationComboBox;
	private ComboBox<OperationTarget> targetComboBox;
	private ComboBox<OperationPermission> permissionOwnComboBox;
	private ComboBox<OperationPermission> permissionCompanyComboBox;


	PermissionLayout(Permission permission) {
		addClassName(CLASS_NAME);

		// OPERATION
		List<Operation> operations = new ArrayList<>(EnumSet.allOf(Operation.class));
		operations.removeIf(target -> target.getMinimalPermissionLevel().higherThan(AuthenticationService.getCurrentSessionUser().getPermissionLevel()));

		operationComboBox = new ComboBox<>();
		operationComboBox.setLabel("Operation");
		operationComboBox.setItems(operations);
		operationComboBox.setItemLabelGenerator(Operation::getName);
		operationComboBox.setErrorMessage(ProjectConstants.VALUE_REQUIRED);
		operationComboBox.setRequired(true);
		if (permission != null) {
			operationComboBox.setValue(permission.getOperation());
		}
		operationComboBox.addValueChangeListener(operationChangeEvent -> {
			if (operationChangeEvent.getValue() != operationChangeEvent.getOldValue()) {

				List<OperationTarget> targets = new ArrayList<>(EnumSet.allOf(OperationTarget.class));
				targets.removeIf(target -> target.getMinimalPermissionLevel().higherThan(AuthenticationService.getCurrentSessionUser().getPermissionLevel()));
				targets.removeIf(target -> {
					if (target.getForOperation() != null) {
						for (Operation operation : target.getForOperation()) {
							if (operation.equals(operationChangeEvent.getValue())) {
								return false;
							}
						}
					}
					return true;
				});
				targetComboBox.setItems(targets);
			}
		});
		add(operationComboBox);


		targetComboBox = new ComboBox<>();
		targetComboBox.setLabel("Target");
		targetComboBox.setItems();
		targetComboBox.setItemLabelGenerator(OperationTarget::getName);
		targetComboBox.setErrorMessage(ProjectConstants.VALUE_REQUIRED);
		targetComboBox.setRequired(true);
		if (permission != null) {
			targetComboBox.setValue(permission.getOperationTarget());
		}
		add(targetComboBox);


		// OWN
		permissionOwnComboBox = new ComboBox<>();
		permissionOwnComboBox.setLabel(PermissionRange.OWN.getName());
		permissionOwnComboBox.setItems(EnumSet.allOf(OperationPermission.class));
		permissionOwnComboBox.setItemLabelGenerator(OperationPermission::getName);
		permissionOwnComboBox.setErrorMessage(ProjectConstants.VALUE_REQUIRED);
		permissionOwnComboBox.setRequired(true);
		if (permission != null) {
			permissionOwnComboBox.setValue(permission.getPermissionOwn());
		} else {
			permissionOwnComboBox.setValue(OperationPermission.NO);
		}
		add(permissionOwnComboBox);


		// COMPANY
		permissionCompanyComboBox = new ComboBox<>();
		permissionCompanyComboBox.setLabel(PermissionRange.COMPANY.getName());
		permissionCompanyComboBox.setItems(EnumSet.allOf(OperationPermission.class));
		permissionCompanyComboBox.setItemLabelGenerator(OperationPermission::getName);
		permissionCompanyComboBox.setErrorMessage(ProjectConstants.VALUE_REQUIRED);
		permissionCompanyComboBox.setRequired(true);
		if (permission != null) {
			permissionCompanyComboBox.setValue(permission.getPermissionCompany());
		} else {
			permissionCompanyComboBox.setValue(OperationPermission.NO);
		}
		add(permissionCompanyComboBox);
	}


	private Permission permission;

	public Permission getPermission() {
		permission = new Permission();
		if (isValid()) {
			return permission;
		} else {
			return null;
		}
	}

	public boolean isValid() {
		if (operationComboBox.getValue() == null || operationComboBox.isInvalid()) {
			return false;
		} else {
			permission.setOperation(operationComboBox.getValue());
		}

		if (targetComboBox.getValue() == null || targetComboBox.isInvalid()) {
			return false;
		} else {
			permission.setOperationTarget(targetComboBox.getValue());
		}

		if (permissionOwnComboBox.getValue() == null || permissionOwnComboBox.isInvalid()) {
			return false;
		} else {
			permission.setPermissionOwn(permissionOwnComboBox.getValue());
		}

		if (permissionCompanyComboBox.getValue() == null || permissionCompanyComboBox.isInvalid()) {
			return false;
		} else {
			permission.setPermissionCompany(permissionCompanyComboBox.getValue());
		}

		return true;
	}
}
