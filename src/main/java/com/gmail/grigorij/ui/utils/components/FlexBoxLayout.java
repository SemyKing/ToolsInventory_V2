package com.gmail.grigorij.ui.utils.components;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.theme.lumo.Lumo;
import com.gmail.grigorij.ui.utils.css.size.Size;
import com.gmail.grigorij.ui.utils.css.BorderRadius;
import com.gmail.grigorij.ui.utils.css.BoxSizing;
import com.gmail.grigorij.ui.utils.css.Display;
import com.gmail.grigorij.ui.utils.css.FlexDirection;
import com.gmail.grigorij.ui.utils.css.FlexWrap;
import com.gmail.grigorij.ui.utils.css.Overflow;
import com.gmail.grigorij.ui.utils.css.Position;
import com.gmail.grigorij.ui.utils.css.Shadow;

public class FlexBoxLayout extends FlexLayout {

    public static final String BACKGROUND_COLOR = "background-color";
    public static final String COLOR = "color";
    public static final String BORDER_RADIUS = "border-radius";
    public static final String BOX_SHADOW = "box-shadow";
    public static final String BOX_SIZING = "box-sizing";
    public static final String DISPLAY = "display";
    public static final String FLEX_DIRECTION = "flex-direction";
    public static final String FLEX_WRAP = "flex-wrap";
    public static final String MAX_WIDTH = "max-width";
    public static final String OVERFLOW = "overflow";
    public static final String POSITION = "position";

    public FlexBoxLayout(Component... components) {
        super(components);
    }

    public void setBackgroundColor(String value) {
        getStyle().set(BACKGROUND_COLOR, value);
    }

    public void setColor(String value) {
        getStyle().set(COLOR, value);
    }

    public void removeBackgroundColor() {
        getStyle().remove(BACKGROUND_COLOR);
    }

    public void setBorderRadius(BorderRadius radius) {
        getStyle().set(BORDER_RADIUS, radius.getValue());
    }

    public void removeBorderRadius() {
        getStyle().remove(BORDER_RADIUS);
    }

    public void setBoxSizing(BoxSizing sizing) {
        getStyle().set(BOX_SIZING, sizing.getValue());
    }

    public void removeBoxSizing() {
        getStyle().remove(BOX_SIZING);
    }

    public void setDisplay(Display display) {
        getStyle().set(DISPLAY, display.getValue());
    }

    public void setComponentDisplay(Component component, Display display) {
        component.getElement().getStyle().set(DISPLAY, display.getValue());
    }

    public void removeDisplay() {
        getStyle().remove(DISPLAY);
    }

    public void setFlex(String value, Component... components) {
        for (Component component : components) {
            component.getElement().getStyle().set("flex", value);
        }
    }

    public void setFlexBasis(String value, Component... components) {
        for (Component component : components) {
            component.getElement().getStyle().set("flex-basis", value);
        }
    }

    public void setFlexDirection(FlexDirection direction) {
        getStyle().set(FLEX_DIRECTION, direction.getValue());
    }

    public void setComponentFlexDirection(Component component, FlexDirection direction) {
        component.getElement().getStyle().set(FLEX_DIRECTION, direction.getValue());
    }

    public void removeFlexDirection() {
        getStyle().remove(FLEX_DIRECTION);
    }

    public void setFlexGrowSelf(String value) {
        getElement().getStyle().set("flex-grow", value);
    }

    public void setFlexGrow(String value, Component... components) {
        for (Component component : components) {
            component.getElement().getStyle().set("flex-grow", value);
        }
    }

    public void setFlexShrink(String value, Component... components) {
        for (Component component : components) {
            component.getElement().getStyle().set("flex-shrink", value);
        }
    }

    public void setFlexWrap(FlexWrap wrap) {
        getStyle().set(FLEX_WRAP, wrap.getValue());
    }

    public void removeFlexWrap() {
        getStyle().remove(FLEX_WRAP);
    }

    public void setMargin(Size... sizes) {
        for (Size size : sizes) {
            for (String attribute : size.getMarginAttributes()) {
                getStyle().set(attribute, size.getVariable());
            }
        }
    }

    public void setComponentMargin(Component component, Size... sizes) {
        for (Size size : sizes) {
            for (String attribute : size.getMarginAttributes()) {
                component.getElement().getStyle().set(attribute, size.getVariable());
            }
        }
    }

    public void removeMargin() {
        getStyle().remove("margin");
        getStyle().remove("margin-bottom");
        getStyle().remove("margin-left");
        getStyle().remove("margin-right");
        getStyle().remove("margin-top");
    }

    public void setMaxWidth(String value) {
        getStyle().set(MAX_WIDTH, value);
    }

    public void removeMaxWidth() {
        getStyle().remove(MAX_WIDTH);
    }

    public void setOverflow(Overflow overflow) {
        getStyle().set(OVERFLOW, overflow.getValue());
    }

    public void removeOverflow() {
        getStyle().remove(OVERFLOW);
    }

    public void setPadding(Size... sizes) {
        for (Size size : sizes) {
            for (String attribute : size.getPaddingAttributes()) {
                getStyle().set(attribute, size.getVariable());
            }
        }
    }

    public void setComponentPadding(Component component, Size... sizes) {
        for (Size size : sizes) {
            for (String attribute : size.getPaddingAttributes()) {
                component.getElement().getStyle().set(attribute, size.getVariable());
            }
        }
    }

    public void removePadding() {
        getStyle().remove("padding");
        getStyle().remove("padding-bottom");
        getStyle().remove("padding-left");
        getStyle().remove("padding-right");
        getStyle().remove("padding-top");
    }

    public void setPosition(Position position) {
        getStyle().set(POSITION, position.getValue());
    }

    public void removePosition() {
        getStyle().remove(POSITION);
    }

    public void setShadow(Shadow shadow) {
        getStyle().set(BOX_SHADOW, shadow.getValue());
    }

    public void removeShadow() {
        getStyle().remove(BOX_SHADOW);
    }

    public void setSpacing(Size... sizes) {
        for (Size size : sizes) {
            addClassName(size.getSpacingClassName());
        }
    }
}
