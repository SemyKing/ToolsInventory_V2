package com.gmail.grigorij.ui.views.navigation.admin;

import com.gmail.grigorij.backend.database.facades.CompanyFacade;
import com.gmail.grigorij.backend.database.facades.UserFacade;
import com.gmail.grigorij.backend.entities.company.Company;
import com.gmail.grigorij.backend.access.EntityStatus;
import com.gmail.grigorij.backend.entities.user.User;
import com.gmail.grigorij.ui.utils.components.*;
import com.gmail.grigorij.ui.utils.components.detailsdrawer.DetailsDrawer;
import com.gmail.grigorij.ui.utils.components.detailsdrawer.DetailsDrawerFooter;
import com.gmail.grigorij.ui.utils.components.detailsdrawer.DetailsDrawerHeader;
import com.gmail.grigorij.ui.utils.UIUtils;
import com.gmail.grigorij.ui.utils.css.Display;
import com.gmail.grigorij.ui.utils.css.FlexDirection;
import com.gmail.grigorij.ui.utils.css.size.*;
import com.gmail.grigorij.ui.utils.forms.admin.AdminCompanyForm;
import com.gmail.grigorij.utils.ProjectConstants;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;

import java.util.EnumSet;
import java.util.List;


class AdminCompanies extends FlexBoxLayout {

	private static final String CLASS_NAME = "admin-companies";
	final static String TAB_NAME = "Companies";

	private AdminMain adminMain;
	private AdminCompanyForm companyForm = new AdminCompanyForm();

	private Grid<Company> grid;
	private ListDataProvider<Company> dataProvider;

	private DetailsDrawer detailsDrawer;
	private DetailsDrawerFooter detailsDrawerFooter;


	AdminCompanies(AdminMain adminMain) {
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

		TextField searchField = new TextField();
		searchField.setWidth("100%");
		searchField.setClearButtonVisible(true);
		searchField.setPrefixComponent(VaadinIcon.SEARCH.create());
		searchField.setPlaceholder("Search Companies");

		header.add(searchField);

		FlexBoxLayout optionsContextMenuButton = adminMain.constructOptionsButton();
		header.add(optionsContextMenuButton);

		ContextMenu contextMenu = new ContextMenu(optionsContextMenuButton);
		contextMenu.setOpenOnClick(true);

		contextMenu.add(new Divider(Bottom.XS));
		contextMenu.addItem(UIUtils.createTextIcon(VaadinIcon.OFFICE, UIUtils.createText("Add Company")), e -> {
			grid.select(null);
			showDetails(null);
		});
		contextMenu.add(new Divider(Vertical.XS));
		contextMenu.addItem(UIUtils.createTextIcon(VaadinIcon.INSERT, UIUtils.createText("Import")), e -> importCompanies());
		contextMenu.add(new Divider(Vertical.XS));
		contextMenu.addItem(UIUtils.createTextIcon(VaadinIcon.EXTERNAL_LINK, UIUtils.createText("Export")), e -> exportCompanies());
		contextMenu.add(new Divider(Top.XS));

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

		grid.addColumn(Company::getId).setHeader("ID")
				.setWidth(UIUtils.COLUMN_WIDTH_XS)
				.setFlexGrow(0);
		grid.addColumn(Company::getName).setHeader("Name")
				.setWidth(UIUtils.COLUMN_WIDTH_L);
		grid.addColumn(new ComponentRenderer<>(this::createContactPersonInfo))
				.setHeader("Contact Person")
				.setWidth(UIUtils.COLUMN_WIDTH_XXL);
		grid.addColumn(new ComponentRenderer<>(selectedCompany -> UIUtils.createActiveGridIcon(selectedCompany.isDeleted()))).setHeader("Active")
				.setWidth(UIUtils.COLUMN_WIDTH_XS)
				.setFlexGrow(0);

		add(grid);
	}

	private Component createContactPersonInfo(Company company) {
		ListItem item = new ListItem(UIUtils.createInitials(company.getInitials()), company.getFirstName() + " " + company.getLastName(), company.getEmail());
		item.setHorizontalPadding(false);
		return item;
	}

