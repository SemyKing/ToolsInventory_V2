package com.gmail.grigorij.backend.enums.inventory;

import com.gmail.grigorij.ui.components.CustomBadge.BadgeColor;
import com.vaadin.flow.component.icon.VaadinIcon;

public enum ToolStatus {

	//Tool is in use
	IN_USE(     "In Use",       VaadinIcon.CLOCK,           BadgeColor.GREY),

	//Tool is free, but reserved -> can be taken only by user who reserved it
	RESERVED(   "Reserved",     VaadinIcon.CALENDAR_CLOCK,  BadgeColor.BLUE),

	//Tool is in use and was reserved by another user
	IN_USE_AND_RESERVED(   "In Use & Reserved",     VaadinIcon.CALENDAR_CLOCK,  BadgeColor.GREY),

	//Tool is free
	FREE(       "Free",         VaadinIcon.CHECK,           BadgeColor.GREEN),

	//Tool is lost
	LOST(       "Lost",         VaadinIcon.WARNING,         BadgeColor.RED),

	//Tool is broken
	BROKEN(     "Broken",       VaadinIcon.WRENCH,          BadgeColor.RED);

	private String stringValue;
	private VaadinIcon icon;
	private BadgeColor color;

	ToolStatus(String stringValue, VaadinIcon icon, BadgeColor color) {
		this.stringValue = stringValue;
		this.icon = icon;
		this.color = color;
	}

	public String getStringValue() {
		return stringValue;
	}

	public VaadinIcon getIcon() {
		return this.icon;
	}

	public BadgeColor getColor() {
		return this.color;
	}
}
