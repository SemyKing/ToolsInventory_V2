package com.gmail.grigorij.ui.components.detailsdrawer;

import com.gmail.grigorij.ui.utils.css.size.Size;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.gmail.grigorij.ui.components.FlexBoxLayout;
import com.gmail.grigorij.ui.utils.css.FlexDirection;

@StyleSheet("styles/components/details-drawer.css")
public class DetailsDrawer extends Composite<FlexLayout> implements HasStyle, HasSize {

    private static final String CLASS_NAME = "details-drawer";

    private final FlexBoxLayout header;
    private final FlexBoxLayout content;
    private final FlexBoxLayout footer;

    public enum Position {
        RIGHT, BOTTOM
    }

    public DetailsDrawer(Position position, Component... components) {
        addClassName(CLASS_NAME);

        header = new FlexBoxLayout();
        header.addClassName(CLASS_NAME + "__header");

        content = new FlexBoxLayout();
        content.addClassName(CLASS_NAME + "__content");
        content.setFlexDirection(FlexDirection.COLUMN);

        footer = new FlexBoxLayout();
        footer.addClassName(CLASS_NAME + "__footer");

        getContent().add(header, content, footer);

        setPosition(position);
    }

    public void setHeader(Component... components) {
        this.header.removeAll();
        this.header.add(components);
    }

    public FlexBoxLayout getHeader() {
        return this.header;
    }

    public void setContentPadding(Size...sizes) {
        if (this.content != null) {
            for (Size size : sizes) {
                for (String attribute : size.getPaddingAttributes()) {
                    content.getElement().getStyle().set(attribute, size.getVariable());
                }
            }
        }
    }

    public void setContent(Component... components) {
        this.content.removeAll();
        this.content.add(components);
    }

    public void setFooter(Component... components) {
        this.footer.removeAll();
        this.footer.add(components);
    }

    public void setPosition(Position position) {
        getElement().setAttribute("position", position.name().toLowerCase());
    }

    private boolean open = false;

    public void hide() {
        open = false;
        getElement().setAttribute("open", open);
    }

    public void show() {
        open = true;
        getElement().setAttribute("open", open);
    }

    public boolean isOpen() {
        return open;
    }
}