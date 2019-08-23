package com.gmail.grigorij.backend.enums.permissions;

public enum Permission {
	YES( "YES"),
	NO(  "NO");

	private final String name;

	Permission(String name) {this.name = name;}

	public String getStringValue() {
		return this.name;
	}
}
