package com.gmail.grigorij.ui.views.navigation.admin;

import com.gmail.grigorij.backend.database.facades.UserFacade;
import com.gmail.grigorij.backend.entities.user.User;
import com.gmail.grigorij.ui.utils.components.CustomDialog;
import com.gmail.grigorij.ui.utils.components.ListItem;
import com.gmail.grigorij.ui.utils.components.detailsdrawer.DetailsDrawer;
import com.gmail.grigorij.ui.utils.components.detailsdrawer.DetailsDrawerFooter;
import com.gmail.grigorij.ui.utils.components.detailsdrawer.DetailsDrawerHeader;
import com.gmail.grigorij.ui.utils.UIUtils;
import com.gmail.grigorij.ui.utils.css.FlexDirection;
import com.gmail.grigorij.ui.utils.forms.AdminUserForm;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


class AdminPersonnel extends Div {

	private static final String CLASS_NAME = "admin-personnel";
	final static String TAB_NAME = "Personnel";

	private AdminMain adminMain;
	private AdminUserForm userForm = new AdminUserForm();

	private Grid<User> grid;
	private ListDataProvider<User> dataProvider;

	private DetailsDrawer detailsDrawer;
	private DetailsDrawerFooter detailsDrawerFooter;


	AdminPersonnel(AdminMain adminMain) {
		this.adminMain = adminMain;
		setId("admin-personnel");
		setClassName(CLASS_NAME);
		setSizeFull();

		createHeader();
		createGrid();
		createDetailsDrawer();
	}

	private void createHeader() {
		FlexLayout header = new FlexLayout();
		header.setClassName(CLASS_NAME + "__header");

		TextField searchField = new TextField();
		searchField.setClassName(CLASS_NAME + "__search");
		searchField.setClearButtonVisible(true);
		searchField.setPrefixComponent(VaadinIcon.SEARCH.create());
		searchField.setPlaceholder("Search Personnel");
		searchField.setValueChangeMode(ValueChangeMode.EAGER);
		searchField.addValueChangeListener(event -> filterGrid(searchField.getValue()));

		header.add(searchField);

		FlexLayout buttons = new FlexLayout();
		buttons.setClassName(CLASS_NAME + "__buttons");

		Button newUserButton = UIUtils.createButton("New User", VaadinIcon.PLUS);
		newUserButton.addClickListener(e -> {
			grid.select(null);
			showDetails(null);
		});
		buttons.add(newUserButton);

		Button importUsersButton = UIUtils.createButton("Import", VaadinIcon.DOWNLOAD);
		importUsersButton.addClickListener(e -> importOnClick());
		buttons.add(importUsersButton);

		Button exportUsersButton = UIUtils.createButton("Export", VaadinIcon.UPLOAD);
		exportUsersButton.addClickListener(e -> exportOnClick());
		buttons.add(exportUsersButton);

		header.add(buttons);

		add(header);
//		adminMain.setContentHeader(header);
	}

