package com.gmail.grigorij.backend.database.enums.permissions;

public enum PermissionRange {

	OWN("Own"),
	COMPANY("Company"),
	SYSTEM("System");

	private String name;

	PermissionRange(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}
}
