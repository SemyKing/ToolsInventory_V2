package com.gmail.grigorij.ui.utils.forms.editable;

import com.gmail.grigorij.backend.entities.embeddable.Person;
import com.gmail.grigorij.ui.utils.css.LumoStyles;
import com.gmail.grigorij.utils.ProjectConstants;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;

public class EditablePersonForm extends FormLayout {

	private Binder<Person> binder = new Binder<>(Person.class);
	private Person person;

	public EditablePersonForm() {

		TextField firstName = new TextField("First Name");

		TextField lastName = new TextField("Last Name");

		TextField phone = new TextField("Phone");
		phone.getElement().setAttribute("type", "tel");

		TextField email = new TextField("Email");

		// Form layout
		addClassNames(LumoStyles.Padding.Bottom.S, LumoStyles.Padding.Top.S);
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
				.bind(Person::getPhoneNumber, Person::setPhoneNumber);
		binder.forField(email)
				.bind(Person::getEmail, Person::setEmail);
	}

	public void setPerson(Person person) {
		this.person = person;
		binder.readBean(this.person);
	}

	public Person getPerson() {
		try {
			binder.validate();
			if (binder.isValid()) {
				binder.writeBean(person);

				return person;
			}
		} catch (ValidationException e) {
			e.printStackTrace();
			return null;
		}
		return null;
	}
}
