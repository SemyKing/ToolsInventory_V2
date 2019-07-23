package com.gmail.grigorij.ui.utils.forms.readonly;

import com.gmail.grigorij.backend.entities.transaction.Transaction;
import com.gmail.grigorij.backend.entities.transaction.TransactionOperation;
import com.gmail.grigorij.backend.entities.transaction.TransactionTarget;
import com.gmail.grigorij.ui.utils.UIUtils;
import com.gmail.grigorij.ui.utils.components.CustomDialog;
import com.gmail.grigorij.ui.utils.components.FlexBoxLayout;
import com.gmail.grigorij.ui.utils.css.Display;
import com.gmail.grigorij.ui.utils.css.FlexDirection;
import com.gmail.grigorij.ui.utils.css.LumoStyles;
import com.gmail.grigorij.ui.utils.forms.UserForm;
import com.gmail.grigorij.ui.utils.forms.admin.AdminCompanyForm;
import com.gmail.grigorij.ui.utils.forms.admin.ToolCategoryForm;
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

		TextField fullNameField = getTextField("Description", "");
		ReadOnlyHasValue<Transaction> fullName = new ReadOnlyHasValue<>(transaction ->
				fullNameField.setValue(transaction.getFullName()));

		TextField fullDateField = getTextField("Date & Time", "");
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

		TextField whoDidField = getTextField("Who Did", "");
		ReadOnlyHasValue<Transaction> whoDid = new ReadOnlyHasValue<>(transaction -> {
			whoDidField.setValue( (transaction.getWhoDid() == null) ? "" : transaction.getWhoDid().getUsername() );
		});

		TextField additionalInfoField = getTextField("Additional Info", "");
		ReadOnlyHasValue<Transaction> additionalInfo = new ReadOnlyHasValue<>(transaction -> {
			additionalInfoField.setValue( transaction.getAdditionalInfo() );
		});

		UIUtils.setColSpan(2, fullNameField, additionalInfoField);

		staticForm.addClassNames(LumoStyles.Padding.Bottom.L, LumoStyles.Padding.Horizontal.M, LumoStyles.Padding.Top.S);
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
	}

	private TextField getTextField(String text, String width) {
		TextField textField = new TextField(text);
		if (width.length() > 0) {
			textField.setWidth(width);
		}
		textField.setReadOnly(true);
		textField.getStyle().set("padding-top", "var(--lumo-space-s)");

		return textField;
	}


	public void setTransaction(Transaction t) {
		this.transaction = t;

		constructDynamicForm();

		UIUtils.updateFormSize(staticForm);
		UIUtils.updateFormSize(dynamicForm);

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
		}

		if (transaction.getTransactionOperation().equals(TransactionOperation.LOGIN)) {
			return;
		}
		if (transaction.getTransactionOperation().equals(TransactionOperation.LOGOUT)) {
			return;
		}

		dynamicForm = new FormLayout();
		dynamicForm.addClassNames(LumoStyles.Padding.Horizontal.M);
		dynamicForm.setResponsiveSteps(
				new FormLayout.ResponsiveStep("0", 1, FormLayout.ResponsiveStep.LabelsPosition.TOP),
				new FormLayout.ResponsiveStep(ProjectConstants.COL_2_MIN_WIDTH, 2, FormLayout.ResponsiveStep.LabelsPosition.TOP));

		Button showDetailsButton = UIUtils.createButton("Show Details", ButtonVariant.LUMO_CONTRAST);


		if (transaction.getTransactionTarget().equals(TransactionTarget.USER)) {

			TextField targetUser = getTextField("User", "");
			ReadOnlyHasValue<Transaction> t_user = new ReadOnlyHasValue<>(transaction -> {
				targetUser.setValue( (transaction.getDestinationUser() == null) ? "" : transaction.getDestinationUser().getUsername() );
			});

			showDetailsButton.addClickListener(e -> {
				UserForm form = new UserForm();
				form.setUser(transaction.getDestinationUser());

				constructTargetDetailsDialog(form);
			});

			finalizeForm(t_user, targetUser, showDetailsButton);


		} else if (transaction.getTransactionTarget().equals(TransactionTarget.COMPANY)) {

			TextField company = getTextField("Company", "");
			ReadOnlyHasValue<Transaction> t_company = new ReadOnlyHasValue<>(transaction -> {
				company.setValue( (transaction.getCompany() == null) ? "" : transaction.getCompany().getName() );
			});

			showDetailsButton.addClickListener(e -> {
				AdminCompanyForm form = new AdminCompanyForm();
				form.setCompany(transaction.getCompany());

				constructTargetDetailsDialog(form);
			});

			finalizeForm(t_company, company, showDetailsButton);


		} else if (transaction.getTransactionTarget().equals(TransactionTarget.CATEGORY)) {

			TextField category = getTextField("Category", "");
			ReadOnlyHasValue<Transaction> t_category = new ReadOnlyHasValue<>(transaction -> {
				category.setValue( (transaction.getInventoryEntity() == null) ? "" : transaction.getInventoryEntity().getName() );
			});

			showDetailsButton.addClickListener(e -> {
				ToolCategoryForm form = new ToolCategoryForm();
				form.setCategory(transaction.getInventoryEntity());

				constructTargetDetailsDialog(form);
			});

			finalizeForm(t_category, category, showDetailsButton);


		} else if (transaction.getTransactionTarget().equals(TransactionTarget.TOOL)) {

			TextField tool = getTextField("Tool", "");
			ReadOnlyHasValue<Transaction> t_tool = new ReadOnlyHasValue<>(transaction -> {
				tool.setValue( (transaction.getInventoryEntity() == null) ? "" : transaction.getInventoryEntity().getName() );
			});

			showDetailsButton.addClickListener(e -> {
				ReadOnlyToolForm form = new ReadOnlyToolForm();
				form.setTool(transaction.getInventoryEntity());

				constructTargetDetailsDialog(form);
			});

			finalizeForm(t_tool, tool, showDetailsButton);


		}

		add(dynamicForm);
	}

	private void finalizeForm(ReadOnlyHasValue<Transaction> readOnlyField, TextField field, Button button) {
		FlexBoxLayout targetLayout = UIUtils.getFormRowLayout(field, button);
		UIUtils.setColSpan(2, targetLayout);
		dynamicForm.add(targetLayout);

		binder.forField(readOnlyField)
				.bind(transaction -> transaction, null);
	}

	private void constructTargetDetailsDialog(Component content) {
		CustomDialog dialog = new CustomDialog();

		dialog.setHeader(UIUtils.createH4Label("Target Details"));
		dialog.setContent(content);
		dialog.getCancelButton().addClickListener(closeEvent -> dialog.close());
		dialog.setConfirmButton(null);

		dialog.open();
	}
}
