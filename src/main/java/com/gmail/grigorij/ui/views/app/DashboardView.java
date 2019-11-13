package com.gmail.grigorij.ui.views.app;

import com.gmail.grigorij.backend.database.entities.Company;
import com.gmail.grigorij.backend.database.entities.Tool;
import com.gmail.grigorij.backend.database.entities.Transaction;
import com.gmail.grigorij.backend.database.entities.User;
import com.gmail.grigorij.backend.database.enums.operations.Operation;
import com.gmail.grigorij.backend.database.enums.operations.OperationTarget;
import com.gmail.grigorij.backend.database.enums.permissions.PermissionLevel;
import com.gmail.grigorij.backend.database.enums.tools.ToolUsageStatus;
import com.gmail.grigorij.backend.database.facades.CompanyFacade;
import com.gmail.grigorij.backend.database.facades.InventoryFacade;
import com.gmail.grigorij.backend.database.facades.TransactionFacade;
import com.gmail.grigorij.backend.database.facades.UserFacade;
import com.gmail.grigorij.ui.components.DashboardItem;
import com.gmail.grigorij.ui.utils.UIUtils;
import com.gmail.grigorij.utils.AuthenticationService;
import com.gmail.grigorij.utils.ProjectConstants;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextArea;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;


@CssImport("./styles/views/dashboard.css")
public class DashboardView extends Div {

	private final static String CLASS_NAME = "dashboard";
	private final static String ANNOUNCEMENTS_ROW = "__announcements-row";
	private final static String ITEMS_ROW = "__items-row";
	private final static String AMOUNT_SPAN = "amount-span";
	private final static String TITLE_SPAN = "title-span";

	private Company currentCompany;
	private Div content;


	public DashboardView() {
		addClassName(CLASS_NAME);

		add(constructContent());
	}


	private Div constructContent() {
		content = new Div();
		content.setClassName(CLASS_NAME + "__content");

		currentCompany = AuthenticationService.getCurrentSessionUser().getCompany();

		if (AuthenticationService.getCurrentSessionUser().getPermissionLevel().equalsTo(PermissionLevel.SYSTEM_ADMIN)) {
			return constructSystemAdminDashboard();
		}

		if (AuthenticationService.getCurrentSessionUser().getPermissionLevel().equalsTo(PermissionLevel.COMPANY_ADMIN)) {
			return constructCompanyAdminDashboard();
		}

		return constructEmployeeDashboard();
	}


	private Div constructSystemAdminDashboard() {
		content.add(UIUtils.createH2Label(ProjectConstants.PROJECT_NAME_FULL.toUpperCase()));

		List<Company> companies = CompanyFacade.getInstance().getAllActiveCompanies();
		List<User> users = UserFacade.getInstance().getAllActiveUsers();
		List<Tool> tools = InventoryFacade.getInstance().getAllActiveTools();


		Div announcementsDiv = new Div();
		announcementsDiv.addClassName(CLASS_NAME + ANNOUNCEMENTS_ROW);

		Div systemAnnouncementsDiv = getAnnouncementDiv("System Announcement",
				currentCompany.getAnnouncements(), true);

		announcementsDiv.add(systemAnnouncementsDiv);



		// 2nd ROW
		Div itemsDiv = new Div();
		itemsDiv.addClassName(CLASS_NAME + ITEMS_ROW);


		// COMPANIES
		DashboardItem companiesItem = new DashboardItem();
		companiesItem.getContentLeft().add(getSpan(String.valueOf(companies.size()), AMOUNT_SPAN));
		companiesItem.getContentLeft().add(getSpan("ACTIVE COMPANIES", TITLE_SPAN));
		companiesItem.getContentRight().add(VaadinIcon.OFFICE.create());
		itemsDiv.add(companiesItem);


		// USERS
		Div usersLayout = new Div();
		usersLayout.setClassName(CLASS_NAME + "__details_layout");
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
		usersItem.getContentLeft().add(getSpan(String.valueOf(users.size()), AMOUNT_SPAN));
		usersItem.getContentLeft().add(getSpan("ACTIVE USERS", TITLE_SPAN));
		usersItem.getContentRight().add(VaadinIcon.USER_CARD.create());
		usersItem.getFooter().add(usersDetails);

		itemsDiv.add(usersItem);


		// TOOLS
		Div toolsLayout = new Div();
		toolsLayout.setClassName(CLASS_NAME + "__details_layout");
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
		toolsItem.getContentLeft().add(getSpan(String.valueOf(tools.size()), AMOUNT_SPAN));
		toolsItem.getContentLeft().add(getSpan("ACTIVE TOOLS", TITLE_SPAN));
		toolsItem.getContentRight().add(VaadinIcon.TOOLS.create());
		toolsItem.getFooter().add(toolsDetails);

		itemsDiv.add(toolsItem);



		content.add(announcementsDiv);
		content.add(itemsDiv);

		return content;
	}

