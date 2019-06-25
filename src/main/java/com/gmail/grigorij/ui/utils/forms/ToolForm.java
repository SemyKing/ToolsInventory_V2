package com.gmail.grigorij.ui.utils.forms;

import com.gmail.grigorij.backend.database.facades.ToolFacade;
import com.gmail.grigorij.backend.entities.tool.HierarchyType;
import com.gmail.grigorij.backend.entities.tool.Tool;
import com.gmail.grigorij.backend.entities.tool.ToolStatus;
import com.gmail.grigorij.ui.utils.components.Divider;
import com.gmail.grigorij.ui.utils.components.FlexBoxLayout;
import com.gmail.grigorij.ui.utils.css.FlexDirection;
import com.gmail.grigorij.ui.utils.css.LumoStyles;
import com.gmail.grigorij.utils.converters.CustomConverter;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.binder.Validator;
import com.vaadin.flow.data.converter.StringToDoubleConverter;
import com.vaadin.flow.data.converter.StringToIntegerConverter;

import java.time.LocalDate;
import java.util.EnumSet;

public class ToolForm extends Div {

	private Tool tool;

//	private Binder<Tool> toolBinder = new Binder<>(Tool.class);
//	private Binder<Tool> categoryBinder = new Binder<>(Tool.class);
	private Binder<Tool> binder = new Binder<>(Tool.class);

	private Div categoryFormLayout;
	private Div toolFormLayout;

	public ToolForm() {
		setSizeFull();

		toolFormLayout = new Div();
		toolFormLayout.setSizeFull();
		toolFormLayout.add(constructToolForm());


		categoryFormLayout = new Div();
		categoryFormLayout.setSizeFull();
		categoryFormLayout.add(constructCategoryForm());
	}

	private FormLayout constructToolForm() {
		TextField toolNameField = new TextField();
		toolNameField.setWidth("100%");

		TextField manufacturerField = new TextField();
		manufacturerField.setWidth("100%");

		TextField modelField = new TextField();
		modelField.setWidth("100%");

		TextField toolInfoField = new TextField();
		toolInfoField.setWidth("100%");

		TextField snCodeField = new TextField();
		snCodeField.setWidth("100%");

		TextField barCodeField = new TextField();
		barCodeField.setWidth("100%");


		Divider divider1 = new Divider("1px");


		ComboBox<Tool> categoriesComboBox = new ComboBox<>();
		categoriesComboBox.setItems(ToolFacade.getInstance().getAllCategoriesList());
		categoriesComboBox.setItemLabelGenerator(Tool::getName);
		categoriesComboBox.setWidth("100%");

		TextField userField = new TextField();
		userField.setWidth("100%");

		ComboBox<ToolStatus> toolStatusComboBox = new ComboBox<>();
		toolStatusComboBox.setItems(EnumSet.allOf(ToolStatus.class));
		toolStatusComboBox.setItemLabelGenerator(ToolStatus::getStringValue);
		toolStatusComboBox.setWidth("100%");

		TextField reservedByField = new TextField();
		reservedByField.setWidth("100%");


		Divider divider2 = new Divider("1px");


		DatePicker boughtDateField = new DatePicker("Bought");
		boughtDateField.setWidth("calc(48% - 0rem)");
		boughtDateField.setWeekNumbersVisible(true);

		DatePicker nextMaintenanceDateField = new DatePicker("Next Maintenance");
		nextMaintenanceDateField.setWidth("calc(48% - 0rem)");
		nextMaintenanceDateField.setWeekNumbersVisible(true);

		FlexBoxLayout datesLayout = new FlexBoxLayout();
		datesLayout.setFlexDirection(FlexDirection.ROW);
		datesLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
		datesLayout.add(boughtDateField, nextMaintenanceDateField);


		TextField priceField = new TextField("Price");
		priceField.setWidth("calc(48% - 0rem)");
		priceField.setSuffixComponent(new Span("€"));
		priceField.getElement().setAttribute("type", "number");
//		priceField.getElement().setAttribute("step", "0.001");

		TextField guaranteeField = new TextField("Guarantee");
		guaranteeField.setWidth("calc(48% - 0rem)");
		guaranteeField.setSuffixComponent(new Span("Months"));

		FlexBoxLayout priceAndGuaranteeLayout = new FlexBoxLayout();
		priceAndGuaranteeLayout.setFlexDirection(FlexDirection.ROW);
		priceAndGuaranteeLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
		priceAndGuaranteeLayout.add(priceField, guaranteeField);


		TextArea additionalInfo = new TextArea();
		additionalInfo.setWidth("100%");
		additionalInfo.setMaxHeight("300px");


//      Form layout
		FormLayout formLayout = new FormLayout();
		formLayout.addClassNames(LumoStyles.Padding.Bottom.L, LumoStyles.Padding.Horizontal.M, LumoStyles.Padding.Top.S);
		formLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 2, FormLayout.ResponsiveStep.LabelsPosition.TOP));
		formLayout.addFormItem(toolNameField,       "Tool Name");
		formLayout.addFormItem(manufacturerField,   "Manufacturer");
		formLayout.addFormItem(modelField,          "Model");
		formLayout.addFormItem(toolInfoField,       "Tool Info");
		formLayout.addFormItem(snCodeField,         "SN");
		formLayout.addFormItem(barCodeField,        "Barcode");
		formLayout.addFormItem(divider1,"")
				.getElement().setAttribute("colspan", "2");
		formLayout.addFormItem(categoriesComboBox,  "Category");
		formLayout.addFormItem(toolStatusComboBox,  "Status");
		formLayout.addFormItem(userField,           "User");
		formLayout.addFormItem(reservedByField,     "Reserved By");
		formLayout.addFormItem(divider2,"")
				.getElement().setAttribute("colspan", "2");
		formLayout.addFormItem(datesLayout,         "");
		formLayout.addFormItem(priceAndGuaranteeLayout,"");
		formLayout.addFormItem(additionalInfo, "Additional Info")
				.getElement().setAttribute("colspan", "2");

