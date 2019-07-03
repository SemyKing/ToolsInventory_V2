package com.gmail.grigorij.ui.utils.forms;

import com.gmail.grigorij.backend.entities.user.Person;
import com.gmail.grigorij.utils.ProjectConstants;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;

public class PersonForm<T extends Person> extends FormLayout {

	private Binder<T> binder = new Binder<>();
	private T person;

	public PersonForm() {
		TextField firstName = new TextField("First Name");
		TextField lastName = new TextField("Last Name");
		TextField phone = new TextField("Phone");
		phone.getElement().setAttribute("type", "tel");
		TextField email = new TextField("Email");

		// Form layout
//		addClassNames(LumoStyles.Padding.Bottom.M, LumoStyles.Padding.Horizontal.M, LumoStyles.Padding.Top.XS);
		setResponsiveSteps(
				new FormLayout.ResponsiveStep("0", 1, FormLayout.ResponsiveStep.LabelsPosition.TOP),
				new FormLayout.ResponsiveStep(ProjectConstants.COL_2_MIN_WIDTH, 2, FormLayout.ResponsiveStep.LabelsPosition.TOP));
		add(firstName);
		add(lastName);
		add(phone);
		add(email);

		binder.forField(firstName)
				.bind(Person::getFirstName, Person::setFirstName);
		binder.forField(lastName)
				.bind(Person::getLastName, Person::setLastName);
		binder.forField(phone)
//				.withValidator(new CustomValidator.PhoneNumberValidator("Phone number invalid"))
				.bind(Person::getPhoneNumber, Person::setPhoneNumber);
		binder.forField(email)
//				.withValidator(new EmailValidator("Email address invalid"))
				.bind(Person::getEmail, Person::setEmail);
	}

	public void setPerson(T person) {
		this.person = person;
		binder.readBean(this.person);
	}

	public <T> T getPerson() {
		try {
			binder.validate();
			if (binder.isValid()) {
				binder.writeBean(this.person);
				return (T) this.person;
			}
		} catch (ValidationException e) {
			e.printStackTrace();
			return null;
		}
		return null;
	}
}
