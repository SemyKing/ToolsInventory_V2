package com.gmail.grigorij.ui.utils.forms.admin;

import com.gmail.grigorij.backend.database.facades.CompanyFacade;
import com.gmail.grigorij.backend.database.facades.ToolFacade;
import com.gmail.grigorij.backend.entities.company.Company;
import com.gmail.grigorij.backend.entities.tool.Tool;
import com.gmail.grigorij.backend.entities.tool.ToolStatus;
import com.gmail.grigorij.ui.utils.UIUtils;
import com.gmail.grigorij.ui.utils.components.Divider;
import com.gmail.grigorij.ui.utils.components.FlexBoxLayout;
import com.gmail.grigorij.ui.utils.css.FlexDirection;
import com.gmail.grigorij.ui.utils.css.LumoStyles;
import com.gmail.grigorij.ui.utils.css.size.Horizontal;
import com.gmail.grigorij.ui.utils.css.size.Left;
import com.gmail.grigorij.ui.utils.css.size.Vertical;
import com.gmail.grigorij.ui.views.navigation.admin.AdminInventory;
import com.gmail.grigorij.utils.ProjectConstants;
import com.gmail.grigorij.utils.converters.CustomConverter;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.select.Select;
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

public class AdminToolForm extends FormLayout {

	private Binder<Tool> binder = new Binder<>(Tool.class);

	private Tool tool;
	private boolean isNew;

	private ComboBox<Tool> categoriesComboBox;
	private Button editCategoryButton;


