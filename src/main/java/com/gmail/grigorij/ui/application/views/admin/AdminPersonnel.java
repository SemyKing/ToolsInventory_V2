package com.gmail.grigorij.ui.application.views.admin;

import com.gmail.grigorij.backend.database.facades.TransactionFacade;
import com.gmail.grigorij.backend.database.facades.UserFacade;
import com.gmail.grigorij.backend.entities.transaction.Transaction;
import com.gmail.grigorij.backend.entities.user.User;
import com.gmail.grigorij.backend.enums.permissions.PermissionLevel;
import com.gmail.grigorij.backend.enums.transactions.TransactionTarget;
import com.gmail.grigorij.backend.enums.transactions.TransactionType;
import com.gmail.grigorij.ui.application.views.Admin;
import com.gmail.grigorij.ui.utils.UIUtils;
import com.gmail.grigorij.ui.components.layouts.FlexBoxLayout;
import com.gmail.grigorij.ui.components.detailsdrawer.DetailsDrawer;
import com.gmail.grigorij.ui.components.detailsdrawer.DetailsDrawerFooter;
import com.gmail.grigorij.ui.components.detailsdrawer.DetailsDrawerHeader;
import com.gmail.grigorij.ui.components.forms.editable.UserForm;
import com.gmail.grigorij.utils.AuthenticationService;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.icon.Icon;
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
	private final UserForm userForm = new UserForm();
	private final Admin admin;

	private Grid<User> grid;
	private ListDataProvider<User> dataProvider;

	private DetailsDrawer detailsDrawer;


	public AdminPersonnel(Admin admin) {
		this.admin = admin;
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
		actionsMenuBar.addThemeVariants(MenuBarVariant.LUMO_TERTIARY, MenuBarVariant.LUMO_CONTRAST);

		MenuItem menuItem = actionsMenuBar.addItem(new Icon(VaadinIcon.MENU));

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
			dataProvider = DataProvider.ofCollection(UserFacade.getInstance().getUsersInCompany(AuthenticationService.getCurrentSessionUser().getCompany().getId()));
		}

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

		return grid;
	}

	private void constructDetails() {
		detailsDrawer = admin.getDetailsDrawer();

		DetailsDrawerHeader detailsDrawerHeader = new DetailsDrawerHeader("User Details");
		detailsDrawerHeader.getClose().addClickListener(e -> closeDetails());
		detailsDrawer.setHeader(detailsDrawerHeader);

		detailsDrawer.setContent(userForm);

		DetailsDrawerFooter detailsDrawerFooter = new DetailsDrawerFooter();
		detailsDrawerFooter.getSave().addClickListener(e -> saveOnClick());
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

	private void showDetails(User user) {
		userForm.setUser(user);
		detailsDrawer.show();
	}

	private void closeDetails() {
		detailsDrawer.hide();
		grid.deselectAll();
	}

	private void saveOnClick() {
		User editedUser = userForm.getUser();

		if (editedUser == null) {
			return;
		}

		if (userForm.isNew()) {
			if (UserFacade.getInstance().insert(editedUser)) {

				UIUtils.showNotification("User created successfully", UIUtils.NotificationType.SUCCESS);

				Transaction tr = new Transaction();
				tr.setTransactionOperation(TransactionType.ADD);
				tr.setTransactionTarget(TransactionTarget.USER);
				tr.setDestinationUser(editedUser);
				tr.setWhoDid(AuthenticationService.getCurrentSessionUser());
				TransactionFacade.getInstance().insert(tr);
			} else {
				UIUtils.showNotification("User insert failed", UIUtils.NotificationType.ERROR);
				return;
			}
		} else {
			if (UserFacade.getInstance().update(editedUser)) {

				UIUtils.showNotification("User updated successfully", UIUtils.NotificationType.SUCCESS);

				Transaction tr = new Transaction();
				tr.setTransactionOperation(TransactionType.EDIT);
				tr.setTransactionTarget(TransactionTarget.USER);
				tr.setDestinationUser(editedUser);
				tr.setWhoDid(AuthenticationService.getCurrentSessionUser());
				TransactionFacade.getInstance().insert(tr);
			} else {
				UIUtils.showNotification("User update failed", UIUtils.NotificationType.ERROR);
				return;
			}
		}

		dataProvider.refreshAll();
		grid.select(editedUser);
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
