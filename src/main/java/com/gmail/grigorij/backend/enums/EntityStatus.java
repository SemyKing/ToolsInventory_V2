package com.gmail.grigorij.backend.enums;

public enum EntityStatus {

	ACTIVE("Active", false), INACTIVE("Inactive", true);

	private String name;
	private boolean deleted;

	EntityStatus(String name, boolean deleted) {
		this.name = name;
		this.deleted = deleted;
	}

	public String getName() {
		return name;
	}

	public boolean isDeleted() {
		return deleted;
	}
}
