package com.gmail.grigorij.backend.entities.user;

import com.gmail.grigorij.backend.enums.operations.Operation;
import com.gmail.grigorij.backend.enums.operations.OperationPermission;
import com.gmail.grigorij.backend.enums.operations.OperationTarget;

import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Embeddable
public class PermissionTest {

	@Enumerated( EnumType.STRING )
	private Operation operation;

	@Enumerated( EnumType.STRING )
	private OperationTarget operationTarget;

	@Enumerated( EnumType.STRING )
	private OperationPermission permissionOwn;

	@Enumerated( EnumType.STRING )
	private OperationPermission permissionCompany;

	@Enumerated( EnumType.STRING )
	private OperationPermission permissionSystem;

	private boolean visible = false;


	public PermissionTest() {}

	public PermissionTest(PermissionTest other) {
		this.operation = other.operation;
		this.operationTarget = other.operationTarget;
		this.permissionOwn = other.permissionOwn;
		this.permissionCompany = other.permissionCompany;
		this.permissionSystem = other.permissionSystem;
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

	public OperationPermission getPermissionSystem() {
		return permissionSystem;
	}
	public void setPermissionSystem(OperationPermission permissionSystem) {
		this.permissionSystem = permissionSystem;
	}

	public boolean isVisible() {
		return visible;
	}
	public void setVisible(boolean visible) {
		this.visible = visible;
	}
}
