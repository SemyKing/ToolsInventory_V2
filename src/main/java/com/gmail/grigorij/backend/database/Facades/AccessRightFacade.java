package com.gmail.grigorij.backend.database.facades;

import com.gmail.grigorij.backend.embeddable.AccessRight;
import com.gmail.grigorij.backend.enums.permissions.*;
import com.gmail.grigorij.utils.AuthenticationService;

import java.util.*;

public class AccessRightFacade {

	private static AccessRightFacade mInstance;
	private AccessRightFacade() {}
	public static AccessRightFacade getInstance() {
		if (mInstance == null) {
			mInstance = new AccessRightFacade();
		}
		return mInstance;
	}



	public List<AccessRight> constructAccessRights(Permission permissionOwn, Permission permissionCompany, Permission permissionSystem, Permission visibleToUser) {

		List<AccessRight> accessRights = new ArrayList<>();
		List<PermissionOperation> permissionOperations = new ArrayList<>(EnumSet.allOf(PermissionOperation.class));

		for (PermissionOperation po : permissionOperations) {
			accessRights.add(new AccessRight(po, permissionOwn, permissionCompany, permissionSystem, visibleToUser));
		}

		return accessRights;
	}




	/*
		DEFAULT RIGHTS FOR SYSTEM ADMIN
	 */
//	public List<AccessRight> getDefaultsForSystemAdmin() {
//		List<AccessRight> defaults = new ArrayList<>();
//
//		defaults.add(new AccessRight(PermissionOperation.ADD, PermissionTarget.USER, Permission.PERMISSION_NO));
//		defaults.add(new AccessRight(PermissionOperation.EDIT, PermissionTarget.USER, Permission.PERMISSION_NO));
//		defaults.add(new AccessRight(PermissionOperation.VIEW, PermissionTarget.USER, Permission.PERMISSION_NO));
//		defaults.add(new AccessRight(PermissionOperation.DELETE, PermissionTarget.USER, Permission.PERMISSION_NO));
//
//		defaults.add(new AccessRight(PermissionOperation.EDIT, PermissionTarget.USER_STATUS, Permission.PERMISSION_NO));
//		defaults.add(new AccessRight(PermissionOperation.VIEW, PermissionTarget.USER_STATUS, Permission.PERMISSION_NO));
//
//		defaults.add(new AccessRight(PermissionOperation.EDIT, PermissionTarget.USER_ACCESS_RIGHTS, Permission.PERMISSION_NO));
//		defaults.add(new AccessRight(PermissionOperation.VIEW, PermissionTarget.USER_ACCESS_RIGHTS, Permission.PERMISSION_NO));
//
//		defaults.add(new AccessRight(PermissionOperation.EDIT, PermissionTarget.USER_ACCESS_GROUP, Permission.PERMISSION_NO));
//		defaults.add(new AccessRight(PermissionOperation.VIEW, PermissionTarget.USER_ACCESS_GROUP, Permission.PERMISSION_NO));
//
//		defaults.add(new AccessRight(PermissionOperation.EDIT, PermissionTarget.USER_ACCESS_RIGHTS_RANGE, Permission.PERMISSION_NO));
//		defaults.add(new AccessRight(PermissionOperation.VIEW, PermissionTarget.USER_ACCESS_RIGHTS_RANGE, Permission.PERMISSION_NO));
//
//		defaults.add(new AccessRight(PermissionOperation.ADD, PermissionTarget.COMPANY, Permission.PERMISSION_NO));
//		defaults.add(new AccessRight(PermissionOperation.EDIT, PermissionTarget.COMPANY, Permission.PERMISSION_NO));
//		defaults.add(new AccessRight(PermissionOperation.VIEW, PermissionTarget.COMPANY, Permission.PERMISSION_NO));
//		defaults.add(new AccessRight(PermissionOperation.DELETE, PermissionTarget.COMPANY, Permission.PERMISSION_NO));
//
//		defaults.add(new AccessRight(PermissionOperation.ADD, PermissionTarget.CATEGORY, Permission.PERMISSION_NO));
//		defaults.add(new AccessRight(PermissionOperation.EDIT, PermissionTarget.CATEGORY, Permission.PERMISSION_NO));
//		defaults.add(new AccessRight(PermissionOperation.VIEW, PermissionTarget.CATEGORY, Permission.PERMISSION_NO));
//		defaults.add(new AccessRight(PermissionOperation.DELETE, PermissionTarget.CATEGORY, Permission.PERMISSION_NO));
//
//		defaults.add(new AccessRight(PermissionOperation.ADD, PermissionTarget.TOOL, Permission.PERMISSION_NO));
//		defaults.add(new AccessRight(PermissionOperation.EDIT, PermissionTarget.TOOL, Permission.PERMISSION_NO));
//		defaults.add(new AccessRight(PermissionOperation.VIEW, PermissionTarget.TOOL, Permission.PERMISSION_NO));
//		defaults.add(new AccessRight(PermissionOperation.DELETE, PermissionTarget.TOOL, Permission.PERMISSION_NO));
//
//		defaults.add(new AccessRight(PermissionOperation.EDIT, PermissionTarget.TOOL_STATUS, Permission.PERMISSION_NO));
//
//		return defaults;
//	}