	private Div constructCompanyAdminDashboard() {
		content.add(UIUtils.createH2Label(currentCompany.getName().toUpperCase()));

		// 1st ROW
		Div announcementsDiv = new Div();
		announcementsDiv.addClassName(CLASS_NAME + ANNOUNCEMENTS_ROW);


		if (CompanyFacade.getInstance().getAdministrationCompany() == null) {
			System.err.println("NULL ADMINISTRATION COMPANY");
		} else {
			String systemAnnouncement = CompanyFacade.getInstance().getAdministrationCompany().getAnnouncements();

			if (systemAnnouncement.length() > 0) {
				announcementsDiv.add(getAnnouncementDiv("System Announcement", systemAnnouncement, false));
			}
		}

		announcementsDiv.add(getAnnouncementDiv("Company Announcement",
				currentCompany.getAnnouncements(), true));



		// 2nd ROW
		Div itemsDiv = new Div();
		itemsDiv.addClassName(CLASS_NAME + ITEMS_ROW);


		// USERS IN COMPANY
		DashboardItem usersItem = new DashboardItem();

		long userCount = UserFacade.getInstance().getAllActiveUsersInCompany(currentCompany.getId()).size();

		usersItem.getContentLeft().add(getSpan(String.valueOf(userCount), AMOUNT_SPAN));
		usersItem.getContentLeft().add(getSpan("TOTAL EMPLOYEES", TITLE_SPAN));
		usersItem.getContentRight().add(VaadinIcon.SPECIALIST.create());

		itemsDiv.add(usersItem);


		// TOOLS IN COMPANY
		List<Tool> tools = InventoryFacade.getInstance().getAllActiveToolsInCompany(currentCompany.getId());

		long toolsInUse = 0;
		long toolsReserved = 0;
		long toolsInUseAndReserved = 0;
		long toolsFree = 0;
		long toolsLost = 0;
		long toolsBroken = 0;

		for (Tool tool : tools) {
			switch (tool.getUsageStatus()) {
				case IN_USE:
					toolsInUse++;
					break;
				case RESERVED:
					toolsReserved++;
					break;
				case IN_USE_AND_RESERVED:
					toolsInUseAndReserved++;
					break;
				case FREE:
					toolsFree++;
					break;
				case LOST:
					toolsLost++;
					break;
				case BROKEN:
					toolsBroken++;
					break;
			}
		}

		Div toolsLayout = new Div();
		toolsLayout.setClassName(CLASS_NAME + "__details_layout");
		toolsLayout.add(new HorizontalLayout(UIUtils.createH5Label("Status"), UIUtils.createH5Label("Amount")));

		toolsLayout.add(new Hr());
		toolsLayout.add(new HorizontalLayout(UIUtils.createH6Label(ToolUsageStatus.IN_USE.getName()),
				UIUtils.createH6Label(String.valueOf(toolsInUse))));
		toolsLayout.add(new Hr());
		toolsLayout.add(new HorizontalLayout(UIUtils.createH6Label(ToolUsageStatus.RESERVED.getName()),
				UIUtils.createH6Label(String.valueOf(toolsReserved))));
		toolsLayout.add(new Hr());
		toolsLayout.add(new HorizontalLayout(UIUtils.createH6Label(ToolUsageStatus.IN_USE_AND_RESERVED.getName()),
				UIUtils.createH6Label(String.valueOf(toolsInUseAndReserved))));
		toolsLayout.add(new Hr());
		toolsLayout.add(new HorizontalLayout(UIUtils.createH6Label(ToolUsageStatus.FREE.getName()),
				UIUtils.createH6Label(String.valueOf(toolsFree))));
		toolsLayout.add(new Hr());
		toolsLayout.add(new HorizontalLayout(UIUtils.createH6Label(ToolUsageStatus.LOST.getName()),
				UIUtils.createH6Label(String.valueOf(toolsLost))));
		toolsLayout.add(new Hr());
		toolsLayout.add(new HorizontalLayout(UIUtils.createH6Label(ToolUsageStatus.BROKEN.getName()),
				UIUtils.createH6Label(String.valueOf(toolsBroken))));

		Details toolsDetails = new Details("Details", toolsLayout);

		DashboardItem toolsItem = new DashboardItem();
		toolsItem.getContentLeft().add(getSpan(String.valueOf(tools.size()), AMOUNT_SPAN));
		toolsItem.getContentLeft().add(getSpan("TOTAL TOOLS", TITLE_SPAN));
		toolsItem.getContentRight().add(VaadinIcon.TOOLS.create());
		toolsItem.getFooter().add(toolsDetails);

		itemsDiv.add(toolsItem);

		// LOCATIONS
		DashboardItem locationsItem = new DashboardItem();
		locationsItem.getContentLeft().add(getSpan(String.valueOf(currentCompany.getLocations().size()), AMOUNT_SPAN));
		locationsItem.getContentLeft().add(getSpan("TOTAL LOCATIONS", TITLE_SPAN));
		locationsItem.getContentRight().add(VaadinIcon.MAP_MARKER.create());

		itemsDiv.add(locationsItem);


		content.add(announcementsDiv);
		content.add(itemsDiv);

		return content;
	}

