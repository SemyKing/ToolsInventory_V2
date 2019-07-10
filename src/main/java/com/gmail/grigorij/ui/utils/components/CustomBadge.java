package com.gmail.grigorij.ui.utils.components;

import com.gmail.grigorij.ui.utils.css.BorderRadius;
import com.gmail.grigorij.ui.utils.css.size.Horizontal;
import com.gmail.grigorij.ui.utils.css.size.Right;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;


public class CustomBadge extends FlexBoxLayout {

	private Icon icon = null;
	private Span text = null;


	public enum BadgeColor {
		RED("var(--lumo-error-color-10pct)", "var(--lumo-error-text-color)"),
		GREEN("var(--lumo-success-color-10pct)", "var(--lumo-success-text-color)"),
		BLUE("var(--lumo-primary-color-10pct)","var(--lumo-primary-text-color)"),
		GREY("var(--lumo-contrast-5pct)","var(--lumo-contrast-80pct)");

		private String backGroundColor, textColor;

		BadgeColor(String backGroundColor, String textColor) {
			this.backGroundColor = backGroundColor;
			this.textColor = textColor;
		}

		public String getBackGroundColor() {
			return backGroundColor;
		}
		public String getTextColor() {
			return textColor;
		}
	}

	public CustomBadge(String text) {
		this(text, BadgeColor.GREY);
	}

	public CustomBadge(String text, BadgeColor badgeColor) {
		this(text, badgeColor, null);
	}

	public CustomBadge(String text, BadgeColor badgeColor, VaadinIcon icon) {
		this.setSizeFull();
		this.setPadding(Horizontal.S);
		this.setAlignItems(Alignment.CENTER);
		this.setBorderRadius(BorderRadius.S);

		this.setBackgroundColor(badgeColor.getBackGroundColor());
		this.setColor(badgeColor.getTextColor());

		if (icon != null) {
			this.icon = new Icon(icon);
			this.setComponentPadding(this.icon, Right.S);
			this.add(this.icon);
		}

		if (text.length() > 0) {
			this.text = new Span(text);
			this.text.getStyle().set("font-weight", "bold");
			this.add(this.text);
		}

//		add(this);
	}

	public CustomBadge getBadge() {
		return this;
	}
}
