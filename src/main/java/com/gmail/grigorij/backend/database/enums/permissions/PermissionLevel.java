package com.gmail.grigorij.backend.database.enums.permissions;

public enum PermissionLevel {

	VIEWER("Viewer", 0),
	USER("User", 1),
	FOREMAN("Foreman", 2),
	COMPANY_ADMIN("Company Admin", 3),
	SYSTEM_ADMIN("System Admin", 99),
	FOR_TRANSACTIONS("System Admin", 999);


	private final String name;
	private final int level;

	PermissionLevel(String name, int level) {
		this.name = name;
		this.level = level;
	}

	public String getName() {
		return this.name;
	}
	public int getLevel() {
		return this.level;
	}


	public boolean equalsTo(PermissionLevel pl) {
		if (pl == null) {
			return false;
		}
		return this.getLevel() == pl.getLevel();
	}

	public boolean lowerThan(PermissionLevel pl) {
		if (pl == null) {
			return false;
		}
		return this.getLevel() < pl.getLevel();
	}

	public boolean lowerOrEqualsTo(PermissionLevel pl) {
		if (pl == null) {
			return false;
		}
		return this.getLevel() <= pl.getLevel();
	}

	public boolean higherThan(PermissionLevel pl) {
		if (pl == null) {
			return false;
		}
		return this.getLevel() > pl.getLevel();
	}

	public boolean higherOrEqualsTo(PermissionLevel pl) {
		if (pl == null) {
			return false;
		}
		return this.getLevel() >= pl.getLevel();
	}
}
