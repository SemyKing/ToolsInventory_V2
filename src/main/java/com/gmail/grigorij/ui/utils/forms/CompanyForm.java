package com.gmail.grigorij.ui.utils.forms;

import com.gmail.grigorij.backend.entities.company.Company;
import com.gmail.grigorij.backend.access.Status;
import com.gmail.grigorij.backend.entities.user.Address;
import com.gmail.grigorij.ui.utils.components.Divider;
import com.gmail.grigorij.ui.utils.UIUtils;
import com.gmail.grigorij.ui.utils.css.LumoStyles;
import com.gmail.grigorij.utils.converters.CustomConverter;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.renderer.ComponentRenderer;

import java.util.EnumSet;

public class CompanyForm extends FormLayout {

	private Binder<Company> companyBinder = new Binder<>(Company.class);
	private Binder<Address> addressBinder = new Binder<>(Address.class);
//	private Company company;

	private boolean newCompany;

	public CompanyForm() {

		TextField companyName = new TextField();
		companyName.setWidth("100%");

		TextField companyVAT = new TextField();
		companyVAT.setWidth("100%");

		RadioButtonGroup<Status> status = new RadioButtonGroup<>();
		status.setItems(EnumSet.allOf(Status.class));
		status.setRenderer(new ComponentRenderer<>(eStatus -> new Label(
				eStatus.getStringValue()
		)));

		Label label = UIUtils.createH5Label("Contact Person");

		TextField firstName = new TextField();
		firstName.setWidth("100%");

		TextField lastName = new TextField();
		lastName.setWidth("100%");

		TextField phone = new TextField();
		phone.getElement().setAttribute("type", "tel");
		phone.setWidth("100%");

		TextField email = new TextField();
		email.setWidth("100%");

		Label addressLabel = UIUtils.createH5Label("Address");

		TextField addressLine1 = new TextField();
		addressLine1.setWidth("100%");
		TextField addressLine2 = new TextField();
		addressLine2.setWidth("100%");
		TextField postcode = new TextField();
		postcode.setWidth("100%");
		TextField city = new TextField();
		city.setWidth("100%");
		TextField country = new TextField();
		country.setWidth("100%");

		TextArea additionalInfo = new TextArea();
		additionalInfo.setWidth("100%");
		additionalInfo.setMaxHeight("200px");

		// Form layout
		addClassNames(LumoStyles.Padding.Bottom.M, LumoStyles.Padding.Horizontal.M, LumoStyles.Padding.Top.XS);
		setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1, FormLayout.ResponsiveStep.LabelsPosition.TOP));
		addFormItem(companyName, "Name *");
		addFormItem(companyVAT, "VAT *");
		addFormItem(status, "Status");
		addFormItem(new Divider("1px"), "");
		addFormItem(label, "");
		addFormItem(firstName, "First Name");
		addFormItem(lastName, "Last Name");
		addFormItem(phone, "Phone");
		addFormItem(email, "Email");
		addFormItem(new Divider("1px"), "");
		addFormItem(addressLabel, "");
		addFormItem(addressLine1, "Address Line 1");
		addFormItem(addressLine2, "Address Line 2");
		addFormItem(postcode, "Postcode");
		addFormItem(city, "City");
		addFormItem(country, "Country");
		addFormItem(new Divider("1px"), "");
		addFormItem(additionalInfo, "Additional Info");

//		companyBinder.setBean(company);

		companyBinder.forField(companyName)
				.asRequired("Company name is required")
				.bind(Company::getCompanyName, Company::setCompanyName);
		companyBinder.forField(companyVAT)
				.asRequired("Company VAT is required")
				.bind(Company::getCompanyVAT, Company::setCompanyVAT);
		companyBinder.forField(status)
				.asRequired("Select one")
				.withConverter(new CustomConverter.StatusConverter())
				.bind(Company::isDeleted, Company::setDeleted);
		companyBinder.forField(firstName)
				.bind(Company::getFirstName, Company::setFirstName);
		companyBinder.forField(lastName)
				.bind(Company::getLastName, Company::setLastName);
		companyBinder.forField(phone)
//				.withValidator(new CustomValidator.PhoneNumberValidator("Phone number invalid"))
				.bind(Company::getPhoneNumber, Company::setPhoneNumber);
		companyBinder.forField(email)
//				.withValidator(new EmailValidator("Email address invalid"))
				.bind(Company::getEmail, Company::setEmail);
		companyBinder.forField(additionalInfo)
				.bind(Company::getAdditionalInfo, Company::setAdditionalInfo);

		addressBinder.forField(addressLine1)
				.bind(Address::getAddressLine1, Address::setAddressLine1);
		addressBinder.forField(addressLine2)
				.bind(Address::getAddressLine2, Address::setAddressLine2);
		addressBinder.forField(postcode)
				.bind(Address::getPostcode, Address::setPostcode);
		addressBinder.forField(city)
				.bind(Address::getCity, Address::setCity);
		addressBinder.forField(country)
				.bind(Address::getCountry, Address::setCountry);
	}


	private Company company;

	public void setCompany(Company c) {
		companyBinder.removeBean();
		addressBinder.removeBean();
		newCompany = false;
		company = c;

		if (company == null) {
			company = Company.getEmptyCompany();
			newCompany = true;
		}

		companyBinder.readBean(company);
		addressBinder.readBean(company.getAddress());
	}

	public Company getCompany() {
		try {
			companyBinder.validate();
			if (companyBinder.isValid()) {
				companyBinder.writeBean(company);

				Address address = new Address();
				addressBinder.writeBean(address);
				company.setAddress(address);

				return company;
			}
		} catch (ValidationException e) {
			e.printStackTrace();
			return null;
		}
		return null;
	}

	public boolean isNewCompany() {
		return newCompany;
	}
}
