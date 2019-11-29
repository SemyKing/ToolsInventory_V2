package com.gmail.grigorij.ui.components.forms;

import com.gmail.grigorij.backend.database.entities.Company;
import com.gmail.grigorij.backend.database.entities.PDF_Template;
import com.gmail.grigorij.backend.database.entities.embeddable.Location;
import com.gmail.grigorij.backend.database.entities.embeddable.Person;
import com.gmail.grigorij.backend.database.enums.operations.Operation;
import com.gmail.grigorij.backend.database.enums.operations.OperationTarget;
import com.gmail.grigorij.backend.database.enums.permissions.PermissionLevel;
import com.gmail.grigorij.backend.database.enums.permissions.PermissionRange;
import com.gmail.grigorij.backend.database.facades.PermissionFacade;
import com.gmail.grigorij.ui.components.FlexBoxLayout;
import com.gmail.grigorij.ui.components.dialogs.ConfirmDialog;
import com.gmail.grigorij.ui.components.dialogs.CustomDialog;
import com.gmail.grigorij.ui.components.dialogs.pdf.PDF_TemplateView;
import com.gmail.grigorij.ui.utils.UIUtils;
import com.gmail.grigorij.ui.utils.css.size.Right;
import com.gmail.grigorij.utils.authentication.AuthenticationService;
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

	private PDF_TemplateView pdf_templateView;
	private PDF_Template pdf_template;

	private Binder<Company> binder;
	private Company company, originalCompany;
	private boolean isNew;

	private List<Location> tempLocations;

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

		setColspan(entityStatusDiv, 2);

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

		if (PermissionFacade.getInstance().isSystemAdminOrAllowedTo(Operation.VIEW, OperationTarget.LOCATIONS, PermissionRange.OWN)) {
			companyLocationsComboBox.setReadOnly(false);
		}


		Button newLocationButton = UIUtils.createIconButton(VaadinIcon.FILE_ADD, ButtonVariant.LUMO_PRIMARY);
		UIUtils.setTooltip("Add New Location", newLocationButton);
		newLocationButton.addClickListener(e -> constructLocationDialog(null));
		newLocationButton.setEnabled(false);

		if (PermissionFacade.getInstance().isSystemAdminOrAllowedTo(Operation.ADD, OperationTarget.LOCATIONS, PermissionRange.OWN)) {
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
		editPDF_TemplateButton.addClickListener(e -> constructPDF_TemplateDialog());

		PDF_TemplateButtonDiv = new Div();
		PDF_TemplateButtonDiv.addClassName(ProjectConstants.CONTAINER_ALIGN_CENTER);

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
		pdf_template = null;

		try {
			entityStatusDiv.remove(entityStatusCheckbox);
			PDF_TemplateButtonDiv.remove(editPDF_TemplateButton);
		} catch (Exception ignored) {}

		if (PermissionFacade.getInstance().isSystemAdminOrAllowedTo(Operation.DELETE, OperationTarget.COMPANY, PermissionRange.COMPANY)) {
			entityStatusDiv.add(entityStatusCheckbox);
		}

		if (AuthenticationService.getCurrentSessionUser().getPermissionLevel().equalsTo(PermissionLevel.SYSTEM_ADMIN)) {
			PDF_TemplateButtonDiv.add(editPDF_TemplateButton);
		}


		tempLocations = new ArrayList<>();
		for (Location location : company.getLocations()) {
			tempLocations.add(new Location(location));
		}
		companyLocationsComboBox.setItems(tempLocations);
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

		if (PermissionFacade.getInstance().isSystemAdminOrAllowedTo(Operation.EDIT, OperationTarget.LOCATIONS, PermissionRange.OWN)) {
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

		pdf_templateView = new PDF_TemplateView(company.getPdf_template());

		CustomDialog dialog = new CustomDialog();

		dialog.setCloseOnOutsideClick(false);
		dialog.setCloseOnEsc(false);

		dialog.setHeader(UIUtils.createH3Label("PDF Template"));
		dialog.setContent(pdf_templateView);

		dialog.getConfirmButton().setText("Save");
		dialog.getConfirmButton().addClickListener(saveOnClick -> {
			PDF_Template pdf_template = pdf_templateView.getTemplate();
			if (pdf_template != null) {
				this.pdf_template = pdf_template;
				dialog.close();
			}
		});

		dialog.getCancelButton().addClickListener(cancelEditOnClick -> {
			ConfirmDialog confirmDialog = new ConfirmDialog();
			confirmDialog.setMessage("Are you sure you want to cancel?" + ProjectConstants.NEW_LINE + "All changes will be lost");
			confirmDialog.closeOnCancel();
			confirmDialog.getConfirmButton().addClickListener(confirmOnClick -> {
				pdf_templateView.setChanges(null);
				confirmDialog.close();
				dialog.close();
			});
			confirmDialog.open();
		});

		dialog.open();
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

				if (pdf_template != null) {
					company.setPdf_template(pdf_template);
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
			changes.add("Status changed from: '" + UIUtils.entityStatusToString(originalCompany.isDeleted()) + "',  to:  '" + UIUtils.entityStatusToString(company.isDeleted()) + "'");
		}
		if (!originalCompany.getName().equals(company.getName())) {
			changes.add("Name changed from: '" + originalCompany.getName() + "',  to:  '" + company.getName() + "'");
		}
		if (!originalCompany.getVat().equals(company.getVat())) {
			changes.add("VAT changed from: '" + originalCompany.getVat() + "',  to:  '" + company.getVat() + "'");
		}
		if (!originalCompany.getAdditionalInfo().equals(company.getAdditionalInfo())) {
			changes.add("Additional Info changed from: '" + originalCompany.getAdditionalInfo() + "',  to:  '" + company.getAdditionalInfo() + "'");
		}

		List<String> otherChanges = addressForm.getChanges();
		if (otherChanges.size() > 0) {
			changes.add("-- Address changed");
			changes.addAll(otherChanges);
		}

		otherChanges = contactPersonForm.getChanges();
		if (otherChanges.size() > 0) {
			changes.add("-- Contact Person changed");
			changes.addAll(otherChanges);
		}

		if (pdf_template != null) {
			otherChanges = pdf_templateView.getChanges();
			if (otherChanges != null) {
				if (otherChanges.size() > 0) {
					changes.add("-- PDF Template changed");
					changes.addAll(otherChanges);
				}
			}
		}

		return changes;
	}

	public boolean isNew() {
		return isNew;
	}
}
