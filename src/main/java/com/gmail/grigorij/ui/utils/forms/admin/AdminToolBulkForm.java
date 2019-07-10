package com.gmail.grigorij.ui.utils.forms.admin;

import com.gmail.grigorij.backend.database.facades.CompanyFacade;
import com.gmail.grigorij.backend.database.facades.ToolFacade;
import com.gmail.grigorij.backend.database.facades.UserFacade;
import com.gmail.grigorij.backend.entities.company.Company;
import com.gmail.grigorij.backend.entities.tool.Tool;
import com.gmail.grigorij.backend.entities.tool.ToolStatus;
import com.gmail.grigorij.backend.entities.user.User;
import com.gmail.grigorij.ui.utils.UIUtils;
import com.gmail.grigorij.ui.utils.components.Divider;
import com.gmail.grigorij.ui.utils.components.FlexBoxLayout;
import com.gmail.grigorij.ui.utils.css.FlexDirection;
import com.gmail.grigorij.ui.utils.css.size.Horizontal;
import com.gmail.grigorij.ui.utils.css.size.Left;
import com.gmail.grigorij.ui.utils.css.size.Vertical;
import com.gmail.grigorij.ui.views.navigation.admin.AdminInventory;
import com.gmail.grigorij.ui.views.navigation.admin.AdminInventoryBulkEditor;
import com.gmail.grigorij.utils.ProjectConstants;
import com.gmail.grigorij.utils.converters.CustomConverter;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.binder.Validator;
import com.vaadin.flow.data.converter.DateToSqlDateConverter;
import com.vaadin.flow.data.converter.LocalDateToDateConverter;
import com.vaadin.flow.data.converter.StringToDoubleConverter;
import com.vaadin.flow.data.converter.StringToIntegerConverter;

import java.time.LocalDate;
import java.util.EnumSet;

public class AdminToolBulkForm extends FormLayout {


	private Binder<Tool> binder = new Binder<>(Tool.class);
	private Tool tool;

	private ComboBox<Tool> categoriesComboBox;
	private ComboBox<User> toolUserComboBox;
	private ComboBox<User> toolReservedComboBox;


