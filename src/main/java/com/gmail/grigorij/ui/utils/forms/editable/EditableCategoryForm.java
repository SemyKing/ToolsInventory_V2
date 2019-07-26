package com.gmail.grigorij.ui.utils.forms.editable;

import com.gmail.grigorij.backend.database.facades.CompanyFacade;
import com.gmail.grigorij.backend.database.facades.InventoryFacade;
import com.gmail.grigorij.backend.entities.company.Company;
import com.gmail.grigorij.backend.entities.inventory.InventoryEntity;
import com.gmail.grigorij.backend.entities.inventory.InventoryHierarchyType;
import com.gmail.grigorij.ui.utils.UIUtils;
import com.gmail.grigorij.ui.utils.components.FlexBoxLayout;
import com.gmail.grigorij.utils.ProjectConstants;
import com.gmail.grigorij.utils.converters.CustomConverter;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;

import java.util.List;

public class EditableCategoryForm extends FormLayout {

	private Binder<InventoryEntity> binder = new Binder<>(InventoryEntity.class);

	private InventoryEntity category;
	private Company initialCompany;
	private boolean isNew;

	private ComboBox<InventoryEntity> parentsComboBox;

	public EditableCategoryForm() {

		TextField nameField = new TextField("Name");
		nameField.setRequired(true);

		Select<String> status = new Select<>(ProjectConstants.ACTIVE, ProjectConstants.INACTIVE);
		status.setWidth("25%");
		status.setLabel("Status");

		//NAME & STATUS
		FlexBoxLayout nameStatusLayout = UIUtils.getFormRowLayout(nameField, status, false);

		parentsComboBox = new ComboBox<>();

		ComboBox<Company> companiesComboBox = new ComboBox<>();
		companiesComboBox.setItems(CompanyFacade.getInstance().getAllCompanies());
		companiesComboBox.setItemLabelGenerator(Company::getName);
		companiesComboBox.setLabel("Company");
		companiesComboBox.setRequired(true);
		companiesComboBox.addValueChangeListener(e -> {
			if (e != null) {
				if (e.getValue() != null) {
					parentsComboBox.setValue(null);
					updateCategoriesComboBoxData(e.getValue());
				}
			}
		});

		parentsComboBox.setItems();
		parentsComboBox.setLabel("Parent Category");
		parentsComboBox.setItemLabelGenerator(InventoryEntity::getName);
		parentsComboBox.setRequired(true);

//		UIUtils.setColSpan(2, categoryLayout);

//      Form layout
		setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1, FormLayout.ResponsiveStep.LabelsPosition.TOP));
		add(nameStatusLayout);
		add(companiesComboBox);
		add(parentsComboBox);

		binder.forField(nameField)
				.asRequired("Name is required")
				.bind(InventoryEntity::getName, InventoryEntity::setName);
		binder.forField(status)
				.asRequired("Status is required")
				.withConverter(new CustomConverter.StatusConverter())
				.bind(InventoryEntity::isDeleted, InventoryEntity::setDeleted);
		binder.forField(companiesComboBox)
				.asRequired("Company is required")
				.bind(InventoryEntity::getCompany, InventoryEntity::setCompany);
		binder.forField(parentsComboBox)
				.asRequired("Parent Category is required")
//				.withConverter(new CustomConverter.ToolCategoryConverter())
				.bind(InventoryEntity::getParentCategory, InventoryEntity::setParentCategory);
	}



	public void setCategory(InventoryEntity c) {
		category = c;
		isNew = false;
		binder.removeBean();

		if (category == null) {
//			category = InventoryEntity.getEmptyTool();
			category = new InventoryEntity();
			isNew = true;
		}
		category.setInventoryHierarchyType(InventoryHierarchyType.CATEGORY);

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
				If category's company was changed, it must also be changed for all category children
				 */
				if (initialCompany != category.getCompany()) {
					for (InventoryEntity ie : category.getChildren()) {
						ie.setCompany(category.getCompany());
					}
				}

				if (category.getParentCategory().equals(InventoryFacade.getInstance().getRootCategory())) {
					category.setParentCategory(null);
				}

				return category;
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

	private void updateCategoriesComboBoxData(Company company) {
		List<InventoryEntity> categories = InventoryFacade.getInstance().getAllCategoriesInCompany(company.getId());
		categories.add(0, InventoryFacade.getInstance().getRootCategory());

		/*
		When editing Category remove same category from Parent Category -> can't set self as parent
		 */
		if (initialCompany != null) {
			categories.removeIf((InventoryEntity category) -> category.equals(this.category));
		}
		parentsComboBox.setItems(categories);
	}
}