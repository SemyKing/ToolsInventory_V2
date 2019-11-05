package com.gmail.grigorij.ui.components.layouts;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.gmail.grigorij.ui.utils.css.size.Size;
import com.gmail.grigorij.ui.utils.css.BorderRadius;
import com.gmail.grigorij.ui.utils.css.BoxSizing;
import com.gmail.grigorij.ui.utils.css.Display;
import com.gmail.grigorij.ui.utils.css.FlexDirection;
import com.gmail.grigorij.ui.utils.css.Overflow;

public class FlexBoxLayout extends FlexLayout {

    private static final String BACKGROUND_COLOR = "background-color";
    private static final String COLOR = "color";
    private static final String BORDER_RADIUS = "border-radius";
    private static final String BOX_SIZING = "box-sizing";
    private static final String DISPLAY = "display";
    private static final String FLEX_DIRECTION = "flex-direction";
    private static final String MAX_WIDTH = "max-width";
    private static final String OVERFLOW = "overflow";

    public FlexBoxLayout(Component... components) {
        super(components);
    }

    public void setBackgroundColor(String value) {
        getStyle().set(BACKGROUND_COLOR, value);
    }

    public void setColor(String value) {
        getStyle().set(COLOR, value);
    }

    public void setBorderRadius(BorderRadius radius) {
        getStyle().set(BORDER_RADIUS, radius.getValue());
    }

    public void setBoxSizing(BoxSizing sizing) {
        getStyle().set(BOX_SIZING, sizing.getValue());
    }

    public void setDisplay(Display display) {
        getStyle().set(DISPLAY, display.getValue());
    }

    public void setFlexDirection(FlexDirection direction) {
        getStyle().set(FLEX_DIRECTION, direction.getValue());
    }

    public void setFlexGrow(String value, Component... components) {
        for (Component component : components) {
            component.getElement().getStyle().set("flex-grow", value);
        }
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

    public void setMaxWidth(String value) {
        getStyle().set(MAX_WIDTH, value);
    }

    public void setOverflow(Overflow overflow) {
        getStyle().set(OVERFLOW, overflow.getValue());
    }

    public void setPadding(Size... sizes) {
        for (Size size : sizes) {
            for (String attribute : size.getPaddingAttributes()) {
                getStyle().set(attribute, size.getVariable());
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

    public void setSpacing(Size... sizes) {
        for (Size size : sizes) {
            addClassName(size.getSpacingClassName());
        }
    }
}
