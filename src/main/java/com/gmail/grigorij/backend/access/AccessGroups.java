package com.gmail.grigorij.backend.access;

public enum AccessGroups {

	VIEW_ONLY_USER  ("VIEW ONLY USER",  1 ),
	EMPLOYEE        ("EMPLOYEE",        2 ),
	FOREMAN         ("FOREMAN",         3 ),
	EMPLOYER        ("EMPLOYER",        4 ),
	ADMIN           ("ADMIN",           5 );


	private String name;
	private int value;

	AccessGroups(String name, int value ) {
		this.name = name;
		this.value = value;
	}

	public String getStringValue() {
		return this.name;
	}

	public int getIntValue() {
		return this.value;
	}
}