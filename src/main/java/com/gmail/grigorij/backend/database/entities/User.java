package com.gmail.grigorij.backend.database.entities;


import com.gmail.grigorij.backend.database.entities.embeddable.Location;
import com.gmail.grigorij.backend.database.entities.embeddable.Permission;
import com.gmail.grigorij.backend.database.entities.embeddable.Person;
import com.gmail.grigorij.backend.database.enums.permissions.PermissionLevel;
import com.gmail.grigorij.backend.database.facades.PermissionFacade;
import com.gmail.grigorij.utils.ProjectConstants;
import com.vaadin.flow.theme.lumo.Lumo;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@NamedQueries({
		@NamedQuery(name = User.QUERY_ALL,
				query = "SELECT user FROM User user ORDER BY user.username ASC"),

		@NamedQuery(name = User.QUERY_BY_ID,
				query = "SELECT user FROM User user WHERE" +
						" user.id = :" + ProjectConstants.ID_VAR),

		@NamedQuery(name = User.QUERY_BY_EMAIL,
				query = "SELECT user FROM User user WHERE" +
						" user.person IS NOT NULL AND" +
						" user.person.email = :" + ProjectConstants.VAR1),

		@NamedQuery(name = User.QUERY_BY_USERNAME,
				query = "SELECT user FROM User user WHERE" +
						" user.username = :" + ProjectConstants.VAR1),

		@NamedQuery(name = User.QUERY_BY_USERNAME_AND_PASSWORD,
				query = "SELECT user FROM User user WHERE" +
						" user.username = :" + ProjectConstants.VAR1 + " AND" +
						" user.password = :" + ProjectConstants.VAR2),

		@NamedQuery(name = User.QUERY_ALL_BY_COMPANY,
				query = "SELECT user FROM User user WHERE" +
						" user.company.id = :" + ProjectConstants.ID_VAR),

		@NamedQuery(name = User.QUERY_ALL_BY_PERMISSION_LEVEL,
				query = "SELECT user FROM User user WHERE" +
						" user.permissionLevel = :" + ProjectConstants.VAR1)
})
public class User extends EntityPojo {

	public static final String QUERY_ALL = "get_all_users";
	public static final String QUERY_BY_ID = "get_user_by_id";
	public static final String QUERY_BY_EMAIL = "get_user_by_email";
	public static final String QUERY_BY_USERNAME = "get_user_by_username";
	public static final String QUERY_BY_USERNAME_AND_PASSWORD = "get_user_by_username_and_password";
	public static final String QUERY_ALL_BY_COMPANY = "get_users_by_company";
	public static final String QUERY_ALL_BY_PERMISSION_LEVEL = "get_users_by_permission_level";


	@Column(name = "username")
	private String username;

	@Column(name = "password")
	private String password;

	@Column(name = "theme_variant")
	private String themeVariant = Lumo.LIGHT;

	@Column(name = "locale")
	private String locale;

	@OneToOne
	private Company company;

	@Embedded
	private Location address; //NECESSARY?

	@Embedded
	private Person person;

	@Enumerated(EnumType.STRING)
	private PermissionLevel permissionLevel = PermissionLevel.USER;

	@ElementCollection
	@Enumerated(EnumType.STRING)
	private List<Permission> permissions;


	public User() {
		permissions = PermissionFacade.getInstance().getDefaultUserPermissions();
	}

	public User(User other) {
		this.username = other.username;
		this.password = other.password;
		this.themeVariant = other.themeVariant;
		this.locale = other.locale;
		this.company = other.company;
		this.address = other.address;
		this.person = other.person;
		this.permissionLevel = other.permissionLevel;
		this.permissions = other.permissions;
	}


	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}

	public String getThemeVariant() {
		return themeVariant;
	}
	public void setThemeVariant(String themeVariant) {
		this.themeVariant = themeVariant;
	}

	public String getLocale() {
		return locale;
	}
	public void setLocale(String locale) {
		this.locale = locale;
	}

	public Company getCompany() {
		return company;
	}
	public void setCompany(Company company) {
		this.company = company;
	}

	public Location getAddress() {
		return address;
	}
	public void setAddress(Location address) {
		this.address = address;
	}

	public Person getPerson() {
		return person;
	}
	public void setPerson(Person person) {
		this.person = person;
	}

	public PermissionLevel getPermissionLevel() {
		return permissionLevel;
	}
	public void setPermissionLevel(PermissionLevel permissionLevel) {
		this.permissionLevel = permissionLevel;
	}

	public List<Permission> getPermissions() {
		return permissions;
	}
	public void setPermissions(List<Permission> permissions) {
		this.permissions = permissions;
	}


	public String getFullName() {
		if (person == null) {
			return "";
		} else {
			if (person.getFullName().length() <= 0) {
				return username;
			}
			return person.getFullName();
		}
	}

	public String getInitials() {
		if (person == null) {
			return "";
		} else {
			return person.getInitials();
		}
	}
}