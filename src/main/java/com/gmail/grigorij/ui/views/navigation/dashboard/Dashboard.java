package com.gmail.grigorij.ui.views.navigation.dashboard;

import com.gmail.grigorij.backend.database.facades.CompanyFacade;
import com.gmail.grigorij.backend.database.facades.InventoryFacade;
import com.gmail.grigorij.backend.database.facades.UserFacade;
import com.gmail.grigorij.backend.entities.company.Company;
import com.gmail.grigorij.backend.entities.inventory.InventoryItem;
import com.gmail.grigorij.backend.entities.user.User;
import com.gmail.grigorij.backend.enums.inventory.InventoryHierarchyType;
import com.gmail.grigorij.backend.enums.permissions.PermissionLevel;
import com.gmail.grigorij.ui.components.DashboardItem;
import com.gmail.grigorij.ui.components.Divider;
import com.gmail.grigorij.ui.components.FlexBoxLayout;
import com.gmail.grigorij.ui.components.ListItem;
import com.gmail.grigorij.ui.utils.UIUtils;
import com.gmail.grigorij.ui.utils.css.BoxShadowBorders;
import com.gmail.grigorij.ui.utils.css.FlexDirection;
import com.gmail.grigorij.ui.components.frames.ViewFrame;
import com.gmail.grigorij.ui.utils.css.Shadow;
import com.gmail.grigorij.ui.utils.css.size.Vertical;
import com.gmail.grigorij.utils.AuthenticationService;
import com.gmail.grigorij.utils.ProjectConstants;
import com.vaadin.flow.component.dependency.HtmlImport;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.PageTitle;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;


@PageTitle("Dashboard")
@HtmlImport("styles/views/dashboard.html")
public class Dashboard extends ViewFrame {

	private final static String CLASS_NAME = "dashboard";

	public Dashboard() {
		setId("dashboard");
		setViewContent(createContent());
	}

	private FlexBoxLayout createContent() {

		if (AuthenticationService.getCurrentSessionUser().getAccessGroup().getPermissionLevel().equalsTo(PermissionLevel.SYSTEM)) {
			return constructSystemAdminDashBoard();
		}
		if (AuthenticationService.getCurrentSessionUser().getAccessGroup().getPermissionLevel().equalsTo(PermissionLevel.COMPANY)) {
			return constructCompanyAdminDashBoard();
		}
		if (AuthenticationService.getCurrentSessionUser().getAccessGroup().getPermissionLevel().equalsTo(PermissionLevel.OWN)) {
			return constructEmployeeDashBoard();
		}

		// USERS WITHOUT PERMISSION LEVEL -> SHOULD BE IMPOSSIBLE
		FlexBoxLayout content = new FlexBoxLayout();
		content.setClassName(CLASS_NAME + "__content");
		content.add(new Span("Hello Stranger!"));

		return content;
	}

