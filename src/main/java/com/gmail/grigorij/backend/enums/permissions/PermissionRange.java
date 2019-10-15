package com.gmail.grigorij.backend.enums.permissions;

import com.gmail.grigorij.backend.enums.permissions.PermissionLevel;

public enum PermissionRange {

	OWN("Own", PermissionLevel.USER),
	COMPANY("Company", PermissionLevel.COMPANY_ADMIN),
	SYSTEM("System", PermissionLevel.SYSTEM_ADMIN);

	private String name;
	private PermissionLevel minimalPermissionLevel;

	PermissionRange(String name, PermissionLevel minimalPermissionLevel) {
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
