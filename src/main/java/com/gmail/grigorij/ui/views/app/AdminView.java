package com.gmail.grigorij.ui.views.app;

import com.gmail.grigorij.backend.database.entities.Tool;
import com.gmail.grigorij.backend.database.entities.User;
import com.gmail.grigorij.backend.database.enums.permissions.PermissionLevel;
import com.gmail.grigorij.backend.database.enums.tools.ToolUsageStatus;
import com.gmail.grigorij.backend.database.facades.InventoryFacade;
import com.gmail.grigorij.backend.database.facades.UserFacade;
import com.gmail.grigorij.ui.components.detailsdrawer.DetailsDrawer;
import com.gmail.grigorij.ui.components.navigation.bar.AppBar;
import com.gmail.grigorij.ui.utils.UIUtils;
import com.gmail.grigorij.ui.views.ApplicationContainerView;
import com.gmail.grigorij.ui.views.app.admin.AdminCompanies;
import com.gmail.grigorij.ui.views.app.admin.AdminInventory;
import com.gmail.grigorij.ui.views.app.admin.AdminPersonnel;
import com.gmail.grigorij.utils.AuthenticationService;
import com.gmail.grigorij.utils.ProjectConstants;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.tabs.TabsVariant;

import java.util.List;

@StyleSheet("context://styles/views/admin.css")
public class AdminView extends Div {

	private static final String CLASS_NAME = "admin-container";

	private AppBar appBar;
	private Div content;
	private DetailsDrawer detailsDrawer;

	private final ApplicationContainerView menuLayout;


	public AdminView(ApplicationContainerView menuLayout) {
		this.menuLayout = menuLayout;

		addClassName(CLASS_NAME);

		Div contentWrapper = new Div();
		contentWrapper.addClassName(CLASS_NAME + "__content_wrapper");
		contentWrapper.add(constructContent());

		add(contentWrapper);
		add(constructDetails());

		constructAppBarTabs();

		tabsOnSelect();
	}


	private void constructAppBarTabs() {
		appBar = menuLayout.getAppBar();
		appBar.setTabsVariant(TabsVariant.LUMO_SMALL);

		if (AuthenticationService.getCurrentSessionUser().getPermissionLevel().equalsTo(PermissionLevel.SYSTEM_ADMIN)) {
			appBar.addTab(ProjectConstants.COMPANIES);
		} else {
			appBar.addTab(ProjectConstants.COMPANY);
		}
		appBar.addTab(ProjectConstants.PERSONNEL);
		appBar.addTab(ProjectConstants.ADMIN_INVENTORY);

		appBar.addTabSelectionListener(e -> {
			if (detailsDrawer != null)
				detailsDrawer.hide();
			tabsOnSelect();
		});
	}

	private void tabsOnSelect() {
		if (appBar.getSelectedTab() != null) {

			this.content.removeAll();

			switch (appBar.getSelectedTab().getLabel()) {

				case ProjectConstants.COMPANY:
					this.content.add(new AdminCompanies(this));
					menuLayout.selectCorrectNaviItem(ProjectConstants.ADMIN, ProjectConstants.COMPANY);
					break;
				case ProjectConstants.COMPANIES:
					this.content.add(new AdminCompanies(this));
					menuLayout.selectCorrectNaviItem(ProjectConstants.ADMIN, ProjectConstants.COMPANIES);
					break;

				case ProjectConstants.PERSONNEL:
					this.content.add(new AdminPersonnel(this));
					menuLayout.selectCorrectNaviItem(ProjectConstants.ADMIN, ProjectConstants.PERSONNEL);
					break;

				case ProjectConstants.ADMIN_INVENTORY:
					this.content.add(new AdminInventory(this));
					menuLayout.selectCorrectNaviItem(ProjectConstants.ADMIN, ProjectConstants.ADMIN_INVENTORY);
					break;
			}
		}
	}

	private Div constructContent() {
		content = new Div();
		content.setClassName(CLASS_NAME + "__content");
		return content;
	}

	private DetailsDrawer constructDetails() {
		detailsDrawer = new DetailsDrawer(DetailsDrawer.Position.RIGHT);
		return detailsDrawer;
	}

	public DetailsDrawer getDetailsDrawer() {
		return detailsDrawer;
	}


	public boolean handleUserStatusChange(long userId, boolean newStatus) {
		User user = UserFacade.getInstance().getUserById(userId);

		user.setDeleted(newStatus);

		// REMOVE USER FROM TOOLS
		if (newStatus) {
			List<Tool> toolsInUse = InventoryFacade.getInstance().getAllToolsByCurrentUserId(userId);

			for (Tool tool : toolsInUse) {
				tool.setCurrentUser(null);

				if (tool.getReservedUser() == null) {
					tool.setUsageStatus(ToolUsageStatus.FREE);
				} else {
					tool.setUsageStatus(ToolUsageStatus.RESERVED);
				}

				InventoryFacade.getInstance().update(tool);
			}

			List<Tool> toolsReserved = InventoryFacade.getInstance().getAllToolsByReservedUserId(userId);

			for (Tool tool : toolsReserved) {
				tool.setReservedUser(null);

				if (tool.getCurrentUser() == null) {
					tool.setUsageStatus(ToolUsageStatus.FREE);
				} else {
					tool.setUsageStatus(ToolUsageStatus.IN_USE);
				}

				InventoryFacade.getInstance().update(tool);
			}
		}

		if (!UserFacade.getInstance().update(user)) {
			UIUtils.showNotification("User status change failed for: " + user.getUsername(), NotificationVariant.LUMO_ERROR);
			return false;
		}

		return true;
	}


}