package com.gmail.grigorij.ui.views.application.admin;

import com.gmail.grigorij.backend.database.facades.TransactionFacade;
import com.gmail.grigorij.backend.database.facades.UserFacade;
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
import com.gmail.grigorij.ui.components.forms.editable.EditableUserForm;
import com.gmail.grigorij.utils.AuthenticationService;
import com.gmail.grigorij.utils.ProjectConstants;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import org.apache.commons.lang3.StringUtils;


public class AdminPersonnel extends FlexBoxLayout {

	private static final String CLASS_NAME = "admin-personnel";

	private final AdminContainerView adminMain;
	private EditableUserForm userForm = new EditableUserForm();

	private Grid<User> grid;
	private ListDataProvider<User> dataProvider;

	private DetailsDrawer detailsDrawer;
	private Button deleteButton;


	public AdminPersonnel(AdminContainerView adminMain) {
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
		searchField.setPlaceholder("Search Personnel");
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

		menuItem.getSubMenu().addItem("New User", e -> {
			grid.select(null);
			showDetails(null);
		});
		menuItem.getSubMenu().add(new Hr());
		menuItem.getSubMenu().addItem("Import", e -> {
			importOnClick();
		});
		menuItem.getSubMenu().add(new Hr());
		menuItem.getSubMenu().addItem("Export", e -> {
			exportOnClick();
		});

		header.add(actionsMenuBar);

		add(header);
	}

	private void createGrid() {
		grid = new Grid<>();
		grid.setId("personnel-grid");
		grid.setClassName("grid-view");
		grid.setSizeFull();
		grid.asSingleSelect().addValueChangeListener(e -> {
			if (grid.asSingleSelect().getValue() != null) {
				showDetails(grid.asSingleSelect().getValue());
			} else {
				detailsDrawer.hide();
			}
		});

		dataProvider = DataProvider.ofCollection(UserFacade.getInstance().getAllUsers());
		grid.setDataProvider(dataProvider);

		grid.addColumn(user -> (user.getPerson() == null) ? "" : user.getPerson().getFullName())
				.setHeader("Employee")
				.setAutoWidth(true);

		grid.addColumn(user -> (user.getCompany() == null) ? "" : user.getCompany().getName())
				.setHeader("Company")
				.setAutoWidth(true);

		grid.addColumn(new ComponentRenderer<>(selectedUser -> UIUtils.createActiveGridIcon(selectedUser.isDeleted())))
				.setHeader("Active")
				.setAutoWidth(true);

		add(grid);
	}

