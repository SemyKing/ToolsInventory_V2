package com.gmail.grigorij.ui.application.views;

import com.gmail.grigorij.backend.database.facades.CompanyFacade;
import com.gmail.grigorij.backend.database.facades.InventoryFacade;
import com.gmail.grigorij.backend.database.facades.UserFacade;
import com.gmail.grigorij.backend.database.entities.Company;
import com.gmail.grigorij.backend.database.entities.Tool;
import com.gmail.grigorij.backend.database.entities.User;
import com.gmail.grigorij.backend.database.enums.permissions.PermissionLevel;
import com.gmail.grigorij.ui.components.DashboardItem;
import com.gmail.grigorij.ui.components.layouts.FlexBoxLayout;
import com.gmail.grigorij.ui.components.ListItem;
import com.gmail.grigorij.ui.components.layouts.ViewFrame;
import com.gmail.grigorij.ui.utils.UIUtils;
import com.gmail.grigorij.ui.utils.css.FlexDirection;
import com.gmail.grigorij.utils.AuthenticationService;
import com.gmail.grigorij.utils.ProjectConstants;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;


@CssImport("./styles/views/dashboard.css")
public class DashboardView extends ViewFrame {

	private final static String CLASS_NAME = "dashboard";

	public DashboardView() {
		setId("dashboard");
		setViewContent(createContent());
	}

	private FlexBoxLayout createContent() {

		if (AuthenticationService.getCurrentSessionUser().getPermissionLevel().equalsTo(PermissionLevel.SYSTEM_ADMIN)) {
			return constructSystemAdminDashBoard();
		}
		if (AuthenticationService.getCurrentSessionUser().getPermissionLevel().equalsTo(PermissionLevel.COMPANY_ADMIN)) {
			return constructCompanyAdminDashBoard();
		}
		if (AuthenticationService.getCurrentSessionUser().getPermissionLevel().equalsTo(PermissionLevel.USER)) {
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
		List<Tool> tools = InventoryFacade.getInstance().getAllTools();


		// 1st ROW
		FlexBoxLayout firstRowLayout = getContentRowLayout();

		// COMPANIES
		DashboardItem companiesItem = new DashboardItem();
		companiesItem.getHeader().add(new ListItem(VaadinIcon.OFFICE.create(), "TOTAL COMPANIES"));
		companiesItem.getContent().add(new Span("" + companies.size()));
//		companiesItem.getFooter().add();

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
					}
				}
			});
			usersLayout.add(new Hr());
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
			toolsLayout.add(new Hr());
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



	private FlexBoxLayout getContentRowLayout() {
		FlexBoxLayout contentRowLayout = new FlexBoxLayout();
		contentRowLayout.setClassName(CLASS_NAME + "__content_row_layout");
		contentRowLayout.setFlexDirection(FlexDirection.ROW);
		return contentRowLayout;
	}
}
