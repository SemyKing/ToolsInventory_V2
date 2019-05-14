package com.gmail.grigorij.ui;

import com.gmail.grigorij.ui.authentication.AccessGroups;
import com.gmail.grigorij.ui.authentication.CurrentSession;
import com.gmail.grigorij.ui.views.navigation.admin.Admin;
import com.gmail.grigorij.ui.views.navigation.dashboard.Dashboard;
import com.gmail.grigorij.ui.views.navigation.inventory.Inventory;
import com.gmail.grigorij.ui.views.navigation.messages.Messages;
import com.gmail.grigorij.ui.views.navigation.reporting.Reporting;
import com.gmail.grigorij.ui.views.navigation.transactions.Transactions;
import com.gmail.grigorij.utils.Constants;
import com.vaadin.flow.router.Route;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasElement;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dependency.HtmlImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.page.Viewport;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.RouterLayout;
import com.vaadin.flow.server.ErrorHandler;
import com.vaadin.flow.server.InitialPageSettings;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.server.PageConfigurator;
import com.vaadin.flow.server.VaadinSession;
import com.gmail.grigorij.ui.components.FlexBoxLayout;
import com.gmail.grigorij.ui.components.navigation.bar.AppBar;
import com.gmail.grigorij.ui.components.navigation.drawer.NaviDrawer;
import com.gmail.grigorij.ui.components.navigation.drawer.NaviItem;
import com.gmail.grigorij.ui.components.navigation.drawer.NaviMenu;
import com.gmail.grigorij.ui.util.LumoStyles;
import com.gmail.grigorij.ui.util.css.FlexDirection;
import com.gmail.grigorij.ui.util.css.Overflow;
import com.gmail.grigorij.ui.views.examples.Accounts;
import com.gmail.grigorij.ui.views.examples.Home;
import com.gmail.grigorij.ui.views.examples.Payments;
import com.gmail.grigorij.ui.views.examples.Statistics;
import com.gmail.grigorij.ui.views.examples.personnel.Accountants;
import com.gmail.grigorij.ui.views.examples.personnel.Managers;


@HtmlImport("frontend://styles/shared-styles.html")
@Viewport("width=device-width, minimum-scale=1.0, initial-scale=1.0, user-scalable=yes")
@PWA(name = "Tools Inventory v2.0", shortName = "Tools Inventory v2.0", iconPath = "images/logo-18.png", backgroundColor = "#233348", themeColor = "#233348")
@Route("")
public class MainLayout extends FlexBoxLayout implements RouterLayout, PageConfigurator, AfterNavigationObserver {

	private static final Logger log = LoggerFactory.getLogger(MainLayout.class);
	private static final String CLASS_NAME = "root";

	private Div appHeaderOuter;

	private FlexBoxLayout row;
	private NaviDrawer naviDrawer;
	private FlexBoxLayout column;

	private Div appHeaderInner;
	private FlexBoxLayout viewContainer;
	private Div appFooterInner;

	private Div appFooterOuter;

	private AppBar appBar;

	public MainLayout() {
		setId("main-layout");

		VaadinSession.getCurrent().setErrorHandler((ErrorHandler) errorEvent -> {
			log.error("Uncaught UI exception", errorEvent.getThrowable());
			Notification.show("We are sorry, but an internal error occurred");
		});

		if (CurrentSession.getUser() == null) {
			UI.getCurrent().navigate(Constants.LOGIN_ROUTE);
			return;
		}

		addClassName(CLASS_NAME);
		setBackgroundColor(LumoStyles.Color.Contrast._5);
		setFlexDirection(FlexDirection.COLUMN);
		setSizeFull();

		// Initialise the UI building blocks
		initStructure();

		// Populate the navigation drawer
		initNaviItems();

		// Configure the headers and footers (optional)
		initHeadersAndFooters();
	}

	/**
	 * Initialise the required components and containers.
	 */
	private void initStructure() {
		naviDrawer = new NaviDrawer();

		viewContainer = new FlexBoxLayout();
		viewContainer.addClassName(CLASS_NAME + "__view-container");
		viewContainer.setOverflow(Overflow.HIDDEN);

		column = new FlexBoxLayout(viewContainer);
		column.addClassName(CLASS_NAME + "__column");
		column.setFlexDirection(FlexDirection.COLUMN);
		column.setFlexGrow(1, viewContainer);
		column.setOverflow(Overflow.HIDDEN);

		row = new FlexBoxLayout(naviDrawer, column);
		row.addClassName(CLASS_NAME + "__row");
		row.setFlexGrow(1, column);
		row.setOverflow(Overflow.HIDDEN);
		add(row);
		setFlexGrow(1, row);
	}

