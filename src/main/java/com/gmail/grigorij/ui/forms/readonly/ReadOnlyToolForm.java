package com.gmail.grigorij.ui.forms.readonly;

import com.gmail.grigorij.backend.entities.inventory.InventoryItem;
import com.gmail.grigorij.ui.utils.UIUtils;
import com.gmail.grigorij.ui.components.Divider;
import com.gmail.grigorij.ui.components.FlexBoxLayout;
import com.gmail.grigorij.ui.utils.css.LumoStyles;
import com.gmail.grigorij.ui.utils.css.size.Horizontal;
import com.gmail.grigorij.ui.utils.css.size.Vertical;
import com.gmail.grigorij.utils.ProjectConstants;
import com.gmail.grigorij.utils.converters.DateConverter;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.*;

public class ReadOnlyToolForm extends FormLayout {

	private Binder<InventoryItem> binder = new Binder<>(InventoryItem.class);

	public ReadOnlyToolForm() {

		TextField nameField = new TextField("Name");
		nameField.setReadOnly(true);
		ReadOnlyHasValue<InventoryItem> name = new ReadOnlyHasValue<>(tool -> nameField.setValue( tool.getName() ));

		TextField qrField = new TextField("QR Code");
		qrField.setReadOnly(true);
		ReadOnlyHasValue<InventoryItem> qr = new ReadOnlyHasValue<>(tool -> qrField.setValue( tool.getQrCode() ));

		TextField barcodeField = new TextField("Barcode");
		barcodeField.setReadOnly(true);
		ReadOnlyHasValue<InventoryItem> barcode = new ReadOnlyHasValue<>(tool -> barcodeField.setValue( tool.getBarcode() ));

		TextField snField = new TextField("SN");
		snField.setReadOnly(true);
		ReadOnlyHasValue<InventoryItem> sn = new ReadOnlyHasValue<>(tool -> snField.setValue(tool.getSnCode()));

		TextField toolInfoField = new TextField("Tool Info");
		toolInfoField.setReadOnly(true);
		ReadOnlyHasValue<InventoryItem> toolInfo = new ReadOnlyHasValue<>(tool -> toolInfoField.setValue( tool.getToolInfo() ));

		TextField manufacturerField = new TextField("Manufacturer");
		manufacturerField.setReadOnly(true);
		ReadOnlyHasValue<InventoryItem> manufacturer = new ReadOnlyHasValue<>(tool -> manufacturerField.setValue( tool.getManufacturer() ));

		TextField modelField = new TextField("Model");
		modelField.setReadOnly(true);
		ReadOnlyHasValue<InventoryItem> model = new ReadOnlyHasValue<>(tool -> modelField.setValue( tool.getModel() ));

		TextField companyField = new TextField("Model");
		companyField.setReadOnly(true);
		ReadOnlyHasValue<InventoryItem> company = new ReadOnlyHasValue<>(tool -> {
			companyField.setValue((tool.getCompany() == null) ? "" : tool.getCompany().getName());
		});

		TextField categoryField = new TextField("Category");
		categoryField.setReadOnly(true);
		ReadOnlyHasValue<InventoryItem> category = new ReadOnlyHasValue<>(tool -> {
			categoryField.setValue((tool.getParentCategory() == null) ? "" : tool.getParentCategory().getName());
		});

		TextField usageStatusField = new TextField("Status");
		usageStatusField.setReadOnly(true);
		ReadOnlyHasValue<InventoryItem> usageStatus = new ReadOnlyHasValue<>(tool -> {
			usageStatusField.setValue(tool.getUsageStatus().getStringValue());
		});

		TextField inUseByField = new TextField("In Use By");
		inUseByField.setReadOnly(true);
		ReadOnlyHasValue<InventoryItem> inUseBy = new ReadOnlyHasValue<>(tool -> {
			inUseByField.setValue((tool.getInUseByUser() == null) ? "" : tool.getInUseByUser().getUsername());
		});

		TextField reservedByField = new TextField("Reserved By");
		reservedByField.setReadOnly(true);
		ReadOnlyHasValue<InventoryItem> reservedBy = new ReadOnlyHasValue<>(tool -> {
			reservedByField.setValue((tool.getReservedByUser() == null) ? "" : tool.getReservedByUser().getUsername());
		});

		TextField dateBoughtField = new TextField("Bought");
		dateBoughtField.setWidth(ProjectConstants.FORM_HALF_WIDTH);
		dateBoughtField.setReadOnly(true);
		ReadOnlyHasValue<InventoryItem> dateBought = new ReadOnlyHasValue<>(tool -> {
			dateBoughtField.setValue(tool.getDateBought() == null ? "" : DateConverter.toStringDate(tool.getDateBought()));
		});

		TextField dateNextMaintenanceField = new TextField("Next Maintenance");
		dateNextMaintenanceField.setWidth(ProjectConstants.FORM_HALF_WIDTH);
		dateNextMaintenanceField.setReadOnly(true);
		ReadOnlyHasValue<InventoryItem> dateNextMaintenance = new ReadOnlyHasValue<>(tool -> {
			dateNextMaintenanceField.setValue((tool.getDateBought() == null) ? "" : DateConverter.toStringDate(tool.getDateNextMaintenance()));
		});

		//DATE BOUGHT & DATE NEXT MAINTENANCE
		FlexBoxLayout datesLayout = UIUtils.getFormRowLayout(dateBoughtField, dateNextMaintenanceField, true);

		TextField priceField = new TextField("Price");
		priceField.setWidth(ProjectConstants.FORM_HALF_WIDTH);
		priceField.setSuffixComponent(new Span("€"));
		priceField.setReadOnly(true);
		ReadOnlyHasValue<InventoryItem> price = new ReadOnlyHasValue<>(tool -> {
			priceField.setValue((tool.getPrice() == null) ? "" : String.valueOf(tool.getPrice()));
		});

		TextField guaranteeField = new TextField("Guarantee");
		guaranteeField.setWidth(ProjectConstants.FORM_HALF_WIDTH);
		guaranteeField.setSuffixComponent(new Span("Months"));
		guaranteeField.setReadOnly(true);
		ReadOnlyHasValue<InventoryItem> guarantee = new ReadOnlyHasValue<>(tool ->{
			guaranteeField.setValue((tool.getGuarantee_months() == null) ? "" : String.valueOf(tool.getGuarantee_months()));
		});

		//PRICE & GUARANTEE
		FlexBoxLayout priceAndGuaranteeLayout = UIUtils.getFormRowLayout(priceField, guaranteeField, true);

		TextField additionalInfoField = new TextField("Additional Info");
		additionalInfoField.setReadOnly(true);
		ReadOnlyHasValue<InventoryItem> additionalInfo = new ReadOnlyHasValue<>(tool -> additionalInfoField.setValue(tool.getAdditionalInfo()));


		UIUtils.setColSpan(2, nameField, usageStatusField, additionalInfoField);

		addClassNames(LumoStyles.Padding.Bottom.S, LumoStyles.Padding.Top.S);
		setResponsiveSteps(
				new FormLayout.ResponsiveStep("0", 1, FormLayout.ResponsiveStep.LabelsPosition.TOP),
				new FormLayout.ResponsiveStep(ProjectConstants.COL_2_MIN_WIDTH, 2, FormLayout.ResponsiveStep.LabelsPosition.TOP));
		add(nameField);
		add(qrField);
		add(barcodeField);
		add(snField);
		add(toolInfoField);
		add(manufacturerField);
		add(modelField);
		add(new Divider(2, Horizontal.NONE, Vertical.S));
		add(companyField);
		add(categoryField);
		add(usageStatusField);
		add(inUseByField);
		add(reservedByField);
		add(new Divider(2, Horizontal.NONE, Vertical.S));
		add(datesLayout);
		add(priceAndGuaranteeLayout);
		add(additionalInfoField);


		binder.forField(name)
				.bind(tool -> tool, null);
		binder.forField(qr)
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
		binder.forField(category)
				.bind(tool -> tool, null);
		binder.forField(inUseBy)
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

	public void setTool(InventoryItem tool) {
		try {
			binder.removeBean();
			binder.readBean(tool);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
