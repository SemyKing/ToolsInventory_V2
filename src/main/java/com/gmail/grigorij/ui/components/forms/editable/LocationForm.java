package com.gmail.grigorij.ui.components.forms.editable;

import com.gmail.grigorij.backend.embeddable.Location;
import com.gmail.grigorij.ui.utils.UIUtils;
import com.gmail.grigorij.ui.utils.css.LumoStyles;
import com.gmail.grigorij.utils.ProjectConstants;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;


public class LocationForm extends FormLayout {

	private final String CLASS_NAME = "form";

	private Binder<Location> binder;
	private Location tempLocation;
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
//		addClassNames(LumoStyles.Padding.Bottom.S);
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
				.asRequired("Name is required")
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


	public void setLocation(Location originalLocation) {
		isNew = false;

		if (originalLocation == null) {
			tempLocation = new Location();
			isNew = true;
		} else {
			tempLocation = originalLocation;
		}

		binder.readBean(tempLocation);
	}

	public Location getLocation() {
		try {
			binder.validate();
			if (binder.isValid()) {
				binder.writeBean(tempLocation);

				return tempLocation;
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
}