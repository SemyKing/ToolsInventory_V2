package com.gmail.grigorij.backend.enums;

public enum OperationTarget {
	USER("User"),
	COMPANY("Company"),
	LOCATION("Location"),
	CATEGORY("Category"),
	TOOL("Tool"),
	TOOL_STATUS("Tool Status"),
	ACCESS_RIGHTS("Access Rights");

	private String name;

	OperationTarget(String name) {
		this.name = name;
	}

	public String getStringValue() {
		return this.name;
	}
}
