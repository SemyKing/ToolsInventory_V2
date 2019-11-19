package com.gmail.grigorij.ui.components.forms;

import com.gmail.grigorij.backend.database.entities.Category;
import com.gmail.grigorij.backend.database.enums.permissions.PermissionLevel;
import com.gmail.grigorij.utils.authentication.AuthenticationService;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ReadOnlyHasValue;
import com.vaadin.flow.data.binder.ValidationException;

import java.util.ArrayList;
import java.util.List;

public class CategoryForm extends FormLayout {

//	private final String CLASS_NAME = "form";

	private Binder<Category> binder;

	private Category category, originalCategory;
	private boolean isNew;

	// FORM ITEMS
	private TextField nameField;
	private TextField companyField;

	private ReadOnlyHasValue<Category> company;


	public CategoryForm() {
//		addClassName(CLASS_NAME);

		constructFormItems();

		constructForm();

		constructBinder();
	}


	private void constructFormItems() {
		nameField = new TextField("Name");
		nameField.setRequired(true);

		companyField = new TextField("Company");
		companyField.setReadOnly(true);
		company = new ReadOnlyHasValue<>(category -> {
			companyField.setValue(category.getCompany() == null ? "" : category.getCompany().getName());
		});
	}

	private void constructForm() {
		setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1, FormLayout.ResponsiveStep.LabelsPosition.TOP));
		add(nameField);
		if (AuthenticationService.getCurrentSessionUser().getPermissionLevel().equalsTo(PermissionLevel.SYSTEM_ADMIN)) {
			add(companyField);
		}
	}

	private void constructBinder() {
		binder = new Binder<>(Category.class);

		binder.forField(nameField)
				.asRequired("Name is required")
				.bind(Category::getName, Category::setName);
		binder.forField(company)
				.bind(category -> category, null);
	}


	private void initDynamicFormItems() {
		if (isNew) {
			companyField.setValue(AuthenticationService.getCurrentSessionUser().getCompany().getName());
			category.setCompany(AuthenticationService.getCurrentSessionUser().getCompany());
		}
	}


	public void setCategory(Category category) {
		isNew = false;

		if (category == null) {
			this.category = new Category();
			isNew = true;
		} else {
			this.category = category;
		}

		originalCategory = new Category(this.category);

		binder.readBean(this.category);

		initDynamicFormItems();
	}

	public Category getCategory() {
		try {
			binder.validate();

			if (binder.isValid()) {
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
		if (!originalCategory.getCompanyString().equals(category.getCompanyString())) {
			changes.add("Category company changed from: '" + originalCategory.getCompany().getName() + "', to: '" + category.getCompany().getName() + "'");
		}

		return changes;
	}
}