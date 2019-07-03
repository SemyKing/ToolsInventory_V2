package com.gmail.grigorij.ui.utils.forms;

import com.gmail.grigorij.backend.entities.tool.Tool;
import com.gmail.grigorij.ui.utils.UIUtils;
import com.gmail.grigorij.ui.utils.components.Divider;
import com.gmail.grigorij.ui.utils.components.FlexBoxLayout;
import com.gmail.grigorij.ui.utils.css.FlexDirection;
import com.gmail.grigorij.ui.utils.css.size.Horizontal;
import com.gmail.grigorij.ui.utils.css.size.Left;
import com.gmail.grigorij.ui.utils.css.size.Vertical;
import com.gmail.grigorij.utils.ProjectConstants;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.textfield.NumberField;


public class ToolCopyForm extends FormLayout {

	private NumberField numberOfCopiesField;
	private Checkbox name;
	private Checkbox manufacturer;
	private Checkbox model;
	private Checkbox info;
	private Checkbox sn;
	private Checkbox barcode;
	private Checkbox company;
	private Checkbox category;
	private Checkbox status;
	private Checkbox boughtDate;
	private Checkbox nextMaintenanceDate;
	private Checkbox price;
	private Checkbox guarantee;
	private Checkbox additionalInfo;

	private int numberOfCopies = 1;
	private int maxNumberOfCopies = 100;

	public ToolCopyForm() {
		Checkbox allCB = new Checkbox("All");
		allCB.addValueChangeListener(e -> {
			if (e != null) {
				if (e.getValue() != null) {
					setAll(e.getValue());
				}
			}
		});

		numberOfCopiesField = new NumberField("Number of copies");
		numberOfCopiesField.setMin(1);
		numberOfCopiesField.setMax(maxNumberOfCopies);
		numberOfCopiesField.setHasControls(true);
		numberOfCopiesField.setStep(1);
		numberOfCopiesField.setValue(1.00);

		FlexBoxLayout layout = new FlexBoxLayout();
		layout.setWidth("100%");
		layout.setFlexDirection(FlexDirection.ROW);
		layout.add(allCB, numberOfCopiesField);
		layout.setComponentMargin(numberOfCopiesField, Left.S);
		layout.setFlexGrow("1", allCB);
		layout.setAlignItems(FlexComponent.Alignment.BASELINE);


		name = new Checkbox("Name");
		manufacturer = new Checkbox("Manufacturer");
		model = new Checkbox("Model");
		info = new Checkbox("Info");
		sn = new Checkbox("SN");
		barcode = new Checkbox("Barcode");
		company = new Checkbox("Company");
		category = new Checkbox("Category");
		status = new Checkbox("status");
		boughtDate = new Checkbox("Bought Date");
		nextMaintenanceDate = new Checkbox("Next Maintenance Date");
		price = new Checkbox("Price");
		guarantee = new Checkbox("Guarantee");
		additionalInfo = new Checkbox("Additional Info");

		allCB.setValue(true);

		Divider divider = new Divider(Horizontal.NONE, Vertical.S);
		Divider divider2 = new Divider(Horizontal.NONE, Vertical.S);

		UIUtils.setColSpan(2, layout, company, divider, divider2);

		// Form layout
		setResponsiveSteps(
				new FormLayout.ResponsiveStep("0", 1, FormLayout.ResponsiveStep.LabelsPosition.TOP),
				new FormLayout.ResponsiveStep(ProjectConstants.COL_2_MIN_WIDTH, 2, FormLayout.ResponsiveStep.LabelsPosition.TOP));
		add(layout);
		add(divider);
		add(name);
		add(manufacturer);
		add(model);
		add(info);
		add(sn);
		add(barcode);
		add(company);
		add(category);
		add(status);
		add(boughtDate);
		add(nextMaintenanceDate);
		add(price);
		add(guarantee);
		add(additionalInfo);
	}

	private void setAll(boolean b) {
		name.setValue(b);
		manufacturer.setValue(b);
		model.setValue(b);
		info.setValue(b);
		sn.setValue(b);
		barcode.setValue(b);
		company.setValue(b);
		category.setValue(b);
		status.setValue(b);
		boughtDate.setValue(b);
		nextMaintenanceDate.setValue(b);
		price.setValue(b);
		guarantee.setValue(b);
		additionalInfo.setValue(b);
	}


	public int getNumberOfCopies() {
		return this.numberOfCopies;
	}

	public Tool getToolCopy() {
		if (numberOfCopiesField.isInvalid()) {
			return null;
		}

		if (numberOfCopiesField.getValue() != null) {
			this.numberOfCopies = numberOfCopiesField.getValue().intValue();
		}

		Tool tool = Tool.getEmptyTool();
//		tool.setLevel(originalTool.getLevel());

		if (name.getValue()) {
			tool.setName(originalTool.getName());
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
		if (sn.getValue()) {
			tool.setSnCode(originalTool.getSnCode());
		}
		if (barcode.getValue()) {
			tool.setBarcode(originalTool.getBarcode());
		}
		if (company.getValue()) {
			tool.setCompanyId(originalTool.getCompanyId());
		}
		if (category.getValue()) {
			tool.setParentCategory(originalTool.getParentCategory());
		}
		if (status.getValue()) {
			tool.setUsageStatus(originalTool.getUsageStatus());
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

	private Tool originalTool;

	public boolean setOriginalTool(Tool originalTool) {
		if (originalTool == null) {
			System.err.println("Tool copy, original tool cannot be NULL");
			return false;
		}
		this.originalTool = originalTool;
		return true;
	}
}
