package com.gmail.grigorij.backend.enums.permissions;

public enum PermissionLevel {
	//CAN SEE ONLY OWN DATA
	OWN(    1,  "Own"),

	//CAN SEE ONLY OWN & COMPANY DATA
	COMPANY(2,  "Company"),

	//CAN SEE ALL DATA (ALL COMPANIES & OWN)
	SYSTEM( 3,  "System");


	private final int level;
	private final String name;

	PermissionLevel(int level, String str) {
		this.level = level;
		this.name = str;
	}

	public int getIntegerValue() {
		return this.level;
	}

	public String getStringValue() {
		return this.name;
	}


	public boolean equalsTo(PermissionLevel pl) {
		if (pl == null) {
			return false;
		}
		return this.getIntegerValue() == pl.getIntegerValue();
	}

	public boolean lowerThan(PermissionLevel pl) {
		if (pl == null) {
			return false;
		}
		return this.getIntegerValue() < pl.getIntegerValue();
	}

	public boolean lowerOrEqualsTo(PermissionLevel pl) {
		if (pl == null) {
			return false;
		}
		return this.getIntegerValue() <= pl.getIntegerValue();
	}

	public boolean higherThan(PermissionLevel pl) {
		if (pl == null) {
			return false;
		}
		return this.getIntegerValue() > pl.getIntegerValue();
	}

	public boolean higherOrEqualsTo(PermissionLevel pl) {
		if (pl == null) {
			return false;
		}
		return this.getIntegerValue() >= pl.getIntegerValue();
	}
}
