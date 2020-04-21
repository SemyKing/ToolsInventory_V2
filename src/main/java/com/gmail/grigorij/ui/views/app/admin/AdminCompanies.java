package com.gmail.grigorij.ui.views.app.admin;

import com.gmail.grigorij.backend.database.entities.Company;
import com.gmail.grigorij.backend.database.entities.Transaction;
import com.gmail.grigorij.backend.database.entities.User;
import com.gmail.grigorij.backend.database.enums.operations.Operation;
import com.gmail.grigorij.backend.database.enums.operations.OperationTarget;
import com.gmail.grigorij.backend.database.enums.permissions.PermissionLevel;
import com.gmail.grigorij.backend.database.enums.permissions.PermissionRange;
import com.gmail.grigorij.backend.database.facades.CompanyFacade;
import com.gmail.grigorij.backend.database.facades.PermissionFacade;
import com.gmail.grigorij.backend.database.facades.TransactionFacade;
import com.gmail.grigorij.backend.database.facades.UserFacade;
import com.gmail.grigorij.ui.components.FlexBoxLayout;
import com.gmail.grigorij.ui.components.detailsdrawer.DetailsDrawer;
import com.gmail.grigorij.ui.components.detailsdrawer.DetailsDrawerFooter;
import com.gmail.grigorij.ui.components.detailsdrawer.DetailsDrawerHeader;
import com.gmail.grigorij.ui.components.dialogs.ConfirmDialog;
import com.gmail.grigorij.ui.components.forms.CompanyForm;
import com.gmail.grigorij.ui.utils.UIUtils;
import com.gmail.grigorij.ui.views.app.AdminWrapperView;
import com.gmail.grigorij.utils.authentication.AuthenticationService;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.value.ValueChangeMode;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;


public class AdminCompanies extends Div {

	private static final String CLASS_NAME = "admin-companies";
	private final CompanyForm companyForm = new CompanyForm();
	private final AdminWrapperView adminView;

	private Grid<Company> grid;
	private ListDataProvider<Company> dataProvider;

	private DetailsDrawer detailsDrawer;
	private boolean entityOldStatus;


	public AdminCompanies(AdminWrapperView adminView) {
		this.adminView = adminView;
		setClassName(CLASS_NAME);

		if (AuthenticationService.getCurrentSessionUser().getPermissionLevel().equalsTo(PermissionLevel.SYSTEM_ADMIN)) {

			Div wrapper = new Div();
			wrapper.addClassName(CLASS_NAME + "__wrapper");

			wrapper.add(constructHeader());
			wrapper.add(constructContent());

			add(wrapper);
			add(constructDetails());
		} else {
			add(constructCompanyAdminView());
		}
	}


	private Div constructHeader() {
		Div header = new Div();
		header.setClassName(CLASS_NAME + "__header");

		TextField searchField = new TextField();
		searchField.setClearButtonVisible(true);
		searchField.setPrefixComponent(VaadinIcon.SEARCH.create());
		searchField.setPlaceholder("Search Companies");
		searchField.setValueChangeMode(ValueChangeMode.LAZY);
		searchField.addValueChangeListener(event -> filterGrid(searchField.getValue()));

		header.add(searchField);

		MenuBar actionsMenuBar = new MenuBar();
		actionsMenuBar.addThemeVariants(MenuBarVariant.LUMO_PRIMARY, MenuBarVariant.LUMO_ICON);

		MenuItem menuItem = actionsMenuBar.addItem(new Icon(VaadinIcon.MENU));

		if (PermissionFacade.getInstance().isSystemAdminOrAllowedTo(Operation.ADD, OperationTarget.COMPANY, null)) {
			menuItem.getSubMenu().addItem("New Company", e -> {
				grid.deselectAll();
				showDetails(null);
			});
			menuItem.getSubMenu().add(new Hr());
		}

		if (PermissionFacade.getInstance().isSystemAdminOrAllowedTo(Operation.IMPORT, OperationTarget.COMPANY, null)) {
			menuItem.getSubMenu().addItem("Import", e -> {
				importCompanies();
			});
			menuItem.getSubMenu().add(new Hr());
		}

		if (PermissionFacade.getInstance().isSystemAdminOrAllowedTo(Operation.EXPORT, OperationTarget.COMPANY, null)) {
			menuItem.getSubMenu().addItem("Export", e -> {
				exportCompanies();
			});
		}

		header.add(actionsMenuBar);

		return header;
	}

	private Div constructContent() {
		Div content = new Div();
		content.setClassName(CLASS_NAME + "__content");

		content.add(constructGrid());

		return content;
	}

