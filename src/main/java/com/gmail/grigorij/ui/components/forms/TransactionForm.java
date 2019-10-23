package com.gmail.grigorij.ui.components.forms;

import com.gmail.grigorij.backend.entities.transaction.Transaction;
import com.gmail.grigorij.utils.DateConverter;
import com.gmail.grigorij.utils.ProjectConstants;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ReadOnlyHasValue;
import com.vaadin.flow.data.binder.ValidationException;

import java.util.List;


public class TransactionForm extends FormLayout {

	private final String CLASS_NAME = "form";

	private Binder<Transaction> binder = new Binder<>(Transaction.class);
	private Transaction transaction;

	// FORM ITEMS
	private TextField descriptionField;
	private TextField dateField;
	private TextField whoDidField;
	private TextArea changesField;
	private TextArea additionalInfo;


	// BINDER ITEMS
	private ReadOnlyHasValue<Transaction> description;
	private ReadOnlyHasValue<Transaction> date;
	private ReadOnlyHasValue<Transaction> whoDid;
	private ReadOnlyHasValue<Transaction> changes;

	public TransactionForm() {
		addClassName(CLASS_NAME);

		constructFormItems();

		constructForm();

		constructBinder();
	}


	private void constructFormItems() {
		dateField = new TextField("Date & Time");
		dateField.setReadOnly(true);
		date = new ReadOnlyHasValue<>(transaction -> {
			String transactionDate;
			try {
				transactionDate = DateConverter.dateToStringWithTime(transaction.getDate());
			} catch (Exception e) {
				System.out.println("Error formatting Date: " + transaction.getDate());
				transactionDate = "";
			}
			dateField.setValue(transactionDate);
		});


		whoDidField = new TextField("Who Did");
		whoDidField.setReadOnly(true);
		whoDid = new ReadOnlyHasValue<>(transaction -> {
			whoDidField.setValue((transaction.getUser() == null) ? "" : transaction.getUser().getFullName());
		});


		descriptionField = new TextField("Description");
		descriptionField.setReadOnly(true);
		description = new ReadOnlyHasValue<>(transaction -> {
			descriptionField.setValue(transaction.getDescription(true));
		});

		setColspan(descriptionField, 2);


		changesField = new TextArea("Changes");
		changesField.setMinHeight("200px");
		changesField.setReadOnly(true);
		changes = new ReadOnlyHasValue<>(transaction -> {
			changesField.setValue(getTransactionChanges(transaction.getChanges()));
		});

		setColspan(changesField, 2);


		additionalInfo = new TextArea("Additional Info");
		additionalInfo.setMaxHeight("200px");

		setColspan(additionalInfo, 2);
	}

	private void constructForm() {
		setResponsiveSteps(
				new FormLayout.ResponsiveStep("0", 1, FormLayout.ResponsiveStep.LabelsPosition.TOP),
				new FormLayout.ResponsiveStep(ProjectConstants.COL_2_MIN_WIDTH, 2, FormLayout.ResponsiveStep.LabelsPosition.TOP));
		add(dateField);
		add(whoDidField);
		add(descriptionField);
		add(changesField);
		add(additionalInfo);
	}

	private void constructBinder() {
		binder.forField(description)
				.bind(transaction -> transaction, null);
		binder.forField(date)
				.bind(transaction -> transaction, null);
		binder.forField(whoDid)
				.bind(transaction -> transaction, null);
		binder.forField(changes)
				.bind(transaction -> transaction, null);
		binder.forField(additionalInfo)
				.bind(Transaction::getAdditionalInfo, Transaction::setAdditionalInfo);
	}


	private void initDynamicFormItems() {

	}

	private String getTransactionChanges(List<String> changesList) {
		StringBuilder changes = new StringBuilder();

		if (changesList.size() <= 0) {
			return changes.toString();
		}

		for (String line : changesList) {
			changes.append(line);
			changes.append(System.getProperty("line.separator"));
		}

		return changes.toString();
	}


	public void setTransaction(Transaction transaction) {
		initDynamicFormItems();

		this.transaction = transaction;

		binder.readBean(this.transaction);
	}

	public Transaction getTransaction() {
		try {
			binder.validate();
			if (binder.isValid()) {
				binder.writeBean(transaction);
				return transaction;
			}
		} catch (ValidationException e) {
			e.printStackTrace();
			return null;
		}
		return null;
	}
}
