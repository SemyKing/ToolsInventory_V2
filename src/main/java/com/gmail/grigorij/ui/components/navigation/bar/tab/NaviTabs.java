package com.gmail.grigorij.ui.components.navigation.bar.tab;

import com.gmail.grigorij.ui.utils.UIUtils;
import com.gmail.grigorij.ui.utils.css.Overflow;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;

/**
 * NaviTabs supports tabs that can be closed, and that can navigate to a
 * specific target when clicked.
 */
public class NaviTabs extends Tabs {

    private ComponentEventListener<SelectedChangeEvent> listener = (ComponentEventListener<SelectedChangeEvent>) selectedChangeEvent -> navigateToSelectedTab();

    public NaviTabs() {
        getElement().setAttribute("overflow", "end");
        UIUtils.setOverflow(Overflow.HIDDEN, this);
        addSelectedChangeListener(listener);
    }

    /**
     * When adding the first tab, the selection change event is triggered. This
     * will cause the app to navigate to that tab's navigation target (if any).
     * This constructor allows you to add the tabs before the event listener is
     * set.
     */
    public NaviTabs(NaviTab... naviTabs) {
        this();
        add(naviTabs);
    }

    /**
     * Creates a regular tab without any click listeners.
     */
    public Tab addTab(String text) {
        Tab tab = new Tab(text);
        add(tab);
        return tab;
    }

    /**
     * Creates a tab that when clicked navigates to the specified target.
     */
    public Tab addTab(String text, Class<? extends Component> navigationTarget) {
        Tab tab = new NaviTab(text, navigationTarget);
        add(tab);
        return tab;
    }


    /**
     * Navigates to the selected tab's navigation target if available.
     */
    public void navigateToSelectedTab() {
        if (getSelectedTab() instanceof NaviTab) {
            try {
                UI.getCurrent().navigate( ((NaviTab) getSelectedTab()).getNavigationTarget() );
            } catch (Exception e) {
                // @todo this is a code flow by exception anti-pattern. Either handle the case without the exception, or
                // @todo    at least document meticulously why this can't be done any other way and what kind of exceptions are we catching
                // @todo    and when they can occur. this block consumes all exceptions, even backend-originated, and may result in exceptions disappearing mysteriously.

                System.err.println("NaviTabs -> navigateToSelectedTab() \n");
                e.printStackTrace();
            }
        }
    }

    /**
     * Updates the current tab's name and navigation target.
     */
    public void updateSelectedTab(String text, Class<? extends Component> navigationTarget) {
        Tab tab = getSelectedTab();
        tab.setLabel(text);

        if (tab instanceof NaviTab) {
            ((NaviTab) tab).setNavigationTarget(navigationTarget);
        }

        navigateToSelectedTab();
    }

    /**
     * Returns the number of tabs.
     */
    public int getTabCount() {
        return Math.toIntExact(getChildren().filter(component -> component instanceof Tab).count());
    }
}