	private void filterGrid(String s) {
		dataProvider.clearFilters();
		final String searchParam = s.trim();

		if (searchParam.contains(" ")) {
			String[] searchParams = searchParam.split(" ");

			dataProvider.addFilter(
					user -> {
						boolean res = true;
						for (String sParam : searchParams) {
							res =  StringUtils.containsIgnoreCase(user.getUsername(), sParam) ||
									StringUtils.containsIgnoreCase(user.getFirstName(), sParam) ||
									StringUtils.containsIgnoreCase(user.getLastName(), sParam) ||
									StringUtils.containsIgnoreCase(user.getEmail(), sParam);

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
							StringUtils.containsIgnoreCase(user.getFirstName(), searchParam) ||
							StringUtils.containsIgnoreCase(user.getLastName(), searchParam)  ||
							StringUtils.containsIgnoreCase(user.getEmail(), searchParam)
			);
		}

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

		dataProvider = DataProvider.ofCollection(UserFacade.getInstance().listAllUsers());
		grid.setDataProvider(dataProvider);

		grid.addColumn(User::getId).setHeader("ID")
				.setWidth(UIUtils.COLUMN_WIDTH_XS)
				.setFlexGrow(0);
		grid.addColumn(User::getUsername).setHeader("Username");
		grid.addColumn(new ComponentRenderer<>(this::createGridUserInfo)).setHeader("Person")
				.setWidth(UIUtils.COLUMN_WIDTH_XL);
		grid.addColumn(new ComponentRenderer<>(selectedUser -> UIUtils.createActiveGridIcon(selectedUser.isDeleted()))).setHeader("Active")
				.setWidth(UIUtils.COLUMN_WIDTH_XS)
				.setFlexGrow(0);

		add(grid);
	}


	private Component createGridUserInfo(User user) {
		ListItem item = new ListItem(UIUtils.createInitials(user.getInitials()), user.getFirstName() + " " + user.getLastName(), user.getEmail());
		item.setHorizontalPadding(false);
		return item;
	}


	private void createDetailsDrawer() {
		detailsDrawer = new DetailsDrawer(DetailsDrawer.Position.RIGHT);
		detailsDrawer.setContent(userForm);

		// Header
		DetailsDrawerHeader detailsDrawerTitle = new DetailsDrawerHeader("User Details");

		detailsDrawer.setHeader(detailsDrawerTitle);
		detailsDrawer.getHeader().setFlexDirection(FlexDirection.COLUMN);

		// Footer
		detailsDrawerFooter = new DetailsDrawerFooter();
		detailsDrawerFooter.getSave().addClickListener(e -> updateUser());
		detailsDrawerFooter.getCancel().addClickListener(e -> detailsDrawer.hide());
		detailsDrawerFooter.getDelete().addClickListener(e -> confirmUserDelete());
		detailsDrawer.setFooter(detailsDrawerFooter);

		adminMain.setDetailsDrawer(detailsDrawer);
	}


	private void showDetails(User user) {
		detailsDrawerFooter.getDelete().setEnabled( user != null );
		userForm.setUser(user);
		detailsDrawer.show();
	}


	private void updateUser() {
		System.out.println();
		System.out.println("updateUser()");

		User editedUser = userForm.getUser();

		if (editedUser != null) {
			if (UserFacade.getInstance().update(editedUser)) {
				if (userForm.isNewUser()) {
					dataProvider.getItems().add(editedUser);
				} else {
					dataProvider.refreshItem(grid.asSingleSelect().getValue());
				}

				dataProvider.refreshAll();
				grid.select(editedUser);
			}
		}
	}


	private void confirmUserDelete() {
		System.out.println("Delete selected user...");

		if (detailsDrawer.isOpen()) {

			System.out.println("selectedUser: " + grid.asSingleSelect().getValue());
			final User selectedUser =  grid.asSingleSelect().getValue();
			if (selectedUser != null) {

				CustomDialog dialog = new CustomDialog();
				dialog.setHeader(UIUtils.createH4Label("Confirm delete"));

				dialog.setConfirmButton(UIUtils.createButton("Delete", VaadinIcon.TRASH, ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_TERTIARY));
				dialog.getConfirmButton().setEnabled(false);

				TextField confirmInputField = new TextField("Input username to confirm action");
				confirmInputField.setRequired(true);
				confirmInputField.addValueChangeListener(e -> {
					dialog.getConfirmButton().setEnabled(false);

					if (e.getValue() != null) {
						if (e.getValue().length() > 0) {
							if (e.getValue().equals(selectedUser.getUsername())) {
								dialog.getConfirmButton().setEnabled(true);
							}
						}
					}
				});

				dialog.setContent(
						new Span("Are you sure you want to delete this user?"),
						new Span("This will completely remove selected user from Database."),
						new HorizontalLayout(new Span("Deleting user: "), UIUtils.createBoldText(selectedUser.getUsername())),
						confirmInputField
				);

				dialog.getConfirmButton().addClickListener(e -> {
					if (UserFacade.getInstance().remove(selectedUser)) {
						dataProvider.getItems().remove(selectedUser);
						dataProvider.refreshAll();
						detailsDrawer.hide();
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
//			Component component = createComponent(event.getMIMEType(), event.getFileName(), buffer.getInputStream());
//			showOutput(event.getFileName(), component, output);
			System.out.println("SUCCESS");
			importUsers(buffer);
			importDialog.close();
		});
	}

	private void importUsers(MemoryBuffer buffer) {
		try {
			InputStreamReader inputStreamReader = new InputStreamReader(buffer.getInputStream(), StandardCharsets.UTF_8);
			BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

			//Skipping first line of the csv which is grid headers
			List<String> list = bufferedReader.lines().skip(1).collect(Collectors.toList());

			ArrayList<User> importedUsers = new ArrayList<>();

			String separator = "";

			if (list.size() > 0) {
				if (list.get(0).split(",").length > 1) {
					separator = ",";
				} else if (list.get(0).split(";").length > 1) {
					separator = ";";
				}
			}

			for (String row : list) {
				String[] rowSplit = row.split(separator);

				System.out.println("ROW: " + row);

				User user = new User();
				user.setUsername(rowSplit[0]);
				user.setPassword(rowSplit[1]);
				user.setCompany_id(Integer.parseInt(rowSplit[2]));
				user.setAccess_group(Integer.parseInt(rowSplit[3]));
				user.setDeleted(Boolean.parseBoolean(rowSplit[4]));
				user.setFirstName(rowSplit[5]);
				user.setLastName(rowSplit[6]);
				user.setPhoneNumber(rowSplit[7]);
				user.setEmail(rowSplit[8]);

				importedUsers.add(user);
			}

			for (User u : importedUsers) {
				UserFacade.getInstance().insert(u);
			}

			UIUtils.showNotification("Users imported successfully", UIUtils.NotificationType.SUCCESS, 5000);
		} catch (Exception e) {
			UIUtils.showNotification("USER IMPORT ERROR", UIUtils.NotificationType.ERROR);
			e.printStackTrace();
		}
	}


	private void exportOnClick() {
		System.out.println("Export Users...");

	}
}
