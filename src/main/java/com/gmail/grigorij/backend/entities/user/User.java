package com.gmail.grigorij.backend.entities.user;


import com.gmail.grigorij.backend.embeddable.Location;
import com.gmail.grigorij.backend.embeddable.Person;
import com.gmail.grigorij.backend.entities.EntityPojo;
import com.gmail.grigorij.backend.entities.company.Company;
import com.gmail.grigorij.backend.enums.permissions.PermissionLevel;
import com.vaadin.flow.theme.lumo.Lumo;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@NamedQueries({
		@NamedQuery(
				name="getUserByUsernameAndPassword",
				query="SELECT user FROM User user WHERE" +
						" user.username = :username_var AND" +
						" user.password = :password_var"),

		@NamedQuery(
				name="getUserByUsername",
				query="SELECT user FROM User user WHERE" +
						" user.username = :username_var ORDER BY user.username ASC"),

		@NamedQuery(
				name="getAllUsers",
				query="SELECT user FROM User user ORDER BY user.username ASC"),

		@NamedQuery(
				name="getUsersInCompany",
				query="SELECT user FROM User user WHERE" +
						" user.company.id = :id_var"),

		@NamedQuery(
				name="getUserById",
				query="SELECT user FROM User user WHERE" +
						" user.id = :id_var"),

		@NamedQuery(
				name="getUserByEmail",
				query="SELECT user FROM User user WHERE" +
						" user.person IS NOT NULL AND" +
						" user.person.email = :email_var")
})
public class User extends EntityPojo {

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

	@Enumerated( EnumType.STRING )
	private PermissionLevel permissionLevel = PermissionLevel.VIEWER;

	@ElementCollection
	@Enumerated( EnumType.STRING )
	private List<PermissionTest> permissions = new ArrayList<>();


	public User() {}


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

	public List<PermissionTest> getPermissions() {
		return permissions;
	}
	public void setPermissions(List<PermissionTest> permissions) {
		this.permissions = permissions;
	}


	public String getFullName() {
		if (this.person == null) {
			return "";
		} else {
			return this.person.getFullName();
		}
	}

	public String getInitials() {
		if (this.person == null) {
			return "";
		} else {
			return this.person.getInitials();
		}
	}
}