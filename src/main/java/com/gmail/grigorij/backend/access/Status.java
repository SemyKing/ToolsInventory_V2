package com.gmail.grigorij.backend.access;

public enum Status {

	ACTIVE  ("Active",  false), //isDeleted()
	INACTIVE("Inactive",true);

	private boolean value;
	private String name;

	Status (String name, boolean value) {
		this.name = name;
		this.value = value;
	}


	public String getStringValue() {
		return this.name;
	}

	public boolean getBooleanValue() {
		return this.value;
	}
}