	/*
		DEFAULT RIGHTS FOR COMPANY ADMIN
	 */
//	public List<AccessRight> getDefaultsForCompanyAdmin() {
//		List<AccessRight> defaults = new ArrayList<>();
//
//		defaults.add(new AccessRight(PermissionOperation.VIEW, PermissionTarget.USER,
//				Permission.PERMISSION_NO,
//				Permission.PERMISSION_NO,
//				Permission.PERMISSION_YES));
//		defaults.add(new AccessRight(PermissionOperation.EDIT, PermissionTarget.USER,
//				Permission.PERMISSION_NO,
//				Permission.PERMISSION_NO,
//				Permission.PERMISSION_YES));
//		defaults.add(new AccessRight(PermissionOperation.ADD, PermissionTarget.USER,
//				Permission.PERMISSION_NO,
//				Permission.PERMISSION_NO,
//				Permission.PERMISSION_YES));
//		defaults.add(new AccessRight(PermissionOperation.DELETE, PermissionTarget.USER,
//				Permission.PERMISSION_YES,
//				Permission.PERMISSION_NO,
//				Permission.PERMISSION_YES));
//
//		defaults.add(new AccessRight(PermissionOperation.VIEW, PermissionTarget.USER_STATUS,
//				Permission.PERMISSION_NO,
//				Permission.PERMISSION_NO,
//				Permission.PERMISSION_YES));
//		defaults.add(new AccessRight(PermissionOperation.EDIT, PermissionTarget.USER_STATUS,
//				Permission.PERMISSION_NO,
//				Permission.PERMISSION_NO,
//				Permission.PERMISSION_YES));
//
//		defaults.add(new AccessRight(PermissionOperation.VIEW, PermissionTarget.USER_ACCESS_RIGHTS,
//				Permission.PERMISSION_NO,
//				Permission.PERMISSION_NO,
//				Permission.PERMISSION_YES));
//		defaults.add(new AccessRight(PermissionOperation.EDIT, PermissionTarget.USER_ACCESS_RIGHTS,
//				Permission.PERMISSION_NO,
//				Permission.PERMISSION_NO,
//				Permission.PERMISSION_YES));
//
//		defaults.add(new AccessRight(PermissionOperation.VIEW, PermissionTarget.USER_ACCESS_GROUP,
//				Permission.PERMISSION_NO,
//				Permission.PERMISSION_NO,
//				Permission.PERMISSION_YES));
//		defaults.add(new AccessRight(PermissionOperation.EDIT, PermissionTarget.USER_ACCESS_GROUP,
//				Permission.PERMISSION_NO,
//				Permission.PERMISSION_NO,
//				Permission.PERMISSION_YES));
//
//		defaults.add(new AccessRight(PermissionOperation.VIEW, PermissionTarget.USER_ACCESS_RIGHTS_RANGE,
//				Permission.PERMISSION_NO,
//				Permission.PERMISSION_NO,
//				Permission.PERMISSION_YES));
//		defaults.add(new AccessRight(PermissionOperation.EDIT, PermissionTarget.USER_ACCESS_RIGHTS_RANGE,
//				Permission.PERMISSION_NO,
//				Permission.PERMISSION_NO,
//				Permission.PERMISSION_YES));
//
//		defaults.add(new AccessRight(PermissionOperation.EDIT, PermissionTarget.CATEGORY,
//				Permission.PERMISSION_NO,
//				Permission.PERMISSION_NO,
//				Permission.PERMISSION_YES));
//		defaults.add(new AccessRight(PermissionOperation.ADD, PermissionTarget.CATEGORY,
//				Permission.PERMISSION_NO,
//				Permission.PERMISSION_NO,
//				Permission.PERMISSION_YES));
//		defaults.add(new AccessRight(PermissionOperation.DELETE, PermissionTarget.CATEGORY,
//				Permission.PERMISSION_NO,
//				Permission.PERMISSION_NO,
//				Permission.PERMISSION_YES));
//
//		defaults.add(new AccessRight(PermissionOperation.VIEW, PermissionTarget.TOOL,
//				Permission.PERMISSION_NO,
//				Permission.PERMISSION_NO,
//				Permission.PERMISSION_YES));
//		defaults.add(new AccessRight(PermissionOperation.EDIT, PermissionTarget.TOOL,
//				Permission.PERMISSION_NO,
//				Permission.PERMISSION_NO,
//				Permission.PERMISSION_YES));
//		defaults.add(new AccessRight(PermissionOperation.ADD, PermissionTarget.TOOL,
//				Permission.PERMISSION_NO,
//				Permission.PERMISSION_NO,
//				Permission.PERMISSION_YES));
//		defaults.add(new AccessRight(PermissionOperation.DELETE, PermissionTarget.TOOL,
//				Permission.PERMISSION_NO,
//				Permission.PERMISSION_NO,
//				Permission.PERMISSION_YES));
//
//		defaults.add(new AccessRight(PermissionOperation.EDIT, PermissionTarget.TOOL_STATUS,
//				Permission.PERMISSION_NO,
//				Permission.PERMISSION_NO,
//				Permission.PERMISSION_YES));
//
//		return defaults;
//	}

