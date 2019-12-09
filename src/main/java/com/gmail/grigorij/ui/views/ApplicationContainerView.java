package com.gmail.grigorij.ui.views;

import com.gmail.grigorij.MainLayout;
import com.gmail.grigorij.backend.database.enums.operations.Operation;
import com.gmail.grigorij.backend.database.enums.operations.OperationTarget;
import com.gmail.grigorij.backend.database.enums.permissions.PermissionLevel;
import com.gmail.grigorij.backend.database.facades.PermissionFacade;
import com.gmail.grigorij.ui.components.FlexBoxLayout;
import com.gmail.grigorij.ui.components.navigation.bar.AppBar;
import com.gmail.grigorij.ui.components.navigation.drawer.NaviDrawer;
import com.gmail.grigorij.ui.components.navigation.drawer.NaviItem;
import com.gmail.grigorij.ui.components.navigation.drawer.NaviMenu;
import com.gmail.grigorij.ui.utils.UIUtils;
import com.gmail.grigorij.ui.utils.css.FlexDirection;
import com.gmail.grigorij.ui.utils.css.Overflow;
import com.gmail.grigorij.ui.views.app.*;
import com.gmail.grigorij.utils.authentication.AuthenticationService;
import com.gmail.grigorij.utils.Broadcaster;
import com.gmail.grigorij.utils.ProjectConstants;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.server.InitialPageSettings;
import com.vaadin.flow.server.PageConfigurator;
import com.vaadin.flow.shared.Registration;

import java.util.ArrayList;
import java.util.List;

@CssImport("./styles/components/forms.css")
@CssImport("./styles/components/dialogs.css")
@CssImport("./styles/components/navi-drawer.css")
@CssImport(value = "./styles/components/vaadin-components/grid-style.css", themeFor = "vaadin-grid")
@CssImport(value = "./styles/components/vaadin-components/date-picker-style.css", themeFor = "vaadin-date-picker")
@CssImport(value = "./styles/components/vaadin-components/button-style.css", themeFor = "vaadin-button")
@CssImport(value = "./styles/components/vaadin-components/vaadin-combo-box.css", themeFor = "vaadin-combo-box")
@CssImport(value = "./styles/components/vaadin-components/menu-bar-style.css", themeFor = "vaadin-menu-bar")
@CssImport(value = "./styles/components/vaadin-components/vaadin-dialog-overlay.css", themeFor = "vaadin-dialog-overlay")
public class ApplicationContainerView extends FlexBoxLayout implements PageConfigurator {

//	private static final Logger log = LoggerFactory.getLogger(ApplicationContainerView.class);
	private static final String CLASS_NAME = "application-container";

	private List<Notification> notifications = new ArrayList<>();

	private NaviDrawer naviDrawer;
	private FlexBoxLayout column;

	private AppBar appBar;

	private FlexBoxLayout viewContainer;
	private Div appHeader;

	private final MainLayout mainLayout;

	public ApplicationContainerView(MainLayout mainLayout) {
		this.mainLayout = mainLayout;

		addClassName(CLASS_NAME);
		setFlexDirection(FlexDirection.COLUMN);
		setSizeFull();

		setThemeVariant(AuthenticationService.getCurrentSessionUser().getThemeVariant());

		constructDrawer();

		constructAppHeader();

		constructNaviItems();

		// Show notification about closing tab
//		if (UI.getCurrent() != null) {
//			Page page = UI.getCurrent().getPage();
//
//			if (page != null) {
//				page.executeJS("window.onbeforeunload = confirmExit; function confirmExit() { return 'Are you sure, you want to exit?';}");
//			}
//		}
	}

	public void setThemeVariant(String themeVariant) {
		mainLayout.setThemeVariant(themeVariant);
	}


