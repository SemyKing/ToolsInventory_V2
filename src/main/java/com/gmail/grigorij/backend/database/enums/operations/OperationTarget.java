package com.gmail.grigorij.backend.database.enums.operations;

import com.gmail.grigorij.backend.database.enums.permissions.PermissionLevel;

import java.util.Arrays;
import java.util.List;

public enum OperationTarget {

	USER("User", PermissionLevel.USER,
			Operation.VIEW, Operation.ADD, Operation.EDIT, Operation.DELETE),

	PASSWORD("Password", PermissionLevel.USER,
			Operation.CHANGE),

	MESSAGES("Messages", PermissionLevel.COMPANY_ADMIN,
			Operation.SEND),

	INVENTORY_CATEGORY("Category", PermissionLevel.COMPANY_ADMIN,
			Operation.VIEW, Operation.ADD, Operation.EDIT),

	INVENTORY_TOOL("Tool", PermissionLevel.COMPANY_ADMIN,
			Operation.VIEW, Operation.ADD, Operation.EDIT, Operation.COPY, Operation.REPORT, Operation.RESERVE, Operation.TAKE, Operation.DELETE),

	INVENTORY_TAB("Inventory Tab", PermissionLevel.SYSTEM_ADMIN,
			Operation.VIEW),

	MESSAGES_TAB("Messages Tab", PermissionLevel.SYSTEM_ADMIN,
			Operation.VIEW),

	TRANSACTIONS_TAB("Transactions Tab", PermissionLevel.SYSTEM_ADMIN,
			Operation.VIEW),

	REPORTING_TAB("Reporting Tab", PermissionLevel.SYSTEM_ADMIN,
			Operation.VIEW),

	PERMISSIONS("Permissions", PermissionLevel.SYSTEM_ADMIN,
			Operation.VIEW, Operation.EDIT),

	PERMISSION_LEVEL("Permission Level", PermissionLevel.SYSTEM_ADMIN,
			Operation.EDIT),

	COMPANY("Company", PermissionLevel.SYSTEM_ADMIN,
			Operation.VIEW, Operation.ADD, Operation.EDIT, Operation.DELETE),

	LOCATIONS("Locations", PermissionLevel.SYSTEM_ADMIN,
			Operation.VIEW, Operation.ADD, Operation.EDIT, Operation.DELETE),

	TRANSACTIONS("Transactions", PermissionLevel.SYSTEM_ADMIN,
			Operation.EDIT),


	PASSWORD_RESET_EMAIL_T("Password Reset Email", PermissionLevel.FOR_TRANSACTIONS),

	ANNOUNCEMENT_T("Announcement", PermissionLevel.FOR_TRANSACTIONS),

	STATUS_T("Status", PermissionLevel.FOR_TRANSACTIONS);


	private String name;
	private PermissionLevel minimalPermissionLevel;
	private List<Operation> forOperations;

	OperationTarget(String name, PermissionLevel minimalPermissionLevel, Operation... forOperation) {
		this.name = name;
		this.minimalPermissionLevel = minimalPermissionLevel;
		forOperations = Arrays.asList(forOperation);
	}

	public String getName() {
		return this.name;
	}

	public PermissionLevel getMinimalPermissionLevel() {
		return this.minimalPermissionLevel;
	}

	public List<Operation> getForOperation() {
		return forOperations;
	}
}
