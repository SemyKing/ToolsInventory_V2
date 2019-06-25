package com.gmail.grigorij.ui.utils.forms;

import com.gmail.grigorij.backend.entities.location.Address;
import com.gmail.grigorij.backend.entities.location.Location;
import com.gmail.grigorij.ui.utils.css.LumoStyles;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;

public class LocationForm extends FormLayout {

	private Binder<Location> locationBinder = new Binder<>(Location.class);
	private Location location;

	public LocationForm() {
		TextField locationName = new TextField();
		locationName.setWidth("100%");
		TextField addressLine1 = new TextField();
		addressLine1.setWidth("100%");
		TextField addressLine2 = new TextField();
		addressLine2.setWidth("100%");
		TextField postcode = new TextField();
		postcode.setWidth("100%");
		TextField city = new TextField();
		city.setWidth("100%");
		TextField country = new TextField();
		country.setWidth("100%");

		addClassNames(LumoStyles.Padding.Bottom.M, LumoStyles.Padding.Horizontal.M, LumoStyles.Padding.Top.XS);
		setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1, FormLayout.ResponsiveStep.LabelsPosition.TOP));
		addFormItem(addressLine1, "Address Line 1");
		addFormItem(addressLine2, "Address Line 2");
		addFormItem(postcode, "Postcode");
		addFormItem(city, "City");
		addFormItem(country, "Country");

		locationBinder.forField(locationName)
				.bind(Location::getLocationName, Location::setLocationName);
		locationBinder.forField(addressLine1)
				.bind(Location::getAddressLine1, Location::setAddressLine1);
		locationBinder.forField(addressLine2)
				.bind(Location::getAddressLine2, Location::setAddressLine2);
		locationBinder.forField(postcode)
				.bind(Location::getPostcode, Location::setPostcode);
		locationBinder.forField(city)
				.bind(Location::getCity, Location::setCity);
		locationBinder.forField(country)
				.bind(Address::getCountry, Address::setCountry);
	}

	public void setLocation(Location location) {
		if (location == null) {
			location = Location.getEmptyLocation();
		}
		this.location = location;
		locationBinder.readBean(this.location);
	}

	public Location getLocation() {
		try {
			locationBinder.validate();
			if (locationBinder.isValid()) {
				locationBinder.writeBean(location);

				return location;
			}
		} catch (ValidationException e) {
			e.printStackTrace();
			return null;
		}
		return null;
	}
}
