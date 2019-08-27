package com.gmail.grigorij.ui.components.navigation.drawer;

import com.gmail.grigorij.ui.utils.UIUtils;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.HighlightConditions;
import com.vaadin.flow.router.RouterLink;

import java.util.ArrayList;
import java.util.List;

@StyleSheet("styles/components/navi-item.css")
public class NaviItem extends Div {

    protected final String CLASS_NAME = "navi-item";
    private final String HIGHLIGHT = "highlight";

    private String text;
    private Div naviLink;
    public Button expandCollapse;

    private List<NaviItem> subItems;
    private boolean subItemsVisible = true;

    private boolean selected = false;
    private boolean expandCollapseAdded = false;

    private int level = 0;

    public NaviItem(String text) {
        this(null, text);
    }

    public NaviItem(VaadinIcon icon, String text) {
        setClassName(CLASS_NAME);

        this.text = text;
        subItems = new ArrayList<>();
        setLevel(0);

        naviLink = new Div(new Label(text));
        naviLink.setClassName(CLASS_NAME + "__link");

        if (icon != null) {
            naviLink.getElement().insertChild(0, new Icon(icon).getElement());
        }

        getElement().insertChild(0, naviLink.getElement());
    }

    void addSubItem(NaviItem item) {
        if (!expandCollapseAdded) {
            expandCollapse = UIUtils.createSmallButton("", VaadinIcon.CARET_UP, ButtonVariant.LUMO_TERTIARY);
            expandCollapse.getElement().getStyle().set("padding-right", "0");
            expandCollapse.addClickListener(event -> setSubItemsVisible(!subItemsVisible));

            naviLink.add(expandCollapse);
            expandCollapseAdded = true;
        }
        item.setLevel(getLevel() + 1);
        subItems.add(item);
    }

    private void setLevel(int level) {
        this.level = level;
        if (level > 0) {
            getElement().setAttribute("level", Integer.toString(level));
        }
    }

    private int getLevel() {
        return level;
    }

    public String getText() {
        return text;
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);

        // If true, we only update the icon. If false, we hide all the sub items.
        if (visible) {
            if (level == 0) {
                expandCollapse.setIcon(new Icon(VaadinIcon.CARET_DOWN));
            }
        } else {
            setSubItemsVisible(visible);
        }
    }

    private void setSubItemsVisible(boolean visible) {
        if (level == 0) {
            expandCollapse.setIcon(new Icon(visible ? VaadinIcon.CARET_UP : VaadinIcon.CARET_DOWN));
        }
        subItems.forEach(item -> item.setVisible(visible));
        subItemsVisible = visible;
        updateAriaLabel();
    }

    private void updateAriaLabel(){
        String action;
        if(subItemsVisible) {
            action = "Collapse " + text;
        } else {
            action = "Expand " + text;
        }

        if (expandCollapse != null) {
            expandCollapse.getElement().setAttribute("aria-label", action);
        }
    }

    public boolean hasSubItems() {
        return subItems.size() > 0;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;

        if (selected) {
            this.getElement().setAttribute(HIGHLIGHT, true);

            if (this.getElement().getChild(0) != null)
                this.getElement().getChild(0).setAttribute(HIGHLIGHT, true);
        } else {
            this.getElement().setAttribute(HIGHLIGHT, false);

            if (this.getElement().getChild(0) != null)
                this.getElement().getChild(0).setAttribute(HIGHLIGHT, false);
        }
    }
}
