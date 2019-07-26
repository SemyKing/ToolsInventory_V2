package com.gmail.grigorij.backend.entities.transaction;

public enum OperationTarget {
	USER("User"),
	COMPANY("Company"),
	LOCATION("Location"),
	CATEGORY("Category"),
	TOOL("Tool"),
	TOOL_STATUS("Tool Status"),
	ACCESS_RIGHTS("Access Rights"),
	SERVER("Server");

	private String name;

	OperationTarget(String name) {
		this.name = name;
	}

	public String getStringValue() {
		return this.name;
	}
}
