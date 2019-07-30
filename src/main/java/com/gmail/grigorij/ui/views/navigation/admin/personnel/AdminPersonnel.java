package com.gmail.grigorij.ui.views.navigation.admin.personnel;

import com.github.appreciated.papermenubutton.PaperMenuButton;
import com.gmail.grigorij.backend.database.facades.UserFacade;
import com.gmail.grigorij.backend.entities.user.User;
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
import com.gmail.grigorij.ui.utils.forms.editable.EditableUserForm;
import com.gmail.grigorij.ui.views.navigation.admin.AdminMain;
import com.gmail.grigorij.utils.ProjectConstants;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.VaadinIcon;
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

	private final AdminMain adminMain;
	private EditableUserForm userForm = new EditableUserForm();

	private Grid<User> grid;
	private ListDataProvider<User> dataProvider;

	private DetailsDrawer detailsDrawer;
	private Button deleteButton;


	public AdminPersonnel(AdminMain adminMain) {
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
//		searchField.setValueChangeMode(ValueChangeMode.EAGER);
		searchField.setValueChangeMode(ValueChangeMode.LAZY);
		searchField.addValueChangeListener(event -> filterGrid(searchField.getValue()));

		header.add(searchField);
		header.setComponentMargin(searchField, Right.S);



		Button actionsButton = UIUtils.createIconButton("Options", VaadinIcon.MENU, ButtonVariant.LUMO_CONTRAST);
		actionsButton.addClassName("hiding-text-button");

		FlexBoxLayout popupWrapper = new FlexBoxLayout();

		PaperMenuButton inventoryPaperMenuButton = new PaperMenuButton(actionsButton, popupWrapper);
		inventoryPaperMenuButton.setVerticalOffset(40);
		inventoryPaperMenuButton.setHorizontalOffset(-100);


		//POPUP VIEW
		popupWrapper.setFlexDirection(FlexDirection.COLUMN);
		popupWrapper.setDisplay(Display.FLEX);
		popupWrapper.setPadding(Horizontal.S);
		popupWrapper.setBackgroundColor("var(--lumo-base-color)");


		Button newToolButton = UIUtils.createIconButton("New User", VaadinIcon.USER_CARD, ButtonVariant.LUMO_CONTRAST, ButtonVariant.LUMO_TERTIARY);
		newToolButton.addClassName("button-align-left");
		newToolButton.addClickListener(e -> {
			inventoryPaperMenuButton.close();
			grid.select(null);
			showDetails(null);
		});

		popupWrapper.add(newToolButton);
		popupWrapper.setComponentMargin(newToolButton, Vertical.NONE);

		popupWrapper.add(new Divider(1, Vertical.XS));

		Button changeThemeButton = UIUtils.createIconButton("Import", VaadinIcon.SIGN_IN, ButtonVariant.LUMO_CONTRAST, ButtonVariant.LUMO_TERTIARY);
		changeThemeButton.addClassName("button-align-left");
		changeThemeButton.addClickListener(e -> {
			inventoryPaperMenuButton.close();
			importOnClick();
		});

		popupWrapper.add(changeThemeButton);
		popupWrapper.setComponentMargin(changeThemeButton, Vertical.NONE);

		popupWrapper.add(new Divider(1, Vertical.XS));

		Button logOutButton = UIUtils.createIconButton("Export", VaadinIcon.SIGN_OUT, ButtonVariant.LUMO_CONTRAST, ButtonVariant.LUMO_TERTIARY);
		logOutButton.addClassName("button-align-left");
		logOutButton.addClickListener(e -> {
			inventoryPaperMenuButton.close();
			exportOnClick();
		});

		popupWrapper.add(logOutButton);
		popupWrapper.setComponentMargin(logOutButton, Vertical.NONE);

		header.add(inventoryPaperMenuButton);
		header.setComponentPadding(inventoryPaperMenuButton, Horizontal.NONE);
		header.setComponentPadding(inventoryPaperMenuButton, Vertical.NONE);

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

		grid.addColumn(User::getId).setHeader("ID")
				.setWidth(UIUtils.COLUMN_WIDTH_XS)
				.setFlexGrow(0);

		grid.addColumn(User::getUsername)
				.setHeader("Username")
				.setWidth(UIUtils.COLUMN_WIDTH_L);

		grid.addColumn(user -> (user.getCompany() == null) ? "" : user.getCompany().getName())
				.setHeader("Company")
				.setWidth(UIUtils.COLUMN_WIDTH_L);

		grid.addColumn(user -> (user.getPerson() == null) ? "" : user.getPerson().getFullName())
				.setHeader("Person")
				.setWidth(UIUtils.COLUMN_WIDTH_L);

		grid.addColumn(new ComponentRenderer<>(selectedUser -> UIUtils.createActiveGridIcon(selectedUser.isDeleted()))).setHeader("Active")
				.setWidth(UIUtils.COLUMN_WIDTH_XS)
				.setFlexGrow(0);

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

		deleteButton = UIUtils.createIconButton(VaadinIcon.TRASH, ButtonVariant.LUMO_ERROR);
		deleteButton.addClickListener(e -> confirmDelete());
		UIUtils.setTooltip("Delete this user from Database", deleteButton);

		detailsDrawerHeader.add(deleteButton);
		detailsDrawerHeader.getContainer().setComponentMargin(deleteButton, Left.AUTO);

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
		deleteButton.setEnabled( user != null );
		userForm.setUser(user);
		detailsDrawer.show();

		UIUtils.updateFormSize(userForm);
	}

	private void closeDetails() {
		detailsDrawer.hide();
		grid.select(null);
	}

	private void updateUser() {
		System.out.println("updateUser()");

		User editedUser = userForm.getUser();

		if (editedUser != null) {

			if (userForm.isNew()) {
				if (UserFacade.getInstance().insert(editedUser)) {
					dataProvider.getItems().add(editedUser);
					dataProvider.refreshAll();
					UIUtils.showNotification("User created successfully", UIUtils.NotificationType.SUCCESS);
				} else {
					UIUtils.showNotification("User insert failed", UIUtils.NotificationType.ERROR);
				}
			} else {
				if (UserFacade.getInstance().update(editedUser)) {
					if (grid.asSingleSelect().getValue() != null) {
						dataProvider.refreshItem(grid.asSingleSelect().getValue());
					}

					UIUtils.showNotification("User updated successfully", UIUtils.NotificationType.SUCCESS);
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

				ConfirmDialog dialog = new ConfirmDialog(ConfirmDialog.Type.DELETE, "selected user", selectedUser.getUsername());
				dialog.closeOnCancel();
				dialog.getConfirmButton().addClickListener(e -> {
					if (UserFacade.getInstance().remove(selectedUser)) {
						dataProvider.getItems().remove(selectedUser);
						dataProvider.refreshAll();
						closeDetails();
						UIUtils.showNotification("User deleted successfully", UIUtils.NotificationType.SUCCESS);
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
