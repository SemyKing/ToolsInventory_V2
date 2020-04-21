package com.gmail.grigorij.ui.components.dialogs.permissions;

import com.gmail.grigorij.backend.database.entities.PermissionHolder;
import com.gmail.grigorij.backend.database.entities.User;
import com.gmail.grigorij.backend.database.entities.embeddable.Permission;
import com.gmail.grigorij.backend.database.enums.operations.Operation;
import com.gmail.grigorij.backend.database.enums.operations.OperationPermission;
import com.gmail.grigorij.backend.database.enums.operations.OperationTarget;
import com.gmail.grigorij.backend.database.enums.permissions.PermissionLevel;
import com.gmail.grigorij.backend.database.enums.permissions.PermissionRange;
import com.gmail.grigorij.backend.database.facades.PermissionFacade;
import com.gmail.grigorij.ui.components.dialogs.CustomDialog;
import com.gmail.grigorij.ui.utils.UIUtils;
import com.gmail.grigorij.utils.ProjectConstants;
import com.gmail.grigorij.utils.authentication.AuthenticationService;
import com.gmail.grigorij.utils.changes.Pair;
import com.gmail.grigorij.utils.changes.SimpleChangesTracker;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.ListItem;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.data.provider.ListDataProvider;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;

@StyleSheet("context://styles/views/permissions.css")
public class PermissionsView extends Div {

	private final static String CLASS_NAME = "permissions-view";
	private final User user;

	private SimpleChangesTracker<Permission> permissionChangesTracker;

	private PermissionHolder permissionHolder;

	private ListDataProvider<Permission> dataProvider;
	private List<String> changes = new ArrayList<>();
	private static int counter;

	private boolean systemAdmin = false;
	private boolean editOwnAllowed = false;
	private boolean editOthersAllowed = false;
	private boolean self = false;


	public PermissionsView(User user, PermissionHolder permissionHolder) {
		addClassName(CLASS_NAME);

		this.user = user;

		if (permissionHolder == null) {
			this.permissionHolder = new PermissionHolder();
		} else {
			this.permissionHolder = new PermissionHolder(permissionHolder);
		}


		systemAdmin = AuthenticationService.getCurrentSessionUser().getPermissionLevel().equalsTo(PermissionLevel.SYSTEM_ADMIN);
		self = user.getId().equals(AuthenticationService.getCurrentSessionUser().getId());

		if (!systemAdmin) {
			editOwnAllowed = PermissionFacade.getInstance().isUserAllowedTo(Operation.EDIT, OperationTarget.PERMISSIONS, PermissionRange.OWN);
			editOthersAllowed = PermissionFacade.getInstance().isUserAllowedTo(Operation.EDIT, OperationTarget.PERMISSIONS, PermissionRange.COMPANY);
		}

		add(constructHeader());

		add(new Hr());

		add(constructContent());
	}


	private Div constructHeader() {
		Div header = new Div();
		header.addClassName(CLASS_NAME + "__header");

		Div left = new Div();
		left.addClassName("left");
		left.add(UIUtils.createH5Label("Permissions for:"));
		if (systemAdmin) {
			left.add(UIUtils.createH5Label("Company:"));
		}
		left.add(UIUtils.createH5Label("Permission Level:"));


		Div right = new Div();
		right.addClassName("right");
		right.add(UIUtils.createH4Label(user.getFullName()));
		if (systemAdmin) {
			right.add(UIUtils.createH4Label(user.getCompanyNameString()));
		}
		right.add(UIUtils.createH4Label(user.getPermissionLevel().getName()));

		header.add(left, right);

		return header;
	}

	private Div constructContent() {
		Div content = new Div();
		content.addClassName(CLASS_NAME + "__content");

		if (systemAdmin) {
			Button addPermissionButton = UIUtils.createButton("Add Permission", VaadinIcon.PLUS_CIRCLE, ButtonVariant.LUMO_PRIMARY);
			addPermissionButton.addClickListener(e -> addPermissionOnClick());
			content.add(addPermissionButton);
		}

		// GRID
		content.add(constructGrid());

		return content;
	}

