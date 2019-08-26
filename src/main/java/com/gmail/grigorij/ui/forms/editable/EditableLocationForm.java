package com.gmail.grigorij.ui.forms.editable;

import com.gmail.grigorij.backend.embeddable.Location;
import com.gmail.grigorij.ui.utils.UIUtils;
import com.gmail.grigorij.ui.components.FlexBoxLayout;
import com.gmail.grigorij.ui.utils.css.LumoStyles;
import com.gmail.grigorij.utils.ProjectConstants;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;


public class EditableLocationForm extends FormLayout {

	private Binder<Location> binder = new Binder<>(Location.class);
	private Location location;

	public EditableLocationForm() {
		TextField nameField = new TextField("Location Name");
		nameField.setRequired(true);

		TextField addressLine1Field = new TextField("Address");

		TextField addressLine2Field = new TextField("", "Optional");

		TextField cityField = new TextField("City");

		TextField postcodeField = new TextField("Postcode");
		postcodeField.setWidth(ProjectConstants.FORM_HALF_WIDTH);

		TextField countryField = new TextField("Country");
		countryField.setWidth(ProjectConstants.FORM_HALF_WIDTH);

		//POSTCODE & COUNTRY
		FlexBoxLayout postcodeCountryLayout = UIUtils.getFormRowLayout(postcodeField, countryField, true);


		UIUtils.setColSpan(2, nameField);

		addClassNames(LumoStyles.Padding.Bottom.S, LumoStyles.Padding.Top.S);
		setResponsiveSteps(
				new FormLayout.ResponsiveStep("0", 1, FormLayout.ResponsiveStep.LabelsPosition.TOP),
				new FormLayout.ResponsiveStep(ProjectConstants.COL_2_MIN_WIDTH, 2, FormLayout.ResponsiveStep.LabelsPosition.TOP));
		add(nameField);
		add(addressLine1Field);
		add(addressLine2Field);
		add(cityField);
		add(postcodeCountryLayout);

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


	public void setLocation(Location location) {
		if (location == null) {
			location = new Location();
		}
		this.location = location;
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
}