package com.gmail.grigorij.backend.enums.permissions;

import com.gmail.grigorij.backend.enums.transactions.TransactionTarget;

public enum PermissionOperation {

	ADD_USER(   "Add User",    TransactionTarget.USER),
	DELETE_USER("Delete User", TransactionTarget.USER),

	VIEW_USER_DETAILS(       "View User Details", TransactionTarget.USER),
	EDIT_USER_DETAILS(       "Edit User Details", TransactionTarget.USER),

	VIEW_USER_ACCESS_RIGHTS( "View Access Rights", TransactionTarget.USER),
	EDIT_USER_ACCESS_RIGHTS( "Edit Access Rights", TransactionTarget.USER),

	VIEW_USER_ACCESS_GROUP(  "View Access Group", TransactionTarget.USER),
	EDIT_USER_ACCESS_GROUP(  "Edit Access Group", TransactionTarget.USER),



	ADD_COMPANY(    "Add Company",    TransactionTarget.COMPANY),
	DELETE_COMPANY( "Delete Company", TransactionTarget.COMPANY),

	VIEW_COMPANY_DETAILS("View Company Details", TransactionTarget.COMPANY),
	EDIT_COMPANY_DETAILS("Edit Company Details", TransactionTarget.COMPANY),



	ADD_CATEGORY(           "Add Category",          TransactionTarget.CATEGORY),
	DELETE_CATEGORY(        "Delete Category",       TransactionTarget.CATEGORY),
	EDIT_CATEGORY_DETAILS(  "Edit Category Details", TransactionTarget.CATEGORY),



	ADD_TOOL(           "Add Tool",          TransactionTarget.TOOL),
	DELETE_TOOL(        "Delete Tool",       TransactionTarget.TOOL),
	EDIT_TOOL_DETAILS(  "Edit Tool Details", TransactionTarget.TOOL);

	private final String name;
	private final TransactionTarget target;

	PermissionOperation(String name, TransactionTarget target) {
		this.name = name;
		this.target = target;
	}

	public String getStringValue() {
		return this.name;
	}

	public TransactionTarget getTarget() {
		return target;
	}
}
