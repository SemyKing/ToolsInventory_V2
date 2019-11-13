package com.gmail.grigorij.ui.views.app.admin;

import com.gmail.grigorij.backend.database.entities.Transaction;
import com.gmail.grigorij.backend.database.entities.User;
import com.gmail.grigorij.backend.database.enums.operations.Operation;
import com.gmail.grigorij.backend.database.enums.operations.OperationTarget;
import com.gmail.grigorij.backend.database.enums.permissions.PermissionLevel;
import com.gmail.grigorij.backend.database.enums.permissions.PermissionRange;
import com.gmail.grigorij.backend.database.facades.PermissionFacade;
import com.gmail.grigorij.backend.database.facades.TransactionFacade;
import com.gmail.grigorij.backend.database.facades.UserFacade;
import com.gmail.grigorij.ui.components.FlexBoxLayout;
import com.gmail.grigorij.ui.components.detailsdrawer.DetailsDrawer;
import com.gmail.grigorij.ui.components.detailsdrawer.DetailsDrawerFooter;
import com.gmail.grigorij.ui.components.detailsdrawer.DetailsDrawerHeader;
import com.gmail.grigorij.ui.components.forms.UserForm;
import com.gmail.grigorij.ui.utils.UIUtils;
import com.gmail.grigorij.ui.views.app.AdminView;
import com.gmail.grigorij.utils.AuthenticationService;
import com.gmail.grigorij.utils.ProjectConstants;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;


public class AdminPersonnel extends FlexBoxLayout {

	private static final String CLASS_NAME = "admin-personnel";
	private final UserForm userForm = new UserForm();
	private final AdminView adminView;

	private Grid<User> grid;
	private ListDataProvider<User> dataProvider;

	private DetailsDrawer detailsDrawer;

	private boolean entityOldStatus;


	public AdminPersonnel(AdminView adminView) {
		this.adminView = adminView;
		setClassName(CLASS_NAME);

		add(constructHeader());
		add(constructContent());

		constructDetails();
	}


	private Div constructHeader() {
		Div header = new Div();
		header.setClassName(CLASS_NAME + "__header");

		TextField searchField = new TextField();
		searchField.setClearButtonVisible(true);
		searchField.setPrefixComponent(VaadinIcon.SEARCH.create());
		searchField.setPlaceholder("Search Personnel");
		searchField.setValueChangeMode(ValueChangeMode.LAZY);
		searchField.addValueChangeListener(event -> filterGrid(searchField.getValue()));

		header.add(searchField);

		MenuBar actionsMenuBar = new MenuBar();
		actionsMenuBar.addThemeVariants(MenuBarVariant.LUMO_PRIMARY, MenuBarVariant.LUMO_ICON);

		MenuItem menuItem = actionsMenuBar.addItem(new Icon(VaadinIcon.MENU));

		if (AuthenticationService.getCurrentSessionUser().getPermissionLevel().equalsTo(PermissionLevel.SYSTEM_ADMIN) ||
				PermissionFacade.getInstance().isUserAllowedTo(Operation.ADD, OperationTarget.USER, null)) {
			menuItem.getSubMenu().addItem("New User", e -> {
				grid.select(null);
				showDetails(null);
			});
			menuItem.getSubMenu().add(new Hr());
		}


		if (AuthenticationService.getCurrentSessionUser().getPermissionLevel().equalsTo(PermissionLevel.SYSTEM_ADMIN) ||
				PermissionFacade.getInstance().isUserAllowedTo(Operation.IMPORT, OperationTarget.USER, null)) {
			menuItem.getSubMenu().addItem("Import", e -> {
				importOnClick();
			});
			menuItem.getSubMenu().add(new Hr());
		}

		if (AuthenticationService.getCurrentSessionUser().getPermissionLevel().equalsTo(PermissionLevel.SYSTEM_ADMIN) ||
				PermissionFacade.getInstance().isUserAllowedTo(Operation.EXPORT, OperationTarget.USER, null)) {
			menuItem.getSubMenu().addItem("Export", e -> {
				exportOnClick();
			});
		}

		header.add(actionsMenuBar);

		return header;
	}

	private Div constructContent() {
		Div content = new Div();
		content.setClassName(CLASS_NAME + "__content");

		// GRID
		content.add(constructGrid());

		return content;
	}

	private Grid constructGrid() {
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

		if (AuthenticationService.getCurrentSessionUser().getPermissionLevel().equalsTo(PermissionLevel.SYSTEM_ADMIN)) {
			dataProvider = DataProvider.ofCollection(UserFacade.getInstance().getAllUsers());
		} else {
			dataProvider = DataProvider.ofCollection(UserFacade.getInstance().getAllUsersInCompany(AuthenticationService.getCurrentSessionUser().getCompany().getId()));
		}

		grid.setDataProvider(dataProvider);

		grid.addColumn(user -> (user.getPerson() == null) ? "" : user.getPerson().getFullName())
				.setHeader("Employee")
				.setFlexGrow(1)
				.setAutoWidth(true);

		grid.addColumn(user -> (user.getCompany() == null) ? "" : user.getCompany().getName())
				.setHeader("Company")
				.setFlexGrow(1)
				.setAutoWidth(true);

		grid.addColumn(new ComponentRenderer<>(selectedUser -> UIUtils.createActiveGridIcon(selectedUser.isDeleted())))
				.setHeader("Active")
				.setFlexGrow(0)
				.setAutoWidth(true);

		return grid;
	}

