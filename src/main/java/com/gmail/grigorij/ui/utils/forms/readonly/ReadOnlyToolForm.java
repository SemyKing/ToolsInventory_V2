package com.gmail.grigorij.ui.utils.forms.readonly;

import com.gmail.grigorij.backend.entities.inventory.InventoryEntity;
import com.gmail.grigorij.ui.utils.UIUtils;
import com.gmail.grigorij.ui.utils.components.Divider;
import com.gmail.grigorij.ui.utils.components.FlexBoxLayout;
import com.gmail.grigorij.ui.utils.css.FlexDirection;
import com.gmail.grigorij.ui.utils.css.LumoStyles;
import com.gmail.grigorij.ui.utils.css.size.Horizontal;
import com.gmail.grigorij.ui.utils.css.size.Vertical;
import com.gmail.grigorij.utils.ProjectConstants;
import com.gmail.grigorij.utils.converters.DateConverter;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.*;

public class ReadOnlyToolForm extends FormLayout {

	private Binder<InventoryEntity> binder = new Binder<>(InventoryEntity.class);

	public ReadOnlyToolForm() {
		TextField toolName = getTextField("Name", "", "");
		ReadOnlyHasValue<InventoryEntity> name = new ReadOnlyHasValue<>(tool ->
				toolName.setValue(tool.getName()));

		TextField toolManufacturer = getTextField("Manufacturer", "", "");
		ReadOnlyHasValue<InventoryEntity> manufacturer = new ReadOnlyHasValue<>(tool ->
				toolManufacturer.setValue(tool.getManufacturer()));

		TextField toolModel = getTextField("Model", "", "");
		ReadOnlyHasValue<InventoryEntity> model = new ReadOnlyHasValue<>(tool ->
				toolModel.setValue(tool.getModel()));

		TextField toolInfo = getTextField("Tool Info", "", "");
		toolInfo.setReadOnly(true);
		ReadOnlyHasValue<InventoryEntity> info = new ReadOnlyHasValue<>(tool ->
				toolInfo.setValue(tool.getToolInfo()));

		TextField toolSN = getTextField("SN", "", "");
		ReadOnlyHasValue<InventoryEntity> sn = new ReadOnlyHasValue<>(tool ->
				toolSN.setValue(tool.getSnCode()));

		TextField toolBarcode = getTextField("Barcode", "", "");
		ReadOnlyHasValue<InventoryEntity> barcode = new ReadOnlyHasValue<>(tool ->
				toolBarcode.setValue(tool.getBarcode()));

		TextField toolCategory = getTextField("Category", "", "");
		ReadOnlyHasValue<InventoryEntity> category = new ReadOnlyHasValue<>(tool ->
				toolCategory.setValue( tool.getParentCategory()==null ? "" : tool.getParentCategory().getName() )
		);

		TextField toolUsageStatus = getTextField("Status", "", "");
		ReadOnlyHasValue<InventoryEntity> usageStatus = new ReadOnlyHasValue<>(tool ->
				toolUsageStatus.setValue(tool.getUsageStatus().getStringValue()));

		TextField toolUser = getTextField("In Use By", "", "");
		ReadOnlyHasValue<InventoryEntity> user = new ReadOnlyHasValue<>(tool ->
				toolUser.setValue( tool.getUser() == null ? "" : tool.getUser().getUsername()));

		TextField toolReservedByUser = getTextField("Reserved By", "", "");
		ReadOnlyHasValue<InventoryEntity> reservedByUser = new ReadOnlyHasValue<>(tool ->
				toolReservedByUser.setValue( tool.getReservedByUser() == null ? "" : tool.getReservedByUser().getUsername()));

		TextField toolBoughtDate = getTextField("Bought", ProjectConstants.FORM_HALF_WIDTH, "");
		ReadOnlyHasValue<InventoryEntity> boughtDate = new ReadOnlyHasValue<>(tool ->
				toolBoughtDate.setValue( tool.getDateBought() == null ? "" : DateConverter.DateToString(tool.getDateBought())));

		TextField toolNextMaintenanceDate = getTextField("Next Maintenance", ProjectConstants.FORM_HALF_WIDTH, "");
		ReadOnlyHasValue<InventoryEntity> nextMaintenanceDate = new ReadOnlyHasValue<>(tool ->
				toolNextMaintenanceDate.setValue( tool.getDateBought() == null ? "" : DateConverter.DateToString(tool.getDateNextMaintenance())));

		FlexBoxLayout datesLayout = new FlexBoxLayout();
		datesLayout.setFlexDirection(FlexDirection.ROW);
		datesLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
		datesLayout.add(toolBoughtDate, toolNextMaintenanceDate);

		TextField toolPrice = getTextField("Price", ProjectConstants.FORM_HALF_WIDTH, "€");
		ReadOnlyHasValue<InventoryEntity> price = new ReadOnlyHasValue<>(tool ->
				toolPrice.setValue( tool.getPrice() == null ? "" : String.valueOf(tool.getPrice())));

		TextField toolGuarantee = getTextField("Guarantee", ProjectConstants.FORM_HALF_WIDTH, "Months");
		ReadOnlyHasValue<InventoryEntity> guarantee = new ReadOnlyHasValue<>(tool ->
				toolGuarantee.setValue( tool.getGuarantee_months() == null ? "" : String.valueOf(tool.getGuarantee_months())));

		FlexBoxLayout priceAndGuaranteeLayout = new FlexBoxLayout();
		priceAndGuaranteeLayout.setFlexDirection(FlexDirection.ROW);
		priceAndGuaranteeLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
		priceAndGuaranteeLayout.add(toolPrice, toolGuarantee);

		TextField toolAdditionalInfo = getTextField("Additional Info", "", "");
		ReadOnlyHasValue<InventoryEntity> additionalInfo = new ReadOnlyHasValue<>(tool ->
				toolAdditionalInfo.setValue(tool.getAdditionalInfo()));

		Divider divider = new Divider(Horizontal.NONE, Vertical.S);
		Divider divider2 = new Divider(Horizontal.NONE, Vertical.S);

		UIUtils.setColSpan(2, divider, divider2, toolAdditionalInfo);

		addClassNames(LumoStyles.Padding.Bottom.L, LumoStyles.Padding.Horizontal.M, LumoStyles.Padding.Top.S);
		setResponsiveSteps(
				new FormLayout.ResponsiveStep("0", 1, FormLayout.ResponsiveStep.LabelsPosition.TOP),
				new FormLayout.ResponsiveStep(ProjectConstants.COL_2_MIN_WIDTH, 2, FormLayout.ResponsiveStep.LabelsPosition.TOP));
		add(toolName);
		add(toolManufacturer);
		add(toolModel);
		add(toolInfo);
		add(toolSN);
		add(toolBarcode);
		add(divider);
		add(toolCategory);
		add(toolUsageStatus);
		add(toolUser);
		add(toolReservedByUser);
		add(divider2);
		add(datesLayout);
		add(priceAndGuaranteeLayout);
		add(toolAdditionalInfo);


		binder.forField(name)
				.bind(tool -> tool, null);
		binder.forField(manufacturer)
				.bind(tool -> tool, null);
		binder.forField(model)
				.bind(tool -> tool, null);
		binder.forField(info)
				.bind(tool -> tool, null);
		binder.forField(sn)
				.bind(tool -> tool, null);
		binder.forField(barcode)
				.bind(tool -> tool, null);
		binder.forField(category)
				.bind(tool -> tool, null);
		binder.forField(user)
				.bind(tool -> tool, null);
		binder.forField(usageStatus)
				.bind(tool -> tool, null);
		binder.forField(reservedByUser)
				.bind(tool -> tool, null);
		binder.forField(price)
				.bind(tool -> tool, null);
		binder.forField(guarantee)
				.bind(tool -> tool, null);
		binder.forField(boughtDate)
				.bind(tool -> tool, null);
		binder.forField(nextMaintenanceDate)
				.bind(tool -> tool, null);
		binder.forField(additionalInfo)
				.bind(tool -> tool, null);
	}

	private TextField getTextField(String text, String width, String suffix) {
		TextField textField = new TextField(text);
		if (width.length() > 0) {
			textField.setWidth(width);
		}
		if (suffix.length() > 0) {
			textField.setSuffixComponent(new Span(suffix));
		}
		textField.setReadOnly(true);
		textField.getStyle().set("padding-top", "var(--lumo-space-s)");

		return textField;
	}

	public void setTool(InventoryEntity tool) {
		try {
			binder.removeBean();
			binder.readBean(tool);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
