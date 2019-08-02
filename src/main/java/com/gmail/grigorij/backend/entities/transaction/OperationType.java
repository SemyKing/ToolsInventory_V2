package com.gmail.grigorij.backend.entities.transaction;

public enum OperationType {
	ADD("Add"),
	EDIT("Edit"),
	CHANGE("Change"),
	COPY("Copy"),
	UPDATE("Update"),
	DELETE("Delete"),

	START("Start"),
	STOP("Stop"),

	LOGIN("Log In"),
	LOGOUT("Log Out");

	private String name;

	OperationType(String name) {
		this.name = name;
	}

	public String getStringValue() {
		return this.name;
	}
}
