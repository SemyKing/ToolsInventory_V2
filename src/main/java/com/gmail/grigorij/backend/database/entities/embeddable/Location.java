package com.gmail.grigorij.backend.database.entities.embeddable;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Embeddable;


@Embeddable
@AttributeOverride(name="name", column=@Column(name="location_name"))
public class Location {

	private String name = "";

	private String addressLine1 = "";
	private String addressLine2 = "";
	private String postcode = "";
	private String city = "";
	private String country = "";

	public Location() {}

	public Location(Location other) {
		this.name = other.name;
		this.addressLine1 = other.addressLine1;
		this.addressLine2 = other.addressLine2;
		this.postcode = other.postcode;
		this.city = other.city;
		this.country = other.country;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

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
}
