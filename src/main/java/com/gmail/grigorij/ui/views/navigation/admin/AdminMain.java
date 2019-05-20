package com.gmail.grigorij.ui.views.navigation.admin;

import com.gmail.grigorij.ui.MainLayout;
import com.gmail.grigorij.ui.components.detailsdrawer.DetailsDrawer;
import com.gmail.grigorij.ui.components.navigation.bar.AppBar;
import com.gmail.grigorij.ui.views.frames.SplitViewFrame;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;


@PageTitle("Admin")
@Route(value = "admin", layout = MainLayout.class)
public class AdminMain extends SplitViewFrame {

	private AppBar appBar;
	private Div content;
	private DetailsDrawer detailsDrawer;


	@Override
	protected void onAttach(AttachEvent attachEvent) {
		super.onAttach(attachEvent);
		initAppBar();
		setViewContent(createContent());
		handleContent();
	}


	private void initAppBar() {
		appBar = MainLayout.get().getAppBar();

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
//		this.detailsDrawerFooter.removeSaveListener();

		if (appBar.getSelectedTab() != null) {
			if (appBar.getSelectedTab().getLabel().equals(AdminCompanies.TAB_NAME)) {
				this.content.add(new AdminCompanies(this));
				return;
			}

			if (appBar.getSelectedTab().getLabel().equals(AdminPersonnel.TAB_NAME)) {
				this.content.add(new AdminPersonnel(this));
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


	public void setDetailsDrawer(DetailsDrawer detailsDrawer) {
		this.detailsDrawer = detailsDrawer;
		setViewDetails(detailsDrawer);
	}


//	private DetailsDrawer createDetailsDrawer() {
//		detailsDrawer = new DetailsDrawer(DetailsDrawer.Position.RIGHT);
//
//		// Header
//		DetailsDrawerHeader detailsDrawerTitle = new DetailsDrawerHeader("Details");
//
//		detailsDrawer.setHeader(detailsDrawerTitle);
//		detailsDrawer.getHeader().setFlexDirection(FlexDirection.COLUMN);
//
//		// Footer
//		detailsDrawerFooter = new DetailsDrawerFooter();
//		detailsDrawerFooter.addCancelListener(e -> detailsDrawer.hide());
//		detailsDrawer.setFooter(detailsDrawerFooter);
//
//		return detailsDrawer;
//	}
}
