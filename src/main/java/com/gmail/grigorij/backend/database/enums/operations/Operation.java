package com.gmail.grigorij.backend.database.enums.operations;

import com.gmail.grigorij.backend.database.enums.permissions.PermissionLevel;

/**
 * PermissionLevel is used for Access Rights management.
 * Users with PermissionLevel LOWER than minimalPermissionLevel cannot see / edit said Operation
 */

public enum Operation {

	VIEW("View", PermissionLevel.USER),
	SEND("Send", PermissionLevel.USER),

	ADD("Add", PermissionLevel.COMPANY_ADMIN),
	EDIT("Edit", PermissionLevel.COMPANY_ADMIN),
	COPY("Copy", PermissionLevel.COMPANY_ADMIN),
	IMPORT("Import", PermissionLevel.COMPANY_ADMIN),
	EXPORT("Export", PermissionLevel.COMPANY_ADMIN),
	REPORT("Report", PermissionLevel.COMPANY_ADMIN),
	RESERVE("Reserve", PermissionLevel.COMPANY_ADMIN),
	TAKE("Take", PermissionLevel.COMPANY_ADMIN),

	DELETE("Delete", PermissionLevel.SYSTEM_ADMIN),

	LOG_IN("Log In", PermissionLevel.FOR_TRANSACTIONS),
	LOG_OUT("Log Out", PermissionLevel.FOR_TRANSACTIONS),
	RETURN("Return", PermissionLevel.FOR_TRANSACTIONS),
	CANCEL_RESERVATION("Cancel Reservation", PermissionLevel.FOR_TRANSACTIONS),
	REQUEST("Request", PermissionLevel.FOR_TRANSACTIONS),
	CHANGE("Change", PermissionLevel.FOR_TRANSACTIONS);

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
