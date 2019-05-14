package com.gmail.grigorij.backend.entities;


import javax.persistence.*;
import java.io.Serializable;


@Entity
@Table(name = "users")
@NamedQueries({
		@NamedQuery(name="User.findUserInDatabase", query="SELECT u FROM User u WHERE u.username = :username AND u.password = :password")})
public class User implements Serializable {

	@Id
	@GeneratedValue
	@Column(name = "id")
	private int id;

	@Column(name = "username")
	private String username;

	@Column(name = "password")
	private String password;

	@Column(name = "company_id")
	private int company_id;

	@Column(name = "access_group")
	private int access_group;

	public User() {}

	public User(String username, String password) {
		this.username = username;
		this.password = password;
	}

	public int getId() {
		return id;
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

	public int getCompany_id() {
		return company_id;
	}

	public void setCompany_id(int company_id) {
		this.company_id = company_id;
	}

	public int getAccess_group() {
		return access_group;
	}

	public void setAccess_group(int access_group) {
		this.access_group = access_group;
	}
}
