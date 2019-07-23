package com.gmail.grigorij.ui.utils.forms.readonly;

import com.gmail.grigorij.backend.entities.location.Location;
import com.gmail.grigorij.ui.utils.UIUtils;
import com.gmail.grigorij.ui.utils.components.FlexBoxLayout;
import com.gmail.grigorij.ui.utils.css.LumoStyles;
import com.gmail.grigorij.utils.ProjectConstants;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ReadOnlyHasValue;

public class ReadOnlyLocationForm extends FormLayout {

	private Binder<Location> binder = new Binder<>(Location.class);


	public ReadOnlyLocationForm() {

		TextField locationNameField = UIUtils.getTextFieldTopS("Name", "",  "", null, null, true);
		ReadOnlyHasValue<Location> locationName = new ReadOnlyHasValue<>(location -> {
			locationNameField.setValue( location.getName() );
		});

		TextField addressLine1Field = UIUtils.getTextFieldTopS("Address 1", "",   "", null, null, true);
		ReadOnlyHasValue<Location> addressLine1 = new ReadOnlyHasValue<>(location -> {
			addressLine1Field.setValue( location.getAddressLine1() );
		});

		TextField addressLine2Field = UIUtils.getTextFieldTopS("Address 2", "Optional",   "", null, null, true);
		ReadOnlyHasValue<Location> addressLine2 = new ReadOnlyHasValue<>(location -> {
			addressLine2Field.setValue( location.getAddressLine2() );
		});

		TextField postcodeField = UIUtils.getTextFieldTopS("Postcode", "",   "", null, null, true);
		ReadOnlyHasValue<Location> postcode = new ReadOnlyHasValue<>(location -> {
			postcodeField.setValue( location.getPostcode() );
		});

		TextField cityField = UIUtils.getTextFieldTopS("City", "",   "", null, null, true);
		ReadOnlyHasValue<Location> city = new ReadOnlyHasValue<>(location -> {
			cityField.setValue( location.getCity() );
		});

		TextField countryField = UIUtils.getTextFieldTopS("Country Code", "",   "", null, null, true);
		ReadOnlyHasValue<Location> country = new ReadOnlyHasValue<>(location -> {
			countryField.setValue( location.getCountry() );
		});

		//CITY & COUNTRY
		FlexBoxLayout cityCountryLayout = UIUtils.getFormRowLayout(cityField, countryField);

		UIUtils.setColSpan(2, locationNameField);

		addClassNames(LumoStyles.Padding.Bottom.M, LumoStyles.Padding.Horizontal.M, LumoStyles.Padding.Top.S);
		setResponsiveSteps(
				new FormLayout.ResponsiveStep("0", 1, FormLayout.ResponsiveStep.LabelsPosition.TOP),
				new FormLayout.ResponsiveStep(ProjectConstants.COL_2_MIN_WIDTH, 2, FormLayout.ResponsiveStep.LabelsPosition.TOP));

		add(locationNameField);
		add(addressLine1Field);
		add(addressLine2Field);
		add(postcodeField);
		add(cityCountryLayout);

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
