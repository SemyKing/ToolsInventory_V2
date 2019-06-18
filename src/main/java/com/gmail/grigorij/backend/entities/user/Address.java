package com.gmail.grigorij.backend.entities.user;

import javax.persistence.*;


@Embeddable
public class Address {

	private String addressLine1;
	private String addressLine2;
	private String postcode;
	private String city;
	private String country;

	public Address() {}

	public String getAddressLine1() {
		return addressLine1;
	}
	public void setAddressLine1(String addressLine1) {
		this.addressLine1 = addressLine1;
	}

	public String getAddressLine2() {
		return addressLine2;
	}
	public void setAddressLine2(String addressLine2) {
		this.addressLine2 = addressLine2;
	}

	public String getPostcode() {
		return postcode;
	}
	public void setPostcode(String postcode) {
		this.postcode = postcode;
	}

	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}

	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}

	public static Address getEmptyAddress() {
		Address address = new Address();
		address.setAddressLine1("");
		address.setAddressLine2("");
		address.setPostcode("");
		address.setCity("");
		address.setCountry("");

		return address;
	}
}