	private void filterGrid(String searchString) {
		dataProvider.clearFilters();
		final String searchParam = searchString.trim();

		if (searchParam.contains(" ")) {
			String[] searchParams = searchParam.split(" ");

			dataProvider.addFilter(
					user -> {
						boolean res = true;
						for (String sParam : searchParams) {
							res =  StringUtils.containsIgnoreCase(user.getUsername(), sParam) ||
									StringUtils.containsIgnoreCase((user.getPerson() == null) ? "" : user.getPerson().getFirstName(), sParam) ||
									StringUtils.containsIgnoreCase((user.getPerson() == null) ? "" : user.getPerson().getLastName(), sParam) ||
									StringUtils.containsIgnoreCase((user.getPerson() == null) ? "" : user.getPerson().getEmail(), sParam) ||
									StringUtils.containsIgnoreCase((user.getCompany() == null) ? "" : user.getCompany().getName(), sParam);

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
					user -> StringUtils.containsIgnoreCase(user.getUsername(), searchParam)  ||
							StringUtils.containsIgnoreCase((user.getPerson() == null) ? "" : user.getPerson().getFirstName(), searchParam) ||
							StringUtils.containsIgnoreCase((user.getPerson() == null) ? "" : user.getPerson().getLastName(), searchParam)  ||
							StringUtils.containsIgnoreCase((user.getPerson() == null) ? "" : user.getPerson().getEmail(), searchParam) ||
							StringUtils.containsIgnoreCase((user.getCompany() == null) ? "" : user.getCompany().getName(), searchParam)
			);
		}

	}


	private void createDetailsDrawer() {
		detailsDrawer = new DetailsDrawer(DetailsDrawer.Position.RIGHT);
		detailsDrawer.getElement().setAttribute(ProjectConstants.FORM_LAYOUT_LARGE_ATTR, true);
		detailsDrawer.setContent(userForm);
		detailsDrawer.setContentPadding(Left.M, Right.S);

		// Header
		DetailsDrawerHeader detailsDrawerHeader = new DetailsDrawerHeader("User Details");
		detailsDrawerHeader.getClose().addClickListener(e -> closeDetails());

//		Select<String> userStatusSelector = new Select<>(ProjectConstants.ACTIVE, ProjectConstants.INACTIVE);
//		userForm.setUserStatusSelector(userStatusSelector);

//		detailsDrawerHeader.add(userStatusSelector);
//		detailsDrawerHeader.getContainer().setComponentMargin(userStatusSelector, Left.AUTO);

		detailsDrawer.setHeader(detailsDrawerHeader);
		detailsDrawer.getHeader().setFlexDirection(FlexDirection.COLUMN);

		// Footer
		DetailsDrawerFooter detailsDrawerFooter = new DetailsDrawerFooter();
		detailsDrawerFooter.getSave().addClickListener(e -> updateUser());
		detailsDrawerFooter.getCancel().addClickListener(e -> closeDetails());
		detailsDrawer.setFooter(detailsDrawerFooter);

		adminMain.setDetailsDrawer(detailsDrawer);
	}

	private void showDetails(User user) {
		userForm.setTargetUser(user);
		detailsDrawer.show();

		UIUtils.updateFormSize(userForm);
	}

	private void closeDetails() {
		detailsDrawer.hide();
		grid.select(null);
	}

	private void updateUser() {
		System.out.println("updateUser()");

		User editedUser = userForm.getTargetUser();

		if (editedUser != null) {

			if (userForm.isNew()) {
				if (UserFacade.getInstance().insert(editedUser)) {
					dataProvider.getItems().add(editedUser);
					dataProvider.refreshAll();
					UIUtils.showNotification("User created successfully", UIUtils.NotificationType.SUCCESS);

					Transaction tr = new Transaction();
					tr.setTransactionOperation(TransactionType.ADD);
					tr.setTransactionTarget(TransactionTarget.USER);
					tr.setDestinationUser(editedUser);
					tr.setWhoDid(AuthenticationService.getCurrentSessionUser());
					TransactionFacade.getInstance().insert(tr);
				} else {
					UIUtils.showNotification("User insert failed", UIUtils.NotificationType.ERROR);
				}
			} else {
				if (UserFacade.getInstance().update(editedUser)) {
					if (grid.asSingleSelect().getValue() != null) {
						dataProvider.refreshItem(grid.asSingleSelect().getValue());
					}

					UIUtils.showNotification("User updated successfully", UIUtils.NotificationType.SUCCESS);

					Transaction tr = new Transaction();
					tr.setTransactionOperation(TransactionType.EDIT);
					tr.setTransactionTarget(TransactionTarget.USER);
					tr.setDestinationUser(editedUser);
					tr.setWhoDid(AuthenticationService.getCurrentSessionUser());
					TransactionFacade.getInstance().insert(tr);
				} else {
					UIUtils.showNotification("User update failed", UIUtils.NotificationType.ERROR);
				}

			}

			grid.select(editedUser);
		}
	}

	private void confirmDelete() {
		System.out.println("Delete selected user...");

		if (detailsDrawer.isOpen()) {

			final User selectedUser =  grid.asSingleSelect().getValue();
			if (selectedUser != null) {

				ConfirmDialog dialog = new ConfirmDialog(ConfirmDialog.Type.DELETE, " selected user ", selectedUser.getUsername());
				dialog.closeOnCancel();
				dialog.getConfirmButton().addClickListener(e -> {
					if (UserFacade.getInstance().remove(selectedUser)) {
						dataProvider.getItems().remove(selectedUser);
						dataProvider.refreshAll();
						closeDetails();
						UIUtils.showNotification("User deleted successfully", UIUtils.NotificationType.SUCCESS);

						Transaction tr = new Transaction();
						tr.setTransactionOperation(TransactionType.DELETE);
						tr.setTransactionTarget(TransactionTarget.USER);
						tr.setDestinationUser(selectedUser);
						tr.setWhoDid(AuthenticationService.getCurrentSessionUser());
						tr.setAdditionalInfo("Completely remove from database");
						TransactionFacade.getInstance().insert(tr);
					} else {
						UIUtils.showNotification("User delete failed", UIUtils.NotificationType.ERROR);
					}
					dialog.close();
				});
				dialog.open();
			}
		}
	}


	private void importOnClick() {
		System.out.println("Import Users...");

		Dialog importDialog = new Dialog();

		MemoryBuffer buffer = new MemoryBuffer();
		Upload upload = new Upload(buffer);

		importDialog.add(upload);
		importDialog.open();

		upload.addSucceededListener(event -> {
//			importUsers(buffer);
//			importDialog.close();
		});
	}

//	private void importUsers(MemoryBuffer buffer) {
//		try {
//			InputStreamReader inputStreamReader = new InputStreamReader(buffer.getInputStream(), StandardCharsets.UTF_8);
//			BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
//
//			//Skipping first line of the csv which is grid headers
//			List<String> list = bufferedReader.lines().skip(1).collect(Collectors.toList());
//
//			ArrayList<User> importedUsers = new ArrayList<>();
//
//			String separator = "";
//
//			if (list.size() > 0) {
//				if (list.get(0).split(",").length > 1) {
//					separator = ",";
//				} else if (list.get(0).split(";").length > 1) {
//					separator = ";";
//				}
//			}
//
//			for (String row : list) {
//				String[] rowSplit = row.split(separator);
//
//				System.out.println("ROW: " + row);
//
//				User user = new User();
//				user.setUsername(rowSplit[0]);
//				user.setPassword(rowSplit[1]);
//				user.setCompanyId(Integer.parseInt(rowSplit[2]));
//				user.setAccessGroup(Integer.parseInt(rowSplit[3]));
//				user.setDeleted(Boolean.parseBoolean(rowSplit[4]));
//				user.setFirstName(rowSplit[5]);
//				user.setLastName(rowSplit[6]);
//				user.setPhoneNumber(rowSplit[7]);
//				user.setEmail(rowSplit[8]);
//
//				importedUsers.add(user);
//			}
//
//			for (User u : importedUsers) {
//				UserFacade.getInstance().insert(u);
//			}
//
//			UIUtils.showNotification("Users imported successfully", UIUtils.NotificationType.SUCCESS);
//		} catch (Exception e) {
//			UIUtils.showNotification("Users import failed", UIUtils.NotificationType.ERROR);
//			e.printStackTrace();
//		}
//	}

	private void exportOnClick() {
		System.out.println("Export Users...");
	}
}