	public AdminToolForm(AdminInventory adminTools) {

		TextField toolNameField = new TextField("Name");

		Select<String> toolVisibility = new Select<>(ProjectConstants.ACTIVE, ProjectConstants.INACTIVE);
		toolVisibility.setWidth("25%");
		toolVisibility.setLabel("Status (Visibility)");

		FlexBoxLayout toolLayout = new FlexBoxLayout();
		toolLayout.setFlexDirection(FlexDirection.ROW);
		toolLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
		toolLayout.add(toolNameField, toolVisibility);
		toolLayout.setComponentMargin(toolVisibility, Left.M);
		toolLayout.setFlexGrow("1", toolNameField);


		TextField manufacturerField = new TextField("Manufacturer");
		TextField modelField = new TextField("Model");
		TextField toolInfoField = new TextField("Tool Info");
		TextField snCodeField = new TextField("SN");
		TextField barCodeField = new TextField("Barcode");

		editCategoryButton = UIUtils.createButton(VaadinIcon.PENCIL);
		editCategoryButton.addClickListener(e -> {
			if (e != null) {
				Tool selectedCategory = categoriesComboBox.getValue();
				if (selectedCategory != null) {
					adminTools.constructToolCategoryDetails(selectedCategory);
				}
			}
		});
		UIUtils.setTooltip("Edit selected category", editCategoryButton);

		ComboBox<Company> companyComboBox = new ComboBox<>();
		companyComboBox.setItems(CompanyFacade.getInstance().getAllCompanies());
		companyComboBox.setItemLabelGenerator(Company::getName);
		companyComboBox.setLabel("Company (Owner)");
		companyComboBox.setRequired(true);
		companyComboBox.addValueChangeListener(e -> {
			if (e != null) {
				if (e.getValue() != null) {
					updateCategoriesProvider(e.getValue());
				}
			}
		});

		categoriesComboBox = new ComboBox<>();
		categoriesComboBox.setItems(ToolFacade.getInstance().getEmptyList());
		categoriesComboBox.setLabel("Category");
		categoriesComboBox.setItemLabelGenerator(Tool::getName);
		categoriesComboBox.setRequired(true);
		categoriesComboBox.addValueChangeListener(e -> {
			if (e != null) {
				if (e.getValue() != null) {
					editCategoryButton.setEnabled(!e.getValue().equals(ToolFacade.getInstance().getRootCategory()));
				}
			}
		});

		FlexBoxLayout categoryLayout = new FlexBoxLayout();
		categoryLayout.setWidth("100%");
		categoryLayout.setFlexDirection(FlexDirection.ROW);
		categoryLayout.add(categoriesComboBox, editCategoryButton);
		categoryLayout.setComponentMargin(editCategoryButton, Left.S);
		categoryLayout.setFlexGrow("1", categoriesComboBox);
		categoryLayout.setAlignItems(FlexComponent.Alignment.BASELINE);


		TextField userField = new TextField("In Use By");

		ComboBox<ToolStatus> toolStatusComboBox = new ComboBox<>();
		toolStatusComboBox.setLabel("Status (Usage)");
		toolStatusComboBox.setItems(EnumSet.allOf(ToolStatus.class));
		toolStatusComboBox.setItemLabelGenerator(ToolStatus::getStringValue);

		TextField reservedByField = new TextField("Reserved By");

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

		UIUtils.setColSpan(2, toolLayout, toolInfoField, companyComboBox, additionalInfo, divider, divider2);

//      Form layout
//		FormLayout formLayout = new FormLayout();
		addClassNames(LumoStyles.Padding.Bottom.L, LumoStyles.Padding.Horizontal.M, LumoStyles.Padding.Top.S);
		setResponsiveSteps(
				new FormLayout.ResponsiveStep("0", 1, FormLayout.ResponsiveStep.LabelsPosition.TOP),
				new FormLayout.ResponsiveStep(ProjectConstants.COL_2_MIN_WIDTH, 2, FormLayout.ResponsiveStep.LabelsPosition.TOP));
		add(toolLayout);    //colspan 2
		add(toolInfoField); //colspan 2
		add(manufacturerField);
		add(modelField);
		add(snCodeField);
		add(barCodeField);
		add(divider);       //colspan 2
		add(companyComboBox); //colspan 2
		add(categoryLayout);
		add(toolStatusComboBox);
		add(userField);
		add(reservedByField);
		add(divider2);      //colspan 2
		add(datesLayout);
		add(priceAndGuaranteeLayout);
		add(additionalInfo); //colspan 2


		binder.forField(toolNameField)
				.asRequired("Name is required")
				.bind(Tool::getName, Tool::setName);
		binder.forField(toolVisibility)
				.asRequired("Status is required")
				.withConverter(new CustomConverter.StatusConverter())
				.bind(Tool::isDeleted, Tool::setDeleted);
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
				.withConverter(new CustomConverter.CompanyConverter())
				.bind(Tool::getCompanyId, Tool::setCompanyId);
		binder.forField(categoriesComboBox)
				.asRequired("Category is required")
				.withConverter(new CustomConverter.ToolCategoryConverter())
				.bind(Tool::getParentCategory, Tool::setParentCategory);
		binder.forField(userField)
				.withConverter(new CustomConverter.UserById())
				.bind(Tool::getInUseByUserId, Tool::setInUseByUserId);
		binder.forField(toolStatusComboBox)
				.asRequired("Status is required")
				.bind(Tool::getUsageStatus, Tool::setUsageStatus);
		binder.forField(reservedByField)
				.withConverter(new CustomConverter.UserById())
				.bind(Tool::getReservedByUserId, Tool::setReservedByUserId);
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
		isNew = false;
		binder.removeBean();

		if (tool == null) {
			tool = Tool.getEmptyTool();
			isNew = true;
		}

		try {
			binder.readBean(tool);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void updateCategoriesProvider(Company company) {
		if (company.getId() <= 0) {
			System.err.println("Company ID is <= 0, company name: " + company.getName());
			return;
		}
		categoriesComboBox.setItems(ToolFacade.getInstance().getAllCategoriesInCompanyWithRoot(company.getId()));
		if (categoriesComboBox.getValue() != null) {
			editCategoryButton.setEnabled(!categoriesComboBox.getValue().equals(ToolFacade.getInstance().getRootCategory()));
		}
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

	public boolean isNew() {
		return isNew;
	}

	public void setNew(boolean b) {
		this.isNew = b;
	}
}
