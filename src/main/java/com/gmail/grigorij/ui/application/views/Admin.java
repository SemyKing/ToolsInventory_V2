package com.gmail.grigorij.ui.application.views;

import com.gmail.grigorij.ui.application.views.admin.AdminCompanies;
import com.gmail.grigorij.ui.application.views.admin.AdminInventory;
import com.gmail.grigorij.ui.application.views.admin.AdminPersonnel;
import com.gmail.grigorij.ui.application.views.admin.AdminTransactions;
import com.gmail.grigorij.ui.components.detailsdrawer.DetailsDrawer;
import com.gmail.grigorij.ui.components.navigation.bar.AppBar;
import com.gmail.grigorij.ui.application.ApplicationContainerView;
import com.gmail.grigorij.utils.ProjectConstants;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.tabs.TabsVariant;

@StyleSheet("context://styles/views/admin.css")
public class Admin extends Div {

	private static final String CLASS_NAME = "admin-container";

	private AppBar appBar;
	private Div content;
	private DetailsDrawer detailsDrawer;

	private final ApplicationContainerView menuLayout;


	public Admin(ApplicationContainerView menuLayout) {
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

		appBar.addTab(ProjectConstants.COMPANIES);
		appBar.addTab(ProjectConstants.PERSONNEL);
		appBar.addTab(ProjectConstants.ADMIN_INVENTORY);
		appBar.addTab(ProjectConstants.ADMIN_TRANSACTIONS);

		appBar.addTabSelectionListener(e -> {
			if (detailsDrawer != null)
				detailsDrawer.hide();
			tabsOnSelect();
		});
		appBar.centerTabs();
	}

	private void tabsOnSelect() {
		if (appBar.getSelectedTab() != null) {

			this.content.removeAll();

			switch (appBar.getSelectedTab().getLabel()) {

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

				case ProjectConstants.ADMIN_TRANSACTIONS:
					this.content.add(new AdminTransactions(this));
					menuLayout.selectCorrectNaviItem(ProjectConstants.ADMIN, ProjectConstants.ADMIN_TRANSACTIONS);
					break;
			}
		}
	}

	private Component constructContent() {
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
}