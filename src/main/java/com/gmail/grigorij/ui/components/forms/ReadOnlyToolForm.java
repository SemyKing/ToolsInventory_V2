package com.gmail.grigorij.ui.components.forms;

import com.gmail.grigorij.backend.database.entities.Tool;
import com.gmail.grigorij.ui.components.FlexBoxLayout;
import com.gmail.grigorij.ui.utils.UIUtils;
import com.gmail.grigorij.ui.utils.css.size.Right;
import com.gmail.grigorij.utils.DateConverter;
import com.gmail.grigorij.utils.ProjectConstants;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ReadOnlyHasValue;

public class ReadOnlyToolForm extends FormLayout {

	private final String CLASS_NAME = "form";

	private Tool tool;
	private Binder<Tool> binder;

	// FORM ITEMS
	private TextField nameField;
	private TextField barcodeField;
	private TextField snField;
	private TextField toolInfoField;
	private TextField manufacturerField;
	private TextField modelField;
	private TextField currentLocationField;
	private TextField categoryField;
	private TextField usageStatusField;
	private Div toolUsersDiv;
	private TextField currentUserField;
	private TextField reservedByField;
	private Div datesDiv;
	private TextField dateBoughtField;
	private TextField dateNextMaintenanceField;
	private Div priceGuaranteeDiv;
	private TextField priceField;
	private TextField guaranteeField;
	private TextField additionalInfoField;

	private FlexBoxLayout reportsLayout;
	private TextField reportsField;
	private Button reportsButton;


	// BINDER ITEMS
	private ReadOnlyHasValue<Tool> name;
	private ReadOnlyHasValue<Tool> barcode;
	private ReadOnlyHasValue<Tool> sn;
	private ReadOnlyHasValue<Tool> toolInfo;
	private ReadOnlyHasValue<Tool> manufacturer;
	private ReadOnlyHasValue<Tool> model;
	private ReadOnlyHasValue<Tool> currentLocation;
	private ReadOnlyHasValue<Tool> category;
	private ReadOnlyHasValue<Tool> usageStatus;
	private ReadOnlyHasValue<Tool> currentUser;
	private ReadOnlyHasValue<Tool> reservedBy;
	private ReadOnlyHasValue<Tool> dateBought;
	private ReadOnlyHasValue<Tool> dateNextMaintenance;
	private ReadOnlyHasValue<Tool> price;
	private ReadOnlyHasValue<Tool> guarantee;
	private ReadOnlyHasValue<Tool> additionalInfo;


	public ReadOnlyToolForm() {
		addClassName(CLASS_NAME);

		constructFormItems();

		constructForm();

		constructBinder();
	}


