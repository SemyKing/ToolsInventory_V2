package com.gmail.grigorij.ui.components.dialogs;

import com.gmail.grigorij.ui.utils.UIUtils;
import com.gmail.grigorij.utils.ProjectConstants;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;


public class ConfirmDialog extends Div {

	private final static String CLASS_NAME = "confirm-dialog";

	private Dialog dialog;

	private Div content;
	private Button cancelButton;
	private Button confirmButton;


	public ConfirmDialog() {
		addClassName(CLASS_NAME);

		Div header = new Div();
		header.addClassName(CLASS_NAME  + "__header");
		header.add(UIUtils.createH3Label("Confirm Action"));

		content = new Div();
		content.addClassName(CLASS_NAME  + "__content");

		Div footer = new Div();
		footer.addClassName(CLASS_NAME  + "__footer");

		cancelButton = UIUtils.createSmallButton("No", ButtonVariant.LUMO_TERTIARY);
		confirmButton = UIUtils.createSmallButton("Yes", ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_SUCCESS);

		footer.add(cancelButton, confirmButton);

		add(header, content, footer);

		dialog = new Dialog(this);
		dialog.setCloseOnEsc(false);
		dialog.setCloseOnOutsideClick(false);
	}

	public void setMessage(String message) {
		String[] lines = message.split(ProjectConstants.NEW_LINE);

		for (String line : lines) {
			Span row = new Span(line);
			row.addClassName(CLASS_NAME + "__row");

			content.add(row);
		}
	}

	public Button getConfirmButton() {
		return confirmButton;
	}

	public void open() {
		dialog.open();
	}

	public void close() {
		dialog.close();
	}

	public void closeOnCancel() {
		cancelButton.addClickListener(e -> dialog.close());
	}
}
