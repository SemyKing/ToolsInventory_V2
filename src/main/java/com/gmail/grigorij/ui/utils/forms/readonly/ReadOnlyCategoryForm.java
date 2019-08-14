package com.gmail.grigorij.ui.utils.forms.readonly;

import com.gmail.grigorij.backend.database.facades.InventoryFacade;
import com.gmail.grigorij.backend.entities.inventory.InventoryEntity;
import com.gmail.grigorij.ui.utils.UIUtils;
import com.gmail.grigorij.ui.utils.css.LumoStyles;
import com.gmail.grigorij.utils.ProjectConstants;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ReadOnlyHasValue;

public class ReadOnlyCategoryForm extends FormLayout {

	private Binder<InventoryEntity> binder = new Binder<>(InventoryEntity.class);


	public ReadOnlyCategoryForm() {

		TextField categoryNameField = new TextField("Name");
		categoryNameField.setReadOnly(true);
		ReadOnlyHasValue<InventoryEntity> categoryName = new ReadOnlyHasValue<>(category -> {
			categoryNameField.setValue( category.getName() );
		});

		TextField parentNameField = new TextField("Parent Name");
		parentNameField.setReadOnly(true);
		ReadOnlyHasValue<InventoryEntity> parentName = new ReadOnlyHasValue<>(category -> {
			parentNameField.setValue( (category.getParentCategory() == null) ? ProjectConstants.ROOT_CATEGORY : category.getParentCategory().getName() );
		});

		TextField companyField = new TextField("Company (Owner)");
		companyField.setReadOnly(true);
		ReadOnlyHasValue<InventoryEntity> company = new ReadOnlyHasValue<>(category -> {
			companyField.setValue( (category.getCompany() == null) ? "" : category.getCompany().getName() );
		});

		addClassNames(LumoStyles.Padding.Top.S);
		setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1, FormLayout.ResponsiveStep.LabelsPosition.TOP));
		add(categoryNameField);
		add(parentNameField);
		add(companyField);

		binder.forField(categoryName)
				.bind(category -> category, null);
		binder.forField(parentName)
				.bind(category -> category, null);
		binder.forField(company)
				.bind(category -> category, null);
	}

	public void setCategory(InventoryEntity category) {
		try {
			binder.removeBean();
			binder.readBean(category);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
