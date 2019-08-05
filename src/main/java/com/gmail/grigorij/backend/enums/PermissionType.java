package com.gmail.grigorij.backend.enums;

public enum PermissionType {
	NOT_ALLOWED("Not Allowed"),
	READ_ONLY("Read"),
	EDIT("Edit");

	private String str;

	PermissionType(String str) {
		this.str = str;
	}

	public String getStringValue() {
		return this.str;
	}
}
