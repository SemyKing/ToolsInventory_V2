package com.gmail.grigorij.ui.application.views.admin;

import com.gmail.grigorij.backend.database.facades.CompanyFacade;
import com.gmail.grigorij.backend.database.facades.TransactionFacade;
import com.gmail.grigorij.backend.database.facades.UserFacade;
import com.gmail.grigorij.backend.entities.company.Company;
import com.gmail.grigorij.backend.entities.transaction.Transaction;
import com.gmail.grigorij.backend.entities.user.User;
import com.gmail.grigorij.backend.enums.transactions.TransactionTarget;
import com.gmail.grigorij.backend.enums.transactions.TransactionType;
import com.gmail.grigorij.ui.utils.UIUtils;
import com.gmail.grigorij.ui.components.dialogs.ConfirmDialog;
import com.gmail.grigorij.ui.components.layouts.FlexBoxLayout;
import com.gmail.grigorij.ui.components.detailsdrawer.DetailsDrawer;
import com.gmail.grigorij.ui.components.detailsdrawer.DetailsDrawerFooter;
import com.gmail.grigorij.ui.components.detailsdrawer.DetailsDrawerHeader;
import com.gmail.grigorij.ui.utils.css.Display;
import com.gmail.grigorij.ui.utils.css.FlexDirection;
import com.gmail.grigorij.ui.utils.css.size.*;
import com.gmail.grigorij.ui.components.forms.editable.EditableCompanyForm;
import com.gmail.grigorij.utils.AuthenticationService;
import com.gmail.grigorij.utils.ProjectConstants;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import org.apache.commons.lang3.StringUtils;

import java.util.List;


class AdminCompanies extends FlexBoxLayout {

	private static final String CLASS_NAME = "admin-companies";

	private final AdminContainerView adminMain;
	private EditableCompanyForm companyForm = new EditableCompanyForm();


	private Grid<Company> grid;
	private ListDataProvider<Company> dataProvider;

	private DetailsDrawer detailsDrawer;


	AdminCompanies(AdminContainerView adminMain) {
		this.adminMain = adminMain;
		setClassName(CLASS_NAME);
		setSizeFull();
		setDisplay(Display.FLEX);
		setFlexDirection(FlexDirection.COLUMN);

		createHeader();
		createGrid();
		createDetailsDrawer();
	}

	private void createHeader() {
		FlexBoxLayout header = new FlexBoxLayout();
		header.setClassName(CLASS_NAME + "__header");
		header.setMargin(Top.S);
		header.setAlignItems(Alignment.BASELINE);
		header.setWidthFull();

		TextField searchField = new TextField();
		searchField.setWidth("100%");
		searchField.setClearButtonVisible(true);
		searchField.setPrefixComponent(VaadinIcon.SEARCH.create());
		searchField.setPlaceholder("Search Companies");
		searchField.setValueChangeMode(ValueChangeMode.LAZY);
		searchField.addValueChangeListener(event -> filterGrid(searchField.getValue()));

		header.add(searchField);
		header.setComponentMargin(searchField, Right.S);


		Div actionsButton = new Div();
		actionsButton.addClassName("hiding-text-menu-bar");
		actionsButton.add(VaadinIcon.MENU.create());
		actionsButton.add(new Span("Options"));


		MenuBar actionsMenuBar = new MenuBar();
		actionsMenuBar.addThemeVariants(MenuBarVariant.LUMO_TERTIARY, MenuBarVariant.LUMO_CONTRAST);

		MenuItem menuItem = actionsMenuBar.addItem(actionsButton);

		menuItem.getSubMenu().addItem("New Company", e -> {
			grid.select(null);
			showDetails(null);
		});
		menuItem.getSubMenu().add(new Hr());
		menuItem.getSubMenu().addItem("Import", e -> {
			importCompanies();
		});
		menuItem.getSubMenu().add(new Hr());
		menuItem.getSubMenu().addItem("Export", e -> {
			exportCompanies();
		});

		header.add(actionsMenuBar);

		add(header);
	}

