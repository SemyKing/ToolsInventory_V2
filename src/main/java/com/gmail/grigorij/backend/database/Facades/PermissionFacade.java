package com.gmail.grigorij.backend.database.facades;

import com.gmail.grigorij.backend.database.DatabaseManager;
import com.gmail.grigorij.backend.database.entities.PermissionHolder;
import com.gmail.grigorij.backend.database.entities.embeddable.Permission;
import com.gmail.grigorij.backend.database.enums.operations.Operation;
import com.gmail.grigorij.backend.database.enums.operations.OperationPermission;
import com.gmail.grigorij.backend.database.enums.operations.OperationTarget;
import com.gmail.grigorij.backend.database.enums.permissions.PermissionLevel;
import com.gmail.grigorij.backend.database.enums.permissions.PermissionRange;
import com.gmail.grigorij.utils.authentication.AuthenticationService;

import java.util.ArrayList;
import java.util.List;

public class PermissionFacade {

	private static PermissionFacade mInstance;
	private PermissionFacade() {}
	public static PermissionFacade getInstance() {
		if (mInstance == null) {
			mInstance = new PermissionFacade();
		}
		return mInstance;
	}

	public List<Permission> getDefaultViewerPermissions() {
		List<Permission> userPermissions = new ArrayList<>();

		userPermissions.add(constructPermission(Operation.VIEW, OperationTarget.USER,
				OperationPermission.YES,    // OWN
				OperationPermission.NO,     // COMPANY
				false));

		userPermissions.add(constructPermission(Operation.EDIT, OperationTarget.USER,
				OperationPermission.YES,    // OWN
				OperationPermission.NO,     // COMPANY
				false));

		return userPermissions;
	}

	public List<Permission> getDefaultUserPermissions() {
		List<Permission> userPermissions = new ArrayList<>();

		userPermissions.add(constructPermission(Operation.VIEW, OperationTarget.INVENTORY_TAB,
				OperationPermission.YES,    // OWN
				OperationPermission.YES,     // COMPANY
				false));
		userPermissions.add(constructPermission(Operation.VIEW, OperationTarget.MESSAGES_TAB,
				OperationPermission.YES,    // OWN
				OperationPermission.YES,     // COMPANY
				false));

		userPermissions.add(constructPermission(Operation.VIEW, OperationTarget.USER,
				OperationPermission.YES,    // OWN
				OperationPermission.NO,     // COMPANY
				false));
		userPermissions.add(constructPermission(Operation.EDIT, OperationTarget.USER,
				OperationPermission.YES,    // OWN
				OperationPermission.NO,     // COMPANY
				false));

		userPermissions.add(constructPermission(Operation.SEND, OperationTarget.MESSAGES,
				OperationPermission.YES,    // OWN
				OperationPermission.YES,    // COMPANY
				false));

		userPermissions.add(constructPermission(Operation.REPORT, OperationTarget.INVENTORY_TOOL,
				OperationPermission.YES,    // OWN
				OperationPermission.YES,     // COMPANY
				false));
		userPermissions.add(constructPermission(Operation.RESERVE, OperationTarget.INVENTORY_TOOL,
				OperationPermission.YES,    // OWN
				OperationPermission.YES,    // COMPANY
				false));
		userPermissions.add(constructPermission(Operation.TAKE, OperationTarget.INVENTORY_TOOL,
				OperationPermission.YES,    // OWN
				OperationPermission.YES,    // COMPANY
				false));

		return userPermissions;
	}

	public List<Permission> getDefaultForemanPermissions() {
		List<Permission> userPermissions = new ArrayList<>();

		userPermissions.add(constructPermission(Operation.VIEW, OperationTarget.INVENTORY_TAB,
				OperationPermission.YES,    // OWN
				OperationPermission.NO,     // COMPANY
				false));

		userPermissions.add(constructPermission(Operation.VIEW, OperationTarget.MESSAGES_TAB,
				OperationPermission.YES,    // OWN
				OperationPermission.NO,     // COMPANY
				false));

		userPermissions.add(constructPermission(Operation.VIEW, OperationTarget.TRANSACTIONS_TAB,
				OperationPermission.YES,    // OWN
				OperationPermission.NO,     // COMPANY
				false));

		userPermissions.add(constructPermission(Operation.VIEW, OperationTarget.REPORTING_TAB,
				OperationPermission.YES,    // OWN
				OperationPermission.NO,     // COMPANY
				false));

		userPermissions.add(constructPermission(Operation.VIEW, OperationTarget.USER,
				OperationPermission.YES,    // OWN
				OperationPermission.NO,     // COMPANY
				false));

		userPermissions.add(constructPermission(Operation.EDIT, OperationTarget.USER,
				OperationPermission.YES,    // OWN
				OperationPermission.NO,     // COMPANY
				false));

		userPermissions.add(constructPermission(Operation.SEND, OperationTarget.MESSAGES,
				OperationPermission.YES,    // OWN
				OperationPermission.YES,    // COMPANY
				false));

		userPermissions.add(constructPermission(Operation.REPORT, OperationTarget.INVENTORY_TOOL,
				OperationPermission.YES,    // OWN
				OperationPermission.YES,     // COMPANY
				false));

		userPermissions.add(constructPermission(Operation.RESERVE, OperationTarget.INVENTORY_TOOL,
				OperationPermission.YES,    // OWN
				OperationPermission.YES,    // COMPANY
				false));

		userPermissions.add(constructPermission(Operation.TAKE, OperationTarget.INVENTORY_TOOL,
				OperationPermission.YES,    // OWN
				OperationPermission.YES,    // COMPANY
				false));

		return userPermissions;
	}

