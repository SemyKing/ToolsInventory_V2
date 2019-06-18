package com.gmail.grigorij.backend.entities.user;


import com.vaadin.flow.theme.lumo.Lumo;

import javax.persistence.*;

@Entity
@Table(name = "users")
@NamedQueries({
		@NamedQuery(name="User.findUserInDatabase",
				query="SELECT user FROM User user WHERE user.username = :username AND user.password = :password"),
		@NamedQuery(name="User.findUserInDatabaseByUsername",
				query="SELECT user FROM User user WHERE user.username = :username ORDER BY user.username ASC"),
		@NamedQuery(name="User.listAllUsers",
				query="SELECT user FROM User user ORDER BY user.username ASC"),
		@NamedQuery(name="User.listUsersByCompanyId",
				query="SELECT user FROM User user WHERE user.company_id = :companyId")
})
public class User extends Person {

	@Column(name = "username")
	private String username;

	@Column(name = "password")
	private String password;

	@Column(name = "theme_variant")
	private String themeVariant;

	@Column(name = "company_id")
	private long company_id;

	@Column(name = "access_group")
	private int access_group;

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

	public long getCompany_id() {
		return company_id;
	}
	public void setCompany_id(long company_id) {
		this.company_id = company_id;
	}

	public int getAccess_group() {
		return access_group;
	}
	public void setAccess_group(int access_group) {
		this.access_group = access_group;
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
		user.setCompany_id(-1);
		user.setDeleted(false);
		user.setFirstName("");
		user.setLastName("");
		user.setEmail("");

		user.setThemeVariant(Lumo.LIGHT);
		user.setAddress(Address.getEmptyAddress());

		return user;
	}
}