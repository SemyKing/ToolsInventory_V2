package com.gmail.grigorij.ui.components.navigation.bar;

import com.gmail.grigorij.backend.database.entities.Transaction;
import com.gmail.grigorij.backend.database.entities.User;
import com.gmail.grigorij.backend.database.enums.operations.Operation;
import com.gmail.grigorij.backend.database.enums.operations.OperationTarget;
import com.gmail.grigorij.backend.database.enums.permissions.PermissionLevel;
import com.gmail.grigorij.backend.database.enums.permissions.PermissionRange;
import com.gmail.grigorij.backend.database.facades.PermissionFacade;
import com.gmail.grigorij.backend.database.facades.TransactionFacade;
import com.gmail.grigorij.backend.database.facades.UserFacade;
import com.gmail.grigorij.ui.components.FlexBoxLayout;
import com.gmail.grigorij.ui.components.ListItem;
import com.gmail.grigorij.ui.components.dialogs.CustomDialog;
import com.gmail.grigorij.ui.components.forms.UserForm;
import com.gmail.grigorij.ui.components.navigation.bar.tab.NaviTab;
import com.gmail.grigorij.ui.components.navigation.bar.tab.NaviTabs;
import com.gmail.grigorij.ui.utils.UIUtils;
import com.gmail.grigorij.ui.utils.css.LumoStyles;
import com.gmail.grigorij.ui.views.ApplicationContainerView;
import com.gmail.grigorij.utils.AuthenticationService;
import com.gmail.grigorij.utils.ProjectConstants;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.tabs.TabsVariant;

import java.util.ArrayList;


@CssImport("./styles/components/app-bar/app-bar.css")
@CssImport(value = "./styles/components/app-bar/navi-icon.css", themeFor = "vaadin-button")
public class AppBar extends Composite<FlexLayout> {

    private final String CLASS_NAME = "app-bar";
    private final ApplicationContainerView menuLayout;

    private Button menuIcon;
    private H4 title;
    private MenuBar profileMenuBar;
    private NaviTabs tabs;

    private ArrayList<Tab> tabsList = new ArrayList<>();


    public AppBar(ApplicationContainerView menuLayout, String title, NaviTab... tabs) {
        this.menuLayout = menuLayout;

        getContent().setClassName(CLASS_NAME);

        initMenuIcon();
        initTitle(title);
        initProfileMenu();
        initContainer();
        initTabs(tabs);
    }

    /**
     * 'NaviDrawer' button visible only on small views -> open / close NaviDrawer
     */
    private void initMenuIcon() {
        menuIcon = UIUtils.createButton(VaadinIcon.MENU, ButtonVariant.LUMO_TERTIARY_INLINE);
        menuIcon.removeThemeVariants(ButtonVariant.LUMO_ICON);
        menuIcon.addClassName(CLASS_NAME + "__navi-icon");
        menuIcon.addClickListener(e -> menuLayout.getNaviDrawer().toggle());

        UIUtils.setAriaLabel("Menu", menuIcon);
    }

    private void initTitle(String title) {
        this.title = new H4(title);
        this.title.setClassName(CLASS_NAME + "__title");
    }

    private void initProfileMenu() {
        User currentUser = AuthenticationService.getCurrentSessionUser();

        ListItem userItem = new ListItem(currentUser.getFullName(), currentUser.getCompany().getName());
        userItem.addClassName(CLASS_NAME + "__user-item");

        ListItem wrapperItem = new ListItem(userItem, UIUtils.createInitials(currentUser.getInitials()));
        wrapperItem.addClassName(CLASS_NAME + "__wrapper-item");


        profileMenuBar = new MenuBar();
        profileMenuBar.addThemeVariants(MenuBarVariant.LUMO_TERTIARY);

        MenuItem menuItem = profileMenuBar.addItem(wrapperItem);
        menuItem.getSubMenu().addItem("Profile", e -> constructUserProfileDialog());
        menuItem.getSubMenu().add(new Hr());
        menuItem.getSubMenu().addItem("Change Theme", e -> changeTheme());
        menuItem.getSubMenu().add(new Hr());
        menuItem.getSubMenu().addItem("Sign Out", e -> AuthenticationService.signOut());
    }