	/**
	 * Initialise the navigation items.
	 */
	private void initNaviItems() {
		NaviMenu menu = naviDrawer.getMenu();

		NaviItem examples = menu.addNaviItem(VaadinIcon.DEL_A, "Examples", null);

		menu.addNaviItem(examples, "Home", Home.class);
		menu.addNaviItem(examples, "Accounts", Accounts.class);
		menu.addNaviItem(examples, "Payments", Payments.class);
		menu.addNaviItem(examples, "Statistics", Statistics.class);

		NaviItem personnel = menu.addNaviItem(examples, "Personnel", null);
		menu.addNaviItem(personnel, "Accountants", Accountants.class);
		menu.addNaviItem(personnel, "Managers", Managers.class);


		menu.addNaviItem(VaadinIcon.DASHBOARD, "Dashboard", Dashboard.class);
		menu.addNaviItem(VaadinIcon.STORAGE, "Inventory", Inventory.class);
		menu.addNaviItem(VaadinIcon.ENVELOPES_O, "Messages", Messages.class);
		menu.addNaviItem(VaadinIcon.EXCHANGE, "Transactions", Transactions.class);
		menu.addNaviItem(VaadinIcon.CLIPBOARD_TEXT, "Reporting", Reporting.class);

		if (CurrentSession.getUser().getAccess_group() == AccessGroups.ADMIN.value())
			menu.addNaviItem(VaadinIcon.DOCTOR, "Admin", Admin.class);




	}

	/**
	 * Configure the app's inner and outer headers and footers.
	 */
	private void initHeadersAndFooters() {
		appBar = new AppBar("");
		setAppHeaderInner(appBar);
	}

	private void setAppHeaderOuter(Component... components) {
		if (appHeaderOuter == null) {
			appHeaderOuter = new Div();
			appHeaderOuter.addClassName("app-header-outer");
			getElement().insertChild(0, appHeaderOuter.getElement());
		}
		appHeaderOuter.removeAll();
		appHeaderOuter.add(components);
	}

	private void setAppHeaderInner(Component... components) {
		if (appHeaderInner == null) {
			appHeaderInner = new Div();
			appHeaderInner.addClassName("app-header-inner");
			column.getElement().insertChild(0, appHeaderInner.getElement());
		}
		appHeaderInner.removeAll();
		appHeaderInner.add(components);
	}

	private void setAppFooterInner(Component... components) {
		if (appFooterInner == null) {
			appFooterInner = new Div();
			appFooterInner.addClassName("app-footer-inner");
			column.getElement().insertChild(column.getElement().getChildCount(),
					appFooterInner.getElement());
		}
		appFooterInner.removeAll();
		appFooterInner.add(components);
	}

	private void setAppFooterOuter(Component... components) {
		if (appFooterOuter == null) {
			appFooterOuter = new Div();
			appFooterOuter.addClassName("app-footer-outer");
			getElement().insertChild(getElement().getChildCount(),
					appFooterOuter.getElement());
		}
		appFooterOuter.removeAll();
		appFooterOuter.add(components);
	}

	@Override
	public void configurePage(InitialPageSettings settings) {
		settings.addMetaTag("apple-mobile-web-app-capable", "yes");
		settings.addMetaTag("apple-mobile-web-app-status-bar-style", "black");

		settings.addFavIcon("icon", "frontend/styles/favicons/favicon.ico",
				"256x256");
	}

	@Override
	public void showRouterLayoutContent(HasElement content) {
		this.viewContainer.getElement().appendChild(content.getElement());
	}

	public NaviDrawer getNaviDrawer() {
		return naviDrawer;
	}

	public static MainLayout get() {
		return (MainLayout) UI.getCurrent().getChildren()
				.filter(component -> component.getClass() == MainLayout.class)
				.findFirst().get();
	}

	public AppBar getAppBar() {
		return appBar;
	}

	@Override
	public void afterNavigation(AfterNavigationEvent event) {
//        if (navigationTabs) {
//            afterNavigationWithTabs(event);
//        } else {
//            afterNavigationWithoutTabs(event);
//        }
		afterNavigationWithoutTabs(event);
	}

	private void afterNavigationWithTabs(AfterNavigationEvent e) {
//        NaviItem active = getActiveItem(e);
//        if (active == null) {
//            if (tabBar.getTabCount() == 0) {
//                tabBar.addClosableTab("", Home.class);
//            }
//        } else {
//            if (tabBar.getTabCount() > 0) {
//                tabBar.updateSelectedTab(active.getText(),
//                        active.getNavigationTarget());
//            } else {
//                tabBar.addClosableTab(active.getText(),
//                        active.getNavigationTarget());
//            }
//        }
		appBar.getMenuIcon().setVisible(false);
	}

	private NaviItem getActiveItem(AfterNavigationEvent e) {
		for (NaviItem item : naviDrawer.getMenu().getNaviItems()) {
			if (item.isHighlighted(e)) {
				return item;
			}
		}
		return null;
	}

	private void afterNavigationWithoutTabs(AfterNavigationEvent e) {
		NaviItem active = getActiveItem(e);
		if (active != null) {
			getAppBar().setTitle(active.getText());
		}
	}

}
