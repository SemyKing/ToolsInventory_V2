package com.gmail.grigorij.backend.embeddable;

import com.gmail.grigorij.backend.enums.permissions.PermissionOperation;
import com.gmail.grigorij.backend.enums.permissions.Permission;

import javax.persistence.*;

@Embeddable
public class AccessRight {

	private Long userId;

	@Enumerated( EnumType.STRING )
	private PermissionOperation permissionOperation;

	@Enumerated( EnumType.STRING )
	private Permission permissionOwn;

	@Enumerated( EnumType.STRING )
	private Permission permissionCompany;

	@Enumerated( EnumType.STRING )
	private Permission permissionSystem;

	@Enumerated( EnumType.STRING )
	private Permission visibleToUser;


	public AccessRight() {}

	public AccessRight(PermissionOperation permissionOperation,Permission permissionOwn, Permission permissionCompany, Permission permissionSystem, Permission visibleToUser) {
		this.permissionOperation = permissionOperation;
		this.permissionOwn = permissionOwn;
		this.permissionCompany = permissionCompany;
		this.permissionSystem = permissionSystem;
		this.visibleToUser = visibleToUser;
	}

	public AccessRight(AccessRight accessRight) {
		this.permissionOperation = accessRight.getPermissionOperation();
		this.permissionOwn = accessRight.getPermissionOwn();
		this.permissionCompany = accessRight.getPermissionCompany();
		this.permissionSystem = accessRight.getPermissionSystem();
		this.visibleToUser = accessRight.getVisibleToUser();
	}

	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public PermissionOperation getPermissionOperation() {
		return permissionOperation;
	}
	public void setPermissionOperation(PermissionOperation permissionOperation) {
		this.permissionOperation = permissionOperation;
	}

	public Permission getPermissionOwn() {
		return permissionOwn;
	}
	public void setPermissionOwn(Permission permissionOwn) {
		this.permissionOwn = permissionOwn;
	}

	public Permission getPermissionCompany() {
		return permissionCompany;
	}
	public void setPermissionCompany(Permission permissionCompany) {
		this.permissionCompany = permissionCompany;
	}

	public Permission getPermissionSystem() {
		return permissionSystem;
	}
	public void setPermissionSystem(Permission permissionSystem) {
		this.permissionSystem = permissionSystem;
	}

	public Permission getVisibleToUser() {
		return visibleToUser;
	}
	public void setVisibleToUser(Permission visibleToUser) {
		this.visibleToUser = visibleToUser;
	}
}
