package com.gmail.grigorij.backend.entities.tool;

import com.gmail.grigorij.ui.utils.css.badge.BadgeColor;
import com.vaadin.flow.component.icon.VaadinIcon;

public enum ToolStatus {
	IN_USE( "In Use",   VaadinIcon.CLOCK,   BadgeColor.CONTRAST),
	FREE(   "Free",     VaadinIcon.CHECK,   BadgeColor.SUCCESS),
	LOST(   "Lost",     VaadinIcon.WARNING, BadgeColor.ERROR);

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
