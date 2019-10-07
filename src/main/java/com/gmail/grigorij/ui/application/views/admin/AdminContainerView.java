package com.gmail.grigorij.ui.application.views.admin;

import com.gmail.grigorij.ui.components.detailsdrawer.DetailsDrawer;
import com.gmail.grigorij.ui.components.layouts.SplitViewFrame;
import com.gmail.grigorij.ui.components.navigation.bar.AppBar;
import com.gmail.grigorij.ui.application.views.ApplicationContainerView;
import com.gmail.grigorij.utils.ProjectConstants;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.tabs.TabsVariant;


@CssImport("./styles/views/admin/admin-main.css")
public class AdminContainerView extends SplitViewFrame {

	private static final String CLASS_NAME = "admin-main";

	private AppBar appBar;
	private Div content;
	private DetailsDrawer detailsDrawer;

	private final ApplicationContainerView menuLayout;

	public AdminContainerView(ApplicationContainerView menuLayout) {
		this.menuLayout = menuLayout;

		initAppBar();
		setViewContent(createContent());
		constructTab();
	}

	private void initAppBar() {
		appBar = menuLayout.getAppBar();
		appBar.setTabsVariant(TabsVariant.LUMO_SMALL);

		appBar.addTab(ProjectConstants.COMPANIES);
		appBar.addTab(ProjectConstants.PERSONNEL);
		appBar.addTab(ProjectConstants.ADMIN_INVENTORY);
		appBar.addTab(ProjectConstants.ADMIN_TRANSACTIONS);

		appBar.addTabSelectionListener(e -> {
			if (detailsDrawer != null)
				detailsDrawer.hide();
			constructTab();
		});
		appBar.centerTabs();
	}

	private void constructTab() {
		this.content.removeAll();

		if (appBar.getSelectedTab() != null) {
			menuLayout.selectCorrectNaviItem(ProjectConstants.ADMIN, appBar.getSelectedTab().getLabel());

			if (appBar.getSelectedTab().getLabel().equals(ProjectConstants.COMPANIES)) {
				this.content.add(new AdminCompanies(this));
				return;
			}

			if (appBar.getSelectedTab().getLabel().equals(ProjectConstants.PERSONNEL)) {
				this.content.add(new AdminPersonnel(this));
				return;
			}

			if (appBar.getSelectedTab().getLabel().equals(ProjectConstants.ADMIN_INVENTORY)) {
				this.content.add(new AdminInventory(this));
				return;
			}
			if (appBar.getSelectedTab().getLabel().equals(ProjectConstants.ADMIN_TRANSACTIONS)) {
				this.content.add(new AdminTransactions(this));
				return;
			}


			//...
		}
	}

	private Component createContent() {
		content = new Div();
		content.addClassName("admin-content-view");
		return content;
	}

	public void setDetailsDrawer(DetailsDrawer detailsDrawer) {
		this.detailsDrawer = detailsDrawer;
		setViewDetails(detailsDrawer);
	}
}