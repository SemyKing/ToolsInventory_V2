package com.gmail.grigorij.ui.components.forms;

import com.gmail.grigorij.backend.database.entities.Company;
import com.gmail.grigorij.backend.database.entities.PDF_Template;
import com.gmail.grigorij.backend.database.entities.embeddable.Location;
import com.gmail.grigorij.backend.database.entities.embeddable.Person;
import com.gmail.grigorij.backend.database.enums.operations.Operation;
import com.gmail.grigorij.backend.database.enums.operations.OperationTarget;
import com.gmail.grigorij.backend.database.enums.permissions.PermissionLevel;
import com.gmail.grigorij.backend.database.enums.permissions.PermissionRange;
import com.gmail.grigorij.backend.database.facades.PDF_Facade;
import com.gmail.grigorij.backend.database.facades.PermissionFacade;
import com.gmail.grigorij.ui.components.FlexBoxLayout;
import com.gmail.grigorij.ui.components.dialogs.CustomDialog;
import com.gmail.grigorij.ui.components.dialogs.PDF_TemplateDialog;
import com.gmail.grigorij.ui.utils.UIUtils;
import com.gmail.grigorij.ui.utils.css.size.Right;
import com.gmail.grigorij.utils.AuthenticationService;
import com.gmail.grigorij.utils.ProjectConstants;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;

import java.util.ArrayList;
import java.util.List;

public class CompanyForm extends FormLayout {

	private final String CLASS_NAME = "form";

	private final LocationForm addressForm = new LocationForm();
	private final PersonForm contactPersonForm = new PersonForm();

	private PDF_TemplateDialog pdfTemplateDialog;

	private Binder<Company> binder;
	private Company company, originalCompany;
	private boolean isNew;

	private List<Location> tempLocations;
	private PDF_Template tempPDF_Template;

	// FORM ITEMS
	private Div entityStatusDiv;
	private Checkbox entityStatusCheckbox;
	private TextField nameField;
	private TextField vatField;
	private ComboBox<Location> companyLocationsComboBox;
	private FlexBoxLayout locationsLayout;
	private TextArea additionalInfo;

	private Div PDF_TemplateButtonDiv;
	private Button editPDF_TemplateButton;


	public CompanyForm() {
		addClassName(CLASS_NAME);

		constructFormItems();

		constructForm();

		constructBinder();
	}


	private void constructFormItems() {
		entityStatusCheckbox = new Checkbox("Deleted");

		entityStatusDiv = new Div();
		entityStatusDiv.addClassName(ProjectConstants.CONTAINER_ALIGN_CENTER);
		entityStatusDiv.add(entityStatusCheckbox);

		setColspan(entityStatusDiv, 2);

		if (AuthenticationService.getCurrentSessionUser().getPermissionLevel().lowerThan(PermissionLevel.SYSTEM_ADMIN)) {
			if (!PermissionFacade.getInstance().isUserAllowedTo(Operation.DELETE, OperationTarget.COMPANY, PermissionRange.COMPANY)) {
				entityStatusCheckbox.setReadOnly(true);
				entityStatusDiv.getElement().setAttribute("hidden", true);
			}
		}

		nameField = new TextField("Name");
		nameField.setRequired(true);

		vatField = new TextField("VAT");
		vatField.setRequired(true);

		companyLocationsComboBox = new ComboBox<>();
		companyLocationsComboBox.setLabel("Locations");
		companyLocationsComboBox.setPlaceholder("Select location to edit");
		companyLocationsComboBox.setItems();
		companyLocationsComboBox.setItemLabelGenerator(Location::getName);
		companyLocationsComboBox.addValueChangeListener(e -> {
			if (e.getValue() != null) {
				constructLocationDialog(e.getValue());
				companyLocationsComboBox.setValue(null);
			}
		});
		companyLocationsComboBox.setReadOnly(true);

		if (AuthenticationService.getCurrentSessionUser().getPermissionLevel().equalsTo(PermissionLevel.SYSTEM_ADMIN) ||
				PermissionFacade.getInstance().isUserAllowedTo(Operation.VIEW, OperationTarget.LOCATIONS, null)) {
			companyLocationsComboBox.setReadOnly(false);
		}


		Button newLocationButton = UIUtils.createIconButton(VaadinIcon.FILE_ADD, ButtonVariant.LUMO_PRIMARY);
		UIUtils.setTooltip("Add New Location", newLocationButton);
		newLocationButton.addClickListener(e -> constructLocationDialog(null));
		newLocationButton.setEnabled(false);

		if (AuthenticationService.getCurrentSessionUser().getPermissionLevel().equalsTo(PermissionLevel.SYSTEM_ADMIN) ||
				PermissionFacade.getInstance().isUserAllowedTo(Operation.ADD, OperationTarget.LOCATIONS, null)) {
			newLocationButton.setEnabled(true);
		}


		//LOCATION & NEW LOCATION BUTTON
		locationsLayout = new FlexBoxLayout();
		locationsLayout.addClassName(ProjectConstants.CONTAINER_SPACE_BETWEEN);
		locationsLayout.add(companyLocationsComboBox, newLocationButton);
		locationsLayout.setFlexGrow("1", companyLocationsComboBox);
		locationsLayout.setComponentMargin(companyLocationsComboBox, Right.S);

		setColspan(locationsLayout, 2);

		editPDF_TemplateButton = UIUtils.createButton("EDIT PDF TEMPLATE", VaadinIcon.CLIPBOARD_TEXT, ButtonVariant.LUMO_PRIMARY);
		editPDF_TemplateButton.addClickListener(e -> {
			constructPDF_TemplateDialog();
		});

		PDF_TemplateButtonDiv = new Div();
		PDF_TemplateButtonDiv.addClassName(ProjectConstants.CONTAINER_ALIGN_CENTER);
		PDF_TemplateButtonDiv.add(editPDF_TemplateButton);

		setColspan(PDF_TemplateButtonDiv, 2);

		additionalInfo = new TextArea("Additional Info");
		additionalInfo.setMaxHeight("200px");

		setColspan(additionalInfo, 2);
		setColspan(addressForm, 2);
		setColspan(contactPersonForm, 2);
	}