//		.getElement().setAttribute("colspan", "2");



		binder.forField(toolNameField)
				.asRequired("Name is required")
				.bind(Tool::getName, Tool::setName);
		binder.forField(manufacturerField)
				.bind(Tool::getManufacturer, Tool::setManufacturer);
		binder.forField(modelField)
				.bind(Tool::getModel, Tool::setModel);
		binder.forField(toolInfoField)
				.bind(Tool::getToolInfo, Tool::setToolInfo);
		binder.forField(snCodeField)
				.bind(Tool::getSnCode, Tool::setSnCode);
		binder.forField(barCodeField)
				.bind(Tool::getBarcode, Tool::setBarcode);
		binder.forField(categoriesComboBox)
				.asRequired("Category is required")
				.withConverter(new CustomConverter.ToolCategoryConverter())
				.bind(Tool::getParent, Tool::setParent);
		binder.forField(userField)
				.withConverter(new CustomConverter.UserById())
				.bind(Tool::getInUseByUserId, Tool::setInUseByUserId);
		binder.forField(toolStatusComboBox)
				.asRequired("Status is required")
				.bind(Tool::getStatus, Tool::setStatus);
		binder.forField(reservedByField)
				.withConverter(new CustomConverter.UserById())
				.bind(Tool::getReservedByUserId, Tool::setReservedByUserId);
		binder.forField(boughtDateField)
				.bind(Tool::getDateBought, Tool::setDateBought);
		binder.forField(priceField)
				.withConverter(new StringToDoubleConverter("Price must be a number"))
				.withNullRepresentation(0.00)
				.bind(Tool::getPrice, Tool::setPrice);
		binder.forField(guaranteeField)
				.withConverter(new StringToIntegerConverter("Guarantee must be a number"))
				.withNullRepresentation(0)
				.bind(Tool::getGuarantee_months, Tool::setGuarantee_months);

		binder.forField(nextMaintenanceDateField)
				.withValidator((Validator<LocalDate>) (nextMaintenanceDate, valueContext) -> {
					if (boughtDateField.getValue() == null) {
						return ValidationResult.ok();
					}
					if (nextMaintenanceDate == null) {
						return ValidationResult.ok();
					} else {
						if (nextMaintenanceDate.isBefore(boughtDateField.getValue())) {
							return ValidationResult.error("Next maintenance date cannot be before purchase date");
						} else {
							return ValidationResult.ok();
						}
					}
				})
				.bind(Tool::getDateBought, Tool::setDateBought);

		binder.forField(additionalInfo)
				.bind(Tool::getAdditionalInfo, Tool::setAdditionalInfo);

		return formLayout;
	}

	private FormLayout constructCategoryForm() {
		TextField toolCategoryField = new TextField();
		toolCategoryField.setWidth("100%");

//      Form layout
		FormLayout formLayout = new FormLayout();
		formLayout.addClassNames(LumoStyles.Padding.Bottom.L, LumoStyles.Padding.Horizontal.M, LumoStyles.Padding.Top.S);
		formLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 2, FormLayout.ResponsiveStep.LabelsPosition.TOP));
		formLayout.addFormItem(toolCategoryField, "Category Name");

		binder.forField(toolCategoryField)
				.asRequired("Name is required")
				.bind(Tool::getName, Tool::setName);

		return formLayout;
	}


	public boolean setTool(Tool t, HierarchyType hierarchyType) {
		if (t == null) {
			System.out.println("Tool cannot be NULL");
			return false;
		}

		removeAll();
		binder.removeBean();

		tool = t;

		if (hierarchyType.equals(HierarchyType.CATEGORY)) {
			add(categoryFormLayout);
		} else if (hierarchyType.equals(HierarchyType.TOOL)) {
			add(toolFormLayout);
		} else {
			System.out.println("Unknown ToolForm HierarchyType: " + hierarchyType.toString());
			return false;
		}

		try {
			binder.readBean(t);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public Tool getTool() {
		try {
			binder.validate();

			if (binder.isValid()) {
				binder.writeBean(tool);

				return tool;
			}
		} catch (ValidationException e) {
			e.printStackTrace();
			return null;
		}
		return null;
	}
}
