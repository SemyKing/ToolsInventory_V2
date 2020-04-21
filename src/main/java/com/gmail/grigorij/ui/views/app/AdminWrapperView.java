package com.gmail.grigorij.ui.views.app;

import com.gmail.grigorij.backend.database.enums.operations.Operation;
import com.gmail.grigorij.backend.database.enums.operations.OperationTarget;
import com.gmail.grigorij.backend.database.enums.permissions.PermissionLevel;
import com.gmail.grigorij.backend.database.enums.permissions.PermissionRange;
import com.gmail.grigorij.backend.database.facades.PermissionFacade;
import com.gmail.grigorij.ui.components.navigation.bar.AppBar;
import com.gmail.grigorij.ui.views.ApplicationContainerView;
import com.gmail.grigorij.ui.views.app.admin.AdminCompanies;
import com.gmail.grigorij.ui.views.app.admin.AdminInventory;
import com.gmail.grigorij.ui.views.app.admin.AdminPersonnel;
import com.gmail.grigorij.utils.authentication.AuthenticationService;
import com.gmail.grigorij.utils.ProjectConstants;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.tabs.TabsVariant;

@StyleSheet("context://styles/views/admin.css")
public class AdminWrapperView extends Div {

	private static final String CLASS_NAME = "admin-container";

	private AppBar appBar;
	private Div content;

	private final ApplicationContainerView applicationContainerView;


	public AdminWrapperView(ApplicationContainerView applicationContainerView) {
		this.applicationContainerView = applicationContainerView;

		addClassName(CLASS_NAME);

		add(constructContent());

		constructAppBarTabs();

		tabsOnSelect();
	}


	private Div constructContent() {
		content = new Div();
		content.setClassName(CLASS_NAME + "__content");
		return content;
	}

	private void constructAppBarTabs() {
		appBar = applicationContainerView.getAppBar();
		appBar.setTabsVariant(TabsVariant.LUMO_SMALL);

		if (AuthenticationService.getCurrentSessionUser().getPermissionLevel().equalsTo(PermissionLevel.SYSTEM_ADMIN)) {
			appBar.addTab(ProjectConstants.ADMIN_COMPANIES);
		} else {
			if (PermissionFacade.getInstance().isSystemAdminOrAllowedTo(Operation.VIEW, OperationTarget.COMPANY, PermissionRange.OWN)) {
				appBar.addTab(ProjectConstants.ADMIN_COMPANY);
			}
		}
		appBar.addTab(ProjectConstants.ADMIN_PERSONNEL);
		appBar.addTab(ProjectConstants.ADMIN_INVENTORY);

		appBar.addTabSelectionListener(e -> tabsOnSelect());
	}

	private void tabsOnSelect() {
		if (appBar.getSelectedTab() != null) {

			this.content.removeAll();

			switch (appBar.getSelectedTab().getLabel()) {

				case ProjectConstants.ADMIN_COMPANY:
					this.content.add(new AdminCompanies(this));
					applicationContainerView.selectCorrectNaviItem(ProjectConstants.ADMIN, ProjectConstants.ADMIN_COMPANY);
					break;
				case ProjectConstants.ADMIN_COMPANIES:
					this.content.add(new AdminCompanies(this));
					applicationContainerView.selectCorrectNaviItem(ProjectConstants.ADMIN, ProjectConstants.ADMIN_COMPANIES);
					break;

				case ProjectConstants.ADMIN_PERSONNEL:
					this.content.add(new AdminPersonnel(this));
					applicationContainerView.selectCorrectNaviItem(ProjectConstants.ADMIN, ProjectConstants.ADMIN_PERSONNEL);
					break;

				case ProjectConstants.ADMIN_INVENTORY:
					this.content.add(new AdminInventory(this));
					applicationContainerView.selectCorrectNaviItem(ProjectConstants.ADMIN, ProjectConstants.ADMIN_INVENTORY);
					break;
			}
		}
	}
}