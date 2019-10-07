package com.gmail.grigorij.ui.components.dialogs;

import com.gmail.grigorij.ui.components.layouts.FlexBoxLayout;
import com.gmail.grigorij.ui.utils.UIUtils;
import com.gmail.grigorij.ui.utils.css.Display;
import com.gmail.grigorij.ui.utils.css.FlexDirection;
import com.gmail.grigorij.ui.utils.css.FlexWrap;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;


public class ConfirmDialog extends CustomDialog {

	public enum Type {
		DELETE
	}


	public ConfirmDialog(String message) {
		getHeader().add(UIUtils.createH3Label("Confirm Action"));
		getContent().add(new Span(message));
		getCancelButton().setText("No");
		getConfirmButton().setText("Yes");
	}

//	public ConfirmDialog(Type type, String entity, String comparisonTarget) {
//		getCancelButton().setText("Cancel");
//		getConfirmButton().setText("Confirm");
//
//		Span targetBold = UIUtils.createBoldText(comparisonTarget);
//
//		/*
//		DELETE DIALOG WITH CONFIRM INPUT FIELD
//		 */
//		if (type.equals(Type.DELETE)) {
//			getHeader().add(UIUtils.createH3Label("Confirm Delete"));
//
//			FlexBoxLayout layout = new FlexBoxLayout();
//			layout.setFlexWrap(FlexWrap.WRAP);
//
//			FlexBoxLayout contentLayout = new FlexBoxLayout();
//			contentLayout.setDisplay(Display.FLEX);
//			contentLayout.setFlexDirection(FlexDirection.COLUMN);
//
//			contentLayout.add(new Span("Are you sure you want to delete" + entity + "?"));
//			contentLayout.add(new Span("This operation will completely remove" + entity + "from Database."));
//			contentLayout.add(new Span(""));
//			contentLayout.add(new HorizontalLayout(new Span("Confirmation text :"), targetBold));
//
//
//			getContent().add(contentLayout);
//
//			getConfirmButton().setEnabled(false);
//
//			TextField confirmInputField = new TextField("Input confirmation text to proceed");
//			confirmInputField.setRequired(true);
//			confirmInputField.setValueChangeMode(ValueChangeMode.LAZY);
//			confirmInputField.addValueChangeListener(e -> {
//				getConfirmButton().setEnabled(false);
//
//				if (e.getValue() != null) {
//					if (e.getValue().length() > 0) {
//						if (e.getValue().equals(comparisonTarget)) {
//							getConfirmButton().setEnabled(true);
//						}
//					}
//				}
//			});
//
//			getContent().add(confirmInputField);
//		}
//	}


	public void closeOnCancel() {
		getCancelButton().addClickListener(e -> close());
	}
}