	private Grid constructGrid() {
		Grid<Permission> grid = new Grid<>();
		grid.addClassNames("grid-view", "small-padding-cell");

		dataProvider = new ListDataProvider<>(user.getPermissionHolder().getPermissions());

		counter = 0;
		permissionChangesTracker = new SimpleChangesTracker<>();

		for (Permission p : dataProvider.getItems()) {
			p.setCounter(counter);

			permissionChangesTracker.getChangesHashMap().put(counter, new Pair<>(new Permission(p), new Permission(p)));
			counter++;
		}

		dataProvider.getItems().removeIf(permission -> (self && !permission.isVisible()));

		grid.setDataProvider(dataProvider);

		grid.addColumn(Permission::getOperationString)
				.setHeader("Operation")
				.setAutoWidth(true)
				.setFlexGrow(1);

		grid.addColumn(Permission::getTargetString)
				.setHeader("Target")
				.setAutoWidth(true)
				.setFlexGrow(2);

		grid.addColumn(Permission::getPermissionOwnString)
				.setHeader("Own")
				.setWidth("60px")
				.setFlexGrow(0);

		grid.addColumn(Permission::getPermissionCompanyString)
				.setHeader("Company")
				.setWidth("60px")
				.setFlexGrow(0);

		if (systemAdmin || (!self && editOthersAllowed)) {
			grid.addComponentColumn(permission -> {
				Button toggleVisibilityButton = UIUtils.createIconButton(VaadinIcon.EYE_SLASH, ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SMALL);

				configureVisibilityButton(toggleVisibilityButton, permission.isVisible());

				toggleVisibilityButton.addClickListener(editEvent -> {
					toggleVisibility(permission, toggleVisibilityButton);
				});

				return toggleVisibilityButton;
			})
			.setTextAlign(ColumnTextAlign.CENTER)
			.setWidth("50px")
			.setFlexGrow(0);
		}

		if (systemAdmin || (!self && editOthersAllowed) || (self && editOwnAllowed)) {
			grid.addComponentColumn(permission -> {
				Button editPermissionButton = UIUtils.createIconButton(VaadinIcon.EDIT, ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SMALL);
				editPermissionButton.addClickListener(editEvent -> {
					editPermissionOnClick(permission);
				});

				return editPermissionButton;
			})
			.setTextAlign(ColumnTextAlign.CENTER)
			.setWidth("50px")
			.setFlexGrow(0);
		}

		if (PermissionFacade.getInstance().isSystemAdminOrAllowedTo(Operation.DELETE, OperationTarget.PERMISSIONS, PermissionRange.COMPANY)) {
			grid.addComponentColumn(permission -> {
				Button removePermissionButton = UIUtils.createIconButton(VaadinIcon.TRASH, ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_SMALL);
				removePermissionButton.addClickListener(removeEvent -> {
					removePermissionOnClick(permission);
				});

				return removePermissionButton;
			})
			.setTextAlign(ColumnTextAlign.CENTER)
			.setWidth("50px")
			.setFlexGrow(0);
		}

		return grid;
	}


	private void addPermissionOnClick() {
		CustomDialog dialog = new CustomDialog();
		dialog.closeOnCancel();
		dialog.setHeader(UIUtils.createH3Label("Permission Details"));

		PermissionLayout layout = new PermissionLayout(null);
		dialog.setContent(layout);

		dialog.getConfirmButton().setText("Save");
		dialog.getConfirmButton().addClickListener(e -> {
			Permission p = layout.getPermission();
			if (p != null) {
				dataProvider.getItems().add(p);
				dataProvider.refreshAll();

				permissionChangesTracker.getChangesHashMap().put(counter, new Pair<>(null, new Permission(p)));
				counter++;
				dialog.close();
			}
		});
		dialog.open();
	}

	private void editPermissionOnClick(Permission permission) {
		CustomDialog dialog = new CustomDialog();
		dialog.closeOnCancel();
		dialog.setHeader(UIUtils.createH3Label("Permission Details"));

		PermissionLayout layout = new PermissionLayout(permission);
		dialog.setContent(layout);

		dialog.getConfirmButton().setText("Save");
		dialog.getConfirmButton().addClickListener(e -> {
			Permission p = layout.getPermission();
			if (p != null) {
				permission.setOperation(p.getOperation());
				permission.setOperationTarget(p.getOperationTarget());
				permission.setPermissionOwn(p.getPermissionOwn());
				permission.setPermissionCompany(p.getPermissionCompany());
				permission.setVisible(p.isVisible());
				dataProvider.refreshItem(permission);

				permissionChangesTracker.getChangesHashMap().get(permission.getCounter()).setObj2(p);
				dialog.close();
			}
		});
		dialog.open();
	}

	private void removePermissionOnClick(Permission permission) {
		permissionChangesTracker.getChangesHashMap().get(permission.getCounter()).setObj2(null);

		dataProvider.getItems().remove(permission);
		dataProvider.refreshAll();
	}

	private void toggleVisibility(Permission permission, Button button) {
		permission.setVisible(!permission.isVisible());
		configureVisibilityButton(button, permission.isVisible());
	}

	private void configureVisibilityButton(Button button, boolean visible) {
		if (visible) {
			button.setIcon(VaadinIcon.EYE.create());
			button.removeThemeVariants(ButtonVariant.LUMO_CONTRAST);
		} else {
			button.setIcon(VaadinIcon.EYE_SLASH.create());
			button.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
		}
	}


	public PermissionHolder getPermissionHolder() {
		return permissionHolder;
	}

	public List<String> getChanges() {
		if (changes == null) {
			return null;
		}

		for (Permission permission : user.getPermissionHolder().getPermissions()) {
			permissionChangesTracker.getChangesHashMap().get(permission.getCounter()).setObj2(permission);
		}

		for (Integer i : permissionChangesTracker.getChangesHashMap().keySet()) {

			Permission p1 = permissionChangesTracker.getChangesHashMap().get(i).getObj1();
			Permission p2 = permissionChangesTracker.getChangesHashMap().get(i).getObj2();

			if (p1 == null) {
				changes.add("Added Permission: " + p2.toString());
				continue;
			}

			if (p2 == null) {
				changes.add("Removed Permission: " + p1.toString());
				continue;
			}

			String p1s = p1.toString();
			String p2s = p2.toString();

			if (!p1s.equals(p2s)) {
				changes.add("Permission changed from:  '" + p1s + "'  to:  '" + p2s + "'");
			}
		}

		return changes;
	}

	public void setChanges(List<String> changes) {
		this.changes = changes;
	}
}
