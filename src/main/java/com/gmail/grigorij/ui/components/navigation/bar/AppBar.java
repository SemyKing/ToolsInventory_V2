package com.gmail.grigorij.ui.components.navigation.bar;

import com.gmail.grigorij.backend.database.facades.UserFacade;
import com.gmail.grigorij.backend.entities.user.User;
import com.gmail.grigorij.ui.components.layouts.FlexBoxLayout;
import com.gmail.grigorij.ui.components.dialogs.CustomDialog;
import com.gmail.grigorij.ui.components.forms.editable.EditableUserForm;
import com.gmail.grigorij.ui.components.navigation.bar.tab.NaviTab;
import com.gmail.grigorij.ui.components.navigation.bar.tab.NaviTabs;
import com.gmail.grigorij.ui.utils.UIUtils;
import com.gmail.grigorij.ui.utils.css.LumoStyles;
import com.gmail.grigorij.ui.views.application.ApplicationContainerView;
import com.gmail.grigorij.utils.AuthenticationService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.tabs.TabsVariant;

import java.util.ArrayList;


//@StyleSheet("context://styles/app-bar.css")
@CssImport("./styles/components/app-bar/app-bar.css")
@CssImport(value = "./styles/components/app-bar/navi-icon.css", themeFor = "vaadin-button")

public class AppBar extends Composite<FlexLayout> {

    private String CLASS_NAME = "app-bar";

    private Button menuIcon;
    private H4 title;
    private MenuBar userInfoMenuBar;
    private NaviTabs tabs;

    private ArrayList<Tab> tabsList = new ArrayList<>();

    private final ApplicationContainerView menuLayout;


    public AppBar(ApplicationContainerView menuLayout, String title, NaviTab... tabs) {
        this.menuLayout = menuLayout;

        getContent().setClassName(CLASS_NAME);

        initMenuIcon();
        initTitle(title);
        initUserInfo();
        initContainer();
        initTabs(tabs);
    }

    /**
     * 'NaviDrawer' button visible only on small views -> open / close NaviDrawer
     */
    private void initMenuIcon() {
        menuIcon = UIUtils.createTertiaryInlineButton(VaadinIcon.MENU);
        menuIcon.removeThemeVariants(ButtonVariant.LUMO_ICON);
        menuIcon.addClassName(CLASS_NAME + "__navi-icon");
        menuIcon.addClickListener(e -> menuLayout.getNaviDrawer().toggle());

        UIUtils.setAriaLabel("Menu", menuIcon);
    }

    private void initTitle(String title) {
        this.title = new H4(title);
        this.title.setClassName(CLASS_NAME + "__title");
    }

    private void initUserInfo() {
        Div userInfoContainer = new Div();
        userInfoContainer.addClassName(CLASS_NAME + "__user_info_container");

        Div userInfo = new Div();
        userInfo.addClassName(CLASS_NAME + "__user_info");

        Span userFullName = new Span(AuthenticationService.getCurrentSessionUser().getFullName());
        userFullName.addClassName(CLASS_NAME + "__user_info_full_name");
        userInfo.add(userFullName);

        Span userCompanyName = new Span(AuthenticationService.getCurrentSessionUser().getCompany().getName());
        userCompanyName.addClassName(CLASS_NAME + "__user_info_company");
        userInfo.add(userCompanyName);


        userInfoContainer.add(userInfo);
        userInfoContainer.add(UIUtils.createInitials(AuthenticationService.getCurrentSessionUser().getInitials()));


        userInfoMenuBar = new MenuBar();
        userInfoMenuBar.addThemeVariants(MenuBarVariant.LUMO_TERTIARY);

        MenuItem userMenuItem = userInfoMenuBar.addItem(userInfoContainer);

        userMenuItem.getSubMenu().addItem("Profile", e -> constructUserProfileDialog());
        userMenuItem.getSubMenu().add(new Hr());
        userMenuItem.getSubMenu().addItem("Change Theme", e -> {
            String themeVariant = AuthenticationService.getCurrentSessionUser().getThemeVariant();

            themeVariant = (themeVariant.equals(LumoStyles.DARK)) ? LumoStyles.LIGHT : LumoStyles.DARK;
            menuLayout.setThemeVariant(themeVariant);

            AuthenticationService.getCurrentSessionUser().setThemeVariant(themeVariant);
            UserFacade.getInstance().update(AuthenticationService.getCurrentSessionUser());
        });
        userMenuItem.getSubMenu().add(new Hr());
        userMenuItem.getSubMenu().addItem("Sign Out", e -> {
            AuthenticationService.signOut();
        });
    }