	private void constructDetails() {
		detailsDrawer = adminView.getDetailsDrawer();

		DetailsDrawerHeader detailsDrawerHeader = new DetailsDrawerHeader("User Details");
		detailsDrawerHeader.getClose().addClickListener(e -> closeDetails());
		detailsDrawer.setHeader(detailsDrawerHeader);

		detailsDrawer.setContent(userForm);

		DetailsDrawerFooter detailsDrawerFooter = new DetailsDrawerFooter();
		detailsDrawerFooter.getSave().setEnabled(false);

		if (AuthenticationService.getCurrentSessionUser().getPermissionLevel().equalsTo(PermissionLevel.SYSTEM_ADMIN) ||
				PermissionFacade.getInstance().isUserAllowedTo(Operation.EDIT, OperationTarget.USER, PermissionRange.COMPANY)) {
			detailsDrawerFooter.getSave().setEnabled(true);
			detailsDrawerFooter.getSave().addClickListener(e -> saveOnClick(userForm.getUser(), userForm.isNew()));
		}

		detailsDrawerFooter.getClose().addClickListener(e -> closeDetails());
		detailsDrawer.setFooter(detailsDrawerFooter);
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

	private void showDetails(User user)  {
		if (user != null) {
			if (!AuthenticationService.getCurrentSessionUser().getPermissionLevel().equalsTo(PermissionLevel.SYSTEM_ADMIN)) {
				if (AuthenticationService.getCurrentSessionUser().getId().equals(user.getId())) {
					if (!PermissionFacade.getInstance().isUserAllowedTo(Operation.VIEW, OperationTarget.USER, PermissionRange.OWN)) {
						UIUtils.showNotification(ProjectConstants.ACTION_NOT_ALLOWED, NotificationVariant.LUMO_PRIMARY);
						grid.deselectAll();
						return;
					}
				} else {
					if (!PermissionFacade.getInstance().isUserAllowedTo(Operation.VIEW, OperationTarget.USER, PermissionRange.COMPANY)) {
						UIUtils.showNotification(ProjectConstants.ACTION_NOT_ALLOWED, NotificationVariant.LUMO_PRIMARY);
						grid.deselectAll();
						return;
					}
				}
			}

			entityOldStatus = user.isDeleted();
			detailsDrawer.setDeletedAttribute(user.isDeleted());
		} else {
			entityOldStatus = false;
			detailsDrawer.setDeletedAttribute(false);
		}


		userForm.setUser(user);
		detailsDrawer.show();
	}

	private void closeDetails() {
		detailsDrawer.hide();
		grid.deselectAll();
	}

	private void saveOnClick(User user, boolean isNew) {
		if (user == null) {
			return;
		}

		if (isNew) {
			if (UserFacade.getInstance().insert(user)) {
				dataProvider.getItems().add(user);

				UIUtils.showNotification("User created", NotificationVariant.LUMO_SUCCESS);
			} else {
				UIUtils.showNotification("User insert failed", NotificationVariant.LUMO_ERROR);
				return;
			}
		} else {
			if (UserFacade.getInstance().update(user)) {
				UIUtils.showNotification("User updated", NotificationVariant.LUMO_SUCCESS);

				if (Boolean.compare(entityOldStatus, user.isDeleted()) != 0) {
					if (adminView.handleUserStatusChange(user.getId(), user.isDeleted())) {
						UIUtils.showNotification("User status changed", NotificationVariant.LUMO_SUCCESS);
					}
				}
			} else {
				UIUtils.showNotification("User update failed", NotificationVariant.LUMO_ERROR);
				return;
			}
		}

		Transaction transaction = new Transaction();
		transaction.setUser(AuthenticationService.getCurrentSessionUser());
		transaction.setCompany(AuthenticationService.getCurrentSessionUser().getCompany());
		transaction.setOperation(isNew ? Operation.ADD : Operation.EDIT);
		transaction.setOperationTarget1(OperationTarget.USER);
		transaction.setTargetDetails(user.getFullName());
		transaction.setChanges(isNew ? new ArrayList<>() : userForm.getChanges());
		TransactionFacade.getInstance().insert(transaction);

		if (isNew) {
			grid.select(user);
		}

		dataProvider.refreshAll();
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
//			UIUtils.showNotification("Users imported successfully", NotificationVariant.LUMO_SUCCESS);
//		} catch (Exception e) {
//			UIUtils.showNotification("Users import failed", NotificationVariant.LUMO_ERROR);
//			e.printStackTrace();
//		}
//	}

	private void exportOnClick() {
		System.out.println("Export Users...");
	}
}
