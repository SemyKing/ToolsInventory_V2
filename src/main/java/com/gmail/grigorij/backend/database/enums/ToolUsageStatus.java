package com.gmail.grigorij.backend.database.enums;

public enum ToolUsageStatus {

	IN_USE("In Use", "var(--lumo-primary-color)"),
	RESERVED("Reserved", "var(--lumo-primary-color-50pct)"),
	IN_USE_AND_RESERVED("In Use & Reserved", "var(--lumo-shade-20pct)"),
	FREE("Free", "var(--lumo-success-color)"),
	LOST("Lost", "var(--lumo-error-color)"),
	BROKEN("Broken", "var(--lumo-error-color-50pct)");

	private String name;
	private String color;

	ToolUsageStatus(String name, String color) {
		this.name = name;
		this.color = color;
	}

	public String getName() {
		return name;
	}

	public String getColor() {
		return this.color;
	}
}
