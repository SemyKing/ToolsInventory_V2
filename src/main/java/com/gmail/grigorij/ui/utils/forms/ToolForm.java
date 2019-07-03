package com.gmail.grigorij.ui.utils.forms;

import com.gmail.grigorij.backend.database.facades.UserFacade;
import com.gmail.grigorij.backend.entities.tool.Tool;
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

public class ToolForm extends FormLayout {


	private Binder<Tool> binder = new Binder<>(Tool.class);

	public ToolForm() {
//		setSizeFull();

		TextField toolName = getTextField("Name", "", "");
		ReadOnlyHasValue<Tool> name = new ReadOnlyHasValue<>(tool ->
				toolName.setValue(tool.getName()));

		TextField toolManufacturer = getTextField("Manufacturer", "", "");
		ReadOnlyHasValue<Tool> manufacturer = new ReadOnlyHasValue<>(tool ->
				toolManufacturer.setValue(tool.getManufacturer()));

		TextField toolModel = getTextField("Model", "", "");
		ReadOnlyHasValue<Tool> model = new ReadOnlyHasValue<>(tool ->
				toolModel.setValue(tool.getModel()));

		TextField toolInfo = getTextField("Tool Info", "", "");
		toolInfo.setReadOnly(true);
		ReadOnlyHasValue<Tool> info = new ReadOnlyHasValue<>(tool ->
				toolInfo.setValue(tool.getToolInfo()));

		TextField toolSN = getTextField("SN", "", "");
		ReadOnlyHasValue<Tool> sn = new ReadOnlyHasValue<>(tool ->
				toolSN.setValue(tool.getSnCode()));

		TextField toolBarcode = getTextField("Barcode", "", "");
		ReadOnlyHasValue<Tool> barcode = new ReadOnlyHasValue<>(tool ->
				toolBarcode.setValue(tool.getBarcode()));

		TextField toolCategory = getTextField("Category", "", "");
		ReadOnlyHasValue<Tool> category = new ReadOnlyHasValue<>(tool ->
				toolCategory.setValue( tool.getParentCategory()==null ? "" : tool.getParentCategory().getName() )
		);

		TextField toolUsageStatus = getTextField("Status", "", "");
		ReadOnlyHasValue<Tool> usageStatus = new ReadOnlyHasValue<>(tool ->
				toolUsageStatus.setValue(tool.getUsageStatus().getStringValue()));

		TextField toolUser = getTextField("In Use By", "", "");
		ReadOnlyHasValue<Tool> user = new ReadOnlyHasValue<>(tool ->
				toolUser.setValue( tool.getInUseByUserId()<0 ? "" : UserFacade.getInstance().getUserById(tool.getInUseByUserId()).getUsername()));

		TextField toolReservedByUser = getTextField("Reserved By", "", "");
		ReadOnlyHasValue<Tool> reservedByUser = new ReadOnlyHasValue<>(tool ->
				toolReservedByUser.setValue( tool.getReservedByUserId()<0 ? "" : UserFacade.getInstance().getUserById(tool.getReservedByUserId()).getUsername()));

		TextField toolBoughtDate = getTextField("Bought", ProjectConstants.FORM_HALF_WIDTH, "");
		ReadOnlyHasValue<Tool> boughtDate = new ReadOnlyHasValue<>(tool ->
				toolBoughtDate.setValue( tool.getDateBought() == null ? "" : DateConverter.DateToString(tool.getDateBought())));

		TextField toolNextMaintenanceDate = getTextField("Next Maintenance", ProjectConstants.FORM_HALF_WIDTH, "");
		ReadOnlyHasValue<Tool> nextMaintenanceDate = new ReadOnlyHasValue<>(tool ->
				toolNextMaintenanceDate.setValue( tool.getDateBought() == null ? "" : DateConverter.DateToString(tool.getDateNextMaintenance())));

		FlexBoxLayout datesLayout = new FlexBoxLayout();
		datesLayout.setFlexDirection(FlexDirection.ROW);
		datesLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
		datesLayout.add(toolBoughtDate, toolNextMaintenanceDate);

		TextField toolPrice = getTextField("Price", ProjectConstants.FORM_HALF_WIDTH, "€");
		ReadOnlyHasValue<Tool> price = new ReadOnlyHasValue<>(tool ->
				toolPrice.setValue( tool.getPrice() == null ? "" : String.valueOf(tool.getPrice())));

		TextField toolGuarantee = getTextField("Guarantee", ProjectConstants.FORM_HALF_WIDTH, "Months");
		ReadOnlyHasValue<Tool> guarantee = new ReadOnlyHasValue<>(tool ->
				toolGuarantee.setValue( tool.getGuarantee_months() == null ? "" : String.valueOf(tool.getGuarantee_months())));

		FlexBoxLayout priceAndGuaranteeLayout = new FlexBoxLayout();
		priceAndGuaranteeLayout.setFlexDirection(FlexDirection.ROW);
		priceAndGuaranteeLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
		priceAndGuaranteeLayout.add(toolPrice, toolGuarantee);

		TextField toolAdditionalInfo = getTextField("Additional Info", "", "");
		ReadOnlyHasValue<Tool> additionalInfo = new ReadOnlyHasValue<>(tool ->
				toolAdditionalInfo.setValue(tool.getAdditionalInfo()));

		Divider divider = new Divider(Horizontal.NONE, Vertical.S);
		Divider divider2 = new Divider(Horizontal.NONE, Vertical.S);

		UIUtils.setColSpan(2, divider, divider2, toolAdditionalInfo);

//      Form layout
//		FormLayout formLayout = new FormLayout();
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
		add(divider); //colspan 2
		add(toolCategory);
		add(toolUsageStatus);
		add(toolUser);
		add(toolReservedByUser);
		add(divider2); //colspan 2
		add(datesLayout);
		add(priceAndGuaranteeLayout);
		add(toolAdditionalInfo); //colspan 2


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

//		add(formLayout);
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

	public boolean setTool(Tool tool) {
		try {
			binder.removeBean();
			binder.readBean(tool);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}
}