	public AdminToolBulkForm(AdminInventoryBulkEditor bulkEditor) {

		TextField toolNameField = new TextField("Name");
		TextField manufacturerField = new TextField("Manufacturer");
		TextField modelField = new TextField("Model");
		TextField toolInfoField = new TextField("Tool Info");
		TextField snCodeField = new TextField("SN");
		TextField barCodeField = new TextField("Barcode");



		//--------------COMPANY
		FlexBoxLayout companyLayout = new FlexBoxLayout();
		companyLayout.setWidth("100%");
		companyLayout.setFlexDirection(FlexDirection.ROW);
		companyLayout.setAlignItems(FlexComponent.Alignment.BASELINE);

		Button setAllCompaniesButton = UIUtils.createButton("Set all", ButtonVariant.LUMO_CONTRAST);

		ComboBox<Company> companyComboBox = new ComboBox<>();
		companyComboBox.setItems(CompanyFacade.getInstance().getAllCompanies());
		companyComboBox.setItemLabelGenerator(Company::getName);
		companyComboBox.setLabel("Company");
		companyComboBox.setRequired(true);
		companyComboBox.addValueChangeListener(e -> {
			if (e != null) {
				if (e.getValue() != null) {
					updateComboBoxData(e.getValue());
				}
			}
		});

		setAllCompaniesButton.addClickListener(e -> {
			if (e != null) {
				if (companyComboBox.getValue() != null) {
					bulkEditor.setBulkCompanies(companyComboBox.getValue());
				}
			}
		});
		UIUtils.setTooltip("Set selected company to all tools", setAllCompaniesButton);

		companyLayout.add(companyComboBox, setAllCompaniesButton);
		companyLayout.setComponentMargin(setAllCompaniesButton, Left.S);
		companyLayout.setFlexGrow("1", companyComboBox);
		//COMPANY--------------



		//--------------CATEGORY
		FlexBoxLayout categoryLayout = new FlexBoxLayout();
		categoryLayout.setWidth("100%");
		categoryLayout.setFlexDirection(FlexDirection.ROW);
		categoryLayout.setAlignItems(FlexComponent.Alignment.BASELINE);

		Button setAllCategoriesButton = UIUtils.createButton("Set all", ButtonVariant.LUMO_CONTRAST);

		categoriesComboBox = new ComboBox<>();
		categoriesComboBox.setItems(ToolFacade.getInstance().getEmptyList());
		categoriesComboBox.setLabel("Category");
		categoriesComboBox.setItemLabelGenerator(Tool::getName);
		categoriesComboBox.setRequired(true);

		setAllCategoriesButton.addClickListener(e -> {
			if (e != null) {
				if (categoriesComboBox.getValue() != null) {
					bulkEditor.setBulkCategories(categoriesComboBox.getValue());
				}
			}
		});
		UIUtils.setTooltip("Set selected category to all tools", setAllCategoriesButton);

		categoryLayout.add(categoriesComboBox, setAllCategoriesButton);
		categoryLayout.setComponentMargin(setAllCategoriesButton, Left.S);
		categoryLayout.setFlexGrow("1", categoriesComboBox);
		//CATEGORY--------------


		//--------------USAGE STATUS
		FlexBoxLayout statusLayout = new FlexBoxLayout();
		statusLayout.setWidth("100%");
		statusLayout.setFlexDirection(FlexDirection.ROW);
		statusLayout.setAlignItems(FlexComponent.Alignment.BASELINE);

		Button setAllUsageStatusButton = UIUtils.createButton("Set all", ButtonVariant.LUMO_CONTRAST);

		ComboBox<ToolStatus> toolStatusComboBox = new ComboBox<>();
		toolStatusComboBox.setLabel("Status");
		toolStatusComboBox.setItems(EnumSet.allOf(ToolStatus.class));
		toolStatusComboBox.setItemLabelGenerator(ToolStatus::getStringValue);

		setAllUsageStatusButton.addClickListener(e -> {
			if (e != null) {
				if (toolStatusComboBox.getValue() != null) {
					bulkEditor.setBulkUsageStatus(toolStatusComboBox.getValue());
				}
			}
		});
		UIUtils.setTooltip("Set selected usage status to all tools", setAllCategoriesButton);

		statusLayout.add(toolStatusComboBox, setAllUsageStatusButton);
		statusLayout.setComponentMargin(setAllUsageStatusButton, Left.S);
		statusLayout.setFlexGrow("1", toolStatusComboBox);
		//USAGE STATUS--------------


		toolUserComboBox = new ComboBox<>();
		toolUserComboBox.setItems(UserFacade.getInstance().getEmptyList());
		toolUserComboBox.setLabel("In Use By");
		toolUserComboBox.setItemLabelGenerator(User::getUsername);

		toolReservedComboBox = new ComboBox<>();
		toolReservedComboBox.setItems(UserFacade.getInstance().getEmptyList());
		toolReservedComboBox.setLabel("Reserved By");
		toolReservedComboBox.setItemLabelGenerator(User::getUsername);


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

		TextField guaranteeField = new TextField("Guarantee");
		guaranteeField.setWidth("calc(48% - 0rem)");
		guaranteeField.setSuffixComponent(new Span("Months"));

		FlexBoxLayout priceAndGuaranteeLayout = new FlexBoxLayout();
		priceAndGuaranteeLayout.setFlexDirection(FlexDirection.ROW);
		priceAndGuaranteeLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
		priceAndGuaranteeLayout.add(priceField, guaranteeField);

		TextArea additionalInfo = new TextArea("Additional Info");
		additionalInfo.setMaxHeight("200px");

		Divider divider = new Divider(Horizontal.NONE, Vertical.S);
		Divider divider2 = new Divider(Horizontal.NONE, Vertical.S);

		UIUtils.setColSpan(2, companyLayout, categoryLayout, statusLayout, additionalInfo, datesLayout, priceAndGuaranteeLayout, divider, divider2);

//      Form layout
		setResponsiveSteps(
				new ResponsiveStep("0", 1, ResponsiveStep.LabelsPosition.TOP),
				new ResponsiveStep(ProjectConstants.COL_2_MIN_WIDTH, 2, ResponsiveStep.LabelsPosition.TOP));
		add(toolNameField);
		add(manufacturerField);
		add(modelField);
		add(toolInfoField);
		add(snCodeField);
		add(barCodeField);
		add(divider);
		add(companyLayout);
		add(categoryLayout);
		add(statusLayout);
		add(toolUserComboBox);
		add(toolReservedComboBox);
		add(divider2);
		add(datesLayout);
		add(priceAndGuaranteeLayout);
		add(additionalInfo);


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
		binder.forField(companyComboBox)
				.asRequired("Company is required")
//				.withConverter(new CustomConverter.CompanyConverter())
				.bind(Tool::getCompany, Tool::setCompany);
		binder.forField(categoriesComboBox)
				.asRequired("Category is required")
				.withConverter(new CustomConverter.ToolCategoryConverter())
				.bind(Tool::getParentCategory, Tool::setParentCategory);

		binder.forField(toolStatusComboBox)
				.asRequired("Status is required")
				.bind(Tool::getUsageStatus, Tool::setUsageStatus);

		binder.forField(toolUserComboBox)
				.withValidator((s, valueContext) -> {
					if(toolStatusComboBox.getValue().equals(ToolStatus.IN_USE) || toolStatusComboBox.getValue().equals(ToolStatus.RESERVED)) {
						if (toolUserComboBox.getValue() == null) {
							return ValidationResult.error("User required for status: " + toolStatusComboBox.getValue().getStringValue());
						}
					}
					return ValidationResult.ok();
				}).bind(Tool::getUser, Tool::setUser);

		binder.forField(toolReservedComboBox)
				.withValidator((s, valueContext) -> {
					if(toolStatusComboBox.getValue().equals(ToolStatus.RESERVED)) {
						if (toolReservedComboBox.getValue() == null) {
							return ValidationResult.error("User required for status: " + toolStatusComboBox.getValue().getStringValue());
						}
					}
					return ValidationResult.ok();
				}).bind(Tool::getReservedByUser, Tool::setReservedByUser);

		binder.forField(priceField)
				.withConverter(new StringToDoubleConverter("Price must be a number"))
				.withNullRepresentation(0.00)
				.bind(Tool::getPrice, Tool::setPrice);
		binder.forField(guaranteeField)
				.withConverter(new StringToIntegerConverter("Guarantee must be a number"))
				.withNullRepresentation(0)
				.bind(Tool::getGuarantee_months, Tool::setGuarantee_months);

		binder.forField(boughtDateField)
				.withConverter(new LocalDateToDateConverter())
				.withConverter(new DateToSqlDateConverter())
				.bind(Tool::getDateBought, Tool::setDateBought);

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
				.withConverter(new LocalDateToDateConverter())
				.withConverter(new DateToSqlDateConverter())
				.bind(Tool::getDateNextMaintenance, Tool::setDateNextMaintenance);

		binder.forField(additionalInfo)
				.bind(Tool::getAdditionalInfo, Tool::setAdditionalInfo);
	}

	public void setTool(Tool t) {
		tool = t;
		binder.removeBean();

		try {
			binder.readBean(tool);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void updateComboBoxData(Company company) {
		if (company.getId() <= 0) {
			System.err.println("Company ID is <= 0, company name: " + company.getName());
			return;
		}
		categoriesComboBox.setItems(ToolFacade.getInstance().getAllCategoriesInCompanyWithRoot(company.getId()));
		toolUserComboBox.setItems(UserFacade.getInstance().getUsersByCompanyId(company.getId()));
		toolReservedComboBox.setItems(UserFacade.getInstance().getUsersByCompanyId(company.getId()));
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
