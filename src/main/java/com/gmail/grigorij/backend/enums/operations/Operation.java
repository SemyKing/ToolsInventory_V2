package com.gmail.grigorij.backend.enums.operations;

import com.gmail.grigorij.backend.enums.permissions.PermissionLevel;

/**
 * PermissionLevel is used for Access Rights management.
 * Users with PermissionLevel LOWER than minimalPermissionLevel cannot see / edit said Operation
 */

public enum Operation {

	VIEW("View", PermissionLevel.USER),
	ADD("Add", PermissionLevel.COMPANY_ADMIN),
	EDIT("Edit", PermissionLevel.COMPANY_ADMIN),
	DELETE("Delete", PermissionLevel.SYSTEM_ADMIN),
	SEND("Send", PermissionLevel.USER),
	LOG_IN("Log In", PermissionLevel.SYSTEM_ADMIN),
	LOG_OUT("Log Out", PermissionLevel.SYSTEM_ADMIN),
	REPORT("Report", PermissionLevel.COMPANY_ADMIN),
	RESERVE("Reserve", PermissionLevel.COMPANY_ADMIN),
	TAKE("Take", PermissionLevel.COMPANY_ADMIN),
	RETURN("Return", PermissionLevel.COMPANY_ADMIN),
	CANCEL_RESERVATION("Cancel Reservation", PermissionLevel.COMPANY_ADMIN);

	private final String name;
	private final PermissionLevel minimalPermissionLevel;

	Operation(String name, PermissionLevel minimalPermissionLevel) {
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
