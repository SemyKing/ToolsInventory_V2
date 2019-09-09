package com.gmail.grigorij.ui.forms.editable;

import com.gmail.grigorij.backend.database.facades.CompanyFacade;
import com.gmail.grigorij.backend.entities.company.Company;
import com.gmail.grigorij.backend.embeddable.Location;
import com.gmail.grigorij.backend.embeddable.Person;
import com.gmail.grigorij.ui.components.dialogs.ConfirmDialog;
import com.gmail.grigorij.ui.components.dialogs.CustomDialog;
import com.gmail.grigorij.ui.utils.UIUtils;
import com.gmail.grigorij.ui.components.Divider;
import com.gmail.grigorij.ui.components.FlexBoxLayout;
import com.gmail.grigorij.ui.utils.css.LumoStyles;
import com.gmail.grigorij.ui.utils.css.size.Horizontal;
import com.gmail.grigorij.ui.utils.css.size.Vertical;
import com.gmail.grigorij.utils.ProjectConstants;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.converter.StringToBooleanConverter;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.ListDataProvider;

public class EditableCompanyForm extends FormLayout {

	private EditableLocationForm locationForm = new EditableLocationForm();
	private EditablePersonForm personForm = new EditablePersonForm();

	private Binder<Company> binder = new Binder<>(Company.class);

	private Company company;
	private boolean isNew;

	private ComboBox<Location> companyLocationsComboBox;
	private ListDataProvider<Location> provider;


	public EditableCompanyForm() {

		TextField nameField = new TextField("Name");
		nameField.setRequired(true);

		TextField vatField = new TextField("VAT");
		vatField.setWidth(ProjectConstants.FORM_HALF_WIDTH);
		vatField.setRequired(true);

		Select<String> status = new Select<>(ProjectConstants.ACTIVE, ProjectConstants.INACTIVE);
		status.setLabel("Status");
		status.setWidth(ProjectConstants.FORM_HALF_WIDTH);

		//NAME & STATUS
		FlexBoxLayout vatStatusLayout = UIUtils.getFormRowLayout(vatField, status, true);

		companyLocationsComboBox = new ComboBox<>();
		companyLocationsComboBox.setLabel("Locations");
		companyLocationsComboBox.setItemLabelGenerator(Location::getName);
		companyLocationsComboBox.addValueChangeListener(e -> {
			if (e != null) {
				if (e.getValue() != null) {
					openLocationDialog(e.getValue());
				}
			}
		});

		Button newLocationButton = UIUtils.createIconButton(VaadinIcon.FILE_ADD, ButtonVariant.LUMO_CONTRAST);
		UIUtils.setTooltip("Add New Location", newLocationButton);
		newLocationButton.addClickListener(e -> openLocationDialog(null));

		//LOCATION & NEW LOCATION BUTTON
		FlexBoxLayout locationsLayout = UIUtils.getFormRowLayout(companyLocationsComboBox, newLocationButton, false);

		TextArea additionalInfo = new TextArea("Additional Info");
		additionalInfo.setMaxHeight("200px");

//		UIUtils.setColSpan(2, locationForm, personForm, locationsLayout, additionalInfo);
		UIUtils.setColSpan(2, locationsLayout, locationForm, personForm, additionalInfo);

		// Form layout
		addClassNames(LumoStyles.Padding.Bottom.S, LumoStyles.Padding.Top.S);
		setResponsiveSteps(
				new FormLayout.ResponsiveStep("0", 1, FormLayout.ResponsiveStep.LabelsPosition.TOP),
				new FormLayout.ResponsiveStep(ProjectConstants.COL_2_MIN_WIDTH, 2, FormLayout.ResponsiveStep.LabelsPosition.TOP));

		add(nameField);
		add(vatStatusLayout);
		add(locationsLayout);
		add(new Divider(2, Horizontal.NONE, Vertical.S));
		add(UIUtils.createH4Label("Address"));
		add(locationForm);
		add(new Divider(2, Horizontal.NONE, Vertical.S));
		add(UIUtils.createH4Label("Contact Person"));
		add(personForm);
		add(new Divider(2, Horizontal.NONE, Vertical.S));
		add(additionalInfo);

		binder.forField(nameField)
				.asRequired("Name is required")
				.bind(Company::getName, Company::setName);
		binder.forField(vatField)
				.asRequired("VAT is required")
				.bind(Company::getVat, Company::setVat);
		binder.forField(status)
				.asRequired("Select one")
//				.withConverter(new CustomConverter.StatusConverter())
				.withConverter(new StringToBooleanConverter("Error", ProjectConstants.INACTIVE, ProjectConstants.ACTIVE))
				.bind(Company::isDeleted, Company::setDeleted);
		binder.forField(additionalInfo)
				.bind(Company::getAdditionalInfo, Company::setAdditionalInfo);
	}

