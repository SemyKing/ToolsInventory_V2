package com.gmail.grigorij.backend.database.enums.operations;

import com.gmail.grigorij.backend.database.enums.permissions.PermissionLevel;

public enum OperationTarget {

	USER("User", PermissionLevel.USER),

	MESSAGES("Messages", PermissionLevel.COMPANY_ADMIN),
	INVENTORY_CATEGORY("Category", PermissionLevel.COMPANY_ADMIN),
	INVENTORY_TOOL("Tool", PermissionLevel.COMPANY_ADMIN),

	INVENTORY_TAB("Inventory Tab", PermissionLevel.SYSTEM_ADMIN),
	MESSAGES_TAB("Messages Tab", PermissionLevel.SYSTEM_ADMIN),
	TRANSACTIONS_TAB("Transactions Tab", PermissionLevel.SYSTEM_ADMIN),
	REPORTING_TAB("Reporting Tab", PermissionLevel.SYSTEM_ADMIN),
	PERMISSIONS("Permissions", PermissionLevel.SYSTEM_ADMIN),
	PERMISSION_LEVEL("Permission Level", PermissionLevel.SYSTEM_ADMIN),
	COMPANY("Company", PermissionLevel.SYSTEM_ADMIN),
	LOCATIONS("Locations", PermissionLevel.SYSTEM_ADMIN),
	TRANSACTIONS("Transactions", PermissionLevel.SYSTEM_ADMIN),

	PASSWORD("Password", PermissionLevel.FOR_TRANSACTIONS),
	PASSWORD_RESET_EMAIL("Password Reset Email", PermissionLevel.FOR_TRANSACTIONS);

	private String name;
	private PermissionLevel minimalPermissionLevel;

	OperationTarget(String name, PermissionLevel minimalPermissionLevel) {
		this.name = name;
		this.minimalPermissionLevel = minimalPermissionLevel;
	}

	public String getName() {
		return this.name;
	}

	public PermissionLevel getMinimalPermissionLevel() {
		return this.minimalPermissionLevel;
	}
}
