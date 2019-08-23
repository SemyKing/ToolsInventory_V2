package com.gmail.grigorij.ui.views.navigation.admin.companies;

import com.github.appreciated.papermenubutton.HorizontalAlignment;
import com.github.appreciated.papermenubutton.PaperMenuButton;
import com.github.appreciated.papermenubutton.VerticalAlignment;
import com.gmail.grigorij.backend.database.facades.CompanyFacade;
import com.gmail.grigorij.backend.database.facades.TransactionFacade;
import com.gmail.grigorij.backend.database.facades.UserFacade;
import com.gmail.grigorij.backend.entities.company.Company;
import com.gmail.grigorij.backend.entities.transaction.Transaction;
import com.gmail.grigorij.backend.entities.user.User;
import com.gmail.grigorij.backend.enums.transactions.TransactionTarget;
import com.gmail.grigorij.backend.enums.transactions.TransactionType;
import com.gmail.grigorij.ui.utils.UIUtils;
import com.gmail.grigorij.ui.utils.components.ConfirmDialog;
import com.gmail.grigorij.ui.utils.components.Divider;
import com.gmail.grigorij.ui.utils.components.FlexBoxLayout;
import com.gmail.grigorij.ui.utils.components.detailsdrawer.DetailsDrawer;
import com.gmail.grigorij.ui.utils.components.detailsdrawer.DetailsDrawerFooter;
import com.gmail.grigorij.ui.utils.components.detailsdrawer.DetailsDrawerHeader;
import com.gmail.grigorij.ui.utils.css.Display;
import com.gmail.grigorij.ui.utils.css.FlexDirection;
import com.gmail.grigorij.ui.utils.css.size.*;
import com.gmail.grigorij.ui.utils.forms.editable.EditableCompanyForm;
import com.gmail.grigorij.ui.views.navigation.admin.AdminMain;
import com.gmail.grigorij.utils.AuthenticationService;
import com.gmail.grigorij.utils.ProjectConstants;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import org.apache.commons.lang3.StringUtils;

import java.util.List;


public class AdminCompanies extends FlexBoxLayout {

	private static final String CLASS_NAME = "admin-companies";

	private final AdminMain adminMain;
	private EditableCompanyForm companyForm = new EditableCompanyForm();

	private Grid<Company> grid;
	private ListDataProvider<Company> dataProvider;

	private DetailsDrawer detailsDrawer;
	private Button deleteButton;


