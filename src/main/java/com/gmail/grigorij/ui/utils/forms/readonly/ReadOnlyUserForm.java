package com.gmail.grigorij.ui.utils.forms.readonly;

import com.gmail.grigorij.backend.entities.user.User;
import com.gmail.grigorij.ui.utils.css.LumoStyles;
import com.gmail.grigorij.utils.ProjectConstants;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ReadOnlyHasValue;

public class ReadOnlyUserForm extends FormLayout {

	private Binder<User> binder = new Binder<>(User.class);


	public ReadOnlyUserForm() {

		TextField usernameField = new TextField("Username");
		usernameField.setReadOnly(true);
		ReadOnlyHasValue<User> username = new ReadOnlyHasValue<>(user -> {
			usernameField.setValue(user.getUsername());
		});

		TextField statusField = new TextField("Status");
		usernameField.setReadOnly(true);
		ReadOnlyHasValue<User> status = new ReadOnlyHasValue<>(user -> {
			statusField.setValue((user.isDeleted()) ? ProjectConstants.INACTIVE : ProjectConstants.ACTIVE);
		});

		TextField companyField = new TextField("Company");
		companyField.setReadOnly(true);
		ReadOnlyHasValue<User> company = new ReadOnlyHasValue<>(user -> {
			companyField.setValue((user.getCompany() == null) ? "" : user.getCompany().getName());
		});


		addClassNames(LumoStyles.Padding.Bottom.S, LumoStyles.Padding.Top.S);
		setResponsiveSteps(
				new FormLayout.ResponsiveStep("0", 1, FormLayout.ResponsiveStep.LabelsPosition.TOP),
				new FormLayout.ResponsiveStep(ProjectConstants.COL_2_MIN_WIDTH, 2, FormLayout.ResponsiveStep.LabelsPosition.TOP));

		add(usernameField);
		add(companyField);

		binder.forField(username)
				.bind(user -> user, null);
		binder.forField(company)
				.bind(user -> user, null);
		binder.forField(status)
				.bind(user -> user, null);
	}

	public void setUser(User user) {
		try {
			binder.removeBean();
			binder.readBean(user);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