	private void createDetailsDrawer() {
		detailsDrawer = new DetailsDrawer(DetailsDrawer.Position.RIGHT);
		detailsDrawer.getElement().setAttribute(ProjectConstants.FORM_LAYOUT_LARGE_ATTR, true);
		detailsDrawer.setContent(companyForm);

		// Header
		DetailsDrawerHeader detailsDrawerHeader = new DetailsDrawerHeader("Company Details");
		detailsDrawerHeader.getClose().addClickListener(e -> closeDetails());

		detailsDrawer.setHeader(detailsDrawerHeader);
		detailsDrawer.getHeader().setFlexDirection(FlexDirection.COLUMN);

		// Footer
		detailsDrawerFooter = new DetailsDrawerFooter();
		detailsDrawerFooter.getSave().addClickListener(e -> updateCompany());
		detailsDrawerFooter.getCancel().addClickListener(e -> detailsDrawer.hide());
		detailsDrawerFooter.getDelete().addClickListener(e -> confirmCompanyDelete());
		detailsDrawer.setFooter(detailsDrawerFooter);

		adminMain.setDetailsDrawer(detailsDrawer);
	}


	private boolean previousStatus;

	private void showDetails(Company company) {
		detailsDrawerFooter.getDelete().setEnabled( company != null );

		if (company != null)
			previousStatus = company.isDeleted();

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
			if (CompanyFacade.getInstance().update(editedCompany)) {
				if (companyForm.isNew()) {
					dataProvider.getItems().add(editedCompany);
					dataProvider.refreshAll();
					UIUtils.showNotification("Company created successfully", UIUtils.NotificationType.SUCCESS);
				} else {
					if ((!previousStatus && editedCompany.isDeleted()) || (previousStatus && !editedCompany.isDeleted())) {
						confirmAllEmployeesInCompanyStatusChange(editedCompany);
					}
					dataProvider.refreshItem(grid.asSingleSelect().getValue());
					UIUtils.showNotification("Company updated successfully", UIUtils.NotificationType.SUCCESS);
				}

				grid.select(editedCompany);
			} else {
				UIUtils.showNotification("Company create/edit failed", UIUtils.NotificationType.ERROR);
			}
		}
	}

	/**
	 * Prompt to change status of all employees to company status
	 */
	private void confirmAllEmployeesInCompanyStatusChange(Company company) {
		String status = "";

		for (EntityStatus s : EnumSet.allOf(EntityStatus.class)) {
			if (s.getBooleanValue() == !previousStatus) {
				status = s.getStringValue();
			}
		}

		CustomDialog dialog = new CustomDialog();
		dialog.setHeader(UIUtils.createH4Label("Employees status change"));

		Paragraph content = new Paragraph();
		content.add(new Span("Would you also like to set all employees in: "));
		content.add(UIUtils.createBoldText(company.getName()));
		content.add(new Span(" as " + status + "?"));

		dialog.setContent(content);

		dialog.getCancelButton().setText("No");
		dialog.getCancelButton().addClickListener(e -> dialog.close());
		dialog.setConfirmButton(UIUtils.createButton("Yes", ButtonVariant.LUMO_SUCCESS, ButtonVariant.LUMO_TERTIARY));
		dialog.getConfirmButton().addClickListener(e -> {

			List<User> employeesInSelectedCompany = UserFacade.getInstance().getUsersByCompanyId(company.getId());

			boolean operationsSuccessful = true;

			for (User user : employeesInSelectedCompany) {
				user.setDeleted(!previousStatus);
				if (!UserFacade.getInstance().update(user)) {
					UIUtils.showNotification("User status change failed for: " + user.getUsername(), UIUtils.NotificationType.ERROR);
					operationsSuccessful = false;
				}
			}
			dialog.close();
			if (operationsSuccessful) {
				UIUtils.showNotification("Users status change successful", UIUtils.NotificationType.SUCCESS);
			}
		});
		dialog.open();
	}

	private void confirmCompanyDelete() {
		System.out.println("Delete selected company...");

		if (detailsDrawer.isOpen()) {

			final Company selectedCompany = grid.asSingleSelect().getValue();
			if (selectedCompany != null) {

				ConfirmDialog dialog = new ConfirmDialog(ConfirmDialog.Type.DELETE, "company", selectedCompany.getName());
				dialog.closeOnCancel();
				dialog.getConfirmButton().addClickListener(e -> {
					if (CompanyFacade.getInstance().remove(selectedCompany)) {
						dataProvider.getItems().remove(selectedCompany);
						dataProvider.refreshAll();
						closeDetails();
						UIUtils.showNotification("Company deleted successfully", UIUtils.NotificationType.SUCCESS);
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