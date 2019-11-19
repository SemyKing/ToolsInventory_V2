//package com.gmail.grigorij.ui.components.dialogs;
//
//import com.gmail.grigorij.backend.database.entities.User;
//import com.gmail.grigorij.backend.database.entities.embeddable.Permission;
//import com.gmail.grigorij.backend.database.enums.operations.Operation;
//import com.gmail.grigorij.backend.database.enums.operations.OperationPermission;
//import com.gmail.grigorij.backend.database.enums.operations.OperationTarget;
//import com.gmail.grigorij.backend.database.enums.permissions.PermissionLevel;
//import com.gmail.grigorij.backend.database.enums.permissions.PermissionRange;
//import com.gmail.grigorij.backend.database.facades.PermissionFacade;
//import com.gmail.grigorij.ui.components.FlexBoxLayout;
//import com.gmail.grigorij.ui.components.ListItem;
//import com.gmail.grigorij.ui.utils.UIUtils;
//import com.gmail.grigorij.utils.authentication.AuthenticationService;
//import com.gmail.grigorij.utils.ProjectConstants;
//import com.vaadin.flow.component.button.Button;
//import com.vaadin.flow.component.button.ButtonVariant;
//import com.vaadin.flow.component.combobox.ComboBox;
//import com.vaadin.flow.component.html.Div;
//import com.vaadin.flow.component.html.Hr;
//import com.vaadin.flow.component.icon.VaadinIcon;
//
//import java.util.ArrayList;
//import java.util.EnumSet;
//import java.util.LinkedHashMap;
//import java.util.List;
//
//
//public class PermissionsDialogOld extends CustomDialog {
//
//	private final static String CLASS_NAME = "permissions-dialog";
//	private final User user;
//
//	private List<Permission> permissions = new ArrayList<>();
//	private List<PermissionRowLayout> permissionRows = new ArrayList<>();
//
//	private List<String> changes = new ArrayList<>();
//	private LinkedHashMap<Integer, PermissionPair> permissionChangesHashMap = new LinkedHashMap<>();
//
//	private FlexBoxLayout content;
//	private Div contentHeader;
//
//	private boolean systemAdmin = false;
//	private boolean editOwnAllowed = false;
//	private boolean editOthersAllowed = false;
//	private boolean self = false;
//
//
//	public PermissionsDialogOld(User user) {
//		this.user = user;
//
//		PermissionRowLayout.instanceCounter = 0;
//
//		for (Permission p : user.getPermissions()) {
//			permissions.add(new Permission(p));
//		}
//
//		setCloseOnEsc(false);
//		setCloseOnOutsideClick(false);
//
//		systemAdmin = AuthenticationService.getCurrentSessionUser().getPermissionLevel().equalsTo(PermissionLevel.SYSTEM_ADMIN);
//
//		if (!systemAdmin) {
//			editOwnAllowed = PermissionFacade.getInstance().isUserAllowedTo(Operation.EDIT, OperationTarget.PERMISSIONS, PermissionRange.OWN);
//			editOthersAllowed = PermissionFacade.getInstance().isUserAllowedTo(Operation.EDIT, OperationTarget.PERMISSIONS, PermissionRange.COMPANY);
//		}
//
//		self = user.getId().equals(AuthenticationService.getCurrentSessionUser().getId());
//
//		getContent().add(constructContentHeader());
//		getContent().add(new Hr());
//		getContent().add(constructContent());
//
//		// CANNOT ADD PERMISSIONS TO SELF
//		if (!self) {
//			if (PermissionFacade.getInstance().isSystemAdminOrAllowedTo(Operation.ADD, OperationTarget.PERMISSIONS, PermissionRange.COMPANY)) {
//				Button addPermissionButton = UIUtils.createButton("Add Permission", VaadinIcon.PLUS, ButtonVariant.LUMO_PRIMARY);
//				addPermissionButton.getElement().getStyle().set("min-height", "31.5px");
//				addPermissionButton.addClickListener(e -> {
//					PermissionRowLayout permissionRow = constructPermissionRow(null);
//					content.add(permissionRow);
//				});
//
//				getContent().add(addPermissionButton);
//			}
//		}
//
//		getCancelButton().addClickListener(cancelEditOnClick -> {
//			ConfirmDialog confirmDialog = new ConfirmDialog();
//			confirmDialog.setMessage("Are you sure you want to cancel?" + ProjectConstants.NEW_LINE + "All changes will be lost");
//			confirmDialog.closeOnCancel();
//			confirmDialog.getConfirmButton().addClickListener(confirmOnClick -> {
//				changes = null;
//				confirmDialog.close();
//				this.close();
//			});
//			confirmDialog.open();
//		});
//
//		getConfirmButton().setText("Save");
//	}
//
//
//	private Div constructContentHeader() {
//		contentHeader = new Div();
//		contentHeader.addClassName(CLASS_NAME + "__content_header");
//
//		return contentHeader;
//	}
//
//	private FlexBoxLayout constructContent() {
//		content = new FlexBoxLayout();
//		content.addClassName(CLASS_NAME + "__content");
//
//		return content;
//	}
//
//
//	public void constructView() {
//		Div left = new Div();
//		left.addClassName("left");
//		left.add(UIUtils.createH5Label("Permissions for:"));
//		left.add(UIUtils.createH5Label("Company:"));
//		left.add(UIUtils.createH5Label("Permission Level:"));
//
//
//		Div right = new Div();
//		right.addClassName("right");
//		right.add(UIUtils.createH4Label(user.getFullName()));
//		right.add(UIUtils.createH4Label(user.getCompany() == null ? "" : user.getCompany().getName()));
//		right.add(UIUtils.createH4Label(user.getPermissionLevel().getName()));
//
//		contentHeader.add(left, right);
//
//		populateData();
//	}
//
//	private void populateData() {
//		for (Permission permission : permissions) {
//			if (systemAdmin || (self && permission.isVisible()) || !self) {
//				content.add(constructPermissionRow(permission));
//			}
//		}
//	}
//
//	private PermissionRowLayout constructPermissionRow(Permission permission) {
//
//		PermissionRowLayout permissionRow = null;
//
//		if (systemAdmin) {
//			permissionRow = new PermissionRowLayout(permission, true);
//		} else {
//
//			// READ ONLY
//			if (!editOthersAllowed || (self && !editOwnAllowed)) {
//				permissionRow = new PermissionRowLayout(permission, false);
//				permissionRow.removeToggleVisibleButton();
//				permissionRow.removeDeleteButton();
//
//			// EDIT
//			} else {
//				permissionRow = new PermissionRowLayout(permission, true);
//				if (!PermissionFacade.getInstance().isSystemAdminOrAllowedTo(Operation.DELETE, OperationTarget.PERMISSIONS, PermissionRange.COMPANY)) {
//					permissionRow.removeDeleteButton();
//				}
//			}
//		}
//
//
//		if (permission == null) {
//			permissionChangesHashMap.put(permissionRow.getCounter(), new PermissionPair(null, null));
//		} else {
//			permissionChangesHashMap.put(permissionRow.getCounter(),
//					new PermissionPair(new Permission(permission), new Permission(permission)));
//		}
//
////		PermissionRowLayout permissionRow = new PermissionRowLayout();
////
////		if (permission != null) {
////			permissionChangesHashMap.put(permissionRow.getCounter(),
////					new PermissionPair(new Permission(permission), new Permission(permission)));
////
////			permissionRow.getOperationComboBox().setValue(permission.getOperation());
////			permissionRow.getTargetComboBox().setValue(permission.getOperationTarget());
////			permissionRow.getPermissionOwnComboBox().setValue(permission.getPermissionOwn());
////			permissionRow.getPermissionCompanyComboBox().setValue(permission.getPermissionCompany());
////
////			if (systemAdmin || !self) {
////				permissionRow.addVisibleButton();
////				permissionRow.setPermissionVisible(permission.isVisible());
////
////				if (PermissionFacade.getInstance().isSystemAdminOrAllowedTo(Operation.DELETE, OperationTarget.PERMISSIONS, PermissionRange.COMPANY)) {
////					initDeleteButton(permissionRow);
////				}
////			}
////
////			if (!systemAdmin) {
////				if (!editOthersAllowed || (self && !editOwnAllowed)) {
////
////					permissionRow.getOperationComboBox().setEnabled(false);
////					permissionRow.getTargetComboBox().setEnabled(false);
////					permissionRow.getPermissionOwnComboBox().setEnabled(false);
////					permissionRow.getPermissionCompanyComboBox().setEnabled(false);
////					permissionRow.getToggleVisibleButton().setEnabled(false);
////				}
////			}
////		} else {
////			permissionChangesHashMap.put(permissionRow.getCounter(), new PermissionPair(null, null));
////
////			if (systemAdmin || !self) {
////				permissionRow.addVisibleButton();
////
////				if (PermissionFacade.getInstance().isSystemAdminOrAllowedTo(Operation.DELETE, OperationTarget.PERMISSIONS, PermissionRange.COMPANY)) {
////					initDeleteButton(permissionRow);
////				}
////			}
////		}
//
//		permissionRows.add(permissionRow);
//
//		return permissionRow;
//	}
//
////	private void initDeleteButton(PermissionRowLayout permissionRow) {
////		permissionRow.addDeleteButton();
////		permissionRow.getDeleteButton().addClickListener(e -> {
////			permissionChangesHashMap.get(permissionRow.getCounter()).setP2(null);
////
////			content.remove(permissionRow);
////			permissionRows.remove(permissionRow);
////		});
////	}
//
//
//	private boolean validate() {
//		if (!systemAdmin) {
//
//			// EDIT NOT ALLOWED -> RETURN ORIGINAL PERMISSIONS
//			if (!editOwnAllowed || !editOthersAllowed) {
//				return true;
//			}
//		}
//
//		permissions.clear();
//
//		// COMPANY ADMIN
//		if (self) {
//
//			// ADD HIDDEN PERMISSIONS
//			for (Permission permission : user.getPermissions()) {
//				if (!permission.isVisible()) {
//					permissions.add(new Permission(permission));
//				}
//			}
//		}
//
//		for (PermissionRowLayout permissionRow : permissionRows) {
//			if (permissionRow.getOperationComboBox().getValue() == null) {
//				permissionRow.getOperationComboBox().setInvalid(true);
//				return false;
//			}
//
//			if (permissionRow.getTargetComboBox().getValue() == null) {
//				permissionRow.getTargetComboBox().setInvalid(true);
//				return false;
//			}
//
//			if (permissionRow.getPermissionOwnComboBox().getValue() == null) {
//				permissionRow.getPermissionOwnComboBox().setInvalid(true);
//				return false;
//			}
//
//			if (permissionRow.getPermissionCompanyComboBox().getValue() == null) {
//				permissionRow.getPermissionCompanyComboBox().setInvalid(true);
//				return false;
//			}
//
//			Permission permission = new Permission();
//			permission.setOperation(permissionRow.getOperationComboBox().getValue());
//			permission.setOperationTarget(permissionRow.getTargetComboBox().getValue());
//			permission.setPermissionOwn(permissionRow.getPermissionOwnComboBox().getValue());
//			permission.setPermissionCompany(permissionRow.getPermissionCompanyComboBox().getValue());
//			permission.setVisible(permissionRow.isPermissionVisible());
//
//			permissions.add(permission);
//			permissionChangesHashMap.get(permissionRow.getCounter()).setP2(permission);
//		}
//
//		return true;
//	}
//
//
//	public List<Permission> getPermissions() {
//		if (validate()) {
//			return permissions;
//		} else {
//			return null;
//		}
//	}
//
//	public List<String> getChanges() {
//
//		if (changes == null) {
//			return null;
//		}
//
//		for (Integer i : permissionChangesHashMap.keySet()) {
//
//			Permission p1 = permissionChangesHashMap.get(i).getP1();
//			Permission p2 = permissionChangesHashMap.get(i).getP2();
//
//			if (p1 == null) {
//				changes.add("Added Permission: " + getPermissionString(p2));
//				continue;
//			}
//
//			if (p2 == null) {
//				changes.add("Removed Permission: " + getPermissionString(p1));
//				continue;
//			}
//
//			String p1s = getPermissionString(p1);
//			String p2s = getPermissionString(p2);
//
//			if (!p1s.equals(p2s)) {
//				changes.add("Permission changed from:  '" + p1s + "'\nto:  '" + p2s + "'");
//			}
//		}
//
//		return changes;
//	}
//
//
//	private String getPermissionString(Permission p) {
//		String ps = "";
//
//		if (p != null) {
//			ps = p.getOperation().getName() + " " + p.getOperationTarget().getName() + ", " +
//					PermissionRange.OWN.getName() + ": " + p.getPermissionOwn().getName() + ", " +
//					PermissionRange.COMPANY.getName() + ": " + p.getPermissionCompany().getName();
//		}
//
//		return ps;
//	}
//
//	private static class PermissionRowLayout extends Div {
//
//		private final static String CLASS_NAME = "permission-row";
//
//		private static int instanceCounter = 0;
//		private int counter;
//
//
//		private Div rowDiv;
//		private Div operationTargetOwnCompanyDiv;
//		private Div operationTargetDiv;
//		private Div ownCompanyDiv;
//		private Div buttonsDiv;
//
//		// EDITABLE
//		private ComboBox<Operation> operationComboBox;
//		private ComboBox<OperationTarget> targetComboBox;
//		private ComboBox<OperationPermission> permissionOwnComboBox;
//		private ComboBox<OperationPermission> permissionCompanyComboBox;
//
//		private boolean permissionVisible = false;
//		private Button toggleVisibleButton;
//		private Button deleteButton;
//
//
//		PermissionRowLayout(Permission p, boolean editable) {
//			addClassName(CLASS_NAME);
//
//			add(constructContent(p, editable));
//			add(new Hr());
//
//			counter = instanceCounter;
//			instanceCounter++;
//		}
//
//		private Div constructContent(Permission p, boolean editable) {
//			rowDiv = new Div();
//			rowDiv.addClassName(CLASS_NAME + "__content");
//
//			operationTargetDiv = new Div();
//			operationTargetDiv.addClassName(CLASS_NAME + "__operation-target");
//
//			ownCompanyDiv = new Div();
//			ownCompanyDiv.addClassName(CLASS_NAME + "__own-company");
//
//			buttonsDiv = new Div();
//			buttonsDiv.addClassName(CLASS_NAME + "__buttons");
//
//			operationTargetOwnCompanyDiv = new Div();
//			operationTargetOwnCompanyDiv.addClassName(CLASS_NAME + "__operation-target-own-company");
//
//			operationTargetOwnCompanyDiv.add(operationTargetDiv);
//			operationTargetOwnCompanyDiv.add(ownCompanyDiv);
//
//			rowDiv.add(operationTargetOwnCompanyDiv);
//			rowDiv.add(buttonsDiv);
//
//			if (editable) {
//				constructEditableContent(p);
//			} else {
//				constructReadOnlyContent(p);
//			}
//
//			return rowDiv;
//		}
//
//
//		private void constructEditableContent(Permission p) {
//			List<Operation> operations = new ArrayList<>(EnumSet.allOf(Operation.class));
//			operations.removeIf(operation -> operation.getMinimalPermissionLevel().higherThan(AuthenticationService.getCurrentSessionUser().getPermissionLevel()));
//
//			operationComboBox = new ComboBox<>();
//			operationComboBox.addClassName(ProjectConstants.NO_PADDING_TOP);
//			operationComboBox.setLabel("Operation");
//			operationComboBox.setItems(operations);
//			operationComboBox.setItemLabelGenerator(Operation::getName);
//			operationComboBox.setErrorMessage(ProjectConstants.VALUE_REQUIRED);
//			operationComboBox.setRequired(true);
//			if (p != null) {
//				operationComboBox.setValue(p.getOperation());
//			}
//			operationComboBox.addValueChangeListener(e -> operationComboBox.setInvalid(false));
//			operationTargetDiv.add(operationComboBox);
//
//			List<OperationTarget> targets = new ArrayList<>(EnumSet.allOf(OperationTarget.class));
//			targets.removeIf(target -> target.getMinimalPermissionLevel().higherThan(AuthenticationService.getCurrentSessionUser().getPermissionLevel()));
//
//			targetComboBox = new ComboBox<>();
//			targetComboBox.addClassName(ProjectConstants.NO_PADDING_TOP);
//			targetComboBox.setLabel("Target");
//			targetComboBox.setItems(targets);
//			targetComboBox.setItemLabelGenerator(OperationTarget::getName);
//			targetComboBox.setErrorMessage(ProjectConstants.VALUE_REQUIRED);
//			targetComboBox.setRequired(true);
//			if (p != null) {
//				targetComboBox.setValue(p.getOperationTarget());
//			}
//			targetComboBox.addValueChangeListener(e -> targetComboBox.setInvalid(false));
//			operationTargetDiv.add(targetComboBox);
//
//
//			permissionOwnComboBox = new ComboBox<>();
//			permissionOwnComboBox.addClassName(ProjectConstants.NO_PADDING_TOP);
//			permissionOwnComboBox.setLabel(PermissionRange.OWN.getName());
//			permissionOwnComboBox.setItems(EnumSet.allOf(OperationPermission.class));
//			permissionOwnComboBox.setItemLabelGenerator(OperationPermission::getName);
//			permissionOwnComboBox.setErrorMessage(ProjectConstants.VALUE_REQUIRED);
//			permissionOwnComboBox.setRequired(true);
//			if (p != null) {
//				permissionOwnComboBox.setValue(p.getPermissionOwn());
//			} else {
//				permissionOwnComboBox.setValue(OperationPermission.NO);
//			}
//			permissionOwnComboBox.addValueChangeListener(e -> permissionOwnComboBox.setInvalid(false));
//			ownCompanyDiv.add(permissionOwnComboBox);
//
//			permissionCompanyComboBox = new ComboBox<>();
//			permissionCompanyComboBox.addClassName(ProjectConstants.NO_PADDING_TOP);
//			permissionCompanyComboBox.setLabel(PermissionRange.OWN.getName());
//			permissionCompanyComboBox.setItems(EnumSet.allOf(OperationPermission.class));
//			permissionCompanyComboBox.setItemLabelGenerator(OperationPermission::getName);
//			permissionCompanyComboBox.setErrorMessage(ProjectConstants.VALUE_REQUIRED);
//			permissionCompanyComboBox.setRequired(true);
//			if (p != null) {
//				permissionCompanyComboBox.setValue(p.getPermissionCompany());
//			} else {
//				permissionCompanyComboBox.setValue(OperationPermission.NO);
//			}
//			permissionCompanyComboBox.addValueChangeListener(e -> permissionCompanyComboBox.setInvalid(false));
//			ownCompanyDiv.add(permissionCompanyComboBox);
//
//
//			toggleVisibleButton = UIUtils.createIconButton(VaadinIcon.EYE_SLASH, ButtonVariant.LUMO_PRIMARY);
//			toggleVisibleButton.addClickListener(e -> toggleVisibility());
//			buttonsDiv.add(toggleVisibleButton);
//			if (p != null) {
//				setPermissionVisible(p.isVisible());
//			}
//			configureVisibilityButton();
//
//			deleteButton = UIUtils.createIconButton(VaadinIcon.TRASH, ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);
//			buttonsDiv.add(deleteButton);
//		}
//
//		private void constructReadOnlyContent(Permission p) {
//			// READ ONLY
//			ListItem operationItem = new ListItem("Operation", p.getOperation().getName(), ListItem.Direction.COLUMN);
//			operationTargetDiv.add(operationItem);
//
//			ListItem targetItem = new ListItem("Target", p.getOperationTarget().getName(), ListItem.Direction.COLUMN);
//			operationTargetDiv.add(targetItem);
//
//			ListItem permissionOwnItem = new ListItem(PermissionRange.OWN.getName(), p.getPermissionOwn().getName(), ListItem.Direction.COLUMN);
//			ownCompanyDiv.add(permissionOwnItem);
//
//			ListItem permissionCompanyItem = new ListItem(PermissionRange.COMPANY.getName(), p.getPermissionCompany().getName(), ListItem.Direction.COLUMN);
//			ownCompanyDiv.add(permissionCompanyItem);
//		}
//
//
//		private ComboBox<Operation> getOperationComboBox() {
//			return operationComboBox;
//		}
//
//		private ComboBox<OperationTarget> getTargetComboBox() {
//			return targetComboBox;
//		}
//
//		private ComboBox<OperationPermission> getPermissionOwnComboBox() {
//			return permissionOwnComboBox;
//		}
//
//		private ComboBox<OperationPermission> getPermissionCompanyComboBox() {
//			return permissionCompanyComboBox;
//		}
//
//		private Button getToggleVisibleButton() {
//			return toggleVisibleButton;
//		}
//
//		private Button getDeleteButton() {
//			return deleteButton;
//		}
//
//
//		private void removeToggleVisibleButton() {
//			if (toggleVisibleButton != null) {
//				try {
//					buttonsDiv.remove(toggleVisibleButton);
//				} catch (Exception ignored) {}
//			}
//		}
//
//		private void removeDeleteButton() {
//			if (deleteButton != null) {
//				try {
//					buttonsDiv.remove(deleteButton);
//				} catch (Exception ignored) {}
//			}
//		}
//
//
//		private boolean isPermissionVisible() {
//			return permissionVisible;
//		}
//
//		private void setPermissionVisible(boolean visible) {
//			permissionVisible = visible;
//
//			configureVisibilityButton();
//		}
//
//		private void toggleVisibility() {
//			permissionVisible = !permissionVisible;
//
//			configureVisibilityButton();
//		}
//
//		private void configureVisibilityButton() {
//			if (permissionVisible) {
//				toggleVisibleButton.setIcon(VaadinIcon.EYE.create());
//				toggleVisibleButton.removeThemeVariants(ButtonVariant.LUMO_CONTRAST);
//			} else {
//				toggleVisibleButton.setIcon(VaadinIcon.EYE_SLASH.create());
//				toggleVisibleButton.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
//			}
//		}
//
//		private int getCounter() {
//			return counter;
//		}
//	}
//
//
////	private static class PermissionRowLayout extends Div {
////
////		private final static String WRAPPER_DIV = "row-wrapper";
////		private final static String ROW = "permission-row";
////
////		private static int instanceCounter = 0;
////		private int counter;
////
////		private Div permissionsAndActionsDiv;
////
////		private ComboBox<Operation> operationComboBox;
////		private ComboBox<OperationTarget> targetComboBox;
////		private ComboBox<OperationPermission> permissionOwnComboBox;
////		private ComboBox<OperationPermission> permissionCompanyComboBox;
////
////		private boolean permissionVisible = false;
////		private Button toggleVisibleButton;
////
////		private Button deleteButton;
////
////
////		PermissionRowLayout() {
////			addClassName(WRAPPER_DIV);
////
////			add(constructContent());
////			add(new Hr());
////
////			counter = instanceCounter;
////			instanceCounter++;
////		}
////
////		private Div constructContent() {
////			Div contentRow = new Div();
////			contentRow.addClassName(ROW);
////
////			Div operationAndTargetDiv = new Div();
////			operationAndTargetDiv.addClassName(ROW + "__ot");
////
////			List<Operation> operations = new ArrayList<>(EnumSet.allOf(Operation.class));
////			operations.removeIf(operation -> operation.getMinimalPermissionLevel().higherThan(AuthenticationService.getCurrentSessionUser().getPermissionLevel()));
////
////			operationComboBox = new ComboBox<>();
////			operationComboBox.addClassName(ProjectConstants.NO_PADDING_TOP);
////			operationComboBox.setLabel("Operation");
////			operationComboBox.setItems(operations);
////			operationComboBox.setItemLabelGenerator(Operation::getName);
////			operationComboBox.setErrorMessage("Value Required");
////			operationComboBox.setRequired(true);
////			operationComboBox.addValueChangeListener(e -> {
////				operationComboBox.setInvalid(false);
////			});
////
////			List<OperationTarget> targets = new ArrayList<>(EnumSet.allOf(OperationTarget.class));
////			targets.removeIf(target -> target.getMinimalPermissionLevel().higherThan(AuthenticationService.getCurrentSessionUser().getPermissionLevel()));
////
////			targetComboBox = new ComboBox<>();
////			targetComboBox.addClassName(ProjectConstants.NO_PADDING_TOP);
////			targetComboBox.setLabel("Target");
////			targetComboBox.setItems(targets);
////			targetComboBox.setItemLabelGenerator(OperationTarget::getName);
////			targetComboBox.setErrorMessage("Value Required");
////			targetComboBox.setRequired(true);
////			targetComboBox.addValueChangeListener(e -> {
////				targetComboBox.setInvalid(false);
////			});
////
////			operationAndTargetDiv.add(operationComboBox, targetComboBox);
////
////
////			permissionsAndActionsDiv = new Div();
////			permissionsAndActionsDiv.addClassName(ROW + "__pa");
////
////			permissionOwnComboBox = new ComboBox<>();
////			permissionOwnComboBox.addClassName(ProjectConstants.NO_PADDING_TOP);
////			permissionOwnComboBox.setLabel("Own");
////			permissionOwnComboBox.setItems(EnumSet.allOf(OperationPermission.class));
////			permissionOwnComboBox.setItemLabelGenerator(OperationPermission::getName);
////			permissionOwnComboBox.setErrorMessage("Value Required");
////			permissionOwnComboBox.setRequired(true);
////			permissionOwnComboBox.setValue(OperationPermission.NO);
////			permissionOwnComboBox.addValueChangeListener(e -> {
////				permissionOwnComboBox.setInvalid(false);
////			});
////
////			permissionCompanyComboBox = new ComboBox<>();
////			permissionCompanyComboBox.addClassName(ProjectConstants.NO_PADDING_TOP);
////			permissionCompanyComboBox.setLabel("Company");
////			permissionCompanyComboBox.setItems(EnumSet.allOf(OperationPermission.class));
////			permissionCompanyComboBox.setItemLabelGenerator(OperationPermission::getName);
////			permissionCompanyComboBox.setErrorMessage("Value Required");
////			permissionCompanyComboBox.setRequired(true);
////			permissionCompanyComboBox.setValue(OperationPermission.NO);
////			permissionCompanyComboBox.addValueChangeListener(e -> {
////				permissionCompanyComboBox.setInvalid(false);
////			});
////
////			permissionsAndActionsDiv.add(permissionOwnComboBox, permissionCompanyComboBox);
////
////
////			toggleVisibleButton = UIUtils.createIconButton(VaadinIcon.EYE_SLASH, ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_CONTRAST);
////			toggleVisibleButton.addClassName(ROW + "__tvb");
////			toggleVisibleButton.addClickListener(e -> {
////				toggleVisibility();
////			});
////
////			deleteButton = UIUtils.createIconButton(VaadinIcon.TRASH, ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);
////
////			contentRow.add(operationAndTargetDiv);
////			contentRow.add(permissionsAndActionsDiv);
////
////			return contentRow;
////		}
////
////
////		private void addVisibleButton() {
////			permissionsAndActionsDiv.add(toggleVisibleButton);
////		}
////
////		private void addDeleteButton() {
////			permissionsAndActionsDiv.add(deleteButton);
////		}
////
////
////		private ComboBox<Operation> getOperationComboBox() {
////			return operationComboBox;
////		}
////
////		private ComboBox<OperationTarget> getTargetComboBox() {
////			return targetComboBox;
////		}
////
////		private ComboBox<OperationPermission> getPermissionOwnComboBox() {
////			return permissionOwnComboBox;
////		}
////
////		private ComboBox<OperationPermission> getPermissionCompanyComboBox() {
////			return permissionCompanyComboBox;
////		}
////
////		private Button getToggleVisibleButton() {
////			return toggleVisibleButton;
////		}
////
////		private Button getDeleteButton() {
////			return deleteButton;
////		}
////
////
////		private void toggleVisibility() {
////			permissionVisible = !permissionVisible;
////
////			configureVisibilityButton();
////		}
////
////		private boolean isPermissionVisible() {
////			return permissionVisible;
////		}
////
////		private void setPermissionVisible(boolean visible) {
////			permissionVisible = visible;
////
////			configureVisibilityButton();
////		}
////
////		private void configureVisibilityButton() {
////			if (permissionVisible) {
////				toggleVisibleButton.setIcon(VaadinIcon.EYE.create());
////				toggleVisibleButton.removeThemeVariants(ButtonVariant.LUMO_CONTRAST);
////			} else {
////				toggleVisibleButton.setIcon(VaadinIcon.EYE_SLASH.create());
////				toggleVisibleButton.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
////			}
////		}
////
////		public int getCounter() {
////			return counter;
////		}
////	}
//
//	private static class PermissionPair {
//
//		private Permission p1;
//		private Permission p2;
//
//		PermissionPair(Permission p1, Permission p2) {
//			this.p1 = p1;
//			this.p2 = p2;
//		}
//
//		private Permission getP1() {
//			return p1;
//		}
//		private void setP1(Permission p1) {
//			this.p1 = p1;
//		}
//
//		private Permission getP2() {
//			return p2;
//		}
//		private void setP2(Permission p2) {
//			this.p2 = p2;
//		}
//
//		private boolean isNull() {
//			return this.p1 == null && this.p2 == null;
//		}
//	}
//}
