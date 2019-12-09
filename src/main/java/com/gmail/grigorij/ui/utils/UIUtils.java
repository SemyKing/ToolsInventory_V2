package com.gmail.grigorij.ui.utils;

import com.gmail.grigorij.ui.components.FlexBoxLayout;
import com.gmail.grigorij.ui.utils.css.FontSize;
import com.gmail.grigorij.ui.utils.css.LumoStyles;
import com.gmail.grigorij.ui.utils.css.Overflow;
import com.gmail.grigorij.ui.utils.css.TextColor;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;


public class UIUtils {

	private static final String CUSTOM_SMALL_BUTTON = "custom-vaadin-small-button";


	public static String entityStatusToString(boolean status) {
		if (status) {
			return "Inactive";
		} else {
			return "Active";
		}
	}

	public static String visibleBooleanToString(boolean visible) {
		if (visible) {
			return "visible";
		} else {
			return "invisible";
		}
	}



	/* ==== BUTTONS ==== */

	public static Button createSmallButton(VaadinIcon icon) {
		Button smallButton = createCustomButton("", icon, ButtonVariant.LUMO_SMALL);
		smallButton.addClassName(CUSTOM_SMALL_BUTTON);
		return smallButton;
	}

	public static Button createSmallButton(String text, ButtonVariant... variants) {
		Button smallButton = createCustomButton(text, null, variants);
		smallButton.addClassName(CUSTOM_SMALL_BUTTON);
		smallButton.addThemeVariants(ButtonVariant.LUMO_SMALL);
		return smallButton;
	}

	public static Button createSmallButton(String text, VaadinIcon icon, ButtonVariant... variants) {
		Button smallButton = createCustomButton(text, icon, variants);
		smallButton.addClassName(CUSTOM_SMALL_BUTTON);
		smallButton.addThemeVariants(ButtonVariant.LUMO_SMALL);
		return smallButton;
	}

	public static Button createIconButton(VaadinIcon icon, ButtonVariant... variants) {
		Button button = new Button();
		button.getStyle().set("cursor", "pointer");

		if (icon != null) {
			Icon i = new Icon(icon);
			i.getStyle().set("padding", "0");
			i.getElement().setAttribute("slot", "prefix");
			button.setIcon(i);
		}
		if (variants.length > 0) {
			button.addThemeVariants(variants);
		}

//		boolean setBackground = true;
//		for (ButtonVariant bv : variants) {
//			if (bv.equals(ButtonVariant.LUMO_PRIMARY) || bv.equals(ButtonVariant.LUMO_TERTIARY)) {
//				setBackground = false;
//				break;
//			}
//		}
//
//		if (setBackground) {
//			button.getStyle().set("background-color", "var(--lumo-contrast-10pct)");
//		}
		return button;
	}

	public static Button createButton(String text) {
		return createCustomButton(text, null);
	}

	public static Button createButton(String text, VaadinIcon icon) {
		return createCustomButton(text, icon);
	}

	public static Button createButton(String text, ButtonVariant... variants) {
		return createCustomButton(text, null, variants);
	}

	public static Button createButton(VaadinIcon icon, ButtonVariant... variants) {
		return createCustomButton("", icon, variants);
	}

	public static Button createButton(String text, VaadinIcon icon, ButtonVariant... variants) {
		return createCustomButton(text, icon, variants);
	}

	private static Button createCustomButton(String text, VaadinIcon icon, ButtonVariant... variants) {
		Button button = new Button();

		if (text.length() > 0) {
			button.setText(text);
		}

		if (icon != null) {
			Icon i = new Icon(icon);
			i.getElement().setAttribute("slot", "prefix");
			button.setIcon(i);
		}

		if (variants.length > 0) {
			button.addThemeVariants(variants);
		}

		button.getStyle().set("cursor", "pointer");

//		boolean setBackground = true;
//
//		for (ButtonVariant bv : variants) {
//			if (bv.equals(ButtonVariant.LUMO_PRIMARY) || bv.equals(ButtonVariant.LUMO_TERTIARY)) {
//				setBackground = false;
//				break;
//			}
//		}
//
//		if (setBackground) {
//			button.getStyle().set("background-color", "var(--lumo-contrast-10pct)");
//		}
		return button;
	}



	/* ==== GRID ==== */

	public static Component createActiveGridIcon(boolean b) {
		return (b) ? UIUtils.createErrorIcon(VaadinIcon.CLOSE) : UIUtils.createPrimaryIcon(VaadinIcon.CHECK);
	}



	/* ==== LABELS ==== */

	public static Label createH1Label(String text) {
		Label label = new Label(text);
		label.addClassName(LumoStyles.Heading.H1);
		return label;
	}

	public static Label createH2Label(String text) {
		Label label = new Label(text);
		label.addClassName(LumoStyles.Heading.H2);
		return label;
	}

	public static Label createH3Label(String text) {
		Label label = new Label(text);
		label.addClassName(LumoStyles.Heading.H3);
		return label;
	}

	public static Label createH4Label(String text) {
		Label label = new Label(text);
		label.addClassName(LumoStyles.Heading.H4);
		return label;
	}

	public static Label createH5Label(String text) {
		Label label = new Label(text);
		label.addClassName(LumoStyles.Heading.H5);
		return label;
	}

	public static Label createH6Label(String text) {
		Label label = new Label(text);
		label.addClassName(LumoStyles.Heading.H6);
		return label;
	}


	/* === MISC === */


	public static Component createInitials(String initials) {
		FlexBoxLayout layout = new FlexBoxLayout(new Text(initials.toUpperCase()));
		layout.addClassName("initials");
		return layout;
	}



	/* === ICONS === */

	private static Icon createPrimaryIcon(VaadinIcon icon) {
		Icon i = new Icon(icon);
		setTextColor(TextColor.PRIMARY, i);
		return i;
	}

	private static Icon createErrorIcon(VaadinIcon icon) {
		Icon i = new Icon(icon);
		setTextColor(TextColor.ERROR, i);
		return i;
	}


	/* === NOTIFICATIONS === */

	public static void showNotification(String msg, NotificationVariant themeVariant, int... duration) {
		Notification notification = constructNotification(msg, themeVariant, duration);
		notification.open();
	}

	public static Notification constructNotification(String msg, NotificationVariant themeVariant, int... duration) {
		Notification notification = new Notification();
		notification.setPosition(Notification.Position.TOP_CENTER);
		notification.addThemeVariants(themeVariant);

		Div notificationContent = new Div();
		notificationContent.addClassName("notification-content");
		notificationContent.addClickListener(e -> notification.close());
		notificationContent.add(new Span(msg));

		notification.add(notificationContent);

		if (duration.length > 0) {
			notification.setDuration(duration[0]);
		} else {
			if (themeVariant.equals(NotificationVariant.LUMO_ERROR)) {
				notification.setDuration(0);
			} else {
				notification.setDuration(5000);
			}
		}
		return notification;
	}


	/* === CSS UTILITIES === */
	public static void setOverflow(Overflow overflow, Component... components) {
		for (Component component : components) {
			component.getElement().getStyle().set("overflow", overflow.getValue());
		}
	}

	private static void setTextColor(TextColor textColor, Component... components) {
		for (Component component : components) {
			component.getElement().getStyle().set("color", textColor.getValue());
		}
	}

	public static void setTooltip(String tooltip, Component... components) {
		for (Component component : components) {
			component.getElement().setProperty("title", tooltip);
		}
	}


	/* === ACCESSIBILITY === */

	public static void setAriaLabel(String value, Component... components) {
		for (Component component : components) {
			component.getElement().setAttribute("aria-label", value);
		}
	}
}