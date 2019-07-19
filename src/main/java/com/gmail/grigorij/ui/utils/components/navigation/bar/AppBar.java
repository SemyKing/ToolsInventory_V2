package com.gmail.grigorij.ui.utils.components.navigation.bar;

import com.gmail.grigorij.backend.database.facades.UserFacade;
import com.gmail.grigorij.backend.entities.user.User;
import com.gmail.grigorij.ui.utils.css.size.Bottom;
import com.gmail.grigorij.ui.utils.css.size.Top;
import com.gmail.grigorij.ui.utils.css.size.Vertical;
import com.gmail.grigorij.ui.views.MenuLayout;
import com.gmail.grigorij.ui.utils.UIUtils;
import com.gmail.grigorij.ui.utils.components.CustomDialog;
import com.gmail.grigorij.ui.utils.components.Divider;
import com.gmail.grigorij.ui.utils.components.FlexBoxLayout;
import com.gmail.grigorij.ui.utils.components.ListItem;
import com.gmail.grigorij.ui.utils.components.navigation.bar.tab.NaviTab;
import com.gmail.grigorij.ui.utils.components.navigation.bar.tab.NaviTabs;
import com.gmail.grigorij.ui.utils.css.FlexDirection;
import com.gmail.grigorij.ui.utils.css.LumoStyles;
import com.gmail.grigorij.ui.utils.forms.UserForm;
import com.gmail.grigorij.ui.views.authentication.AuthenticationService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.tabs.TabsVariant;
import com.vaadin.flow.theme.lumo.Lumo;

import java.util.ArrayList;

public class AppBar extends Composite<FlexLayout> {

    private String CLASS_NAME = "app-bar";

    private FlexBoxLayout container;

    private Button menuIcon;
    private Button contextIcon;

    private H4 title;
    private FlexBoxLayout actionItems;
    private FlexBoxLayout userInfo;

    private FlexBoxLayout tabContainer;
    private NaviTabs tabs;

    private UserForm userForm;


    public enum NaviMode {
        MENU, CONTEXTUAL
    }

    private ArrayList<Tab> tabsList = new ArrayList<>();

    private final MenuLayout menuLayout;

    public AppBar(MenuLayout menuLayout, String title, NaviTab... tabs) {
        this.menuLayout = menuLayout;

        getContent().setClassName(CLASS_NAME);

        userForm = new UserForm();

        initMenuIcon();
        initContextIcon();
        initTitle(title);
        initUserInfo();
        initActionItems();
        initContainer();
        initTabs(tabs);
    }