	public List<Permission> getDefaultCompanyAdminPermissions() {
		List<Permission> userPermissions = new ArrayList<>();

		userPermissions.add(constructPermission(Operation.VIEW, OperationTarget.INVENTORY_TAB,
				OperationPermission.YES,    // OWN
				OperationPermission.NO,     // COMPANY
				false));
		userPermissions.add(constructPermission(Operation.VIEW, OperationTarget.MESSAGES_TAB,
				OperationPermission.YES,    // OWN
				OperationPermission.NO,     // COMPANY
				false));
		userPermissions.add(constructPermission(Operation.VIEW, OperationTarget.TRANSACTIONS_TAB,
				OperationPermission.YES,    // OWN
				OperationPermission.NO,     // COMPANY
				false));
		userPermissions.add(constructPermission(Operation.VIEW, OperationTarget.REPORTING_TAB,
				OperationPermission.YES,    // OWN
				OperationPermission.NO,     // COMPANY
				false));


		userPermissions.add(constructPermission(Operation.VIEW, OperationTarget.USER,
				OperationPermission.YES,    // OWN
				OperationPermission.YES,    // COMPANY
				true));
		userPermissions.add(constructPermission(Operation.EDIT, OperationTarget.USER,
				OperationPermission.YES,    // OWN
				OperationPermission.YES,    // COMPANY
				true));
		userPermissions.add(constructPermission(Operation.ADD, OperationTarget.USER,
				OperationPermission.YES,    // OWN
				OperationPermission.YES,    // COMPANY
				true));


		userPermissions.add(constructPermission(Operation.CHANGE, OperationTarget.PASSWORD,
				OperationPermission.YES,    // OWN
				OperationPermission.YES,    // COMPANY
				true));


		userPermissions.add(constructPermission(Operation.VIEW, OperationTarget.COMPANY,
				OperationPermission.YES,    // OWN
				OperationPermission.NO,     // COMPANY
				false));
		userPermissions.add(constructPermission(Operation.EDIT, OperationTarget.COMPANY,
				OperationPermission.YES,    // OWN
				OperationPermission.NO,     // COMPANY
				false));


		userPermissions.add(constructPermission(Operation.VIEW, OperationTarget.LOCATIONS,
				OperationPermission.YES,    // OWN
				OperationPermission.NO,     // COMPANY
				false));
		userPermissions.add(constructPermission(Operation.EDIT, OperationTarget.LOCATIONS,
				OperationPermission.YES,    // OWN
				OperationPermission.NO,     // COMPANY
				false));
		userPermissions.add(constructPermission(Operation.ADD, OperationTarget.LOCATIONS,
				OperationPermission.YES,    // OWN
				OperationPermission.NO,     // COMPANY
				false));


		userPermissions.add(constructPermission(Operation.VIEW, OperationTarget.PERMISSIONS,
				OperationPermission.YES,    // OWN
				OperationPermission.YES,    // COMPANY
				false));
		userPermissions.add(constructPermission(Operation.EDIT, OperationTarget.PERMISSIONS,
				OperationPermission.NO,     // OWN
				OperationPermission.YES,    // COMPANY
				true));


		userPermissions.add(constructPermission(Operation.EDIT, OperationTarget.PERMISSION_LEVEL,
				OperationPermission.NO,     // OWN
				OperationPermission.YES,    // COMPANY
				true));


		userPermissions.add(constructPermission(Operation.SEND, OperationTarget.MESSAGES,
				OperationPermission.YES,    // OWN
				OperationPermission.YES,    // COMPANY
				true));


		userPermissions.add(constructPermission(Operation.VIEW, OperationTarget.INVENTORY_CATEGORY,
				OperationPermission.YES,    // OWN
				OperationPermission.YES,    // COMPANY
				true));
		userPermissions.add(constructPermission(Operation.ADD, OperationTarget.INVENTORY_CATEGORY,
				OperationPermission.YES,    // OWN
				OperationPermission.YES,    // COMPANY
				true));
		userPermissions.add(constructPermission(Operation.EDIT, OperationTarget.INVENTORY_CATEGORY,
				OperationPermission.YES,    // OWN
				OperationPermission.YES,    // COMPANY
				true));


		userPermissions.add(constructPermission(Operation.VIEW, OperationTarget.INVENTORY_TOOL,
				OperationPermission.YES,    // OWN
				OperationPermission.YES,    // COMPANY
				true));
		userPermissions.add(constructPermission(Operation.ADD, OperationTarget.INVENTORY_TOOL,
				OperationPermission.YES,    // OWN
				OperationPermission.YES,    // COMPANY
				true));
		userPermissions.add(constructPermission(Operation.EDIT, OperationTarget.INVENTORY_TOOL,
				OperationPermission.YES,    // OWN
				OperationPermission.YES,    // COMPANY
				true));
		userPermissions.add(constructPermission(Operation.DELETE, OperationTarget.INVENTORY_TOOL,
				OperationPermission.YES,    // OWN
				OperationPermission.YES,    // COMPANY
				true));
		userPermissions.add(constructPermission(Operation.REPORT, OperationTarget.INVENTORY_TOOL,
				OperationPermission.YES,    // OWN
				OperationPermission.YES,    // COMPANY
				true));
		userPermissions.add(constructPermission(Operation.RESERVE, OperationTarget.INVENTORY_TOOL,
				OperationPermission.YES,    // OWN
				OperationPermission.YES,    // COMPANY
				true));
		userPermissions.add(constructPermission(Operation.TAKE, OperationTarget.INVENTORY_TOOL,
				OperationPermission.YES,    // OWN
				OperationPermission.YES,    // COMPANY
				true));

		return userPermissions;
	}


