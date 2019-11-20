package com.gmail.grigorij.ui.components;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;


@CssImport("./styles/components/list-item.css")
public class ListItem extends Div implements HasStyle {

    public enum Direction {
        ROW("row"),
        COLUMN("column");

        private String direction;

        Direction(String direction) {
            this.direction = direction;
        }

        public String getDirection() {
            return direction;
        }
    }

    private final String CLASS_NAME = "list-item";

    private Div content;

    public ListItem(String primary, String secondary, Direction direction) {
        setClassName(CLASS_NAME);

        Span primary1 = new Span(primary);
        primary1.addClassName(CLASS_NAME + "__primary");

        Span secondary1 = new Span(secondary);
        secondary1.addClassName(CLASS_NAME + "__secondary");

        content = new Div(primary1, secondary1);
        content.addClassName(CLASS_NAME + "__" + direction.getDirection() + "__content");

        add(content);
    }

    public ListItem(Component prefix, Component suffix, Direction direction) {
        setClassName(CLASS_NAME);

        Div prefix1 = new Div(prefix);
        prefix1.addClassName(CLASS_NAME + "__prefix");

        Div suffix1 = new Div(suffix);
        suffix1.addClassName(CLASS_NAME + "__suffix");

        content = new Div(prefix1, suffix1);
        content.addClassName(CLASS_NAME + "__" + direction.getDirection() + "__content");

        add(content);
    }
}
