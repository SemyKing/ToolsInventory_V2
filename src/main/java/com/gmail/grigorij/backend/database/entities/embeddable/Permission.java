package com.gmail.grigorij.backend.database.entities.embeddable;

import com.gmail.grigorij.backend.database.enums.operations.Operation;
import com.gmail.grigorij.backend.database.enums.operations.OperationPermission;
import com.gmail.grigorij.backend.database.enums.operations.OperationTarget;

import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Transient;


@Embeddable
public class Permission {

	// Helper for tracking changes
	@Transient
	private int counter;

	@Enumerated( EnumType.STRING )
	private Operation operation;

	@Enumerated( EnumType.STRING )
	private OperationTarget operationTarget;

	@Enumerated( EnumType.STRING )
	private OperationPermission permissionOwn;

	@Enumerated( EnumType.STRING )
	private OperationPermission permissionCompany;

	private boolean visible = false;


	public Permission() {}

	public Permission(Permission other) {
		this.counter = other.counter;
		this.operation = other.operation;
		this.operationTarget = other.operationTarget;
		this.permissionOwn = other.permissionOwn;
		this.permissionCompany = other.permissionCompany;
		this.visible = other.visible;
	}


	public Operation getOperation() {
		return operation;
	}
	public void setOperation(Operation operation) {
		this.operation = operation;
	}

	public OperationTarget getOperationTarget() {
		return operationTarget;
	}
	public void setOperationTarget(OperationTarget operationTarget) {
		this.operationTarget = operationTarget;
	}

	public OperationPermission getPermissionOwn() {
		return permissionOwn;
	}
	public void setPermissionOwn(OperationPermission permissionOwn) {
		this.permissionOwn = permissionOwn;
	}

	public OperationPermission getPermissionCompany() {
		return permissionCompany;
	}
	public void setPermissionCompany(OperationPermission permissionCompany) {
		this.permissionCompany = permissionCompany;
	}

	public boolean isVisible() {
		return visible;
	}
	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public int getCounter() {
		return counter;
	}
	public void setCounter(int counter) {
		this.counter = counter;
	}



	public String getOperationString() {
		String s = "";

		if (operation != null) {
			s = operation.getName();
		}

		return s;
	}

	public String getTargetString() {
		String s = "";

		if (operationTarget != null) {
			s = operationTarget.getName();
		}

		return s;
	}

	public String getPermissionOwnString() {
		String s = "";

		if (permissionOwn != null) {
			s = permissionOwn.getName();
		}

		return s;
	}

	public String getPermissionCompanyString() {
		String s = "";

		if (permissionCompany != null) {
			s = permissionCompany.getName();
		}

		return s;
	}
}
