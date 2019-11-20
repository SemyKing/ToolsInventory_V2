package com.gmail.grigorij.backend.database.enums.tools;

public enum ToolParameter {

	NUMBERS("No.", 0.5f),
	NAME("Tool Name", 4f),
	SERIAL_NUMBER("Serial Number", 3f),
	RF_CODE("RF Code", 3f),
	BARCODE("Barcode", 3f),
	MANUFACTURER("Manufacturer", 2f),
	MODEL("Model", 2f),
	TOOL_INFO("Tool Info", 3f),
	USAGE_STATUS("Usage Status", 1f),
	CURRENT_USER("Current User", 2f),
	RESERVED_USER("Reserved User", 2f),
	DATE_BOUGHT("Date Bought", 1f),
	DATE_NEXT_MAINTENANCE("Date Next Maintenance", 1f),
	PRICE("Price", 1f),
	GUARANTEE("Guarantee", 1f),

	CURRENT_LOCATION("Current Location", 3f);


	private String name;
	private float prefWidth;


	ToolParameter(String name, float prefWidth) {
		this.name = name;
		this.prefWidth = prefWidth;
	}


	public String getName() {
		return name;
	}

	public float getPrefWidth() {
		return this.prefWidth;
	}
}