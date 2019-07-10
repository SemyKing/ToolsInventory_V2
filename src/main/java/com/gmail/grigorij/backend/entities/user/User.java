package com.gmail.grigorij.backend.entities.user;


import com.gmail.grigorij.backend.entities.company.Company;
import com.gmail.grigorij.backend.entities.location.Location;
import com.vaadin.flow.theme.lumo.Lumo;

import javax.persistence.*;

@Entity
@Table(name = "users")
@NamedQueries({
		@NamedQuery(
				name="User.findUserInDatabase",
				query="SELECT user FROM User user WHERE user.username = :username AND user.password = :password"),
		@NamedQuery(
				name="User.getUserByUsername",
				query="SELECT user FROM User user WHERE user.username = :username ORDER BY user.username ASC"),
		@NamedQuery(
				name="User.getAllUsers",
				query="SELECT user FROM User user ORDER BY user.username ASC"),
		@NamedQuery(
				name="User.getUsersByCompanyId",
				query="SELECT user FROM User user WHERE user.company.id = :id_var"),
		@NamedQuery(
				name="User.getUserById",
				query="SELECT user FROM User user WHERE user.id = :id")
})
public class User extends Person {

	@Column(name = "username")
	private String username;

	@Column(name = "password")
	private String password;

	@Column(name = "theme_variant")
	private String themeVariant;

	@Column(name = "locale")
	private String locale;

	@OneToOne
	private Company company;

	@Column(name = "access_group")
	private int accessGroup;

	public User() {
		//Default theme for new users
		this.themeVariant = Lumo.LIGHT;
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

	public int getAccessGroup() {
		return accessGroup;
	}
	public void setAccessGroup(int accessGroup) {
		this.accessGroup = accessGroup;
	}

	public String getThemeVariant() {
		return themeVariant;
	}
	public void setThemeVariant(String themeVariant) {
		this.themeVariant = themeVariant;
	}


	public static User getEmptyUser() {
		User user = new User();
		user.setUsername("");
		user.setPassword("");
//		user.setCompanyId(-1);
		user.setCompany(null);
		user.setDeleted(false);
		user.setFirstName("");
		user.setLastName("");
		user.setEmail("");

		user.setThemeVariant(Lumo.LIGHT);
		user.setAddress(new Location());

		return user;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}
}