	private void constructForm() {
		setResponsiveSteps(
				new FormLayout.ResponsiveStep("0", 1, FormLayout.ResponsiveStep.LabelsPosition.TOP),
				new FormLayout.ResponsiveStep(ProjectConstants.COL_2_MIN_WIDTH, 2, FormLayout.ResponsiveStep.LabelsPosition.TOP));

		add(entityStatusDiv);
		add(nameField);
		add(vatField);
		add(locationsLayout);
		add(PDF_TemplateButtonDiv);

		Hr hr = new Hr();
		setColspan(hr, 2);
		add(hr);

		Label addressLabel = UIUtils.createH4Label("Address");
		setColspan(addressLabel, 2);
		add(addressLabel);

		add(addressForm);

		hr = new Hr();
		setColspan(hr, 2);
		add(hr);

		Label contactLabel = UIUtils.createH4Label("Contact Person");
		setColspan(contactLabel, 2);
		add(contactLabel);

		add(contactPersonForm);

		hr = new Hr();
		setColspan(hr, 2);
		add(hr);

		add(additionalInfo);
	}

	private void constructBinder() {
		binder = new Binder<>(Company.class);

		binder.forField(entityStatusCheckbox)
				.bind(Company::isDeleted, Company::setDeleted);

		binder.forField(nameField)
				.asRequired("Name is required")
				.bind(Company::getName, Company::setName);

		binder.forField(vatField)
				.bind(Company::getVat, Company::setVat);

		binder.forField(additionalInfo)
				.bind(Company::getAdditionalInfo, Company::setAdditionalInfo);
	}


	private void initDynamicFormItems() {
		tempLocations = new ArrayList<>();
		for (Location location : company.getLocations()) {
			tempLocations.add(new Location(location));
		}
		companyLocationsComboBox.setItems(tempLocations);

		if (isNew) {
			tempPDF_Template = new PDF_Template();
		} else {
//			tempPDF_Template = new PDF_Template(PDF_Facade.getInstance().getPDF_TemplateByCompany(company.getId()));
			tempPDF_Template = PDF_Facade.getInstance().getPDF_TemplateByCompany(company.getId());
		}

		try {
			editPDF_TemplateButton.setEnabled(false);
			PDF_TemplateButtonDiv.getElement().setAttribute("hidden", true);
		} catch (Exception ignored) {}

		if (AuthenticationService.getCurrentSessionUser().getPermissionLevel().equalsTo(PermissionLevel.SYSTEM_ADMIN)) {
			editPDF_TemplateButton.setEnabled(true);
			PDF_TemplateButtonDiv.getElement().setAttribute("hidden", false);
		}
	}

