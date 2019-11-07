package com.gmail.grigorij.ui.components.dialogs;

import com.gmail.grigorij.ui.components.layouts.FlexBoxLayout;
import com.gmail.grigorij.ui.utils.UIUtils;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;

public class PDF_Dialog extends Div {

	private final static String CLASS_NAME = "pdf-dialog";

	private Button closeButton;
	private Dialog dialog;

	private FlexBoxLayout content, footer;


	public PDF_Dialog() {
		addClassName(CLASS_NAME);

		content = new FlexBoxLayout();
		content.addClassName(CLASS_NAME  + "__content");

		footer = new FlexBoxLayout();
		footer.addClassName(CLASS_NAME  + "__footer");

		closeButton = UIUtils.createSmallButton("Close", ButtonVariant.LUMO_TERTIARY);
		closeButton.addClickListener(e -> close());

		footer.add(closeButton);

		add(content, footer);


		dialog = new Dialog(this);
		dialog.setCloseOnEsc(false);
		dialog.setCloseOnOutsideClick(false);
	}

	public void setContent(Component... components) {
		content.removeAll();
		content.add(components);
	}


	public void open() {
		dialog.open();
	}

	public void close() {
		dialog.close();
	}

	public void closeOnCancel() {
		this.closeButton.addClickListener(e -> close());
	}
}
