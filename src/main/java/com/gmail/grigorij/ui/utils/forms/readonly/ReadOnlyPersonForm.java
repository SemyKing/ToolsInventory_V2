package com.gmail.grigorij.ui.utils.forms.readonly;

import com.gmail.grigorij.backend.entities.user.Person;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.data.binder.Binder;

public class ReadOnlyPersonForm extends FormLayout {

	private Binder<Person> binder = new Binder<>(Person.class);

	public ReadOnlyPersonForm() {

	}
}
