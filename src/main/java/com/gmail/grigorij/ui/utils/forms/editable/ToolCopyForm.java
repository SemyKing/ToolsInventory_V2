package com.gmail.grigorij.ui.utils.forms.editable;

import com.gmail.grigorij.backend.entities.inventory.InventoryItem;
import com.gmail.grigorij.ui.utils.UIUtils;
import com.gmail.grigorij.ui.utils.components.Divider;
import com.gmail.grigorij.ui.utils.components.FlexBoxLayout;
import com.gmail.grigorij.ui.utils.css.LumoStyles;
import com.gmail.grigorij.ui.utils.css.size.Horizontal;
import com.gmail.grigorij.ui.utils.css.size.Vertical;
import com.gmail.grigorij.utils.ProjectConstants;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.NumberField;


public class ToolCopyForm extends FormLayout {

	private InventoryItem originalTool;

	private NumberField numberOfCopiesField;
	private Checkbox name;
	private Checkbox qrCode;
	private Checkbox barcode;
	private Checkbox sn;
	private Checkbox manufacturer;
	private Checkbox model;
	private Checkbox info;
	private Checkbox company;
	private Checkbox category;
	private Checkbox status;
	private Checkbox toolUser;
	private Checkbox toolReserved;
	private Checkbox boughtDate;
	private Checkbox nextMaintenanceDate;
	private Checkbox price;
	private Checkbox guarantee;
	private Checkbox additionalInfo;

	private int numberOfCopies = 1;

	public ToolCopyForm() {
		Checkbox allCheckBox = new Checkbox("All");
		allCheckBox.addValueChangeListener(e -> {
			if (e != null) {
				if (e.getValue() != null) {
					setAll(e.getValue());
				}
			}
		});

		numberOfCopiesField = new NumberField("Number of copies");
		numberOfCopiesField.setMin(1);
		numberOfCopiesField.setMax(100);
		numberOfCopiesField.setHasControls(true);
		numberOfCopiesField.setStep(1);
		numberOfCopiesField.setValue(1.00);

		//ALL CHECK BOX & NUMBER OF COPIES FIELD
		FlexBoxLayout layout = UIUtils.getFormRowLayout(allCheckBox, numberOfCopiesField, true);

		name = new Checkbox("Name");
		qrCode = new Checkbox("QR Code");
		barcode = new Checkbox("Barcode");
		sn = new Checkbox("SN");
		manufacturer = new Checkbox("Manufacturer");
		model = new Checkbox("Model");
		info = new Checkbox("Info");
		company = new Checkbox("Company");
		category = new Checkbox("Category");
		status = new Checkbox("status");
		toolUser = new Checkbox("In Use By");
		toolReserved = new Checkbox("Reserved By");
		boughtDate = new Checkbox("Bought Date");
		nextMaintenanceDate = new Checkbox("Next Maintenance Date");
		price = new Checkbox("Price");
		guarantee = new Checkbox("Guarantee");
		additionalInfo = new Checkbox("Additional Info");

		allCheckBox.setValue(true);


		UIUtils.setColSpan(2, layout);

		addClassNames(LumoStyles.Padding.Bottom.S, LumoStyles.Padding.Top.S);
		setResponsiveSteps(
				new FormLayout.ResponsiveStep("0", 1, FormLayout.ResponsiveStep.LabelsPosition.TOP),
				new FormLayout.ResponsiveStep(ProjectConstants.COL_2_MIN_WIDTH, 2, FormLayout.ResponsiveStep.LabelsPosition.TOP));
		add(layout);
		add(new Divider(2, Horizontal.NONE, Vertical.S));
		add(name);
		add(qrCode);
		add(barcode);
		add(sn);
		add(manufacturer);
		add(model);
		add(info);
		add(company);
		add(category);
		add(status);
		add(toolUser);
		add(toolReserved);
		add(boughtDate);
		add(nextMaintenanceDate);
		add(price);
		add(guarantee);
		add(additionalInfo);
	}

	private void setAll(boolean b) {
		name.setValue(b);
		qrCode.setValue(b);
		barcode.setValue(b);
		sn.setValue(b);
		manufacturer.setValue(b);
		model.setValue(b);
		info.setValue(b);
		company.setValue(b);
		category.setValue(b);
		status.setValue(b);
		toolUser.setValue(b);
		toolReserved.setValue(b);
		boughtDate.setValue(b);
		nextMaintenanceDate.setValue(b);
		price.setValue(b);
		guarantee.setValue(b);
		additionalInfo.setValue(b);
	}


	public boolean setOriginalTool(InventoryItem originalTool) {
		if (originalTool == null) {
			System.err.println("Tool copy, original inventory cannot be NULL");
			return false;
		}
		this.originalTool = originalTool;
		return true;
	}

	public InventoryItem getToolCopy() {
		if (numberOfCopiesField.isInvalid()) {
			return null;
		}

		if (numberOfCopiesField.getValue() != null) {
			this.numberOfCopies = numberOfCopiesField.getValue().intValue();
		}

		InventoryItem tool = new InventoryItem();

		if (name.getValue()) {
			tool.setName(originalTool.getName());
		}
		if (qrCode.getValue()) {
			tool.setQrCode(originalTool.getQrCode());
		}
		if (barcode.getValue()) {
			tool.setBarcode(originalTool.getBarcode());
		}
		if (sn.getValue()) {
			tool.setSnCode(originalTool.getSnCode());
		}
		if (manufacturer.getValue()) {
			tool.setManufacturer(originalTool.getManufacturer());
		}
		if (model.getValue()) {
			tool.setModel(originalTool.getModel());
		}
		if (info.getValue()) {
			tool.setToolInfo(originalTool.getToolInfo());
		}
		if (company.getValue()) {
			tool.setCompany(originalTool.getCompany());
		}
		if (category.getValue()) {
			tool.setParentCategory(originalTool.getParentCategory());
		}
		if (status.getValue()) {
			tool.setUsageStatus(originalTool.getUsageStatus());
		}
		if (toolUser.getValue()) {
			tool.setUser(originalTool.getUser());
		}
		if (toolReserved.getValue()) {
			tool.setReservedByUser(originalTool.getReservedByUser());
		}
		if (boughtDate.getValue()) {
			tool.setDateBought(originalTool.getDateBought());
		}
		if (nextMaintenanceDate.getValue()) {
			tool.setDateNextMaintenance(originalTool.getDateNextMaintenance());
		}
		if (price.getValue()) {
			tool.setPrice(originalTool.getPrice());
		}
		if (guarantee.getValue()) {
			tool.setGuarantee_months(originalTool.getGuarantee_months());
		}
		if (additionalInfo.getValue()) {
			tool.setAdditionalInfo(originalTool.getAdditionalInfo());
		}

		return tool;
	}

	public int getNumberOfCopies() {
		return this.numberOfCopies;
	}
}