    public void setNaviMode(NaviMode mode) {
        if (mode.equals(NaviMode.MENU)) {
            menuIcon.setVisible(true);
            contextIcon.setVisible(false);
        } else {
            menuIcon.setVisible(false);
            contextIcon.setVisible(true);
        }
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


    private void initContextIcon() {
        contextIcon = UIUtils.createTertiaryInlineButton(VaadinIcon.ARROW_LEFT);
        contextIcon.removeThemeVariants(ButtonVariant.LUMO_ICON);
        contextIcon.addClassNames(CLASS_NAME + "__context-icon");
        contextIcon.setVisible(false);
        UIUtils.setAriaLabel("Back", contextIcon);
    }

    private void initTitle(String title) {
        this.title = new H4(title);
        this.title.setClassName(CLASS_NAME + "__title");
    }

    private void initUserInfo() {
        userInfo = new FlexBoxLayout();
        userInfo.addClassNames(CLASS_NAME + "__user-info");
        userInfo.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        userInfo.setFlexDirection(FlexDirection.COLUMN);

        User user = AuthenticationService.getCurrentSessionUser();

        ListItem item = new ListItem(user.getUsername(), user.getCompany().getName(), UIUtils.createInitials(user.getInitials()));
        item.setClassName("user-info-list-item");
        item.getContent().setClassName("user-list-item__content");
        item.getSuffix().setClassName("user-list-item__suffix");

        item.setHorizontalPadding(false);
        userInfo.add(item);


        ContextMenu contextMenu = new ContextMenu(userInfo);
        contextMenu.setOpenOnClick(true);

        contextMenu.add(new Divider(Bottom.XS));
        contextMenu.addItem(UIUtils.createTextIcon(VaadinIcon.USER, UIUtils.createText("Profile")), e -> openUserInformationDialog());
        contextMenu.add(new Divider(Vertical.XS));
        contextMenu.addItem(UIUtils.createTextIcon(VaadinIcon.MOON, UIUtils.createText("Change theme")), e -> {

            String themeVariant = AuthenticationService.getCurrentSessionUser().getThemeVariant();
            if (themeVariant.equals(Lumo.DARK)) {
                themeVariant = Lumo.LIGHT;
            } else {
                themeVariant = Lumo.DARK;
            }
            menuLayout.setThemeVariant(themeVariant);
            AuthenticationService.getCurrentSessionUser().setThemeVariant(themeVariant);
            UserFacade.getInstance().update(AuthenticationService.getCurrentSessionUser());
        });
        contextMenu.add(new Divider(Vertical.XS));
        contextMenu.addItem(UIUtils.createTextIcon(VaadinIcon.EXIT_O, UIUtils.createBoldText("Log Out")), e -> AuthenticationService.signOut());
        contextMenu.add(new Divider(Top.XS));
    }

    private void openUserInformationDialog() {
        CustomDialog dialog = new CustomDialog();
        dialog.setHeader(UIUtils.createH4Label("User Information"));

        userForm.setUser(AuthenticationService.getCurrentSessionUser());
        dialog.setContent( userForm );

        dialog.getCancelButton().addClickListener(e -> dialog.close());

        dialog.setConfirmButton(UIUtils.createButton("Save", ButtonVariant.LUMO_SUCCESS, ButtonVariant.LUMO_TERTIARY));
        dialog.getConfirmButton().addClickListener(e -> {
            User editedCurrentUser = userForm.getUser();

            if (editedCurrentUser != null) {
                AuthenticationService.setCurrentSessionUser(editedCurrentUser);

                if (UserFacade.getInstance().update(editedCurrentUser)) {
                    UIUtils.showNotification("Information updated successfully", UIUtils.NotificationType.SUCCESS);
                } else {
                    UIUtils.showNotification("Information update failed", UIUtils.NotificationType.ERROR);
                }
                dialog.close();
            }
        });


        dialog.open();
    }

    private void initActionItems() {
        actionItems = new FlexBoxLayout();
        actionItems.addClassName(CLASS_NAME + "__action-items");
        actionItems.setVisible(false);
    }

    private void initContainer() {
        container = new FlexBoxLayout(menuIcon, contextIcon, this.title, actionItems, userInfo);
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

        tabContainer = new FlexBoxLayout(this.tabs);
        tabContainer.addClassName(CLASS_NAME + "__tab-container");
        tabContainer.setAlignItems(FlexComponent.Alignment.CENTER);
        getContent().add(tabContainer);
    }

    public void setTabsVariant(TabsVariant tabsVariant) {
        this.tabs.addThemeVariants(tabsVariant);
    }


    /* === MENU ICON === */

    public Button getMenuIcon() {
        return menuIcon;
    }


    /* === CONTEXT ICON === */

    public Button getContextIcon() {
        return contextIcon;
    }

    public void setContextIcon(Icon icon) {
        contextIcon.setIcon(icon);
        contextIcon.removeThemeVariants(ButtonVariant.LUMO_ICON);
    }


    /* === TITLE === */

    public String getTitle() {
        return this.title.getText();
    }

    public void setTitle(String title) {
        this.title.setText(title);
    }


    /* === ACTION ITEMS === */

    public Component addActionItem(Component component) {
        actionItems.add(component);
        updateActionItemsVisibility();
        return component;
    }

//    public Button addActionItem(VaadinIcon icon) {
//        Button button = UIUtils.createButton(icon, ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_TERTIARY);
//        addActionItem(button);
//        return button;
//    }

    public void removeAllActionItems() {
        actionItems.removeAll();
        updateActionItemsVisibility();
    }


    /* === TABS === */

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
        setNaviMode(AppBar.NaviMode.MENU);
        removeAllActionItems();
        removeAllTabs();

        tabsList.clear();
    }


    /* === UPDATE VISIBILITY === */

    private void updateActionItemsVisibility() {
        actionItems.setVisible(actionItems.getComponentCount() > 0);
    }

    private void updateTabsVisibility() {
        tabs.setVisible(tabs.getComponentCount() > 0);
    }
}