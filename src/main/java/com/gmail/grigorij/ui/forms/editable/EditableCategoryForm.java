package com.gmail.grigorij.ui.forms.editable;

import com.gmail.grigorij.backend.database.facades.CompanyFacade;
import com.gmail.grigorij.backend.database.facades.InventoryFacade;
import com.gmail.grigorij.backend.entities.company.Company;
import com.gmail.grigorij.backend.entities.inventory.InventoryItem;
import com.gmail.grigorij.backend.enums.inventory.InventoryHierarchyType;
import com.gmail.grigorij.ui.utils.UIUtils;
import com.gmail.grigorij.ui.components.FlexBoxLayout;
import com.gmail.grigorij.utils.ProjectConstants;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.converter.StringToBooleanConverter;

import java.util.List;

public class EditableCategoryForm extends FormLayout {

	private Binder<InventoryItem> binder = new Binder<>(InventoryItem.class);

	private InventoryItem category;
	private Company initialCompany;
	private boolean isNew;

	private ComboBox<InventoryItem> parentsComboBox;

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
		parentsComboBox.setItemLabelGenerator(InventoryItem::getName);
		parentsComboBox.setRequired(true);

//		UIUtils.setColSpan(2, categoryLayout);

//      Form layout
		setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1, FormLayout.ResponsiveStep.LabelsPosition.TOP));
		add(nameStatusLayout);
		add(companiesComboBox);
		add(parentsComboBox);

		binder.forField(nameField)
				.asRequired("Name is required")
				.bind(InventoryItem::getName, InventoryItem::setName);
		binder.forField(status)
				.asRequired("Status is required")
//				.withConverter(new CustomConverter.StatusConverter())
				.withConverter(new StringToBooleanConverter("Error", ProjectConstants.INACTIVE, ProjectConstants.ACTIVE))
				.bind(InventoryItem::isDeleted, InventoryItem::setDeleted);
		binder.forField(companiesComboBox)
				.asRequired("Company is required")
				.bind(InventoryItem::getCompany, InventoryItem::setCompany);
		binder.forField(parentsComboBox)
				.asRequired("Parent Category is required")
//				.withConverter(new CustomConverter.ToolCategoryConverter())
				.bind(InventoryItem::getParentCategory, InventoryItem::setParentCategory);
	}



	public void setCategory(InventoryItem c) {
		category = c;
		isNew = false;
		binder.removeBean();

		if (category == null) {
			category = new InventoryItem();
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

	public InventoryItem getCategory() {
		try {
			binder.validate();

			if (binder.isValid()) {
				binder.writeBean(category);

				/*
				If category's company was changed, it must also be changed for all category children
				 */
				if (initialCompany != category.getCompany()) {
					for (InventoryItem ie : category.getChildren()) {
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
		List<InventoryItem> categories = InventoryFacade.getInstance().getAllCategoriesInCompany(company.getId());
		categories.add(0, InventoryFacade.getInstance().getRootCategory());

		/*
		When editing Category remove same category from Parent Category -> can't set self as parent
		 */
		if (initialCompany != null) {
			categories.removeIf((InventoryItem category) -> category.equals(this.category));
		}
		parentsComboBox.setItems(categories);
	}
}