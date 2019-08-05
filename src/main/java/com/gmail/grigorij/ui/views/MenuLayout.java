package com.gmail.grigorij.ui.views;

import com.gmail.grigorij.ui.MainLayout;
import com.gmail.grigorij.backend.entities.access.AccessGroups;
import com.gmail.grigorij.ui.utils.UIUtils;
import com.gmail.grigorij.utils.AuthenticationService;
import com.gmail.grigorij.ui.utils.components.FlexBoxLayout;
import com.gmail.grigorij.ui.utils.components.navigation.bar.AppBar;
import com.gmail.grigorij.ui.utils.components.navigation.drawer.NaviDrawer;
import com.gmail.grigorij.ui.utils.components.navigation.drawer.NaviItem;
import com.gmail.grigorij.ui.utils.components.navigation.drawer.NaviMenu;
import com.gmail.grigorij.ui.utils.css.FlexDirection;
import com.gmail.grigorij.ui.utils.css.Overflow;
import com.gmail.grigorij.ui.views.navigation.admin.AdminMain;
import com.gmail.grigorij.ui.views.navigation.dashboard.Dashboard;
import com.gmail.grigorij.ui.views.navigation.inventory.Inventory;
import com.gmail.grigorij.ui.views.navigation.messages.Messages;
import com.gmail.grigorij.ui.views.navigation.reporting.Reporting;
import com.gmail.grigorij.ui.views.navigation.transactions.Transactions;
import com.gmail.grigorij.utils.Broadcaster;
import com.gmail.grigorij.utils.ProjectConstants;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.server.InitialPageSettings;
import com.vaadin.flow.server.PageConfigurator;
import com.vaadin.flow.shared.Registration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class MenuLayout extends FlexBoxLayout implements PageConfigurator {

	private static final Logger log = LoggerFactory.getLogger(MenuLayout.class);
	private static final String CLASS_NAME = "main-menu";

	private Div appHeaderOuter;

	private NaviDrawer naviDrawer;
	private FlexBoxLayout column;

	private AppBar appBar;

	private FlexBoxLayout viewContainer;
	private Div appHeaderInner;
	private Div appFooterInner;
	private Div appFooterOuter;

	private final MainLayout mainLayout;

	public MenuLayout(MainLayout mainLayout) {
		this.mainLayout = mainLayout;
		setId("menu-layout");
		addClassName(CLASS_NAME);
		setThemeVariant(AuthenticationService.getCurrentSessionUser().getThemeVariant());
		setFlexDirection(FlexDirection.COLUMN);
		setSizeFull();

		// Initialise the navigation drawer
		initDrawerStructure();

		// Configure the headers and footers (optional)
		initHeadersAndFooters();

		// Populate the navigation drawer
		//!!! Must be constructed after initHeadersAndFooters();
		initNaviItems();


		//Show notification about closing tab
//		if (UI.getCurrent() != null) {
//			Page page = UI.getCurrent().getPage();
//
//			if (page != null) {
//				System.out.println("execute js");
//				page.executeJavaScript("window.onbeforeunload = confirmExit; function confirmExit() { return 'Are you sure, you want to close?';}");
//			}
//		}
	}

	public void setThemeVariant(String themeVariant) {
		mainLayout.setThemeVariant(themeVariant);
	}


	/**
	 * Initialise the navigation drawer.
	 */
	private void initDrawerStructure() {
		naviDrawer = new NaviDrawer();

		viewContainer = new FlexBoxLayout();
		viewContainer.addClassName(CLASS_NAME + "__view-container");
		viewContainer.setOverflow(Overflow.HIDDEN);

		column = new FlexBoxLayout(viewContainer);
		column.addClassName(CLASS_NAME + "__column");
		column.setFlexDirection(FlexDirection.COLUMN);
		column.setFlexGrow(1, viewContainer);
		column.setOverflow(Overflow.HIDDEN);

		FlexBoxLayout row = new FlexBoxLayout(naviDrawer, column);
		row.addClassName(CLASS_NAME + "__row");
		row.setFlexGrow(1, column);
		row.setOverflow(Overflow.HIDDEN);
		add(row);

		setFlexGrow(1, row);
	}

	/**
	 * Initialise the navigation items inside navigation drawer.
	 */
	private void initNaviItems() {
		NaviMenu menu = naviDrawer.getMenu();

		NaviItem dashboard = new NaviItem(VaadinIcon.DASHBOARD, ProjectConstants.DASHBOARD, false);
		NaviItem inventory = new NaviItem(VaadinIcon.STORAGE, ProjectConstants.INVENTORY, false);
		NaviItem messages = new NaviItem(VaadinIcon.ENVELOPES_O, ProjectConstants.MESSAGES, false);
		NaviItem transaction = new NaviItem(VaadinIcon.EXCHANGE, ProjectConstants.TRANSACTIONS, false);
		NaviItem reporting = new NaviItem(VaadinIcon.CLIPBOARD_TEXT, ProjectConstants.REPORTING, false);


		dashboard.addClickListener(e-> {
			naviItemOnClick(dashboard);
			viewContainer.add(new Dashboard());
		});
		menu.addNaviItem(dashboard);

		inventory.addClickListener(e-> {
			naviItemOnClick(inventory);
			viewContainer.add(new Inventory());
		});
		menu.addNaviItem(inventory);

		messages.addClickListener(e-> {
			naviItemOnClick(messages);
			viewContainer.add(new Messages());
		});
		menu.addNaviItem(messages);

		transaction.addClickListener(e-> {
			naviItemOnClick(transaction);
			viewContainer.add(new Transactions());
		});
		menu.addNaviItem(transaction);

		reporting.addClickListener(e-> {
			naviItemOnClick(reporting);
			viewContainer.add(new Reporting());
		});
		menu.addNaviItem(reporting);

		if (AuthenticationService.getCurrentSessionUser().getAccessGroup() == AccessGroups.ADMIN.getIntValue()) {
			NaviItem admin = new NaviItem(VaadinIcon.DOCTOR, ProjectConstants.ADMIN, true);
			menu.addNaviItem(admin);

			admin.addClickListener(e-> {
				admin.expandCollapse.click();
			});

			NaviItem admin_companies = new NaviItem(ProjectConstants.COMPANIES, null , false);
			menu.addNaviItem(admin, admin_companies);

			admin_companies.addClickListener(e-> {
				adminNaviItemOnClick(admin_companies);
			});


			NaviItem admin_personnel = new NaviItem(ProjectConstants.PERSONNEL, null , false);
			menu.addNaviItem(admin, admin_personnel);

			admin_personnel.addClickListener(e-> {
				adminNaviItemOnClick(admin_personnel);
			});

			NaviItem admin_inventory = new NaviItem(ProjectConstants.ADMIN_INVENTORY, null , false);
			menu.addNaviItem(admin, admin_inventory);

			admin_inventory.addClickListener(e-> {
				adminNaviItemOnClick(admin_inventory);
			});

			NaviItem admin_transactions = new NaviItem(ProjectConstants.ADMIN_TRANSACTIONS, null , false);
			menu.addNaviItem(admin, admin_transactions);

			admin_transactions.addClickListener(e-> {
				adminNaviItemOnClick(admin_transactions);
			});
		}


		//Open Dashboard view
		naviItemOnClick(dashboard);
		viewContainer.add(new Dashboard());
	}

	private void naviItemOnClick(NaviItem naviItem) {
		viewContainer.removeAll();
		appBar.reset();

		selectCorrectNaviItem(naviItem.getText(), false);

		naviDrawer.close();
	}

	private void adminNaviItemOnClick(NaviItem naviItem) {
		viewContainer.removeAll();
		appBar.reset();

		viewContainer.add(new AdminMain(this));

		selectCorrectNaviItem(naviItem.getText(), true);

		naviDrawer.close();

		for (Tab tab : appBar.getTabs()) {
			if (tab.getLabel().equals(naviItem.getText())) {
				appBar.setSelectedTab(tab);
				break;
			}
		}
	}

	public void selectCorrectNaviItem(String tabName, boolean adminTab) {
		if (adminTab) {
			appBar.setTitle(ProjectConstants.ADMIN + " " + tabName);
		} else {
			appBar.setTitle(tabName);
		}

		for (NaviItem item : naviDrawer.getMenu().getNaviItems()) {
			item.setSelected(false);

			if (item.getText().equals(tabName)) {
				item.setSelected(true);
			}
		}
	}


	private void initHeadersAndFooters() {
		appBar = new AppBar(this, "");
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


	public NaviDrawer getNaviDrawer() {
		return naviDrawer;
	}

	public AppBar getAppBar() {
		return appBar;
	}

	@Override
	public void configurePage(InitialPageSettings settings) {
		settings.addMetaTag("apple-mobile-web-app-capable", "yes");
		settings.addMetaTag("apple-mobile-web-app-status-bar-style", "black");
		settings.addFavIcon("icon", "images/favicons/favicon.ico", "256x256");
	}



	private Registration broadcasterRegistration;

	@Override
	protected void onAttach(AttachEvent attachEvent) {
		UI ui = attachEvent.getUI();
		broadcasterRegistration = Broadcaster.registerUser(AuthenticationService.getCurrentSessionUser().getId(), newMessage -> {
			ui.access(() -> UIUtils.showNotification(newMessage, UIUtils.NotificationType.INFO, 0));
			ui.getSession().lock();
			ui.push();
			ui.getSession().unlock();
		});
	}

	@Override
	protected void onDetach(DetachEvent detachEvent) {
		broadcasterRegistration.remove();
		broadcasterRegistration = null;
	}
}
