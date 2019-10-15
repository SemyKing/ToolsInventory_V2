package com.gmail.grigorij.backend.database.facades;

import com.gmail.grigorij.backend.entities.user.PermissionTest;
import com.gmail.grigorij.backend.enums.operations.Operation;
import com.gmail.grigorij.backend.enums.operations.OperationPermission;
import com.gmail.grigorij.backend.enums.operations.OperationTarget;
import com.gmail.grigorij.backend.enums.permissions.PermissionRange;
import com.gmail.grigorij.utils.AuthenticationService;

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

	public List<PermissionTest> getDefaultViewerPermissions() {
		List<PermissionTest> userPermissions = new ArrayList<>();

		userPermissions.add(constructPermission(Operation.VIEW, OperationTarget.USER,
				OperationPermission.YES,    // OWN
				OperationPermission.NO,     // COMPANY
				OperationPermission.NO,     // SYSTEM
				false));

		userPermissions.add(constructPermission(Operation.EDIT, OperationTarget.USER,
				OperationPermission.YES,    // OWN
				OperationPermission.NO,     // COMPANY
				OperationPermission.NO,     // SYSTEM
				false));

		userPermissions.add(constructPermission(Operation.SEND_MESSAGE, OperationTarget.USER,
				OperationPermission.YES,    // OWN
				OperationPermission.YES,    // COMPANY
				OperationPermission.NO,     // SYSTEM
				false));

		userPermissions.add(constructPermission(Operation.REPORT, OperationTarget.INVENTORY_TOOL,
				OperationPermission.YES,    // OWN
				OperationPermission.NO,     // COMPANY
				OperationPermission.NO,     // SYSTEM
				false));

		userPermissions.add(constructPermission(Operation.RESERVE, OperationTarget.INVENTORY_TOOL,
				OperationPermission.YES,    // OWN
				OperationPermission.YES,    // COMPANY
				OperationPermission.NO,     // SYSTEM
				false));

		userPermissions.add(constructPermission(Operation.TAKE, OperationTarget.INVENTORY_TOOL,
				OperationPermission.YES,    // OWN
				OperationPermission.YES,    // COMPANY
				OperationPermission.NO,     // SYSTEM
				false));

		return userPermissions;
	}

	public List<PermissionTest> getDefaultUserPermissions() {
		List<PermissionTest> userPermissions = new ArrayList<>();

		userPermissions.add(constructPermission(Operation.VIEW, OperationTarget.USER,
				OperationPermission.YES,    // OWN
				OperationPermission.NO,     // COMPANY
				OperationPermission.NO,     // SYSTEM
				false));

		userPermissions.add(constructPermission(Operation.EDIT, OperationTarget.USER,
				OperationPermission.YES,    // OWN
				OperationPermission.NO,     // COMPANY
				OperationPermission.NO,     // SYSTEM
				false));

		userPermissions.add(constructPermission(Operation.SEND_MESSAGE, OperationTarget.USER,
				OperationPermission.YES,    // OWN
				OperationPermission.YES,    // COMPANY
				OperationPermission.NO,     // SYSTEM
				false));

		userPermissions.add(constructPermission(Operation.REPORT, OperationTarget.INVENTORY_TOOL,
				OperationPermission.YES,    // OWN
				OperationPermission.NO,     // COMPANY
				OperationPermission.NO,     // SYSTEM
				false));

		userPermissions.add(constructPermission(Operation.RESERVE, OperationTarget.INVENTORY_TOOL,
				OperationPermission.YES,    // OWN
				OperationPermission.YES,    // COMPANY
				OperationPermission.NO,     // SYSTEM
				false));

		userPermissions.add(constructPermission(Operation.TAKE, OperationTarget.INVENTORY_TOOL,
				OperationPermission.YES,    // OWN
				OperationPermission.YES,    // COMPANY
				OperationPermission.NO,     // SYSTEM
				false));

		return userPermissions;
	}

	public List<PermissionTest> getDefaultForemanPermissions() {
		List<PermissionTest> userPermissions = new ArrayList<>();

		userPermissions.add(constructPermission(Operation.VIEW, OperationTarget.USER,
				OperationPermission.YES,    // OWN
				OperationPermission.NO,     // COMPANY
				OperationPermission.NO,     // SYSTEM
				false));

		userPermissions.add(constructPermission(Operation.EDIT, OperationTarget.USER,
				OperationPermission.YES,    // OWN
				OperationPermission.NO,     // COMPANY
				OperationPermission.NO,     // SYSTEM
				false));

		userPermissions.add(constructPermission(Operation.SEND_MESSAGE, OperationTarget.USER,
				OperationPermission.YES,    // OWN
				OperationPermission.YES,    // COMPANY
				OperationPermission.NO,     // SYSTEM
				false));

		userPermissions.add(constructPermission(Operation.REPORT, OperationTarget.INVENTORY_TOOL,
				OperationPermission.YES,    // OWN
				OperationPermission.NO,     // COMPANY
				OperationPermission.NO,     // SYSTEM
				false));

		userPermissions.add(constructPermission(Operation.RESERVE, OperationTarget.INVENTORY_TOOL,
				OperationPermission.YES,    // OWN
				OperationPermission.YES,    // COMPANY
				OperationPermission.NO,     // SYSTEM
				false));

		userPermissions.add(constructPermission(Operation.TAKE, OperationTarget.INVENTORY_TOOL,
				OperationPermission.YES,    // OWN
				OperationPermission.YES,    // COMPANY
				OperationPermission.NO,     // SYSTEM
				false));

		return userPermissions;
	}

	public List<PermissionTest> getDefaultCompanyAdminPermissions() {
		List<PermissionTest> userPermissions = new ArrayList<>();

		userPermissions.add(constructPermission(Operation.VIEW, OperationTarget.USER,
				OperationPermission.YES,    // OWN
				OperationPermission.YES,    // COMPANY
				OperationPermission.NO,     // SYSTEM
				true));

		userPermissions.add(constructPermission(Operation.VIEW, OperationTarget.PERMISSIONS,
				OperationPermission.YES,    // OWN
				OperationPermission.YES,    // COMPANY
				OperationPermission.NO,     // SYSTEM
				false));

		userPermissions.add(constructPermission(Operation.EDIT, OperationTarget.USER,
				OperationPermission.YES,    // OWN
				OperationPermission.YES,    // COMPANY
				OperationPermission.NO,     // SYSTEM
				true));

		userPermissions.add(constructPermission(Operation.SEND_MESSAGE, OperationTarget.USER,
				OperationPermission.YES,    // OWN
				OperationPermission.YES,    // COMPANY
				OperationPermission.NO,     // SYSTEM
				true));

		userPermissions.add(constructPermission(Operation.ADD, OperationTarget.INVENTORY_CATEGORY,
				OperationPermission.YES,    // OWN
				OperationPermission.YES,    // COMPANY
				OperationPermission.NO,     // SYSTEM
				true));

		userPermissions.add(constructPermission(Operation.EDIT, OperationTarget.INVENTORY_CATEGORY,
				OperationPermission.YES,    // OWN
				OperationPermission.YES,    // COMPANY
				OperationPermission.NO,     // SYSTEM
				true));

		userPermissions.add(constructPermission(Operation.ADD, OperationTarget.INVENTORY_TOOL,
				OperationPermission.YES,    // OWN
				OperationPermission.YES,    // COMPANY
				OperationPermission.NO,     // SYSTEM
				true));

		userPermissions.add(constructPermission(Operation.EDIT, OperationTarget.INVENTORY_TOOL,
				OperationPermission.YES,    // OWN
				OperationPermission.YES,    // COMPANY
				OperationPermission.NO,     // SYSTEM
				true));

		userPermissions.add(constructPermission(Operation.REPORT, OperationTarget.INVENTORY_TOOL,
				OperationPermission.YES,    // OWN
				OperationPermission.YES,    // COMPANY
				OperationPermission.NO,     // SYSTEM
				true));

		userPermissions.add(constructPermission(Operation.RESERVE, OperationTarget.INVENTORY_TOOL,
				OperationPermission.YES,    // OWN
				OperationPermission.YES,    // COMPANY
				OperationPermission.NO,     // SYSTEM
				true));

		userPermissions.add(constructPermission(Operation.TAKE, OperationTarget.INVENTORY_TOOL,
				OperationPermission.YES,    // OWN
				OperationPermission.YES,    // COMPANY
				OperationPermission.NO,     // SYSTEM
				true));

		return userPermissions;
	}


	private PermissionTest constructPermission(Operation action, OperationTarget target,
	                                           OperationPermission own, OperationPermission company, OperationPermission system, boolean visible) {
		PermissionTest permissionTest = new PermissionTest();
		permissionTest.setOperation(action);
		permissionTest.setOperationTarget(target);
		permissionTest.setPermissionOwn(own);
		permissionTest.setPermissionCompany(company);
		permissionTest.setPermissionSystem(system);
		permissionTest.setVisible(visible);
		return permissionTest;
	}


	public boolean isUserAllowedTo(Operation action, OperationTarget target, PermissionRange range) {

		if (action == null || target == null || range == null) {
			return false;
		}

		for (PermissionTest permission : AuthenticationService.getCurrentSessionUser().getPermissions()) {
			if (action.equals(permission.getOperation())) {
				if (target.equals(permission.getOperationTarget())) {

					if (range.equals(PermissionRange.OWN)) {
						return permission.getPermissionOwn().isAllowed();
					} else if (range.equals(PermissionRange.COMPANY)) {
						return permission.getPermissionCompany().isAllowed();
					} else if (range.equals(PermissionRange.SYSTEM)) {
						return permission.getPermissionSystem().isAllowed();
					}
				}
			}
		}

		return false;
	}
}
