package com.gmail.grigorij.ui.components.forms;

import com.gmail.grigorij.backend.database.facades.CompanyFacade;
import com.gmail.grigorij.backend.database.facades.InventoryFacade;
import com.gmail.grigorij.backend.database.entities.Company;
import com.gmail.grigorij.backend.database.entities.inventory.InventoryItem;
import com.gmail.grigorij.backend.database.enums.inventory.InventoryHierarchyType;
import com.gmail.grigorij.backend.database.enums.permissions.PermissionLevel;
import com.gmail.grigorij.utils.AuthenticationService;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;

import java.util.ArrayList;
import java.util.List;

public class CategoryForm extends FormLayout {

	private final String CLASS_NAME = "form";

	private Binder<InventoryItem> binder;

	private InventoryItem category, originalCategory;
//	private long initialCompanyId = -1L;
	private boolean isNew;

	// FORM ITEMS
	private TextField nameField;
	private ComboBox<Company> companyComboBox;
	private ComboBox<InventoryItem> parentCategoryComboBox;


	public CategoryForm() {
		addClassName(CLASS_NAME);

		constructFormItems();

		constructForm();

		constructBinder();
	}


	private void constructFormItems() {
		nameField = new TextField("Name");
		nameField.setRequired(true);

		parentCategoryComboBox = new ComboBox<>();
		parentCategoryComboBox.setItems();
		parentCategoryComboBox.setLabel("Parent Category");
		parentCategoryComboBox.setRequired(true);
		parentCategoryComboBox.setItemLabelGenerator(InventoryItem::getName);

		companyComboBox = new ComboBox<>();
		companyComboBox.setLabel("Company");
		companyComboBox.setRequired(true);
		companyComboBox.setItems(CompanyFacade.getInstance().getAllCompanies());
		companyComboBox.setItemLabelGenerator(Company::getName);
		companyComboBox.addValueChangeListener(e -> {
			if (e != null) {
				if (e.getValue() != null) {
					parentCategoryComboBox.setValue(null);
					updateCategoriesComboBoxData(e.getValue());
				}
			}
		});

		if (AuthenticationService.getCurrentSessionUser().getPermissionLevel().lowerThan(PermissionLevel.SYSTEM_ADMIN)) {
			companyComboBox.setReadOnly(true);
			companyComboBox.getElement().getStyle().set("display", "none");
		}
	}

	private void constructForm() {
		setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1, FormLayout.ResponsiveStep.LabelsPosition.TOP));
		add(nameField);
		add(companyComboBox);
		add(parentCategoryComboBox);
	}

	private void constructBinder() {
		binder = new Binder<>(InventoryItem.class);

		binder.forField(nameField)
				.asRequired("Name is required")
				.bind(InventoryItem::getName, InventoryItem::setName);
		binder.forField(companyComboBox)
				.asRequired("Company is required")
				.bind(InventoryItem::getCompany, InventoryItem::setCompany);
		binder.forField(parentCategoryComboBox)
				.asRequired("Parent Category is required")
				.withNullRepresentation(InventoryFacade.getInstance().getRootCategory())
				.bind(InventoryItem::getParentCategory, InventoryItem::setParentCategory);
	}


	private void initDynamicFormItems() {
		companyComboBox.setReadOnly(true);

		if (isNew) {
			category.setCompany(AuthenticationService.getCurrentSessionUser().getCompany());

			if (AuthenticationService.getCurrentSessionUser().getPermissionLevel().equalsTo(PermissionLevel.SYSTEM_ADMIN)) {
				companyComboBox.setReadOnly(false);
			}
		}
	}

	private void updateCategoriesComboBoxData(Company company) {
		List<InventoryItem> categories = InventoryFacade.getInstance().getAllInCompanyByType(company.getId(), InventoryHierarchyType.CATEGORY);
		categories.add(0, InventoryFacade.getInstance().getRootCategory());

		/*
		When editing Category remove same category from Parent Category -> can't set self as parent
		 */
		categories.removeIf(category -> category.getId().equals(this.category.getId()));

		parentCategoryComboBox.setItems(categories);
	}


	public void setCategory(InventoryItem category) {
		isNew = false;

		if (category == null) {
			this.category = new InventoryItem();
			isNew = true;
		} else {
			this.category = category;
		}

		this.category.setInventoryHierarchyType(InventoryHierarchyType.CATEGORY);

		initDynamicFormItems();

		binder.readBean(category);

		originalCategory = new InventoryItem(this.category);
	}

	public InventoryItem getCategory() {
		try {
			binder.validate();

			if (binder.isValid()) {

				/*
				If category's company was changed, it must also be changed for all category children
				 */

//				NO COMPANY CHANGING.

//				if (initialCompanyId != category.getCompany().getId()) {
//					for (InventoryItem child : InventoryFacade.getInstance().getAllByParentId(category.getId())) {
//						child.setCompany(category.getCompany());
//
//						InventoryFacade.getInstance().update(child);
//					}
//				}

				binder.writeBean(category);

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

	public List<String> getChanges() {
		List<String> changes = new ArrayList<>();

		if (!originalCategory.getName().equals(category.getName())) {
			changes.add("Category name changed from: '" + originalCategory.getName() + "', to: '" + category.getName() + "'");
		}
		if (!originalCategory.getCompany().equals(category.getCompany())) {
			changes.add("Category company changed from: '" + originalCategory.getCompany().getName() + "', to: '" + category.getCompany().getName() + "'");
		}

		if (originalCategory.getParentCategory() != null || category.getParentCategory() != null) {
			changes.add("Category parent changed from: '" +
					(originalCategory.getParentCategory()==null ? "" : originalCategory.getParentCategory().getName()) +
					"', to: '" +
					(category.getParentCategory()==null ? "" : category.getParentCategory().getName()) + "'");
		}

		return changes;
	}
}