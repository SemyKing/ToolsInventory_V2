package com.gmail.grigorij.backend.entities.user;

import com.gmail.grigorij.backend.entities.Address;
import com.gmail.grigorij.backend.entities.EntityPojo;

import javax.persistence.Embedded;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public class Person extends EntityPojo {

	private String firstName;
	private String lastName;
	private String phoneNumber;
	private String email;

	@Embedded
	private Address address;

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

	public Address getAddress() {
		return address;
	}
	public void setAddress(Address address) {
		this.address = address;
	}


	public String getInitials() {
		String initials = getFirstName().substring(0, 1) + getLastName().substring(0, 1);
		return initials;
	}
}