	private FlexBoxLayout constructSystemAdminDashBoard() {
		FlexBoxLayout content = new FlexBoxLayout();
		content.setClassName(CLASS_NAME + "__content");
		content.add(UIUtils.createH2Label(ProjectConstants.PROJECT_NAME_FULL.toUpperCase()));


		List<Company> companies = CompanyFacade.getInstance().getAllCompanies();
		List<User> users = UserFacade.getInstance().getAllUsers();
		List<InventoryItem> tools = InventoryFacade.getInstance().getAllByHierarchyType(InventoryHierarchyType.TOOL);


		// 1st ROW
		FlexBoxLayout firstRowLayout = getContentRowLayout();

		// COMPANIES
		DashboardItem companiesItem = new DashboardItem();
		companiesItem.getHeader().add(new ListItem(VaadinIcon.OFFICE.create(), "TOTAL COMPANIES"));
		companiesItem.getContent().add(new Span("" + companies.size()));
//		companiesItem.getFooter().add(companiesDetails);

		firstRowLayout.add(companiesItem);


		// USERS
		FlexBoxLayout usersLayout = new FlexBoxLayout();
		usersLayout.setClassName(CLASS_NAME + "__details_layout");
		usersLayout.setFlexDirection(FlexDirection.COLUMN);
		usersLayout.add(new HorizontalLayout(UIUtils.createH5Label("Company"), UIUtils.createH5Label("Employees")));

		for (Company company : companies) {
			AtomicLong userCount = new AtomicLong();
			users.forEach(user -> {
				if (user.getCompany() != null) {
					if (user.getCompany().getId().equals(company.getId())) {
						userCount.getAndIncrement();
//						users.remove(user);
					}
				}
			});
			usersLayout.add(new Divider(1, Vertical.XS));
			usersLayout.add(new HorizontalLayout(UIUtils.createH6Label(company.getName()), UIUtils.createH6Label(userCount.toString())));
		}

		Details usersDetails = new Details("Details", usersLayout);

		DashboardItem usersItem = new DashboardItem();
		usersItem.getHeader().add(new ListItem(VaadinIcon.USER.create(), "TOTAL USERS"));
		usersItem.getContent().add(new Span("" + users.size()));
		usersItem.getFooter().add(usersDetails);

		firstRowLayout.add(usersItem);


		// TOOLS
		FlexBoxLayout toolsLayout = new FlexBoxLayout();
		toolsLayout.setClassName(CLASS_NAME + "__details_layout");
		toolsLayout.setFlexDirection(FlexDirection.COLUMN);
		toolsLayout.add(new HorizontalLayout(UIUtils.createH5Label("Company"), UIUtils.createH5Label("Tools")));

		for (Company company : companies) {
			AtomicLong toolsCount = new AtomicLong();
			tools.forEach(tool -> {
				if (tool.getCompany() != null) {
					if (tool.getCompany().getId().equals(company.getId())) {
						toolsCount.getAndIncrement();
					}
				}
			});
			toolsLayout.add(new Divider(1, Vertical.XS));
			toolsLayout.add(new HorizontalLayout(UIUtils.createH6Label(company.getName()), UIUtils.createH6Label(toolsCount.toString())));
		}

		Details toolsDetails = new Details("Details", toolsLayout);

		DashboardItem toolsItem = new DashboardItem();
		toolsItem.getHeader().add(new ListItem(VaadinIcon.TOOLS.create(), "TOTAL TOOLS"));
		toolsItem.getContent().add(new Span("" + tools.size()));
		toolsItem.getFooter().add(toolsDetails);

		firstRowLayout.add(toolsItem);



		// 2nd ROW
		FlexBoxLayout secondRowLayout = getContentRowLayout();

		// TOOLS
		DashboardItem tools2Item = new DashboardItem();
		tools2Item.getHeader().add(new ListItem(VaadinIcon.TOOLBOX.create(), "2 TOTAL TOOLS"));
		tools2Item.getContent().add(new Span("" + tools.size()));

		secondRowLayout.add(tools2Item);

		// TOOLS
		DashboardItem tools3Item = new DashboardItem();
		tools3Item.getHeader().add(new ListItem(VaadinIcon.TOOLBOX.create(), "3 TOTAL TOOLS"));
		tools3Item.getContent().add(new Span("" + tools.size()));

		secondRowLayout.add(tools3Item);

		content.add(firstRowLayout);
		content.add(secondRowLayout);

		return content;
	}

	private FlexBoxLayout constructCompanyAdminDashBoard() {
		FlexBoxLayout content = new FlexBoxLayout();
		content.setClassName(CLASS_NAME + "__content");
		content.add(UIUtils.createH2Label(AuthenticationService.getCurrentSessionUser().getCompany().getName().toUpperCase()));

		// 1st ROW
		FlexBoxLayout firstRowLayout = getContentRowLayout();

		DashboardItem item = new DashboardItem();
//		item.getHeader().add();
		item.getContent().add(new Span("99999"));
		item.getFooter().add(new ListItem(VaadinIcon.EXCLAMATION.create(), "DAYS WITHOUT AN ACCIDENT"));

		firstRowLayout.add(item);


		content.add(firstRowLayout);

		return content;
	}

	private FlexBoxLayout constructEmployeeDashBoard() {
		FlexBoxLayout content = new FlexBoxLayout();
		content.setClassName(CLASS_NAME + "__content");
		content.add(UIUtils.createH2Label(AuthenticationService.getCurrentSessionUser().getCompany().getName().toUpperCase()));

		return content;
	}



	private FlexBoxLayout getContentRowLayout() {
		FlexBoxLayout contentRowLayout = new FlexBoxLayout();
		contentRowLayout.setClassName(CLASS_NAME + "__content_row_layout");
		contentRowLayout.setFlexDirection(FlexDirection.ROW);
		return contentRowLayout;
	}
}
