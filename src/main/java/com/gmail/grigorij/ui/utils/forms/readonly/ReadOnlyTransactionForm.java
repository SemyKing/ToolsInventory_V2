package com.gmail.grigorij.ui.utils.forms.readonly;

import com.gmail.grigorij.backend.entities.transaction.Transaction;
import com.gmail.grigorij.backend.enums.transactions.TransactionType;
import com.gmail.grigorij.ui.utils.UIUtils;
import com.gmail.grigorij.ui.utils.components.CustomDialog;
import com.gmail.grigorij.ui.utils.components.Divider;
import com.gmail.grigorij.ui.utils.components.FlexBoxLayout;
import com.gmail.grigorij.ui.utils.css.Display;
import com.gmail.grigorij.ui.utils.css.FlexDirection;
import com.gmail.grigorij.ui.utils.css.LumoStyles;
import com.gmail.grigorij.ui.utils.css.size.Horizontal;
import com.gmail.grigorij.ui.utils.css.size.Vertical;
import com.gmail.grigorij.utils.ProjectConstants;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ReadOnlyHasValue;

import java.text.SimpleDateFormat;

public class ReadOnlyTransactionForm extends FlexBoxLayout {

	private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

	private Binder<Transaction> binder = new Binder<>(Transaction.class);

	private FormLayout staticForm = new FormLayout();
	private FormLayout dynamicForm;


	private Transaction transaction;


	public ReadOnlyTransactionForm() {
		setDisplay(Display.FLEX);
		setFlexDirection(FlexDirection.COLUMN);

		TextField fullNameField = new TextField("Description");
		fullNameField.setReadOnly(true);
		ReadOnlyHasValue<Transaction> fullName = new ReadOnlyHasValue<>(transaction -> fullNameField.setValue(transaction.getFullName()));

		TextField fullDateField = new TextField("Date & Time");
		fullDateField.setReadOnly(true);
		ReadOnlyHasValue<Transaction> fullDate = new ReadOnlyHasValue<>(transaction -> {
			String tr_date;
			try {
				tr_date = dateFormat.format(transaction.getDate());
			} catch (Exception e) {
				System.out.println("Error formatting Date: " + transaction.getDate());
				tr_date = "";
			}
			fullDateField.setValue(tr_date);
		});

		TextField whoDidField = new TextField("Who Did");
		whoDidField.setReadOnly(true);
		ReadOnlyHasValue<Transaction> whoDid = new ReadOnlyHasValue<>(transaction -> {
			whoDidField.setValue( (transaction.getWhoDid() == null) ? "" : transaction.getWhoDid().getUsername() );
		});

		TextField additionalInfoField = new TextField("Additional Info");
		additionalInfoField.setReadOnly(true);
		ReadOnlyHasValue<Transaction> additionalInfo = new ReadOnlyHasValue<>(transaction -> {
			additionalInfoField.setValue( transaction.getAdditionalInfo() );
		});

		UIUtils.setColSpan(2, fullNameField, additionalInfoField);

		staticForm.addClassNames(LumoStyles.Padding.Bottom.S, LumoStyles.Padding.Top.S);
		staticForm.setResponsiveSteps(
				new FormLayout.ResponsiveStep("0", 1, FormLayout.ResponsiveStep.LabelsPosition.TOP),
				new FormLayout.ResponsiveStep(ProjectConstants.COL_2_MIN_WIDTH, 2, FormLayout.ResponsiveStep.LabelsPosition.TOP));

		staticForm.add(fullNameField);
		staticForm.add(whoDidField);
		staticForm.add(fullDateField);
		staticForm.add(additionalInfoField);

		binder.forField(fullName)
				.bind(transaction -> transaction, null);
		binder.forField(fullDate)
				.bind(transaction -> transaction, null);
		binder.forField(whoDid)
				.bind(transaction -> transaction, null);
		binder.forField(additionalInfo)
				.bind(transaction -> transaction, null);

		add(staticForm);
		add(new Divider(2, Horizontal.NONE, Vertical.S));
	}