	private void createGrid() {
		grid = new Grid<>();
		grid.setId("companies-grid");
		grid.setClassName("grid-view");
		grid.setSizeFull();
		grid.asSingleSelect().addValueChangeListener(e -> {
			if (grid.asSingleSelect().getValue() != null) {
				showDetails(grid.asSingleSelect().getValue());
			} else {
				detailsDrawer.hide();
			}
		});

		dataProvider = DataProvider.ofCollection(CompanyFacade.getInstance().getAllCompanies());
		grid.setDataProvider(dataProvider);

		grid.addColumn(Company::getName)
				.setHeader("Company Name")
				.setAutoWidth(true);

		grid.addColumn(company -> (company.getContactPerson() == null) ? "" : company.getContactPerson().getFullName())
				.setHeader("Contact Person")
				.setAutoWidth(true);

		grid.addColumn(new ComponentRenderer<>(selectedCompany -> UIUtils.createActiveGridIcon(selectedCompany.isDeleted())))
				.setHeader("Active")
				.setAutoWidth(true);

		add(grid);
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
							res =  StringUtils.containsIgnoreCase(company.getName(), sParam) ||
									StringUtils.containsIgnoreCase(company.getVat(), sParam) ||
									StringUtils.containsIgnoreCase((company.getContactPerson() == null) ? "" : company.getContactPerson().getFirstName(), sParam) ||
									StringUtils.containsIgnoreCase((company.getContactPerson() == null) ? "" : company.getContactPerson().getLastName(), sParam) ||
									StringUtils.containsIgnoreCase((company.getContactPerson() == null) ? "" : company.getContactPerson().getEmail(), sParam);

							//(res) -> shows All items based on searchParams
							//(!res) -> shows ONE item based on searchParams
							if (!res)
								break;
						}
						return res;
					}
			);
		} else {
			dataProvider.addFilter(
					company -> StringUtils.containsIgnoreCase(company.getName(), mainSearchString)  ||
							StringUtils.containsIgnoreCase(company.getVat(), mainSearchString) ||
							StringUtils.containsIgnoreCase((company.getContactPerson() == null) ? "" : company.getContactPerson().getFirstName(), mainSearchString) ||
							StringUtils.containsIgnoreCase((company.getContactPerson() == null) ? "" : company.getContactPerson().getLastName(), mainSearchString) ||
							StringUtils.containsIgnoreCase((company.getContactPerson() == null) ? "" : company.getContactPerson().getEmail(), mainSearchString)
			);
		}
	}

	private void createDetailsDrawer() {
		detailsDrawer = new DetailsDrawer(DetailsDrawer.Position.RIGHT);

		// Header
		DetailsDrawerHeader detailsDrawerHeader = new DetailsDrawerHeader("Company Details");
		detailsDrawerHeader.getClose().addClickListener(e -> closeDetails());

		detailsDrawer.setHeader(detailsDrawerHeader);

		// Content
		detailsDrawer.setContent(companyForm);
		detailsDrawer.setContentPadding(Left.M, Right.S);

		// Footer
		DetailsDrawerFooter detailsDrawerFooter = new DetailsDrawerFooter();
		detailsDrawerFooter.getSave().addClickListener(e -> updateCompany());
		detailsDrawerFooter.getCancel().addClickListener(e -> closeDetails());
		detailsDrawer.setFooter(detailsDrawerFooter);

		adminMain.setDetailsDrawer(detailsDrawer);
	}


	private boolean previousStatus;

	private void showDetails(Company company) {
		if (company != null) {
			previousStatus = company.isDeleted();
		}

		companyForm.setCompany(company);
		detailsDrawer.show();

//		UIUtils.updateFormSize(companyForm);
	}

	private void closeDetails() {
		detailsDrawer.hide();
		grid.select(null);
	}

	private void updateCompany() {
		System.out.println("updateCompany()");

		Company editedCompany = companyForm.getCompany();

		if (editedCompany != null) {

			if (companyForm.isNew()) {
				if (CompanyFacade.getInstance().insert(editedCompany)) {
					dataProvider.getItems().add(editedCompany);
					dataProvider.refreshAll();
					UIUtils.showNotification("Company created successfully", UIUtils.NotificationType.SUCCESS);

					Transaction tr = new Transaction();
					tr.setTransactionOperation(TransactionType.ADD);
					tr.setTransactionTarget(TransactionTarget.COMPANY);
					tr.setCompany(editedCompany);
					tr.setWhoDid(AuthenticationService.getCurrentSessionUser());
					TransactionFacade.getInstance().insert(tr);

				} else {
					UIUtils.showNotification("Company insert failed", UIUtils.NotificationType.ERROR);
				}
			} else {
				if (CompanyFacade.getInstance().update(editedCompany)) {
					if (grid.asSingleSelect().getValue() != null) {
						dataProvider.refreshItem(grid.asSingleSelect().getValue());
					}

					UIUtils.showNotification("Company updated successfully", UIUtils.NotificationType.SUCCESS);

					Transaction tr = new Transaction();
					tr.setTransactionOperation(TransactionType.EDIT);
					tr.setTransactionTarget(TransactionTarget.COMPANY);
					tr.setCompany(editedCompany);
					tr.setWhoDid(AuthenticationService.getCurrentSessionUser());
					TransactionFacade.getInstance().insert(tr);

					if ((!previousStatus && editedCompany.isDeleted()) || (previousStatus && !editedCompany.isDeleted())) {
						confirmAllEmployeesInCompanyStatusChange(editedCompany);
					}
				} else {
					UIUtils.showNotification("Company update failed", UIUtils.NotificationType.ERROR);
				}
			}
		}
	}

	/**
	 * Prompt to change status of all employees to company status
	 */
	private void confirmAllEmployeesInCompanyStatusChange(Company company) {
		String status = (company.isDeleted()) ? ProjectConstants.INACTIVE : ProjectConstants.ACTIVE;

		ConfirmDialog dialog = new ConfirmDialog("Would you like to set all employees in " + company.getName()+" as " + status);
		dialog.closeOnCancel();
		dialog.getConfirmButton().addClickListener(e -> {

			List<User> employeesInSelectedCompany = UserFacade.getInstance().getUsersInCompany(company.getId());

			boolean error = false;

			for (User user : employeesInSelectedCompany) {
				user.setDeleted(company.isDeleted());
				if (UserFacade.getInstance().update(user)) {

					Transaction tr = new Transaction();
					tr.setTransactionOperation(TransactionType.EDIT);
					tr.setTransactionTarget(TransactionTarget.USER_STATUS);
					tr.setDestinationUser(user);
					tr.setWhoDid(AuthenticationService.getCurrentSessionUser());
					tr.setAdditionalInfo("User Status changed to:  " + status);
					TransactionFacade.getInstance().insert(tr);

				} else {
					UIUtils.showNotification("User status change failed for: " + user.getUsername(), UIUtils.NotificationType.ERROR);
					error = true;
				}
			}
			dialog.close();
			if (!error) {
				UIUtils.showNotification("Users status change successful", UIUtils.NotificationType.SUCCESS);
			}
		});
		dialog.open();
	}

	private void exportCompanies() {
		System.out.println("Export companies...");
	}

	private void importCompanies() {
		System.out.println("Import companies...");
	}
}