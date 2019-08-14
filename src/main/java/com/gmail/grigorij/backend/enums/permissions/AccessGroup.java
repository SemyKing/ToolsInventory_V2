package com.gmail.grigorij.backend.enums.permissions;

public enum AccessGroup {

	VIEWER          (1, PermissionLevel.OWN,    "VIEWER"),
	EMPLOYEE        (2, PermissionLevel.OWN,    "EMPLOYEE"),
	FOREMAN         (3, PermissionLevel.COMPANY,"FOREMAN"),
	COMPANY_ADMIN   (4, PermissionLevel.COMPANY,"COMPANY ADMIN"),
	SYSTEM_ADMIN    (5, PermissionLevel.SYSTEM, "SYSTEM ADMIN");


	private final int value;
	private final PermissionLevel permissionLevel;
	private final String name;

	AccessGroup(int value, PermissionLevel permissionLevel, String name) {
		this.name = name;
		this.permissionLevel = permissionLevel;
		this.value = value;
	}

	public int getIntegerValue() {
		return this.value;
	}
	public String getStringValue() {
		return this.name;
	}
	public PermissionLevel getPermissionLevel() {
		return permissionLevel;
	}
}