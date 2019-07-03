package com.gmail.grigorij.ui.utils.forms.admin;

import com.gmail.grigorij.backend.database.facades.CompanyFacade;
import com.gmail.grigorij.backend.entities.company.Company;
import com.gmail.grigorij.backend.entities.location.Location;
import com.gmail.grigorij.ui.utils.components.ConfirmDialog;
import com.gmail.grigorij.ui.utils.components.CustomDialog;
import com.gmail.grigorij.ui.utils.components.Divider;
import com.gmail.grigorij.ui.utils.UIUtils;
import com.gmail.grigorij.ui.utils.components.FlexBoxLayout;
import com.gmail.grigorij.ui.utils.css.FlexDirection;
import com.gmail.grigorij.ui.utils.css.LumoStyles;
import com.gmail.grigorij.ui.utils.css.size.Horizontal;
import com.gmail.grigorij.ui.utils.css.size.Left;
import com.gmail.grigorij.ui.utils.css.size.Vertical;
import com.gmail.grigorij.ui.utils.forms.LocationForm;
import com.gmail.grigorij.ui.utils.forms.PersonForm;
import com.gmail.grigorij.utils.ProjectConstants;
import com.gmail.grigorij.utils.converters.CustomConverter;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.ListDataProvider;

public class AdminCompanyForm extends FormLayout {

	private LocationForm locationForm = new LocationForm();
	private PersonForm<Company> personForm = new PersonForm<>();

	private Binder<Company> companyBinder = new Binder<>(Company.class);

	private Company company;
	private boolean isNew;

	private ComboBox<Location> companyLocationsComboBox;
	private ListDataProvider<Location> provider;

	private TextField companyName;

	public AdminCompanyForm() {
		companyName = new TextField("Name");
		companyName.setRequired(true);

		TextField companyVAT = new TextField("VAT");
		companyVAT.setWidth(ProjectConstants.FORM_HALF_WIDTH);
		companyVAT.setRequired(true);

		Select<String> companyStatus = new Select<>(ProjectConstants.ACTIVE, ProjectConstants.INACTIVE);
		companyStatus.setLabel("Status");
		companyStatus.setWidth(ProjectConstants.FORM_HALF_WIDTH);

		FlexBoxLayout vatAndStatusLayout = new FlexBoxLayout();
		vatAndStatusLayout.setFlexDirection(FlexDirection.ROW);
		vatAndStatusLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
		vatAndStatusLayout.add(companyVAT, companyStatus);

		Label addressLabel = UIUtils.createH5Label("Address");

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

		Button newLocationButton = UIUtils.createButton(VaadinIcon.PLUS);
		UIUtils.setTooltip("Add new Location", newLocationButton);
		newLocationButton.addClickListener(e -> openLocationDialog(null));

		FlexBoxLayout locationsLayout = new FlexBoxLayout();
		locationsLayout.setWidth("100%");
		locationsLayout.setFlexDirection(FlexDirection.ROW);
		locationsLayout.add(companyLocationsComboBox, newLocationButton);
		locationsLayout.setComponentMargin(newLocationButton, Left.S);
		locationsLayout.setFlexGrow("1", companyLocationsComboBox);
		locationsLayout.setAlignItems(FlexComponent.Alignment.BASELINE);

		Label contactPersonLabel = UIUtils.createH5Label("Contact Person");

		TextArea additionalInfo = new TextArea("Additional Info");
		additionalInfo.setWidth("100%");
		additionalInfo.setMaxHeight("200px");

		Divider divider1 = new Divider(Horizontal.NONE, Vertical.S);
		Divider divider2 = new Divider(Horizontal.NONE, Vertical.S);
		Divider divider3 = new Divider(Horizontal.NONE, Vertical.S);

		UIUtils.setColSpan(2, divider1, divider2, divider3, locationForm, personForm, locationsLayout, additionalInfo);

		// Form layout
		addClassNames(LumoStyles.Padding.Bottom.M, LumoStyles.Padding.Horizontal.M, LumoStyles.Padding.Top.XS);
		setResponsiveSteps(
				new FormLayout.ResponsiveStep("0", 1, FormLayout.ResponsiveStep.LabelsPosition.TOP),
				new FormLayout.ResponsiveStep(ProjectConstants.COL_2_MIN_WIDTH, 2, FormLayout.ResponsiveStep.LabelsPosition.TOP));

		add(companyName);
		add(vatAndStatusLayout);
		add(locationsLayout);
		add(divider1);//colspan 2
		add(addressLabel);
		add(locationForm);
		add(divider2);//colspan 2
		add(contactPersonLabel);
		add(personForm);
		add(divider3);//colspan 2
		add(additionalInfo);

		companyBinder.forField(companyName)
				.asRequired("Company name is required")
				.bind(Company::getName, Company::setName);
		companyBinder.forField(companyVAT)
				.asRequired("Company VAT is required")
				.bind(Company::getVat, Company::setVat);
		companyBinder.forField(companyStatus)
				.asRequired("Select one")
				.withConverter(new CustomConverter.StatusConverter())
				.bind(Company::isDeleted, Company::setDeleted);

		companyBinder.forField(additionalInfo)
				.bind(Company::getAdditionalInfo, Company::setAdditionalInfo);
	}

	public void setCompany(Company c) {
		companyBinder.removeBean();
		isNew = false;
		company = c;

		if (company == null) {
			company = Company.getEmptyCompany();
			isNew = true;
			companyName.focus();
		}

		companyBinder.readBean(company);
		locationForm.setLocation(company.getAddress());
		personForm.setPerson(company);

		provider = DataProvider.ofCollection(company.getLocations());

		companyLocationsComboBox.setDataProvider(provider);
		companyLocationsComboBox.setPlaceholder( (company.getLocations().size() <= 0) ? "No locations found" : "Select location to edit" );
	}

	public Company getCompany() {
		try {
			companyBinder.validate();
			if (companyBinder.isValid()) {
				companyBinder.writeBean(company);

				Location companyAddress = locationForm.getLocation();
				if (companyAddress != null) {
					company.setAddress(companyAddress);
				}

				Object personInfo = personForm.getPerson();

				if (personInfo != null) {
					if (personInfo instanceof Company) {
						company.setFirstName(((Company) personInfo).getFirstName());
						company.setLastName(((Company) personInfo).getLastName());
						company.setPhoneNumber(((Company) personInfo).getPhoneNumber());
						company.setEmail(((Company) personInfo).getEmail());
					}
				}

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

		LocationForm locationForm = new LocationForm();
		locationForm.setLocation(location);

		String header = (bNewLocation) ? "New Location" : "Edit Location";
		String buttonText = (bNewLocation) ? "Add" : "Save";

		CustomDialog dialog = new CustomDialog();
		dialog.setHeader(UIUtils.createH4Label(header));

		/*
		Allow and handle location delete
		 */
		if (!bNewLocation) {
			dialog.setDeleteButtonVisible(true);

			dialog.getDeleteButton().addClickListener(deleteEvent -> {

				ConfirmDialog confirmDialog = new ConfirmDialog(ConfirmDialog.Type.DELETE, "location", location.getName());
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

		dialog.getCancelButton().addClickListener(e -> dialog.close());
		dialog.setConfirmButton(UIUtils.createButton(buttonText, ButtonVariant.LUMO_SUCCESS, ButtonVariant.LUMO_TERTIARY));

		dialog.setContent(locationForm);

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
