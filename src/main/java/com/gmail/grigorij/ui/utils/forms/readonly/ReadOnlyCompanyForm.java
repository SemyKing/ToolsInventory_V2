package com.gmail.grigorij.ui.utils.forms.readonly;

import com.gmail.grigorij.backend.entities.company.Company;
import com.gmail.grigorij.ui.utils.UIUtils;
import com.gmail.grigorij.ui.utils.components.FlexBoxLayout;
import com.gmail.grigorij.ui.utils.css.LumoStyles;
import com.gmail.grigorij.ui.utils.forms.LocationForm;
import com.gmail.grigorij.ui.utils.forms.PersonForm;
import com.gmail.grigorij.utils.ProjectConstants;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ReadOnlyHasValue;

public class ReadOnlyCompanyForm extends FormLayout {

	private Binder<Company> binder = new Binder<>(Company.class);

	private LocationForm locationForm = new LocationForm();
	private PersonForm<Company> personForm = new PersonForm<>();


	public ReadOnlyCompanyForm() {

		TextField companyNameField = UIUtils.getTextFieldTopS("Name", "",  "", null, null, true);
		ReadOnlyHasValue<Company> companyName = new ReadOnlyHasValue<>(company -> {
			companyNameField.setValue( company.getName() );
		});

		TextField companyVATField = UIUtils.getTextFieldTopS("VAT", "",  "", null, null, true);
		ReadOnlyHasValue<Company> companyVAT = new ReadOnlyHasValue<>(company -> {
			companyVATField.setValue( company.getVat() );
		});

		TextField companyStatusField = UIUtils.getTextFieldTopS("Status", "",  "", null, null, true);
		ReadOnlyHasValue<Company> companyStatus = new ReadOnlyHasValue<>(company -> {
			companyStatusField.setValue( company.getVat() );
		});

		//VAT & STATUS
		FlexBoxLayout vatStatusLayout = UIUtils.getFormRowLayout(companyVATField, companyStatusField);

		//ADDRESS FORM

		//CONTACT PERSON FORM

		TextField additionalInfoField = UIUtils.getTextFieldTopS("Additional Info", "",  "", null, null, true);
		ReadOnlyHasValue<Company> additionalInfo = new ReadOnlyHasValue<>(company -> {
			additionalInfoField.setValue( company.getAdditionalInfo() );
		});

		addClassNames(LumoStyles.Padding.Bottom.M, LumoStyles.Padding.Horizontal.M, LumoStyles.Padding.Top.S);
		setResponsiveSteps(
				new FormLayout.ResponsiveStep("0", 1, FormLayout.ResponsiveStep.LabelsPosition.TOP),
				new FormLayout.ResponsiveStep(ProjectConstants.COL_2_MIN_WIDTH, 2, FormLayout.ResponsiveStep.LabelsPosition.TOP));

		add(companyNameField);
		add(vatStatusLayout);
		add(UIUtils.createH4Label("Address"));
		add(locationForm);
		add(UIUtils.createH4Label("Contact Person"));
		add(personForm);
		add(additionalInfoField);

		binder.forField(companyName)
				.bind(company -> company, null);
		binder.forField(companyVAT)
				.bind(company -> company, null);
		binder.forField(companyStatus)
				.bind(company -> company, null);
		binder.forField(additionalInfo)
				.bind(company -> company, null);
	}

	public void setCompany(Company company) {
		try {
			binder.removeBean();
			binder.readBean(company);

			locationForm.setLocation(company.getAddress());
			personForm.setPerson(company);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