    private void constructUserProfileDialog() {
        CustomDialog dialog = new CustomDialog();
        dialog.setHeader(UIUtils.createH3Label("Profile"));

        EditableUserForm form = new EditableUserForm();
        form.setTargetUser(AuthenticationService.getCurrentSessionUser());

        dialog.setContent( form );

        dialog.getCancelButton().addClickListener(e -> dialog.close());

        dialog.getConfirmButton().setText("Save");
        dialog.getConfirmButton().addClickListener(e -> {
            User editedCurrentUser = form.getTargetUser();

            if (editedCurrentUser != null) {
                if (UserFacade.getInstance().update(editedCurrentUser)) {
                    AuthenticationService.setCurrentSessionUser(editedCurrentUser);
                    UIUtils.showNotification("Information saved", UIUtils.NotificationType.SUCCESS);
                } else {
                    UIUtils.showNotification("Information update failed", UIUtils.NotificationType.ERROR);
                }
                dialog.close();
            }
        });


        dialog.open();
    }

    private void initContainer() {
        FlexBoxLayout container = new FlexBoxLayout(menuIcon, title, userInfoMenuBar);
        container.addClassName(CLASS_NAME + "__container");
        container.setAlignItems(FlexComponent.Alignment.CENTER);

        getContent().add(container);
    }

    private void initTabs(NaviTab... tabs) {
        this.tabs = tabs.length > 0 ? new NaviTabs(tabs) : new NaviTabs();
        this.tabs.setClassName(CLASS_NAME + "__tabs");
        this.tabs.setVisible(false);
        for (NaviTab tab : tabs) {
            configureTab(tab);
        }

        FlexBoxLayout tabContainer = new FlexBoxLayout(this.tabs);
        tabContainer.addClassName(CLASS_NAME + "__tab-container");
        tabContainer.setAlignItems(FlexComponent.Alignment.CENTER);
        getContent().add(tabContainer);
    }



    /* === MENU ICON === */

    public Button getMenuIcon() {
        return menuIcon;
    }



    /* === TITLE === */

    public String getTitle() {
        return this.title.getText();
    }

    public void setTitle(String title) {
        this.title.setText(title);
    }



    /* === TABS === */

    public void setTabsVariant(TabsVariant tabsVariant) {
        this.tabs.addThemeVariants(tabsVariant);
    }

    public void centerTabs() {
        tabs.addClassName(LumoStyles.Margin.Horizontal.AUTO);
    }

    private void configureTab(Tab tab) {
        tab.addClassName(CLASS_NAME + "__tab");
        updateTabsVisibility();
    }

    public Tab addTab(String text) {
        Tab tab = tabs.addTab(text);
        configureTab(tab);
        tabsList.add(tab);
        return tab;
    }

    public Tab addTab(String text, Class<? extends Component> navigationTarget) {
        Tab tab = tabs.addTab(text, navigationTarget);
        configureTab(tab);
        return tab;
    }

    public Tab getSelectedTab() {
        return tabs.getSelectedTab();
    }

    public void setSelectedTab(Tab selectedTab) {
        tabs.setSelectedTab(selectedTab);
    }

    public void updateSelectedTab(String text, Class<? extends Component> navigationTarget) {
        tabs.updateSelectedTab(text, navigationTarget);
    }

    public void navigateToSelectedTab() {
        tabs.navigateToSelectedTab();
    }

    public void addTabSelectionListener(ComponentEventListener<Tabs.SelectedChangeEvent> listener) {
        tabs.addSelectedChangeListener(listener);
    }

    public ArrayList<Tab> getTabs() {
        return tabsList;
    }

    public int getTabCount() {
        return tabs.getTabCount();
    }

    public void removeAllTabs() {
        tabs.removeAll();
        updateTabsVisibility();
    }


    /* === RESET === */

    public void reset() {
//        setNaviMode(AppBar.NaviMode.MENU);
//        removeAllActionItems();
        removeAllTabs();

        tabsList.clear();
    }


    /* === UPDATE VISIBILITY === */

//    private void updateActionItemsVisibility() {
//        actionItems.setVisible(actionItems.getComponentCount() > 0);
//    }

    private void updateTabsVisibility() {
        tabs.setVisible(tabs.getComponentCount() > 0);
    }
}