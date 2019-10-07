package com.gmail.grigorij.ui.components.forms.editable;

import com.gmail.grigorij.backend.database.facades.CompanyFacade;
import com.gmail.grigorij.backend.entities.company.Company;
import com.gmail.grigorij.backend.embeddable.Location;
import com.gmail.grigorij.backend.embeddable.Person;
import com.gmail.grigorij.ui.components.dialogs.CustomDialog;
import com.gmail.grigorij.ui.components.layouts.FlexBoxLayout;
import com.gmail.grigorij.ui.utils.UIUtils;
import com.gmail.grigorij.ui.utils.css.LumoStyles;
import com.gmail.grigorij.ui.utils.css.size.Right;
import com.gmail.grigorij.utils.ProjectConstants;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.ListDataProvider;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EditableCompanyForm extends FormLayout {

	private EditableLocationForm locationForm = new EditableLocationForm();
	private EditablePersonForm personForm = new EditablePersonForm();

	private Binder<Company> binder;

	private Company tempCompany;
	private List<Location> locations = new ArrayList<>();
	private boolean isNew;

	// FORM ITEMS
	private TextField nameField;
	private TextField vatField;
	private Checkbox entityStatusCheckbox;
//	private ComboBox<Location> companyLocationsComboBox;
	private FlexBoxLayout locationsLayout;
	private TextArea additionalInfo;

	private Hr hr;


	public EditableCompanyForm() {
		constructFormItems();

		constructForm();

		constructBinder();
	}


	private void constructFormItems() {
		nameField = new TextField("Name");
		nameField.setRequired(true);

		vatField = new TextField("VAT");
		vatField.setRequired(true);

		ComboBox<Location>  companyLocationsComboBox = new ComboBox<>();
		companyLocationsComboBox.setLabel("Locations");
		companyLocationsComboBox.setPlaceholder("Select location to edit");
		companyLocationsComboBox.setItems(locations);
		companyLocationsComboBox.setItemLabelGenerator(Location::getName);
		companyLocationsComboBox.addValueChangeListener(e -> {
			if (e != null) {
				if (e.getValue() != null) {
					openLocationDialog(e.getValue());
					companyLocationsComboBox.setValue(null);
				}
			}
		});

		Button newLocationButton = UIUtils.createIconButton(VaadinIcon.FILE_ADD, ButtonVariant.LUMO_CONTRAST);
		UIUtils.setTooltip("Add New Location", newLocationButton);
		newLocationButton.addClickListener(e -> openLocationDialog(null));

		//LOCATION & NEW LOCATION BUTTON
		locationsLayout = new FlexBoxLayout();
		locationsLayout.addClassName(ProjectConstants.SPACE_BETWEEN_CONTAINER);
		locationsLayout.add(companyLocationsComboBox, newLocationButton);
		locationsLayout.setFlexGrow("1", companyLocationsComboBox);
		locationsLayout.setComponentMargin(companyLocationsComboBox, Right.S);

		setColspan(locationsLayout, 2);


		additionalInfo = new TextArea("Additional Info");
		additionalInfo.setMaxHeight("200px");

		entityStatusCheckbox = new Checkbox("Deleted");

		UIUtils.setColSpan(2, locationsLayout, locationForm, personForm, additionalInfo);
	}

	private void initDynamicFormItems() {
		locations = tempCompany.getLocations();
	}

	private void constructForm() {
		addClassNames(LumoStyles.Padding.Vertical.S);
		setResponsiveSteps(
				new FormLayout.ResponsiveStep("0", 1, FormLayout.ResponsiveStep.LabelsPosition.TOP),
				new FormLayout.ResponsiveStep(ProjectConstants.COL_2_MIN_WIDTH, 2, FormLayout.ResponsiveStep.LabelsPosition.TOP));

		add(nameField);
		add(vatField);
		add(locationsLayout);

		hr = new Hr();
		setColspan(hr, 2);
		add(hr);

		Label addressLabel = UIUtils.createH4Label("Address");
		setColspan(addressLabel, 2);
		add(addressLabel);

		add(locationForm);

		hr = new Hr();
		setColspan(hr, 2);
		add(hr);

		Label contactLabel = UIUtils.createH4Label("Contact Person");
		setColspan(contactLabel, 2);
		add(contactLabel);

		add(personForm);

		hr = new Hr();
		setColspan(hr, 2);
		add(hr);

		add(additionalInfo);
	}

	private void constructBinder() {
		binder = new Binder<>(Company.class);

		binder.forField(nameField)
				.asRequired("Name is required")
				.bind(Company::getName, Company::setName);
		binder.forField(vatField)
				.asRequired("VAT is required")
				.bind(Company::getVat, Company::setVat);
		binder.forField(entityStatusCheckbox)
				.bind(Company::isDeleted, Company::setDeleted);
		binder.forField(additionalInfo)
				.bind(Company::getAdditionalInfo, Company::setAdditionalInfo);
	}

	private void openLocationDialog(Location location) {

		boolean bNewLocation = (location == null);

		EditableLocationForm locationForm = new EditableLocationForm();
		locationForm.setLocation(location);

		String buttonText = (bNewLocation) ? "Add" : "Save";

		CustomDialog dialog = new CustomDialog();
		dialog.setHeader(UIUtils.createH3Label("Location Details"));

		dialog.setContent(locationForm);

		dialog.getCancelButton().addClickListener(e -> dialog.close());

		dialog.getConfirmButton().setText(buttonText);
		dialog.getConfirmButton().addClickListener(e -> {

			Location editedLocation = locationForm.getLocation();

			if (editedLocation != null) {
				if (bNewLocation) {
					tempCompany.addLocation(locationForm.getLocation());
					locations.add(editedLocation);
				}

				String notificationSuccess = (bNewLocation) ? "New location added successfully" : "Location edited successfully";
				String notificationFail = (bNewLocation) ? "New location add failed" : "Location edit fail";

				if (CompanyFacade.getInstance().update(tempCompany)) {
					UIUtils.showNotification(notificationSuccess, UIUtils.NotificationType.SUCCESS);
				} else {
					UIUtils.showNotification(notificationFail, UIUtils.NotificationType.ERROR);
				}
				dialog.close();
			}
		});
		dialog.open();

//		dialog.addDetachListener((DetachEvent event) -> companyLocationsComboBox.setValue(null));
	}


	public void setCompany(Company company) {
		isNew = false;

		if (company == null) {
			tempCompany = new Company();
			isNew = true;
		} else {
			tempCompany = new Company(company);
		}

		binder.readBean(tempCompany);

		locationForm.setLocation(tempCompany.getAddress());
		personForm.setPerson(tempCompany.getContactPerson());

		initDynamicFormItems();
	}

	public Company getCompany() {
		try {
			binder.validate();

			if (binder.isValid()) {

				Location location = locationForm.getLocation();

				if (location == null) {
					return null;
				} else {
					tempCompany.setAddress(location);
				}

				Person contactPerson = personForm.getPerson();

				if (contactPerson == null) {
					return null;
				} else {
					tempCompany.setContactPerson(contactPerson);
				}

				binder.writeBean(tempCompany);
				return tempCompany;
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
