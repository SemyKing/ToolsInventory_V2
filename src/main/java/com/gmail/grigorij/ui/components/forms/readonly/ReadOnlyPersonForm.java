package com.gmail.grigorij.ui.components.forms.readonly;

import com.gmail.grigorij.backend.embeddable.Person;
import com.gmail.grigorij.ui.utils.css.LumoStyles;
import com.gmail.grigorij.utils.ProjectConstants;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ReadOnlyHasValue;

public class ReadOnlyPersonForm extends FormLayout {

	private Binder<Person> binder = new Binder<>(Person.class);


	public ReadOnlyPersonForm() {

		TextField firstNameField = new TextField("First Name");
		firstNameField.setReadOnly(true);
		ReadOnlyHasValue<Person> firstName = new ReadOnlyHasValue<>(person -> {
			firstNameField.setValue( person.getFirstName() );
		});

		TextField lastNameField = new TextField("Last Name");
		lastNameField.setReadOnly(true);
		ReadOnlyHasValue<Person> lastName = new ReadOnlyHasValue<>(person -> {
			lastNameField.setValue( person.getLastName() );
		});

		TextField phoneField = new TextField("Phone");
		phoneField.setReadOnly(true);
		ReadOnlyHasValue<Person> phone = new ReadOnlyHasValue<>(person -> {
			phoneField.setValue( person.getPhoneNumber() );
		});

		TextField emailField = new TextField("Email");
		emailField.setReadOnly(true);
		ReadOnlyHasValue<Person> email = new ReadOnlyHasValue<>(person -> {
			emailField.setValue( person.getEmail() );
		});


		addClassNames(LumoStyles.Padding.Bottom.S, LumoStyles.Padding.Top.S);
		setResponsiveSteps(
				new FormLayout.ResponsiveStep("0", 1, FormLayout.ResponsiveStep.LabelsPosition.TOP),
				new FormLayout.ResponsiveStep(ProjectConstants.COL_2_MIN_WIDTH, 2, FormLayout.ResponsiveStep.LabelsPosition.TOP));

		add(firstNameField);
		add(lastNameField);
		add(phoneField);
		add(emailField);

		binder.forField(firstName)
				.bind(person -> person, null);
		binder.forField(lastName)
				.bind(person -> person, null);
		binder.forField(phone)
				.bind(person -> person, null);
		binder.forField(email)
				.bind(person -> person, null);
	}

	public void setPerson(Person person) {
		try {
			binder.removeBean();
			binder.readBean(person);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
