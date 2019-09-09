package com.gmail.grigorij.ui.navigation.bar;

import com.gmail.grigorij.backend.database.facades.UserFacade;
import com.gmail.grigorij.backend.entities.user.User;
import com.gmail.grigorij.ui.components.FlexBoxLayout;
import com.gmail.grigorij.ui.components.dialogs.CustomDialog;
import com.gmail.grigorij.ui.forms.editable.EditableUserForm;
import com.gmail.grigorij.ui.navigation.bar.tab.NaviTab;
import com.gmail.grigorij.ui.navigation.bar.tab.NaviTabs;
import com.gmail.grigorij.ui.utils.UIUtils;
import com.gmail.grigorij.ui.utils.css.LumoStyles;
import com.gmail.grigorij.ui.views.MenuLayout;
import com.gmail.grigorij.utils.AuthenticationService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.dependency.HtmlImport;
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

@StyleSheet("styles/components/app-bar.css")
@HtmlImport("styles/components/navi-icon.html")
@HtmlImport("styles/components/user-info.html")
public class AppBar extends Composite<FlexLayout> {

    private String CLASS_NAME = "app-bar";

    private Button menuIcon;
    private H4 title;
    private MenuBar userInfoMenuBar;
    private NaviTabs tabs;

    private ArrayList<Tab> tabsList = new ArrayList<>();

    private final MenuLayout menuLayout;

//    public enum NaviMode {
//        MENU, CONTEXTUAL
//    }

    public AppBar(MenuLayout menuLayout, String title, NaviTab... tabs) {
        this.menuLayout = menuLayout;

        getContent().setClassName(CLASS_NAME);

        initMenuIcon();
        initTitle(title);
        initUserInfo();
        initContainer();
        initTabs(tabs);
    }

//    private void setNaviMode(NaviMode mode) {
//        if (mode.equals(NaviMode.MENU)) {
//            menuIcon.setVisible(true);
//            contextIcon.setVisible(false);
//        } else {
//            menuIcon.setVisible(false);
//            contextIcon.setVisible(true);
//        }
//    }

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


        Div userImage = new Div();
        userImage.addClassName(CLASS_NAME + "__user_image");


        userInfoContainer.add(userInfo);
//        userInfoContainer.add(userImage);
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

//    private void initUserInfo() {
//        FlexBoxLayout userInfoLayout = new FlexBoxLayout();
//        userInfoLayout.addClassNames(CLASS_NAME + "__user-info");
//        userInfoLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
//        userInfoLayout.setFlexDirection(FlexDirection.COLUMN);
//
//        User user = AuthenticationService.getCurrentSessionUser();
//
//        ListItem item = new ListItem(user.getUsername(), user.getCompany().getName(), UIUtils.createInitials(user.getInitials()));
//        item.setClassName("user-info-list-item");
//        item.getContent().setClassName("user-list-item__content");
//        item.getSuffix().setClassName("user-list-item__suffix");
//        item.setHorizontalPadding(false);
//        userInfoLayout.add(item);
//
//
//        //POPUP VIEW
//        FlexBoxLayout popupWrapper = new FlexBoxLayout();
//        popupWrapper.setFlexDirection(FlexDirection.COLUMN);
//        popupWrapper.setDisplay(Display.FLEX);
//        popupWrapper.setPadding(Horizontal.S);
//        popupWrapper.setBackgroundColor("var(--lumo-base-color)");
//
//
//        Button profileButton = UIUtils.createIconButton("Profile", VaadinIcon.USER, ButtonVariant.LUMO_CONTRAST, ButtonVariant.LUMO_TERTIARY);
//        profileButton.addClassName("button-align-left");
//        profileButton.addClickListener(e -> {
//            userInfo.close();
//            constructUserProfileDialog();
//        });
//
//        popupWrapper.add(profileButton);
//        popupWrapper.setComponentMargin(profileButton, Vertical.NONE);
//
//        popupWrapper.add(new Divider(1, Vertical.XS));
//
//        Button changeThemeButton = UIUtils.createIconButton("Change Theme", VaadinIcon.MOON, ButtonVariant.LUMO_CONTRAST, ButtonVariant.LUMO_TERTIARY);
//        changeThemeButton.addClassName("button-align-left");
//        changeThemeButton.addClickListener(e -> {
//            userInfo.close();
//
//            String themeVariant = AuthenticationService.getCurrentSessionUser().getThemeVariant();
//
//            themeVariant = (themeVariant.equals(Lumo.DARK)) ? Lumo.LIGHT : Lumo.DARK;
//            menuLayout.setThemeVariant(themeVariant);
//
//            AuthenticationService.getCurrentSessionUser().setThemeVariant(themeVariant);
//            UserFacade.getInstance().update(AuthenticationService.getCurrentSessionUser());
//        });
//
//        popupWrapper.add(changeThemeButton);
//        popupWrapper.setComponentMargin(changeThemeButton, Vertical.NONE);
//
//        popupWrapper.add(new Divider(1, Vertical.XS));
//
//        Button logOutButton = UIUtils.createIconButton("Log Out", VaadinIcon.EXIT_O, ButtonVariant.LUMO_CONTRAST, ButtonVariant.LUMO_TERTIARY);
//        logOutButton.addClassName("button-align-left");
//        logOutButton.addClickListener(e -> {
//            AuthenticationService.signOut();
//        });
//
//        popupWrapper.add(logOutButton);
//        popupWrapper.setComponentMargin(logOutButton, Vertical.NONE);
//
//        userInfo = new PaperMenuButton(userInfoLayout, popupWrapper);
//        userInfo.setHorizontalAlignment(HorizontalAlignment.RIGHT);
//        userInfo.setVerticalAlignment(VerticalAlignment.TOP);
//        userInfo.setVerticalOffset(45);
//    }

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

//        container.setComponentPadding(userInfoContainer, Vertical.NONE);
//        container.setComponentPadding(userInfoContainer, Horizontal.NONE);

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