	private void constructFormItems() {
		nameField = new TextField("Name");
		nameField.setReadOnly(true);
		name = new ReadOnlyHasValue<>(tool -> nameField.setValue( tool.getName() ));

		barcodeField = new TextField("Barcode");
		barcodeField.setReadOnly(true);
		barcode = new ReadOnlyHasValue<>(tool -> barcodeField.setValue( tool.getBarcode() ));

		snField = new TextField("SN");
		snField.setReadOnly(true);
		sn = new ReadOnlyHasValue<>(tool -> snField.setValue(tool.getSerialNumber()));

		toolInfoField = new TextField("Tool Info");
		toolInfoField.setReadOnly(true);
		toolInfo = new ReadOnlyHasValue<>(tool -> toolInfoField.setValue( tool.getToolInfo() ));

		manufacturerField = new TextField("Manufacturer");
		manufacturerField.setReadOnly(true);
		manufacturer = new ReadOnlyHasValue<>(tool -> manufacturerField.setValue( tool.getManufacturer() ));

		modelField = new TextField("Model");
		modelField.setReadOnly(true);
		model = new ReadOnlyHasValue<>(tool -> modelField.setValue( tool.getModel() ));


		currentLocationField = new TextField("Current Location");
		currentLocationField.setReadOnly(true);
		currentLocation = new ReadOnlyHasValue<>(tool -> {
			currentLocationField.setValue((tool.getCurrentLocation() == null) ? "" : tool.getCurrentLocation().getName());
		});

		setColspan(currentLocationField, 2);

		categoryField = new TextField("Category");
		categoryField.setReadOnly(true);
		category = new ReadOnlyHasValue<>(tool -> {
			categoryField.setValue(tool.getCategoryString());
		});

		usageStatusField = new TextField("Status");
		usageStatusField.setReadOnly(true);
		usageStatus = new ReadOnlyHasValue<>(tool -> {
			usageStatusField.setValue(tool.getUsageStatus().getName());
		});

		currentUserField = new TextField("Current User");
		currentUserField.setWidth("calc(50% - (0.5 * var(--vaadin-form-layout-column-spacing)))");
		currentUserField.setReadOnly(true);
		currentUser = new ReadOnlyHasValue<>(tool -> {
			currentUserField.setValue((tool.getCurrentUser() == null) ? "" : tool.getCurrentUser().getFullName());
		});

		reservedByField = new TextField("Reserved By");
		reservedByField.setWidth("calc(50% - (0.5 * var(--vaadin-form-layout-column-spacing)))");
		reservedByField.setReadOnly(true);
		reservedBy = new ReadOnlyHasValue<>(tool -> {
			reservedByField.setValue((tool.getReservedUser() == null) ? "" : tool.getReservedUser().getFullName());
		});

		toolUsersDiv = new Div();
		toolUsersDiv.addClassName(ProjectConstants.CONTAINER_SPACE_BETWEEN);
		toolUsersDiv.add(currentUserField, reservedByField);

		setColspan(toolUsersDiv, 2);


		dateBoughtField = new TextField("Bought");
		dateBoughtField.setWidth("calc(50% - (0.5 * var(--vaadin-form-layout-column-spacing)))");
		dateBoughtField.setReadOnly(true);
		dateBought = new ReadOnlyHasValue<>(tool -> {
			dateBoughtField.setValue(tool.getDateBought() == null ? "" : DateConverter.localDateToString(tool.getDateBought()));
		});

		dateNextMaintenanceField = new TextField("Next Maintenance");
		dateNextMaintenanceField.setWidth("calc(50% - (0.5 * var(--vaadin-form-layout-column-spacing)))");
		dateNextMaintenanceField.setReadOnly(true);
		dateNextMaintenance = new ReadOnlyHasValue<>(tool -> {
			dateNextMaintenanceField.setValue((tool.getDateBought() == null) ? "" : DateConverter.localDateToString(tool.getDateNextMaintenance()));
		});

		datesDiv = new Div();
		datesDiv.addClassName(ProjectConstants.CONTAINER_SPACE_BETWEEN);
		datesDiv.add(dateBoughtField, dateNextMaintenanceField);


		priceField = new TextField("Price");
		priceField.setWidth("calc(50% - (0.5 * var(--vaadin-form-layout-column-spacing)))");
		priceField.setSuffixComponent(new Span("€"));
		priceField.setReadOnly(true);
		price = new ReadOnlyHasValue<>(tool -> {
			priceField.setValue((tool.getPrice() == null) ? "" : String.valueOf(tool.getPrice()));
		});

		guaranteeField = new TextField("Guarantee");
		guaranteeField.setWidth("calc(50% - (0.5 * var(--vaadin-form-layout-column-spacing)))");
		guaranteeField.setSuffixComponent(new Span("Months"));
		guaranteeField.setReadOnly(true);
		guarantee = new ReadOnlyHasValue<>(tool ->{
			guaranteeField.setValue((tool.getGuarantee_months() == null) ? "" : String.valueOf(tool.getGuarantee_months()));
		});

		priceGuaranteeDiv = new Div();
		priceGuaranteeDiv.addClassName(ProjectConstants.CONTAINER_SPACE_BETWEEN);
		priceGuaranteeDiv.add(priceField, guaranteeField);


		additionalInfoField = new TextField("Additional Info");
		additionalInfoField.setReadOnly(true);
		additionalInfo = new ReadOnlyHasValue<>(tool -> additionalInfoField.setValue(tool.getAdditionalInfo()));

		setColspan(additionalInfoField, 2);


		reportsField = new TextField("Reports");
		reportsField.setReadOnly(true);

		reportsButton = UIUtils.createButton("Show Reports", ButtonVariant.LUMO_CONTRAST);
		reportsButton.addClickListener(e -> constructReportsDialog());

		//REPORTS LAYOUT
		reportsLayout = new FlexBoxLayout();
		reportsLayout.addClassName(ProjectConstants.CONTAINER_SPACE_BETWEEN);
		reportsLayout.add(reportsField, reportsButton);
		reportsLayout.setFlexGrow("1", reportsField);
		reportsLayout.setComponentMargin(reportsField, Right.S);

		setColspan(reportsLayout, 2);
	}

	private void constructReportsDialog() {
	}

	private void constructForm() {
		setResponsiveSteps(
				new FormLayout.ResponsiveStep("0", 1, FormLayout.ResponsiveStep.LabelsPosition.TOP),
				new FormLayout.ResponsiveStep(ProjectConstants.COL_2_MIN_WIDTH, 2, FormLayout.ResponsiveStep.LabelsPosition.TOP));
		add(nameField);
		add(barcodeField);
		add(snField);
		add(toolInfoField);
		add(manufacturerField);
		add(modelField);

		Hr hr = new Hr();
		setColspan(hr, 2);
		add(hr);

		add(currentLocationField);
		add(categoryField);
		add(usageStatusField);
		add(toolUsersDiv);

		hr = new Hr();
		setColspan(hr, 2);
		add(hr);

		add(datesDiv);
		add(priceGuaranteeDiv);
		add(additionalInfoField);
	}

	private void constructBinder() {
		binder = new Binder<>(Tool.class);
		binder.forField(name)
				.bind(tool -> tool, null);
		binder.forField(barcode)
				.bind(tool -> tool, null);
		binder.forField(sn)
				.bind(tool -> tool, null);
		binder.forField(toolInfo)
				.bind(tool -> tool, null);
		binder.forField(manufacturer)
				.bind(tool -> tool, null);
		binder.forField(model)
				.bind(tool -> tool, null);
		binder.forField(currentLocation)
				.bind(tool -> tool, null);
		binder.forField(category)
				.bind(tool -> tool, null);
		binder.forField(currentUser)
				.bind(tool -> tool, null);
		binder.forField(usageStatus)
				.bind(tool -> tool, null);
		binder.forField(reservedBy)
				.bind(tool -> tool, null);
		binder.forField(price)
				.bind(tool -> tool, null);
		binder.forField(guarantee)
				.bind(tool -> tool, null);
		binder.forField(dateBought)
				.bind(tool -> tool, null);
		binder.forField(dateNextMaintenance)
				.bind(tool -> tool, null);
		binder.forField(additionalInfo)
				.bind(tool -> tool, null);
	}


	private void initDynamicFormItems() {

	}


	public void setTool(Tool tool) {
		this.tool = tool;
		binder.readBean(tool);
		initDynamicFormItems();
	}
}
