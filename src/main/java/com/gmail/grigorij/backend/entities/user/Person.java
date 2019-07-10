package com.gmail.grigorij.backend.entities.user;

import com.gmail.grigorij.backend.entities.EntityPojo;
import com.gmail.grigorij.backend.entities.location.Location;

import javax.persistence.*;


@MappedSuperclass
public class Person extends EntityPojo {

	private String firstName = "";
	private String lastName = "";
	private String phoneNumber = "";
	private String email = "";

	@Embedded
	private Location address = null;

	public Person() {
	}

	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}


	public String getInitials() {
		String initials = "";
		if (getFirstName().length() >= 1) {
			initials += getFirstName().substring(0, 1);
		} else {
			initials += " ";
		}

		if (getLastName().length() >= 1) {
			initials += getLastName().substring(0, 1);
		} else {
			initials += " ";
		}
		return initials;
	}

	public Location getAddress() {
		return (this.address == null) ? new Location() : address;
	}
	public void setAddress(Location address) {
		this.address = address;
	}
}