    private void initContainer() {
        FlexBoxLayout container = new FlexBoxLayout(menuIcon, title, profileMenuBar);
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


    private void constructUserProfileDialog() {
        if (!AuthenticationService.getCurrentSessionUser().getPermissionLevel().equalsTo(PermissionLevel.SYSTEM_ADMIN)) {
            if (!PermissionFacade.getInstance().isUserAllowedTo(Operation.VIEW, OperationTarget.USER, PermissionRange.OWN)) {
                UIUtils.showNotification(ProjectConstants.ACTION_NOT_ALLOWED, NotificationVariant.LUMO_PRIMARY);
                return;
            }
        }

        CustomDialog dialog = new CustomDialog();
        dialog.setHeader(UIUtils.createH3Label("Profile"));

        UserForm userForm = new UserForm();
        userForm.setUser(AuthenticationService.getCurrentSessionUser());

        dialog.setContent(userForm);
        dialog.closeOnCancel();

        dialog.getConfirmButton().setText("Save");
        dialog.getConfirmButton().setEnabled(false);

        if (AuthenticationService.getCurrentSessionUser().getPermissionLevel().equalsTo(PermissionLevel.SYSTEM_ADMIN) ||
                PermissionFacade.getInstance().isUserAllowedTo(Operation.EDIT, OperationTarget.USER, PermissionRange.OWN)) {

            dialog.getConfirmButton().setEnabled(true);
            dialog.getConfirmButton().addClickListener(e -> {
                User editedUser = userForm.getUser();

                if (editedUser != null) {
                    if (UserFacade.getInstance().update(editedUser)) {
                        UIUtils.showNotification("Information saved", NotificationVariant.LUMO_SUCCESS);
                    } else {
                        UIUtils.showNotification("Information update failed", NotificationVariant.LUMO_ERROR);
                    }
                    dialog.close();

                    Transaction transaction = new Transaction();
                    transaction.setUser(AuthenticationService.getCurrentSessionUser());
                    transaction.setCompany(AuthenticationService.getCurrentSessionUser().getCompany());
                    transaction.setOperation(Operation.EDIT);
                    transaction.setOperationTarget1(OperationTarget.USER);
                    transaction.setTargetDetails(editedUser.getFullName());
                    transaction.setChanges(userForm.getChanges());
                    TransactionFacade.getInstance().insert(transaction);
                }
            });
        }

        dialog.open();
    }

    private void changeTheme() {
        String themeVariant = AuthenticationService.getCurrentSessionUser().getThemeVariant();

        themeVariant = (themeVariant.equals(LumoStyles.DARK)) ? LumoStyles.LIGHT : LumoStyles.DARK;
        menuLayout.setThemeVariant(themeVariant);

        User user = AuthenticationService.getCurrentSessionUser();
        user.setThemeVariant(themeVariant);
        UserFacade.getInstance().update(user);
    }


    /* === TITLE === */

    public void setTitle(String title) {
        this.title.setText(title);
    }



    /* === TABS === */

    public void setTabsVariant(TabsVariant tabsVariant) {
        this.tabs.addThemeVariants(tabsVariant);
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

    public Tab getSelectedTab() {
        return tabs.getSelectedTab();
    }

    public void setSelectedTab(Tab selectedTab) {
        tabs.setSelectedTab(selectedTab);
    }

    public void addTabSelectionListener(ComponentEventListener<Tabs.SelectedChangeEvent> listener) {
        tabs.addSelectedChangeListener(listener);
    }

    public ArrayList<Tab> getTabs() {
        return tabsList;
    }

    private void removeAllTabs() {
        tabs.removeAll();
        updateTabsVisibility();
    }

    public void reset() {
        removeAllTabs();

        tabsList.clear();
    }

    private void updateTabsVisibility() {
        tabs.setVisible(tabs.getComponentCount() > 0);
    }
}