	public void setTransaction(Transaction t) {
		this.transaction = t;

		constructDynamicForm();

		UIUtils.updateFormSize(staticForm);

		if (dynamicForm != null) {
			UIUtils.updateFormSize(dynamicForm);
		}


		try {
			binder.removeBean();
			binder.readBean(transaction);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	/*
	Construct FormLayout based on TransactionTarget
	 */
	private void constructDynamicForm() {
		if (dynamicForm != null) {
			remove(dynamicForm);
			dynamicForm = null;
		}

		if (transaction.getTransactionOperation().equals(TransactionType.LOGIN)) {
			return;
		}
		if (transaction.getTransactionOperation().equals(TransactionType.LOGOUT)) {
			return;
		}

		dynamicForm = new FormLayout();
		dynamicForm.addClassNames(LumoStyles.Padding.Bottom.S);
		dynamicForm.setResponsiveSteps(
				new FormLayout.ResponsiveStep("0", 1, FormLayout.ResponsiveStep.LabelsPosition.TOP),
				new FormLayout.ResponsiveStep(ProjectConstants.COL_2_MIN_WIDTH, 2, FormLayout.ResponsiveStep.LabelsPosition.TOP));

		Button showDetailsButton = UIUtils.createButton("Show Details", ButtonVariant.LUMO_CONTRAST);

		switch (transaction.getTransactionTarget()) {

			case USER:
			case USER_STATUS:
			case USER_PASSWORD:
			case USER_ACCESS_RIGHTS:
				TextField targetUserField = new TextField("User");
				targetUserField.setReadOnly(true);
				ReadOnlyHasValue<Transaction> t_user = new ReadOnlyHasValue<>(transaction -> {
					targetUserField.setValue( (transaction.getDestinationUser() == null) ? "" : transaction.getDestinationUser().getUsername() );
				});

				showDetailsButton.addClickListener(e -> {
					ReadOnlyUserForm form = new ReadOnlyUserForm();
					form.setUser(transaction.getDestinationUser());

					constructTargetDetailsDialog(form);
				});

				finalizeForm(t_user, targetUserField, showDetailsButton);
				break;

			case COMPANY:
			case COMPANY_STATUS:
			case COMPANY_LOCATION:
				TextField companyField = new TextField("Company");
				companyField.setReadOnly(true);
				ReadOnlyHasValue<Transaction> t_company = new ReadOnlyHasValue<>(transaction -> {
					companyField.setValue( (transaction.getCompany() == null) ? "" : transaction.getCompany().getName() );
				});

				showDetailsButton.addClickListener(e -> {
					ReadOnlyCompanyForm form = new ReadOnlyCompanyForm();
					form.setCompany(transaction.getCompany());

					constructTargetDetailsDialog(form);
				});

				finalizeForm(t_company, companyField, showDetailsButton);
				break;

			case CATEGORY:
				TextField categoryField = new TextField("Category");
				categoryField.setReadOnly(true);
				ReadOnlyHasValue<Transaction> t_category = new ReadOnlyHasValue<>(transaction -> {
					categoryField.setValue((transaction.getInventoryEntity() == null) ? "" : transaction.getInventoryEntity().getName());
				});

				showDetailsButton.addClickListener(e -> {
					ReadOnlyCategoryForm form = new ReadOnlyCategoryForm();
					form.setCategory(transaction.getInventoryEntity());

					constructTargetDetailsDialog(form);
				});

				finalizeForm(t_category, categoryField, showDetailsButton);
				break;

			case TOOL:
			case TOOL_STATUS:
				TextField toolField = new TextField("Tool");
				toolField.setReadOnly(true);
				ReadOnlyHasValue<Transaction> t_tool = new ReadOnlyHasValue<>(transaction -> {
					toolField.setValue( (transaction.getInventoryEntity() == null) ? "" : transaction.getInventoryEntity().getName() );
				});

				showDetailsButton.addClickListener(e -> {
					ReadOnlyToolForm form = new ReadOnlyToolForm();
					form.setTool(transaction.getInventoryEntity());

					constructTargetDetailsDialog(form);
				});

				finalizeForm(t_tool, toolField, showDetailsButton);
				break;

			default:
				System.out.println("Unknown / unhandled TransactionTarget in switch case: \n'" + transaction.getTransactionTarget().getStringValue() + "'");
				break;
		}

		add(dynamicForm);
	}

	private void finalizeForm(ReadOnlyHasValue<Transaction> readOnlyField, TextField field, Button button) {
		FlexBoxLayout targetLayout = UIUtils.getFormRowLayout(field, button, false);
		UIUtils.setColSpan(2, targetLayout);

		dynamicForm.add(UIUtils.createH4Label("Target"));
		dynamicForm.add(targetLayout);

		binder.forField(readOnlyField)
				.bind(transaction -> transaction, null);
	}

	private void constructTargetDetailsDialog(Component content) {
		CustomDialog dialog = new CustomDialog();

		dialog.setHeader(UIUtils.createH3Label("Target Details"));
		dialog.setContent(content);
		dialog.getCancelButton().addClickListener(closeEvent -> dialog.close());
		dialog.setConfirmButton(null);

		dialog.open();
	}
}