	public AdminCompanies(AdminMain adminMain) {
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


		Button actionsButton = UIUtils.createIconButton("Options", VaadinIcon.MENU, ButtonVariant.LUMO_CONTRAST);
		actionsButton.addClassName("hiding-text-button");

		FlexBoxLayout popupWrapper = new FlexBoxLayout();

		PaperMenuButton companiesPaperMenuButton = new PaperMenuButton(actionsButton, popupWrapper);
		companiesPaperMenuButton.setHorizontalAlignment(HorizontalAlignment.RIGHT);
		companiesPaperMenuButton.setVerticalAlignment(VerticalAlignment.TOP);
		companiesPaperMenuButton.setVerticalOffset(32);

		//POPUP VIEW
		popupWrapper.setFlexDirection(FlexDirection.COLUMN);
		popupWrapper.setDisplay(Display.FLEX);
		popupWrapper.setPadding(Horizontal.S);
		popupWrapper.setBackgroundColor("var(--lumo-base-color)");


		Button newCompanyButton = UIUtils.createIconButton("New Company", VaadinIcon.OFFICE, ButtonVariant.LUMO_CONTRAST, ButtonVariant.LUMO_TERTIARY);
		newCompanyButton.addClassName("button-align-left");
		newCompanyButton.addClickListener(e -> {
			companiesPaperMenuButton.close();
			grid.select(null);
			showDetails(null);
		});

		popupWrapper.add(newCompanyButton);
		popupWrapper.setComponentMargin(newCompanyButton, Vertical.NONE);

		popupWrapper.add(new Divider(1, Vertical.XS));

		Button importCompaniesButton = UIUtils.createIconButton("Import", VaadinIcon.SIGN_IN, ButtonVariant.LUMO_CONTRAST, ButtonVariant.LUMO_TERTIARY);
		importCompaniesButton.addClassName("button-align-left");
		importCompaniesButton.addClickListener(e -> {
			companiesPaperMenuButton.close();
			importCompanies();
		});

		popupWrapper.add(importCompaniesButton);
		popupWrapper.setComponentMargin(importCompaniesButton, Vertical.NONE);

		popupWrapper.add(new Divider(1, Vertical.XS));

		Button exportCompaniesButton = UIUtils.createIconButton("Export", VaadinIcon.SIGN_OUT, ButtonVariant.LUMO_CONTRAST, ButtonVariant.LUMO_TERTIARY);
		exportCompaniesButton.addClassName("button-align-left");
		exportCompaniesButton.addClickListener(e -> {
			companiesPaperMenuButton.close();
			exportCompanies();
		});

		popupWrapper.add(exportCompaniesButton);
		popupWrapper.setComponentMargin(exportCompaniesButton, Vertical.NONE);

		header.add(companiesPaperMenuButton);
		header.setComponentPadding(companiesPaperMenuButton, Horizontal.NONE);
		header.setComponentPadding(companiesPaperMenuButton, Vertical.NONE);

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
		detailsDrawer.getElement().setAttribute(ProjectConstants.FORM_LAYOUT_LARGE_ATTR, true);
		detailsDrawer.setContent(companyForm);
		detailsDrawer.setContentPadding(Left.M, Right.S);

		// Header
		DetailsDrawerHeader detailsDrawerHeader = new DetailsDrawerHeader("Company Details");
		detailsDrawerHeader.getClose().addClickListener(e -> closeDetails());

		deleteButton = UIUtils.createIconButton(VaadinIcon.TRASH, ButtonVariant.LUMO_ERROR);
		deleteButton.addClickListener(e -> confirmDelete());
		UIUtils.setTooltip("Delete this company from Database", deleteButton);

		detailsDrawerHeader.getContainer().add(deleteButton);
		detailsDrawerHeader.getContainer().setComponentMargin(deleteButton, Left.AUTO);

		detailsDrawer.setHeader(detailsDrawerHeader);
		detailsDrawer.getHeader().setFlexDirection(FlexDirection.COLUMN);

		// Footer
		DetailsDrawerFooter detailsDrawerFooter = new DetailsDrawerFooter();
		detailsDrawerFooter.getSave().addClickListener(e -> updateCompany());
		detailsDrawerFooter.getCancel().addClickListener(e -> closeDetails());
		detailsDrawer.setFooter(detailsDrawerFooter);

		adminMain.setDetailsDrawer(detailsDrawer);
	}


	private boolean previousStatus;

	private void showDetails(Company company) {
		deleteButton.setEnabled( company != null );

		if (company != null) {
			previousStatus = company.isDeleted();
		}

		companyForm.setCompany(company);
		detailsDrawer.show();

		UIUtils.updateFormSize(companyForm);
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

	private void confirmDelete() {
		System.out.println("Delete selected company...");

		if (detailsDrawer.isOpen()) {

			final Company selectedCompany = grid.asSingleSelect().getValue();
			if (selectedCompany != null) {

				ConfirmDialog dialog = new ConfirmDialog(ConfirmDialog.Type.DELETE, " selected company ", selectedCompany.getName());
				dialog.closeOnCancel();
				dialog.getConfirmButton().addClickListener(e -> {
					if (CompanyFacade.getInstance().remove(selectedCompany)) {
						dataProvider.getItems().remove(selectedCompany);
						dataProvider.refreshAll();
						closeDetails();
						UIUtils.showNotification("Company deleted successfully", UIUtils.NotificationType.SUCCESS);

						Transaction tr = new Transaction();
						tr.setTransactionOperation(TransactionType.DELETE);
						tr.setTransactionTarget(TransactionTarget.COMPANY);
						tr.setCompany(selectedCompany);
						tr.setWhoDid(AuthenticationService.getCurrentSessionUser());
						tr.setAdditionalInfo("Completely removed from database");
						TransactionFacade.getInstance().insert(tr);

					} else {
						UIUtils.showNotification("Company delete failed", UIUtils.NotificationType.ERROR);
					}
					dialog.close();
				});
				dialog.open();
			}
		}
	}


	private void exportCompanies() {
		System.out.println("Export companies...");
	}

	private void importCompanies() {
		System.out.println("Import companies...");
	}
}