	private Div constructEmployeeDashboard() {
		content.add(UIUtils.createH2Label(AuthenticationService.getCurrentSessionUser().getCompany().getName().toUpperCase()));

		// 1st ROW
		Div announcementsDiv = new Div();
		announcementsDiv.addClassName(CLASS_NAME + ANNOUNCEMENTS_ROW);


		if (CompanyFacade.getInstance().getAdministrationCompany() == null) {
			System.err.println("NULL ADMINISTRATION COMPANY");
		} else {
			String systemAnnouncement = CompanyFacade.getInstance().getAdministrationCompany().getAnnouncements();

			if (systemAnnouncement.length() > 0) {
				announcementsDiv.add(getAnnouncementDiv("System Announcement", systemAnnouncement, false));
			}
		}

		String companyAnnouncement = AuthenticationService.getCurrentSessionUser().getCompany().getAnnouncements();

		if (companyAnnouncement.length() > 0) {
			announcementsDiv.add(getAnnouncementDiv("Company Announcement", companyAnnouncement, false));
		}


		// 2nd ROW
		Div itemsDiv = new Div();
		itemsDiv.addClassName(CLASS_NAME + ITEMS_ROW);

		// TOOLS IN USE BY USER
		long toolsInUseByUserCount = InventoryFacade.getInstance().getAllToolsByCurrentUserId(AuthenticationService.getCurrentSessionUser().getId()).size();

		DashboardItem toolsInUseItem = new DashboardItem();
		toolsInUseItem.getContentLeft().add(getSpan(String.valueOf(toolsInUseByUserCount), AMOUNT_SPAN));
		toolsInUseItem.getContentLeft().add(getSpan("TOOLS IN USE BY YOU", TITLE_SPAN));
		toolsInUseItem.getContentRight().add(VaadinIcon.WRENCH.create());

		itemsDiv.add(toolsInUseItem);


		content.add(announcementsDiv);
		content.add(itemsDiv);

		return content;
	}


	private Span getSpan(String text, String className) {
		Span span = new Span(text);
		span.addClassName(className);
		return span;
	}

	private Div getAnnouncementDiv(String title, String message, boolean editable) {
		Div announcementDiv = new Div();
		announcementDiv.addClassName(CLASS_NAME + "__announcement");


		TextArea textArea = new TextArea();
		textArea.setLabel(title);
		textArea.setValue(message);

		announcementDiv.add(textArea);

		if (editable) {
			Button saveButton = UIUtils.createButton("Save", ButtonVariant.LUMO_PRIMARY);
			saveButton.addClickListener(e -> {
				currentCompany.setAnnouncements(textArea.getValue());

				if (CompanyFacade.getInstance().update(currentCompany)) {

					Transaction transaction = new Transaction();
					transaction.setUser(AuthenticationService.getCurrentSessionUser());
					transaction.setCompany(currentCompany);
					transaction.setOperation(Operation.EDIT);
					transaction.setOperationTarget1(OperationTarget.ANNOUNCEMENT);
					TransactionFacade.getInstance().insert(transaction);

					UIUtils.showNotification("Announcement saved", NotificationVariant.LUMO_SUCCESS);
				} else {
					UIUtils.showNotification("Announcement save error", NotificationVariant.LUMO_ERROR);
				}
			});
			announcementDiv.add(saveButton);

		} else {
			textArea.setReadOnly(true);
			announcementDiv.add(textArea);
		}

		return announcementDiv;
	}
}
