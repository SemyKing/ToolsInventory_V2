package com.gmail.grigorij.backend.entities.transaction;

public enum TransactionTarget {
	USER("User"),
	COMPANY("Company"),
	LOCATION("Location"),
	CATEGORY("Category"),
	TOOL("Tool"),
	TOOL_STATUS("Tool Status"),
	ACCESS_RIGHTS("Access Rights");

	private String name;

	TransactionTarget(String name) {
		this.name = name;
	}

	public String getStringValue() {
		return this.name;
	}
}
