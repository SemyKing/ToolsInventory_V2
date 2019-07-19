package com.gmail.grigorij.ui.utils.forms.admin;

import com.gmail.grigorij.backend.database.facades.CompanyFacade;
import com.gmail.grigorij.backend.database.facades.ToolFacade;
import com.gmail.grigorij.backend.entities.company.Company;
import com.gmail.grigorij.backend.entities.inventory.HierarchyType;
import com.gmail.grigorij.backend.entities.inventory.InventoryEntity;
import com.gmail.grigorij.ui.utils.components.FlexBoxLayout;
import com.gmail.grigorij.ui.utils.css.FlexDirection;
import com.gmail.grigorij.ui.utils.css.size.Left;
import com.gmail.grigorij.utils.ProjectConstants;
import com.gmail.grigorij.utils.converters.CustomConverter;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;

import java.util.List;

public class AdminToolCategoryForm extends FormLayout {

	private Binder<InventoryEntity> binder = new Binder<>(InventoryEntity.class);

	private InventoryEntity category;
	private boolean isNew;

	private ComboBox<InventoryEntity> categoriesComboBox;

	public AdminToolCategoryForm() {
		TextField categoryNameField = new TextField("Name");
		categoryNameField.setRequired(true);

		Select<String> categoryStatus = new Select<>(ProjectConstants.ACTIVE, ProjectConstants.INACTIVE);
		categoryStatus.setWidth("25%");
		categoryStatus.setLabel("Status");

		FlexBoxLayout nameLayout = new FlexBoxLayout();
		nameLayout.setFlexDirection(FlexDirection.ROW);
		nameLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
		nameLayout.add(categoryNameField, categoryStatus);
		nameLayout.setComponentMargin(categoryStatus, Left.M);
		nameLayout.setFlexGrow("1", categoryNameField);


		categoriesComboBox = new ComboBox<>();

		ComboBox<Company> companyComboBox = new ComboBox<>();
		companyComboBox.setItems(CompanyFacade.getInstance().getAllCompanies());
		companyComboBox.setItemLabelGenerator(Company::getName);
		companyComboBox.setLabel("Company");
		companyComboBox.setRequired(true);
		companyComboBox.addValueChangeListener(e -> {
			if (e != null) {
				if (e.getValue() != null) {
					categoriesComboBox.setValue(null);
					updateCategoriesComboBoxData(e.getValue());
				}
			}
		});

		categoriesComboBox.setItems(ToolFacade.getInstance().getEmptyList());
		categoriesComboBox.setLabel("Parent Category");
		categoriesComboBox.setItemLabelGenerator(InventoryEntity::getName);
		categoriesComboBox.setRequired(true);

//		UIUtils.setColSpan(2, categoryLayout);

//      Form layout
		setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1, FormLayout.ResponsiveStep.LabelsPosition.TOP));
		add(nameLayout);
		add(companyComboBox);
		add(categoriesComboBox);

		binder.forField(categoryNameField)
				.asRequired("Name is required")
				.bind(InventoryEntity::getName, InventoryEntity::setName);
		binder.forField(categoryStatus)
				.asRequired("Status is required")
				.withConverter(new CustomConverter.StatusConverter())
				.bind(InventoryEntity::isDeleted, InventoryEntity::setDeleted);
		binder.forField(companyComboBox)
				.asRequired("Company is required")
				.bind(InventoryEntity::getCompany, InventoryEntity::setCompany);
		binder.forField(categoriesComboBox)
				.asRequired("Parent Category is required")
				.withConverter(new CustomConverter.ToolCategoryConverter())
				.bind(InventoryEntity::getParentCategory, InventoryEntity::setParentCategory);
	}

	private Company initialCompany;

	public void setCategory(InventoryEntity c) {
		category = c;
		isNew = false;
		binder.removeBean();

		if (category == null) {
			category = InventoryEntity.getEmptyTool();
			isNew = true;
		}
		category.setHierarchyType(HierarchyType.CATEGORY);

		try {
			initialCompany = category.getCompany();

			binder.readBean(category);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public InventoryEntity getCategory() {
		try {
			binder.validate();

			if (binder.isValid()) {
				binder.writeBean(category);

				/*
				If category company was changed, it must also be changed for all category children
				 */
				if (initialCompany != category.getCompany()) {
					for (InventoryEntity tool : category.getChildren()) {
						tool.setCompany(category.getCompany());
					}
				}

				System.out.println("getCategory()");
				System.out.println("parentCategory: " + category.getParentCategory());
				if (category.getParentCategory() != null)
					System.out.println("parentCategory name: " + category.getParentCategory().getName());

				return category;
			}
		} catch (ValidationException e) {
			e.printStackTrace();
			return null;
		}
		return null;
	}

	private void updateCategoriesComboBoxData(Company company) {
		System.out.println("updateCategoriesComboBoxData()");
		if (company == null) {
			System.err.println("Company is NULL (should be impossible), updateCategoriesComboBoxData(), " + this.getClass().getSimpleName());
			return;
		}

		List<InventoryEntity> categories = ToolFacade.getInstance().getAllCategoriesInCompanyWithRoot(company.getId());

		if (initialCompany != null) {
			categories.removeIf((InventoryEntity category) -> category.getId().equals(initialCompany.getId()));
		}
		categoriesComboBox.setItems(categories);
	}

	public boolean isNew() {
		return isNew;
	}
}