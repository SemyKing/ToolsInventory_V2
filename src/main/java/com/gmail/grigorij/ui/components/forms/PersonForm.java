package com.gmail.grigorij.ui.components.forms;

import com.gmail.grigorij.backend.database.entities.embeddable.Person;
import com.gmail.grigorij.backend.database.facades.UserFacade;
import com.gmail.grigorij.utils.ProjectConstants;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.validator.EmailValidator;

import java.util.ArrayList;
import java.util.List;

public class PersonForm extends FormLayout {

	private final String CLASS_NAME = "form";

	private Binder<Person> binder = new Binder<>(Person.class);
	private Person person, originalPerson;
	private boolean isNew;
	private String initialEmail;


	// FORM ITEMS
	private TextField firstNameField;
	private TextField lastNameField;
	private TextField phoneField;
	private EmailField emailField;


	public PersonForm() {
		addClassName(CLASS_NAME);

		constructFormItems();

		constructForm();

		constructBinder();
	}


	private void constructFormItems() {
		firstNameField = new TextField("First Name");
		firstNameField.setRequired(true);

		lastNameField = new TextField("Last Name");
		lastNameField.setRequired(true);

		phoneField = new TextField("Phone");
		phoneField.getElement().setAttribute("type", "tel");

		emailField = new EmailField("Email");
	}

	private void constructForm() {
		setResponsiveSteps(
				new FormLayout.ResponsiveStep("0", 1, FormLayout.ResponsiveStep.LabelsPosition.TOP),
				new FormLayout.ResponsiveStep(ProjectConstants.COL_2_MIN_WIDTH, 2, FormLayout.ResponsiveStep.LabelsPosition.TOP));
		add(firstNameField);
		add(lastNameField);
		add(phoneField);
		add(emailField);
	}

	private void constructBinder() {
		binder.forField(firstNameField)
				.bind(Person::getFirstName, Person::setFirstName);
		binder.forField(lastNameField)
				.bind(Person::getLastName, Person::setLastName);
		binder.forField(phoneField)
				.bind(Person::getPhoneNumber, Person::setPhoneNumber);

		binder.forField(emailField)
				.asRequired("Email is required")
				.withValidator(new EmailValidator("Email invalid"))
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
				}, "Email address already exists")
				.bind(Person::getEmail, Person::setEmail);
	}


	private void initDynamicFormItems() {
		initialEmail = this.person.getEmail();
	}


	public void setPerson(Person person) {
		isNew = false;

		if (person == null) {
			this.person = new Person();
			isNew = true;
		} else {
			this.person = person;
		}

		initDynamicFormItems();

		originalPerson = new Person(this.person);

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

	public List<String> getChanges() {
		List<String> changes = new ArrayList<>();

		if (!originalPerson.getFirstName().equals(person.getFirstName())) {
			changes.add("First Name changed from: '" + originalPerson.getFirstName() + "', to: '" + person.getFirstName() + "'");
		}
		if (!originalPerson.getLastName().equals(person.getLastName())) {
			changes.add("Last Name changed from: '" + originalPerson.getLastName() + "', to: '" + person.getLastName() + "'");
		}
		if (!originalPerson.getPhoneNumber().equals(person.getPhoneNumber())) {
			changes.add("Phone Number changed from: '" + originalPerson.getPhoneNumber() + "', to: '" + person.getPhoneNumber() + "'");
		}
		if (!originalPerson.getEmail().equals(person.getEmail())) {
			changes.add("Email changed from: '" + originalPerson.getEmail() + "', to: '" + person.getEmail() + "'");
		}

		return changes;
	}
}
