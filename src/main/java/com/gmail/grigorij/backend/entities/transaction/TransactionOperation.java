package com.gmail.grigorij.backend.entities.transaction;

public enum TransactionOperation {
	ADD("Add"),
	EDIT("Edit"),
	UPDATE("Update"),
	DELETE("Delete"),

	LOGIN("Log In"),
	LOGOUT("Log Out");

	private String name;

	TransactionOperation(String name) {
		this.name = name;
	}

	public String getStringValue() {
		return this.name;
	}
}
