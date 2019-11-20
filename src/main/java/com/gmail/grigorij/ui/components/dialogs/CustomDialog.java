package com.gmail.grigorij.ui.components.dialogs;

import com.gmail.grigorij.ui.components.FlexBoxLayout;
import com.gmail.grigorij.ui.utils.UIUtils;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;


public class CustomDialog extends Div {

	private final static String CLASS_NAME = "custom-dialog";

	private Button cancelButton, confirmButton;
	private Dialog dialog;

	private FlexBoxLayout header, content, footer;


	public CustomDialog() {
		addClassName(CLASS_NAME);

		header = new FlexBoxLayout();
		header.addClassName(CLASS_NAME  + "__header");

		content = new FlexBoxLayout();
		content.addClassName(CLASS_NAME  + "__content");

		footer = new FlexBoxLayout();
		footer.addClassName(CLASS_NAME  + "__footer");

		cancelButton = UIUtils.createSmallButton("Cancel", ButtonVariant.LUMO_TERTIARY);
		confirmButton = UIUtils.createSmallButton("Confirm", ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_SUCCESS);

		footer.add(cancelButton, confirmButton);

		add(header, content, footer);

		dialog = new Dialog(this);
	}

	public FlexBoxLayout getHeader() {
		return this.header;
	}
	public void setHeader(Component component) {
		header.removeAll();
		header.add(component);
	}

	public FlexBoxLayout getContent() {
		return content;
	}
	public void setContent(Component... components) {
		content.removeAll();
		content.add(components);
	}

	public FlexBoxLayout getFooter() {
		return footer;
	}


	public Button getCancelButton() {
		return cancelButton;
	}
	public void setCancelButton(Button cancelButton) {
		footer.remove(this.cancelButton);
		this.cancelButton = cancelButton;

		if (cancelButton != null) {
			footer.add(cancelButton);
		}
	}


	public Button getConfirmButton() {
		return confirmButton;
	}
	public void setConfirmButton(Button confirmButton) {
		footer.remove(this.confirmButton);
		this.confirmButton = confirmButton;

		if (confirmButton != null) {
			footer.add(confirmButton);
		}
	}

	public void setCloseOnEsc(boolean b) {
		this.dialog.setCloseOnEsc(b);
	}
	public void setCloseOnOutsideClick(boolean b) {
		this.dialog.setCloseOnOutsideClick(b);
	}

	public void open() {
		dialog.open();
	}

	public void close() {
		dialog.close();
	}

	public void closeOnCancel() {
		this.cancelButton.addClickListener(e -> close());
	}
}
