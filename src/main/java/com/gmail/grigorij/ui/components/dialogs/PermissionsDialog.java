package com.gmail.grigorij.ui.components.dialogs;

import com.gmail.grigorij.backend.database.entities.PermissionHolder;
import com.gmail.grigorij.backend.database.entities.User;
import com.gmail.grigorij.backend.database.entities.embeddable.Permission;
import com.gmail.grigorij.backend.database.enums.operations.Operation;
import com.gmail.grigorij.backend.database.enums.operations.OperationPermission;
import com.gmail.grigorij.backend.database.enums.operations.OperationTarget;
import com.gmail.grigorij.backend.database.enums.permissions.PermissionLevel;
import com.gmail.grigorij.backend.database.enums.permissions.PermissionRange;
import com.gmail.grigorij.backend.database.facades.PermissionFacade;
import com.gmail.grigorij.ui.utils.UIUtils;
import com.gmail.grigorij.utils.authentication.AuthenticationService;
import com.gmail.grigorij.utils.ProjectConstants;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.data.provider.ListDataProvider;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;


public class PermissionsDialog extends CustomDialog {

	private final static String CLASS_NAME = "permissions-dialog";
	private final User user;

	private PermissionHolder permissionHolder;

	private ListDataProvider<Permission> dataProvider;
	private List<String> changes = new ArrayList<>();
	private LinkedHashMap<Integer, PermissionPair> permissionChangesHashMap = new LinkedHashMap<>();

	private static int counter;

	private boolean systemAdmin = false;
	private boolean editOwnAllowed = false;
	private boolean editOthersAllowed = false;
	private boolean self = false;
	private boolean editMode = false;


	public PermissionsDialog(User user) {
		this.user = user;
		permissionHolder = this.user.getPermissionHolder();

		if (permissionHolder == null) {
			permissionHolder = new PermissionHolder();
		}

		setCloseOnEsc(false);
		setCloseOnOutsideClick(false);

		counter = 0;

		systemAdmin = AuthenticationService.getCurrentSessionUser().getPermissionLevel().equalsTo(PermissionLevel.SYSTEM_ADMIN);
		self = user.getId().equals(AuthenticationService.getCurrentSessionUser().getId());

		if (!systemAdmin) {
			editOwnAllowed = PermissionFacade.getInstance().isUserAllowedTo(Operation.EDIT, OperationTarget.PERMISSIONS, PermissionRange.OWN);
			editOthersAllowed = PermissionFacade.getInstance().isUserAllowedTo(Operation.EDIT, OperationTarget.PERMISSIONS, PermissionRange.COMPANY);
		}


		Div wrapper = new Div();
		wrapper.addClassName(CLASS_NAME);

		wrapper.add(constructUserInformation());
		wrapper.add(new Hr());
		wrapper.add(constructContent());

		getContent().add(wrapper);


		getCancelButton().addClickListener(cancelEditOnClick -> {
			ConfirmDialog confirmDialog = new ConfirmDialog();
			confirmDialog.setMessage("Are you sure you want to cancel?" + ProjectConstants.NEW_LINE + "All changes will be lost");
			confirmDialog.closeOnCancel();
			confirmDialog.getConfirmButton().addClickListener(confirmOnClick -> {
				changes = null;
				confirmDialog.close();
				this.close();
			});
			confirmDialog.open();
		});

		getConfirmButton().setText("Save");
		getConfirmButton().setEnabled(false);
	}


	private Div constructUserInformation() {
		Div contentHeader = new Div();
		contentHeader.addClassName(CLASS_NAME + "__content_header");

		Div left = new Div();
		left.addClassName("left");
		left.add(UIUtils.createH5Label("Permissions for:"));
		left.add(UIUtils.createH5Label("Company:"));
		left.add(UIUtils.createH5Label("Permission Level:"));


		Div right = new Div();
		right.addClassName("right");
		right.add(UIUtils.createH4Label(user.getFullName()));
		right.add(UIUtils.createH4Label(user.getCompany() == null ? "" : user.getCompany().getName()));
		right.add(UIUtils.createH4Label(user.getPermissionLevel().getName()));

		contentHeader.add(left, right);

		return contentHeader;
	}

