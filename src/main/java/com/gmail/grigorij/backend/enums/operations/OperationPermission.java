package com.gmail.grigorij.backend.enums.operations;

public enum OperationPermission {

	YES( "YES", true),
	NO(  "NO", false);

	private final String name;
	private final boolean allowed;

	OperationPermission(String name, boolean allowed) {
		this.name = name;
		this.allowed = allowed;
	}

	public String getName() {
		return this.name;
	}
	public boolean isAllowed() {
		return this.allowed;
	}
}
