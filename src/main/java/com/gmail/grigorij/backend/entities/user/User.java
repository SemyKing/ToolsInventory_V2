package com.gmail.grigorij.backend.entities.user;


import com.gmail.grigorij.backend.entities.EntityPojo;
import com.gmail.grigorij.backend.entities.company.Company;
import com.gmail.grigorij.backend.entities.embeddable.Location;
import com.gmail.grigorij.backend.entities.embeddable.Person;
import com.gmail.grigorij.backend.entities.inventory.InventoryEntity;
import com.vaadin.flow.theme.lumo.Lumo;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@NamedQueries({
		@NamedQuery(
				name="findUserInDatabase",
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
						" user.id = :id_var")
})
public class User extends EntityPojo {

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


	@Embedded
	private Location address;

	@Embedded
	private Person person;

	@OneToMany(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private Set<InventoryEntity> toolsInUse = new HashSet<>();

	@OneToMany(fetch = FetchType.LAZY)
	@JoinColumn(name = "reservedbyuser_id")
	private Set<InventoryEntity> toolsReserved = new HashSet<>();



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

	public int getAccessGroup() {
		return accessGroup;
	}
	public void setAccessGroup(int accessGroup) {
		this.accessGroup = accessGroup;
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


	public static User getEmptyUser() {
		User user = new User();
		user.setUsername("");
		user.setPassword("");
		user.setCompany(null);
		user.setDeleted(false);
		user.setThemeVariant(Lumo.LIGHT);

		return user;
	}

	public Set<InventoryEntity> getToolsInUse() {
		return toolsInUse;
	}
	public void setToolsInUse(Set<InventoryEntity> toolsInUse) {
		this.toolsInUse = toolsInUse;
	}
	public void addToolInUse(InventoryEntity tool) {
		this.toolsInUse.add(tool);
	}
	public void removeToolInUse(InventoryEntity tool) {
		this.toolsInUse.remove(tool);
	}

	public Set<InventoryEntity> getToolsReserved() {
		return toolsReserved;
	}
	public void setToolsReserved(Set<InventoryEntity> toolsReserved) {
		this.toolsReserved = toolsReserved;
	}
	public void addToolReserved(InventoryEntity tool) {
		this.toolsReserved.add(tool);
	}
	public void removeToolReserved(InventoryEntity tool) {
		this.toolsReserved.remove(tool);
	}
}