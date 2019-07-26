package com.gmail.grigorij.ui.utils.forms.readonly;

import com.gmail.grigorij.backend.entities.company.Company;
import com.gmail.grigorij.ui.utils.UIUtils;
import com.gmail.grigorij.ui.utils.components.Divider;
import com.gmail.grigorij.ui.utils.components.FlexBoxLayout;
import com.gmail.grigorij.ui.utils.css.LumoStyles;
import com.gmail.grigorij.ui.utils.css.size.Horizontal;
import com.gmail.grigorij.ui.utils.css.size.Vertical;
import com.gmail.grigorij.utils.ProjectConstants;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ReadOnlyHasValue;

public class ReadOnlyCompanyForm extends FormLayout {

	private Binder<Company> binder = new Binder<>(Company.class);

	private ReadOnlyLocationForm locationForm = new ReadOnlyLocationForm();
	private ReadOnlyPersonForm personForm = new ReadOnlyPersonForm();


	public ReadOnlyCompanyForm() {

		TextField nameField = new TextField("Name");
		nameField.setReadOnly(true);
		ReadOnlyHasValue<Company> name = new ReadOnlyHasValue<>(company -> {
			nameField.setValue( company.getName() );
		});

		TextField vatField = new TextField("VAT");
		vatField.setReadOnly(true);
		ReadOnlyHasValue<Company> vat = new ReadOnlyHasValue<>(company -> {
			vatField.setValue( company.getVat() );
		});

		TextField statusField = new TextField("Status");
		statusField.setReadOnly(true);
		ReadOnlyHasValue<Company> status = new ReadOnlyHasValue<>(company -> {
			statusField.setValue( company.getVat() );
		});

		//VAT & STATUS
		FlexBoxLayout vatStatusLayout = UIUtils.getFormRowLayout(vatField, statusField, true);

		//ADDRESS FORM

		//CONTACT PERSON FORM

		TextField additionalInfoField = new TextField("Additional Info");
		ReadOnlyHasValue<Company> additionalInfo = new ReadOnlyHasValue<>(company -> {
			additionalInfoField.setValue( company.getAdditionalInfo() );
		});

//		UIUtils.setColSpan(2, locationForm, personForm);

		addClassNames(LumoStyles.Padding.Bottom.S, LumoStyles.Padding.Top.S);
		setResponsiveSteps(
				new FormLayout.ResponsiveStep("0", 1, FormLayout.ResponsiveStep.LabelsPosition.TOP),
				new FormLayout.ResponsiveStep(ProjectConstants.COL_2_MIN_WIDTH, 2, FormLayout.ResponsiveStep.LabelsPosition.TOP));

		add(nameField);
		add(vatStatusLayout);
		add(new Divider(2, Horizontal.NONE, Vertical.S));
		add(UIUtils.createH4Label("Address"));
		add(locationForm);
		add(new Divider(2, Horizontal.NONE, Vertical.S));
		add(UIUtils.createH4Label("Contact Person"));
		add(personForm);
		add(additionalInfoField);

		binder.forField(name)
				.bind(company -> company, null);
		binder.forField(vat)
				.bind(company -> company, null);
		binder.forField(status)
				.bind(company -> company, null);
		binder.forField(additionalInfo)
				.bind(company -> company, null);
	}

	public void setCompany(Company company) {
		try {
			binder.removeBean();
			binder.readBean(company);

			locationForm.setLocation(company.getAddress());
			personForm.setPerson(company.getContactPerson());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
