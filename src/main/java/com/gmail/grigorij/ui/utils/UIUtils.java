package com.gmail.grigorij.ui.utils;

import com.gmail.grigorij.ui.utils.components.FlexBoxLayout;
import com.gmail.grigorij.ui.utils.css.*;
import com.gmail.grigorij.ui.utils.css.size.Left;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldVariant;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class UIUtils {

	public static final String COLUMN_WIDTH_XS = "80px";
	public static final String COLUMN_WIDTH_S = "120px";
	public static final String COLUMN_WIDTH_M = "160px";
	public static final String COLUMN_WIDTH_L = "200px";
	public static final String COLUMN_WIDTH_XL = "240px";
	public static final String COLUMN_WIDTH_XXL = "280px";

	public static final String CUSTOM_SMALL_BUTTON = "custom-vaadin-small-button";

	/**
	 * Thread-unsafe formatters.
	 */
	private static final ThreadLocal<DecimalFormat> decimalFormat = ThreadLocal
			.withInitial(() -> new DecimalFormat("#,00"));

	private static final ThreadLocal<DateTimeFormatter> dateFormat = ThreadLocal
			.withInitial(() -> DateTimeFormatter.ofPattern("dd.MM.YYYY"));


	/* ==== FORMS ==== */

	/**
	 * Apply correct column span to FormLayout
	 *
	 * If element is hidden before custom column span is set, automatic column span is 1.
	 */
	public static void updateFormSize(FormLayout formLayout) {
		if (formLayout != null) {
			if (formLayout.getElement() != null) {
				if (UI.getCurrent() != null) {
					UI.getCurrent().getPage().executeJs("$0.notifyResize()", formLayout.getElement());
				}
			}
		}
	}


	public static FlexBoxLayout getFormRowLayout(Component c1, Component c2, boolean evenWith) {
		if (c1 == null) {
			c1 = createEmptyInvisibleLabel();
		}
		if (c2 == null) {
			c2 = createEmptyInvisibleLabel();
		}

		FlexBoxLayout layout = new FlexBoxLayout();
		layout.setFlexDirection(FlexDirection.ROW);
		layout.setAlignItems(FlexComponent.Alignment.BASELINE);
		layout.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);

		layout.add(c1, c2);
		layout.setComponentMargin(c2, Left.M);

//		if (evenWith) {
//
//		}

		if (!evenWith) {
			layout.setFlexGrow("1", c1);
		}

		return layout;
	}


	/* ==== BUTTONS ==== */

	public static Button createPrimaryButton(String text) {
		return createCustomButton(text, null, ButtonVariant.LUMO_PRIMARY);
	}

	public static Button createTertiaryInlineButton(VaadinIcon icon) {
		return createCustomButton("", icon, ButtonVariant.LUMO_TERTIARY_INLINE);
	}

	public static Button createSmallButton(String text) {
		Button smallButton = createCustomButton(text, null, ButtonVariant.LUMO_SMALL);
		smallButton.addClassName(CUSTOM_SMALL_BUTTON);
		return smallButton;
	}

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

	public static Button createIconButton(String text, VaadinIcon icon, ButtonVariant... variants) {
		Button button = new Button();
		button.setText(text);
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


		boolean setBackground = true;
		for (ButtonVariant bv : variants) {
			if (bv.equals(ButtonVariant.LUMO_PRIMARY) || bv.equals(ButtonVariant.LUMO_TERTIARY)) {
				setBackground = false;
				break;
			}
		}

		if (setBackground) {
			button.getStyle().set("background-color", "var(--lumo-contrast-10pct)");
		}
		return button;
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


		boolean setBackground = true;
		for (ButtonVariant bv : variants) {
			if (bv.equals(ButtonVariant.LUMO_PRIMARY) || bv.equals(ButtonVariant.LUMO_TERTIARY)) {
				setBackground = false;
				break;
			}
		}

		if (setBackground) {
			button.getStyle().set("background-color", "var(--lumo-contrast-10pct)");
		}
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

	/*
	All 'create*Button' methods use this method
	 */
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

		boolean setBackground = true;

		for (ButtonVariant bv : variants) {
			if (bv.equals(ButtonVariant.LUMO_PRIMARY) || bv.equals(ButtonVariant.LUMO_TERTIARY)) {
				setBackground = false;
				break;
			}
		}

		if (setBackground) {
			button.getStyle().set("background-color", "var(--lumo-contrast-10pct)");
		}


		return button;
	}


	/* ==== GRID ==== */

	public static Component createActiveGridIcon(boolean b) {
		return (b) ? UIUtils.createErrorIcon(VaadinIcon.CLOSE) : UIUtils.createPrimaryIcon(VaadinIcon.CHECK);
	}


	/* ==== TEXT FIELDS ==== */

	public static TextField createSmallTextField() {
		TextField textField = new TextField();
		textField.addThemeVariants(TextFieldVariant.LUMO_SMALL);
		return textField;
	}


	/* ==== LABELS ==== */

	public static Label createLabel(FontSize size, TextColor color, String text) {
		Label label = new Label(text);
		setFontSize(size, label);
		setTextColor(color, label);
		return label;
	}

	public static Label createLabel(FontSize size, String text) {
		return createLabel(size, TextColor.BODY, text);
	}

	public static Label createLabel(TextColor color, String text) {
		return createLabel(FontSize.M, color, text);
	}

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

	private static Label createEmptyInvisibleLabel() {
		Label label = new Label("");
		label.setMaxHeight("0px");
		label.setMaxWidth("0px");
		label.getStyle().set("display", "none");
		return label;
	}


	/* === MISC === */

	public static Span createBoldText(String text) {
		Span span = new Span(text);
		span.getElement().getStyle().set("font-weight", "bold");
		return span;
	}

	public static Span createText(String text) {
		Span span = new Span(text);
		span.addClassName("text-component");
		return span;
	}

	public static Span createText(FontSize size, TextColor color, String text) {
		Span span = new Span(text);
		span.addClassName("text-component");
		setFontSize(size, span);
		setTextColor(color, span);
		return span;
	}

	public static Component createInitials(String initials) {
		FlexBoxLayout layout = new FlexBoxLayout(new Text(initials.toUpperCase()));
		setFontSize(FontSize.S, layout);
		layout.addClassName("initials");

		layout.setAlignItems(FlexComponent.Alignment.CENTER);
		layout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);

		layout.setHeight(LumoStyles.Size.M);
		layout.setWidth(LumoStyles.Size.M);
		return layout;
	}


	/* === NUMBERS === */

	public static String formatAmount(Double amount) {
		return decimalFormat.get().format(amount);
	}

	public static String formatAmount(int amount) {
		return decimalFormat.get().format(amount);
	}

	public static Label createAmountLabel(double amount) {
		Label label = createH5Label(formatAmount(amount));
		label.addClassName(LumoStyles.FontFamily.MONOSPACE);
		return label;
	}

	public static String formatUnits(int units) {
		return NumberFormat.getIntegerInstance().format(units);
	}



	/* === ICONS === */

	public static FlexBoxLayout createTextIcon(VaadinIcon icon, Component textComponent) {
		FlexBoxLayout layout = new FlexBoxLayout();
		layout.setFlexDirection(FlexDirection.ROW);
		layout.setClassName("icon-text-component");
		layout.setAlignItems(FlexComponent.Alignment.CENTER);

		Icon i = new Icon(icon);
		setTextColor(TextColor.TERTIARY, i);
		i.getStyle().set("padding-right", "15px");
		layout.add(i);

		layout.add(textComponent);
		return layout;
	}

	public static Icon createPrimaryIcon(VaadinIcon icon) {
		Icon i = new Icon(icon);
		setTextColor(TextColor.PRIMARY, i);
		return i;
	}

	public static Icon createErrorIcon(VaadinIcon icon) {
		Icon i = new Icon(icon);
		setTextColor(TextColor.ERROR, i);
		return i;
	}


	/* === NOTIFICATIONS === */

	public enum NotificationType {
		INFO (      LumoStyles.Color.Primary._100,5000),
		SUCCESS (   LumoStyles.Color.Success._100,5000),
		WARNING(    "hsl(22, 96%, 47%)",        5000), //ORANGE
		ERROR (     LumoStyles.Color.Error._100,  0);

		private String backgroundColor;
		private int duration;

		NotificationType(String backgroundColor, int duration) {
			this.backgroundColor = backgroundColor;
			this.duration = duration;
		}

		public String getBackgroundColor() {
			return backgroundColor;
		}

		public int getDuration() {
			return duration;
		}
	}

	public static void showNotification(String msg, NotificationType type, int... duration) {
		Notification notification = new Notification();
		notification.setPosition(Notification.Position.TOP_CENTER);

		Label msgLabel = new Label(msg);
		msgLabel.addClassName("notification-text-container");

		Button close = UIUtils.createIconButton(VaadinIcon.CLOSE, ButtonVariant.LUMO_CONTRAST);
		close.addClassName("notification-button");

		FlexBoxLayout layout = new FlexBoxLayout();
		layout.setSizeFull();
		layout.setAlignItems(FlexComponent.Alignment.CENTER);
		layout.setMinHeight("32px");
		layout.add(msgLabel);
		layout.setBackgroundColor(type.getBackgroundColor());
		layout.setColor("var(--lumo-primary-contrast-color)");

		layout.add(close);
		close.addClickListener(ev -> notification.close());

		if (duration.length > 0) {
			notification.setDuration(duration[0]);
		} else {
			notification.setDuration(type.getDuration());
		}

		notification.add(layout);
		notification.open();
	}


	/* === CSS UTILITIES === */

	public static void setBackgroundColor(String backgroundColor, Component... components) {
		for (Component component : components) {
			component.getElement().getStyle().set("background-color", backgroundColor);
		}
	}

	public static void setBoxSizing(BoxSizing boxSizing, Component... components) {
		for (Component component : components) {
			component.getElement().getStyle().set("box-sizing", boxSizing.getValue());
		}
	}

	public static void setColSpan(int span, Component... components) {
		for (Component component : components) {
			component.getElement().setAttribute("colspan", Integer.toString(span));
		}
	}

	public static void setFontSize(FontSize fontSize, Component... components) {
		for (Component component : components) {
			component.getElement().getStyle().set("font-size", fontSize.getValue());
		}
	}

	public static void setOverflow(Overflow overflow, Component... components) {
		for (Component component : components) {
			component.getElement().getStyle().set("overflow", overflow.getValue());
		}
	}

	public static void setTextColor(TextColor textColor, Component... components) {
		for (Component component : components) {
			component.getElement().getStyle().set("color", textColor.getValue());
		}
	}

	public static void setTooltip(String tooltip, Component... components) {
		for (Component component : components) {
			component.getElement().setProperty("title", tooltip);
		}
	}

	public static void setWhiteSpace(WhiteSpace whiteSpace, Component... components) {
		for (Component component : components) {
			component.getElement().setProperty("white-space", whiteSpace.getValue());
		}
	}

	public static void setWidth(String value, Component... components) {
		for (Component component : components) {
			component.getElement().getStyle().set("width", value);
		}
	}


	/* === ACCESSIBILITY === */

	public static void setAriaLabel(String value, Component... components) {
		for (Component component : components) {
			component.getElement().setAttribute("aria-label", value);
		}
	}
}