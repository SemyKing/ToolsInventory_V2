package com.gmail.grigorij.backend.entities.inventory;

import com.gmail.grigorij.ui.utils.components.CustomBadge.BadgeColor;
import com.vaadin.flow.component.icon.VaadinIcon;

public enum ToolStatus {

	IN_USE(     "In Use",       VaadinIcon.CLOCK,           BadgeColor.GREY),
	RESERVED(   "Reserved",     VaadinIcon.CALENDAR_CLOCK,  BadgeColor.BLUE),
	FREE(       "Free",         VaadinIcon.CHECK,           BadgeColor.GREEN),
	LOST(       "Lost",         VaadinIcon.WARNING,         BadgeColor.RED),
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