	private Div constructContent() {
		Div content = new Div();
		content.addClassName(CLASS_NAME + "__content");

		if (AuthenticationService.getCurrentSessionUser().getPermissionLevel().equalsTo(PermissionLevel.SYSTEM_ADMIN)) {
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
		grid.addClassNames("grid-view", "permissions-grid");

		dataProvider = new ListDataProvider<>(user.getPermissionHolder().getPermissions());

		for (Permission p : dataProvider.getItems()) {
			p.setCounter(counter);
			permissionChangesHashMap.put(counter, new PermissionPair(new Permission(p), new Permission(p)));
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
				.setFlexGrow(0);

		grid.addColumn(Permission::getPermissionCompanyString)
				.setHeader("Company")
				.setFlexGrow(0);

		if (systemAdmin || (!self && editOthersAllowed)) {
			grid.addComponentColumn(permission -> {
				Button setPermissionVisibleButton = UIUtils.createIconButton(VaadinIcon.EYE_SLASH, ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SMALL);

				configureVisibilityButton(setPermissionVisibleButton, permission.isVisible());

				setPermissionVisibleButton.addClickListener(editEvent -> {
					toggleVisibility(permission, setPermissionVisibleButton);
				});

				return setPermissionVisibleButton;
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
		CustomDialog permissionEditDialog = new CustomDialog();
		permissionEditDialog.closeOnCancel();
		permissionEditDialog.setHeader(UIUtils.createH3Label("Permission Details"));

		PermissionLayout layout = new PermissionLayout(null);
		permissionEditDialog.setContent(layout);

		permissionEditDialog.getConfirmButton().setText("Save");
		permissionEditDialog.getConfirmButton().addClickListener(e -> {
			Permission p = getValidPermission(layout);
			if (p != null) {
				dataProvider.getItems().add(p);
				dataProvider.refreshAll();

				permissionChangesHashMap.put(counter, new PermissionPair(null, new Permission(p)));
				counter++;
				permissionEditDialog.close();
			}
		});
		permissionEditDialog.open();
	}

	private void editPermissionOnClick(Permission permission) {
		CustomDialog permissionEditDialog = new CustomDialog();
		permissionEditDialog.closeOnCancel();
		permissionEditDialog.setHeader(UIUtils.createH3Label("Permission Details"));

		PermissionLayout layout = new PermissionLayout(permission);
		permissionEditDialog.setContent(layout);

		permissionEditDialog.getConfirmButton().setText("Save");
		permissionEditDialog.getConfirmButton().addClickListener(e -> {
			Permission p = getValidPermission(layout);
			if (p != null) {
				permission.setOperation(p.getOperation());
				permission.setOperationTarget(p.getOperationTarget());
				permission.setPermissionOwn(p.getPermissionOwn());
				permission.setPermissionCompany(p.getPermissionCompany());
				permission.setVisible(p.isVisible());
				dataProvider.refreshItem(permission);

				permissionChangesHashMap.get(permission.getCounter()).setP2(p);

				permissionEditDialog.close();
			}
		});
		permissionEditDialog.open();
	}

	private void removePermissionOnClick(Permission permission) {
		permissionChangesHashMap.get(permission.getCounter()).setP2(null);

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

	private Permission getValidPermission(PermissionLayout layout) {
		Permission permission = new Permission();

		if (layout.getOperationComboBox().getValue() == null || layout.getOperationComboBox().isInvalid()) {
			return null;
		} else {
			permission.setOperation(layout.getOperationComboBox().getValue());
		}

		if (layout.getTargetComboBox().getValue() == null || layout.getTargetComboBox().isInvalid()) {
			return null;
		} else {
			permission.setOperationTarget(layout.getTargetComboBox().getValue());
		}

		if (layout.getPermissionOwnComboBox().getValue() == null || layout.getPermissionOwnComboBox().isInvalid()) {
			return null;
		} else {
			permission.setPermissionOwn(layout.getPermissionOwnComboBox().getValue());
		}

		if (layout.getPermissionCompanyComboBox().getValue() == null || layout.getPermissionCompanyComboBox().isInvalid()) {
			return null;
		} else {
			permission.setPermissionCompany(layout.getPermissionCompanyComboBox().getValue());
		}

		return permission;
	}


	private boolean validate() {
		for (Permission permission : user.getPermissionHolder().getPermissions()) {
			permissionChangesHashMap.get(permission.getCounter()).setP2(permission);
		}

		return true;
	}


	public PermissionHolder getPermissionHolder() {
		if (validate()) {
			return permissionHolder;
		} else {
			return null;
		}
	}

	public List<String> getChanges() {

		if (changes == null) {
			return null;
		}

		for (Integer i : permissionChangesHashMap.keySet()) {

			Permission p1 = permissionChangesHashMap.get(i).getP1();
			Permission p2 = permissionChangesHashMap.get(i).getP2();

			if (p1 == null) {
				changes.add("Added Permission: " + getPermissionString(p2));
				continue;
			}

			if (p2 == null) {
				changes.add("Removed Permission: " + getPermissionString(p1));
				continue;
			}

			String p1s = getPermissionString(p1);
			String p2s = getPermissionString(p2);

			if (!p1s.equals(p2s)) {
				changes.add("Permission changed from:  '" + p1s + "'\nto:  '" + p2s + "'");
			}
		}

		return changes;
	}


	private String getPermissionString(Permission p) {
		String ps = "";

		if (p != null) {
			ps = p.getOperation().getName() + " " + p.getOperationTarget().getName() + ", " +
					PermissionRange.OWN.getName() + ": " + p.getPermissionOwn().getName() + ", " +
					PermissionRange.COMPANY.getName() + ": " + p.getPermissionCompany().getName() + ", " +
					"visible: " + p.isVisible();
		}

		return ps;
	}


//	private static class PermissionLayout extends Div {
//
//		private ComboBox<Operation> operationComboBox;
//		private ComboBox<OperationTarget> targetComboBox;
//		private ComboBox<OperationPermission> permissionOwnComboBox;
//		private ComboBox<OperationPermission> permissionCompanyComboBox;
//
//
//		PermissionLayout(Permission permission) {
//			addClassName(CLASS_NAME + "__permission");
//
//
//			// OPERATION
//			List<Operation> operations = new ArrayList<>(EnumSet.allOf(Operation.class));
//			operations.removeIf(target -> target.getMinimalPermissionLevel().higherThan(AuthenticationService.getCurrentSessionUser().getPermissionLevel()));
//
//			operationComboBox = new ComboBox<>();
//			operationComboBox.setLabel("Operation");
//			operationComboBox.setItems(operations);
//			operationComboBox.setItemLabelGenerator(Operation::getName);
//			operationComboBox.setErrorMessage(ProjectConstants.VALUE_REQUIRED);
//			operationComboBox.setRequired(true);
//			if (permission != null) {
//				operationComboBox.setValue(permission.getOperation());
//			}
//			add(operationComboBox);
//
//
//			// TARGET
//			List<OperationTarget> targets = new ArrayList<>(EnumSet.allOf(OperationTarget.class));
//			targets.removeIf(target -> target.getMinimalPermissionLevel().higherThan(AuthenticationService.getCurrentSessionUser().getPermissionLevel()));
//
//			targetComboBox = new ComboBox<>();
//			targetComboBox.setLabel("Target");
//			targetComboBox.setItems(targets);
//			targetComboBox.setItemLabelGenerator(OperationTarget::getName);
//			targetComboBox.setErrorMessage(ProjectConstants.VALUE_REQUIRED);
//			targetComboBox.setRequired(true);
//			if (permission != null) {
//				targetComboBox.setValue(permission.getOperationTarget());
//			}
//			add(targetComboBox);
//
//
//			// OWN
//			permissionOwnComboBox = new ComboBox<>();
//			permissionOwnComboBox.setLabel(PermissionRange.OWN.getName());
//			permissionOwnComboBox.setItems(EnumSet.allOf(OperationPermission.class));
//			permissionOwnComboBox.setItemLabelGenerator(OperationPermission::getName);
//			permissionOwnComboBox.setErrorMessage(ProjectConstants.VALUE_REQUIRED);
//			permissionOwnComboBox.setRequired(true);
//			if (permission != null) {
//				permissionOwnComboBox.setValue(permission.getPermissionOwn());
//			} else {
//				permissionOwnComboBox.setValue(OperationPermission.NO);
//			}
//			add(permissionOwnComboBox);
//
//
//			// COMPANY
//			permissionCompanyComboBox = new ComboBox<>();
//			permissionCompanyComboBox.setLabel(PermissionRange.COMPANY.getName());
//			permissionCompanyComboBox.setItems(EnumSet.allOf(OperationPermission.class));
//			permissionCompanyComboBox.setItemLabelGenerator(OperationPermission::getName);
//			permissionCompanyComboBox.setErrorMessage(ProjectConstants.VALUE_REQUIRED);
//			permissionCompanyComboBox.setRequired(true);
//			if (permission != null) {
//				permissionCompanyComboBox.setValue(permission.getPermissionCompany());
//			} else {
//				permissionCompanyComboBox.setValue(OperationPermission.NO);
//			}
//			add(permissionCompanyComboBox);
//		}
//
//
//		private ComboBox<Operation> getOperationComboBox() {
//			return operationComboBox;
//		}
//		private ComboBox<OperationTarget> getTargetComboBox() {
//			return targetComboBox;
//		}
//		private ComboBox<OperationPermission> getPermissionOwnComboBox() {
//			return permissionOwnComboBox;
//		}
//		private ComboBox<OperationPermission> getPermissionCompanyComboBox() {
//			return permissionCompanyComboBox;
//		}
//	}

	private static class PermissionLayout extends Div {

		private ComboBox<Operation> operationComboBox;
		private ComboBox<OperationTarget> targetComboBox;
		private ComboBox<OperationPermission> permissionOwnComboBox;
		private ComboBox<OperationPermission> permissionCompanyComboBox;


		PermissionLayout(Permission permission) {
			addClassName(CLASS_NAME + "__permission");


			// OPERATION
			List<Operation> operations = new ArrayList<>(EnumSet.allOf(Operation.class));
			operations.removeIf(target -> target.getMinimalPermissionLevel().higherThan(AuthenticationService.getCurrentSessionUser().getPermissionLevel()));

			operationComboBox = new ComboBox<>();
			operationComboBox.setLabel("Operation");
			operationComboBox.setItems(operations);
			operationComboBox.setItemLabelGenerator(Operation::getName);
			operationComboBox.setErrorMessage(ProjectConstants.VALUE_REQUIRED);
			operationComboBox.setRequired(true);
			if (permission != null) {
				operationComboBox.setValue(permission.getOperation());
			}
			operationComboBox.addValueChangeListener(operationChangeEvent -> {
				if (operationChangeEvent.getValue() != operationChangeEvent.getOldValue()) {

					List<OperationTarget> targets = new ArrayList<>(EnumSet.allOf(OperationTarget.class));
					targets.removeIf(target -> target.getMinimalPermissionLevel().higherThan(AuthenticationService.getCurrentSessionUser().getPermissionLevel()));
					targets.removeIf(target -> {
						System.out.println("T: " + target.getForOperation());
						if (target.getForOperation() != null) {
							for (Operation operation : target.getForOperation()) {
								if (operation.equals(operationChangeEvent.getValue())) {
									return false;
								}
							}
						}
						return true;
					});
					targetComboBox.setItems(targets);
				}
			});
			add(operationComboBox);


			targetComboBox = new ComboBox<>();
			targetComboBox.setLabel("Target");
			targetComboBox.setItems();
			targetComboBox.setItemLabelGenerator(OperationTarget::getName);
			targetComboBox.setErrorMessage(ProjectConstants.VALUE_REQUIRED);
			targetComboBox.setRequired(true);
			if (permission != null) {
				targetComboBox.setValue(permission.getOperationTarget());
			}
			add(targetComboBox);


			// OWN
			permissionOwnComboBox = new ComboBox<>();
			permissionOwnComboBox.setLabel(PermissionRange.OWN.getName());
			permissionOwnComboBox.setItems(EnumSet.allOf(OperationPermission.class));
			permissionOwnComboBox.setItemLabelGenerator(OperationPermission::getName);
			permissionOwnComboBox.setErrorMessage(ProjectConstants.VALUE_REQUIRED);
			permissionOwnComboBox.setRequired(true);
			if (permission != null) {
				permissionOwnComboBox.setValue(permission.getPermissionOwn());
			} else {
				permissionOwnComboBox.setValue(OperationPermission.NO);
			}
			add(permissionOwnComboBox);


			// COMPANY
			permissionCompanyComboBox = new ComboBox<>();
			permissionCompanyComboBox.setLabel(PermissionRange.COMPANY.getName());
			permissionCompanyComboBox.setItems(EnumSet.allOf(OperationPermission.class));
			permissionCompanyComboBox.setItemLabelGenerator(OperationPermission::getName);
			permissionCompanyComboBox.setErrorMessage(ProjectConstants.VALUE_REQUIRED);
			permissionCompanyComboBox.setRequired(true);
			if (permission != null) {
				permissionCompanyComboBox.setValue(permission.getPermissionCompany());
			} else {
				permissionCompanyComboBox.setValue(OperationPermission.NO);
			}
			add(permissionCompanyComboBox);
		}


		private ComboBox<Operation> getOperationComboBox() {
			return operationComboBox;
		}
		private ComboBox<OperationTarget> getTargetComboBox() {
			return targetComboBox;
		}
		private ComboBox<OperationPermission> getPermissionOwnComboBox() {
			return permissionOwnComboBox;
		}
		private ComboBox<OperationPermission> getPermissionCompanyComboBox() {
			return permissionCompanyComboBox;
		}
	}

	private static class PermissionPair {

		private Permission p1;
		private Permission p2;

		PermissionPair(Permission p1, Permission p2) {
			this.p1 = p1;
			this.p2 = p2;
		}

		private Permission getP1() {
			return p1;
		}
		private void setP1(Permission p1) {
			this.p1 = p1;
		}

		private Permission getP2() {
			return p2;
		}
		private void setP2(Permission p2) {
			this.p2 = p2;
		}

		private boolean isNull() {
			return this.p1 == null && this.p2 == null;
		}
	}
}
