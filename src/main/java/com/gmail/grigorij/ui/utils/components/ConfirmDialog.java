package com.gmail.grigorij.ui.utils.components;

import com.gmail.grigorij.ui.utils.UIUtils;
import com.gmail.grigorij.ui.utils.css.FlexWrap;
import com.gmail.grigorij.ui.utils.css.size.Horizontal;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;


public class ConfirmDialog extends FlexBoxLayout {

	private final static String CLASS_NAME = "custom-dialog";

	public enum Type {
		DELETE
	}

	private Button cancelButton;
	private Button confirmButton;
	private Dialog dialog;


	private FlexBoxLayout header;
	private FlexBoxLayout content;
	private FlexBoxLayout footer;

	public ConfirmDialog(String message) {
		addClassName(CLASS_NAME);

		header = new FlexBoxLayout();
		header.addClassName(CLASS_NAME  + "__header");
		header.add(UIUtils.createH4Label("Confirm action"));

		content = new FlexBoxLayout();
		content.addClassName(CLASS_NAME  + "__content");
		content.add(new Span(message));

		cancelButton = UIUtils.createButton("No", ButtonVariant.LUMO_TERTIARY);
		confirmButton = UIUtils.createButton("Yes", ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_SUCCESS);

		footer = new FlexBoxLayout();
		footer.addClassName(CLASS_NAME  + "__footer");
		footer.setPadding(Horizontal.M);

		footer.add(cancelButton, confirmButton);

		add(header, content, footer);
		dialog = new Dialog(this);
	}

	public ConfirmDialog(Type type, String entity, String comparisonTarget) {
		addClassName(CLASS_NAME);

		header = new FlexBoxLayout();
		header.addClassName(CLASS_NAME  + "__header");

		content = new FlexBoxLayout();
		content.addClassName(CLASS_NAME  + "__content");

		cancelButton = UIUtils.createButton("Cancel", ButtonVariant.LUMO_TERTIARY);
		confirmButton = UIUtils.createButton("Confirm", ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_SUCCESS);

		footer = new FlexBoxLayout();
		footer.addClassName(CLASS_NAME  + "__footer");
		footer.setPadding(Horizontal.M);
		footer.add(cancelButton, confirmButton);

		Span targetBold = UIUtils.createBoldText(comparisonTarget);

		/*
		DELETE DIALOG WITH CONFIRM INPUT FIELD
		 */
		if (type.equals(Type.DELETE)) {
			header.add(UIUtils.createH4Label("Confirm Delete"));

			FlexBoxLayout layout = new FlexBoxLayout();
			layout.setFlexWrap(FlexWrap.WRAP);

			Paragraph p = new Paragraph();
			p.add(new Span("Are you sure you want to delete " + entity + "? This operation will completely remove " + entity + " from Database."));
//			layout.add(new Label("Entities that have reference to this entity, will throw: "), UIUtils.createBoldText("NullPointerException"));
//			p.add(layout);
			p.add(new HorizontalLayout(new Span("Confirmation text :"), targetBold));

			content.add(p);

			this.confirmButton.setEnabled(false);

			TextField confirmInputField = new TextField("Input confirmation text to proceed");
			confirmInputField.setRequired(true);
			confirmInputField.setValueChangeMode(ValueChangeMode.EAGER);
			confirmInputField.addValueChangeListener(e -> {
				this.confirmButton.setEnabled(false);

				if (e.getValue() != null) {
					if (e.getValue().length() > 0) {
						if (e.getValue().equals(comparisonTarget)) {
							this.confirmButton.setEnabled(true);
						}
					}
				}
			});

			content.add(confirmInputField);
		}


		add(header, content, footer);
		dialog = new Dialog(this);
	}



	public void closeOnCancel() {
		this.cancelButton.addClickListener(e -> close());
	}

	public Button getCancelButton() {
		return cancelButton;
	}

	public Button getConfirmButton() {
		return confirmButton;
	}

	public void open() {
		this.dialog.open();
	}
	public void close() {
		this.dialog.close();
	}
}
