package com.gmail.grigorij.backend.entities.location;

import javax.persistence.Embeddable;


@Embeddable
public class Location extends Address {

	private String locationName;

	public Location () {
		this.locationName = "";
	}


	public String getLocationName() {
		return locationName;
	}
	public void setLocationName(String locationName) {
		this.locationName = locationName;
	}


	public static Location getEmptyLocation() {
		Location location = new Location();

		location.setLocationName("");
		location.setAddressLine1("");
		location.setAddressLine2("");
		location.setPostcode("");
		location.setCity("");
		location.setCountry("");

		return location;
	}
}
