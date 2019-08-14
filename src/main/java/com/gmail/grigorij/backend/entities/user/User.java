package com.gmail.grigorij.backend.entities.user;


import com.gmail.grigorij.backend.entities.EntityPojo;
import com.gmail.grigorij.backend.enums.permissions.AccessGroup;
import com.gmail.grigorij.backend.embeddable.AccessRight;
import com.gmail.grigorij.backend.entities.company.Company;
import com.gmail.grigorij.backend.embeddable.Location;
import com.gmail.grigorij.backend.embeddable.Person;
import com.gmail.grigorij.backend.entities.message.Message;
import com.vaadin.flow.theme.lumo.Lumo;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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

	//Set with IDs of tools that this user has in use
	private Set<Long> toolsInUse = new HashSet<>();

	//Set with IDs of tools that this user has reserved
	private Set<Long> toolsReserved = new HashSet<>();

	@OneToMany(fetch = FetchType.LAZY)
	@JoinColumn(name = "recipient_id")
	private Set<Message> messages = new HashSet<>();


	@Enumerated( EnumType.STRING )
	private AccessGroup accessGroup = AccessGroup.VIEWER;

	@ElementCollection
	private List<AccessRight> accessRights = new ArrayList<>();

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

	public AccessGroup getAccessGroup() {
		return accessGroup;
	}
	public void setAccessGroup(AccessGroup accessGroup) {
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

	public Set<Long> getToolsInUse() {
		return toolsInUse;
	}
	public void setToolsInUse(Set<Long> toolsInUse) {
		this.toolsInUse = toolsInUse;
	}
	public void addToolInUse(Long toolId) {
		this.toolsInUse.add(toolId);
	}
	public void removeToolInUse(Long toolId) {
		this.toolsInUse.remove(toolId);
	}

	public Set<Long> getToolsReserved() {
		return toolsReserved;
	}
	public void setToolsReserved(Set<Long> toolsReserved) {
		this.toolsReserved = toolsReserved;
	}
	public void addToolReserved(Long toolId) {
		this.toolsReserved.add(toolId);
	}
	public void removeToolReserved(Long toolId) {
		this.toolsReserved.remove(toolId);
	}

	public Set<Message> getMessages() {
		return messages;
	}
	public void setMessages(Set<Message> messages) {
		this.messages = messages;
	}
	public void addMessage(Message message) {
		this.messages.add(message);
	}
	public void removeMessage(Message message) {
		this.messages.remove(message);
	}

	public List<AccessRight> getAccessRights() {
		List<AccessRight> copyOfAccessRights = new ArrayList<>();
		for (AccessRight ar : accessRights) {
			copyOfAccessRights.add(new AccessRight(ar));
		}
		return copyOfAccessRights;
	}
	public void setAccessRights(List<AccessRight> accessRights) {
		this.accessRights = accessRights;
	}

	public Set<Long> getAllTools() {
		Set<Long> allTools = new HashSet<>();
		allTools.addAll(this.toolsInUse);
		allTools.addAll(this.toolsReserved);
		return allTools;
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