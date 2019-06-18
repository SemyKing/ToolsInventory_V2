package com.gmail.grigorij.ui.utils.components;


import com.gmail.grigorij.ui.utils.UIUtils;
import com.gmail.grigorij.ui.utils.css.FlexDirection;
import com.gmail.grigorij.ui.utils.css.LumoStyles;
import com.gmail.grigorij.ui.views.authentication.AuthenticationService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.theme.lumo.Lumo;


public class CustomDialog extends Div {

	private final static String CLASS_NAME = "custom-dialog";

	private Button cancelButton;
	private Button confirmButton;
	private Dialog dialog;


	private Div header;
	private FlexBoxLayout content;
	private FlexBoxLayout footer;


	public CustomDialog() {
		addClassName(CLASS_NAME);
//		setSizeFull();

		header = new Div();
		header.addClassName(CLASS_NAME  + "__header");

		content = new FlexBoxLayout();
		content.addClassName(CLASS_NAME  + "__content");

		footer = new FlexBoxLayout();
		footer.addClassName(CLASS_NAME  + "__footer");

		cancelButton = UIUtils.createButton("Cancel", ButtonVariant.LUMO_TERTIARY);
		cancelButton.addClickListener(e -> dialog.close());

		confirmButton = UIUtils.createButton("Confirm", ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_SUCCESS);

		footer.add(cancelButton, confirmButton);

		add(header, content, footer);

		dialog = new Dialog(this);
	}

	public void setHeader(Component component) {
		header.removeAll();
		header.add(component);
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

	public Button getCancelButton() {
		return cancelButton;
	}
	public void setCancelButton(Button cancelButton) {
		footer.remove(this.cancelButton);
		this.cancelButton = cancelButton;
		footer.add(cancelButton);
	}

	public Button getConfirmButton() {
		return confirmButton;
	}
	public void setConfirmButton(Button confirmButton) {
		footer.remove(this.confirmButton);
		this.confirmButton = confirmButton;
		footer.add(confirmButton);
	}
}