	/**
	 * Initialise the navigation drawer.
	 */
	private void constructDrawer() {
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
	private void constructNaviItems() {
		NaviMenu menu = naviDrawer.getMenu();

		NaviItem dashboard = new NaviItem(VaadinIcon.DASHBOARD, ProjectConstants.DASHBOARD);
		NaviItem inventory = new NaviItem(VaadinIcon.STORAGE, ProjectConstants.INVENTORY);
		NaviItem messages = new NaviItem(VaadinIcon.ENVELOPES_O, ProjectConstants.MESSAGES);
		NaviItem transaction = new NaviItem(VaadinIcon.EXCHANGE, ProjectConstants.TRANSACTIONS);
		NaviItem reporting = new NaviItem(VaadinIcon.CLIPBOARD_TEXT, ProjectConstants.REPORTING);


		dashboard.addClickListener(e-> {
			naviItemOnClick(dashboard, false);
			viewContainer.add(new DashboardView());
		});
		menu.addNaviItem(dashboard);

		if (PermissionFacade.getInstance().isSystemAdminOrAllowedTo(Operation.VIEW, OperationTarget.INVENTORY_TAB, null)) {
			inventory.addClickListener(e-> {
				naviItemOnClick(inventory, false);
				viewContainer.add(new InventoryView());
			});
			menu.addNaviItem(inventory);
		}


		if (PermissionFacade.getInstance().isSystemAdminOrAllowedTo(Operation.VIEW, OperationTarget.MESSAGES_TAB, null)) {
			messages.addClickListener(e-> {
				naviItemOnClick(messages, false);
				viewContainer.add(new MessagesView());

				notifications.forEach(Notification::close);
				notifications.clear();
			});
			menu.addNaviItem(messages);
		}


		if (PermissionFacade.getInstance().isSystemAdminOrAllowedTo(Operation.VIEW, OperationTarget.TRANSACTIONS_TAB, null)) {

			transaction.addClickListener(e-> {
				naviItemOnClick(transaction, false);
				viewContainer.add(new TransactionsView());
			});
			menu.addNaviItem(transaction);
		}


		if (PermissionFacade.getInstance().isSystemAdminOrAllowedTo(Operation.VIEW, OperationTarget.REPORTING_TAB, null)) {

			reporting.addClickListener(e-> {
				naviItemOnClick(reporting, false);
				viewContainer.add(new ReportingView());
			});
			menu.addNaviItem(reporting);
		}


		if (AuthenticationService.getCurrentSessionUser().getPermissionLevel().higherOrEqualsTo(PermissionLevel.COMPANY_ADMIN)) {
			NaviItem adminItem = new NaviItem(VaadinIcon.DOCTOR, ProjectConstants.ADMIN);
			menu.addNaviItem(adminItem);

			adminItem.addClickListener(e-> {
				adminItem.expandCollapse.click();
			});

			NaviItem admin_companies;

			if (AuthenticationService.getCurrentSessionUser().getPermissionLevel().equalsTo(PermissionLevel.SYSTEM_ADMIN)) {
				admin_companies = new NaviItem(ProjectConstants.ADMIN_COMPANIES);
			} else {
				admin_companies = new NaviItem(ProjectConstants.ADMIN_COMPANY);
			}

			menu.addNaviItem(adminItem, admin_companies);

			admin_companies.addClickListener(e-> {
				naviItemOnClick(admin_companies, true);
			});

			NaviItem admin_personnel = new NaviItem(ProjectConstants.ADMIN_PERSONNEL);
			menu.addNaviItem(adminItem, admin_personnel);

			admin_personnel.addClickListener(e-> {
				naviItemOnClick(admin_personnel, true);
			});

			NaviItem admin_inventory = new NaviItem(ProjectConstants.ADMIN_INVENTORY);
			menu.addNaviItem(adminItem, admin_inventory);

			admin_inventory.addClickListener(e-> {
				naviItemOnClick(admin_inventory, true);
			});
		}

		//Open Dashboard view
		naviItemOnClick(dashboard, false);
		viewContainer.add(new DashboardView());
	}

	private void naviItemOnClick(NaviItem naviItem, boolean adminNaviItem) {
		naviDrawer.close();
		viewContainer.removeAll();
		appBar.reset();

		if (adminNaviItem) {
			viewContainer.add(new AdminWrapperView(this));

			selectCorrectNaviItem(ProjectConstants.ADMIN, naviItem.getText());

			for (Tab tab : appBar.getTabs()) {
				if (tab.getLabel().equals(naviItem.getText())) {
					appBar.setSelectedTab(tab);
					break;
				}
			}
		} else {
			selectCorrectNaviItem("", naviItem.getText());
		}
	}

//	private void adminNaviItemOnClick(NaviItem naviItem) {
//		naviDrawer.close();
//		viewContainer.removeAll();
//		appBar.reset();
//
//		viewContainer.add(new AdminView(this));
//
//		selectCorrectNaviItem(ProjectConstants.ADMIN, naviItem.getText());
//
//		for (Tab tab : appBar.getTabs()) {
//			if (tab.getLabel().equals(naviItem.getText())) {
//				appBar.setSelectedTab(tab);
//				break;
//			}
//		}
//	}

	public void selectCorrectNaviItem(String tabNamePrefix, String tabName) {
		appBar.setTitle(tabNamePrefix + " " + tabName);

		for (NaviItem item : naviDrawer.getMenu().getNaviItems()) {
			item.setSelected(false);

			if (item.getText().equals(tabName)) {
				item.setSelected(true);
			}
		}
	}


	private void constructAppHeader() {
		appBar = new AppBar(this, "");
		setAppHeader(appBar);
	}

	private void setAppHeader(Component... components) {
		if (appHeader == null) {
			appHeader = new Div();
			appHeader.addClassName("app-header-inner");
			column.getElement().insertChild(0, appHeader.getElement());
		}
		appHeader.removeAll();
		appHeader.add(components);
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
		broadcasterRegistration = Broadcaster.registerUser(AuthenticationService.getCurrentSessionUser().getId(), message -> {
			ui.access(() -> {
				Notification notification = UIUtils.constructNotification(message, NotificationVariant.LUMO_PRIMARY, 0);
				notification.open();

				notifications.add(notification);
			});
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
