package com.gmail.grigorij.backend.database.entities;

import com.gmail.grigorij.backend.database.entities.embeddable.Permission;
import com.gmail.grigorij.backend.database.enums.permissions.PermissionLevel;
import com.gmail.grigorij.backend.database.facades.PermissionFacade;
import com.gmail.grigorij.utils.ProjectConstants;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "permission_holders")
@NamedQueries({
		@NamedQuery(name= PermissionHolder.QUERY_ALL,
				query="SELECT permissionHolder FROM PermissionHolder permissionHolder"),

		@NamedQuery(name= PermissionHolder.QUERY_BY_USER_ID,
				query="SELECT permissionHolder FROM PermissionHolder permissionHolder WHERE permissionHolder.user.id = :" + ProjectConstants.ID_VAR)
})
public class PermissionHolder extends EntityPojo {

	public static final String QUERY_ALL = "get_all_permissionHolders";
	public static final String QUERY_BY_USER_ID = "get_permissionHolder_by_id";


	@OneToOne(mappedBy = "permissionHolder")
	private User user;

	@Embedded
	@ElementCollection
	private List<Permission> permissions;


	public PermissionHolder() {
		permissions = new ArrayList<>();
		permissions.addAll(PermissionFacade.getInstance().getDefaultUserPermissions());
	}

	public PermissionHolder(PermissionHolder other) {
		this.user = other.user;
		this.permissions = other.permissions;
	}


	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}

	public List<Permission> getPermissions() {
		return permissions;
	}
	public void setPermissions(List<Permission> permissions) {
		this.permissions = permissions;
	}
}