	private Permission constructPermission(Operation action, OperationTarget target,
	                                       OperationPermission own, OperationPermission company, boolean visible) {
		Permission permissionTest = new Permission();
		permissionTest.setOperation(action);
		permissionTest.setOperationTarget(target);
		permissionTest.setPermissionOwn(own);
		permissionTest.setPermissionCompany(company);
		permissionTest.setVisible(visible);
		return permissionTest;
	}

	public boolean isSystemAdminOrAllowedTo(Operation action, OperationTarget target, PermissionRange range) {
		if (AuthenticationService.getCurrentSessionUser().getPermissionLevel().equalsTo(PermissionLevel.SYSTEM_ADMIN)) {
			return true;
		} else {
			return isUserAllowedTo(action, target, range);
		}
	}

	public boolean isUserAllowedTo(Operation action, OperationTarget target, PermissionRange range) {

		if (action == null || target == null || AuthenticationService.getCurrentSessionUser().getPermissionHolder() == null) {
			return false;
		}

		for (Permission permission : AuthenticationService.getCurrentSessionUser().getPermissionHolder().getPermissions()) {
			if (action.equals(permission.getOperation())) {
				if (target.equals(permission.getOperationTarget())) {

					if (range == null) {
						return permission.getPermissionOwn().isAllowed() || permission.getPermissionCompany().isAllowed();
					} else {
						if (range.equals(PermissionRange.OWN)) {
							return permission.getPermissionOwn().isAllowed();
						} else if (range.equals(PermissionRange.COMPANY)) {
							return permission.getPermissionCompany().isAllowed();
						}
					}
				}
			}
		}

		return false;
	}






	public boolean insert(PermissionHolder permissionHolder) {
		if (permissionHolder == null) {
			System.err.println(this.getClass().getSimpleName() + " -> INSERT NULL PERMISSION HOLDER");
			return false;
		}

		try {
			DatabaseManager.getInstance().insert(permissionHolder);
		} catch (Exception e) {
			System.err.println(this.getClass().getSimpleName() + " -> PERMISSION HOLDER INSERT FAIL");
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public boolean update(PermissionHolder permissionHolder) {
		if (permissionHolder == null) {
			System.err.println(this.getClass().getSimpleName() + " -> UPDATE NULL PERMISSION HOLDER");
			return false;
		}

		PermissionHolder permissionHolderInDatabase = null;

		if (permissionHolder.getId() != null) {
			permissionHolderInDatabase = DatabaseManager.getInstance().find(PermissionHolder.class, permissionHolder.getId());
		}
		try {
			if (permissionHolderInDatabase == null) {
				return insert(permissionHolder);
			} else {
				DatabaseManager.getInstance().update(permissionHolder);
			}
		} catch (Exception e) {
			System.err.println(this.getClass().getSimpleName() + " -> PERMISSION HOLDER UPDATE FAIL");
			e.printStackTrace();
			return false;
		}
		return true;
	}
}
