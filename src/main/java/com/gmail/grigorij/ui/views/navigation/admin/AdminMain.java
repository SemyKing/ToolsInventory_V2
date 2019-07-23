package com.gmail.grigorij.ui.views.navigation.admin;

import com.gmail.grigorij.ui.utils.UIUtils;
import com.gmail.grigorij.ui.utils.components.FlexBoxLayout;
import com.gmail.grigorij.ui.utils.css.FlexDirection;
import com.gmail.grigorij.ui.views.MenuLayout;
import com.gmail.grigorij.ui.utils.components.detailsdrawer.DetailsDrawer;
import com.gmail.grigorij.ui.utils.components.navigation.bar.AppBar;
import com.gmail.grigorij.ui.utils.components.frames.SplitViewFrame;
import com.gmail.grigorij.ui.views.navigation.admin.companies.AdminCompanies;
import com.gmail.grigorij.ui.views.navigation.admin.inventory.AdminInventory;
import com.gmail.grigorij.ui.views.navigation.admin.personnel.AdminPersonnel;
import com.gmail.grigorij.ui.views.navigation.admin.transactions.AdminTransactions;
import com.gmail.grigorij.utils.ProjectConstants;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.tabs.TabsVariant;
import com.vaadin.flow.router.PageTitle;


@PageTitle("Admin")
public class AdminMain extends SplitViewFrame {

	private static final String CLASS_NAME = "admin-main";

	private AppBar appBar;
	private Div content;
	private DetailsDrawer detailsDrawer;

	private final MenuLayout menuLayout;

	public AdminMain(MenuLayout menuLayout) {
		this.menuLayout = menuLayout;

		initAppBar();
		setViewContent(createContent());
		handleContent();
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
			handleContent();
		});
		appBar.centerTabs();
	}

	private void handleContent() {
		this.content.removeAll();

		if (appBar.getSelectedTab() != null) {
			menuLayout.selectCorrectNaviItem(appBar.getSelectedTab().getLabel(), true);

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

	public FlexBoxLayout constructOptionsButton() {
		FlexBoxLayout layout = new FlexBoxLayout();
		layout.setClassName(CLASS_NAME + "__options-button");
		layout.setFlexDirection(FlexDirection.ROW);
		layout.setAlignItems(FlexComponent.Alignment.CENTER);

		Icon leftIcon = VaadinIcon.ELLIPSIS_DOTS_V.create();
		leftIcon.setClassName(CLASS_NAME + "__options-button-li");
		Icon  rightIcon = VaadinIcon.CARET_DOWN.create();
		rightIcon.setClassName(CLASS_NAME + "__options-button-ri");

		Label textLabel = UIUtils.createH4Label("Options");
		textLabel.setClassName(CLASS_NAME + "__options-button-tl");

		layout.add(leftIcon, textLabel, rightIcon);

		return layout;
	}
}