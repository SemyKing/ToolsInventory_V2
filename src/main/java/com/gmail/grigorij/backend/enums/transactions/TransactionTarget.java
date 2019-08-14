package com.gmail.grigorij.backend.enums.transactions;

public enum TransactionTarget {
	USER("User"),
	USER_STATUS("User Status"),
	USER_PASSWORD("User Password"),
	USER_ACCESS_RIGHTS("User Access Rights"),

	COMPANY("Company"),
	COMPANY_STATUS("Company Status"),
	COMPANY_LOCATION("Company Location"),

	CATEGORY("Category"),
	CATEGORY_STATUS("Category Status"),
	TOOL("Tool"),
	TOOL_STATUS("Tool Status");


	private String name;

	TransactionTarget(String name) {
		this.name = name;
	}

	public String getStringValue() {
		return this.name;
	}
}