	public void setCompany(Company c) {
		binder.removeBean();
		isNew = false;
		company = c;

		if (company == null) {
//			company = Company.getEmptyCompany();
			company = new Company();
			isNew = true;
		}

		binder.readBean(company);
		locationForm.setLocation(company.getAddress());
		personForm.setPerson(company.getContactPerson());

		provider = DataProvider.ofCollection(company.getLocations());

		companyLocationsComboBox.setDataProvider(provider);
		companyLocationsComboBox.setPlaceholder( (company.getLocations().size() <= 0) ? "No locations found" : "Select location to edit" );
	}

	public Company getCompany() {
		try {
			binder.validate();
			if (binder.isValid()) {

				Location location = locationForm.getLocation();

				if (location == null) {
					return null;
				} else {
					company.setAddress(location);
				}

				Person contactPerson = personForm.getPerson();

				if (contactPerson == null) {
					return null;
				} else {
					company.setContactPerson(contactPerson);
				}

				binder.writeBean(company);
				return company;
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


	private void openLocationDialog(Location location) {

		boolean bNewLocation = (location == null);

		EditableLocationForm locationForm = new EditableLocationForm();
		locationForm.setLocation(location);

		String header = (bNewLocation) ? "New Location" : "Edit Location";
		String buttonText = (bNewLocation) ? "Add" : "Save";

		CustomDialog dialog = new CustomDialog();
		dialog.setHeader(UIUtils.createH3Label(header));

		/*
		If location is not NULL, allow to delete itS
		 */
		if (!bNewLocation) {
			dialog.setDeleteButtonVisible(true);
			dialog.getDeleteButton().addClickListener(deleteEvent -> {

				ConfirmDialog confirmDialog = new ConfirmDialog(ConfirmDialog.Type.DELETE, "selected location", location.getName());
				confirmDialog.closeOnCancel();
				confirmDialog.getConfirmButton().addClickListener(confirmDeleteEvent -> {
					try {
						company.removeLocation(location);
						provider.getItems().remove(location);
						provider.refreshAll();

						if (CompanyFacade.getInstance().update(company)) {
							UIUtils.showNotification("Location deleted successfully", UIUtils.NotificationType.SUCCESS);
						} else {
							UIUtils.showNotification("Location delete failed", UIUtils.NotificationType.ERROR);
						}
						confirmDialog.close();
						dialog.close();

					} catch (Exception ex) {
						System.err.println("Error deleting location: " + location.getName() +", from company: " + company.getName());
						UIUtils.showNotification("Location delete failed", UIUtils.NotificationType.ERROR);
						ex.printStackTrace();
					}
				});
				confirmDialog.open();
			});
		}


		dialog.setContent(locationForm);

		dialog.getCancelButton().addClickListener(e -> dialog.close());

		dialog.getConfirmButton().setText(buttonText);
		dialog.getConfirmButton().addClickListener(e -> {

			if (locationForm.getLocation() != null) {
				if (bNewLocation) {
					company.addLocation(locationForm.getLocation());
					provider.getItems().add(locationForm.getLocation());
					provider.refreshAll();
				}

				String notificationSuccess = (bNewLocation) ? "New location added successfully" : "Location edited successfully";
				String notificationFail = (bNewLocation) ? "New location add failed" : "Location edit fail";

				if (CompanyFacade.getInstance().update(company)) {
					UIUtils.showNotification(notificationSuccess, UIUtils.NotificationType.SUCCESS);
				} else {
					UIUtils.showNotification(notificationFail, UIUtils.NotificationType.ERROR);
				}
				dialog.close();
			}
		});
		dialog.open();

		dialog.addDetachListener((DetachEvent event) -> companyLocationsComboBox.setValue(null));
	}
}
