package com.gmail.grigorij.backend.enums.operations;

import com.gmail.grigorij.backend.enums.permissions.PermissionLevel;

public enum OperationTarget {

	USER("User", PermissionLevel.USER),
	PERMISSIONS("Permissions", PermissionLevel.SYSTEM_ADMIN),
	PERMISSION_LEVEL("Permission Level", PermissionLevel.SYSTEM_ADMIN),
	COMPANY("Company", PermissionLevel.SYSTEM_ADMIN),
	INVENTORY_CATEGORY("Category", PermissionLevel.COMPANY_ADMIN),
	INVENTORY_TOOL("Tool", PermissionLevel.COMPANY_ADMIN);

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
