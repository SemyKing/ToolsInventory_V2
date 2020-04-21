package com.gmail.grigorij.ui.components.forms;

import com.gmail.grigorij.backend.database.entities.embeddable.Location;
import com.gmail.grigorij.utils.ProjectConstants;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;

import java.util.ArrayList;
import java.util.List;


public class LocationForm extends FormLayout {

	private final String CLASS_NAME = "form";

	private Binder<Location> binder;
	private Location location, original_Location;
	private boolean isNew;

	// FORM ITEMS
	private TextField nameField;
	private TextField countryField;
	private TextField addressLine1Field;
	private TextField addressLine2Field;
	private TextField cityField;
	private TextField postcodeField;

	private Div postcodeCityDiv;


	public LocationForm() {
		addClassName(CLASS_NAME);

		constructFormItems();

		constructForm();

		constructBinder();
	}


	private void constructFormItems() {
		nameField = new TextField("Location Name");
		nameField.setRequired(true);

		countryField = new TextField("Country");

		addressLine1Field = new TextField("Address");
		addressLine2Field = new TextField("", "Optional");

		postcodeField = new TextField("Postcode");
		postcodeField.setWidth("calc(50% - (0.5 * var(--vaadin-form-layout-column-spacing)))"); // 50% for 2 items

		cityField = new TextField("City");
		cityField.setWidth("calc(50% - (0.5 * var(--vaadin-form-layout-column-spacing)))");

		//POSTCODE & CITY
		postcodeCityDiv = new Div();
		postcodeCityDiv.addClassName(ProjectConstants.CONTAINER_SPACE_BETWEEN);
		postcodeCityDiv.add(postcodeField, cityField);

		setColspan(postcodeCityDiv, 2);
	}

	private void constructForm() {
		setResponsiveSteps(
				new FormLayout.ResponsiveStep("0", 1, FormLayout.ResponsiveStep.LabelsPosition.TOP),
				new FormLayout.ResponsiveStep(ProjectConstants.COL_2_MIN_WIDTH, 2, FormLayout.ResponsiveStep.LabelsPosition.TOP));
		add(nameField);
		add(countryField);
		add(addressLine1Field);
		add(addressLine2Field);
		add(postcodeCityDiv);
	}

	private void constructBinder() {
		binder = new Binder<>(Location.class);

		binder.forField(nameField)
				.asRequired("Location Name is required")
				.bind(Location::getName, Location::setName);
		binder.forField(addressLine1Field)
				.bind(Location::getAddressLine1, Location::setAddressLine1);
		binder.forField(addressLine2Field)
				.bind(Location::getAddressLine2, Location::setAddressLine2);
		binder.forField(postcodeField)
				.bind(Location::getPostcode, Location::setPostcode);
		binder.forField(cityField)
				.bind(Location::getCity, Location::setCity);
		binder.forField(countryField)
				.bind(Location::getCountry, Location::setCountry);
	}


	public void setLocation(Location location) {
		isNew = false;

		if (location == null) {
			this.location = new Location();
			isNew = true;
		} else {
			this.location = location;
		}

		original_Location = new Location(this.location);

		binder.readBean(this.location);
	}

	public Location getLocation() {
		try {
			binder.validate();
			if (binder.isValid()) {
				binder.writeBean(location);

				return location;
			}
		} catch (ValidationException e) {
			e.printStackTrace();
			return null;
		}
		return null;
	}

	public boolean isNew() {
		return isNew;
	}

	public List<String> getChanges() {
		List<String> changes = new ArrayList<>();

		if (!original_Location.getName().equals(location.getName())) {
			changes.add("Location name changed from: '" + original_Location.getName() + "', to: '" + location.getName() + "'");
		}
		if (!original_Location.getCountry().equals(location.getCountry())) {
			changes.add("Country changed from: '" + original_Location.getCountry() + "', to: '" + location.getCountry() + "'");
		}
		if (!original_Location.getAddressLine1().equals(location.getAddressLine1())) {
			changes.add("Address Line 1 changed from: '" + original_Location.getAddressLine1() + "', to: '" + location.getAddressLine1() + "'");
		}
		if (!original_Location.getAddressLine2().equals(location.getAddressLine2())) {
			changes.add("Address Line 2 changed from: '" + original_Location.getAddressLine2() + "', to: '" + location.getAddressLine2() + "'");
		}
		if (!original_Location.getPostcode().equals(location.getPostcode())) {
			changes.add("Postcode changed from: '" + original_Location.getPostcode() + "', to: '" + location.getPostcode() + "'");
		}
		if (!original_Location.getCity().equals(location.getCity())) {
			changes.add("City changed from: '" + original_Location.getCity() + "', to: '" + location.getCity() + "'");
		}

		return changes;
	}
}