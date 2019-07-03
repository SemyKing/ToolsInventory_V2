package com.gmail.grigorij.ui.utils.forms;

import com.gmail.grigorij.backend.entities.location.Location;
import com.gmail.grigorij.ui.utils.UIUtils;
import com.gmail.grigorij.ui.utils.components.FlexBoxLayout;
import com.gmail.grigorij.ui.utils.css.FlexDirection;
import com.gmail.grigorij.ui.utils.css.size.Size;
import com.gmail.grigorij.ui.utils.css.size.Top;
import com.gmail.grigorij.utils.ProjectConstants;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;

public class LocationForm extends FormLayout {

	private Binder<Location> binder = new Binder<>(Location.class);
	private Location location;

	public LocationForm() {
		TextField locationName = getTextField("Location Name", "", Top.S);
		locationName.setRequired(true);
		TextField addressLine1 = getTextField("Address Line 1", "", Top.S);
		TextField addressLine2 = getTextField("Address Line 2", "", Top.S);
		TextField postcode = getTextField("Postcode", "32%", Top.S);
		TextField city = getTextField("City", "32%", Top.S);
		TextField country = getTextField("Country", "32%", Top.S);

		FlexBoxLayout layout = new FlexBoxLayout();
		layout.setFlexDirection(FlexDirection.ROW);
		layout.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
		layout.add(postcode, city, country);

		UIUtils.setColSpan(2, locationName, layout);

//		addClassNames(LumoStyles.Padding.Bottom.M, LumoStyles.Padding.Horizontal.M, LumoStyles.Padding.Top.XS);
		setResponsiveSteps(
				new FormLayout.ResponsiveStep("0", 1, FormLayout.ResponsiveStep.LabelsPosition.TOP),
				new FormLayout.ResponsiveStep(ProjectConstants.COL_2_MIN_WIDTH, 2, FormLayout.ResponsiveStep.LabelsPosition.TOP));
		add(locationName);
		add(addressLine1);
		add(addressLine2);
		add(layout);

		binder.forField(locationName)
				.asRequired("Location name is required")
				.bind(Location::getName, Location::setName);
		binder.forField(addressLine1)
				.bind(Location::getAddressLine1, Location::setAddressLine1);
		binder.forField(addressLine2)
				.bind(Location::getAddressLine2, Location::setAddressLine2);
		binder.forField(postcode)
				.bind(Location::getPostcode, Location::setPostcode);
		binder.forField(city)
				.bind(Location::getCity, Location::setCity);
		binder.forField(country)
				.bind(Location::getCountry, Location::setCountry);
	}

	private TextField getTextField(String label, String width, Size... sizes) {
		TextField textField = new TextField();

		if (label.length() > 0) {
			textField.setLabel(label);
		}

		if (width.length() > 0) {
			textField.setWidth(width);
		}

		for (Size size : sizes) {
			for (String attribute : size.getPaddingAttributes()) {
				textField.getElement().getStyle().set(attribute, size.getVariable());
			}
		}

		return textField;
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
				binder.writeBean(this.location);

				return location;
			}
		} catch (ValidationException e) {
			e.printStackTrace();
			return null;
		}
		return null;
	}
}