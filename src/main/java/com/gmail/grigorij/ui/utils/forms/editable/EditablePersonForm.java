package com.gmail.grigorij.ui.utils.forms.editable;

import com.gmail.grigorij.backend.database.facades.UserFacade;
import com.gmail.grigorij.backend.embeddable.Person;
import com.gmail.grigorij.backend.entities.user.User;
import com.gmail.grigorij.ui.utils.css.LumoStyles;
import com.gmail.grigorij.utils.ProjectConstants;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;

public class EditablePersonForm extends FormLayout {

	private Binder<Person> binder = new Binder<>(Person.class);
	private Person person;
	private boolean isNew;
	private String initialEmail;

	public EditablePersonForm() {

		TextField firstNameField = new TextField("First Name");

		TextField lastNameField = new TextField("Last Name");

		TextField phoneField = new TextField("Phone");
		phoneField.getElement().setAttribute("type", "tel");

		TextField emailField = new TextField("Email");

		// Form layout
		addClassNames(LumoStyles.Padding.Bottom.S, LumoStyles.Padding.Top.S);
		setResponsiveSteps(
				new FormLayout.ResponsiveStep("0", 1, FormLayout.ResponsiveStep.LabelsPosition.TOP),
				new FormLayout.ResponsiveStep(ProjectConstants.COL_2_MIN_WIDTH, 2, FormLayout.ResponsiveStep.LabelsPosition.TOP));
		add(firstNameField);
		add(lastNameField);
		add(phoneField);
		add(emailField);

		binder.forField(firstNameField)
				.bind(Person::getFirstName, Person::setFirstName);
		binder.forField(lastNameField)
				.bind(Person::getLastName, Person::setLastName);
		binder.forField(phoneField)
				.bind(Person::getPhoneNumber, Person::setPhoneNumber);

		binder.forField(emailField)
				.asRequired("Email is required")
				.withValidator(em -> {
					if (isNew) {
						return UserFacade.getInstance().isEmailAvailable(em);
					} else {
						if (!initialEmail.equals(emailField.getValue())) {
							return UserFacade.getInstance().isEmailAvailable(em);
						} else {
							return true;
						}
					}
				}, "Email already taken")
				.bind(Person::getEmail, Person::setEmail);
	}

	public void setPerson(Person p) {
		person = p;

		if (person == null) {
			person = new Person();
			isNew = true;
		}

		initialEmail = person.getEmail();

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
