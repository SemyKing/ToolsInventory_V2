package com.gmail.grigorij.ui.views.navigation.admin;

import com.gmail.grigorij.ui.MenuLayout;
import com.gmail.grigorij.ui.utils.components.detailsdrawer.DetailsDrawer;
import com.gmail.grigorij.ui.utils.components.navigation.bar.AppBar;
import com.gmail.grigorij.ui.utils.frames.SplitViewFrame;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.tabs.TabsVariant;
import com.vaadin.flow.router.PageTitle;


@PageTitle("Admin")
public class AdminMain extends SplitViewFrame {

	private AppBar appBar;
	private Div content;
	private DetailsDrawer detailsDrawer;

	private final MenuLayout menuLayout;

	public AdminMain(MenuLayout menuLayout) {
		this.menuLayout = menuLayout;
		menuLayout.getAppBar().reset();

		initAppBar();
		setViewContent(createContent());
		handleContent();
	}

	private void initAppBar() {
		appBar = menuLayout.getAppBar();
		appBar.setTabsVariant(TabsVariant.LUMO_SMALL);

		appBar.addTab(AdminCompanies.TAB_NAME);
		appBar.addTab(AdminPersonnel.TAB_NAME);

		appBar.addTabSelectionListener(e -> {
			if (detailsDrawer != null)
				detailsDrawer.hide();
			handleContent();
		});
		appBar.centerTabs();
	}

	private void handleContent() {
		this.content.removeAll();

		if (appBar.getSelectedTab() != null) {
			if (appBar.getSelectedTab().getLabel().equals(AdminCompanies.TAB_NAME)) {
				this.content.add(new AdminCompanies(this));
				return;
			}

			if (appBar.getSelectedTab().getLabel().equals(AdminPersonnel.TAB_NAME)) {
				this.content.add(new AdminPersonnel(this));
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

	void setDetailsDrawer(DetailsDrawer detailsDrawer) {
		this.detailsDrawer = detailsDrawer;
		setViewDetails(detailsDrawer);
	}
}