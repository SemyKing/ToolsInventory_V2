package com.gmail.grigorij.backend.entities.access;

import com.gmail.grigorij.backend.entities.EntityPojo;
import com.gmail.grigorij.backend.entities.user.User;
import com.gmail.grigorij.backend.enums.OperationTarget;
import com.gmail.grigorij.backend.enums.OperationType;
import com.gmail.grigorij.backend.enums.PermissionType;

import javax.persistence.*;

@Entity
@Table(name = "access_rights")
@NamedQueries({
		@NamedQuery(
				name="findUserInDatabase",
				query="SELECT user FROM User user WHERE" +
						" user.username = :username_var AND" +
						" user.password = :password_var")
})
public class AccessRight extends EntityPojo {

	@Enumerated( EnumType.STRING )
	private OperationType operationType;

	@Enumerated( EnumType.STRING )
	private OperationTarget operationTarget;

	@Enumerated( EnumType.STRING )
	private PermissionType permissionType;

	private User user;


	public AccessRight() {}


	public OperationType getOperationType() {
		return operationType;
	}
	public void setOperationType(OperationType operationType) {
		this.operationType = operationType;
	}

	public OperationTarget getOperationTarget() {
		return operationTarget;
	}
	public void setOperationTarget(OperationTarget operationTarget) {
		this.operationTarget = operationTarget;
	}

	public PermissionType getPermissionType() {
		return permissionType;
	}
	public void setPermissionType(PermissionType permissionType) {
		this.permissionType = permissionType;
	}

	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
}