	private Grid constructGrid() {
		grid = new Grid<>();
		grid.setId("companies-grid");
		grid.setClassName("grid-view");
		grid.setSizeFull();


		if (AuthenticationService.getCurrentSessionUser().getPermissionLevel().equalsTo(PermissionLevel.SYSTEM_ADMIN)) {
			dataProvider = DataProvider.ofCollection(CompanyFacade.getInstance().getAllCompanies());
		} else {
			dataProvider = DataProvider.ofCollection(CompanyFacade.getInstance().getCompanyById(AuthenticationService.getCurrentSessionUser().getCompany().getId()));
		}

		grid.setDataProvider(dataProvider);

		grid.addColumn(Company::getName)
				.setHeader("Company Name")
				.setFlexGrow(1)
				.setAutoWidth(true);

		grid.addColumn(company -> (company.getContactPerson() == null) ? "" : company.getContactPerson().getFullName())
				.setHeader("Contact Person")
				.setFlexGrow(1)
				.setAutoWidth(true);

		grid.addColumn(company -> UIUtils.entityStatusToString(company.isDeleted()))
				.setHeader("Status")
				.setFlexGrow(0)
				.setTextAlign(ColumnTextAlign.END)
				.setAutoWidth(true);

		grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);

		grid.asSingleSelect().addValueChangeListener(e -> {
			Company company = grid.asSingleSelect().getValue();

			if (company != null) {
				showDetails(company);
			} else {
				detailsDrawer.hide();
			}
		});

