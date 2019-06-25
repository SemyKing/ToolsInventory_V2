package com.gmail.grigorij.backend.access;

public enum EntityStatus {

	ACTIVE  ("Active",  false), //isDeleted()
	INACTIVE("Inactive",true);

	private String stringValue;
	private boolean booleanValue;


	EntityStatus(String stringValue, boolean booleanValue) {
		this.stringValue = stringValue;
		this.booleanValue = booleanValue;
	}


	public String getStringValue() {
		return this.stringValue;
	}

	public boolean getBooleanValue() {
		return this.booleanValue;
	}
}
