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
    protected final String HIGHLIGHT = "highlight";

    private String text;
    private Class<? extends Component> navigationTarget;

    public Button expandCollapse;

    private boolean subItemsVisible;
    private List<NaviItem> subItems;

    private boolean selected = false;


    private int level = 0;

    public NaviItem(VaadinIcon icon, String text, boolean groupCollapseButton) {
        this(text, null, groupCollapseButton);
        link.getElement().insertChild(0, new Icon(icon).getElement());
    }

    public NaviItem(VaadinIcon icon, String text, Class<? extends Component> navigationTarget, boolean groupCollapseButton) {
        this(text, navigationTarget, groupCollapseButton);
        link.getElement().insertChild(0, new Icon(icon).getElement());
    }

    public NaviItem(Image image, String text, Class<? extends Component> navigationTarget, boolean groupCollapseButton) {
        this(text, navigationTarget, groupCollapseButton);
        link.getElement().insertChild(0, image.getElement());
    }

    public NaviItem(String text, Class<? extends Component> navigationTarget, boolean groupCollapseButton) {
        setClassName(CLASS_NAME);

        this.text = text;
        this.navigationTarget = navigationTarget;

        subItems = new ArrayList<>();
        setLevel(0);

        if (groupCollapseButton) {
            expandCollapse = UIUtils.createButton(VaadinIcon.CARET_UP, ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_TERTIARY);
            expandCollapse.setVisible(false);
            expandCollapse.addClickListener(event -> setSubItemsVisible(!subItemsVisible));

            Div div = new Div(new Label(text));
            div.setClassName(CLASS_NAME + "__link");
            div.add(expandCollapse);

            subItemsVisible = true;
            updateAriaLabel();

            this.link = div;
        } else {
            if (navigationTarget != null) {
                RouterLink routerLink = new RouterLink(null, navigationTarget);
                routerLink.add(new Label(text));
                routerLink.setHighlightCondition(HighlightConditions.sameLocation());
                routerLink.setClassName(CLASS_NAME + "__link");
                this.link = routerLink;
            } else {
                Div div = new Div(new Label(text));
                div.setClassName(CLASS_NAME + "__link");
                this.link = div;
            }
        }

        getElement().insertChild(0, link.getElement());
    }

    public void addSubItem(NaviItem item) {
        if (!expandCollapse.isVisible()) {
            expandCollapse.setVisible(true);
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

    public int getLevel() {
        return level;
    }

    public String getText() {
        return text;
    }

    public Class<? extends Component> getNavigationTarget() {
        return navigationTarget;
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);

        // If true, we only update the icon. If false, we hide all the sub
        // items.
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


    private final Component link;

    public boolean isHighlighted(AfterNavigationEvent e) {
        return link instanceof RouterLink && ((RouterLink) link).getHighlightCondition().shouldHighlight((RouterLink) link, e);
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