		return grid;
	}

	private DetailsDrawer constructDetails() {
		detailsDrawer = new DetailsDrawer(DetailsDrawer.Position.RIGHT);

		DetailsDrawerHeader detailsDrawerHeader = new DetailsDrawerHeader("Company Details");
		detailsDrawerHeader.getClose().addClickListener(e -> closeDetails());
		detailsDrawer.setHeader(detailsDrawerHeader);

		detailsDrawer.setContent(companyForm);

		DetailsDrawerFooter detailsDrawerFooter = new DetailsDrawerFooter();
		detailsDrawerFooter.getSave().setEnabled(false);

		if (PermissionFacade.getInstance().isSystemAdminOrAllowedTo(Operation.EDIT, OperationTarget.COMPANY, null)) {
			detailsDrawerFooter.getSave().addClickListener(e -> {
				saveCompanyInDatabase(companyForm.getCompany(), companyForm.isNew(), companyForm.getChanges());
			});
			detailsDrawerFooter.getSave().setEnabled(true);
		}

		detailsDrawerFooter.getClose().addClickListener(e -> closeDetails());
		detailsDrawer.setFooter(detailsDrawerFooter);

		return detailsDrawer;
	}


	private void filterGrid(String searchString) {
		dataProvider.clearFilters();

		final String mainSearchString = searchString.trim();

		if (mainSearchString.contains("+")) {
			String[] searchParams = mainSearchString.split("\\+");

			dataProvider.addFilter(
					company -> {
						boolean res = true;
						for (String sParam : searchParams) {
							res =  matchesFilter(company, sParam);
							if (!res)
								break;
						}
						return res;
					}
			);
		} else {
			dataProvider.addFilter(
					company -> matchesFilter(company, mainSearchString)
			);
		}
	}

	private boolean matchesFilter(Company item, String filter) {
		return StringUtils.containsIgnoreCase(item.getName(), filter) ||
				StringUtils.containsIgnoreCase(item.getVat(), filter) ||
				StringUtils.containsIgnoreCase((item.getContactPersonString()), filter) ||
				StringUtils.containsIgnoreCase(item.getAddressString(), filter) ||
				StringUtils.containsIgnoreCase(UIUtils.entityStatusToString(item.isDeleted()), filter);
	}

	private void showDetails(Company company) {
		if (company != null) {
			entityOldStatus = company.isDeleted();
			detailsDrawer.setDeletedAttribute(company.isDeleted());
		} else {
			entityOldStatus = false;
			detailsDrawer.setDeletedAttribute(false);
		}


		companyForm.setCompany(company);
		detailsDrawer.show();
	}

	private void closeDetails() {
		detailsDrawer.hide();
		grid.deselectAll();
	}

	private void saveCompanyInDatabase(Company company, boolean isNew, List<String> changes) {
		if (company == null) {
			return;
		}

		if (isNew) {
			if (CompanyFacade.getInstance().insert(company)) {
				dataProvider.getItems().add(company);

				UIUtils.showNotification("Company created", NotificationVariant.LUMO_SUCCESS);
			} else {
				UIUtils.showNotification("Company insert failed", NotificationVariant.LUMO_ERROR);
				return;
			}
		} else {
			if (CompanyFacade.getInstance().update(company)) {
				UIUtils.showNotification("Company updated", NotificationVariant.LUMO_SUCCESS);

				if (Boolean.compare(entityOldStatus, company.isDeleted())!= 0) {
					confirmAllEmployeesInCompanyStatusChange(company);
				}
			} else {
				UIUtils.showNotification("Company update failed", NotificationVariant.LUMO_ERROR);
				return;
			}
		}

		Transaction transaction = new Transaction();
		transaction.setUser(AuthenticationService.getCurrentSessionUser());
		transaction.setCompany(AuthenticationService.getCurrentSessionUser().getCompany());
		transaction.setOperation(isNew ? Operation.ADD : Operation.EDIT);
		transaction.setOperationTarget1(OperationTarget.COMPANY);
		transaction.setTargetDetails(company.getName());
		transaction.setChanges(changes);
		TransactionFacade.getInstance().insert(transaction);

		if (isNew) {
			grid.select(company);
		}

		dataProvider.refreshAll();
	}

	/**
	 * Prompt to change status of all employees to company status
	 */
	private void confirmAllEmployeesInCompanyStatusChange(Company company) {

		ConfirmDialog dialog = new ConfirmDialog();
		dialog.setMessage("Would you like to set all employees in " + company.getName() + " as " + UIUtils.entityStatusToString(company.isDeleted()));
		dialog.closeOnCancel();
		dialog.getConfirmButton().addClickListener(e -> {

			List<User> employeesInSelectedCompany = UserFacade.getInstance().getAllActiveUsersInCompany(company.getId());
			boolean error = false;

			for (User user : employeesInSelectedCompany) {
				if (UserFacade.getInstance().handleUserStatusChange(user.getId(), company.isDeleted())) {

					Transaction transaction = new Transaction();
					transaction.setUser(AuthenticationService.getCurrentSessionUser());
					transaction.setCompany(AuthenticationService.getCurrentSessionUser().getCompany());
					transaction.setOperation(Operation.EDIT);
					transaction.setOperationTarget1(OperationTarget.USER);
					transaction.setOperationTarget2(OperationTarget.STATUS_T);
					transaction.setTargetDetails(user.getFullName());

					List<String> changes = new ArrayList<>();
					changes.add("Status changed from: '" + UIUtils.entityStatusToString(!company.isDeleted()) + "', to: '" +
							UIUtils.entityStatusToString(company.isDeleted()) + "'");

					transaction.setChanges(changes);
					TransactionFacade.getInstance().insert(transaction);
				} else {
					error = true;
				}
			}

			if (!error) {
				UIUtils.showNotification("All employees in " + company.getName() + " are now " + UIUtils.entityStatusToString(company.isDeleted()), NotificationVariant.LUMO_SUCCESS);
			}

			dialog.close();
		});
		dialog.open();
	}


	private void exportCompanies() {
		System.out.println("Export companies...");
	}

	private void importCompanies() {
		System.out.println("Import companies...");
	}




	// COMPANY ADMIN VIEW
	private Div constructCompanyAdminView() {
		Div companyAdminDiv = new Div();
		companyAdminDiv.addClassName(CLASS_NAME + "__company_admin");

		Div formWrapper = new Div();
		formWrapper.addClassName(CLASS_NAME + "__form-wrapper");

		companyForm.setCompany(AuthenticationService.getCurrentSessionUser().getCompany());
		formWrapper.add(companyForm);

		companyAdminDiv.add(formWrapper);

		DetailsDrawerFooter detailsDrawerFooter = new DetailsDrawerFooter();
		detailsDrawerFooter.getSave().setEnabled(false);

		if (PermissionFacade.getInstance().isSystemAdminOrAllowedTo(Operation.EDIT, OperationTarget.COMPANY, PermissionRange.OWN)) {
			detailsDrawerFooter.getSave().addClickListener(e -> {
				saveAdminCompanyInDatabase(companyForm.getCompany(), companyForm.getChanges());
			});
			detailsDrawerFooter.getSave().setEnabled(true);
		}

		detailsDrawerFooter.getContent().remove(detailsDrawerFooter.getClose());

		companyAdminDiv.add(detailsDrawerFooter);

		return companyAdminDiv;
	}

	private void saveAdminCompanyInDatabase(Company company, List<String> changes) {
		if (CompanyFacade.getInstance().update(company)) {
			UIUtils.showNotification("Company updated", NotificationVariant.LUMO_SUCCESS);

			Transaction transaction = new Transaction();
			transaction.setUser(AuthenticationService.getCurrentSessionUser());
			transaction.setCompany(AuthenticationService.getCurrentSessionUser().getCompany());
			transaction.setOperation(Operation.EDIT);
			transaction.setOperationTarget1(OperationTarget.COMPANY);
			transaction.setTargetDetails(company.getName());
			transaction.setChanges(changes);
			TransactionFacade.getInstance().insert(transaction);

		} else {
			UIUtils.showNotification("Company update failed", NotificationVariant.LUMO_ERROR);
		}
	}
}