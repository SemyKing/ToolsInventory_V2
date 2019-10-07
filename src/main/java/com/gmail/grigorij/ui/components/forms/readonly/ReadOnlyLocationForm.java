package com.gmail.grigorij.ui.components.forms.readonly;

import com.gmail.grigorij.backend.embeddable.Location;
import com.gmail.grigorij.ui.utils.UIUtils;
import com.gmail.grigorij.ui.components.layouts.FlexBoxLayout;
import com.gmail.grigorij.ui.utils.css.LumoStyles;
import com.gmail.grigorij.utils.ProjectConstants;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ReadOnlyHasValue;

public class ReadOnlyLocationForm extends FormLayout {

	private Binder<Location> binder = new Binder<>(Location.class);


	public ReadOnlyLocationForm() {

		TextField locationNameField = new TextField("Name");
		locationNameField.setReadOnly(true);
		ReadOnlyHasValue<Location> locationName = new ReadOnlyHasValue<>(location -> {
			locationNameField.setValue( location.getName() );
		});

		TextField addressLine1Field = new TextField("Address");
		addressLine1Field.setReadOnly(true);
		ReadOnlyHasValue<Location> addressLine1 = new ReadOnlyHasValue<>(location -> {
			addressLine1Field.setValue( location.getAddressLine1() );
		});

		TextField addressLine2Field = new TextField("", "Optional");
		addressLine2Field.setReadOnly(true);
		ReadOnlyHasValue<Location> addressLine2 = new ReadOnlyHasValue<>(location -> {
			addressLine2Field.setValue( location.getAddressLine2() );
		});

		TextField cityField = new TextField("City");
		cityField.setReadOnly(true);
		ReadOnlyHasValue<Location> city = new ReadOnlyHasValue<>(location -> {
			cityField.setValue( location.getCity() );
		});

		TextField postcodeField = new TextField("Postcode");
		postcodeField.setReadOnly(true);
		postcodeField.setWidth(ProjectConstants.FORM_HALF_WIDTH);
		ReadOnlyHasValue<Location> postcode = new ReadOnlyHasValue<>(location -> {
			postcodeField.setValue( location.getPostcode() );
		});

		TextField countryField = new TextField("Country");
		countryField.setReadOnly(true);
		countryField.setWidth(ProjectConstants.FORM_HALF_WIDTH);
		ReadOnlyHasValue<Location> country = new ReadOnlyHasValue<>(location -> {
			countryField.setValue( location.getCountry() );
		});

		//POSTCODE & COUNTRY
		FlexBoxLayout postcodeCountryLayout = UIUtils.getFormRowLayout(postcodeField, countryField, true);

		UIUtils.setColSpan(2, locationNameField);

		addClassNames(LumoStyles.Padding.Bottom.S, LumoStyles.Padding.Top.S);
		setResponsiveSteps(
				new FormLayout.ResponsiveStep("0", 1, FormLayout.ResponsiveStep.LabelsPosition.TOP),
				new FormLayout.ResponsiveStep(ProjectConstants.COL_2_MIN_WIDTH, 2, FormLayout.ResponsiveStep.LabelsPosition.TOP));

		add(locationNameField);
		add(addressLine1Field);
		add(addressLine2Field);
		add(postcodeField);
		add(postcodeCountryLayout);

		binder.forField(locationName)
				.bind(location -> location, null);
		binder.forField(addressLine1)
				.bind(location -> location, null);
		binder.forField(addressLine2)
				.bind(location -> location, null);
		binder.forField(postcode)
				.bind(location -> location, null);
		binder.forField(city)
				.bind(location -> location, null);
		binder.forField(country)
				.bind(location -> location, null);
	}

	public void setLocation(Location location) {
		try {
			binder.removeBean();
			binder.readBean(location);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
