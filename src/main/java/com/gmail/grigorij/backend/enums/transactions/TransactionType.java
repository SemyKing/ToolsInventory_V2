package com.gmail.grigorij.backend.enums.transactions;

public enum TransactionType {
	ADD("Add"),
	EDIT("Edit"),
	DELETE("Delete"),

	LOGIN("Log In"),
	LOGOUT("Log Out");

	private String name;

	TransactionType(String name) {
		this.name = name;
	}

	public String getStringValue() {
		return this.name;
	}
}
