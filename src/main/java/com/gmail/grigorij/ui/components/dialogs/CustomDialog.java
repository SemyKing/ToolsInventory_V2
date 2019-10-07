package com.gmail.grigorij.ui.components.dialogs;

import com.gmail.grigorij.ui.components.layouts.FlexBoxLayout;
import com.gmail.grigorij.ui.utils.UIUtils;
import com.gmail.grigorij.ui.utils.css.Display;
import com.gmail.grigorij.ui.utils.css.size.Horizontal;
import com.gmail.grigorij.ui.utils.css.size.Left;
import com.gmail.grigorij.ui.utils.css.size.Right;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.VaadinIcon;

@CssImport("./styles/components/dialogs/custom-dialog.css")
@CssImport(value = "./styles/components/dialogs/vaadin-dialog-overlay.css", themeFor = "vaadin-dialog-overlay")
public class CustomDialog extends Div {

	private final static String CLASS_NAME = "custom-dialog";

	private boolean deleteButtonAdded = false;
	private Button deleteButton, cancelButton, confirmButton;
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

		deleteButton = UIUtils.createButton("Delete", VaadinIcon.TRASH, ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ERROR);
		cancelButton = UIUtils.createSmallButton("Cancel", ButtonVariant.LUMO_TERTIARY);
		confirmButton = UIUtils.createSmallButton("Confirm", ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_SUCCESS);

		footer.add(cancelButton, confirmButton);
		footer.setPadding(Horizontal.M);

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

	public Button getDeleteButton() {
		return deleteButton;
	}
	public void setDeleteButtonVisible(boolean b) {
		if (!deleteButtonAdded) {
			footer.addComponentAsFirst(deleteButton);
			deleteButtonAdded = true;
		}

		if (b) {
			footer.setComponentDisplay(deleteButton, Display.INITIAL);
			footer.setComponentMargin(cancelButton, Left.AUTO);
			footer.setComponentMargin(cancelButton, Right.M);
		} else {
			footer.setComponentDisplay(deleteButton, Display.NONE);
			footer.setComponentMargin(cancelButton, Left.NONE);
			footer.setComponentMargin(cancelButton, Right.AUTO);
		}
		deleteButton.setEnabled(b);
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
