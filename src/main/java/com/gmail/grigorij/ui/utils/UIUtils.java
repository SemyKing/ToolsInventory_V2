package com.gmail.grigorij.ui.utils;

import com.gmail.grigorij.ui.utils.components.FlexBoxLayout;
import com.gmail.grigorij.ui.utils.css.*;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
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

	public static final String IMG_PATH = "images/";

	public static final String COLUMN_WIDTH_XS = "80px";
	public static final String COLUMN_WIDTH_S = "120px";
	public static final String COLUMN_WIDTH_M = "160px";
	public static final String COLUMN_WIDTH_L = "200px";
	public static final String COLUMN_WIDTH_XL = "240px";

	private static final String BUTTON_CLASS = "custom_button";

	/**
	 * Thread-unsafe formatters.
	 */
	private static final ThreadLocal<DecimalFormat> decimalFormat = ThreadLocal
			.withInitial(() -> new DecimalFormat("#,00"));
	private static final ThreadLocal<DateTimeFormatter> dateFormat = ThreadLocal
			.withInitial(() -> DateTimeFormatter.ofPattern("dd.MM.YYYY"));


	/* ==== BUTTONS ==== */

	public static Button createPrimaryButton(String text) {
		return createCustomButton(text, null, ButtonVariant.LUMO_PRIMARY);
	}

	public static Button createPrimaryButton(VaadinIcon icon) {
		return createCustomButton("", icon, ButtonVariant.LUMO_PRIMARY);
	}

	public static Button createPrimaryButton(String text, VaadinIcon icon) {
		return createCustomButton(text, icon, ButtonVariant.LUMO_PRIMARY);
	}

	public static Button createTertiaryButton(String text) {
		return createCustomButton(text, null, ButtonVariant.LUMO_TERTIARY);
	}

	public static Button createTertiaryButton(VaadinIcon icon) {
		return createCustomButton("", icon, ButtonVariant.LUMO_TERTIARY);
	}

	public static Button createTertiaryButton(String text, VaadinIcon icon) {
		return createCustomButton(text, icon, ButtonVariant.LUMO_TERTIARY);
	}

	public static Button createTertiaryInlineButton(String text) {
		return createCustomButton(text,null,  ButtonVariant.LUMO_TERTIARY_INLINE);
	}

	public static Button createTertiaryInlineButton(VaadinIcon icon) {
		return createCustomButton("", icon, ButtonVariant.LUMO_TERTIARY_INLINE);
	}

	public static Button createTertiaryInlineButton(String text, VaadinIcon icon) {
		return createCustomButton(text, icon, ButtonVariant.LUMO_TERTIARY_INLINE);
	}

	public static Button createSuccessButton(String text) {
		return createCustomButton(text, null, ButtonVariant.LUMO_SUCCESS);
	}

	public static Button createSuccessButton(VaadinIcon icon) {
		return createCustomButton("", icon, ButtonVariant.LUMO_SUCCESS);
	}

	public static Button createSuccessButton(String text, VaadinIcon icon) {
		return createCustomButton(text, icon, ButtonVariant.LUMO_SUCCESS);
	}

	public static Button createSuccessPrimaryButton(String text) {
		return createCustomButton(text, null, ButtonVariant.LUMO_SUCCESS, ButtonVariant.LUMO_PRIMARY);
	}

	public static Button createSuccessPrimaryButton(VaadinIcon icon) {
		return createCustomButton("", icon, ButtonVariant.LUMO_SUCCESS, ButtonVariant.LUMO_PRIMARY);
	}

	public static Button createSuccessPrimaryButton(String text, VaadinIcon icon) {
		return createCustomButton(text, icon, ButtonVariant.LUMO_SUCCESS, ButtonVariant.LUMO_PRIMARY);
	}

	public static Button createErrorButton(String text) {
		return createCustomButton(text, null, ButtonVariant.LUMO_ERROR);
	}

	public static Button createErrorButton(VaadinIcon icon) {
		return createCustomButton("", icon, ButtonVariant.LUMO_ERROR);
	}

	public static Button createErrorButton(String text, VaadinIcon icon) {
		return createCustomButton(text, icon, ButtonVariant.LUMO_ERROR);
	}

	public static Button createErrorPrimaryButton(String text) {
		return createCustomButton(text, null, ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_PRIMARY);
	}

	public static Button createErrorPrimaryButton(VaadinIcon icon) {
		return createCustomButton("", icon, ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_PRIMARY);
	}

	public static Button createErrorPrimaryButton(String text, VaadinIcon icon) {
		return createCustomButton(text, icon, ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_PRIMARY);
	}

	public static Button createContrastButton(String text) {
		return createCustomButton(text, null, ButtonVariant.LUMO_CONTRAST);
	}

	public static Button createContrastButton(VaadinIcon icon) {
		return createCustomButton("", icon, ButtonVariant.LUMO_CONTRAST);
	}

	public static Button createContrastButton(String text, VaadinIcon icon) {
		return createCustomButton(text, icon, ButtonVariant.LUMO_CONTRAST);
	}

	public static Button createContrastPrimaryButton(String text) {
		return createCustomButton(text, null, ButtonVariant.LUMO_CONTRAST, ButtonVariant.LUMO_PRIMARY);
	}

	public static Button createContrastPrimaryButton(VaadinIcon icon) {
		return createCustomButton("", icon, ButtonVariant.LUMO_CONTRAST, ButtonVariant.LUMO_PRIMARY);
	}

	public static Button createContrastPrimaryButton(String text, VaadinIcon icon) {
		return createCustomButton(text, icon, ButtonVariant.LUMO_CONTRAST, ButtonVariant.LUMO_PRIMARY);
	}

	public static Button createSmallButton(String text) {
		return createCustomButton(text, null, ButtonVariant.LUMO_SMALL);
	}

	public static Button createSmallButton(VaadinIcon icon) {
		return createCustomButton("", icon, ButtonVariant.LUMO_SMALL);
	}

	public static Button createSmallButton(String text, VaadinIcon icon) {
		return createCustomButton(text, icon, ButtonVariant.LUMO_SMALL);
	}

	public static Button createLargeButton(String text) {
		return createCustomButton(text, null,  ButtonVariant.LUMO_LARGE);
	}

	public static Button createLargeButton(VaadinIcon icon) {
		return createCustomButton("", icon, ButtonVariant.LUMO_LARGE);
	}

	public static Button createLargeButton(String text, VaadinIcon icon) {
		return createCustomButton(text, icon, ButtonVariant.LUMO_LARGE);
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

			boolean backgroundSet = false;
			boolean colorSet = false;

			for (ButtonVariant variant : variants) {
				if (variant.equals(ButtonVariant.LUMO_ERROR) || variant.equals(ButtonVariant.LUMO_SUCCESS)) {
					colorSet = true;
				}
				if (variant.equals(ButtonVariant.LUMO_PRIMARY)) {
					backgroundSet = true;
					break;
				}
			}

			if (!backgroundSet) {
//				button.getStyle().set("background-color", "var(--lumo-shade-20pct)");

				if (!colorSet) {
					button.getStyle().set("color", "var(--lumo-body-text-color)");
				}
			}
		} else {
//			button.getStyle().set("background-color", "var(--lumo-shade-20pct)");
			button.getStyle().set("color", "var(--lumo-body-text-color)");
		}

		button.getStyle().set("cursor", "pointer");

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


	/* === MISC === */

	public static Span createBoldText(String text) {
		Span span = new Span(text);
		span.addClassName(LumoStyles.FontWeight.BOLD);
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

	public static Button createFloatingActionButton(VaadinIcon icon) {
		Button button = createPrimaryButton(icon);
		button.addThemeName("fab");
		return button;
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

	public static Label createUnitsLabel(int units) {
		Label label = new Label(formatUnits(units));
		label.addClassName(LumoStyles.FontFamily.MONOSPACE);
		return label;
	}


	/* === ICONS === */

	public static FlexBoxLayout createTextIcon(Component textComponent, VaadinIcon icon) {
		FlexBoxLayout layout = new FlexBoxLayout();
		layout.setFlexDirection(FlexDirection.ROW);
//		layout.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
		layout.setAlignItems(FlexComponent.Alignment.CENTER);
		layout.add(textComponent);

		Icon i = new Icon(icon);
		setTextColor(TextColor.TERTIARY, i);
		layout.add(i);

		return layout;
	}

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

	public static Icon createSecondaryIcon(VaadinIcon icon) {
		Icon i = new Icon(icon);
		setTextColor(TextColor.SECONDARY, i);
		return i;
	}

	public static Icon createTertiaryIcon(VaadinIcon icon) {
		Icon i = new Icon(icon);
		setTextColor(TextColor.TERTIARY, i);
		return i;
	}

	public static Icon createDisabledIcon(VaadinIcon icon) {
		Icon i = new Icon(icon);
		setTextColor(TextColor.DISABLED, i);
		return i;
	}

	public static Icon createSuccessIcon(VaadinIcon icon) {
		Icon i = new Icon(icon);
		setTextColor(TextColor.SUCCESS, i);
		return i;
	}

	public static Icon createErrorIcon(VaadinIcon icon) {
		Icon i = new Icon(icon);
		setTextColor(TextColor.ERROR, i);
		return i;
	}

	public static Icon createSmallIcon(VaadinIcon icon) {
		Icon i = new Icon(icon);
		i.addClassName(IconSize.S.getClassName());
		return i;
	}

	public static Icon createLargeIcon(VaadinIcon icon) {
		Icon i = new Icon(icon);
		i.addClassName(IconSize.L.getClassName());
		return i;
	}

	public static Icon createIcon(IconSize size, TextColor color, VaadinIcon icon) {
		Icon i = new Icon(icon);
		i.addClassNames(size.getClassName());
		setTextColor(color, i);
		return i;
	}


	/* === DATES === */

	public static String formatDate(LocalDate date) {
		return dateFormat.get().format(date);
	}


	/* === NOTIFICATIONS === */

	public enum NotificationType {
		INFO (      "#bbb41bf7",                    0),
		SUCCESS (   "var(--lumo-success-color)",    2000),
		WARNING(    "#ff6700",                      5000),
		ERROR (     "var(--lumo-error-color)",      0);

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

//        Label counter = new Label();

		Label msgLabel = new Label(msg);
		msgLabel.addClassName("notification-text-container");

		Button close = UIUtils.createButton(VaadinIcon.CLOSE);
		close.addClassName("notification-button");

		FlexBoxLayout layout = new FlexBoxLayout();
		layout.setSizeFull();
		layout.getStyle().set("align-items", "center");
		layout.getStyle().set("min-height", "32px");
		layout.add(msgLabel);
		layout.setBackgroundColor(type.getBackgroundColor());

		if (duration.length > 0) {
			notification.setDuration(duration[0]);
		}

		if (type.getDuration() <= 0) {
			layout.add(close);

			close.addClickListener(ev -> notification.close());
		} else {
			notification.setDuration(type.getDuration());
		}


		notification.add(layout);
		notification.open();
	}


	/* === CSS UTILITIES === */

	public static void setAlignSelf(AlignSelf alignSelf, Component... components) {
		for (Component component : components) {
			component.getElement().getStyle().set("align-self", alignSelf.getValue());
		}
	}

	public static void setBackgroundColor(String backgroundColor, Component... components) {
		for (Component component : components) {
			component.getElement().getStyle().set("background-color", backgroundColor);
		}
	}

	public static void setBorderRadius(BorderRadius borderRadius, Component... components) {
		for (Component component : components) {
			component.getElement().getStyle().set("border-radius", borderRadius.getValue());
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

	public static void setFontWeight(FontWeight fontWeight, Component... components) {
		for (Component component : components) {
			component.getElement().getStyle().set("font-weight", fontWeight.getValue());
		}
	}

	public static void setMaxWidth(String value, Component... components) {
		for (Component component : components) {
			component.getElement().getStyle().set("max-width", value);
		}
	}

	public static void setOverflow(Overflow overflow, Component... components) {
		for (Component component : components) {
			component.getElement().getStyle().set("overflow", overflow.getValue());
		}
	}

	public static void setShadow(Shadow shadow, Component... components) {
		for (Component component : components) {
			component.getElement().getStyle().set("box-shadow", shadow.getValue());
		}
	}

	public static void setTextAlign(TextAlign textAlign, Component... components) {
		for (Component component : components) {
			component.getElement().getStyle().set("text-align", textAlign.getValue());
		}
	}

	public static void setTextColor(TextColor textColor, Component... components) {
		for (Component component : components) {
			component.getElement().getStyle().set("color", textColor.getValue());
		}
	}

	public static void setTheme(String theme, Component... components) {
		for (Component component : components) {
			component.getElement().setAttribute("theme", theme);
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