	private void constructLocationDialog(Location location) {
		LocationForm locationForm = new LocationForm();
		locationForm.setLocation(location);

		CustomDialog dialog = new CustomDialog();
		dialog.setHeader(UIUtils.createH3Label("Location Details"));

		dialog.setContent(locationForm);

		dialog.getCancelButton().addClickListener(e -> dialog.close());

		dialog.getConfirmButton().setText("Save");
		dialog.getConfirmButton().setEnabled(false);

		if (AuthenticationService.getCurrentSessionUser().getPermissionLevel().equalsTo(PermissionLevel.SYSTEM_ADMIN) ||
				PermissionFacade.getInstance().isUserAllowedTo(Operation.EDIT, OperationTarget.LOCATIONS, null)) {

			dialog.getConfirmButton().setEnabled(true);
			dialog.getConfirmButton().addClickListener(e -> {

				Location editedLocation = locationForm.getLocation();

				if (editedLocation != null) {
					if (locationForm.isNew()) {
						tempLocations.add(editedLocation);
					}
					companyLocationsComboBox.setItems(tempLocations);

					dialog.close();
				}
			});
		}

		dialog.open();
	}

	private void constructPDF_TemplateDialog() {
		pdfTemplateDialog = new PDF_TemplateDialog(tempPDF_Template);
		pdfTemplateDialog.setHeader(UIUtils.createH3Label("PDF Template for Reporting"));
		pdfTemplateDialog.constructView();

		pdfTemplateDialog.getConfirmButton().addClickListener(e -> {
			PDF_Template pdfTemplate = pdfTemplateDialog.getTemplate();

			if (pdfTemplate != null) {
				tempPDF_Template = pdfTemplate;


				pdfTemplateDialog.close();
			}
		});

		pdfTemplateDialog.open();
	}


	public void setCompany(Company company) {
		isNew = false;

		if (company == null) {
			this.company = new Company();
			isNew = true;
		} else {
			this.company = company;
		}

		initDynamicFormItems();

		originalCompany = new Company(this.company);

		addressForm.setLocation(this.company.getAddress());
		contactPersonForm.setPerson(this.company.getContactPerson());

		binder.readBean(this.company);
	}

	public Company getCompany() {
		try {
			binder.validate();

			if (binder.isValid()) {

				Location address = addressForm.getLocation();

				if (address == null) {
					return null;
				} else {
					company.setAddress(address);
				}

				Person contactPerson = contactPersonForm.getPerson();

				if (contactPerson == null) {
					return null;
				} else {
					company.setContactPerson(contactPerson);
				}

				company.setLocations(tempLocations);

				if (!PDF_Facade.getInstance().update(tempPDF_Template)) {
					UIUtils.showNotification(isNew ? "PDF Template insert error" : "PDF Template update error", NotificationVariant.LUMO_ERROR);
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


	public List<String> getChanges() {
		List<String> changes = new ArrayList<>();

		if (Boolean.compare(originalCompany.isDeleted(), company.isDeleted()) != 0) {
			changes.add("Status changed from: '" + UIUtils.entityStatusToString(originalCompany.isDeleted()) + "', to: '" + UIUtils.entityStatusToString(company.isDeleted()) + "'");
		}
		if (!originalCompany.getName().equals(company.getName())) {
			changes.add("Name changed from: '" + originalCompany.getName() + "', to: '" + company.getName() + "'");
		}
		if (!originalCompany.getVat().equals(company.getVat())) {
			changes.add("VAT changed from: '" + originalCompany.getVat() + "', to: '" + company.getVat() + "'");
		}

		List<String> otherChanges = addressForm.getChanges();
		if (otherChanges.size() > 0) {
			changes.add("Address changed");
			changes.addAll(otherChanges);
		}

		otherChanges = contactPersonForm.getChanges();
		if (otherChanges.size() > 0) {
			changes.add("Contact Person changed");
			changes.addAll(otherChanges);
		}

		if (pdfTemplateDialog != null) {
			otherChanges = pdfTemplateDialog.getChanges();
			if (otherChanges.size() > 0) {
				changes.add("Reporting Template changed");
				changes.addAll(otherChanges);
			}
		}

		return changes;
	}

	public boolean isNew() {
		return isNew;
	}
}
