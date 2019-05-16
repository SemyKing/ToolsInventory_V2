package com.gmail.grigorij.ui.views.navigation.admin;

import com.gmail.grigorij.ui.MainLayout;
import com.gmail.grigorij.ui.components.detailsdrawer.DetailsDrawer;
import com.gmail.grigorij.ui.components.detailsdrawer.DetailsDrawerFooter;
import com.gmail.grigorij.ui.components.detailsdrawer.DetailsDrawerHeader;
import com.gmail.grigorij.ui.components.navigation.bar.AppBar;
import com.gmail.grigorij.ui.util.css.FlexDirection;
import com.gmail.grigorij.ui.views.frames.SplitViewFrame;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.tabs.TabsVariant;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;


@PageTitle("Admin")
@Route(value = "admin", layout = MainLayout.class)
public class AdminMain extends SplitViewFrame {

	private AppBar appBar;
	private Div content;
	static DetailsDrawer detailsDrawer;
	static DetailsDrawerFooter detailsDrawerFooter;


	@Override
	protected void onAttach(AttachEvent attachEvent) {
		super.onAttach(attachEvent);
		initAppBar();
		setViewContent(createContent());
		setViewDetails(createDetailsDrawer());
		handleContent();
	}


	private void initAppBar() {
		appBar = MainLayout.get().getAppBar();

		appBar.addTab(AdminCompanies.TAB_NAME);
		appBar.addTab(AdminPersonnel.TAB_NAME);

		appBar.addTabSelectionListener(e -> {
			detailsDrawer.hide();
			handleContent();
		});
		appBar.centerTabs();
	}

	private void handleContent() {
		this.content.removeAll();

		if (appBar.getSelectedTab() != null) {
			if (appBar.getSelectedTab().getLabel().equals(AdminCompanies.TAB_NAME)) {
				this.content.add(new AdminCompanies());
				return;
			}

			if (appBar.getSelectedTab().getLabel().equals(AdminPersonnel.TAB_NAME)) {
				this.content.add(new AdminPersonnel());
				return;
			}
		}
	}


	private Component createContent() {
		content = new Div();
		content.addClassName("admin-content-view");
		content.getStyle().set("margin", "5px");
		return content;
	}


	private DetailsDrawer createDetailsDrawer() {
		detailsDrawer = new DetailsDrawer(DetailsDrawer.Position.RIGHT);

		// Header
		DetailsDrawerHeader detailsDrawerTitle = new DetailsDrawerHeader("Details");

		detailsDrawer.setHeader(detailsDrawerTitle);
		detailsDrawer.getHeader().setFlexDirection(FlexDirection.COLUMN);

		// Footer
		detailsDrawerFooter = new DetailsDrawerFooter();
		detailsDrawerFooter.addCancelListener(e -> detailsDrawer.hide());
		detailsDrawer.setFooter(detailsDrawerFooter);

		return detailsDrawer;
	}
}