	/*
		DEFAULT RIGHTS FOR USER
	 */
//	public List<AccessRight> getDefaultsForUser() {
//		List<AccessRight> defaults = new ArrayList<>();
//
//		defaults.add(new AccessRight(PermissionOperation.VIEW, PermissionTarget.USER,
//				Permission.PERMISSION_NO,
//				Permission.PERMISSION_YES,
//				Permission.PERMISSION_YES));
//		defaults.add(new AccessRight(PermissionOperation.EDIT, PermissionTarget.USER,
//				Permission.PERMISSION_NO,
//				Permission.PERMISSION_YES,
//				Permission.PERMISSION_YES));
//
//		defaults.add(new AccessRight(PermissionOperation.VIEW, PermissionTarget.USER_STATUS,
//				Permission.PERMISSION_NO,
//				Permission.PERMISSION_YES,
//				Permission.PERMISSION_YES));
//
//		defaults.add(new AccessRight(PermissionOperation.VIEW, PermissionTarget.USER_ACCESS_RIGHTS,
//				Permission.PERMISSION_NO,
//				Permission.PERMISSION_YES,
//				Permission.PERMISSION_YES));
//
//		defaults.add(new AccessRight(PermissionOperation.VIEW, PermissionTarget.USER_ACCESS_GROUP,
//				Permission.PERMISSION_NO,
//				Permission.PERMISSION_YES,
//				Permission.PERMISSION_YES));
//
//		defaults.add(new AccessRight(PermissionOperation.VIEW, PermissionTarget.TOOL,
//				Permission.PERMISSION_NO,
//				Permission.PERMISSION_NO,
//				Permission.PERMISSION_YES));
//		defaults.add(new AccessRight(PermissionOperation.EDIT, PermissionTarget.TOOL,
//				Permission.PERMISSION_NO,
//				Permission.PERMISSION_YES,
//				Permission.PERMISSION_YES));
//
//		defaults.add(new AccessRight(PermissionOperation.EDIT, PermissionTarget.TOOL_STATUS,
//				Permission.PERMISSION_NO,
//				Permission.PERMISSION_NO,
//				Permission.PERMISSION_YES));
//
//		return defaults;
//	}

	//CARTESIAN PRODUCT (ALL POSSIBLE VARIANTS)
//	private List<AccessRight> populateAccessRights(List<PermissionOperation> opList, List<PermissionT> ptList) {
//		List<AccessRight> defaults = new ArrayList<>();
//
//		List<List<Enum<? extends Enum<?>>>> allCombinations = Lists.cartesianProduct(opList, ptList);
//
//		for (List<Enum<? extends Enum<?>>> combo : allCombinations) {
//			AccessRight accessRight = new AccessRight();
//
//			accessRight.setPermissionOperation((PermissionOperation) combo.get(0));
//			accessRight.setPermissionTarget((PermissionT) combo.get(1));
//
//			defaults.add(accessRight);
//		}
//
//		return defaults;
//	}


	public boolean isUserAllowedTo(PermissionOperation po, PermissionLevel... pLevels) {
		if (po == null && pLevels == null) {
			return false;
		}

		for (AccessRight ar : AuthenticationService.getCurrentSessionUser().getAccessRights()) {
			if (ar.getPermissionOperation().equals(po)) {

				if (pLevels.length == 0) {
					return false;
				}

				if (pLevels.length == 1) {
					if (pLevels[0].equalsTo(PermissionLevel.OWN)) {
						return (ar.getPermissionOwn().equals(Permission.YES));
					}

					if (pLevels[0].equalsTo(PermissionLevel.COMPANY)) {
						return (ar.getPermissionCompany().equals(Permission.YES));
					}

					if (pLevels[0].equalsTo(PermissionLevel.SYSTEM)) {
						return (ar.getPermissionSystem().equals(Permission.YES));
					}
				}

				boolean res = false;

				//CHECK IF AT LEAST ONE OF LEVELS IS SET TO 'YES'
				if (pLevels.length > 1) {
					for (PermissionLevel pl : pLevels) {

						if (pl.equals(PermissionLevel.OWN)) {
							if (ar.getPermissionOwn().equals(Permission.YES)) {
								res = true;
							}
						}
						if (pl.equals(PermissionLevel.COMPANY)) {
							if (ar.getPermissionCompany().equals(Permission.YES)) {
								res = true;
							}
						}
						if (pl.equals(PermissionLevel.SYSTEM)) {
							if (ar.getPermissionSystem().equals(Permission.YES)) {
								res = true;
							}
						}

						if (res) {
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	public boolean isUserAllowedToSeeAccessRight(AccessRight accessRight) {
		for (AccessRight usersAccessRight : AuthenticationService.getCurrentSessionUser().getAccessRights()) {
			if (usersAccessRight.getPermissionOperation().equals(accessRight.getPermissionOperation())) {
				return usersAccessRight.getVisibleToUser().equals(Permission.YES);
			}
		}
		return false;
	}

}
