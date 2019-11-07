package com.gmail.grigorij.ui.components.navigation.drawer;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;

//@CssImport("./styles/components/navi-drawer/navi-menu.css")
public class NaviMenu extends Div {

    private static final String CLASS_NAME = "navi-menu";

    private List<NaviItem> naviItems = new ArrayList<>();

    public NaviMenu() {
        setClassName(CLASS_NAME);
    }

    public void addNaviItem(NaviItem item) {
        add(item);
        naviItems.add(item);
    }

    public void addNaviItem(NaviItem parent, NaviItem item) {
        parent.addSubItem(item);
        addNaviItem(item);
    }

//    public NaviItem addNaviItem(VaadinIcon icon, String text, Class<? extends Component> navigationTarget, boolean groupCollapseButton) {
//        NaviItem item = new NaviItem(icon, text, navigationTarget, groupCollapseButton);
//        addNaviItem(item);
//        return item;
//    }
//
//    public NaviItem addNaviItem(Image image, String text, Class<? extends Component> navigationTarget, boolean groupCollapseButton) {
//        NaviItem item = new NaviItem(image, text, navigationTarget, groupCollapseButton);
//        addNaviItem(item);
//        return item;
//    }
//
//    public NaviItem addNaviItem(NaviItem parent, String text, Class<? extends Component> navigationTarget, boolean groupCollapseButton) {
//        NaviItem item = new NaviItem(text, navigationTarget, groupCollapseButton);
//        addNaviItem(parent, item);
//        return item;
//    }

    public List<NaviItem> getNaviItems() {
        return new ArrayList<>(naviItems);
    }
}
