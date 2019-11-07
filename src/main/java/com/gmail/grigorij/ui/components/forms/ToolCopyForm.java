package com.gmail.grigorij.ui.components.forms;

import com.gmail.grigorij.backend.database.entities.Tool;
import com.gmail.grigorij.utils.ProjectConstants;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.textfield.NumberField;


public class ToolCopyForm extends FormLayout {

	private final String CLASS_NAME = "form";

	private Tool tool;
	private int numberOfCopies = 1;


	//FORM ITEMS
	private Checkbox allCheckBox;
	private NumberField numberOfCopiesField;
	private Checkbox name;
	private Checkbox barcode;
	private Checkbox sn;
	private Checkbox manufacturer;
	private Checkbox model;
	private Checkbox info;
	private Checkbox company;
	private Checkbox category;
	private Checkbox usageStatus;
	private Checkbox toolUser;
	private Checkbox toolReservedBy;
	private Checkbox boughtDate;
	private Checkbox nextMaintenanceDate;
	private Checkbox price;
	private Checkbox guarantee;
	private Checkbox additionalInfo;

	public ToolCopyForm() {
		addClassName(CLASS_NAME);

		constructFormItems();

		constructForm();

		constructBinder();
	}


	private void constructFormItems() {
		allCheckBox = new Checkbox("All");
		allCheckBox.addValueChangeListener(e -> {
			if (e != null) {
				if (e.getValue() != null) {
					setAll(e.getValue());
				}
			}
		});

		numberOfCopiesField = new NumberField("Number of copies");
		numberOfCopiesField.setWidth("120px");
		numberOfCopiesField.setMin(1);
		numberOfCopiesField.setMax(100);
		numberOfCopiesField.setHasControls(true);
		numberOfCopiesField.setStep(1);
		numberOfCopiesField.setValue(1D);

		name = new Checkbox("Name");
		barcode = new Checkbox("Barcode");
		sn = new Checkbox("SN");
		info = new Checkbox("Tool Info");
		manufacturer = new Checkbox("Manufacturer");
		model = new Checkbox("Model");
		company = new Checkbox("Company");
		category = new Checkbox("Category");
		usageStatus = new Checkbox("Usage Status");
		toolUser = new Checkbox("Current User");
		toolReservedBy = new Checkbox("Reserved By User");
		boughtDate = new Checkbox("Bought Date");
		nextMaintenanceDate = new Checkbox("Next Maintenance Date");
		price = new Checkbox("Price");
		guarantee = new Checkbox("Guarantee");
		additionalInfo = new Checkbox("Additional Info");
	}

	private void constructForm() {
//		addClassNames(LumoStyles.Padding.Bottom.S, LumoStyles.Padding.Top.S);
		setResponsiveSteps(
				new FormLayout.ResponsiveStep("0", 1, FormLayout.ResponsiveStep.LabelsPosition.TOP),
				new FormLayout.ResponsiveStep(ProjectConstants.COL_2_MIN_WIDTH, 2, FormLayout.ResponsiveStep.LabelsPosition.TOP));

		add(allCheckBox);
		add(numberOfCopiesField);

		Hr hr = new Hr();
		setColspan(hr, 2);
		add(hr);

		add(name);
		add(barcode);
		add(sn);
		add(info);
		add(manufacturer);
		add(model);

		hr = new Hr();
		setColspan(hr, 2);
		add(hr);

		add(company);
		add(category);
		add(usageStatus);
		add(toolUser);
		add(toolReservedBy);
		add(boughtDate);
		add(nextMaintenanceDate);
		add(price);
		add(guarantee);
		add(additionalInfo);
	}

	private void constructBinder() {}


	private void setAll(boolean b) {
		name.setValue(b);
		barcode.setValue(b);
		sn.setValue(b);
		manufacturer.setValue(b);
		model.setValue(b);
		info.setValue(b);
		company.setValue(b);
		category.setValue(b);
		usageStatus.setValue(b);
		toolUser.setValue(b);
		toolReservedBy.setValue(b);
		boughtDate.setValue(b);
		nextMaintenanceDate.setValue(b);
		price.setValue(b);
		guarantee.setValue(b);
		additionalInfo.setValue(b);
	}


	public void setTool(Tool tool) {
		if (tool == null) {
			System.err.println("Tool copy, original Tool cannot be NULL");
			return;
		}

		this.tool = tool;

		allCheckBox.setValue(true);
	}

	public Tool getToolCopy() {
		if (numberOfCopiesField.isInvalid()) {
			return null;
		}

		if (numberOfCopiesField.getValue() != null) {
			this.numberOfCopies = numberOfCopiesField.getValue().intValue();
		}

		Tool toolCopy = new Tool();

		if (name.getValue()) {
			toolCopy.setName(tool.getName());
		}
		if (barcode.getValue()) {
			toolCopy.setBarcode(tool.getBarcode());
		}
		if (sn.getValue()) {
			toolCopy.setSerialNumber(tool.getSerialNumber());
		}
		if (manufacturer.getValue()) {
			toolCopy.setManufacturer(tool.getManufacturer());
		}
		if (model.getValue()) {
			toolCopy.setModel(tool.getModel());
		}
		if (info.getValue()) {
			toolCopy.setToolInfo(tool.getToolInfo());
		}
		if (company.getValue()) {
			toolCopy.setCompany(tool.getCompany());
		}
		if (category.getValue()) {
			toolCopy.setCategory(tool.getCategory());
		}
		if (usageStatus.getValue()) {
			toolCopy.setUsageStatus(tool.getUsageStatus());
		}
		if (toolUser.getValue()) {
			toolCopy.setCurrentUser(tool.getCurrentUser());
		}
		if (toolReservedBy.getValue()) {
			toolCopy.setReservedUser(tool.getReservedUser());
		}
		if (boughtDate.getValue()) {
			toolCopy.setDateBought(tool.getDateBought());
		}
		if (nextMaintenanceDate.getValue()) {
			toolCopy.setDateNextMaintenance(tool.getDateNextMaintenance());
		}
		if (price.getValue()) {
			toolCopy.setPrice(tool.getPrice());
		}
		if (guarantee.getValue()) {
			toolCopy.setGuarantee_months(tool.getGuarantee_months());
		}
		if (additionalInfo.getValue()) {
			toolCopy.setAdditionalInfo(tool.getAdditionalInfo());
		}

		return toolCopy;
	}

	public int getNumberOfCopies() {
		return this.numberOfCopies;
	}
}
